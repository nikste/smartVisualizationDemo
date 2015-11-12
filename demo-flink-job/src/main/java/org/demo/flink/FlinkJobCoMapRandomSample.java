package org.demo.flink;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.RMQTopology;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;
import org.demo.connections.RabbitMQQueueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Created by nikste on 29.10.15.
 */
public class FlinkJobCoMapRandomSample {

    static Logger log  = LoggerFactory.getLogger(FlinkJobCoMapRandomSample.class);

    static class ControlObject{

        public ControlObject(double neLat, double neLng, double swLat, double swLng) {
            this.neLat = neLat;
            this.neLng = neLng;
            this.swLat = swLat;
            this.swLng = swLng;
            this.identifier = 1;
        }

        public ControlObject(double changeInDiscardProbability) {
            this.changeInDiscardProbability = changeInDiscardProbability;
            this.identifier = 0;
        }

        double neLat = 0.0;
        double neLng = 0.0;
        double swLat = 0.0;
        double swLng = 0.0;

        int identifier = 1;
        double changeInDiscardProbability;
    }


    public static Random random = new Random();

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // init data source
        DataStream<String> data = env.addSource(new RMQSource<String>("localhost", RabbitMQQueueManager.DATA_QUEUE_NAME, new SimpleStringSchema()));
        // init control source
        final DataStream<String> control = env.addSource(new RMQSource<String>("localhost", RabbitMQQueueManager.FLINK_DATACTRL_QUEUE_NAME, new SimpleStringSchema()));
        DataStream<ControlObject> controlObjects = control.map(new MapFunction<String, ControlObject>() {
            @Override
            public ControlObject map(String s) throws Exception {
                String[] sArr = s.split(",");

                ControlObject co = new ControlObject(1.0);
                // change in probability of pass
                if(sArr[0].equals("0")){
                    co = new ControlObject(Double.parseDouble(sArr[1]));
                }
                if(sArr[0].equals("1")){
                    co = new ControlObject(Double.parseDouble(sArr[1]),
                            Double.parseDouble(sArr[2]),
                            Double.parseDouble(sArr[3]),
                            Double.parseDouble(sArr[4]));
                }
                return co;
            }
        });

        ConnectedStreams<String, ControlObject> fullStream = data.connect(controlObjects);

        DataStream<String> dataStream = fullStream.flatMap(new CoFlatMapFunction<String, ControlObject, String>() {
            // amount of data that is tolerable
            double passProbability = 1.0;

            double neLat = 0.0;
            double neLng = 0.0;
            double swLat = 0.0;
            double swLng = 0.0;

            @Override
            public void flatMap1(String s, Collector<String> collector) throws Exception {

                // filter first with attributes, then filter with Bernoulli.
                if(!(neLat == 0.0 && neLng == 0.0 &&
                        swLat == 0.0 && swLng == 0.0)){
                    Configuration conf2 = Configuration.defaultConfiguration();
                    Configuration conf = conf2.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

                    LinkedHashMap geo = JsonPath.using(conf).parse(s).read("$['geo']");

                    if(geo != null){
                        if(geo.containsKey("coordinates")) {
                            ArrayList<Number> coordinates = (ArrayList<Number>) geo.get("coordinates");//.read("[0]",Double.class);
                            double lat = Double.valueOf(coordinates.get(0).toString());
                            double lng = Double.valueOf(coordinates.get(1).toString());
                            System.out.println("lat:" + lat + " lng:" + lng);
                            System.out.println("swLat" + swLat + " swlng: " + swLng + " " + neLat + " " + neLng);
                            if (!(swLat <= lat && lat <= neLat &&
                                    swLng <= lng && lng <= neLng)) {
                                return;
                            }
                        }

                    }else{
                        return;
                    }

//                    double lat = Double.parseDouble(JsonPath.read(s, "$.coordinates[0]"));
//                    double lng = Double.parseDouble(JsonPath.read(s, "$.coordinates[1]"));

                    // SWLAT < NELAT
                    // SWLNG < NELNG
//                    if(!(swLat <= lat && lat <= neLat &&
//                            swLng <= lng && swLng <= swLng)){
//                        return;
//                    }

                }else {
                    if (passProbability < 1.0) {
                        // sampling happens here
                        // Bernoulli sampling!!
                        double r = random.nextDouble();
                        if (r <= passProbability) {
                            collector.collect(s);
                        } else {
                        }
                    } else {
                        collector.collect(s);
                    }
                }
            }

            @Override
            public void flatMap2(ControlObject controlObject, Collector<String> collector) throws Exception {
                if(controlObject.identifier == 0) {
                    passProbability = controlObject.changeInDiscardProbability;

                }
                if(controlObject.identifier == 1){
                    neLat = controlObject.neLat;
                    neLng = controlObject.neLng;
                    swLat = controlObject.swLat;
                    swLng = controlObject.swLng;
                }
            }
        });

        dataStream.addSink(new RMQSink<String>("localhost", RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME, new RMQTopology.StringToByteSerializer()));

        env.execute();
    }
}

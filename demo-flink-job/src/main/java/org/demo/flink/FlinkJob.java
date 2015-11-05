package org.demo.flink;

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

import java.util.Random;

/**
 * Created by nikste on 29.10.15.
 */
public class FlinkJob {

    static class ControlObject{
        public ControlObject(double samplingRate) {
            this.changeInDiscardProbability = samplingRate;
        }

        double changeInDiscardProbability;
    }


    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // init data source
        DataStream<String> data = env.addSource(new RMQSource<String>("localhost", RabbitMQQueueManager.DATA_QUEUE_NAME, new SimpleStringSchema()));
        // init control source
        DataStream<String> control = env.addSource(new RMQSource<String>("localhost", RabbitMQQueueManager.FLINK_DATACTRL_QUEUE_NAME, new SimpleStringSchema()));
        DataStream<ControlObject> controlObjects = control.map(new MapFunction<String, ControlObject>() {
            @Override
            public ControlObject map(String s) throws Exception {
                return new ControlObject(Double.parseDouble(s));
            }
        });

        ConnectedStreams<String, ControlObject> fullStream = data.connect(controlObjects);

        DataStream<String> dataStream = fullStream.flatMap(new CoFlatMapFunction<String, ControlObject, String>() {
            // amount of data that is tolerable
            double passProbability = 1.0;

            Random random = new Random();
            @Override
            public void flatMap1(String s, Collector<String> collector) throws Exception {

                System.out.println("rolling da dice: P(keep) = " + passProbability);
                if( passProbability < 1.0 ){
                    // sampling happens here
                    // Bernoulli sampling!!
                    double r = random.nextDouble();
                    if( r <= passProbability ){
                        System.out.println("keeping!");
                        collector.collect(s);
                    } else {
                        System.out.println("discarded!");
                    }
                } else {
                    collector.collect(s);
                }
            }

            @Override
            public void flatMap2(ControlObject controlObject, Collector<String> collector) throws Exception {

                //passProbability = passProbability * controlObject.changeInDiscardProbability;              //discardProbability = discardProbability * controlObject.changeInDiscardProbability;
                passProbability = controlObject.changeInDiscardProbability;
                System.out.println("changing passProbability rate to:" + passProbability + " changeInDiscardProbability was:" + controlObject.changeInDiscardProbability);
            }
        });

        dataStream.addSink(new RMQSink<String>("localhost", RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME, new RMQTopology.StringToByteSerializer()));

        env.execute();
    }
}

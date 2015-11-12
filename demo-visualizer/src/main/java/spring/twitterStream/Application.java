package spring.twitterStream;

import com.google.common.base.MoreObjects;
import com.rabbitmq.client.*;
import org.demo.connections.RabbitMQQueueManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import spring.domain.GeoJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@SpringBootApplication
@EnableScheduling
public class Application {
    static Logger log  = LoggerFactory.getLogger(Application.class);

    private static List<JSONObject> dataBuffer = new ArrayList<JSONObject>();
    private static Consumer dataConsumer;

    public static AtomicInteger dataCtr = new AtomicInteger();
    public static double maxDataPerSecond = 10;

    public static double remotePassProbability = 1.0;
    
    private static ArrayList<Integer> lastDataCtrs = new ArrayList<Integer>();

    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);

        Hashmapbuttler fakeSpringController = applicationContext.getBean(Hashmapbuttler.class);

        Connection connection = RabbitMQQueueManager.createConnection();

        Channel flinkDataChannel = RabbitMQQueueManager.createChannel(connection, RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME);
        Channel dataCtrlChannel = RabbitMQQueueManager.createChannel(connection, RabbitMQQueueManager.FLINK_DATACTRL_QUEUE_NAME);

        createDataConsumer(flinkDataChannel, RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME);

        int correctionCounter = 0;

        try {
            flinkDataChannel.basicConsume(RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME, true, dataConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 10000; i++) {


            // remember last values
            lastDataCtrs.add(dataCtr.get());

            // actually last 5
            if(lastDataCtrs.size() > 5){
                lastDataCtrs.remove(0);
            }

            // comput variance
            double avrg = 0;
            for (int j = 0; j < lastDataCtrs.size(); j++) {
                Integer integer = lastDataCtrs.get(j);
                avrg += integer;
            }

            avrg = avrg / lastDataCtrs.size();

            double variance = 0;
            for (int j = 0; j < lastDataCtrs.size(); j++) {
                Integer integer = lastDataCtrs.get(j);
                variance += (integer - avrg) * (integer - avrg);
            }

            ArrayList<Integer> gradients = new ArrayList<Integer>();
            for (int j = 1; j < lastDataCtrs.size(); j++) {
                gradients.add(lastDataCtrs.get(j - 1) - lastDataCtrs.get(j));
            }


            if(dataCtr.get() == 0){dataCtr.set(1);}

            // TODO: send feedback via bounding box


            if(!(fakeSpringController.fakeSpringController.currentBox.getNeLat() == 0.0 &&
                    fakeSpringController.fakeSpringController.currentBox.getNeLng() == 0.0 &&
                    fakeSpringController.fakeSpringController.currentBox.getSwLat() == 0.0 &&
                    fakeSpringController.fakeSpringController.currentBox.getSwLng() == 0.0 ))
            {
                String messageBoundingBox = "1,";

                messageBoundingBox += fakeSpringController.fakeSpringController.currentBox.getNeLat() + ",";
                messageBoundingBox += fakeSpringController.fakeSpringController.currentBox.getNeLng() + ",";
                messageBoundingBox += fakeSpringController.fakeSpringController.currentBox.getSwLat() + ",";
                messageBoundingBox += fakeSpringController.fakeSpringController.currentBox.getSwLng() ;

                // send info bounding box:
                for (int j = 0; j < 100; j++) {
                    try {
                        dataCtrlChannel.basicPublish("", RabbitMQQueueManager.FLINK_DATACTRL_QUEUE_NAME, null, messageBoundingBox.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(correctionCounter >= 5) {

                // amount of data reduction
                double correctionTerm = maxDataPerSecond / (double) avrg;
                remotePassProbability = remotePassProbability * correctionTerm;

                String message = "0" + Double.toString(1.0);

                try {
                    for (int j = 0; j < 100; j++) {
                        dataCtrlChannel.basicPublish("", RabbitMQQueueManager.FLINK_DATACTRL_QUEUE_NAME, null, message.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                correctionCounter = 0;
            }
            correctionCounter ++;

            fakeSpringController.stats.add(dataBuffer.size());

            if(fakeSpringController.stats.size() > 20){
                fakeSpringController.stats.remove(0);
            }

            fakeSpringController.langStats = new ConcurrentHashMap<String, Integer>();

            for (int j = 0; j < dataBuffer.size(); j++) {
                JSONObject elem = dataBuffer.get(j);

                // fill language statistics
                String lang = MoreObjects.firstNonNull((String) elem.get("lang"), "de");
                //TODO: was null once

                Integer integer = fakeSpringController.langStats.get(lang);
                if( integer == null){
                    fakeSpringController.langStats.put(lang,1);
                }else {
                    //TODO: had nullpointer exception ?
                    fakeSpringController.langStats.put(lang, fakeSpringController.langStats.get(lang) + 1);
                }

                // fill hashtagStatistics
                JSONObject entities =(JSONObject) elem.get("entities");
                JSONArray hashtags =(JSONArray) entities.get("hashtags");
                for (int k = 0; k < hashtags.size(); k++) {
                    JSONObject hashtag = (JSONObject) hashtags.get(k);
                    String hashtagtext = (String) hashtag.get("text");
                    Integer intagar = fakeSpringController.hashStats.get(hashtagtext);
                    if(intagar == null){
                        fakeSpringController.hashStats.put(hashtagtext,1);
                    }else{
                        fakeSpringController.hashStats.put(hashtagtext,fakeSpringController.hashStats.get(hashtagtext) + 1);
                    }
                }

                //fill location statistics
                JSONObject coordinates = (JSONObject) elem.get("coordinates");
                if(coordinates != null){
                    JSONArray coordinates1 = (JSONArray) coordinates.get("coordinates");
                    double lat = Double.valueOf(coordinates1.get(0).toString());
                    double lon = Double.valueOf(coordinates1.get(1).toString());
                    List<Double> coordinateList = new ArrayList<Double>();
                    coordinateList.add(lat);
                    coordinateList.add(lon);

                    String text = (String) elem.get("text");

                    fakeSpringController.featureCollection.features.add(new GeoJson(coordinateList, text));
                }
            }

            dataCtr.set(0);

            dataBuffer = new ArrayList<JSONObject>() ;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    
    private static void createDataConsumer(Channel dataGeneratorCtrlChannel, String QUEUE_NAME) {
        try {
            dataGeneratorCtrlChannel.queueDeclare(QUEUE_NAME, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataConsumer = new DefaultConsumer(dataGeneratorCtrlChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");

                JSONParser parser = new JSONParser();

                JSONObject jsonObj = null;

                try {
                    jsonObj = (JSONObject) parser.parse(message);
                } catch (org.json.simple.parser.ParseException e) {
                    e.printStackTrace();
                }

                Application.dataBuffer.add(jsonObj);

                dataCtr.getAndIncrement();
            }
        };
    }
}
package spring.twitterStream;

import com.rabbitmq.client.*;
import org.demo.connections.RabbitMQQueueManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class Application {


    private static List<JSONObject> dataBuffer = new ArrayList<JSONObject>();
    private static Connection connection;
    private static Consumer dataConsumer;

    public static Consumer dataCtrlConsumer2;

    public static int dataCtr = 0;
    public static double maxDataPerSecond = 10;



    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);

        Connection connection = RabbitMQQueueManager.createConnection();

        Channel flinkDataChannel = RabbitMQQueueManager.createChannel(connection, RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME);
        Channel dataCtrlChannel = RabbitMQQueueManager.createChannel(connection, RabbitMQQueueManager.FLINK_DATACTRL_QUEUE_NAME);

        createDataConsumer(flinkDataChannel, RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME);

        for (int i = 0; i < 100000; i++) {

            System.out.println("durchsatz:" + dataCtr);
            if (dataCtr > maxDataPerSecond) {
                String message = Double.toString(((double) maxDataPerSecond / (double) dataCtr));
                System.out.println("Too much man!!, sending correction signal:" + message);
                try {
                    dataCtrlChannel.basicPublish("", RabbitMQQueueManager.FLINK_DATACTRL_QUEUE_NAME, null, message.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(FakeSpringController.stats.size() > 10){
                FakeSpringController.stats = new ArrayList<Integer>();
            }
            FakeSpringController.stats.add(dataBuffer.size());

            FakeSpringController.langStats = new ConcurrentHashMap<String, Integer>();

            for (int j = 0; j < dataBuffer.size(); j++) {
                JSONObject elem = dataBuffer.get(j);

                // fill language statistics
                String lang = (String) elem.get("lang");
                Integer integer = FakeSpringController.langStats.get(lang);
                if( integer == null){
                    FakeSpringController.langStats.put(lang,1);
                }else {
                    FakeSpringController.langStats.put(lang, FakeSpringController.langStats.get(lang) + 1);
                }

                // fill hashtagStatistics
                JSONObject entities =(JSONObject) elem.get("entities");
                JSONArray hashtags =(JSONArray) entities.get("hashtags");
                for (int k = 0; k < hashtags.size(); k++) {
                    JSONObject hashtag = (JSONObject) hashtags.get(k);
                    String hashtagtext = (String) hashtag.get("text");
                    Integer intagar = FakeSpringController.hashStats.get(hashtagtext);
                    if(intagar == null){
                        FakeSpringController.hashStats.put(hashtagtext,1);
                    }else{
                        FakeSpringController.hashStats.put(hashtagtext,FakeSpringController.hashStats.get(hashtagtext) + 1);
                    }
                }
            }
            dataCtr = 0;

            dataBuffer = new ArrayList<JSONObject>() ;

            try {
                flinkDataChannel.basicConsume(RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME, true, dataConsumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }




    private static void createDataConsumer(Channel dataGeneratorCtrlChannel, String QUEUE_NAME) {
        System.out.println("declaring:" + QUEUE_NAME);
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

                spring.twitterStream.Application.dataCtr += 1;
            }
        };
    }
}
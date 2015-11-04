package spring.twitterFakeStream;

import com.rabbitmq.client.*;
import org.demo.connections.RabbitMQQueueManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        //createDataConsumer2(flinkDataChannel, RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME);


        //Greeting webstorage = new Greeting(0,"0");

        for (int i = 0; i < 100000; i++) {

            System.out.println("durchsatz:" + dataCtr);
            //if (dataCtr > maxDataPerSecond) {
            //String message = Double.toString(((double) maxDataPerSecond / (double) dataCtr ));
            //System.out.println("Too much man!!, sending correction signal:" + message);
            /*try {
                dataCtrlChannel.basicPublish("", RabbitMQQueueManager.FLINK_DATACTRL_QUEUE_NAME, null, message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }*/


            // update stats:

            // count different hashtags
            // count languages

            // count number of tweets arrived


            //FakeSpringController.stats = new ArrayList<Integer>();

            //FakeSpringController.aggStats = new ConcurrentHashMap<String, Integer>();


            /*
            ArrayList<Integer> test = FakeSpringController.aggStats.get("tweetCount");
            if(test == null){
                test = new ArrayList<Integer>();
            }
            test.add(dataBuffer.size());
            FakeSpringController.aggStats.put("tweetCount", test);
            */

            dataCtr = 0;




            dataBuffer = new ArrayList<JSONObject>() ;
            //FakeSpringController.aggStats.


            // display new data
            //webstorage.content = dataBuffer.toString();


            /*stats.add(random.nextInt(100));

            if (stats.size() > 10) {
                stats.remove(0);
            }

            for (int i = 0; i < stats.size(); i++) {
                aggStats.put(Integer.toString(i), stats.get(i));
            }


            for (Map.Entry<String, Integer> entry : langStats.entrySet()) {
                langStats.put(entry.getKey(), (int) Math.round(entry.getValue() * (1 + random.nextDouble())));
            }


            static ArrayList<Integer> stats = new ArrayList<Integer>();

            static Map<String, Integer> aggStats = new ConcurrentHashMap<String, Integer>();

            static Map<String, Integer> langStats = new ConcurrentHashMap<String, Integer>();
            */



            //}
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
                //System.out.println(" [x] Received Data message'" + message + "'");

                //spring.twitterStream.GreetingController.dataBuffer.add(message);


                    /*if(GreetingController.dataBuffer.size() > 500){
                        GreetingController.dataBuffer.remove(0);
                    }*/
                JSONParser parser = new JSONParser();

                JSONObject jsonObj = null;

                try {
                    jsonObj = (JSONObject) parser.parse(message);
                } catch (org.json.simple.parser.ParseException e) {
                    e.printStackTrace();
                }


                Application.dataBuffer.add(jsonObj);
                //spring.twitterStream.Application.dataBuffer.remove(0);

                spring.twitterStream.Application.dataCtr += 1;
            }
        };
    }
}
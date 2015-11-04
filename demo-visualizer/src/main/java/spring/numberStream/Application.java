package spring.numberStream;

import com.rabbitmq.client.*;
import org.demo.connections.RabbitMQQueueManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application {


    private static List<Double> dataBuffer = new ArrayList<Double>();
    private static Connection connection;
    private static Consumer dataConsumer;

    public static Consumer dataCtrlConsumer2;

    public static int dataCtr = 0;
    public static double maxDataPerSecond = 100;


    public static double remotePassProbability = 1.0;


    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);



        Connection connection = RabbitMQQueueManager.createConnection();

        Channel flinkDataChannel = RabbitMQQueueManager.createChannel(connection, RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME);
        Channel dataCtrlChannel = RabbitMQQueueManager.createChannel(connection, RabbitMQQueueManager.FLINK_DATACTRL_QUEUE_NAME);

        createDataConsumer(flinkDataChannel, RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME);
        //createDataConsumer2(flinkDataChannel, RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME);


        //Greeting webstorage = new Greeting(0,"0");

        for (int i = 0; i < 100000; i++) {

            // does this every second


            if(dataCtr == 0){dataCtr = 1;}
                //System.out.println("#DataPoints = " + dataCtr);
                //if (dataCtr > maxDataPerSecond) {

                // naive approach
                //double newValue = (remotePassProbability * (double) maxDataPerSecond / (double) dataCtr);
                //gradient descent
                double newValue = 0.2;//remotePassProbability - 0.000001 * (2.0 * (double) dataCtr * (remotePassProbability * dataCtr - maxDataPerSecond));

                String message = Double.toString(newValue);
                remotePassProbability = newValue;//remotePassProbability * (double) maxDataPerSecond / (double) dataCtr;

                System.out.println(dataCtr + "," + message);

                try {
                    dataCtrlChannel.basicPublish("", RabbitMQQueueManager.FLINK_DATACTRL_QUEUE_NAME, null, message.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            // display new data
            //webstorage.content = dataBuffer.toString();


            //}
            dataCtr = 0;
            try {
                flinkDataChannel.basicConsume(RabbitMQQueueManager.FLINK_DATA_QUEUE_NAME, true, dataConsumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
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

                String[] s = message.split(",");
                if(s.length > 1){
                    System.out.println("has weird format.");
                }
                else {
                    double messageInt = Double.parseDouble(message);


                    GreetingController.dataBuffer.add(messageInt);
                    /*if(GreetingController.dataBuffer.size() > 500){
                        GreetingController.dataBuffer.remove(0);
                    }*/

                    Application.dataBuffer.add(messageInt);
                    Application.dataBuffer.remove(0);

                    Application.dataCtr += 1;
                }
            }
        };
    }
}
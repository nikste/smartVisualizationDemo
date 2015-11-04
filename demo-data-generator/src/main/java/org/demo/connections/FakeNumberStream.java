package org.demo.connections;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * Created by nikste on 26.10.15.
 */
public class FakeNumberStream {

    private static long messagesPerSecond = 500;
    public static boolean running = true;

    public static Consumer dataCtrlConsumer = null;

    public static void main(String[] args){

        System.out.println("started");

        System.out.println("initializing Connection");

        Connection connection = RabbitMQQueueManager.createConnection();

        Channel dataChannel = RabbitMQQueueManager.createChannel(connection, RabbitMQQueueManager.DATA_QUEUE_NAME);
        Channel dataCtrChannel = RabbitMQQueueManager.createChannel(connection, RabbitMQQueueManager.DATACTRL_QUEUE_NAME);


        // create consumers
        createDataCtrlConsumer(dataCtrChannel, RabbitMQQueueManager.DATACTRL_QUEUE_NAME);

        try {
            sendMessages(dataChannel, RabbitMQQueueManager.DATA_QUEUE_NAME, dataCtrChannel, RabbitMQQueueManager.DATACTRL_QUEUE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("send to rabbit mq done");
    }

    private static void createDataCtrlConsumer(Channel dataCtrlChannel, String QUEUE_NAME){
        try {
            dataCtrlChannel.queueDeclare(QUEUE_NAME, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        dataCtrlConsumer = new DefaultConsumer(dataCtrlChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received Close control message'" + message + "'");

                // split
                String[] split = message.split(",");

                assert(split.length == 2);

                int running = Integer.parseInt(split[0]);
                int messagesPerSecond = Integer.parseInt(split[1]);

                FakeNumberStream.messagesPerSecond = messagesPerSecond;
                if(running == 0){
                    FakeNumberStream.running = false;
                }else{
                    FakeNumberStream.running = true;
                }
            }
        };
    }

    private static void sendMessages(Channel dataChannel, String DATA_QUEUE_NAME, Channel dataGeneratorCtrlChannel, String CONTROL_QUEUE_NAME) throws IOException {
        Random randomGenerator = new Random();
        while(running){
            for (int i = 0; i < messagesPerSecond; i++) {
                String message = "" + (double)randomGenerator.nextGaussian();//nextInt(100)/100.0 ;//+ "," + randomGenerator.nextInt(100);
                dataChannel.basicPublish("", DATA_QUEUE_NAME, null, message.getBytes());
            }
            try {

                Thread.sleep(1000);
                System.out.println("sending " + messagesPerSecond);

                dataGeneratorCtrlChannel.basicConsume(CONTROL_QUEUE_NAME, true, dataCtrlConsumer);

                System.out.println(" ");
                System.out.println("parameters:");
                System.out.println("messages/s: " + messagesPerSecond);
                System.out.println("is running: " + running);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            dataGeneratorCtrlChannel.close();
            dataChannel.close();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}


package org.demo.connections;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by nikste on 29.10.15.
 */
public class RabbitMQQueueManager {

    public static String DATACTRL_QUEUE_NAME = "DATACTRL";
    public static String DATA_QUEUE_NAME = "DATA";
    public static String FLINK_DATACTRL_QUEUE_NAME = "FLINK_DATACTRL";
    public static String FLINK_DATA_QUEUE_NAME = "FLINK_DATA";

    public static Connection createConnection(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Channel channel = null;

        Connection connection = null;
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        return connection;
    }


    public static Channel createChannel(Connection connection, String QUEUE_NAME) {
        Channel channel = null;
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channel;
    }
}

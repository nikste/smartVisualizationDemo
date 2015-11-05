package org.demo.connections;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by nikste on 28.10.15.
 */



public class DataSourceControl {

    public static Connection connection = null;

    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.println("started");

        System.out.println("initializing Connection");


        Connection connection = RabbitMQQueueManager.createConnection();

        Channel dataCtrlChannel = RabbitMQQueueManager.createChannel(connection, RabbitMQQueueManager.DATACTRL_QUEUE_NAME);

        //running, numer of itemsper second
        String message = "1,100000";
        dataCtrlChannel.basicPublish("", RabbitMQQueueManager.DATACTRL_QUEUE_NAME, null, message.getBytes());

        dataCtrlChannel.close();

        connection.close();

        System.out.println("finished");
    }
}

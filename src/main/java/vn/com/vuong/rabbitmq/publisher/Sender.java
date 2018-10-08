package vn.com.vuong.rabbitmq.publisher;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import vn.com.vuong.rabbitmq.configuration.Configuration.EXCHANGE;
import vn.com.vuong.rabbitmq.configuration.Configuration.Queue;
import vn.com.vuong.rabbitmq.configuration.Configuration.RoutKey;
import vn.com.vuong.rabbitmq.configuration.RabbitConnection;

public class Sender {
	
	private String name;
	private Connection connectionSend;
	private Channel channelSend;

	public Sender(String name) {
		this.name = name;
		initSend();
	}
	
	private void initSend() {
		try {
			connectionSend = RabbitConnection.getConnection();
			channelSend = connectionSend.createChannel();
			channelSend = declareExchange(connectionSend, channelSend, EXCHANGE.CHAT_EXCHANGE);
			channelSend = declareQueues(channelSend, Queue.QUEUE_CHAT,  EXCHANGE.CHAT_EXCHANGE, RoutKey.CHAT_KEY);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
	
	private  Channel declareExchange(Connection connection, Channel channel, String exchangeName) throws IOException {
		boolean durable  = true; // the exchange will survive a server restart
		if (!isExchangeExisted(channel, exchangeName)) {
			channel = connection.createChannel();
			channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT, durable);
		}
		return channel;
	}

	private  boolean isExchangeExisted(Channel channel, String exchangeName) {
		try {
			channel.exchangeDeclarePassive(exchangeName);
		} catch (IOException ioe) {
			return false;
		}
		return true;
	}

	private  Channel declareQueues(Channel channel, String queueName, String exchangeName, String routingKey) throws IOException, TimeoutException {
		Connection conn = RabbitConnection.getConnection();
		channel = declareContextQueues(conn, channel, queueName, exchangeName, routingKey);
		return channel;
	}

	private  Channel declareContextQueues(Connection connection, Channel channel, String queueName, String exchangeName, String routingKey) throws IOException, TimeoutException {
		 return channel = declareQueue(connection, channel, queueName, exchangeName,routingKey);
	}
	
	private  Channel declareQueue(Connection connection, Channel channel, String queueName, String exchangeName, String routingKey) throws IOException {
		boolean durable  = true; // the queue will survive a server restart
        if (!isQueueExisted(channel, queueName)) {
            channel = connection.createChannel();
            channel.queueDeclare(queueName, durable, false, false, null);
        } else {
            channel.queuePurge(queueName);
        }
        channel.queueBind(queueName, exchangeName, routingKey);
        return channel;
    }

    private boolean isQueueExisted(Channel channel, String queueName) {
        try {
            channel.queueDeclarePassive(queueName);
        } catch (IOException ioe) {
            return false;
        }
        return true;
    }
    
    public void sendMessage(String message) {
		try {
			// publish message to exchange with routing key
			channelSend.basicPublish(EXCHANGE.CHAT_EXCHANGE, RoutKey.CHAT_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			System.out.println(name + ": " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		final Sender sender = new Sender("VuongBV");
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					String message = new Scanner(System.in).nextLine();
					sender.sendMessage(message);
				}
			}
		}).start();
	}
}

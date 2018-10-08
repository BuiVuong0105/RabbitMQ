package vn.com.vuong.rabbitmq.consumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import vn.com.vuong.rabbitmq.configuration.Configuration.Queue;
import vn.com.vuong.rabbitmq.configuration.RabbitConnection;

public class Receiver {
	
	private String name;
	private Connection connectionReceive;
	private Channel channelReceive;
	private Consumer consumer;

	public Receiver(String name) {
		initReceive();
		this.name = name;
	}



	private void initReceive() {
		try {
			connectionReceive = RabbitConnection.getConnection();
			channelReceive = connectionReceive.createChannel();
			channelReceive.basicQos(1);// maximum number of messages that the server will deliver, 0 if unlimited
			boolean autoAck = false; //  true if the server should consider messages acknowledged once delivered; false if the server should expect explicit acknowledgements 
			consumer = new DefaultConsumer(channelReceive) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					System.out.println(name + ": " + message);
					channelReceive.basicAck(envelope.getDeliveryTag(), false); // Confirm to rabbit for delete message
				}
			};
			// consume message from Queue Chat
			channelReceive.basicConsume(Queue.QUEUE_CHAT, autoAck, consumer);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Receiver receive1 = new Receiver("NgocNV");
		Receiver receive2 = new Receiver("NamTV");
	}

}

//class Test {
//	public static void main(String[] args) throws IOException, TimeoutException {
//		send();
//		System.out.println("------------------------------------------------------");
////		receive();
//
//	}
//	
//	public static void receive() throws IOException, TimeoutException {
//		Connection connectionReceived = RabbitConnection.getConnection();
//		Channel channel = connectionReceived.createChannel();
//		Channel channel1 = declareExchange(connectionReceived, channel, "111");
//		Channel channel2 = declareExchange(connectionReceived, channel1, "222");
//		Channel channel3 = declareExchange(connectionReceived, channel2, "333");
//		Channel channel4 = declareExchange(connectionReceived, channel3, "444");
//
//		System.out.println("0 == 1: " + (channel == channel1));
//		System.out.println("1 == 2: " + (channel1 == channel2));
//		System.out.println("2 == 3: " + (channel2 == channel3));
//		System.out.println("3 == 4: " + (channel3 == channel4));
//		
//	}
//
//	public static void send() throws IOException, TimeoutException {
////		Connection connectionSend = RabbitConnection.getConnection();
////		Channel channel = connectionSend.createChannel();
////		Channel channel1 = declareExchange(connectionSend, channel, "111");
////		Channel channel2 = declareExchange(connectionSend, channel1, "222");
////		Channel channel3 = declareExchange(connectionSend, channel2, "333");
////		Channel channel4 = declareExchange(connectionSend, channel3, "444");
//
////		System.out.println("0 == 1: " + (channel == channel1));
////		System.out.println("1 == 2: " + (channel1 == channel2));
////		System.out.println("2 == 3: " + (channel2 == channel3));
////		System.out.println("3 == 4: " + (channel3 == channel4));
//		
////		System.out.println("C "+ channel.isOpen());
////		channel = declareQueues(channel, "30");
////		System.out.println("C "+channel.isOpen());
////		System.out.println("C1 "+channel1.isOpen());
////		declareQueues(channel, "31");
////		System.out.println("C1 "+channel1.isOpen());
//
//	}
//
//	private static Channel declareExchange(Connection connection, Channel channel, String exchangeName)
//			throws IOException {
//		if (!isExchangeExisted(channel, exchangeName)) {
//			channel = connection.createChannel();
//			channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT, true);
//		}
//		return channel;
//	}
//
//	private static boolean isExchangeExisted(Channel channel, String exchangeName) {
//		try {
//			channel.exchangeDeclarePassive(exchangeName);
//		} catch (IOException ioe) {
//			return false;
//		}
//		return true;
//	}
//
//	private static Channel declareQueues(Channel channel, String queueName) throws IOException, TimeoutException {
//		Connection conn = RabbitConnection.getConnection();
//		channel = declareContextQueues(conn, channel, queueName);
//		return channel;
//	}
//
//	private static Channel declareContextQueues(Connection connection, Channel channel, String queueName) throws IOException, TimeoutException {
//		 return channel = declareQueue(connection, channel, queueName, "111", "1");
//	}
//	
//	private static Channel declareQueue(Connection connection, Channel channel, String queueName, String exchangeName, String routingKey) throws IOException {
//        Map<String, Object> args = new HashMap<>();
//        if (!isQueueExisted(channel, queueName)) {
//        	System.out.println("Queue: " + queueName + " NONE Exist");
//            channel = connection.createChannel();
//            channel.queueDeclare(queueName, true, false, false, null);
//        } else {
//        	System.out.println("Queue: " + queueName + " Exist");
//            channel.queuePurge(queueName);
//        }
//        channel.queueBind(queueName, exchangeName, routingKey);
//        return channel;
//    }
//
//    private static boolean isQueueExisted(Channel channel, String queueName) {
//        try {
//            channel.queueDeclarePassive(queueName);
//        } catch (IOException ioe) {
//            return false;
//        }
//        return true;
//    }
//}

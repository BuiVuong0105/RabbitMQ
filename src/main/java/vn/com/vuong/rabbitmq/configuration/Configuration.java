package vn.com.vuong.rabbitmq.configuration;

public class Configuration {
	
	public static class EXCHANGE {
		public static final String CHAT_EXCHANGE = "chat_exchange";
	}
	
	public static class RoutKey{
		public static final String CHAT_KEY = "chat_key";
	}
	
	public static class Host {
		public static final String MESSAGE_IP = "localhost";
	}

	public static class Queue {
		public static final String QUEUE_CHAT = "queue_chat";
		public static final String QUEUE_II = "message_queue_receive_send";
	}
}

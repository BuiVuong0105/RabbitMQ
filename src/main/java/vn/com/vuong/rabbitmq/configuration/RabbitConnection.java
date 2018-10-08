package vn.com.vuong.rabbitmq.configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import vn.com.vuong.rabbitmq.configuration.Configuration.Host;

public class RabbitConnection {
	public static Connection getConnection() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(Host.MESSAGE_IP);
		factory.setConnectionTimeout(0);
		factory.setUsername("admin");
		factory.setPassword("123456123");
//		factory.setHandshakeTimeout(0);
//		factory.setShutdownTimeout(0);
		return factory.newConnection();
	}
}

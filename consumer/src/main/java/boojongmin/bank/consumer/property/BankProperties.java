package boojongmin.bank.consumer.property;

import java.util.Properties;

public class BankProperties {
	public static Properties createConsumerProperties(String topic) {
		final Properties p = new Properties();
		p.put("bootstrap.servers", "localhost:9092");
		p.put("session.timeout.ms", "10000");
		p.put("group.id", topic);
		p.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		p.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return p;
	}

	public static Properties createProducerProperties() {
		Properties p = new Properties();
		p.put("bootstrap.servers", "localhost:9092");
		p.put("acks", "all");
		p.put("block.on.buffer.full", "true");
		p.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		p.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return p;
	}
}

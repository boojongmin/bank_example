package boojongmin.bank.consumer.factory;

import boojongmin.bank.consumer.property.BankProperties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

public class Factory {
	public static KafkaConsumer<String, String> createConsumer(String topic) {
		return new KafkaConsumer<>(BankProperties.createConsumerProperties(topic));
	}

	public static KafkaProducer<String, String> createProducer() {
		return new KafkaProducer<>(BankProperties.createProducerProperties());
	}
}

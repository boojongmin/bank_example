package boojongmin.bank.consumer;

import boojongmin.bank.consumer.domain.Bank;
import boojongmin.bank.consumer.factory.Factory;
import boojongmin.bank.consumer.service.ProduceSerivce;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

public class ProducerMain {
	public static void main(String[] args) throws InterruptedException {
		final KafkaProducer<String, String> producer = Factory.createProducer();
		ProduceSerivce service = new ProduceSerivce(new Bank(), producer);
		ExecutorService es = Executors.newFixedThreadPool(1000);
		// 요구사항 2. b
		AtomicInteger atomicInteger = new AtomicInteger(1);
		for (int i = 1; i <= 50_000; i++) {
			es.submit(() -> {
				final int number = atomicInteger.getAndIncrement();
				service.join(number, format("member_name_%d", number));
			});
		}
		System.out.println(">>>>>>>>>>>");
		es.awaitTermination(10, TimeUnit.SECONDS);
		producer.close();
	}
}

package boojongmin.bank.consumer.service;

import boojongmin.bank.consumer.domain.Bank;
import boojongmin.bank.consumer.domain.Member;
import boojongmin.bank.consumer.model.JoinLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProduceSerivce {
	static final String TOPIC = "BANK";
	public Bank bank;
	private KafkaProducer<String, String> producer;
	private final ObjectMapper mapper = new ObjectMapper();

	public ProduceSerivce(Bank bank, KafkaProducer<String, String> producer) {
		this.bank = bank;
		this.producer = producer;
	}


	public void join(int customerNumber, String name) {
		final Member member = bank.createMember(customerNumber, name);
		JoinLog log = new JoinLog(member.number, member.name, member.createdAt);
		String json = null;
		try {
			json = mapper.writeValueAsString(log);
			this.producer.send(new ProducerRecord<>(TOPIC, json));
			this.producer.flush();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}

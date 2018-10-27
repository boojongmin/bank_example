package boojongmin.bank

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import java.util.*

object Factory {
    val properties = Properties()

    init {
        val clazzLoader = javaClass.classLoader
        properties.load(clazzLoader.getResourceAsStream("bank.properties"))
    }

    fun createConsumer(): KafkaConsumer<String, String> {
        return KafkaConsumer(properties)
    }

    fun createProducer(): KafkaProducer<String, String> {
        return KafkaProducer(properties)
    }
}

data class BankConsumer(val consumer: KafkaConsumer<String, String>) {
    init {
    }

    fun getPairs() = consumer.poll(500)
            .filter { BankEnum.values().contains(BankEnum.valueOf(it.topic())) }
            .map {
                Pair(it.topic(), it.value())
            }
}
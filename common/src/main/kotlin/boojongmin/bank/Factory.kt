package boojongmin.bank

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.TopicPartition
import java.util.*

object Factory {
    val properties = Properties()
    val clazzLoader = javaClass.classLoader

    fun createConsumer(partition: Int): KafkaConsumer<String, String> {
        properties.load(clazzLoader.getResourceAsStream("consumer.properties"))
        val consumer = KafkaConsumer<String, String>(properties)
        val topicPartition = LogStep.values().map {TopicPartition(it.name, partition)}
        consumer.assign(topicPartition)
        return consumer
    }

    fun createProducer(): KafkaProducer<String, String> {
        properties.load(clazzLoader.getResourceAsStream("producer.properties"))
        return KafkaProducer(properties)
    }

    fun createObjectMapper(): ObjectMapper {
        val mapper = ObjectMapper().registerModule(KotlinModule())
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        return mapper
    }
}


package boojongmin.bank

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
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
        val consumer = KafkaConsumer<String, String>(properties)
        consumer.subscribe(LogStep.values().map { it.name })
        return consumer
    }

    fun createProducer(): KafkaProducer<String, String> {
        return KafkaProducer(properties)
    }

    fun createObjectMapper(): ObjectMapper {
        val mapper = ObjectMapper().registerModule(KotlinModule())
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        return mapper
    }
}


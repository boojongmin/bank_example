package boojongmin.bank.consumer

import boojongmin.bank.Factory
import org.apache.kafka.clients.consumer.KafkaConsumer
import spark.Spark.get
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

const val MAX_CONCURRENT_COUNT = 16

// 요구사항 2. b
fun main(args: Array<String>) {
    val consumer = Factory.createConsumer()
//    val service = ProduceSerivce(Bank(), producer)
    val startTime = System.currentTimeMillis()
    val es = Executors.newFixedThreadPool(MAX_CONCURRENT_COUNT)

    println("consumer started!!")
    println("1단계: 카프카로부터 전달받은 데이터 컨슘")
    val runner = ConsumerRunner(es, consumer)
    runner.run()
    println("1단계 완료")

    println("2단계: 카프카로 받은 데이터를 객체화")
    println("2단계 완료")

    println("3단계: api 웹서버를 통해 조회")
    get("/hello") { req, res -> "Hello World" }

}

class ConsumerRunner(val es: ExecutorService, val consumer: KafkaConsumer<String, String>) {
    fun run() {
        consumer.subscribe(Arrays.asList("BANK"))
        for(i in 1..MAX_CONCURRENT_COUNT) {
            es.submit {
                while (true) {
                    val records = consumer.poll(500)
                    for (record in records) {
                        val s = record.topic()
                        if ("BANK" == s) {
                            println(record.value())
                        } else {
                            throw IllegalStateException("get message on topic " + record.topic())
                        }
                    }
                }
            }
        }
    }
}

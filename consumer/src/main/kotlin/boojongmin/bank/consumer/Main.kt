package boojongmin.bank.consumer

import boojongmin.bank.Factory
import boojongmin.bank.Member
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import spark.Spark.get
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

const val MAX_CONCURRENT_COUNT = 16
const val TOPIC_PFEFIX = "BANK_"
// 요구사항 2. b
fun main(args: Array<String>) {
    val cache = ConcurrentHashMap<Int, Member>()
    val es = Executors.newFixedThreadPool(MAX_CONCURRENT_COUNT)
    val consumer = Factory.createConsumer()
    val mapper = ObjectMapper().registerModule(KotlinModule())
    val service = ConsumerSerivce(cache, es, consumer, mapper)

    println("consumer started!!")
    println("1단계: 카프카로부터 전달받은 데이터 컨슘")
    service.aynscConsume()

    println("2단계: api 웹서버를 통해 조회")

    get("/all") {
        req, res -> mapper.writeValueAsString(cache)
    }

    get("/member/:number") {
        req, res ->
            mapper.writeValueAsString(
                cache[req.params(":number").toInt()]
            )
    }

}

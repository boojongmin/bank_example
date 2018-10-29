package boojongmin.bank.consumer

import boojongmin.bank.Factory
import boojongmin.bank.Factory.createObjectMapper
import boojongmin.bank.Member
import com.hazelcast.config.Config
import spark.Spark.get
import com.hazelcast.core.Hazelcast


fun main(args: Array<String>) {
    val cfg = Config()
    val instance = Hazelcast.newHazelcastInstance(cfg)
    val cache = instance.getMap<Int, Member>("bank")

    val mapper = createObjectMapper()
    val consumer = Factory.createConsumer()
    val (partitioncount, runner) = ConsumerRunnerFactory(consumer, cache).process()
    val threadCount = if (partitioncount >= 16) 16 else partitioncount
    println("consumer started!!")
    println("1단계: 카프카로부터 전달받은 데이터 컨슘(partitions: ${partitioncount},thread: ${threadCount})")
    runner.run()

    println("2단계: API 서버 시작됨")
    println("- 전체 member 조회: curl localhost:4567/member")
    println("- 특정 member 조회: curl localhost:4567/member/1")

    get("/member") { _, _ ->
        mapper.writeValueAsString(cache)
    }

    get("/member/:number") { req, _ ->
        mapper.writeValueAsString(
                cache[req.params(":number").toInt()]
        )
    }
}

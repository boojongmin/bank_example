package boojongmin.bank.producer

import boojongmin.bank.Bank
import boojongmin.bank.Factory
import java.lang.String.format
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger


const val MAX_CONCURRENT_COUNT = 10
const val MAX_COUNT = 50
//const val MAX_CONCURRENT_COUNT = 1_000
//const val MAX_COUNT = 50_000

// 요구사항 2. b
fun main(args: Array<String>) {
    val producer = Factory.createProducer()
    val service = ProduceSerivce(Bank(), producer)
    val startTime = System.currentTimeMillis()

    val es = Executors.newFixedThreadPool(MAX_CONCURRENT_COUNT)
    val mainCountDownLatch = CountDownLatch(MAX_COUNT * 5)

    val runner = ProducerRunner(es, mainCountDownLatch)

    println("producer started!!")
    println("1단계: 가입 로그")
    runner.run {
        service.join(it, format("member_name_%d", it))
    }

//    Thread.sleep(3000)

    println("2단계: 계좌 개설 로그")
    runner.run {
        service.createAccount(it)
    }

//    Thread.sleep(3000)

    println("3단계: 입금 로그")
    runner.run {
        service.deposit(it)
    }

//    Thread.sleep(3000)

    println("4단계: 출금 로그")
    runner.run {
        service.withdraw(it)
    }

//    Thread.sleep(3000)

    println("5단계: 이체 로그")
    runner.run {
        service.transfer(it)
    }

    // 프로그램 종료
    mainCountDownLatch.await()
    es.shutdownNow()
    producer.close()
    println("producer finished: ${System.currentTimeMillis() - startTime} ms")
}

class ProducerRunner(val es: ExecutorService, val mainCountDownLatch: CountDownLatch) {
    fun run(f: (num: Int) -> Unit) {
        val atomicInteger = AtomicInteger(1)
        var loopCountDownLatch = CountDownLatch(MAX_CONCURRENT_COUNT)
        for (i in 1..MAX_COUNT) {
            if (i % MAX_CONCURRENT_COUNT == 1 && i != 1) {
                loopCountDownLatch = CountDownLatch(MAX_CONCURRENT_COUNT)
            }
            es.submit {
                val number = atomicInteger.getAndIncrement()
                try {
                    f(number)
                } catch (e: Exception) {
                    println("에러발생: ${e.message}")
                }
                loopCountDownLatch.countDown()
                mainCountDownLatch.countDown()
            }
            if(i % MAX_CONCURRENT_COUNT == 0) loopCountDownLatch.await()
        }

        Thread.sleep(1000)
    }
}
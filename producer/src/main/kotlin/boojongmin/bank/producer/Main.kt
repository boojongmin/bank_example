package boojongmin.bank.producer

import boojongmin.bank.Bank
import boojongmin.bank.BankEnum.BANK1
import boojongmin.bank.Factory
import boojongmin.bank.Factory.createObjectMapper
import org.junit.Ignore
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

fun main(args: Array<String>) {
    val producer = Factory.createProducer()
    val mapper = createObjectMapper()
    val service = ProducerSerivce(Bank(), producer, mapper)
    val startTime = System.currentTimeMillis()

    val MAX_TREAD_COUNT = 1_000
    val MAX_RUN_COUNT = 50_000
    val MAX_ALL_STEP_COUNT = MAX_RUN_COUNT * 5
    val SLEEP_TIME = 1000L
    val countDownLatch = CountDownLatch(MAX_ALL_STEP_COUNT)
    val runner = ProducerRunner(service, countDownLatch, MAX_TREAD_COUNT, MAX_RUN_COUNT, SLEEP_TIME)
    runner.runAllSteps()

    producer.close()
    println("producer finished: ${System.currentTimeMillis() - startTime} ms")
}

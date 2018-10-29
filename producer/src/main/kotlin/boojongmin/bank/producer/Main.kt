package boojongmin.bank.producer

import boojongmin.bank.Bank
import boojongmin.bank.BankEnum.*
import boojongmin.bank.Factory
import boojongmin.bank.Factory.createObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Ignore
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger


//const val MAX_CONCURRENT_COUNT = 1_000
//const val MAX_COUNT = 50_000

// 요구사항 2. b
fun main(args: Array<String>) {
    val producer = Factory.createProducer()
    val mapper = createObjectMapper()
    val service = ProducerSerivce(Bank(), producer, mapper)
    val startTime = System.currentTimeMillis()

    val MAX_TREAD_COUNT = 50
    val MAX_RUN_COUNT = 50
    val MAX_ALL_STEP_COUNT = MAX_RUN_COUNT * 5
    val SLEEP_TIME = 1000L
    val countDownLatch = CountDownLatch(MAX_ALL_STEP_COUNT)
    val runner = ProducerRunner(service, countDownLatch, MAX_TREAD_COUNT, MAX_RUN_COUNT, SLEEP_TIME)
    runner.runAllSteps()

    producer.close()
    println("producer finished: ${System.currentTimeMillis() - startTime} ms")
}

@Ignore
class ProducerRunner(val service: ProducerSerivce, val countDownLatch: CountDownLatch, val MAX_THREAD_COUNT: Int, val MAX_RUN_COUNT: Int, val SLEEP_TIME: Long) {
    val es: ExecutorService = Executors.newFixedThreadPool(MAX_THREAD_COUNT)
    fun runAllSteps() {
        println("producer started!!")
        println("1단계: 가입 로그")
        join()
        Thread.sleep(SLEEP_TIME)
        println("2단계: 계좌 개설 로그")
        createAccount()
        Thread.sleep(SLEEP_TIME)
        println("3단계: 입금 로그")
        deposit()
        Thread.sleep(SLEEP_TIME)
        println("4단계: 출금 로그")
        withdraw()
        Thread.sleep(SLEEP_TIME)
        println("5단계: 이체 로그")
        transfer()

        countDownLatch.await()
        es.shutdownNow()
    }

    private fun join() {
        val atomicInteger = AtomicInteger(1)
        for (i in 1..MAX_RUN_COUNT) {
            es.submit {
                val number = atomicInteger.getAndIncrement()
                try {
                    service.join(number, "member_${number}")
                } catch (e: Exception) {
                    println("에러발생: ${e.message}")
                }
                countDownLatch.countDown()
            }
        }
    }

    private fun createAccount() {
        val atomicInteger = AtomicInteger(1)
        for (i in 1..MAX_RUN_COUNT) {
            es.submit {
                val number = atomicInteger.getAndIncrement()
                try {
                    service.createAccount(number)
                } catch (e: Exception) {
                    println("에러발생: ${e.message}")
                }
                countDownLatch.countDown()
            }
        }
    }

    private fun deposit() {
        val atomicInteger = AtomicInteger(1)
        for (i in 1..MAX_RUN_COUNT) {
            es.submit {
                val number = atomicInteger.getAndIncrement()
                val amount = (Math.random() * 10000).toInt()
                try {
                    service.deposit(number, amount)
                } catch (e: Exception) {
                    println("에러발생: ${e.message}")
                }
                countDownLatch.countDown()
            }
        }
    }

    private fun withdraw() {
        val atomicInteger = AtomicInteger(1)
        for (i in 1..MAX_RUN_COUNT) {
            es.submit {
                val number = atomicInteger.getAndIncrement()
                val amount = (Math.random() * 10000).toInt()
                try {
                    service.withdraw(number, amount)
                } catch (e: Exception) {
                    println("에러발생: ${e.message}")
                }
                countDownLatch.countDown()
            }
        }
    }

    private fun transfer() {
        val atomicInteger = AtomicInteger(1)
        for (i in 1..MAX_RUN_COUNT) {
            es.submit {
                val number = atomicInteger.getAndIncrement()
                val amount = (Math.random() * 10000).toInt()
                val bankEnum = BANK1
                val outAccountNumber = "XXXX-XXXX-XXXX"
                val name = "XXX"
                try {
                    service.transfer(number, amount, bankEnum, outAccountNumber, name)
                } catch (e: Exception) {
                    println("에러발생: ${e.message}")
                }
                countDownLatch.countDown()
            }
        }
    }
}
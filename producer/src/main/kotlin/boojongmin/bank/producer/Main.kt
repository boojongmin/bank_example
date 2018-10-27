package boojongmin.bank.producer

import boojongmin.bank.Bank
import boojongmin.bank.BankEnum
import boojongmin.bank.BankEnum.*
import boojongmin.bank.Factory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Ignore
import java.lang.String.format
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger


const val MAX_CONCURRENT_COUNT = 10
const val MAX_COUNT = 50
const val MAX_ALL_STEP_COUNT = MAX_COUNT * 5
//const val MAX_CONCURRENT_COUNT = 1_000
//const val MAX_COUNT = 50_000

// 요구사항 2. b
fun main(args: Array<String>) {
    val producer = Factory.createProducer()
    val mapper = ObjectMapper().registerModule(KotlinModule())
    val service = ProduceSerivce(Bank(), producer, mapper)
    val startTime = System.currentTimeMillis()

    val es = Executors.newFixedThreadPool(MAX_CONCURRENT_COUNT)
    val mainCountDownLatch = CountDownLatch(MAX_ALL_STEP_COUNT)

    val runner = ProducerRunner(es, mainCountDownLatch)

    println("producer started!!")
    println("1단계: 가입 로그")
    runner.run1 {
        service.join(it, format("member_name_%d", it))
    }
    Thread.sleep(2000)

    println("2단계: 계좌 개설 로그")
    runner.run1 {
        service.createAccount(it)
    }

    Thread.sleep(2000)

    println("3단계: 입금 로그")
    runner.run2 { _number, _amount ->
        service.deposit(_number, _amount)
    }

    Thread.sleep(2000)

    println("4단계: 출금 로그")
    runner.run2 { _number, _amount ->
        service.withdraw(_number, _amount)
    }

    Thread.sleep(2000)

    println("5단계: 이체 로그")
    runner.run3 {_number, _amount, _bankEnum, _outAccountNumber, _name ->
        service.transfer(_number, _amount, _bankEnum, _outAccountNumber, _name)
    }

    // 프로그램 종료
    mainCountDownLatch.await()
    es.shutdownNow()
    producer.close()
    println("producer finished: ${System.currentTimeMillis() - startTime} ms")
}

@Ignore
class ProducerRunner(val es: ExecutorService, val mainCountDownLatch: CountDownLatch) {
    fun run1(f: (_number: Int) -> Unit) {
        val atomicInteger = AtomicInteger(1)
        for (i in 1..MAX_COUNT) {
            es.submit {
                val number = atomicInteger.getAndIncrement()
                try {
                    f(number)
                } catch (e: Exception) {
                    println("에러발생: ${e.message}")
                }
                mainCountDownLatch.countDown()
            }
        }
    }

    fun run2(f: (_number: Int, _amount: Int) -> Unit) {
        val atomicInteger = AtomicInteger(1)
        for (i in 1..MAX_COUNT) {
            es.submit {
                val number = atomicInteger.getAndIncrement()
                val amount = (Math.random() * 10000).toInt()
                try {
                    f(number, amount)
                } catch (e: Exception) {
                    println("에러발생: ${e.message}")
                }
                mainCountDownLatch.countDown()
            }
        }

        Thread.sleep(5000)
    }

    fun run3(f: (_number: Int, _amount: Int, _bankEnum: BankEnum, _outAccountNumber: String, _name: String) -> Unit) {
        val atomicInteger = AtomicInteger(1)
        for (i in 1..MAX_COUNT) {
            es.submit {
                val number = atomicInteger.getAndIncrement()
                val amount = (Math.random() * 10000).toInt()
                try {
                    val bankEnum = BANK1
                    val outAccountNumber = "XXXX-XXXX-XXXX"
                    val name = "XXX"
                    f(number, amount, bankEnum, outAccountNumber, name)
                } catch (e: Exception) {
                    println("에러발생: ${e.message}")
                }
                mainCountDownLatch.countDown()
            }
        }

        Thread.sleep(5000)
    }
}
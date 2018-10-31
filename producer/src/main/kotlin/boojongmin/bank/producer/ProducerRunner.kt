package boojongmin.bank.producer

import boojongmin.bank.BankEnum.BANK1
import java.lang.Thread.sleep
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class ProducerRunner(val service: ProducerSerivce, val countDownLatch: CountDownLatch, val MAX_THREAD_COUNT: Int, val MAX_RUN_COUNT: Int, val SLEEP_TIME: Long) {
    val es: ExecutorService = Executors.newFixedThreadPool(MAX_THREAD_COUNT)
    fun runAllSteps() {
        println("producer started!!")
        println("1단계: 가입 로그")
        join()
        sleep(1000L)
        println("2단계: 계좌 개설 로그")
        createAccount()
        sleep(1000L)
        println("3단계: 입금 로그")
        deposit()
        sleep(1000L)
        println("4단계: 출금 로그")
        withdraw()
        sleep(1000L)
        println("5단계: 이체 로그")
        transfer()
        sleep(1000L)

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
            if(i % 1000 == 0 && i > 0) Thread.sleep(SLEEP_TIME)
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
            if(i % 1000 == 0 && i > 0) Thread.sleep(SLEEP_TIME)
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
            if(i % 1000 == 0 && i > 0) Thread.sleep(SLEEP_TIME)
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
            if(i % 1000 == 0 && i > 0) Thread.sleep(SLEEP_TIME)
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
            if(i % 1000 == 0 && i > 0) Thread.sleep(SLEEP_TIME)
        }
    }
}
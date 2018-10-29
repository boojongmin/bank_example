package boojongmin.bank.producer

import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService

class ProducerRunnerTest {

    @Test
    fun runAllStep() {
        val MAX_TREAD_COUNT = 1
        val MAX_RUN_COUNT = 10
        val MAX_ALL_STEP_COUNT = MAX_RUN_COUNT * 5
        val SLEEP_TIME = 0L
        val countDownLatch = CountDownLatch(MAX_ALL_STEP_COUNT)
        val service:ProducerSerivce = mock()
        val runner = ProducerRunner(service, countDownLatch, MAX_TREAD_COUNT, MAX_RUN_COUNT, SLEEP_TIME)

        given(service.join(any(), any())).will { countDownLatch.countDown() }
        given(service.createAccount(any())).will { countDownLatch.countDown() }
        given(service.deposit(any(), any())).will { countDownLatch.countDown() }
        given(service.withdraw(any(), any())).will { countDownLatch.countDown() }
        given(service.transfer(any(), any(), any(), any(), any())).will { countDownLatch.countDown() }

        runner.runAllSteps()
        assertThat(countDownLatch.count).isEqualTo(0)
    }
}

package boojongmin.bank.producer

import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService

class ProducerRunnerTest {
    lateinit var es: ExecutorService
    lateinit var runner: ProducerRunner
    lateinit var countDownLatch: CountDownLatch

    @BeforeEach
    fun before() {
        es = mock()
        countDownLatch = CountDownLatch(MAX_ALL_STEP_COUNT)
        runner = ProducerRunner(es, countDownLatch)
    }

    @Test
    fun run1() {
        given(es.submit(any())).willReturn(any())
        runner.run1 { _ ->
            countDownLatch.countDown()
        }
        verify(es, times(MAX_COUNT)).submit(any())
    }

    @Test
    fun run2() {
        given(es.submit(any())).willReturn(any())
        runner.run2 { _, _ ->
            countDownLatch.countDown()
        }
        verify(es, times(MAX_COUNT)).submit(any())
    }

    @Test
    fun run3() {
        given(es.submit(any())).willReturn(any())
        runner.run3 { _, _, _, _, _ ->
            countDownLatch.countDown()
        }
        verify(es, times(MAX_COUNT)).submit(any())
    }
}

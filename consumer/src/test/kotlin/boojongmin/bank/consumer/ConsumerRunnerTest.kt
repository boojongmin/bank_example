package boojongmin.bank.consumer

import boojongmin.bank.*
import boojongmin.bank.Factory.createObjectMapper
import boojongmin.bank.LogStep.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.MockConsumer
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.apache.kafka.common.TopicPartition
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService

fun createMockConsumer(topic: String): MockConsumer<String, String> {
    val consumer: MockConsumer<String, String> = MockConsumer(OffsetResetStrategy.EARLIEST)
    consumer.assign(arrayListOf(TopicPartition(topic, 0)))
    val beginningOffsets = mutableMapOf<TopicPartition, Long>()
    beginningOffsets[TopicPartition(topic, 0)] = 0L
    consumer.updateBeginningOffsets(beginningOffsets)
    return consumer
}

class ConsumerRunnerTest {
    @Test
    fun run() {
        val MAX_THREAD_COUNT = 100
        val service: IConsumerService = mock()
        val es: ExecutorService = mock()
        val runner = ConsumerRunner(es, service, MAX_THREAD_COUNT)
        runner.run()
        verify(es, times(MAX_THREAD_COUNT)).submit(any())
    }
}

class ConsumerRunnableTest {
    @Test
    fun run() {
        val service: IConsumerService = mock()
        val consumer: MockConsumer<String, String> = createMockConsumer("topic")
        consumer.addRecord(ConsumerRecord<String, String>("topic", 0, 0L, "key1", "value1"))
        consumer.addRecord(ConsumerRecord<String, String>("topic", 0, 1L, "key2", "value2"))
        consumer.addRecord(ConsumerRecord<String, String>("topic", 0, 2L, "key3", "value3"))

        val consumerRunnable = ConsumerRunnable(consumer, service, true)
        consumerRunnable.run()

        verify(service, times(3)).consume(anyString(), anyString())
    }

    @Test
    fun getConsumer() {
        val service: IConsumerService = mock()
        val consumer: MockConsumer<String, String> = MockConsumer(OffsetResetStrategy.EARLIEST)
        val consumerRunnable = ConsumerRunnable(consumer, service, true)
        assertThat(consumerRunnable.consumer).isNotNull
    }

    @Test
    fun getService() {
        val service: IConsumerService = mock()
        val consumer: MockConsumer<String, String> = MockConsumer(OffsetResetStrategy.EARLIEST)
        val consumerRunnable = ConsumerRunnable(consumer, service, true)
        assertThat(consumerRunnable.service).isNotNull
    }

    @Test
    fun isTest() {
        val service: IConsumerService = mock()
        val consumer: MockConsumer<String, String> = MockConsumer(OffsetResetStrategy.EARLIEST)
        val consumerRunnable = ConsumerRunnable(consumer, service, true)
        assertThat(consumerRunnable.isTest).isTrue()
    }
}

class ConsumerServiceTest {
    lateinit var mapper: ObjectMapper
    lateinit var cache: ConcurrentHashMap<Int, Member>
    lateinit var service: ConsumerService

    val memberNumber = 1
    val memberName = "name_1"
    val accountNumber = "11111-11111"

    @BeforeEach
    fun beforeEach() {
        mapper = createObjectMapper()
        cache = ConcurrentHashMap()
        service = ConsumerService(cache, mapper)

        val joinLog = JoinLog(1, memberName, Date())
        val joinJson = mapper.writeValueAsString(joinLog)
        service.consume(BANK_JOIN.name, joinJson)

        val createAccountLog = CreateAccountLog(memberNumber, accountNumber, Date())
        val createAccountJson = mapper.writeValueAsString(createAccountLog)
        service.consume(BANK_CREATE_ACCOUNT.name, createAccountJson)
    }

    @Test
    fun consume() {
        service.consume("topic", "value")
        val member = cache[1]!!
        assertThat(member.accounts.first().transactions.size).isEqualTo(0)
    }

    @Test
    fun consume_BANK_JOIN() {
        val member = cache[1]!!
        assertThat(member).isNotNull
        assertThat(member.number).isEqualTo(memberNumber)
        assertThat(member.name).isEqualTo(memberName)
    }

    @Test
    fun consume_BANK_CREATE_ACCOUNT() {
        val account = cache[1]!!.accounts.first()
        assertThat(account).isNotNull
        assertThat(account.member.number).isEqualTo(memberNumber)
        assertThat(account.number).isEqualTo(accountNumber)
    }

    @Test
    fun consume_BANK_DEPOSIT() {
        val amount = 10_000_000
        val log = DepositLog(memberNumber, accountNumber, amount, Date())
        val json = mapper.writeValueAsString(log)
        service.consume(BANK_DEPOSIT.name, json)

        val transaction = cache[1]!!.accounts.first().transactions.first() as DepositTransaction
        assertThat(transaction).isNotNull
        assertThat(transaction).isInstanceOf(DepositTransaction::class.java)
        assertThat(transaction.amount).isEqualTo(amount)
    }

    @Test
    fun consume_BANK_WITHDRAW() {
        val amount = 10_000
        val log = WithdrawLog(memberNumber, accountNumber, amount, Date())
        val json = mapper.writeValueAsString(log)
        service.consume(BANK_WITHDRAW.name, json)

        val transaction = cache[1]!!.accounts.first().transactions.first() as WithdrawTransaction
        assertThat(transaction).isNotNull
        assertThat(transaction).isInstanceOf(WithdrawTransaction::class.java)
        assertThat(transaction.amount).isEqualTo(amount)
    }

    @Test
    fun consume_BANK_TRANSFER() {
        val bankEnum = BankEnum.BANK1
        val outAccountNumber = "99999-99999";
        val outName = "out_name"
        val amount = 100
        val log = TransferLog(memberNumber, accountNumber, bankEnum, outAccountNumber, outName, amount, Date())
        val json = mapper.writeValueAsString(log)
        service.consume(BANK_TRANSFER.name, json)

        val transaction = cache[1]!!.accounts.first().transactions.first() as TransferTransaction
        assertThat(transaction).isNotNull
        assertThat(transaction).isInstanceOf(TransferTransaction::class.java)
        assertThat(transaction.bankEnum).isEqualTo(bankEnum)
        assertThat(transaction.outAccountNumber).isEqualTo(outAccountNumber)
        assertThat(transaction.name).isEqualTo(outName)
        assertThat(transaction.amount).isEqualTo(amount)
    }
}

class ConsumerRunnerFactoryTest {
    @Test
    fun createConsumerRunner() {
        val maxThreadCount = 10
        val service: IConsumerService = mock()
        val consumer = createMockConsumer("topic")
        val cache: ConcurrentHashMap<Int, Member> = mock()

        val runner = ConsumerRunnerFactory(consumer, cache).createConsumerRunner(maxThreadCount, service)
        assertThat(runner).isNotNull
    }

    @Test
    fun process() {
        val consumer: MockConsumer<String, String> = createMockConsumer(BANK_JOIN.name)
        val cache: ConcurrentHashMap<Int, Member> = mock()
        val (partitionCount, runner ) = ConsumerRunnerFactory(consumer, cache).process()
        assertThat(partitionCount).isEqualTo(1)
        assertThat(runner).isNotNull
        assertThat(cache).isNotNull
    }
}



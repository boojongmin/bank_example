package boojongmin.bank.producer

import boojongmin.bank.*
import boojongmin.bank.LogStep.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.nhaarman.mockitokotlin2.*
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.ExecutorService


class ProduceSerivceTest {
    val mapper = ObjectMapper().registerModule(KotlinModule())

    lateinit var es: ExecutorService
    lateinit var producer: MockProducer<String, String>
    lateinit var service: ProduceSerivce
    lateinit var bank: Bank
    lateinit var member: Member

    @BeforeEach
    fun before() {
        es = mock()
        bank = Bank()
        member = spy(Member(1, "name_1"))

        bank.memberMap.put(1, member)
        bank.memberMap[1]!!.accounts.add(Account(member, UUID.randomUUID().toString()))

        producer = MockProducer<String, String>(
                true, StringSerializer(), StringSerializer())
        service = ProduceSerivce(bank, producer, mapper)
    }

    @Test
    fun join() {
        val number = 2
        val name = "name_${number}"
        service.join(number, name)

        val record = producer.history().first()
        val topic = record.topic()
        val json = record.value()
        val log = mapper.deserialize<MemberLog>(json)

        assertThat(valueOf(topic)).isEqualTo(BANK_JOIN)
        val expected = MemberLog(number, name, Date())
        assertThat(log).isEqualTo(expected)
        assertThat(bank.memberMap.size).isEqualTo(2)
    }

    @Test
    fun createAccount() {
        val memberNumber = 1
        service.createAccount(memberNumber)

        val record = producer.history().first()
        val topic = record.topic()
        val json = record.value()
        val log = mapper.deserialize<AccountLog>(json)
        val member = bank.memberMap[memberNumber]

        val expect = AccountLog(memberNumber, log.accountNumber, Date())
        assertThat(valueOf(topic)).isEqualTo(BANK_CREATE_ACCOUNT)
        assertThat(member!!.accounts.size).isEqualTo(2)
        assertThat(log).isEqualTo(expect)
    }

    @Test
    fun deposit() {
        val memberNumber = 1
        val amount = 10_000
        service.deposit(memberNumber, amount)

        val record = producer.history().first()
        val topic = record.topic()
        val json = record.value()
        val log = mapper.deserialize<DepositLog>(json)
        val member = bank.memberMap[memberNumber]
        val accountNumber = member!!.accounts.first().number

        val expect = DepositLog(memberNumber, accountNumber, amount, Date())
        assertThat(valueOf(topic)).isEqualTo(BANK_DEPOSIT)
        assertThat(log).isEqualTo(expect)
    }

    @Test
    fun withdraw() {
        val memberNumber = 1
        val amount = 10_000
        service.withdraw(memberNumber, amount)

        val record = producer.history().first()
        val topic = record.topic()
        val json = record.value()
        val log = mapper.deserialize<WithdrawLog>(json)
        val member = bank.memberMap[memberNumber]
        val accountNumber = member!!.accounts.first().number

        val expect = WithdrawLog(memberNumber, accountNumber, amount, Date())
        assertThat(valueOf(topic)).isEqualTo(BANK_WITHDRAW)
        assertThat(log).isEqualTo(expect)
    }

    @Test
    fun transfer() {
        val memberNumber = 1
        val amount = 10_000
        val outAccountNUmber = "XXX-XXX"
        val name = "honggildong"
        service.transfer(memberNumber, amount, BankEnum.BANK1, outAccountNUmber, name)

        val record = producer.history().first()
        val topic = record.topic()
        val json = record.value()
        val log = mapper.deserialize<TransferLog>(json)
        val member = bank.memberMap[memberNumber]
        val accountNumber = member!!.accounts.first().number

        val expect = TransferLog(memberNumber, accountNumber, BankEnum.BANK1, outAccountNUmber, name, amount, Date())
        assertThat(valueOf(topic)).isEqualTo(BANK_TRANSFER)
        assertThat(log).isEqualTo(expect)
    }
}

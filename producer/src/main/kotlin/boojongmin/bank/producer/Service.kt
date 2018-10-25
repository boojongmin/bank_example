package boojongmin.bank.producer

import boojongmin.bank.*
import boojongmin.bank.LogStep.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

class ProduceSerivce(var bank: Bank, private val producer: KafkaProducer<String, String>) {
    private val mapper = ObjectMapper()

    private fun sendLog(step: LogStep, log: Log) {
        val json = mapper.writeValueAsString(log)
        this.producer.send(ProducerRecord(step.name, json))
        this.producer.flush()
    }

    fun join(customerNumber: Int, name: String) {
        val (number, name1, createdAt, _) = bank.createMember(customerNumber, name)
        val log = MemberLog(number, name1, createdAt)
        sendLog(BANK_JOIN, log)
    }

    fun createAccount(number: Int) {
        val member = bank.memberMap[number]
        val account: Account = member!!.createAccount()
        val log = AccountLog(account.member.number, account.number, account.createdAt)
        sendLog(BANK_CREATE_ACCOUNT, log)
    }

    fun deposit(number: Int) {
        val (account, amount) = getAccountAndAmount(number)
        val tx = DepositTransaction(account, amount)
        account.transactions.add(tx)
        val log = DepositLog(tx.account.member.number, tx.account.number, tx.amount, tx.account.createdAt)
        sendLog(BANK_DEPOSIT, log)
    }

    fun withdraw(number: Int) {
        val (account, amount) = getAccountAndAmount(number)
        val tx = WithdrawTransaction(account, amount)
        account.transactions.add(tx)
        val log = WithdrawLog(tx.account.member.number, tx.account.number, tx.amount, tx.createdAt)
        sendLog(BANK_WITHDRAW, log)
    }

    fun transfer(number: Int) {
        val (account, amount) = getAccountAndAmount(number)
        val bank = BankEnum.BANK1
        val outAccountNumber = "XXXX-XXXX-XXXX"
        val name = "XXX"
        val tx = TransferTransaction(account, amount, bank, outAccountNumber, name)
        account.transactions.add(tx)
        val log = TransferLog(tx.account.member.number, tx.account.number, tx.bankEnum, tx.outAccountNumber, tx.name, tx.amount, tx.createdAt)
        sendLog(BANK_TRANSFER, log)
    }

    fun getAccountAndAmount(number: Int): Pair<Account, Long> {
        val member = bank.memberMap[number]!!
        val account = bank.createAccount(member)
        // 금액은 임의의 값
        val amount = (Math.random() * 100_000_000_000).toLong()
        return Pair(account, amount)
    }
}
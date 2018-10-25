package boojongmin.bank.producer

import boojongmin.bank.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

class ProduceSerivce(var bank: Bank, private val producer: KafkaProducer<String, String>) {
    companion object {
        val TOPIC = "BANK"
    }

    private val mapper = ObjectMapper()

    private fun sendLog(log: LogModel) {
        val json = mapper.writeValueAsString(log)
        this.producer.send(ProducerRecord(TOPIC, json))
        this.producer.flush()
    }

    fun join(customerNumber: Int, name: String) {
        val (number, name1, _, createdAt) = bank.createMember(customerNumber, name)
        val log = JoinLog(number, name1, createdAt)
        sendLog(LogModel(LogStep.JOIN, log))
    }

    fun createAccount(number: Int) {
        val member = bank.memberMap[number]
        val account: Account = member!!.createAccount()
        val log = AccountLog(account.member.number, account.number, account.createdAt)
        sendLog(LogModel(LogStep.CREATE_ACCOUNT, log))
    }

    fun deposit(number: Int) {
        val (account, amount) = getAccountAndAmount(number)
        val tx = DepositTransaction(account, amount)
        account.transactions.add(tx)
        val log = TransactionLog(tx.account.member.number, tx.account.number, tx.amount, tx.account.createdAt)
        sendLog(LogModel(LogStep.DEPOSIT, log))
    }

    fun withdraw(number: Int) {
        val (account, amount) = getAccountAndAmount(number)
        val tx = DepositTransaction(account, amount)
        account.transactions.add(tx)
        val log = TransactionLog(tx.account.member.number, tx.account.number, tx.amount, tx.createdAt)
        sendLog(LogModel(LogStep.WITHDRAW, log))
    }

    fun transfer(number: Int) {
        val (account, amount) = getAccountAndAmount(number)
        var bank = BankEnum.BANK1
        var outAccountNumber = "XXXX-XXXX-XXXX"
        var name = "XXX"
        val tx = TransferTransaction(account, amount, bank, outAccountNumber, name)
        account.transactions.add(tx)
        val log = TransactionLog(tx.account.member.number, tx.account.number, tx.bankEnum, tx.outAccountNumber, tx.name, tx.amount, tx.createdAt)
        sendLog(LogModel(LogStep.TRANSFER, log))
    }

    fun getAccountAndAmount(number: Int): Pair<Account, Long> {
        val member = bank.memberMap[number]!!
        val account = bank.createAccount(member)
        // 금액은 임의의 값
        val amount = (Math.random() * 100_000_000_000).toLong()
        return Pair(account, amount)
    }
}
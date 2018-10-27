package boojongmin.bank.producer

import boojongmin.bank.*
import boojongmin.bank.LogStep.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

class ProduceSerivce(val bank: Bank, val producer: Producer<String, String>, val mapper: ObjectMapper) {
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

    fun deposit(number: Int, amount: Int) {
        val account = bank.memberMap[number]!!.accounts.first()
        val tx = DepositTransaction(account, amount)
        account.transactions.add(tx)
        val log = DepositLog(tx.account.member.number, tx.account.number, tx.amount, tx.account.createdAt)
        sendLog(BANK_DEPOSIT, log)
    }

    fun withdraw(number: Int, amount: Int) {
        val account = bank.memberMap[number]!!.accounts.first()
        val tx = WithdrawTransaction(account, amount)
        account.transactions.add(tx)
        val log = WithdrawLog(tx.account.member.number, tx.account.number, tx.amount, tx.createdAt)
        sendLog(BANK_WITHDRAW, log)
    }

    fun transfer(number: Int, amount: Int, bankEnum: BankEnum, outAccountNumber: String, name: String) {
        val account = bank.memberMap[number]!!.accounts.first()
        val tx = TransferTransaction(account, amount, bankEnum, outAccountNumber, name)
        account.transactions.add(tx)
        val log = TransferLog(tx.account.member.number, tx.account.number, tx.bankEnum, tx.outAccountNumber, tx.name, tx.amount, tx.createdAt)
        sendLog(BANK_TRANSFER, log)
    }
}
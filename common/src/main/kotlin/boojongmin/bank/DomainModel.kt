package boojongmin.bank

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import java.util.concurrent.ConcurrentHashMap


data class Bank(var memberMap: ConcurrentHashMap<Int, Member> = ConcurrentHashMap()) {
    fun createMember(number: Int, name: String): Member {
        val member = Member(number, name)
        this.memberMap[number] = member
        return member
    }

    fun createAccount(member: Member): Account {
        val account = Account(member, UUID.randomUUID().toString())
        member.accounts.add(account)
        return account
    }
}

data class Member(var number: Int, var name: String,
                  val createdAt: Date = Date(), val accounts: MutableList<Account> = ArrayList()) {
    fun createAccount(): Account {
        val account = Account(this, UUID.randomUUID().toString())
        this.accounts.add(account)
        return account
    }
}


data class Account(
        @JsonIgnore
        val member: Member,
        val number: String,
        val createdAt: Date = Date(),
        val transactions: MutableList<Transaction> = ArrayList()
)

sealed class Transaction

data class DepositTransaction(
        @JsonIgnore
        val account: Account,
        var amount: Long,
        val createdAt: Date = Date()): Transaction()

data class WithdrawTransaction(
        @JsonIgnore
        val account: Account,
        var amount: Long,
        val createdAt: Date = Date()): Transaction()

data class TransferTransaction(
        @JsonIgnore
        val account: Account,
        val amount: Long,
        val bankEnum: BankEnum,
        var outAccountNumber: String,
        var name: String,
        val createdAt: Date = Date()): Transaction()

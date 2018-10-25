package boojongmin.bank

import java.util.*
import java.util.concurrent.ConcurrentHashMap

data class Account(val member: Member, val number: String,
                   val createdAt: Date = Date(), val transactions: MutableList<Transaction> = ArrayList()
)

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
                  val accounts: MutableList<Account> = ArrayList(), val createdAt: Date = Date()) {
    fun createAccount(): Account {
        val account = Account(this, UUID.randomUUID().toString())
        this.accounts.add(account)
        return account
    }
}

sealed class Transaction

data class DepositTransaction(val account: Account, var amount: Long, val createdAt: Date = Date()): Transaction()

data class WithdrawTransaction(val account: Account, var amount: Long, val createdAt: Date = Date()): Transaction()

data class TransferTransaction(val account: Account, val amount: Long, val bankEnum: BankEnum, var outAccountNumber: String, var name: String,
                               val createdAt: Date = Date()): Transaction()

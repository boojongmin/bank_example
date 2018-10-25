package boojongmin.bank

import java.util.*


data class LogModel(val step: LogStep, val log: Log)

sealed class Log

data class AccountLog (val memberNumber: Int, val accountNumber: String, val createdAt: Date): Log()

data class JoinLog(val number: Int, val name: String, val createdAt: Date): Log()

data class TransactionLog(val memberNumber: Int, val accountNumber: String, val bank: BankEnum, val outAccountNumber: String?,
                     val name: String?, val amount: Long, val createdAt: Date): Log() {
    constructor(memberNumber: Int, accountNumber: String, amount: Long, createdAt: Date)
            : this(memberNumber, accountNumber, BankEnum.BANK1, null, null, amount, createdAt)
}

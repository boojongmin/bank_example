package boojongmin.bank

import java.util.*

abstract class Log(val key: Int)

data class MemberLog(val number: Int, val name: String, val createdAt: Date): Log(number)

data class AccountLog (val memberNumber: Int, val accountNumber: String, val createdAt: Date): Log(memberNumber)

data class DepositLog(val memberNumber: Int, val accountNumber: String, val amount: Long, val createdAt: Date): Log(memberNumber)

data class WithdrawLog(val memberNumber: Int, val accountNumber: String, val amount: Long, val createdAt: Date): Log(memberNumber)

data class TransferLog(val memberNumber: Int, val accountNumber: String, val bank: BankEnum, val outAccountNumber: String?,
                       val name: String?, val amount: Long, val createdAt: Date): Log(memberNumber)


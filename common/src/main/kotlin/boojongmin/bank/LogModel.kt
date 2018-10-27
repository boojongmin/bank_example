package boojongmin.bank

import java.util.*

abstract class Log(val key: Int)

data class MemberLog(val number: Int, val name: String, val createdAt: Date): Log(number) {
    override fun equals(other: Any?): Boolean {
        if (other is MemberLog) {
            if(this.name == other.name && this.number == other.number) {
                return true
            }
        }
        return false
    }
}

data class AccountLog (val memberNumber: Int, val accountNumber: String, val createdAt: Date): Log(memberNumber) {
    override fun equals(other: Any?): Boolean {
        if (other is AccountLog) {
            if(this.memberNumber == other.memberNumber && this.accountNumber == other.accountNumber) {
                return true
            }
        }
        return false
    }
}

data class DepositLog(val memberNumber: Int, val accountNumber: String, val amount: Int, val createdAt: Date): Log(memberNumber) {
    override fun equals(other: Any?): Boolean {
        if (other is DepositLog) {
            if(this.memberNumber == other.memberNumber && this.accountNumber == other.accountNumber && this.amount == other.amount) {
                return true
            }
        }
        return false
    }
}

data class WithdrawLog(val memberNumber: Int, val accountNumber: String, val amount: Int, val createdAt: Date): Log(memberNumber) {
    override fun equals(other: Any?): Boolean {
        if (other is WithdrawLog) {
            if(this.memberNumber == other.memberNumber && this.accountNumber == other.accountNumber && this.amount == other.amount) {
                return true
            }
        }
        return false
    }
}

data class TransferLog(val memberNumber: Int, val accountNumber: String, val bank: BankEnum, val outAccountNumber: String?,
                       val name: String?, val amount: Int, val createdAt: Date): Log(memberNumber) {
    override fun equals(other: Any?): Boolean {
        if (other is TransferLog) {
            if(this.memberNumber == other.memberNumber && this.accountNumber == other.accountNumber
                    && this.amount == other.amount && this.bank == other.bank && this.outAccountNumber == other.outAccountNumber
                    && this.name == other.name && this.amount == other.amount) {
                return true
            }
        }
        return false
    }
}


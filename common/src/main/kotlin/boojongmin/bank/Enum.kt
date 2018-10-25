package boojongmin.bank

enum class BankEnum {
    BANK1, BANK2, BANK3, BANK4, BANK5
}

// LogStep을 kafka 
enum class LogStep {
    BANK_JOIN, BANK_CREATE_ACCOUNT, BANK_DEPOSIT, BANK_WITHDRAW, BANK_TRANSFER
}


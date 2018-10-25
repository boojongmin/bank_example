package boojongmin.bank.consumer.domain.transaction;

import boojongmin.bank.consumer.domain.Account;
import boojongmin.bank.consumer.domain.Transaction;
import boojongmin.bank.consumer.enums.BankEnum;

public class TransferTransaction extends Transaction {
	public BankEnum bank;
	public String accountNumber;
	public String name;

	public TransferTransaction(Account account, long amount, BankEnum bank, String accountNumber, String name) {
		super(account, amount);
		this.bank = bank;
		this.accountNumber = accountNumber;
		this.name = name;
	}
}

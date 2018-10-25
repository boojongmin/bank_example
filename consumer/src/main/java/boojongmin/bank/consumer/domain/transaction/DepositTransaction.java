package boojongmin.bank.consumer.domain.transaction;

import boojongmin.bank.consumer.domain.Account;
import boojongmin.bank.consumer.domain.Transaction;

public class DepositTransaction extends Transaction {
	public DepositTransaction(Account account, long amount) {
		super(account, amount);
	}
}

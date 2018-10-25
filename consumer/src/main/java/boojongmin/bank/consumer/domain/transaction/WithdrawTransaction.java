package boojongmin.bank.consumer.domain.transaction;

import boojongmin.bank.consumer.domain.Account;
import boojongmin.bank.consumer.domain.Transaction;

public class WithdrawTransaction extends Transaction {
	public WithdrawTransaction(Account account, long amount) {
		super(account, amount);
	}
}

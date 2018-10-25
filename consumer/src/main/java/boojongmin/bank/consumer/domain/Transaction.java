package boojongmin.bank.consumer.domain;

import java.util.Date;

public class Transaction {
	public Account account;
	public long amount;
	public Date createdAt;

	public Transaction(Account account, long amount) {
		this.account = account;
		this.amount = amount;
		this.createdAt = new Date();
	}
}

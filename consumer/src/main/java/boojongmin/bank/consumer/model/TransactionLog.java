package boojongmin.bank.consumer.model;

import boojongmin.bank.consumer.enums.BankEnum;

import java.time.LocalDateTime;

public class TransactionLog {
	public int memberNumber;
	public String accountNumber;
	public BankEnum bank;
	public String outAccountNumber;
	public String name;
	public long amount;
	public LocalDateTime createdAt;
}

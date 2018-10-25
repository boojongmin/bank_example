package boojongmin.bank.consumer.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Member {
	public int number;
	public String name;
	public Date createdAt;
	public List<Account> accounts;

	public Member(int number, String name) {
		this.number = number;
		this.name = name;
		this.accounts = new ArrayList<>();
		this.createdAt = new Date();
	}

	public void createAccount() {
		Account account = new Account(this, UUID.randomUUID().toString());
		this.accounts.add(account);
	}
}

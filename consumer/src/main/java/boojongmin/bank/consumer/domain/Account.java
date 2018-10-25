package boojongmin.bank.consumer.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Account {
	public Member member;
	public String number;
	public Date createdAt;
	public List<? extends Transaction> transactions;

	public Account(Member member, String number) {
		this.member = member;
		this.number = number;
		this.createdAt = new Date();
		this.transactions = new ArrayList<>();
	}
}

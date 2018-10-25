package boojongmin.bank.consumer.model;

import java.util.Date;

public class JoinLog {
	public int number;
	public String name;
	public Date createdAt;

	public JoinLog(int number, String name, Date createdAt) {
		this.number = number;
		this.name = name;
		this.createdAt = new Date();
	}
}

package boojongmin.bank.consumer.domain;

import java.util.HashMap;
import java.util.Map;

public class Bank {
	public Map<Integer, Member> members;

	public Bank() {
		this.members = new HashMap<>();
	}

	public Member createMember(int number, String name) {
		final Member member = new Member(number, name);
		this.members.put(number, member);
		return member;
	}
}

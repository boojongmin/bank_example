package boojongmin.bank.consumer.model;

public class Log<T> {
	public LogStep step;
	private JoinLog joinLog;
	private AccountLog accountLog;
	private TransactionLog transactionLog;

	public enum LogStep {
		JOIN, NEW_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER
	}
}
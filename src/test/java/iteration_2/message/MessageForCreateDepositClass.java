package iteration_2.message;

public class MessageForCreateDepositClass {
	private MessageForCreateDepositClass() {
		throw new IllegalStateException("Utility class");
	}
	public static final String MESSAGE_FOR_SC400_EXCEED5000 = "Deposit amount cannot exceed 5000";
	public static final String MESSAGE_FOR_SC400_LEAST01 = "Deposit amount must be at least 0.01";
	public static final String MESSAGE_FOR_SC403_UNAUTHORIZED = "Unauthorized access to account";
}

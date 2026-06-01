package iteration_2.message;

public class MessageForTransferMoneyClass {

	private MessageForTransferMoneyClass() {
		throw new IllegalStateException("Utility class");
	}
	public static final String MESSAGE_CREATE_TRANSFER_SC200 = "Transfer successful";
	public static final String MESSAGE_CREATE_TRANSFER_SC400_EXCEED10000 = "Transfer amount cannot exceed 10000";
	public static final String MESSAGE_CREATE_TRANSFER_SC400_LEAST001 = "Transfer amount must be at least 0.01";
	public static final String MESSAGE_FOR_UNAUTHORIZED_SX403 = "Unauthorized access to account";
	public static final String MESSAGE_FOR_NOT_HAVE_PERMISSION_SX403 = "You do not have permission to access this account";
}

package iteration_2.api;

public class ApiEndpoints {
	private ApiEndpoints() {
		throw new IllegalStateException("Utility class");
	}
	public static final String GET_TOKEN = "/api/v1/auth/login";
	public static final String CREATE_USERS = "/api/v1/admin/users";
	public static final String CREATE_ACCOUNT = "/api/v1/accounts";
	public static final String CREATE_DEPOSIT = "/api/v1/accounts/deposit";
	public static final String CREATE_TRANSFER = "/api/v1/accounts/transfer";
	public static final String GET_PROFILE = "/api/v1/customer/profile";
}

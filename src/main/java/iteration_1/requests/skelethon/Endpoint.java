package iteration_1.requests.skelethon;

import iteration_1.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
	ADMIN_USERS(
			"/admin/users", CreateUserRequest.class, CreateUserResponse.class
	),
	ACCOUNTS(
			"/accounts", BaseModel.class, CreateAccountResponse.class
	),
	LOGIN_USER(
			"/auth/login", LoginUserRequest.class, LoginUserResponse.class
	);

	private final String url;
	private final Class<? extends BaseModel> requestModel;
	private final Class<? extends BaseModel> responseModel;
}

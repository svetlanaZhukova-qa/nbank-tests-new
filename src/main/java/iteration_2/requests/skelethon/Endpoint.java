package iteration_2.requests.skelethon;

import iteration_2.models_body_JSON.BaseModel;
import iteration_2.models_body_JSON.create_deposit.CreateDepositRequest;
import iteration_2.models_body_JSON.create_deposit.CreateDepositResponse;
import iteration_2.models_body_JSON.create_user_and_accont.*;
import iteration_2.models_body_JSON.transfer_money.CreateTransferRequest;
import iteration_2.models_body_JSON.transfer_money.CreateTransferResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
	ADMIN_USER("api/v1/admin/users",
			CreateUserRequest.class,
			CreateUserResponse.class),
	ACCOUNT("/api/v1/accounts",
			BaseModel.class,
			CreateAccountResponse.class),
	LOGIN_USER("/api/v1/auth/login",
			UserLoginAndGetTokenRequest.class,
			UserLoginAndGetTokenResponse.class
			),

	DEPOSIT("/api/v1/accounts/deposit",
			CreateDepositRequest.class,
			CreateDepositResponse.class),
	TRANSFER(
			"/api/v1/accounts/transfer",
			CreateTransferRequest.class,
			CreateTransferResponse.class);

	private  final String url;
	private final Class<? extends BaseModel> requestModel;
	private final Class<? extends BaseModel> responseModel;
}

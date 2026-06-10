package api.iteration_2.requests.skelethon;

import api.iteration_2.models_body_JSON.BaseModel;
import api.iteration_2.models_body_JSON.change_name_user.InfoGetUserResponse;
import api.iteration_2.models_body_JSON.change_name_user.InfoPutUserRequest;
import api.iteration_2.models_body_JSON.change_name_user.InfoPutUserResponse;
import api.iteration_2.models_body_JSON.create_deposit.CreateDepositRequest;
import api.iteration_2.models_body_JSON.create_deposit.CreateDepositResponse;
import api.iteration_2.models_body_JSON.create_user_and_accont.*;
import api.iteration_2.models_body_JSON.transfer_money.CreateTransferRequest;
import api.iteration_2.models_body_JSON.transfer_money.CreateTransferResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
	ADMIN_USER("/admin/users",
			CreateUserRequest.class,
			CreateUserResponse.class),
	ACCOUNT("/accounts",
			BaseModel.class,
			CreateAccountResponse.class),
	LOGIN_USER("/auth/login",
			UserLoginAndGetTokenRequest.class,
			UserLoginAndGetTokenResponse.class
			),

	DEPOSIT("/accounts/deposit",
			CreateDepositRequest.class,
			CreateDepositResponse.class),
	TRANSFER(
			"/accounts/transfer",
			CreateTransferRequest.class,
			CreateTransferResponse.class),
	LOOK_TRANSFER(
			"/accounts/{id}/transactions",
			BaseModel.class,
			BaseModel.class),
	USER_INFO(
			"/customer/profile",
			BaseModel.class,
			InfoGetUserResponse.class),
	USER_UPDATE(
			"/customer/profile",
			InfoPutUserRequest.class,
			InfoPutUserResponse.class
	),
	CUSTOMER_ACCOUNTS("/customer/accounts",
			BaseModel.class,
			CreateAccountResponse.class);

	private  final String url;
	private final Class<? extends BaseModel> requestModel;
	private final Class<? extends BaseModel> responseModel;
}

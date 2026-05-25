package iteration_2.requests.steps;

import iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.models_body_JSON.transfer_money.CreateTransferRequest;
import iteration_2.models_body_JSON.transfer_money.CreateTransferResponse;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.ValidateCrudRequester2;
import iteration_2.specs.RequestSpecs;
import iteration_2.specs.ResponseSpecs;

public class UserCreateTransfer {
	// 	// переводим деньги с одного счета на другой
	//		CreateTransferRequest createTransferRequest = CreateTransferRequest.builder().senderAccountId(idAccount1)
	//				.receiverAccountId(idAccount2).amount(sum).build();
	//		CreateTransferResponse createTransferResponse = new ValidateCrudRequester2<CreateTransferResponse>(
	//				RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
	//				ResponseSpecs.requestReturnOk(),
	//				Endpoint.TRANSFER
	//		).post(createTransferRequest);
	public static CreateTransferResponse createTransfer(CreateUserRequest createUserRequest, CreateAccountResponse createAccountResponse1, CreateAccountResponse createAccountResponse2, int sum){
				CreateTransferRequest createTransferRequest = CreateTransferRequest.builder().senderAccountId(createAccountResponse1.getId())
						.receiverAccountId(createAccountResponse2.getId()).amount(sum).build();
				CreateTransferResponse createTransferResponse = new ValidateCrudRequester2<CreateTransferResponse>(
						RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
						ResponseSpecs.requestReturnOk(),
						Endpoint.TRANSFER
				).post(createTransferRequest);
				return createTransferResponse;
	}
}

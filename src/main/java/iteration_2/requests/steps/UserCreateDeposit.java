package iteration_2.requests.steps;

import iteration_2.models_body_JSON.create_deposit.CreateDepositRequest;
import iteration_2.models_body_JSON.create_deposit.CreateDepositResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.ValidateCrudRequester2;
import iteration_2.specs.RequestSpecs;
import iteration_2.specs.ResponseSpecs;


public class UserCreateDeposit {

	public static DepositPair createDeposit(CreateUserRequest createUserRequest, CreateAccountResponse createAccountResponse, int deposit){
		// Создаем объект запроса
		CreateDepositRequest createDepositRequest = CreateDepositRequest.builder()
				.id(createAccountResponse.getId())
				.balance(deposit)
				.build();

		// Отправляем запрос и получаем ответ
		CreateDepositResponse createDepositResponse = new ValidateCrudRequester2<CreateDepositResponse>(
				RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk(),
				Endpoint.DEPOSIT
		).post(createDepositRequest);

		// Возвращаем пару объектов
		return new DepositPair(createDepositRequest, createDepositResponse);
	}

	public static class DepositPair {
		private final CreateDepositRequest request;
		private final CreateDepositResponse response;

		public DepositPair(CreateDepositRequest request, CreateDepositResponse response) {
			this.request = request;
			this.response = response;
		}

		public CreateDepositRequest getRequest() {
			return request;
		}

		public CreateDepositResponse getResponse() {
			return response;
		}
	}
}


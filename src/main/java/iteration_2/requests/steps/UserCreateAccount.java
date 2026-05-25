package iteration_2.requests.steps;

import iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.ValidateCrudRequester2;
import iteration_2.specs.RequestSpecs;
import iteration_2.specs.ResponseSpecs;

public class UserCreateAccount {
	// 		/создаем счет у второго пользователя
	//CreateAccountResponse createAccountResponse2 = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserRequest2.getUsername(), createUserRequest2.getPassword()),
	//		ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);
	//		int idAccountUser2 = createAccountResponse2.getId();
	public static CreateAccountResponse userCreateAccount(CreateUserRequest createUserRequest){
CreateAccountResponse createAccountResponse = new ValidateCrudRequester2<CreateAccountResponse>
		(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword())
		, ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);
return createAccountResponse;
	}

	// public class AdminSteps {
	//	public static CreateUserRequest createUser(){
	//		CreateUserRequest createUserRequest = RandomModelGenerator2Iteration.generate(CreateUserRequest.class);
	//
	//		new ValidateCrudRequester2<CreateUserRequest>(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(),
	//				Endpoint.ADMIN_USER).post(createUserRequest);
	//		return createUserRequest;
	//	}
	//}
}

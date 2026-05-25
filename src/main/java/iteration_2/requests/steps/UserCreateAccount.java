package iteration_2.requests.steps;

import iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.ValidateCrudRequester2;
import iteration_2.specs.RequestSpecs;
import iteration_2.specs.ResponseSpecs;

public class UserCreateAccount {

	public static CreateAccountResponse userCreateAccount(CreateUserRequest createUserRequest){
CreateAccountResponse createAccountResponse = new ValidateCrudRequester2<CreateAccountResponse>
		(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword())
		, ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);
return createAccountResponse;
	}

}

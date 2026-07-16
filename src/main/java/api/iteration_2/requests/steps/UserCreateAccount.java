package api.iteration_2.requests.steps;

import api.iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import api.iteration_2.requests.skelethon.Endpoint;
import api.iteration_2.requests.skelethon.requesters.ValidateCrudRequester2;
import api.iteration_2.specs.RequestSpecs;
import api.iteration_2.specs.ResponseSpecs;
import common.helpers.StepLogger;

public class UserCreateAccount {

	public static CreateAccountResponse userCreateAccount(CreateUserRequest createUserRequest){
		return StepLogger.log("User create account", () -> {CreateAccountResponse createAccountResponse = new ValidateCrudRequester2<CreateAccountResponse>
				(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword())
						, ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);
			return createAccountResponse;});

	}

}

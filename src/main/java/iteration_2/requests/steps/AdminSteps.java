package iteration_2.requests.steps;

import iteration_2.generators.RandomModelGenerator2Iteration;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserResponse;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.ValidateCrudRequester2;
import iteration_2.specs.RequestSpecs;
import iteration_2.specs.ResponseSpecs;

public class AdminSteps {
	public static CreateUserRequest createUser(){
		CreateUserRequest createUserRequest = RandomModelGenerator2Iteration.generate(CreateUserRequest.class);

		new ValidateCrudRequester2<CreateUserRequest>(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(),
				Endpoint.ADMIN_USER).post(createUserRequest);
		return createUserRequest;
	}
}

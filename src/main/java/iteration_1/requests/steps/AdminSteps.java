package iteration_1.requests.steps;

import iteration_1.generators.RandomModelGenerator;
import iteration_1.models.CreateUserRequest;
import iteration_1.models.CreateUserResponse;
import iteration_1.requests.skelethon.Endpoint;
import iteration_1.requests.skelethon.requestsers.ValidateCRUDRequester;
import iteration_1.specs.RequestSpecs;
import iteration_1.specs.ResponseSpecs;

public class AdminSteps {
	public static CreateUserRequest createUser(){
		CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

		 new ValidateCRUDRequester<CreateUserRequest>(RequestSpecs.adminSpec(),
				ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USERS)
				.post(createUserRequest);

		 return createUserRequest;
	}
}

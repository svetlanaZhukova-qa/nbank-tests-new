package api.iteration_1.requests.steps;

import api.iteration_1.generators.RandomModelGenerator;
import api.iteration_1.models.CreateUserRequest;
import api.iteration_1.requests.skelethon.Endpoint;
import api.iteration_1.requests.skelethon.requestsers.ValidateCRUDRequester;
import api.iteration_1.specs.RequestSpecs;
import api.iteration_1.specs.ResponseSpecs;

public class AdminSteps {
	public static CreateUserRequest createUser(){
		CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

		 new ValidateCRUDRequester<CreateUserRequest>(RequestSpecs.adminSpec(),
				ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USERS)
				.post(createUserRequest);

		 return createUserRequest;
	}
}

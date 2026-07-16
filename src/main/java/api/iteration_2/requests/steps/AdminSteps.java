package api.iteration_2.requests.steps;

import api.iteration_2.generators.RandomModelGenerator2Iteration;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserResponse;
import api.iteration_2.requests.skelethon.Endpoint;
import api.iteration_2.requests.skelethon.requesters.ValidateCrudRequester2;
import api.iteration_2.specs.RequestSpecs;
import api.iteration_2.specs.ResponseSpecs;
import common.helpers.StepLogger;

import java.util.List;

public class AdminSteps {
	public static CreateUserRequest createUser(){
	return 	StepLogger.log("Create user ", () -> {	CreateUserRequest createUserRequest = RandomModelGenerator2Iteration.generate(CreateUserRequest.class);

			new ValidateCrudRequester2<CreateUserRequest>(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(),
					Endpoint.ADMIN_USER).post(createUserRequest);
			return createUserRequest;});

	}

	public static List<CreateUserResponse> getAllUsers(){
		return StepLogger.log("Get all users: ", () -> {return new ValidateCrudRequester2<CreateUserResponse>(
				RequestSpecs.adminSpec(),
				ResponseSpecs.requestReturnOk(),
				Endpoint.ADMIN_USER
		).getAll(CreateUserResponse[].class);});

	}
}

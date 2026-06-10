package api.iteration_2.requests.steps;

import api.iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import api.iteration_2.requests.skelethon.Endpoint;
import api.iteration_2.requests.skelethon.requesters.ValidateCrudRequester2;
import api.iteration_2.specs.RequestSpecs;
import api.iteration_2.specs.ResponseSpecs;

import java.util.List;

public class UserSteps {
	private String username;
	private String password;

	public UserSteps(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public  List<CreateAccountResponse> getAllAccounts(){
return new ValidateCrudRequester2<CreateAccountResponse>(
		RequestSpecs.authUserSpec(username, password),
		ResponseSpecs.requestReturnOk(), Endpoint.CUSTOMER_ACCOUNTS).getAll(CreateAccountResponse[].class);
	}
}

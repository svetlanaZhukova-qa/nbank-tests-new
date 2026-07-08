package iteration_1.api;

import api.iteration_1.models.CreateUserRequest;
import api.iteration_1.models.LoginUserRequest;
import api.iteration_1.models.LoginUserResponse;
import api.iteration_1.requests.skelethon.Endpoint;
import api.iteration_1.requests.skelethon.requestsers.CrudRequester;
import api.iteration_1.requests.skelethon.requestsers.ValidateCRUDRequester;
import api.iteration_1.requests.steps.AdminSteps;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import api.iteration_1.specs.RequestSpecs;
import api.iteration_1.specs.ResponseSpecs;

public class LoginUserTestApi extends BaseTest {

	@Test
	public void adminCanGenerateAuthTokenTest() {
		LoginUserRequest userRequest = LoginUserRequest.builder()
				.username("admin")
				.password("admin")
				.build();

		new ValidateCRUDRequester<LoginUserResponse>(RequestSpecs.unauthSpec(),
				ResponseSpecs.requestReturnsOK(), Endpoint.LOGIN_USER)
				.post(userRequest);
	}

	@Test
	public void userCanGenerateAuthTokenTest() {

		CreateUserRequest userRequest = AdminSteps.createUser();

		new CrudRequester(RequestSpecs.unauthSpec(),
				ResponseSpecs.requestReturnsOK(),Endpoint.LOGIN_USER)
				.post(LoginUserRequest.builder().username(userRequest.getUsername()).password(userRequest.getPassword()).build())
				.header("Authorization", Matchers.notNullValue());
	}
}
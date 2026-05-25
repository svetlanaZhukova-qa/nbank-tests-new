package iteration_1;

import iteration_1.generators.RandomData;
import iteration_1.generators.RandomModelGenerator;
import iteration_1.models.*;
import iteration_1.requests.skelethon.Endpoint;
import iteration_1.requests.skelethon.requestsers.CrudRequester;
import iteration_1.requests.skelethon.requestsers.ValidateCRUDRequester;
import iteration_1.requests.steps.AdminSteps;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import iteration_1.specs.RequestSpecs;
import iteration_1.specs.ResponseSpecs;

public class LoginUserTest extends BaseTest {

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
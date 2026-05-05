package iteration_1;

import iteration_1.generators.RandomData;
import iteration_1.models.CreateUserRequest;
import iteration_1.models.LoginUserRequest;
import iteration_1.models.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import iteration_1.requests.AdminCreateUserRequester;
import iteration_1.requests.LoginUserRequester;
import iteration_1.specs.RequestSpecs;
import iteration_1.specs.ResponseSpecs;

public class LoginUserTest extends BaseTest {

	@Test
	public void adminCanGenerateAuthTokenTest() {
		LoginUserRequest userRequest = LoginUserRequest.builder()
				.username("admin")
				.password("admin")
				.build();

		new LoginUserRequester(RequestSpecs.unauthSpec(),
				ResponseSpecs.requestReturnsOK())
				.post(userRequest);
	}

	@Test
	public void userCanGenerateAuthTokenTest() {
		CreateUserRequest userRequest = CreateUserRequest.builder()
				.username(RandomData.getUsername())
				.password(RandomData.getPassword())
				.role(UserRole.USER.toString())
				.build();

		new AdminCreateUserRequester(
				RequestSpecs.adminSpec(),
				ResponseSpecs.entityWasCreated())
				.post(userRequest);

		new LoginUserRequester(RequestSpecs.unauthSpec(),
				ResponseSpecs.requestReturnsOK())
				.post(LoginUserRequest.builder().username(userRequest.getUsername()).password(userRequest.getPassword()).build())
				.header("Authorization", Matchers.notNullValue());
	}
}
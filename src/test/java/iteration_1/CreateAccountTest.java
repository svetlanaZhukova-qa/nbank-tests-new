package iteration_1;

import iteration_1.generators.RandomData;
import iteration_1.BaseTest;
import iteration_1.models.CreateUserRequest;
import iteration_1.models.UserRole;
import org.junit.jupiter.api.Test;
import iteration_1.requests.AdminCreateUserRequester;
import iteration_1.requests.CreateAccountRequester;
import iteration_1.specs.RequestSpecs;
import iteration_1.specs.ResponseSpecs;

public class CreateAccountTest extends BaseTest {

	@Test
	public void userCanCreateAccountTest() {
		// создаем пользователя
		CreateUserRequest userRequest = CreateUserRequest.builder()
				.username(RandomData.getUsername())
				.password(RandomData.getPassword())
				.role(UserRole.USER.toString())
				.build();

		//
		new AdminCreateUserRequester(
				RequestSpecs.adminSpec(),
				ResponseSpecs.entityWasCreated())
				.post(userRequest);

		new CreateAccountRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
				ResponseSpecs.entityWasCreated())
				.post(null);

		// запросить все аккаунты пользователя и проверить, что наш аккаунт там

	}
}
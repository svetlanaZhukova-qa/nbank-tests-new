package iteration_1.api;

import iteration_1.models.CreateUserRequest;
import iteration_1.requests.skelethon.Endpoint;
import iteration_1.requests.skelethon.requestsers.CrudRequester;
import iteration_1.requests.steps.AdminSteps;
import org.junit.jupiter.api.Test;
import iteration_1.specs.RequestSpecs;
import iteration_1.specs.ResponseSpecs;

public class CreateAccountTest extends BaseTest {

	@Test
	public void userCanCreateAccountTest() {
		// создаем пользователя
		CreateUserRequest userRequest = AdminSteps.createUser();

		new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()), ResponseSpecs.entityWasCreated(),
				Endpoint.ACCOUNTS);


	}
}
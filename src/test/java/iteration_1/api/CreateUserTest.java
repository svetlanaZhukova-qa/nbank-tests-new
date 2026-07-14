package iteration_1.api;


import api.dao.UserDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.iteration_1.generators.RandomModelGenerator;
import api.iteration_1.models.CreateUserRequest;
import api.iteration_1.models.CreateUserResponse;
import api.iteration_1.models.comparison.ModelAssertions;
import api.iteration_1.requests.skelethon.Endpoint;
import api.iteration_1.requests.skelethon.requestsers.CrudRequester;
import api.iteration_1.requests.skelethon.requestsers.ValidateCRUDRequester;
import api.iteration_1.requests.steps.DataBaseSteps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.iteration_1.specs.RequestSpecs;
import api.iteration_1.specs.ResponseSpecs;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNull;

public class CreateUserTest extends BaseTest {
	@Test
	public void adminCanCreateUserWithCorrectData() {
		// Подготовка данных
		CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

		//POST-запрос
		CreateUserResponse createUserResponse = new ValidateCRUDRequester<CreateUserResponse>(RequestSpecs.adminSpec(),
				ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USERS)
				.post(createUserRequest);

		// GET-запрос для проверки созданного пользователя

		ModelAssertions.assertThatModels(createUserRequest, createUserResponse).match();
		softly.assertThat(createUserRequest.getPassword()).isNotEqualTo(createUserResponse.getPassword());

		UserDao userDao = DataBaseSteps.getUserByUsername(createUserRequest.getUsername());
		DaoAndModelAssertions.assertThat(createUserResponse, userDao).match();

	}

	public static Stream<Arguments> userInvalidData() {
		return Stream.of(
				// username field validation
				Arguments.of("   ", "Password33$", "USER", "username", "Username cannot be blank"),
				Arguments.of("ab", "Password33$", "USER", "username", "Username must be between 3 and 15 characters"),
				Arguments.of("abc$", "Password33$", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots"),
				Arguments.of("abc%", "Password33$", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots")
		);

	}

	@MethodSource("userInvalidData")
	@ParameterizedTest
	public void adminCanNotCreateUserWithInvalidData(String username, String password, String role, String errorKey, String errorValue) {
		CreateUserRequest createUserRequest = CreateUserRequest.builder()
				.username(username)
				.password(password)
				.role(role)
				.build();

		new CrudRequester(RequestSpecs.adminSpec(),
				ResponseSpecs.requestReturnsBadRequest(errorKey, errorValue), Endpoint.ADMIN_USERS)
				.post(createUserRequest);


		assertNull(DataBaseSteps.getUserByUsername(createUserRequest.getUsername()));

	}
}

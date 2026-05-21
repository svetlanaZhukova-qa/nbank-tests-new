package iteration_1;

import iteration_1.generators.RandomData;
import iteration_1.generators.RandomModelGenerator;
import iteration_1.models.CreateUserRequest;
import iteration_1.models.CreateUserResponse;
import iteration_1.models.UserRole;
import iteration_1.models.comparison.ModelAssertions;
import iteration_1.requests.skelethon.Endpoint;
import iteration_1.requests.skelethon.requestsers.CrudRequester;
import iteration_1.requests.skelethon.requestsers.ValidateCRUDRequester;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import iteration_1.specs.RequestSpecs;
import iteration_1.specs.ResponseSpecs;

import java.util.stream.Stream;

public class CreateUserTest extends BaseTest {
	@Test
	public void adminCanCreateUserWithCorrectData() {
		CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

		CreateUserResponse createUserResponse = new ValidateCRUDRequester<CreateUserResponse>(RequestSpecs.adminSpec(),
				ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USERS)
				.post(createUserRequest);

		ModelAssertions.assertThatModels(createUserRequest, createUserResponse).match();
		softly.assertThat(createUserRequest.getPassword()).isNotEqualTo(createUserResponse.getPassword());

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
	}
}

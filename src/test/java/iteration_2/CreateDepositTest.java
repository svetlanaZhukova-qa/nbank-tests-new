package iteration_2;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import iteration_1.requests.CreateAccountRequester;
import iteration_2.generators.RandomData;
import iteration_2.models_body_JSON.*;
import iteration_2.requests.AdminCreateUserRequester;
import iteration_2.requests.UserCreateAccountRequester;
import iteration_2.requests.UserCreateDepositRequester;
import iteration_2.requests.UserLoginRequester;
import iteration_2.specs.RequestSpecs;
import iteration_2.specs.ResponseSpecs;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
// Депозит денег пользователем
// Депозит (Deposit):
//— Максимальная сумма: 5000
//— Сумма должна быть положительной
//— Нельзя делать депозит в чужой аккаунт или несуществующий
@DisplayName("Тесты на проверку возможности создания Депозита")
public class CreateDepositTest extends BaseTest{


	@ParameterizedTest
	@Tag("positive")
	@DisplayName("Пользователь может создать депозит с суммой не более 5000 за раз и больше 0")
	@ValueSource(ints = {4999,5000})
	public void userCanCreateDepositWithValidSum(int deposit){
		// создаем пользователя
		CreateUserRequest createUserRequest = CreateUserRequest.builder()
						.username(RandomData.getRandomUserName())
						.password(RandomData.getRandomPassword())
						.role(UserRole.USER.toString()).build();

		CreateUserResponse createUserResponse = new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest)
				.extract().as(CreateUserResponse.class);

		// создаем счет
	 CreateAccountResponse createAccountResponse = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserResponse.getUsername(), createUserRequest.getPassword()),
			 ResponseSpecs.entityWasCreated())
				.postApi(null)
				.extract().as(CreateAccountResponse.class);

	int idAccount = createAccountResponse.getId();

		// переводим депозит на счет
		CreateDepositRequest createDepositRequest = CreateDepositRequest.builder().id(idAccount)
				.balance(deposit).build();

		CreateDepositResponse createDepositResponse = new UserCreateDepositRequester(RequestSpecs.authUserSpec(createUserResponse.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk())
				.postApi(createDepositRequest)
				.extract().as(CreateDepositResponse.class);

		softly.assertThat(createDepositRequest.getId()).isEqualTo(createDepositResponse.getId());
		softly.assertThat(createDepositRequest.getBalance()).isEqualTo((int)createDepositResponse.getBalance());

	}


	public static Stream<Arguments> notValidSum(){
		return Stream.of(
				Arguments.of(5001, "Deposit amount cannot exceed 5000"),
				Arguments.of(-1, "Deposit amount must be at least 0.01"),
				Arguments.of(0, "Deposit amount must be at least 0.01")
		);
	}

	@ParameterizedTest
	@Tag("negative")
	@DisplayName("Пользователь не может создать депозит с суммой более 5000 за раз и меньше 0")
	@MethodSource("notValidSum")
	public void userCantCreateDepositWithNotValidSum(int deposit, String error){
		// создаем пользователя
		CreateUserRequest createUserRequest = CreateUserRequest.builder()
				.username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();

		CreateUserResponse createUserResponse = new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest)
				.extract().as(CreateUserResponse.class);

		// создаем счет
		CreateAccountResponse createAccountResponse = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserResponse.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.entityWasCreated())
				.postApi(null)
				.extract().as(CreateAccountResponse.class);

		int idAccount = createAccountResponse.getId();

		// переводим счет на депозит
		CreateDepositRequest createDepositRequest = CreateDepositRequest.builder().id(idAccount)
				.balance(deposit).build();

		String errorMessage = new UserCreateDepositRequester(RequestSpecs.authUserSpecForAcceptTEXT(createUserResponse.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnBadRequest())
				.postApi(createDepositRequest)
				.extract().body().asString();

		softly.assertThat(errorMessage).isEqualTo(error);
	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может переводить деньги на не существующий счет")
	public void userCantCreateDepositOnNonExistAccount(){
		// создаем пользователя
		CreateUserRequest createUserRequest = CreateUserRequest.builder()
				.username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();

		new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest)
				.extract().as(CreateUserResponse.class);

		// переводим депозит
		CreateDepositRequest createDepositRequest = CreateDepositRequest.builder().balance(500).id(10).build();
		 String errorMessage = new UserCreateDepositRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				 ResponseSpecs.requestReturnForbidden())
				 .postApi(createDepositRequest)
				 .extract().body().asString();

		 softly.assertThat(errorMessage).isEqualTo("Unauthorized access to account");

	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может переводить деньги на чужой счет")
	public void userCantCreateDepositOnAnotherAccount(){
		// создаем первого пользователя
		CreateUserRequest createUserRequest1 = CreateUserRequest.builder()
				.username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();

		CreateUserResponse createUserResponse1 = new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest1)
				.extract().as(CreateUserResponse.class);

		// создаем второго пользователя
		CreateUserRequest createUserRequest2 = CreateUserRequest.builder()
				.username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();

		CreateUserResponse createUserResponse2 = new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest2)
				.extract().as(CreateUserResponse.class);

		// создаем счет у второго пользователя
CreateAccountResponse createAccountResponse2 = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserResponse2.getUsername(), createUserRequest2.getPassword()),
		ResponseSpecs.entityWasCreated())
		.postApi(null).extract().as(CreateAccountResponse.class);
		int idAccountUser2 = createAccountResponse2.getId();

		// переводим депозит под токеном первого пользователя на второй
		CreateDepositRequest createDepositRequest = CreateDepositRequest.builder().balance(500).id(idAccountUser2).build();
		String messageError = new UserCreateDepositRequester(RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()), ResponseSpecs.requestReturnForbidden())
				.postApi(createDepositRequest)
				.extract().body().asString();
		softly.assertThat(messageError).isEqualTo("Unauthorized access to account");


	}





}

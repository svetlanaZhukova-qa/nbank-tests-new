package iteration_2;

import iteration_2.data.Account;
import iteration_2.generators.RandomData;
import iteration_2.models_body_JSON.*;
import iteration_2.models_body_JSON.change_name_user.InfoGetUserResponse;
import iteration_2.models_body_JSON.create_deposit.CreateDepositRequest;
import iteration_2.models_body_JSON.create_deposit.CreateDepositResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserResponse;

import iteration_2.requests.UserCreateDepositRequester;
import iteration_2.requests.UserGetInformationRequester;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.CrudRequester;
import iteration_2.requests.skelethon.requesters.ValidateCrudRequester2;
import iteration_2.specs.RequestSpecs;
import iteration_2.specs.ResponseSpecs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
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
		// создаем пользователя и извлекаем токен
		CreateUserRequest createUserRequest = CreateUserRequest.builder()
						.username(RandomData.getRandomUserName())
						.password(RandomData.getRandomPassword())
						.role(UserRole.USER.toString()).build();


		CreateUserResponse createUserResponse = new ValidateCrudRequester2<CreateUserResponse>(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(),
				Endpoint.ADMIN_USER).post(createUserRequest);


		// создаем счет
		CreateAccountResponse createAccountResponse = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserResponse.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);

	int idAccount = createAccountResponse.getId();

		// переводим депозит на счет
		CreateDepositRequest createDepositRequest = CreateDepositRequest.builder().id(idAccount)
				.balance(deposit).build();

		CreateDepositResponse createDepositResponse = new ValidateCrudRequester2<CreateDepositResponse>(
				RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.requestReturnOk(),
				Endpoint.DEPOSIT
		).post(createDepositRequest);


		softly.assertThat(createDepositRequest.getId()).isEqualTo(createDepositResponse.getId());
		softly.assertThat(createDepositRequest.getBalance()).isEqualTo((int)createDepositResponse.getBalance());

		// запрашиваем информацию профиля
		InfoGetUserResponse infoGetUserResponse = new UserGetInformationRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk())
				.getApi().extract().as(InfoGetUserResponse.class);

		softly.assertThat(infoGetUserResponse.getUsername()).isEqualTo(createUserRequest.getUsername());

	List<Account> accounts = new UserGetInformationRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk())
			 .getApi().extract().jsonPath().getList("accounts", Account.class);

		Optional<Account> account = accounts.stream().filter(a -> a.getId() == idAccount).findFirst();
		softly.assertThat(account.get().getBalance()).isEqualTo(deposit);
		softly.assertThat(account.get().getId()).isEqualTo(idAccount);

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
		// создаем пользователя и извлекаем токен
		CreateUserRequest createUserRequest = CreateUserRequest.builder()
				.username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();


		CreateUserResponse createUserResponse = new ValidateCrudRequester2<CreateUserResponse>(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(),
				Endpoint.ADMIN_USER).post(createUserRequest);

		// создаем счет
		CreateAccountResponse createAccountResponse = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserResponse.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);

		int idAccount = createAccountResponse.getId();

		// переводим счет на депозит
		CreateDepositRequest createDepositRequest = CreateDepositRequest.builder().id(idAccount)
				.balance(deposit).build();

		String errorMessage = new CrudRequester(RequestSpecs.authUserSpecForAcceptTEXT(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnBadRequest(),
				Endpoint.DEPOSIT).post(createDepositRequest).extract().body().asString();

		softly.assertThat(errorMessage).isEqualTo(error);
	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может переводить деньги на не существующий счет")
	public void userCantCreateDepositOnNonExistAccount(){
		// создаем пользователя и извлекаем токен
		CreateUserRequest createUserRequest = CreateUserRequest.builder()
				.username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();


		new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USER).post(createUserRequest);



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
		// создаем первого пользователя и извлекаем токен
		CreateUserRequest createUserRequest1 = CreateUserRequest.builder()
				.username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();

		CreateUserResponse createUserResponse1 = new ValidateCrudRequester2<CreateUserResponse>(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(),
				Endpoint.ADMIN_USER).post(createUserRequest1);

		// создаем второго пользователя и извлекаем токен
		CreateUserRequest createUserRequest2 = CreateUserRequest.builder()
				.username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();

		CreateUserResponse createUserResponse2 =  new ValidateCrudRequester2<CreateUserResponse>(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(),
				Endpoint.ADMIN_USER).post(createUserRequest2);

		// создаем счет у второго пользователя
CreateAccountResponse createAccountResponse2 = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserResponse2.getUsername(), createUserRequest2.getPassword()),
		ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);
		int idAccountUser2 = createAccountResponse2.getId();

		// переводим депозит под токеном первого пользователя на второй
		CreateDepositRequest createDepositRequest = CreateDepositRequest.builder().balance(500).id(idAccountUser2).build();
		String messageError = new UserCreateDepositRequester(RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()), ResponseSpecs.requestReturnForbidden())
				.postApi(createDepositRequest)
				.extract().body().asString();
		softly.assertThat(messageError).isEqualTo("Unauthorized access to account");


	}





}

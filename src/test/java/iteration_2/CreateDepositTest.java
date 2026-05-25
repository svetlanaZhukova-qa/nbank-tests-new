package iteration_2;

import iteration_2.data.Account;
import iteration_2.models_body_JSON.change_name_user.InfoGetUserResponse;
import iteration_2.models_body_JSON.create_deposit.CreateDepositRequest;
import iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.CrudRequester;
import iteration_2.requests.steps.AdminSteps;
import iteration_2.requests.steps.GetUserInfo;
import iteration_2.requests.steps.UserCreateAccount;
import iteration_2.requests.steps.UserCreateDeposit;
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
		CreateUserRequest createUserRequest = AdminSteps.createUser();

		// создаем счет
		CreateAccountResponse createAccountResponse =  UserCreateAccount.userCreateAccount(createUserRequest);
	int idAccount = createAccountResponse.getId();

		// переводим депозит на счет
		UserCreateDeposit.DepositPair pair = UserCreateDeposit.createDeposit(createUserRequest, createAccountResponse, deposit);

		softly.assertThat(pair.getRequest().getId()).isEqualTo(pair.getResponse().getId());
		softly.assertThat(pair.getRequest().getBalance()).isEqualTo((int) pair.getResponse().getBalance());

		// запрашиваем информацию профиля
		InfoGetUserResponse infoGetUserResponse = GetUserInfo.getInfo(createUserRequest);

		softly.assertThat(infoGetUserResponse.getUsername()).isEqualTo(createUserRequest.getUsername());

		List<Account> accounts = new CrudRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
			ResponseSpecs.requestReturnOk(),
			Endpoint.USER_INFO).get().extract().jsonPath().getList("accounts", Account.class);;

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
		CreateUserRequest createUserRequest = AdminSteps.createUser();

		// создаем счет
		CreateAccountResponse createAccountResponse = UserCreateAccount.userCreateAccount(createUserRequest);
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
		CreateUserRequest createUserRequest = AdminSteps.createUser();


		// переводим депозит
		CreateDepositRequest createDepositRequest = CreateDepositRequest.builder().balance(500).id(10).build();
		 String errorMessage = new CrudRequester(RequestSpecs.authUserSpecForAcceptTEXT(createUserRequest.getUsername(), createUserRequest.getPassword()),
				 ResponseSpecs.requestReturnForbidden(),
				 Endpoint.DEPOSIT).post(createDepositRequest).extract().body().asString();
//
		 softly.assertThat(errorMessage).isEqualTo("Unauthorized access to account");

	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может переводить деньги на чужой счет")
	public void userCantCreateDepositOnAnotherAccount(){
		// создаем первого пользователя и извлекаем токен
		CreateUserRequest createUserRequest1 = AdminSteps.createUser();

		// создаем второго пользователя и извлекаем токен
		CreateUserRequest createUserRequest2 = AdminSteps.createUser();

		// создаем счет у второго пользователя
       CreateAccountResponse createAccountResponse2 = UserCreateAccount.userCreateAccount(createUserRequest2);

		int idAccountUser2 = createAccountResponse2.getId();

		// переводим депозит под токеном первого пользователя на второй
		CreateDepositRequest createDepositRequest = CreateDepositRequest.builder().balance(500).id(idAccountUser2).build();
		String messageError = new CrudRequester(RequestSpecs.authUserSpec(createUserRequest1.getUsername(),createUserRequest1.getPassword()), ResponseSpecs.requestReturnForbidden(),
				Endpoint.DEPOSIT).post(createDepositRequest).extract().body().asString();

		softly.assertThat(messageError).isEqualTo("Unauthorized access to account");


	}





}

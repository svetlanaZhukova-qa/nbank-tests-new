package iteration_2;


import iteration_1.models.comparison.ModelAssertions;
import iteration_2.data.Account;
import iteration_2.data.Transaction;
import iteration_2.generators.RandomData;
import iteration_2.generators.RandomModelGenerator2Iteration;
import iteration_2.models_body_JSON.change_name_user.InfoGetUserResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserResponse;
import iteration_2.models_body_JSON.transfer_money.CreateTransferRequest;
import iteration_2.models_body_JSON.transfer_money.CreateTransferResponse;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.CrudRequester;
import iteration_2.requests.skelethon.requesters.ValidateCrudRequester2;
import iteration_2.requests.steps.*;
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
import java.util.stream.Stream;

// Перевод денег с одного аккаунта на другой
// — Максимальная сумма: 10000
// — Сумма должна быть положительной и не превышать баланс отправителя
// — Можно переводить между своими аккаунтами и чужими

@DisplayName("Тесты на проверку возможности перевода денег с одного счета на другой")
public class TransferMoneyTest extends BaseTest {

	@ParameterizedTest
	@ValueSource(ints = {9999, 10000})
	@Tag("positive")
	@DisplayName("Пользователь может переводить деньги с одного счета на другой. Максимальная сумма 10000")
	public void UserCanTransferMoneyFromOneAccountToAnother(int sum){
		// создаем пользователя и извлекаем токен
		CreateUserRequest createUserRequest = RandomModelGenerator2Iteration.generate(CreateUserRequest.class);

		CreateUserResponse createUserResponse = new ValidateCrudRequester2<CreateUserResponse>(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(),
				Endpoint.ADMIN_USER).post(createUserRequest);

		// создаем 2 счета
		//1-ый счет
		CreateAccountResponse createAccountResponse1 =  UserCreateAccount.userCreateAccount(createUserRequest);
		int idAccount1 = createAccountResponse1.getId();

		// 2-ой счет
		CreateAccountResponse createAccountResponse2 = UserCreateAccount.userCreateAccount(createUserRequest);
		int idAccount2 = createAccountResponse2.getId();

		// пополняем первый счет на 10 000
		// 1-ый раз на 5 000
		UserCreateDeposit.createDeposit(createUserRequest, createAccountResponse1, getMaxDeposit());

		// 2-ой раз на 5 000
	UserCreateDeposit.createDeposit(createUserRequest, createAccountResponse1, getMaxDeposit());

		// переводим деньги с одного счета на другой
		CreateTransferResponse createTransferResponse = UserCreateTransfer.createTransfer(createUserRequest, createAccountResponse1, createAccountResponse2, sum);

		softly.assertThat(createTransferResponse.getReceiverAccountId()).isEqualTo(idAccount2);
		softly.assertThat(createTransferResponse.getSenderAccountId()).isEqualTo(idAccount1);
		softly.assertThat(createTransferResponse.getAmount()).isEqualTo((double)sum);
		softly.assertThat(createTransferResponse.getMessage()).isEqualTo(ResponseSpecs.MESSAGE_TRANSFER_SUCCESSFUL);

		// запрашиваем информацию профиля
		InfoGetUserResponse infoGetUserResponse = GetUserInfo.getInfo(createUserRequest);
		ModelAssertions.assertThatModels(infoGetUserResponse,createUserRequest).match();
		ModelAssertions.assertThatModels(infoGetUserResponse,createUserResponse).match();

//List<Account> accounts = infoGetUserResponse.getAccounts();
//		Optional<Account> account1 = accounts.stream().filter(a -> a.getId() == idAccount1).findFirst();
//		softly.assertThat(account1.get().getAccountNumber()).isEqualTo(createAccountResponse1.getAccountNumber());
//		softly.assertThat(account1.get().getBalance()).isEqualTo(10000 - sum);
//		softly.assertThat(account1.get().getTransactions().size()).isEqualTo(3);
//
//		Optional<Account> account2 = accounts.stream().filter(a -> a.getId() == idAccount2).findFirst();
//		softly.assertThat(account2.get().getAccountNumber()).isEqualTo(createAccountResponse2.getAccountNumber());
//		softly.assertThat(account2.get().getBalance()).isEqualTo(sum);
//		softly.assertThat(account2.get().getTransactions().size()).isEqualTo(1);

		List<Account> accounts = infoGetUserResponse.getAccounts();

		for (Account account : accounts) {
			for (Transaction transaction : account.getTransactions()) {
				if ("DEPOSIT".equals(transaction.getType())) {
					softly.assertThat(transaction.getRelatedAccountId())
							.as("relatedAccountId для DEPOSIT в аккаунте %s", account.getId())
							.isEqualTo(account.getId());
				} else if ("TRANSFER_OUT".equals(transaction.getType())) {
					// relatedAccountId должен быть id другого аккаунта
					long expectedRelatedId = accounts.stream()
							.filter(a -> a.getId() != account.getId())
							.findFirst()
							.orElseThrow()
							.getId();
					softly.assertThat(transaction.getRelatedAccountId())
							.as("relatedAccountId для TRANSFER_OUT в аккаунте %s", account.getId())
							.isEqualTo(expectedRelatedId);
				} else if ("TRANSFER_IN".equals(transaction.getType())) {
					// аналогично, relatedAccountId — это id другого аккаунта
					long expectedRelatedId = accounts.stream()
							.filter(a -> a.getId() != account.getId())
							.findFirst()
							.orElseThrow()
							.getId();
					softly.assertThat(transaction.getRelatedAccountId())
							.as("relatedAccountId для TRANSFER_IN в аккаунте %s", account.getId())
							.isEqualTo(expectedRelatedId);
				}
			}
		}

	}



	public static Stream<Arguments> notValidSum(){
		return Stream.of(
				Arguments.of(10001, ResponseSpecs.ERROR_MESSAGE_TRANSFER_EXCEED_10000),
				Arguments.of(0, ResponseSpecs.ERROR_MESSAGE_TRANSFER_LEAST_001),
				Arguments.of(-1, ResponseSpecs.ERROR_MESSAGE_TRANSFER_LEAST_001)
		);
	}

	@ParameterizedTest
	@MethodSource("notValidSum")
	@Tag("negative")
	@DisplayName("Пользователь не может переводить отрицательные суммы и суммы больше 10000")
	public void UserCantTransferMoneyFromOneAccountToAnotherWithNotCorrectSum(int sum, String error){
		// создаем пользователя
		CreateUserRequest createUserRequest = AdminSteps.createUser();
		// создаем 2 счета
		//1-ый счет
		 CreateAccountResponse createAccountResponse1 =  UserCreateAccount.userCreateAccount(createUserRequest);
		int idAccount1 = createAccountResponse1.getId();

		// 2-ой счет
		CreateAccountResponse createAccountResponse2 =  UserCreateAccount.userCreateAccount(createUserRequest);
		int idAccount2 = createAccountResponse2.getId();

		// пополняем первый счет на 10 000
		// 1-ый раз на 5 000
		UserCreateDeposit.createDeposit(createUserRequest,createAccountResponse1, getMaxDeposit() );

		// 2-ой раз на 5 000
		UserCreateDeposit.createDeposit(createUserRequest,createAccountResponse1, getMaxDeposit() );

		// переводим деньги с одного счета на другой
       CreateTransferRequest createTransferRequest =  CreateTransferRequest.builder()
				.senderAccountId(idAccount1)
				.receiverAccountId(idAccount2)
				.amount(sum).build();
		String errorMessage = new CrudRequester(RequestSpecs.authUserSpecForAcceptTEXT(
				createUserRequest.getUsername(), createUserRequest.getPassword()
		),ResponseSpecs.requestReturnBadRequest(), Endpoint.TRANSFER).post(createTransferRequest).extract().body().asString();

		softly.assertThat(errorMessage).isEqualTo(error);

		// запрашиваем информацию профиля
		InfoGetUserResponse infoGetUserResponse = GetUserInfo.getInfo(createUserRequest);
		softly.assertThat(infoGetUserResponse.getAccounts().get(1).getBalance() == 0);
		softly.assertThat(infoGetUserResponse.getAccounts().get(0).getBalance() == getMaxDeposit() * 2);

	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может переводить деньги с чужого счета на свой собственный")
	public void userCantTransferMoneyFromSomeOneAccountToHisOne(){
		//создаем 2 пользователя
		// 1-ый юзер
		CreateUserRequest createUserRequest1 = AdminSteps.createUser();
		// 2-ой юзер
		CreateUserRequest createUserRequest2 = AdminSteps.createUser();

		// создаем по 1 счету к каждому пользователю
		// 1-ый юзер
		CreateAccountResponse createAccountResponse1 =  UserCreateAccount.userCreateAccount(createUserRequest1);
		int idAccountFirstUser = createAccountResponse1.getId();
		// 2-ой юзер
		CreateAccountResponse createAccountResponse2 = UserCreateAccount.userCreateAccount(createUserRequest2);
		int idAccountSecondUser = createAccountResponse2.getId();
		// пополняем каждый счет
		// 1-ый юзер
		double balance1 = UserCreateDeposit.createDeposit(createUserRequest1,createAccountResponse1, RandomData.getRandomDeposit()).getResponse().getBalance();

		// 2-ой юзер
		double balance2 = UserCreateDeposit.createDeposit(createUserRequest2,createAccountResponse2, RandomData.getRandomDeposit()).getResponse().getBalance();


		// переводим деньги под одним юзером с чужого счета на его
		CreateTransferRequest createTransferRequest = CreateTransferRequest.builder().senderAccountId(idAccountSecondUser)
				.receiverAccountId(idAccountFirstUser)
				.amount((int)createAccountResponse2.getBalance()).build();
		String errorMessage =  new CrudRequester(RequestSpecs.authUserSpecForAcceptTEXT(
				createUserRequest1.getUsername(), createUserRequest1.getPassword()
		),ResponseSpecs.requestReturnForbidden(), Endpoint.TRANSFER).post(createTransferRequest).extract().body().asString();

		softly.assertThat(errorMessage).isEqualTo(ResponseSpecs.ERROR_MESSAGE_FORBIDDEN );

		// запрашиваем информацию профиля
		InfoGetUserResponse infoGetUserResponse1 = GetUserInfo.getInfo(createUserRequest1);// получатель
		softly.assertThat(infoGetUserResponse1.getAccounts().get(0).getBalance() == balance2);

		InfoGetUserResponse infoGetUserResponse2 = GetUserInfo.getInfo(createUserRequest2);// отправитель
		softly.assertThat(infoGetUserResponse2.getAccounts().get(0).getBalance() == balance1);


	}

	@Test
	@Tag("positive")
	@DisplayName("Пользователь может перевести деньги со своего счета на чужой счет")
	public void userCanTransferMoneyFromHisAccountToAnother(){
		//создаем 2 пользователя
		// 1-ый юзер
		CreateUserRequest createUserRequest1 = AdminSteps.createUser();

		// 2-ой юзер
		CreateUserRequest createUserRequest2 = AdminSteps.createUser();
		// создаем по 1 счету к каждому пользователю
		// 1-ый юзер
		CreateAccountResponse createAccountResponse1 = UserCreateAccount.userCreateAccount(createUserRequest1);
		int idAccountFirstUser = createAccountResponse1.getId();
		// 2-ой юзер
		CreateAccountResponse createAccountResponse2 =  UserCreateAccount.userCreateAccount(createUserRequest2);

		int idAccountSecondUser = createAccountResponse2.getId();
		// пополняем каждый счет
		// 1-ый юзер
		int sum1 = (int)UserCreateDeposit.createDeposit(createUserRequest1,createAccountResponse1, RandomData.getRandomDeposit()).getResponse().getBalance();

		// 2-ой юзер
		int sum2 = (int)UserCreateDeposit.createDeposit(createUserRequest2,createAccountResponse2, RandomData.getRandomDeposit()).getResponse().getBalance();


		// переводим деньги под одним юзером на другой счет
		CreateTransferResponse createTransferResponse = UserCreateTransfer.createTransfer(createUserRequest1,
				createAccountResponse1, createAccountResponse2,
				sum1);

		softly.assertThat(createTransferResponse.getMessage()).isEqualTo(ResponseSpecs.MESSAGE_TRANSFER_SUCCESSFUL);
		softly.assertThat(createTransferResponse.getReceiverAccountId()).isEqualTo(idAccountSecondUser);
		softly.assertThat(createTransferResponse.getSenderAccountId()).isEqualTo(idAccountFirstUser);
		softly.assertThat(createTransferResponse.getAmount()).isEqualTo((double) sum1);

		// запрашиваем информацию профиля
		InfoGetUserResponse infoGetUserResponse1 = GetUserInfo.getInfo(createUserRequest1);// отправитель
		softly.assertThat(infoGetUserResponse1.getAccounts().get(0).getBalance() == (double) sum1);

		InfoGetUserResponse infoGetUserResponse2 = GetUserInfo.getInfo(createUserRequest2);// получатель
		softly.assertThat(infoGetUserResponse2.getAccounts().get(0).getBalance() ==  (double) sum2);

	}

	@Test
	@Tag("positive")
	@DisplayName("Пользователь может отслеживать состояние своих учетных записей")
	public void userCanSeeTrackingOfTheirAccounts(){
		// создаем пользователя
		CreateUserRequest createUserRequest = AdminSteps.createUser();
		// создаем 2 счета
		//1-ый счет
		CreateAccountResponse createAccountResponse1 = UserCreateAccount.userCreateAccount(createUserRequest);
		int idAccount1 = createAccountResponse1.getId();

		// 2-ой счет
		CreateAccountResponse createAccountResponse2 = UserCreateAccount.userCreateAccount(createUserRequest);
		int idAccount2 = createAccountResponse2.getId();

		// пополняем первый счет
		int sum = (int)UserCreateDeposit.createDeposit(createUserRequest,createAccountResponse1, RandomData.getRandomDeposit()).getResponse().getBalance();

		// переводим деньги с одного счета на другой
		UserCreateTransfer.createTransfer(createUserRequest, createAccountResponse1, createAccountResponse2, sum);


		// берем айди аккаунта по которому был перевод
		// делаем запрос на отслеживание транзакций по айди аккаунта
	List<Transaction> transactions = new CrudRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
			ResponseSpecs.requestReturnOk(),
			Endpoint.LOOK_TRANSFER).getWithParams(idAccount1).extract()
			.jsonPath().getList("$", Transaction.class);

	softly.assertThat(!transactions.isEmpty());
	softly.assertThat(transactions.contains("id"));
	softly.assertThat(transactions.contains("amount"));
	softly.assertThat(transactions.contains("type"));
	softly.assertThat(transactions.contains("timestamp"));
	softly.assertThat(transactions.contains("relatedAccountId"));

		softly.assertThat(transactions.stream().map(Transaction::getType))
				.contains("DEPOSIT", "TRANSFER_OUT");

		Transaction transfer_out = transactions.stream()
				.filter(t -> t.getType().equals("TRANSFER_OUT"))
				.findFirst()
				.orElse(null);

		softly.assertThat(transfer_out).isNotNull();
		softly.assertThat(transfer_out.getAmount()).isEqualTo((double) sum);
		softly.assertThat(transfer_out.getRelatedAccountId()).isEqualTo(idAccount2);

		Transaction deposit = transactions.stream()
				.filter(t -> t.getType().equals("DEPOSIT"))
				.findFirst()
				.orElse(null);

		softly.assertThat(deposit).isNotNull();
		softly.assertThat(deposit.getAmount()).isEqualTo((double) sum);
		softly.assertThat(deposit.getRelatedAccountId()).isEqualTo(idAccount1);

	}


	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может отслеживать статус чужих аккаунтов")
	public void userCanSeeTrackingOfOtherAccounts(){
		// создаем юзера1 под которым будет отслеживать операции
		CreateUserRequest createUserRequest1 = AdminSteps.createUser();

		// создаем юзера 2 у которого будем отслеживать операции
		CreateUserRequest createUserRequest2 = AdminSteps.createUser();
		;

		// создаем счет ко второму юзеру
		CreateAccountResponse createAccountResponse2 = UserCreateAccount.userCreateAccount(createUserRequest2);
		// запрашиваем отслеживание операций второго юзера под токеном первого юзера
		String errorMessage = new CrudRequester(RequestSpecs.authUserSpecForAcceptTEXT(createUserRequest1.getUsername(), createUserRequest1.getPassword()),
				ResponseSpecs.requestReturnForbidden(),
				Endpoint.LOOK_TRANSFER).getWithParams(createAccountResponse2.getId()).extract().asString();

		softly.assertThat(errorMessage).isEqualTo(ResponseSpecs.ERROR_MESSAGE_FORBIDDEN_PERMISSION );

	}

	private static int getMaxDeposit(){
		return 5000;
	}

}

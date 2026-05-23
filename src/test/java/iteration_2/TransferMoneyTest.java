package iteration_2;


import iteration_2.data.Account;
import iteration_2.data.Transaction;
import iteration_2.generators.RandomData;
import iteration_2.models_body_JSON.*;
import iteration_2.models_body_JSON.change_name_user.InfoGetUserResponse;
import iteration_2.models_body_JSON.create_deposit.CreateDepositRequest;
import iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserResponse;
import iteration_2.models_body_JSON.transfer_money.CreateTransferRequest;
import iteration_2.models_body_JSON.transfer_money.CreateTransferResponse;
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
		CreateUserRequest createUserRequest = CreateUserRequest.builder()
				.username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
		.role(UserRole.USER.toString())
		.build();

		CreateUserResponse createUserResponse = new ValidateCrudRequester2<CreateUserResponse>(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(),
				Endpoint.ADMIN_USER).post(createUserRequest);

		// создаем 2 счета
		//1-ый счет
		CreateAccountResponse createAccountResponse1 = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);

		int idAccount1 = createAccountResponse1.getId();

		// 2-ой счет
		CreateAccountResponse createAccountResponse2 = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);
		int idAccount2 = createAccountResponse2.getId();

		// пополняем первый счет на 10 000
		// 1-ый раз на 5 000
		CreateDepositRequest createDepositRequest1 = CreateDepositRequest.builder().id(idAccount1).balance(5000).build();
		new CrudRequester(RequestSpecs.authUserSpec(
				createUserRequest.getUsername(), createUserRequest.getPassword()
		),ResponseSpecs.requestReturnOk(), Endpoint.DEPOSIT).post(createDepositRequest1);


		// 2-ой раз на 5 000
		CreateDepositRequest createDepositRequest2 = CreateDepositRequest.builder().id(idAccount1).balance(5000).build();
	new CrudRequester(RequestSpecs.authUserSpec(
				createUserRequest.getUsername(), createUserRequest.getPassword()
		),ResponseSpecs.requestReturnOk(), Endpoint.DEPOSIT).post(createDepositRequest2);

		// переводим деньги с одного счета на другой
		CreateTransferRequest createTransferRequest = CreateTransferRequest.builder().senderAccountId(idAccount1).receiverAccountId(idAccount2).amount(sum).build();
		CreateTransferResponse createTransferResponse = new ValidateCrudRequester2<CreateTransferResponse>(
				RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk(),
				Endpoint.TRANSFER
		).post(createTransferRequest);

		softly.assertThat(createTransferResponse.getReceiverAccountId()).isEqualTo(idAccount2);
		softly.assertThat(createTransferResponse.getSenderAccountId()).isEqualTo(idAccount1);
		softly.assertThat(createTransferResponse.getAmount()).isEqualTo((double)sum);
		softly.assertThat(createTransferResponse.getMessage()).isEqualTo("Transfer successful");

		// запрашиваем информацию профиля

		InfoGetUserResponse infoGetUserResponse = new ValidateCrudRequester2<InfoGetUserResponse>(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk(),
				Endpoint.USER_INFO).get();

softly.assertThat(infoGetUserResponse.getUsername()).isEqualTo(createUserRequest.getUsername());
softly.assertThat(infoGetUserResponse.getId()).isEqualTo(createUserResponse.getId());
softly.assertThat(infoGetUserResponse.getPassword()).isEqualTo(createUserResponse.getPassword());

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

		// Предположим, infoGetUserResponse уже получен

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
				Arguments.of(10001, "Transfer amount cannot exceed 10000"),
				Arguments.of(0, "Transfer amount must be at least 0.01"),
				Arguments.of(-1, "Transfer amount must be at least 0.01")
		);
	}

	@ParameterizedTest
	@MethodSource("notValidSum")
	@Tag("negative")
	@DisplayName("Пользователь не может переводить отрицательные суммы и суммы больше 10000")
	public void UserCantTransferMoneyFromOneAccountToAnotherWithNotCorrectSum(int sum, String error){
		// создаем пользователя
		CreateUserRequest createUserRequest = CreateUserRequest.builder().username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();

		new CrudRequester(RequestSpecs.adminSpec(),ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USER).post(createUserRequest);
		// создаем 2 счета
		//1-ый счет
		 CreateAccountResponse createAccountResponse1 = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				 ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);
		int idAccount1 = createAccountResponse1.getId();

		// 2-ой счет
		CreateAccountResponse createAccountResponse2 = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);
		int idAccount2 = createAccountResponse2.getId();

		// пополняем первый счет на 10 000
		// 1-ый раз на 5 000
		CreateDepositRequest createDepositRequest1 = CreateDepositRequest.builder().id(idAccount1).balance(5000).build();
		new CrudRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.requestReturnOk(),
				Endpoint.DEPOSIT).post(createDepositRequest1);


		// 2-ой раз на 5 000
		CreateDepositRequest createDepositRequest2 = CreateDepositRequest.builder().id(idAccount1).balance(5000).build();
		new CrudRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.requestReturnOk(),
				Endpoint.DEPOSIT).post(createDepositRequest2);

		// переводим деньги с одного счета на другой
       CreateTransferRequest createTransferRequest =  CreateTransferRequest.builder()
				.senderAccountId(idAccount1)
				.receiverAccountId(idAccount2)
				.amount(sum).build();
		String errorMessage = new CrudRequester(RequestSpecs.authUserSpecForAcceptTEXT(
				createUserRequest.getUsername(), createUserRequest.getPassword()
		),ResponseSpecs.requestReturnBadRequest(), Endpoint.TRANSFER).post(createTransferRequest).extract().body().asString();

		softly.assertThat(errorMessage).isEqualTo(error);

	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может переводить деньги с чужого счета на свой собственный")
	public void userCantTransferMoneyFromSomeOneAccountToHisOne(){
		//создаем 2 пользователя
		// 1-ый юзер
		CreateUserRequest createUserRequest1 = CreateUserRequest.builder().username(RandomData.getRandomUserName()).password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();

		new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USER).post(createUserRequest1);
		// 2-ой юзер
		CreateUserRequest createUserRequest2 = CreateUserRequest.builder().username(RandomData.getRandomUserName()).password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();

		new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USER).post(createUserRequest2);

		// создаем по 1 счету к каждому пользователю
		// 1-ый юзер
		CreateAccountResponse createAccountResponse1 = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()),
				ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);
		int idAccountFirstUser = createAccountResponse1.getId();
		// 2-ой юзер
		CreateAccountResponse createAccountResponse2 = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserRequest2.getUsername(), createUserRequest2.getPassword()),
				ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);
		int idAccountSecondUser = createAccountResponse2.getId();
		// пополняем каждый счет
		// 1-ый юзер
		CreateDepositRequest createDepositRequest1 = CreateDepositRequest.builder().id(idAccountFirstUser).balance(500).build();
		new CrudRequester(RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()), ResponseSpecs.requestReturnOk(),
				Endpoint.DEPOSIT).post(createDepositRequest1);

		// 2-ой юзер
		CreateDepositRequest createDepositRequest2 = CreateDepositRequest.builder().id(idAccountSecondUser).balance(500).build();
		new CrudRequester(RequestSpecs.authUserSpec(createUserRequest2.getUsername(), createUserRequest2.getPassword()), ResponseSpecs.requestReturnOk(),
				Endpoint.DEPOSIT).post(createDepositRequest2);

		// переводим деньги под одним юзером с чужого счета на его
		CreateTransferRequest createTransferRequest = CreateTransferRequest.builder().senderAccountId(idAccountSecondUser).receiverAccountId(idAccountFirstUser)
				.amount(100).build();
		String errorMessage =  new CrudRequester(RequestSpecs.authUserSpecForAcceptTEXT(
				createUserRequest1.getUsername(), createUserRequest1.getPassword()
		),ResponseSpecs.requestReturnForbidden(), Endpoint.TRANSFER).post(createTransferRequest).extract().body().asString();

		softly.assertThat(errorMessage).isEqualTo("Unauthorized access to account");


	}

	@Test
	@Tag("positive")
	@DisplayName("Пользователь может перевести деньги со своего счета на чужой счет")
	public void userCanTransferMoneyFromHisAccountToAnother(){
		//создаем 2 пользователя
		// 1-ый юзер
		CreateUserRequest createUserRequest1 = CreateUserRequest.builder().username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();
		new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USER).post(createUserRequest1);


		// 2-ой юзер
		CreateUserRequest createUserRequest2 = CreateUserRequest.builder().username(RandomData.getRandomUserName()).password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();
		new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USER).post(createUserRequest2);


		// создаем по 1 счету к каждому пользователю
		// 1-ый юзер
		CreateAccountResponse createAccountResponse1 = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()),
				ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);

		int idAccountFirstUser = createAccountResponse1.getId();
		// 2-ой юзер
		CreateAccountResponse createAccountResponse2 =  new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserRequest2.getUsername(), createUserRequest2.getPassword()),
				ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);

		int idAccountSecondUser = createAccountResponse2.getId();
		// пополняем каждый счет
		// 1-ый юзер
		CreateDepositRequest createDepositRequest1 = CreateDepositRequest.builder().id(idAccountFirstUser).balance(500).build();
		new CrudRequester(RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()), ResponseSpecs.requestReturnOk(),
				Endpoint.DEPOSIT).post(createDepositRequest1);

		// 2-ой юзер
		CreateDepositRequest createDepositRequest2 = CreateDepositRequest.builder().id(idAccountSecondUser).balance(500).build();
		new CrudRequester(RequestSpecs.authUserSpec(createUserRequest2.getUsername(), createUserRequest2.getPassword()), ResponseSpecs.requestReturnOk(),
				Endpoint.DEPOSIT).post(createDepositRequest2);

		// переводим деньги под одним юзером на другой счет
		CreateTransferRequest createTransferRequest = CreateTransferRequest.builder().senderAccountId(idAccountFirstUser).receiverAccountId(idAccountSecondUser).amount(50).build();
		CreateTransferResponse createTransferResponse =  new ValidateCrudRequester2<CreateTransferResponse>(RequestSpecs.authUserSpec(
				createUserRequest1.getUsername(), createUserRequest1.getPassword()
		),ResponseSpecs.requestReturnOk(), Endpoint.TRANSFER).post(createTransferRequest);

		softly.assertThat(createTransferResponse.getMessage()).isEqualTo("Transfer successful");
		softly.assertThat(createTransferResponse.getReceiverAccountId()).isEqualTo(idAccountSecondUser);
		softly.assertThat(createTransferResponse.getSenderAccountId()).isEqualTo(idAccountFirstUser);
		softly.assertThat(createTransferResponse.getAmount()).isEqualTo((double) 50);

	}

	@Test
	@Tag("positive")
	@DisplayName("Пользователь может отслеживать состояние своих учетных записей")
	public void userCanSeeTrackingOfTheirAccounts(){
		// создаем пользователя
		CreateUserRequest createUserRequest = CreateUserRequest.builder().username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();
new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USER).post(createUserRequest);
		// создаем 2 счета
		//1-ый счет
		CreateAccountResponse createAccountResponse1 = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);
		int idAccount1 = createAccountResponse1.getId();

		// 2-ой счет
		CreateAccountResponse createAccountResponse2 = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);
		int idAccount2 = createAccountResponse2.getId();

		// пополняем первый счет
		CreateDepositRequest createDepositRequest = CreateDepositRequest.builder().id(idAccount1).balance(500).build();
		new CrudRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.requestReturnOk(),
				Endpoint.DEPOSIT).post(createDepositRequest);


		// переводим деньги с одного счета на другой
		CreateTransferRequest createTransferRequest = CreateTransferRequest.builder().senderAccountId(idAccount1).receiverAccountId(idAccount2).amount(50).build();

		new ValidateCrudRequester2<CreateTransferResponse>(RequestSpecs.authUserSpec(
				createUserRequest.getUsername(), createUserRequest.getPassword()
		),ResponseSpecs.requestReturnOk(), Endpoint.TRANSFER).post(createTransferRequest);

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
		softly.assertThat(transfer_out.getAmount()).isEqualTo(50.0);
		softly.assertThat(transfer_out.getRelatedAccountId()).isEqualTo(idAccount2);

		Transaction deposit = transactions.stream()
				.filter(t -> t.getType().equals("DEPOSIT"))
				.findFirst()
				.orElse(null);

		softly.assertThat(deposit).isNotNull();
		softly.assertThat(deposit.getAmount()).isEqualTo(500.0);
		softly.assertThat(deposit.getRelatedAccountId()).isEqualTo(idAccount1);

	}


	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может отслеживать статус чужих аккаунтов")
	public void userCanSeeTrackingOfOtherAccounts(){
		// создаем юзера1 под которым будет отслеживать операции
		CreateUserRequest createUserRequest1 = CreateUserRequest.builder().username(RandomData.getRandomUserName()).password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();
		new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USER).post(createUserRequest1);

		// создаем юзера 2 у которого будем отслеживать операции
		CreateUserRequest createUserRequest2 = CreateUserRequest.builder().username(RandomData.getRandomUserName()).password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();
		new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USER).post(createUserRequest2);
		;

		// создаем счет ко второму юзеру
		CreateAccountResponse createAccountResponse2 = new ValidateCrudRequester2<CreateAccountResponse>(RequestSpecs.authUserSpec(createUserRequest2.getUsername(), createUserRequest2.getPassword()),
				ResponseSpecs.entityWasCreated(), Endpoint.ACCOUNT).post(null);
		// запрашиваем отслеживание операций второго юзера под токеном первого юзера
		String errorMessage = new CrudRequester(RequestSpecs.authUserSpecForAcceptTEXT(createUserRequest1.getUsername(), createUserRequest1.getPassword()),
				ResponseSpecs.requestReturnForbidden(),
				Endpoint.LOOK_TRANSFER).getWithParams(createAccountResponse2.getId()).extract().asString();

		softly.assertThat(errorMessage).isEqualTo("You do not have permission to access this account");

	}




}

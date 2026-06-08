package iteration_2;


import iteration_1.models.comparison.ModelAssertions;
import iteration_2.data.Account;
import iteration_2.data.Transaction;
import iteration_2.data.TransactionType;
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
	public void UserCanTransferMoneyFromOneAccountToAnother(int sum) {
		// Создаем пользователя
		CreateUserRequest createUserRequest = RandomModelGenerator2Iteration.generate(CreateUserRequest.class);
		CreateUserResponse createUserResponse = new ValidateCrudRequester2<CreateUserResponse>(
				RequestSpecs.adminSpec(),
				ResponseSpecs.entityWasCreated(),
				Endpoint.ADMIN_USER
		).post(createUserRequest);

		// Создаем 2 счета
		CreateAccountResponse createAccountResponse1 = UserCreateAccount.userCreateAccount(createUserRequest);
		long idAccount1 = createAccountResponse1.getId();

		CreateAccountResponse createAccountResponse2 = UserCreateAccount.userCreateAccount(createUserRequest);
		long idAccount2 = createAccountResponse2.getId();

		// Пополняем первый счет: 2 депозита по 5000
		UserCreateDeposit.createDeposit(createUserRequest, createAccountResponse1, getMaxDeposit());
		UserCreateDeposit.createDeposit(createUserRequest, createAccountResponse1, getMaxDeposit());

		// Переводим деньги
		CreateTransferResponse createTransferResponse = UserCreateTransfer.createTransfer(
				createUserRequest, createAccountResponse1, createAccountResponse2, sum);

		// Проверяем ответ перевода
		softly.assertThat(createTransferResponse.getReceiverAccountId()).isEqualTo(idAccount2);
		softly.assertThat(createTransferResponse.getSenderAccountId()).isEqualTo(idAccount1);
		softly.assertThat(createTransferResponse.getAmount()).isEqualTo((double) sum);
		softly.assertThat(createTransferResponse.getMessage()).isEqualTo(ResponseSpecs.MESSAGE_TRANSFER_SUCCESSFUL);

		// Получаем профиль
		InfoGetUserResponse infoGetUserResponse = new ValidateCrudRequester2<InfoGetUserResponse>(
				RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk(),
				Endpoint.USER_INFO
		).get();

		ModelAssertions.assertThatModels(infoGetUserResponse, createUserRequest).match();
		ModelAssertions.assertThatModels(infoGetUserResponse, createUserResponse).match();

		// Находим конкретные счета по известным ID
		List<Account> accounts = infoGetUserResponse.getAccounts();

		Account account1 = accounts.stream()
				.filter(a -> a.getId() == idAccount1)
				.findFirst()
				.orElseThrow(() -> new AssertionError("Счет отправителя (id=" + idAccount1 + ") не найден"));

		Account account2 = accounts.stream()
				.filter(a -> a.getId() == idAccount2)
				.findFirst()
				.orElseThrow(() -> new AssertionError("Счет получателя (id=" + idAccount2 + ") не найден"));

		// Проверяем балансы
		softly.assertThat(account1.getBalance()).isEqualTo(10000.0 - sum);
		softly.assertThat(account2.getBalance()).isEqualTo((double) sum);

		// === Проверяем транзакции счета 1 (отправитель) ===
		List<Transaction> transactions1 = account1.getTransactions();
		softly.assertThat(transactions1).hasSize(3);

		// Находим транзакции по типу (не зависим от порядка в JSON)
		List<Transaction> deposits1 = transactions1.stream()
				.filter(t -> t.getType().equals(TransactionType.DEPOSIT.name()))
				.toList();
		List<Transaction> transfersOut = transactions1.stream()
				.filter(t -> t.getType().equals(TransactionType.TRANSFER_OUT.name()))
				.toList();

		softly.assertThat(deposits1).hasSize(2);
		softly.assertThat(transfersOut).hasSize(1);

		// Проверяем депозиты (любой порядок, суммы одинаковые)
		softly.assertThat(deposits1).allMatch(t -> t.getAmount() == 5000.0);
		softly.assertThat(deposits1).allMatch(t -> t.getRelatedAccountId() == idAccount1);

		// Проверяем перевод OUT
		Transaction transferOut = transfersOut.get(0);
		softly.assertThat(transferOut.getAmount()).isEqualTo((double) sum);
		softly.assertThat(transferOut.getRelatedAccountId()).isEqualTo(idAccount2);

		// === Проверяем транзакции счета 2 (получатель) ===
		List<Transaction> transactions2 = account2.getTransactions();
		softly.assertThat(transactions2).hasSize(1);

		List<Transaction> transfersIn = transactions2.stream()
				.filter(t -> t.getType().equals(TransactionType.TRANSFER_IN.name()))
				.toList();
		softly.assertThat(transfersIn).hasSize(1);

		Transaction transferIn = transfersIn.get(0);
		softly.assertThat(transferIn.getAmount()).isEqualTo((double) sum);
		softly.assertThat(transferIn.getRelatedAccountId()).isEqualTo(idAccount1);
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
		InfoGetUserResponse infoGetUserResponse = new ValidateCrudRequester2<InfoGetUserResponse>(
				RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk(),
				Endpoint.USER_INFO
		).get();
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
		InfoGetUserResponse infoGetUserResponse1 = new ValidateCrudRequester2<InfoGetUserResponse>(
				RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()),
				ResponseSpecs.requestReturnOk(),
				Endpoint.USER_INFO
		).get();
		softly.assertThat(infoGetUserResponse1.getAccounts().get(0).getBalance() == balance2);

		// отправитель
		InfoGetUserResponse infoGetUserResponse2 = new ValidateCrudRequester2<InfoGetUserResponse>(
				RequestSpecs.authUserSpec(createUserRequest2.getUsername(), createUserRequest2.getPassword()),
				ResponseSpecs.requestReturnOk(),
				Endpoint.USER_INFO
		).get();
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
		// отправитель
		InfoGetUserResponse infoGetUserResponse1 = new ValidateCrudRequester2<InfoGetUserResponse>(
				RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()),
				ResponseSpecs.requestReturnOk(),
				Endpoint.USER_INFO
		).get();
		softly.assertThat(infoGetUserResponse1.getAccounts().get(0).getBalance() == (double) sum1);

		// получатель
		InfoGetUserResponse infoGetUserResponse2 = new ValidateCrudRequester2<InfoGetUserResponse>(
				RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()),
				ResponseSpecs.requestReturnOk(),
				Endpoint.USER_INFO
		).get();
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

	private record TransactionWithAccount(Transaction transaction, long accountId) {}

}

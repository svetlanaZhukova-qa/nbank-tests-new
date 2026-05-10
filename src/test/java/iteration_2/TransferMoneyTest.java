package iteration_2;


import iteration_2.data.Transaction;
import iteration_2.generators.RandomData;
import iteration_2.models_body_JSON.*;
import iteration_2.requests.*;
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

import static io.restassured.RestAssured.given;
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

		new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest)
				.extract().as(CreateUserResponse.class);

		// создаем 2 счета
		//1-ый счет
		CreateAccountResponse createAccountResponse1 = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.entityWasCreated())
				.postApi(null)
				.extract().as(CreateAccountResponse.class);

		int idAccount1 = createAccountResponse1.getId();

		// 2-ой счет
		CreateAccountResponse createAccountResponse2 = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.entityWasCreated())
				.postApi(null)
				.extract().as(CreateAccountResponse.class);

		int idAccount2 = createAccountResponse2.getId();

		// пополняем первый счет на 10 000
		// 1-ый раз на 5 000
		CreateDepositRequest createDepositRequest1 = CreateDepositRequest.builder().id(idAccount1).balance(5000).build();
		new UserCreateDepositRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.requestReturnOk())
				.postApi(createDepositRequest1);

		// 2-ой раз на 5 000
		CreateDepositRequest createDepositRequest2 = CreateDepositRequest.builder().id(idAccount1).balance(5000).build();
		new UserCreateDepositRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.requestReturnOk())
				.postApi(createDepositRequest2);

		// переводим деньги с одного счета на другой
		CreateTransferRequest createTransferRequest = CreateTransferRequest.builder().senderAccountId(idAccount1).receiverAccountId(idAccount2).amount(sum).build();
		CreateTransferResponse createTransferResponse = new UserCreateTransferRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.requestReturnOk())
				.postApi(createTransferRequest).extract().as(CreateTransferResponse.class);

		softly.assertThat(createTransferResponse.getReceiverAccountId()).isEqualTo(idAccount2);
		softly.assertThat(createTransferResponse.getSenderAccountId()).isEqualTo(idAccount1);
		softly.assertThat(createTransferResponse.getAmount()).isEqualTo((double)sum);
		softly.assertThat(createTransferResponse.getMessage()).isEqualTo("Transfer successful");

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

		new AdminCreateUserRequester(RequestSpecs.adminSpec(),ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest).extract().as(CreateUserResponse.class);
		// создаем 2 счета
		//1-ый счет
		 CreateAccountResponse createAccountResponse1 = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.entityWasCreated())
				 .postApi(null).extract().as(CreateAccountResponse.class);
		int idAccount1 = createAccountResponse1.getId();

		// 2-ой счет
		CreateAccountResponse createAccountResponse2 = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.entityWasCreated())
				.postApi(null).extract().as(CreateAccountResponse.class);
		int idAccount2 = createAccountResponse2.getId();

		// пополняем первый счет на 10 000
		// 1-ый раз на 5 000
		CreateDepositRequest createDepositRequest1 = CreateDepositRequest.builder().id(idAccount1).balance(5000).build();
		new UserCreateDepositRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),ResponseSpecs.requestReturnOk())
				.postApi(createDepositRequest1);

		// 2-ой раз на 5 000
		CreateDepositRequest createDepositRequest2 = CreateDepositRequest.builder().id(idAccount1).balance(5000).build();
		new UserCreateDepositRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),ResponseSpecs.requestReturnOk())
				.postApi(createDepositRequest2);

		// переводим деньги с одного счета на другой
		CreateTransferRequest createTransferRequest = CreateTransferRequest.builder()
				.senderAccountId(idAccount1)
				.receiverAccountId(idAccount2)
				.amount(sum).build();
		String errorMessage = new UserCreateTransferRequester(RequestSpecs.authUserSpecForAcceptTEXT(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.requestReturnBadRequest())
				.postApi(createTransferRequest).extract().asString();


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
		new AdminCreateUserRequester(RequestSpecs.adminSpec(),ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest1);
		// 2-ой юзер
		CreateUserRequest createUserRequest2 = CreateUserRequest.builder().username(RandomData.getRandomUserName()).password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();
		new AdminCreateUserRequester(RequestSpecs.adminSpec(),ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest2);
		// создаем по 1 счету к каждому пользователю
		// 1-ый юзер
		CreateAccountResponse createAccountResponse1 = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()), ResponseSpecs.entityWasCreated())
				.postApi(null).extract().as(CreateAccountResponse.class);
		int idAccountFirstUser = createAccountResponse1.getId();
		// 2-ой юзер
		CreateAccountResponse createAccountResponse2 = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserRequest2.getUsername(), createUserRequest2.getPassword()), ResponseSpecs.entityWasCreated())
				.postApi(null).extract().as(CreateAccountResponse.class);
		int idAccountSecondUser = createAccountResponse2.getId();
		// пополняем каждый счет
		// 1-ый юзер
		CreateDepositRequest createDepositRequest1 = CreateDepositRequest.builder().id(idAccountFirstUser).balance(500).build();
		new UserCreateDepositRequester(RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()), ResponseSpecs.requestReturnOk())
				.postApi(createDepositRequest1);

		// 2-ой юзер
		CreateDepositRequest createDepositRequest2 = CreateDepositRequest.builder().id(idAccountSecondUser).balance(500).build();
		new UserCreateDepositRequester(RequestSpecs.authUserSpec(createUserRequest2.getUsername(), createUserRequest2.getPassword()), ResponseSpecs.requestReturnOk())
				.postApi(createDepositRequest2);

		// переводим деньги под одним юзером с чужого счета на его
		CreateTransferRequest createTransferRequest = CreateTransferRequest.builder().senderAccountId(idAccountSecondUser).receiverAccountId(idAccountFirstUser)
				.amount(100).build();
		String errorMessage = new UserCreateTransferRequester(RequestSpecs.authUserSpecForAcceptTEXT(createUserRequest1.getUsername(), createUserRequest1.getPassword()),ResponseSpecs.requestReturnForbidden())
				.postApi(createTransferRequest).extract().asString();

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
		new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest1);

		// 2-ой юзер
		CreateUserRequest createUserRequest2 = CreateUserRequest.builder().username(RandomData.getRandomUserName()).password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();
		new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest2);

		// создаем по 1 счету к каждому пользователю
		// 1-ый юзер
		CreateAccountResponse createAccountResponse1 = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()),ResponseSpecs.entityWasCreated())
				.postApi(null).extract().as(CreateAccountResponse.class);

		int idAccountFirstUser = createAccountResponse1.getId();
		// 2-ой юзер
		CreateAccountResponse createAccountResponse2 = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserRequest2.getUsername(), createUserRequest2.getPassword()),ResponseSpecs.entityWasCreated())
				.postApi(null).extract().as(CreateAccountResponse.class);

		int idAccountSecondUser = createAccountResponse2.getId();
		// пополняем каждый счет
		// 1-ый юзер
		CreateDepositRequest createDepositRequest1 = CreateDepositRequest.builder().id(idAccountFirstUser).balance(500).build();
		new UserCreateDepositRequester(RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()),ResponseSpecs.requestReturnOk())
				.postApi(createDepositRequest1);

		// 2-ой юзер
		CreateDepositRequest createDepositRequest2 = CreateDepositRequest.builder().id(idAccountSecondUser).balance(500).build();
		new UserCreateDepositRequester(RequestSpecs.authUserSpec(createUserRequest2.getUsername(), createUserRequest2.getPassword()),ResponseSpecs.requestReturnOk())
				.postApi(createDepositRequest2);

		// переводим деньги под одним юзером на другой счет
		CreateTransferRequest createTransferRequest = CreateTransferRequest.builder().senderAccountId(idAccountFirstUser).receiverAccountId(idAccountSecondUser).amount(50).build();
		CreateTransferResponse createTransferResponse = new UserCreateTransferRequester(RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()),ResponseSpecs.requestReturnOk())
				.postApi(createTransferRequest).extract().as(CreateTransferResponse.class);
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
		new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated()).postApi(createUserRequest);

		// создаем 2 счета
		//1-ый счет
		CreateAccountResponse createAccountResponse1 = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.entityWasCreated())
				.postApi(null).extract().as(CreateAccountResponse.class);
		int idAccount1 = createAccountResponse1.getId();

		// 2-ой счет
		CreateAccountResponse createAccountResponse2 = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.entityWasCreated())
				.postApi(null).extract().as(CreateAccountResponse.class);
		int idAccount2 = createAccountResponse2.getId();

		// пополняем первый счет
		CreateDepositRequest createDepositRequest = CreateDepositRequest.builder().id(idAccount1).balance(500).build();
		new UserCreateDepositRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()), ResponseSpecs.requestReturnOk())
				.postApi(createDepositRequest);

		// переводим деньги с одного счета на другой
		CreateTransferRequest createTransferRequest = CreateTransferRequest.builder().senderAccountId(idAccount1).receiverAccountId(idAccount2).amount(50).build();
		CreateTransferResponse createTransferResponse = new UserCreateTransferRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),ResponseSpecs.requestReturnOk())
				.postApi(createTransferRequest).extract().as(CreateTransferResponse.class);


		// берем айди аккаунта по которому был перевод
		// делаем запрос на отслеживание транзакций по айди аккаунта
	List<Transaction> transactions = new UserLookTransferRequester(
				RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk())
				.getApi(idAccount1)
				.extract()
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
		new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest1);

		// создаем юзера 2 у которого будем отслеживать операции
		CreateUserRequest createUserRequest2 = CreateUserRequest.builder().username(RandomData.getRandomUserName()).password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();
		new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest2);

		// создаем счет ко второму юзеру
		CreateAccountResponse createAccountResponse2 = new UserCreateAccountRequester(RequestSpecs.authUserSpec(createUserRequest1.getUsername(), createUserRequest1.getPassword()), ResponseSpecs.entityWasCreated())
				.postApi(null).extract().as(CreateAccountResponse.class);
		int idValueAccountUser2 = createAccountResponse2.getId();
		// запрашиваем отслеживание операций второго юзера под токеном первого юзера
		String errorMessage = new UserLookTransferRequester(RequestSpecs.authUserSpecForAcceptTEXT(createUserRequest1.getUsername(), createUserRequest1.getPassword()), ResponseSpecs.requestReturnForbidden())
				.getApi(createAccountResponse2.getId()).extract().asString();

		softly.assertThat(errorMessage).isEqualTo("You do not have permission to access this account");

	}




}

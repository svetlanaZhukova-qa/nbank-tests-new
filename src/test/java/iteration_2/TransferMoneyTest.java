package iteration_2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
// Перевод денег с одного аккаунта на другой
// — Максимальная сумма: 10000
// — Сумма должна быть положительной и не превышать баланс отправителя
// — Можно переводить между своими аккаунтами и чужими

@DisplayName("Тесты на проверку возможности перевода денег с одного счета на другой")
public class TransferMoneyTest extends LoggerClass {

	@ParameterizedTest
	@ValueSource(ints = {9999, 10000})
	@Tag("positive")
	@DisplayName("Пользователь может переводить деньги с одного счета на другой. Максимальная сумма 10000")
	public void UserCanTransferMoneyFromOneAccountToAnother(int sum){
		// создаем пользователя
		String randomUser = UserHelper.createUser();

		// берем токен
		String userToken = UserHelper.getToken(randomUser);
		// создаем 2 счета
		//1-ый счет
		Response response1 = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(201)
				.extract()
				.response();

		int idValue1 = response1.jsonPath().getInt("id");

		// 2-ой счет
		Response response2 = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(201)
				.extract()
				.response();

		int idValue2 = response2.jsonPath().getInt("id");

		// пополняем первый счет на 10 000
		// 1-ый раз на 5 000
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.body(String.format("""
						{
						  "id": %d,
						  "balance": 5000
						}
						""",idValue1))
				.when()
				.post("http://localhost:4111/api/v1/accounts/deposit")
				.then()
				.statusCode(HttpStatus.SC_OK);

		// 2-ой раз на 5 000
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.body(String.format("""
						{
						  "id": %d,
						  "balance": 5000
						}
						""",idValue1))
				.when()
				.post("http://localhost:4111/api/v1/accounts/deposit")
				.then()
				.statusCode(HttpStatus.SC_OK);

		// переводим деньги с одного счета на другой
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.body(String.format("""
						{
						  "senderAccountId": %d,
						  "receiverAccountId": %d,
						  "amount": %d
						}
						""",idValue1, idValue2,sum))
				.when()
				.post("http://localhost:4111/api/v1/accounts/transfer")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.body("senderAccountId", Matchers.equalTo(idValue1))
				.body("receiverAccountId", Matchers.equalTo(idValue2))
				.body("message", Matchers.equalTo("Transfer successful"))
				.body("amount", Matchers.equalTo((float)sum));
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
		String randomUser = UserHelper.createUser();

		// берем токен
		String userToken = UserHelper.getToken(randomUser);
		// создаем 2 счета
		//1-ый счет
		Response response1 = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(201)
				.extract()
				.response();

		int idValue1 = response1.jsonPath().getInt("id");

		// 2-ой счет
		Response response2 = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(201)
				.extract()
				.response();

		int idValue2 = response2.jsonPath().getInt("id");

		// пополняем первый счет на 10 000
		// 1-ый раз на 5 000
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.body(String.format("""
						{
						  "id": %d,
						  "balance": 5000
						}
						""",idValue1))
				.when()
				.post("http://localhost:4111/api/v1/accounts/deposit")
				.then()
				.statusCode(HttpStatus.SC_OK);

		// 2-ой раз на 5 000
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.body(String.format("""
						{
						  "id": %d,
						  "balance": 5000
						}
						""",idValue1))
				.when()
				.post("http://localhost:4111/api/v1/accounts/deposit")
				.then()
				.statusCode(HttpStatus.SC_OK);

		// переводим деньги с одного счета на другой
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.TEXT)
				.header("Authorization", userToken)
				.body(String.format("""
						{
						  "senderAccountId": %d,
						  "receiverAccountId": %d,
						  "amount": %d
						}
						""",idValue1, idValue2,sum))
				.when()
				.post("http://localhost:4111/api/v1/accounts/transfer")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.body(Matchers.equalTo(error));
	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может переводить деньги с чужого счета на свой собственный")
	public void userCantTransferMoneyFromSomeOneAccountToHisOne(){
		//создаем 2 пользователя
		// 1-ый юзер
		String randomUser1 = UserHelper.createUser();
		// получаем токен пользователя
		String userToken1 = UserHelper.getToken(randomUser1);
		// 2-ой юзер
		String randomUser2= UserHelper.createUser();
		// получаем токен пользователя
		String userToken2 = UserHelper.getToken(randomUser2);
		// создаем по 1 счету к каждому пользователю
		// 1-ый юзер
		Response responseUser1 = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken1)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.response();
		int idAccountFirstUser = responseUser1.jsonPath().getInt("id");
		// 2-ой юзер
		Response responseUser2 = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken2)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.response();
		int idAccountSecondUser = responseUser2.jsonPath().getInt("id");
		// пополняем каждый счет
		// 1-ый юзер
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken1)
				.body(String.format("""
				{
						  "id": %d,
						  "balance": 500
						}
				""",idAccountFirstUser))
				.when()
				.post("http://localhost:4111/api/v1/accounts/deposit")
				.then()
				.statusCode(HttpStatus.SC_OK);
		// 2-ой юзер
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken2)
				.body(String.format("""
				{
						  "id": %d,
						  "balance": 500
						}
				""",idAccountSecondUser))
				.when()
				.post("http://localhost:4111/api/v1/accounts/deposit")
				.then()
				.statusCode(HttpStatus.SC_OK);
		// переводим деньги под одним юзером с чужого счета на его
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.TEXT)
				.header("Authorization", userToken1)
				.body(String.format("""
						{
						  "senderAccountId": %d,
						  "receiverAccountId": %d,
						  "amount": 50
						}
						""",idAccountSecondUser, idAccountFirstUser))
				.when()
				.post("http://localhost:4111/api/v1/accounts/transfer")
				.then()
				.statusCode(HttpStatus.SC_FORBIDDEN)
				.body(Matchers.equalTo("Unauthorized access to account"));

	}

	@Test
	@Tag("positive")
	@DisplayName("Пользователь может перевести деньги со своего счета на чужой счет")
	public void userCanTransferMoneyFromHisAccountToAnother(){
		//создаем 2 пользователя
		// 1-ый юзер
		String randomUser1 = UserHelper.createUser();
		// получаем токен пользователя
		String userToken1 = UserHelper.getToken(randomUser1);
		// 2-ой юзер
		String randomUser2= UserHelper.createUser();
		// получаем токен пользователя
		String userToken2 = UserHelper.getToken(randomUser2);
		// создаем по 1 счету к каждому пользователю
		// 1-ый юзер
		Response responseUser1 = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken1)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.response();
		int idAccountFirstUser = responseUser1.jsonPath().getInt("id");
		// 2-ой юзер
		Response responseUser2 = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken2)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.response();
		int idAccountSecondUser = responseUser2.jsonPath().getInt("id");
		// пополняем каждый счет
		// 1-ый юзер
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken1)
				.body(String.format("""
				{
						  "id": %d,
						  "balance": 500
						}
				""",idAccountFirstUser))
				.when()
				.post("http://localhost:4111/api/v1/accounts/deposit")
				.then()
				.statusCode(HttpStatus.SC_OK);
		// 2-ой юзер
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken2)
				.body(String.format("""
				{
						  "id": %d,
						  "balance": 500
						}
				""",idAccountSecondUser))
				.when()
				.post("http://localhost:4111/api/v1/accounts/deposit")
				.then()
				.statusCode(HttpStatus.SC_OK);
		// переводим деньги под одним юзером на другой счет
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken1)
				.body(String.format("""
						{
						  "senderAccountId": %d,
						  "receiverAccountId": %d,
						  "amount": 50
						}
						""",idAccountFirstUser, idAccountSecondUser))
				.when()
				.post("http://localhost:4111/api/v1/accounts/transfer")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.body("message", Matchers.equalTo("Transfer successful"))
				.body("senderAccountId", Matchers.equalTo(idAccountFirstUser))
				.body("receiverAccountId", Matchers.equalTo(idAccountSecondUser))
				.body("amount", Matchers.equalTo((float)50));

	}

	@Test
	@Tag("positive")
	@DisplayName("Пользователь может отслеживать состояние своих учетных записей")
	public void userCanSeeTrackingOfTheirAccounts(){
		// создаем пользователя
		String randomUser = UserHelper.createUser();

		// берем токен
		String userToken = UserHelper.getToken(randomUser);
		// создаем 2 счета
		//1-ый счет
		Response response1 = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(201)
				.extract()
				.response();

		int idValue1 = response1.jsonPath().getInt("id");

		// 2-ой счет
		Response response2 = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(201)
				.extract()
				.response();

		int idValue2 = response2.jsonPath().getInt("id");

		// пополняем первый счет
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.body(String.format("""
						{
						  "id": %d,
						  "balance": 4999
						}
						""",idValue1))
				.when()
				.post("http://localhost:4111/api/v1/accounts/deposit")
				.then()
				.statusCode(HttpStatus.SC_OK);

		// переводим деньги с одного счета на другой
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.body(String.format("""
						{
						  "senderAccountId": %d,
						  "receiverAccountId": %d,
						  "amount": 50
						}
						""",idValue1, idValue2))
				.when()
				.post("http://localhost:4111/api/v1/accounts/transfer")
				.then()
				.statusCode(HttpStatus.SC_OK);

		// берем айди аккаунта по которому был перевод
		// делаем запрос на отслеживание транзакций по айди аккаунта
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.pathParam("id", idValue1)
				.when()
				.get("http://localhost:4111/api/v1/accounts/{id}/transactions")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.body("$", Matchers.hasItem(Matchers.allOf(
						Matchers.hasKey("id"),
						Matchers.hasKey("amount"),
						Matchers.hasKey("type"),
						Matchers.hasKey("timestamp"),
						Matchers.hasKey("relatedAccountId")
				)));
	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может отслеживать статус чужих аккаунтов")
	public void userCanSeeTrackingOfOtherAccounts(){
		// создаем юзера1 под которым будет отслеживать операции
		String randomUser = UserHelper.createUser();
		// берем токен
		String userToken = UserHelper.getToken(randomUser);

		// создаем юзера 2 у которого будем отслеживать операции
		String randomUser2 = UserHelper.createUser();
		// берем токен
		String userToken2 = UserHelper.getToken(randomUser2);

		// создаем счет ко второму юзеру
		Response response = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken2)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(201)
				.extract()
				.response();

		int idValueAccountUser2 = response.jsonPath().getInt("id");
		// запрашиваем отслеживание операций второго юзера под токеном первого юзера
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.TEXT)
				.header("Authorization", userToken)
				.pathParam("id", idValueAccountUser2)
				.when()
				.get("http://localhost:4111/api/v1/accounts/{id}/transactions")
				.then()
				.statusCode(HttpStatus.SC_FORBIDDEN)
				.body(Matchers.equalTo("You do not have permission to access this account"));
	}




}

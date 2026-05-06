package iteration_2;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
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
public class CreateDepositTest {


	@ParameterizedTest
	@Tag("positive")
	@DisplayName("Пользователь может создать депозит с суммой не более 5000 за раз и больше 0")
	@ValueSource(ints = {4999,5000})
	public void userCanCreateDepositWithValidSum(int deposit){
		// создаем пользователя
		String uniqueUsername = "User_" + UUID.randomUUID().toString().substring(0, 8);

		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", "Basic YWRtaW46YWRtaW4=")
				.body(String.format("""
						{
						  "username": "%s",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""",uniqueUsername ))
				.when()
				.post("http://localhost:4111/api/v1/admin/users")
				.then()
				.statusCode(HttpStatus.SC_CREATED);

		// 	CreateUserRequest createUserRequest = CreateUserRequest.builder()
		//				.username(RandomData.getUsername())
		//				.password(RandomData.getPassword())
		//				.role(UserRole.USER.toString())
		//				.build();
		//
		//		CreateUserResponse createUserResponse = new AdminCreateUserRequester(RequestSpecs.adminSpec(),
		//				ResponseSpecs.entityWasCreated())
		//				.post(createUserRequest).extract().as(CreateUserResponse.class);


		// получаем токен пользователя
		String userToken = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(String.format("""
						{
						  "username": "%s",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""", uniqueUsername))
				.when()
				.post("http://localhost:4111/api/v1/auth/login")
				.then()
				.statusCode(200)
				.extract()
				.header("Authorization");

		// создаем счет
		Response response = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(201)
				.extract()
				.response();

		int idValue = response.jsonPath().getInt("id");
		// переводим депозит на счет
		String requestBody = String.format("""
				{
						  "id": %d,
						  "balance": %d
						}
				""",idValue, deposit);
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.body(requestBody)
				.when()
				.post("http://localhost:4111/api/v1/accounts/deposit")
				.then()
				.statusCode(200)
				.body("id", Matchers.equalTo(idValue))
				.body("balance", Matchers.equalTo((float)deposit));

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
		String uniqueUsername = "User_" + UUID.randomUUID().toString().substring(0, 8);

		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", "Basic YWRtaW46YWRtaW4=")
				.body(String.format("""
						{
						  "username": "%s",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""",uniqueUsername ))
				.when()
				.post("http://localhost:4111/api/v1/admin/users")
				.then()
				.statusCode(HttpStatus.SC_CREATED);

		// получаем токен пользователя
		String userToken = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(String.format("""
						{
						  "username": "%s",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""", uniqueUsername))
				.when()
				.post("http://localhost:4111/api/v1/auth/login")
				.then()
				.statusCode(200)
				.extract()
				.header("Authorization");

		// создаем счет
		Response response = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(201)
				.extract()
				.response();

		int idValue = response.jsonPath().getInt("id");
		// переводим счет на депозит
		String requestBody = String.format("""
				{
						  "id": %d,
						  "balance": %d
						}
				""",idValue, deposit);
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.body(requestBody)
				.when()
				.post("http://localhost:4111/api/v1/accounts/deposit")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.body(Matchers.equalTo(error));
	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может переводить деньги на не существующий счет")
	public void userCantCreateDepositOnNonExistAccount(){
		// создаем пользователя
		String uniqueUsername = "User_" + UUID.randomUUID().toString().substring(0, 8);

		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", "Basic YWRtaW46YWRtaW4=")
				.body(String.format("""
						{
						  "username": "%s",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""",uniqueUsername ))
				.when()
				.post("http://localhost:4111/api/v1/admin/users")
				.then()
				.statusCode(HttpStatus.SC_CREATED);

		// получаем токен пользователя
		String userToken = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(String.format("""
						{
						  "username": "%s",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""", uniqueUsername))
				.when()
				.post("http://localhost:4111/api/v1/auth/login")
				.then()
				.statusCode(200)
				.extract()
				.header("Authorization");
		// переводим депозит
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.TEXT)
				.header("Authorization", userToken)
				.body("""
						{
						  "id": 10,
						  "balance": 100
						}
						""")
				.when()
				.post("http://localhost:4111/api/v1/accounts/deposit")
				.then()
				.body(Matchers.equalTo("Unauthorized access to account"))
				.statusCode(HttpStatus.SC_FORBIDDEN);

	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может переводить деньги на чужой счет")
	public void userCantCreateDepositOnAnotherAccount(){
		// создаем пользователя
		String uniqueUsername = "User_" + UUID.randomUUID().toString().substring(0, 8);

		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", "Basic YWRtaW46YWRtaW4=")
				.body(String.format("""
						{
						  "username": "%s",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""",uniqueUsername ))
				.when()
				.post("http://localhost:4111/api/v1/admin/users")
				.then()
				.statusCode(HttpStatus.SC_CREATED);

		// получаем токен пользователя
		String userToken = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(String.format("""
						{
						  "username": "%s",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""", uniqueUsername))
				.when()
				.post("http://localhost:4111/api/v1/auth/login")
				.then()
				.statusCode(200)
				.extract()
				.header("Authorization");

		// создаем второго пользователя
		String uniqueUsername2 = "User_" + UUID.randomUUID().toString().substring(0, 8);
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", "Basic YWRtaW46YWRtaW4=")
				.body(String.format("""
						{
						  "username": "%s",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""",uniqueUsername2 ))
				.when()
				.post("http://localhost:4111/api/v1/admin/users")
				.then()
				.statusCode(HttpStatus.SC_CREATED);

		// получаем токен пользователя
		String userToken2 = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(String.format("""
						{
						  "username": "%s",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""", uniqueUsername2))
				.when()
				.post("http://localhost:4111/api/v1/auth/login")
				.then()
				.statusCode(200)
				.extract()
				.header("Authorization");
		// создаем счет у второго пользователя
		Response response = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken2)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(HttpStatus.SC_CREATED)
				.extract()
				.response();
		int idAccountUser2 = response.jsonPath().getInt("id");
		// переводим депозит под токеном первого пользователя на второй
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.TEXT)
				.header("Authorization", userToken)
				.body(String.format("""
						{
						  "id": %d,
						  "balance": 100
						}
						""", idAccountUser2))
				.when()
				.post("http://localhost:4111/api/v1/accounts/deposit")
				.then()
				.body(Matchers.equalTo("Unauthorized access to account"))
				.statusCode(HttpStatus.SC_FORBIDDEN);

	}





}

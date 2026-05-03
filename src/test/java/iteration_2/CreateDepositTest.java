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
import static iteration_2.Constants.BALANCE;
import static iteration_2.Constants.ID;
import static iteration_2.MessageForCreateDepositClass.*;

// Депозит денег пользователем
// Депозит (Deposit):
//— Максимальная сумма: 5000
//— Сумма должна быть положительной
//— Нельзя делать депозит в чужой аккаунт или несуществующий
@DisplayName("Тесты на проверку возможности создания Депозита")
public class CreateDepositTest extends LoggerClass {

	@ParameterizedTest
	@Tag("positive")
	@DisplayName("Пользователь может создать депозит с суммой не более 5000 за раз и больше 0")
	@ValueSource(ints = {4999,5000})
	public void userCanCreateDepositWithValidSum(int deposit){
		// создаем пользователя
		String uniqueUsername = UserHelper.createUser();

		// получаем токен пользователя
		String userToken = UserHelper.getToken(uniqueUsername);

		// создаем счет
		int idValue = AccountHelper.createAccount(userToken);
		// переводим депозит на счет
		Response response = AccountHelper.createDeposit(userToken, idValue, deposit);

		response.then()
				.statusCode(200)
				.body(ID, Matchers.equalTo(idValue))
				.body(BALANCE, Matchers.equalTo((float)deposit));

	}


	public static Stream<Arguments> notValidSum(){
		return Stream.of(
				Arguments.of(5001, MESSAGE_FOR_SC400_EXCEED5000),
				Arguments.of(-1, MESSAGE_FOR_SC400_LEAST01),
				Arguments.of(0, MESSAGE_FOR_SC400_LEAST01)
		);
	}

	@ParameterizedTest
	@Tag("negative")
	@DisplayName("Пользователь не может создать депозит с суммой более 5000 за раз и меньше 0")
	@MethodSource("notValidSum")
	public void userCantCreateDepositWithNotValidSum(int deposit, String error){
		// создаем пользователя
		String uniqueUsername = UserHelper.createUser();

		// получаем токен пользователя
		String userToken = UserHelper.getToken(uniqueUsername);

		// создаем счет
		int idValue = AccountHelper.createAccount(userToken);
		// переводим счет на депозит

		Response response = AccountHelper.createDeposit(userToken, idValue, deposit);
		response.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.body(Matchers.equalTo(error));
	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может переводить деньги на не существующий счет")
	public void userCantCreateDepositOnNonExistAccount(){
		// создаем пользователя
		String uniqueUsername = UserHelper.createUser();

		// получаем токен пользователя
		String userToken = UserHelper.getToken(uniqueUsername);
		// переводим депозит


		Response response = AccountHelper.createDeposit(userToken,10, 100);
		response.then()
				.body(Matchers.equalTo(MESSAGE_FOR_SC403_UNAUTHORIZED))
				.statusCode(HttpStatus.SC_FORBIDDEN);

	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может переводить деньги на чужой счет")
	public void userCantCreateDepositOnAnotherAccount(){
		// создаем пользователя
		String uniqueUsername = UserHelper.createUser();

		// получаем токен пользователя
		String userToken = UserHelper.getToken(uniqueUsername);

		// создаем второго пользователя
		String uniqueUsername2 = UserHelper.createUser();

		// получаем токен пользователя
		String userToken2 = UserHelper.getToken(uniqueUsername2);

		// создаем счет у второго пользователя

		int idAccountUser2 = AccountHelper.createAccount(userToken2);
		// переводим депозит под токеном первого пользователя на второй

		Response response = AccountHelper.createDeposit(userToken, idAccountUser2, 100);
		response.then()
				.body(Matchers.equalTo(MESSAGE_FOR_SC403_UNAUTHORIZED))
				.statusCode(HttpStatus.SC_FORBIDDEN);

	}





}

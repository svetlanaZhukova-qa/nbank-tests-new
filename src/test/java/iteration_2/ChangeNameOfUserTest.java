package iteration_2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static io.restassured.RestAssured.given;
import static iteration_2.ApiEndpoints.GET_PROFILE;
import static iteration_2.Constants.*;
import static iteration_2.MessagesForChangeOfUserNameClass.MESSAGE_FOR_UPDATE_200_OK;
import static iteration_2.MessagesForChangeOfUserNameClass.MESSAGE_FOR_UPDATE_400_BR;

// Изменение имени пользователя
// Имя в профиле (name):
//— Два слова, состоящее из букв, разделенные пробелом
@DisplayName("Тесты на возможность изменить имя профиля пользователем")
public class ChangeNameOfUserTest extends LoggerClass  {

	@Test
	@Tag("positive")
	@DisplayName("Пользователь может видеть информацию о своем профиле")
	public void userCanSeeInfoAboutTheirProfile(){
		// создаем пользователя
		String uniqueUsername = UserHelper.createUser();

		// получаем токен пользователя
		String userToken = UserHelper.getToken(uniqueUsername);
		// запрашиваем информацию о профиле
		RequestSpec.getBaseSpec()
				.header(HEADER_AUTHORIZATION, userToken)
				.when()
				.get("http://localhost:4111/api/v1/customer/profile")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.body("$",Matchers.allOf(
				Matchers.hasKey(ID),
				Matchers.hasKey(USER_NAME),
				Matchers.hasKey(PASSWORD),
				Matchers.hasKey(NAME),
				Matchers.hasKey(ROLE),
				Matchers.hasKey(ACCOUNTS)
		));

	}

	@ParameterizedTest
	@Tag("positive")
	@ValueSource(strings = {"S s", "Svetlana s"})
	@DisplayName("Пользователь может меня свое имя в профиле")
	public void userCanChangeTheirNameWithCorrectData(String name){
		// создаем пользователя
		String uniqueUsername = UserHelper.createUser();

		// получаем токен пользователя
		String userToken = UserHelper.getToken(uniqueUsername);
		// меняем имя
		RequestSpec.getBaseSpec()
				.header(HEADER_AUTHORIZATION, userToken)
				.body(String.format("""
						{
						  "name": "%s"
						}
						""", name))
				.when()
				.put(GET_PROFILE)
				.then()
				.statusCode(200)
				.body(MESSAGE, Matchers.equalTo(MESSAGE_FOR_UPDATE_200_OK))
				.body(CUSTOMER, Matchers.allOf(
						Matchers.hasEntry(USER_NAME, uniqueUsername),
						Matchers.hasEntry(NAME, name),
						Matchers.hasEntry(ROLE, "USER")
				));

	}

	@ParameterizedTest
	@Tag("negative")
	@ValueSource(strings = {"S ", "S  !", "   2  2  ", "", "ы№№№ о;;;;"})
	@DisplayName("Пользователь не может меня свое имя в профиле с некорректными данными")
	public void userCantChangeTheirNameWithNotCorrectData(String name){
		// создаем пользователя
		String uniqueUsername = UserHelper.createUser();

		// получаем токен пользователя
		String userToken = UserHelper.getToken(uniqueUsername);
		// меняем имя
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.TEXT)
				.header(HEADER_AUTHORIZATION, userToken)
				.body(String.format("""
						{
						  "name": "%s"
						}
						""", name))
				.when()
				.put(GET_PROFILE)
				.then()
				.statusCode(400)
				.body(Matchers.equalTo(MESSAGE_FOR_UPDATE_400_BR));


	}



}

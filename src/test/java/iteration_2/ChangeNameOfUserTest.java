package iteration_2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;

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
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.when()
				.get("http://localhost:4111/api/v1/customer/profile")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.body("$",Matchers.allOf(
				Matchers.hasKey("id"),
				Matchers.hasKey("username"),
				Matchers.hasKey("password"),
				Matchers.hasKey("name"),
				Matchers.hasKey("role"),
				Matchers.hasKey("accounts")
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
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.body(String.format("""
						{
						  "name": "%s"
						}
						""", name))
				.when()
				.put("http://localhost:4111/api/v1/customer/profile")
				.then()
				.statusCode(200)
				.body("message", Matchers.equalTo("Profile updated successfully"))
				.body("customer", Matchers.allOf(
						Matchers.hasEntry("username", uniqueUsername),
						Matchers.hasEntry("name", name),
						Matchers.hasEntry("role", "USER")
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
				.header("Authorization", userToken)
				.body(String.format("""
						{
						  "name": "%s"
						}
						""", name))
				.when()
				.put("http://localhost:4111/api/v1/customer/profile")
				.then()
				.statusCode(400)
				.body(Matchers.equalTo("Name must contain two words with letters only"));


	}



}

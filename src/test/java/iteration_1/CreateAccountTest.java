package iteration_1;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class CreateAccountTest {

	@BeforeAll
	public static void setUpRestAssured(){
		RestAssured.filters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
	}

	@Test
	@Tag("positive")
	public void userCanCreateAccount(){
		// создание пользователя
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", "Basic YWRtaW46YWRtaW4=")
				.body("""
						{
						  "username": "kate19987",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""")
				.when()
				.post("http://localhost:4111/api/v1/admin/users")
				.then()
				.statusCode(HttpStatus.SC_CREATED);


		// получаем токен юзера
		String userHeader = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body("""
						{
						  "username": "kate19987",
						  "password": "verysTRongPassword33$"
						}
						
						""")
				.when()// не обязательно. Код сработает без этого
				.post("http://localhost:4111/api/v1/auth/login")
				.then()
				//.statusCode(200)
				.assertThat()// не обязательно. Код сработает без этого
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.header("Authorization");

		// создаем аккаунт
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userHeader)
				.when()
				.post("http://localhost:4111/api/v1/accounts")
				.then()
				.statusCode(HttpStatus.SC_CREATED);

	}

}

package iteration_1;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class LoginUserTest {

	@BeforeAll
	public static void setUpRestAssured(){
		RestAssured.filters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
	}

	@Test
	@Tag("positive")
	public void adminCanGenerateAuthTokenTest(){
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body("""
						{
						  "username": "admin",
						  "password": "admin"
						}
						
						""")
				.when()// не обязательно. Код сработает без этого
				.post("http://localhost:4111/api/v1/auth/login")
				.then()
				//.statusCode(200)
				.assertThat()// не обязательно. Код сработает без этого
				.statusCode(HttpStatus.SC_OK)
				.header("Authorization", "Basic YWRtaW46YWRtaW4=");

	}

	@Test
	@Tag("positive")
	public void userCanGenerateAuthTokenTest(){
		// создание пользователя
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", "Basic YWRtaW46YWRtaW4=")
				.body("""
						{
						  "username": "kate19981",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""")
				.when()
				.post("http://localhost:4111/api/v1/admin/users")
						.then()
								.statusCode(HttpStatus.SC_CREATED);


		// проверка возможности логиниться юзером
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body("""
						{
						  "username": "kate19981",
						  "password": "verysTRongPassword33$"
						}
						
						""")
				.when()// не обязательно. Код сработает без этого
				.post("http://localhost:4111/api/v1/auth/login")
				.then()
				//.statusCode(200)
				.assertThat()// не обязательно. Код сработает без этого
				.statusCode(HttpStatus.SC_OK)
				.header("Authorization", Matchers.notNullValue());

	}
}

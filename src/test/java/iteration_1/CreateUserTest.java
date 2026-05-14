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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {

	@BeforeAll
	public static void setUpRestAssured(){
		RestAssured.filters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
	}

	@Test
	@Tag("positive")
	public void adminCanCreateUserWithCorrectDate(){
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", "Basic YWRtaW46YWRtaW4=")
				.body("""
						{
						  "username": "kate19984",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""")
				.when()
				.post("http://localhost:4111/api/v1/admin/users")
				.then()
				.statusCode(HttpStatus.SC_CREATED)
				.contentType(ContentType.JSON)
				.body("username", Matchers.equalTo("kate19984"))
				.body("password", Matchers.not(Matchers.equalTo("verysTRongPassword33$")))
				.body("role", Matchers.equalTo("USER"))
				;
	}

	@Test
	@Tag("negative")
	public void adminCantCreateExistUser(){
		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", "Basic YWRtaW46YWRtaW4=")
				.body("""
						{
						  "username": "kate19984",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""")
				.when()
				.post("http://localhost:4111/api/v1/admin/users")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)

		;
	}
// [Username cannot be blank, Username must contain only letters, digits, dashes, underscores, and dots, Username must be between 3 and 15 characters]
// <[Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long, Password cannot be blank]>
	public static Stream<Arguments> userInvalidData(){
		return Stream.of(
				// username field validation
				Arguments.of(" ", "verysTRongPassword33$", "USER", "username", "Username cannot be blank" ),
				Arguments.of("re","verysTRongPassword33$", "USER" , "username", "Username must be between 3 and 15 characters"),
				Arguments.of("swwwwwertgbnhytr","verysTRongPassword33$", "USER", "username", "Username must be between 3 and 15 characters"),
				Arguments.of("$%^&*()@#","verysTRongPassword33$", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots"),
//				// password field validation
				Arguments.of("kate19985","", "USER", "password", "Password cannot be blank" ),
				Arguments.of("kate19985","veTd33$", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long" )
		);
	}

	@ParameterizedTest
	@MethodSource("userInvalidData")
	@Tag("negative")
	public void adminCantCreateUserWithInvalidData(String username, String password, String role, String errorKey, String errorValue){
		String requestBody = String.format("""
				{
						  "username": "%s",
						  "password": "%s",
						  "role": "%s"
						}
				""", username, password, role);

		given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", "Basic YWRtaW46YWRtaW4=")
				.body(requestBody)
				.when()
				.post("http://localhost:4111/api/v1/admin/users")
				.then()
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.body(errorKey, Matchers.hasItem(errorValue))

		;

	}
}

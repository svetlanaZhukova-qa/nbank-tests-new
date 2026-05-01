package iteration_2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class UserHelper {
	private static  String uniqueUsername;
	public static String createUser(){
		// создаем пользователя
		 uniqueUsername = "User_" + UUID.randomUUID().toString().substring(0, 8);

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
		return uniqueUsername;
	}
	public static String getToken(){
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
		return userToken;
	}
}

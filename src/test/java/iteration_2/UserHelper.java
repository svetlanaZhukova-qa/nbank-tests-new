package iteration_2;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static iteration_2.Constants.HEADER_AUTHORIZATION;

public class UserHelper {

	private UserHelper() {
		throw new IllegalStateException("Utility class");
	}


	public static String createUser(){
		// создаем пользователя
		String uniqueUsername = "User_" + UUID.randomUUID().toString().substring(0, 8);

		RequestSpec.getBaseSpec()
				.header(HEADER_AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
				.body(String.format("""
						{
						  "username": "%s",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""",uniqueUsername ))
				.when()
				.post(ApiEndpoints.CREATE_USERS)
				.then()
				.statusCode(HttpStatus.SC_CREATED);
		return uniqueUsername;
	}

	public static String getToken(String userName){
		String userToken = RequestSpec.getBaseSpec()
				.body(String.format("""
						{
						  "username": "%s",
						  "password": "verysTRongPassword33$",
						  "role": "USER"
						}
						""", userName))
				.when()
				.post(ApiEndpoints.GET_TOKEN)
				.then()
				.statusCode(200)
				.extract()
				.header(HEADER_AUTHORIZATION);
		return userToken;
	}




}

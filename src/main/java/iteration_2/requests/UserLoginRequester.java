package iteration_2.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_2.models_body_JSON.UserLoginAndGetTokenRequest;

import static io.restassured.RestAssured.given;

public class UserLoginRequester extends Request<UserLoginAndGetTokenRequest>{
	public UserLoginRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
		super(requestSpecification, responseSpecification);
	}

	@Override
	public ValidatableResponse postApi(UserLoginAndGetTokenRequest baseModel) {
		// 	// получаем токен пользователя
		// 	String userToken = given()
		//				.contentType(ContentType.JSON)
		//				.accept(ContentType.JSON)//"role": "USER"
		//				.body(String.format("""
		//						{
		//						  "username": "%s",
		//						  "password": "%s"
		//						}
		//						""", createUserRequest.getUsername(), createUserRequest.getPassword()))
		//				.when()
		//				.post("http://localhost:4111/api/v1/auth/login")
		//				.then()
		//				.statusCode(200)
		//				.extract()
		//				.header("Authorization");

		return given()
				.spec(requestSpecification)
				.body(baseModel)
				.post("/api/v1/auth/login")
				.then()
				.assertThat()
				.spec(responseSpecification);

	}
}

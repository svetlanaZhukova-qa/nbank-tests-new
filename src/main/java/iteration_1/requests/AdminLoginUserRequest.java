package iteration_1.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_1.models.LoginUserRequest;

import static io.restassured.RestAssured.given;

public class AdminLoginUserRequest extends Request<LoginUserRequest>{
	public AdminLoginUserRequest(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
		super(requestSpecification, responseSpecification);
	}

	@Override
	public ValidatableResponse post(LoginUserRequest baseModel) {
		return
				given()
						.spec(requestSpecification)

						.body(baseModel)
						.when()// не обязательно. Код сработает без этого
						.post("http://localhost:4111/api/v1/auth/login")
						// Параметр 3: эдпоинт
						.then()
						//.statusCode(200)
						.assertThat()//
						// не обязательно. Код сработает без этого
						// Параметр 4: Спецификация ответа ( статус код, хедеры)
						.spec(responseSpecification);
	}
}

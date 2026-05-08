package iteration_2.requests;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_2.models_body_JSON.CreateUserRequest;
import org.apache.http.HttpStatus;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class AdminCreateUserRequester extends Request<CreateUserRequest>{
	public AdminCreateUserRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
		super(requestSpecification, responseSpecification);
	}

	@Override
	public ValidatableResponse postApi(CreateUserRequest baseModel) {
		return
				// создаем пользователя
				given()
				.spec(requestSpecification)
				.body(baseModel)
				.post("api/v1/admin/users")
				.then().assertThat()
				.spec(responseSpecification);

	}
}

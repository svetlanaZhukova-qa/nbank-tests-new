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
			// получаем токен пользователя

		return given()
				.spec(requestSpecification)
				.body(baseModel)
				.post("/api/v1/auth/login")
				.then()
				.assertThat()
				.spec(responseSpecification);

	}

	/**
	 * Этот метод не реализован, так как GET-запрос не требуется для текущей логики.
	 * Реализация будет добавлена позже, если появится соответствующая задача.
	 */
	@Deprecated
	@Override
	public ValidatableResponse getApi(int id) {
		// TODO: Реализовать логику GET-запроса, когда потребуется
		return null;
	}

	@Deprecated
	@Override
	public ValidatableResponse getApi() {
		return null;
	}


}

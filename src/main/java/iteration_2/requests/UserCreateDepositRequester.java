package iteration_2.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_2.models_body_JSON.CreateDepositRequest;

import static io.restassured.RestAssured.given;

public class UserCreateDepositRequester extends Request<CreateDepositRequest>{
	public UserCreateDepositRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
		super(requestSpecification, responseSpecification);
	}

	@Override
	public ValidatableResponse postApi(CreateDepositRequest baseModel) {
		return given()
				.spec(requestSpecification)
				.body(baseModel)
				.post("/api/v1/accounts/deposit")
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

	@Deprecated
	@Override
	public ValidatableResponse putApi(CreateDepositRequest baseModel) {
		return null;
	}

}

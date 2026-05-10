package iteration_2.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_2.models_body_JSON.BaseModel;

import static io.restassured.RestAssured.given;

public class UserLookTransferRequester extends Request{
	public UserLookTransferRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
		super(requestSpecification, responseSpecification);
	}

	/**
	 * Метод не реализован на бэкэнде
	 * @param baseModel
	 * @return
	 */
	@Deprecated
	@Override
	public ValidatableResponse postApi(BaseModel baseModel) {
		return null;
	}

	@Override
	public ValidatableResponse getApi(int id) {
		return given()
				.spec(requestSpecification)
				.pathParam("id", id)
				.get("/api/v1/accounts/{id}/transactions")
				.then()
				.assertThat()
				.spec(responseSpecification);

	}
}

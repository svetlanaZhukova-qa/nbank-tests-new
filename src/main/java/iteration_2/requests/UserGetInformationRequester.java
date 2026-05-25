package iteration_2.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_2.models_body_JSON.BaseModel;

import static io.restassured.RestAssured.given;

public class UserGetInformationRequester extends Request{
	public UserGetInformationRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
		super(requestSpecification, responseSpecification);
	}

	@Deprecated
	@Override
	public ValidatableResponse postApi(BaseModel baseModel) {
		return null;
	}

	@Deprecated
	@Override
	public ValidatableResponse getApi(int id) {
		return null;
	}

	@Override
	public ValidatableResponse getApi() {
		return given()
				.spec(requestSpecification)
				.get("/api/v1/customer/profile")
				.then()
				.assertThat()
				.spec(responseSpecification);
	}

	@Override
	public ValidatableResponse putApi(BaseModel baseModel) {
		return null;
	}
}

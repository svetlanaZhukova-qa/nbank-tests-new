package iteration_2.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_2.models_body_JSON.InfoPutUserRequest;

import static io.restassured.RestAssured.given;

public class UserPutInformationRequester extends Request<InfoPutUserRequest>{
	public UserPutInformationRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
		super(requestSpecification, responseSpecification);
	}

	@Deprecated
	@Override
	public ValidatableResponse postApi(InfoPutUserRequest baseModel) {
		return null;
	}

	@Deprecated
	@Override
	public ValidatableResponse getApi(int id) {
		return null;
	}

	@Deprecated
	@Override
	public ValidatableResponse getApi() {
		return null;
	}

	@Override
	public ValidatableResponse putApi(InfoPutUserRequest baseModel) {
		return given()
				.spec(requestSpecification)
				.body(baseModel)
				.put("/api/v1/customer/profile")
				.then()
				.assertThat()
				.spec(responseSpecification);
	}
}

package iteration_2.requests.skelethon.requesters;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_2.models_body_JSON.BaseModel;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.HttpRequest;
import iteration_2.requests.skelethon.interfaces.CrudEndpointInterface;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface {
	public CrudRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
		super(requestSpecification, responseSpecification, endpoint);
	}

	@Override
	public ValidatableResponse post(BaseModel baseModel) {
		var body = baseModel == null ? "" : baseModel;
		return given()
				.spec(requestSpecification)
				.body(body)
				.post(endpoint.getUrl())
				.then()
				.assertThat()
				.spec(responseSpecification);
	}

	@Override
	public ValidatableResponse getWithParams(int id) {
		return given()
				.spec(requestSpecification)
				.pathParam("id", id)
				.get(endpoint.getUrl())
				.then()
				.assertThat()
				.spec(responseSpecification);
	}

	@Override
	public ValidatableResponse get() {
		return given()
				.spec(requestSpecification)
				.get(endpoint.getUrl())
				.then()
				.assertThat()
				.spec(responseSpecification);
}

	@Override
	public ValidatableResponse update(BaseModel baseModel) {
		return given()
				.spec(requestSpecification)
				.body(baseModel)
				.put(endpoint.getUrl())
				.then()
				.assertThat()
				.spec(responseSpecification);
	}

	@Override
	public Object delete(long id) {
		return null;
	}
}

package api.iteration_2.requests.skelethon.requesters;

import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserResponse;
import api.iteration_2.requests.skelethon.interfaces.GetAllEndpointInterface;
import api.iteration_2.specs.RequestSpecs;
import common.helpers.StepLogger;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.iteration_2.models_body_JSON.BaseModel;
import api.iteration_2.requests.skelethon.Endpoint;
import api.iteration_2.requests.skelethon.HttpRequest;
import api.iteration_2.requests.skelethon.interfaces.CrudEndpointInterface;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface, GetAllEndpointInterface {
	public CrudRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
		super(requestSpecification, responseSpecification, endpoint);
	}

	@Override
	public ValidatableResponse post(BaseModel baseModel) {
	return 	StepLogger.log("POST request from " + endpoint.getUrl(), () -> {var body = baseModel == null ? "" : baseModel;
			return given()
					.spec(requestSpecification)
					.body(body)
					.post(endpoint.getUrl())
					.then()
					.assertThat()
					.spec(responseSpecification);});
	}

	@Override
	public ValidatableResponse getWithParams(int id) {
		return StepLogger.log("GET request with params id from " + endpoint.getUrl(), () -> {return given()
				.spec(requestSpecification)
				.pathParam("id", id)
				.get(endpoint.getUrl())
				.then()
				.assertThat()
				.spec(responseSpecification);});
	}

	@Override
	public ValidatableResponse get() {
		return StepLogger.log("GET request from " + endpoint.getUrl(), () -> {return given()
				.spec(requestSpecification)
				.get(endpoint.getUrl())
				.then()
				.assertThat()
				.spec(responseSpecification);});

}

	@Override
	public ValidatableResponse update(BaseModel baseModel) {
		return StepLogger.log("PUT request from " + endpoint.getUrl(), () -> {return given()
				.spec(requestSpecification)
				.body(baseModel)
				.put(endpoint.getUrl())
				.then()
				.assertThat()
				.spec(responseSpecification);});

	}

	@Override
	public Object delete(long id) {
		return null;
	}

	@Override
	public ValidatableResponse getAll(Class<?> clazz) {
	return given()
				.spec(requestSpecification)
				.get(endpoint.getUrl())
				.then().assertThat()
				.spec(responseSpecification);
	}
}

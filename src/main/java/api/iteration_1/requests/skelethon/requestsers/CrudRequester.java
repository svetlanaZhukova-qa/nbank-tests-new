package api.iteration_1.requests.skelethon.requestsers;

import api.iteration_1.requests.skelethon.interfaces.GetAllEndpoint;
import common.helpers.StepLogger;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.iteration_1.models.BaseModel;
import api.iteration_1.requests.skelethon.Endpoint;
import api.iteration_1.requests.skelethon.HttpRequest;
import api.iteration_1.requests.skelethon.interfaces.CRUDEndpointInterface;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CRUDEndpointInterface, GetAllEndpoint {
	public CrudRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
		super(requestSpecification, responseSpecification, endpoint);
	}

	@Override
	public ValidatableResponse post(BaseModel baseModel) {
		return StepLogger.log("Post request to " + endpoint.getUrl(), () -> {
			var body = baseModel == null ? "" : baseModel;
			return  given()
					.spec(requestSpecification)
					.body(body)
					.post(endpoint.getUrl())
					.then()
					.assertThat()
					.spec(responseSpecification);
		});

	}

	@Override
	public Object get(long id) {
		return null;
	}

	@Override
	public Object update(long id, BaseModel baseModel) {
		return null;
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

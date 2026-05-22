package iteration_2.requests.skelethon.requesters;

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
	public Object post(BaseModel baseModel) {
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
}

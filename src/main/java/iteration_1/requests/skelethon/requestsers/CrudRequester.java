package iteration_1.requests.skelethon.requestsers;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_1.models.BaseModel;
import iteration_1.requests.skelethon.Endpoint;
import iteration_1.requests.skelethon.HttpRequest;
import iteration_1.requests.skelethon.interfaces.CRUDEndpointInterface;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CRUDEndpointInterface{
	public CrudRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
		super(requestSpecification, responseSpecification, endpoint);
	}

	@Override
	public ValidatableResponse post(BaseModel baseModel) {
		var body = baseModel == null ? "" : baseModel;
		return  given()
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

package iteration_1.requests.skelethon.requestsers;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_1.models.BaseModel;
import iteration_1.requests.skelethon.Endpoint;
import iteration_1.requests.skelethon.HttpRequest;
import iteration_1.requests.skelethon.interfaces.CRUDEndpointInterface;

public class ValidateCRUDRequester<T extends BaseModel> extends HttpRequest implements CRUDEndpointInterface {
	private CrudRequester crudRequester;
	public ValidateCRUDRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
		super(requestSpecification, responseSpecification, endpoint);
		this.crudRequester = new CrudRequester(requestSpecification, responseSpecification, endpoint);
	}

	@Override
	public T post(BaseModel baseModel) {
		return (T) crudRequester.post(baseModel).extract().as(endpoint.getResponseModel());
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

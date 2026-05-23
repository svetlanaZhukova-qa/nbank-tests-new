package iteration_2.requests.skelethon.requesters;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_2.models_body_JSON.BaseModel;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.HttpRequest;
import iteration_2.requests.skelethon.interfaces.CrudEndpointInterface;

public class ValidateCrudRequester2<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface {
	private CrudRequester crudRequester;
	public ValidateCrudRequester2(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
		super(requestSpecification, responseSpecification, endpoint);
		this.crudRequester = new CrudRequester(requestSpecification, responseSpecification, endpoint);
	}

	@Override
	public T post(BaseModel baseModel) {
		return (T) crudRequester.post(baseModel).extract().as(endpoint.getResponseModel());
	}

	@Override
	public T getWithParams(int id) {
		return (T) crudRequester.getWithParams(id).extract().as(endpoint.getResponseModel());
	}

	@Override
	public T get() {
		return (T) crudRequester.get().extract().as(endpoint.getResponseModel());
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

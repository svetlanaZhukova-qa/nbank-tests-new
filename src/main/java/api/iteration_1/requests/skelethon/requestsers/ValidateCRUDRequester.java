package api.iteration_1.requests.skelethon.requestsers;

import api.iteration_1.requests.skelethon.interfaces.GetAllEndpoint;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.iteration_1.models.BaseModel;
import api.iteration_1.requests.skelethon.Endpoint;
import api.iteration_1.requests.skelethon.HttpRequest;
import api.iteration_1.requests.skelethon.interfaces.CRUDEndpointInterface;

import java.util.Arrays;
import java.util.List;

public class ValidateCRUDRequester<T extends BaseModel> extends HttpRequest implements CRUDEndpointInterface, GetAllEndpoint {
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


	@Override
	public List<T> getAll(Class<?> clazz) {
		T[] array = (T[]) crudRequester.getAll(clazz).extract().as(clazz);
		return Arrays.asList(array);
	}
}

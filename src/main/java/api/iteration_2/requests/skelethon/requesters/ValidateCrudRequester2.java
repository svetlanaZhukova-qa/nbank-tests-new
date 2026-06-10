package api.iteration_2.requests.skelethon.requesters;

import api.iteration_2.requests.skelethon.interfaces.GetAllEndpointInterface;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.iteration_2.models_body_JSON.BaseModel;
import api.iteration_2.requests.skelethon.Endpoint;
import api.iteration_2.requests.skelethon.HttpRequest;
import api.iteration_2.requests.skelethon.interfaces.CrudEndpointInterface;

import java.util.Arrays;
import java.util.List;

public class ValidateCrudRequester2<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface, GetAllEndpointInterface {
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
	public T update(BaseModel baseModel) {
		return (T) crudRequester.update(baseModel).extract().as(endpoint.getResponseModel());
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

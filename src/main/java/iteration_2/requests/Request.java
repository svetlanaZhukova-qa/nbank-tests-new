package iteration_2.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_2.models_body_JSON.BaseModel;

public abstract class Request<T extends BaseModel> {
	protected RequestSpecification requestSpecification;
	protected ResponseSpecification responseSpecification;

	public Request(RequestSpecification requestSpecification, ResponseSpecification responseSpecification){
		this.requestSpecification = requestSpecification;
		this.responseSpecification = responseSpecification;
	}

	public abstract ValidatableResponse postApi(T baseModel);
	public abstract ValidatableResponse getApi(int id);
}

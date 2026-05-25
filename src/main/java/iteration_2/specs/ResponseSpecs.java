package iteration_2.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpecs {
	private ResponseSpecs(){}

	private static ResponseSpecBuilder defaultResponseSpecBuilder(){
		return new ResponseSpecBuilder();
	}

	public static ResponseSpecification entityWasCreated(){
		return defaultResponseSpecBuilder().expectStatusCode(201).build();
	}
	public static ResponseSpecification requestReturnOk(){
		return defaultResponseSpecBuilder().expectStatusCode(200).build();
	}

	public static ResponseSpecification requestReturnBadRequest(){
		return defaultResponseSpecBuilder().expectStatusCode(400).build();
	}

	public static ResponseSpecification requestReturnForbidden(){
		return defaultResponseSpecBuilder().expectStatusCode(403).build();
	}
}

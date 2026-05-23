package iteration_2.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.models_body_JSON.create_user_and_accont.UserLoginAndGetTokenRequest;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.CrudRequester;

import java.util.List;


public class RequestSpecs {
	private RequestSpecs(){}

	private static RequestSpecBuilder defaultRequestBuilder(){
		return new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON)
				.addFilters( List.of(new RequestLoggingFilter(),
				new ResponseLoggingFilter()))
				.setBaseUri("http://localhost:4111");
	}

	private static RequestSpecBuilder defaultRequestBuilderForAcceptTEXT(){
		return new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.TEXT)
				.addFilters( List.of(new RequestLoggingFilter(),
						new ResponseLoggingFilter()))
				.setBaseUri("http://localhost:4111");
	}



	public static RequestSpecification unAuthUserSpec(){
		return defaultRequestBuilder().build();
	}

	public static RequestSpecification adminSpec(){
		return defaultRequestBuilder().addHeader("Authorization", "Basic YWRtaW46YWRtaW4=").build();
	}

	public static RequestSpecification authUserSpec(String username, String password){

		String userToken = new CrudRequester(RequestSpecs.unAuthUserSpec(), ResponseSpecs.requestReturnOk(),
				Endpoint.LOGIN_USER)
				.post(UserLoginAndGetTokenRequest.builder().username(username).password(password).build())
				.extract()
				.header("Authorization");

		return defaultRequestBuilder().addHeader("Authorization", userToken)
				.build();

	}

	public static RequestSpecification authUserSpecForAcceptTEXT(String username, String password){

		String userToken = new CrudRequester(RequestSpecs.unAuthUserSpec(), ResponseSpecs.requestReturnOk(),
				Endpoint.LOGIN_USER)
				.post(UserLoginAndGetTokenRequest.builder().username(username).password(password).build())
				.extract()
				.header("Authorization");

		return defaultRequestBuilderForAcceptTEXT().addHeader("Authorization", userToken)
				.build();

	}




}

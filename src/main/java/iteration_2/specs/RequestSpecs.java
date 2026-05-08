package iteration_2.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import iteration_1.models.LoginUserRequest;
import iteration_1.requests.LoginUserRequester;
import iteration_2.models_body_JSON.UserLoginAndGetTokenRequest;
import iteration_2.requests.UserLoginRequester;

import java.util.List;

import static io.restassured.RestAssured.given;

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

		String userToken = new UserLoginRequester(RequestSpecs.unAuthUserSpec(), ResponseSpecs.requestReturnOk())
				.postApi(UserLoginAndGetTokenRequest.builder().username(username).password(password).build())
				.extract()
				.header("Authorization");

		return defaultRequestBuilder().addHeader("Authorization", userToken)
				.build();

	}

	public static RequestSpecification authUserSpecForAcceptTEXT(String username, String password){

		String userToken = new UserLoginRequester(RequestSpecs.unAuthUserSpec(), ResponseSpecs.requestReturnOk())
				.postApi(UserLoginAndGetTokenRequest.builder().username(username).password(password).build())
				.extract()
				.header("Authorization");

		return defaultRequestBuilderForAcceptTEXT().addHeader("Authorization", userToken)
				.build();

	}




}

package api.iteration_2.specs;

import api.configs.Config;
import api.iteration_1.models.LoginUserRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import api.iteration_2.models_body_JSON.create_user_and_accont.UserLoginAndGetTokenRequest;
import api.iteration_2.requests.skelethon.Endpoint;
import api.iteration_2.requests.skelethon.requesters.CrudRequester;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RequestSpecs {
	private static Map<String, String> authHeaders = new HashMap<>(Map.of("admin", "Basic YWRtaW46YWRtaW4="));

	private RequestSpecs(){}

	private static RequestSpecBuilder defaultRequestBuilder(){
		return new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON)
				.addFilters( List.of(new RequestLoggingFilter(),
				new ResponseLoggingFilter()))
				.setBaseUri(Config.getProperty("apiBaseUrl") + Config.getProperty("apiVersion"));
	}

	private static RequestSpecBuilder defaultRequestBuilderForAcceptTEXT(){
		return new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.TEXT)
				.addFilters( List.of(new RequestLoggingFilter(),
						new ResponseLoggingFilter()))
				.setBaseUri(Config.getProperty("apiBaseUrl") + Config.getProperty("apiVersion"));
	}



	public static RequestSpecification unAuthUserSpec(){
		return defaultRequestBuilder().build();
	}

	public static RequestSpecification adminSpec(){
		return defaultRequestBuilder()
				.addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
				.build();
	}

	public static RequestSpecification authUserSpec(String username, String password){


		return defaultRequestBuilder().addHeader("Authorization", getUserAuthHeader(username, password))
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

	public static String getUserAuthHeader(String username, String password) {
		String userAuthHeader;

		if (!authHeaders.containsKey(username)) {
			userAuthHeader = new CrudRequester(
					RequestSpecs.unAuthUserSpec(),
					ResponseSpecs.requestReturnOk(),
					Endpoint.LOGIN_USER)
					.post(UserLoginAndGetTokenRequest.builder().username(username).password(password).build())
					.extract()
					.header("Authorization");

			authHeaders.put(username, userAuthHeader);
		} else {
			userAuthHeader = authHeaders.get(username);
		}

		return userAuthHeader;
	}

}

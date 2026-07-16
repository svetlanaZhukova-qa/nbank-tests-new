package api.iteration_1.specs;

import api.configs.Config;
import com.github.viclovsky.swagger.coverage.FileSystemOutputWriter;
import com.github.viclovsky.swagger.coverage.SwaggerCoverageRestAssured;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import api.iteration_1.models.LoginUserRequest;
import api.iteration_1.requests.skelethon.Endpoint;
import api.iteration_1.requests.skelethon.requestsers.CrudRequester;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.viclovsky.swagger.coverage.SwaggerCoverageConstants.OUTPUT_DIRECTORY;

public class RequestSpecs {
	private static Map<String, String> authHeader = new HashMap<>( Map.of("admin", "Basic YWRtaW46YWRtaW4="));


	private RequestSpecs(){}

	private static RequestSpecBuilder defaultRequestBuilder() {
		return new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON)
				.addFilters( List.of(new RequestLoggingFilter(),
						new ResponseLoggingFilter(), new SwaggerCoverageRestAssured(
								new FileSystemOutputWriter(Paths.get("target/" + OUTPUT_DIRECTORY))), new AllureRestAssured())
				)
				.setBaseUri(Config.getProperty("apiBaseUrl"));
	}

	public static RequestSpecification unauthSpec() {
		return defaultRequestBuilder().build();
	}

	public static RequestSpecification adminSpec() {
		return defaultRequestBuilder()
				.addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
				.build();
	}

	public static RequestSpecification authAsUser(String username, String password) {
		String userAuthHeader ;
		if(!authHeader.containsKey(username)) {
			userAuthHeader =   new CrudRequester(
					RequestSpecs.unauthSpec(),
					ResponseSpecs.requestReturnsOK(), Endpoint.LOGIN_USER)
					.post(LoginUserRequest.builder().username(username).password(password).build())
					.extract()
					.header("Authorization");

			authHeader.put(username, userAuthHeader);
		} else {
			userAuthHeader = authHeader.get(username);
		}

		return defaultRequestBuilder()
				.addHeader("Authorization", userAuthHeader)
				.build();
	}
}
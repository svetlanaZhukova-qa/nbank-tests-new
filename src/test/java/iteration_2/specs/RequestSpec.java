package iteration_2.specs;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RequestSpec {
	private RequestSpec() {
		throw new IllegalStateException("Utility class");
	}
	public static RequestSpecification getBaseSpec(){
		return given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON);
	}
}

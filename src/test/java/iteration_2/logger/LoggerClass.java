package iteration_2.logger;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

public class LoggerClass {

	@BeforeAll
	public static void setUpRestAssured(){
		RestAssured.baseURI = "http://localhost:4111";
		RestAssured.filters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
	}
}

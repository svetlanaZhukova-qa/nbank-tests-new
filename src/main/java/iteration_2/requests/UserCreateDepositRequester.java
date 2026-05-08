package iteration_2.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_2.models_body_JSON.CreateDepositRequest;

import static io.restassured.RestAssured.given;

public class UserCreateDepositRequester extends Request<CreateDepositRequest>{
	public UserCreateDepositRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
		super(requestSpecification, responseSpecification);
	}

	@Override
	public ValidatableResponse postApi(CreateDepositRequest baseModel) {
		return given()
				.spec(requestSpecification)
				.body(baseModel)
				.post("/api/v1/accounts/deposit")
				.then()
				.assertThat()
				.spec(responseSpecification);
		//int idValue = createAccountResponse.getId();
		//
		//		// переводим депозит на счет
		//		String requestBody = String.format("""
		//				{
		//						  "id": %d,
		//						  "balance": %d
		//						}
		//				""",idValue, deposit);
		//		given()
		//				.contentType(ContentType.JSON)
		//				.accept(ContentType.JSON)
		//				.header("Authorization", userToken)
		//				.body(requestBody)
		//				.when()
		//				.post("http://localhost:4111/api/v1/accounts/deposit")
		//				.then()
		//				.statusCode(200)
		//				.body("id", Matchers.equalTo(idValue))
		//				.body("balance", Matchers.equalTo((float)deposit));
	}
}

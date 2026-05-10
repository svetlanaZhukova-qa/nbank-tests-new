package iteration_2.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_2.models_body_JSON.CreateDepositRequest;
import iteration_2.models_body_JSON.CreateTransferRequest;

import static io.restassured.RestAssured.given;

public class UserCreateTransferRequester extends Request<CreateTransferRequest>{
	public UserCreateTransferRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
		super(requestSpecification, responseSpecification);
	}

	@Override
	public ValidatableResponse postApi(CreateTransferRequest baseModel) {
		return given()
				.spec(requestSpecification)
				.body(baseModel)
				.post("/api/v1/accounts/transfer")
				.then()
				.assertThat()
				.spec(responseSpecification)
				;
		// переводим деньги с одного счета на другой
		//		given()
		//				.contentType(ContentType.JSON)
		//				.accept(ContentType.JSON)
		//				.header("Authorization", userToken)
		//				.body(String.format("""
		//						{
		//						  "senderAccountId": %d,
		//						  "receiverAccountId": %d,
		//						  "amount": %d
		//						}
		//						""",idValue1, idValue2,sum))
		//				.when()
		//				.post("http://localhost:4111/api/v1/accounts/transfer")
		//				.then()
		//				.statusCode(HttpStatus.SC_OK)
		//				.body("senderAccountId", Matchers.equalTo(idValue1))
		//				.body("receiverAccountId", Matchers.equalTo(idValue2))
		//				.body("message", Matchers.equalTo("Transfer successful"))
		//				.body("amount", Matchers.equalTo((float)sum));
	}

	/**
	 * Этот метод не реализован, так как GET-запрос не требуется для текущей логики.
	 * Реализация будет добавлена позже, если появится соответствующая задача.
	 */
	@Deprecated
	@Override
	public ValidatableResponse getApi(int id) {
		// TODO: Реализовать логику GET-запроса, когда потребуется
		return null;
	}

}

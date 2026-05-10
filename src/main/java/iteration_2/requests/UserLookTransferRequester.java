package iteration_2.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import iteration_2.models_body_JSON.BaseModel;

import static io.restassured.RestAssured.given;

public class UserLookTransferRequester extends Request{
	public UserLookTransferRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
		super(requestSpecification, responseSpecification);
	}

	/**
	 * Метод не реализован на бэкэнде
	 * @param baseModel
	 * @return
	 */
	@Deprecated
	@Override
	public ValidatableResponse postApi(BaseModel baseModel) {
		return null;
	}

	@Override
	public ValidatableResponse getApi(int id) {
		return given()
				.spec(requestSpecification)
				.pathParam("id", id)
				.get("/api/v1/accounts/{id}/transactions")
				.then()
				.assertThat()
				.spec(responseSpecification);
		//  делаем запрос на отслеживание транзакций по айди аккаунта
		//		given()
		//				.contentType(ContentType.JSON)
		//				.accept(ContentType.JSON)
		//				.header("Authorization", userToken)
		//				.pathParam("id", idValue1)
		//				.when()
		//				.get("http://localhost:4111/api/v1/accounts/{id}/transactions")
		//				.then()
		//				.statusCode(HttpStatus.SC_OK)
		//				.body("$", Matchers.hasItem(Matchers.allOf(
		//						Matchers.hasKey("id"),
		//						Matchers.hasKey("amount"),
		//						Matchers.hasKey("type"),
		//						Matchers.hasKey("timestamp"),
		//						Matchers.hasKey("relatedAccountId")
		//				)));
		//	}
	}
}

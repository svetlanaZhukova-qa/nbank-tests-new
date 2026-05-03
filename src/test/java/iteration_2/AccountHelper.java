package iteration_2;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AccountHelper {
	public static int createAccount(String userToken){
		Response response = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.when()
				.post(ApiEndpoints.CREATE_ACCOUNT)
				.then()
				.statusCode(201)
				.extract()
				.response();

		int idValue = response.jsonPath().getInt("id");
		return idValue;

	}

	public static Response createDeposit(String userToken, int idValueAccount, int deposit){
		String requestBody = String.format("""
				{
						  "id": %d,
						  "balance": %d
						}
				""",idValueAccount, deposit);
		return given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.body(requestBody)
				.when()
				.post(ApiEndpoints.CREATE_DEPOSIT);
	}

	public static Response createTransferMoney(String userToken,int idValueAccount1, int idValueAccount2, int sum ){
		return given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", userToken)
				.body(String.format("""
						{
						  "senderAccountId": %d,
						  "receiverAccountId": %d,
						  "amount": %d
						}
						""",idValueAccount1, idValueAccount2,sum))
				.when()
				.post(ApiEndpoints.CREATE_TRANSFER);
	}
}

package iteration_2.object_helper;

import io.restassured.response.Response;
import iteration_2.api.ApiEndpoints;
import iteration_2.specs.RequestSpec;

import static iteration_2.constants.Constants.HEADER_AUTHORIZATION;

public class AccountHelper {
	private AccountHelper() {
		throw new IllegalStateException("Utility class");
	}
	public static int createAccount(String userToken){
		Response response = RequestSpec.getBaseSpec()
				.header(HEADER_AUTHORIZATION, userToken)
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
		return RequestSpec.getBaseSpec()
				.header(HEADER_AUTHORIZATION, userToken)
				.body(requestBody)
				.when()
				.post(ApiEndpoints.CREATE_DEPOSIT);
	}

	public static Response createTransferMoney(String userToken,int idValueAccount1, int idValueAccount2, int sum ){
		return RequestSpec.getBaseSpec()
				.header(HEADER_AUTHORIZATION, userToken)
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

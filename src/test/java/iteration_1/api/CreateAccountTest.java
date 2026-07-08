package iteration_1.api;


import api.dao.AccountDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.iteration_1.models.CreateAccountResponse;
import api.iteration_1.models.CreateUserRequest;
import api.iteration_1.requests.skelethon.Endpoint;
import api.iteration_1.requests.skelethon.requestsers.ValidateCRUDRequester;
import api.iteration_1.requests.steps.AdminSteps;
import api.iteration_1.requests.steps.DataBaseSteps;
import api.iteration_1.specs.RequestSpecs;
import api.iteration_1.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CreateAccountTestApi extends BaseTest {

	@Test
	public void userCanCreateAccountTest() {
		CreateUserRequest userRequest = AdminSteps.createUser();

		CreateAccountResponse createAccountResponse = new ValidateCRUDRequester<CreateAccountResponse>
				(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
						ResponseSpecs.entityWasCreated(),
						Endpoint.ACCOUNTS)
				.post(null);

		// get- запрос на то, что аккаунт  создался
		List<CreateAccountResponse> accounts = new  ValidateCRUDRequester<CreateAccountResponse>(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
				ResponseSpecs.requestReturnsOK(),
				Endpoint.CUSTOMER_ACCOUNTS).getAll(CreateAccountResponse[].class);
		softly.assertThat(accounts.size() == 1);
		softly.assertThat(accounts.get(0).getAccountNumber().equals(createAccountResponse.getAccountNumber()));


		AccountDao accountDao = DataBaseSteps.getAccountByAccountNumber(createAccountResponse.getAccountNumber());

		DaoAndModelAssertions.assertThat(createAccountResponse, accountDao).match();


	}
}
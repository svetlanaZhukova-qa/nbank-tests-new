package iteration_1.ui;

import api.iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import api.iteration_2.requests.steps.AdminSteps;
import api.iteration_2.requests.steps.UserSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseUITest{
	@Test
	@UserSession
	public void userCanCreateAccountTest() {
		new UserDashboard().open().createNewAccount();

		List<CreateAccountResponse> createdAccounts = SessionStorage.getSteps()
				.getAllAccounts();

		assertThat(createdAccounts).hasSize(1);

		new UserDashboard().checkAlertMessageAndAccept
				(BankAlert.NEW_ACCOUNT_CREATED.format() + createdAccounts.getFirst().getAccountNumber());

		assertThat(createdAccounts.getFirst().getBalance()).isZero();
	}


}

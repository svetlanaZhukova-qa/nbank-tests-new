package iteration_1.ui;

import api.iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import api.iteration_2.requests.steps.AdminSteps;
import api.iteration_2.requests.steps.UserSteps;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseUITest{
	@Test
	public void userCanCreateAccountTest() {
		// ШАГИ ПО НАСТРОЙКЕ ОКРУЖЕНИЯ
		// ШАГ 1: админ логинится в банке
		// ШАГ 2: админ создает юзера
		// ШАГ 3: юзер логинится в банке

		CreateUserRequest user = AdminSteps.createUser();

		authAsUser(user);

		new UserDashboard().open().createNewAccount();


		List<CreateAccountResponse> createdAccounts = new UserSteps(user.getUsername(), user.getPassword())
				.getAllAccounts();

		assertThat(createdAccounts).hasSize(1);

		new UserDashboard().checkAlertMessageAndAccept
				(BankAlert.NEW_ACCOUNT_CREATED.format() + createdAccounts.getFirst().getAccountNumber());

		assertThat(createdAccounts.getFirst().getBalance()).isZero();
	}


}

package iteration_1.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import api.iteration_2.requests.steps.AdminSteps;
import common.annotations.Browsers;
import org.junit.jupiter.api.Test;
import ui.pages.AdminPanel;
import ui.pages.LoginPage;
import ui.pages.UserDashboard;



public class LoginUserTestUi extends BaseUITest {

	@Test
	@Browsers({"firefox"})
	public void adminCanLoginWithCorrectData(){
		CreateUserRequest admin = CreateUserRequest.getAdmin();

		new LoginPage().open().login(admin.getUsername(), admin.getPassword())
				.getPage(AdminPanel.class)
				.getAdminPanelText().shouldBe(Condition.visible);
		Selenide.sleep(20000);

	}

	@Test
	public void userCanLoginWithCorrectDataTest(){
		CreateUserRequest createUserRequest = AdminSteps.createUser();

		new LoginPage().open().login(createUserRequest.getUsername(), createUserRequest.getPassword())
				.getPage(UserDashboard.class)
				.getWelcomeText().shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));

	}
}

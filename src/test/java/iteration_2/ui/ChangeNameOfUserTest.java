package iteration_2.ui;

import api.iteration_2.generators.RandomData;
import api.iteration_2.models_body_JSON.change_name_user.InfoGetUserResponse;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import api.iteration_2.requests.steps.AdminSteps;
import api.iteration_2.requests.steps.GetUserInfo;
import iteration_1.ui.BaseUITest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тесты на возможность изменить имя профиля пользователем")
public class ChangeNameOfUserTest extends BaseUITest {
	@Test
	@Tag("positive")
	@DisplayName("Пользователь может меня свое имя в профиле.")
	public void userCanChangeTheirNameWithCorrectData(){
		// создаем пользователя и логинимся
		CreateUserRequest createUserRequest = AdminSteps.createUser();
		//authAsUser(createUserRequest);

		// меняем имя профиля
		String newName = RandomData.getRandomName();
		new UserDashboard().open().updateName(createUserRequest, newName)
				.checkAlertMessageAndAccept(BankAlert.NAME_UPDATE_SUCCESSFULLY);

		// проверяем что на API имя изменилось
		InfoGetUserResponse infoGetUserResponse = GetUserInfo.getInfo(createUserRequest);
		assertEquals(infoGetUserResponse.getName(), newName);

	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может меня свое имя в профиле с некорректными данными.")
	public void userCantChangeTheirNameWithNotCorrectData(){
		// создаем пользователя и логинимся
		CreateUserRequest createUserRequest = AdminSteps.createUser();
		//authAsUser(createUserRequest);

		// меняем имя профиля
		String newName = RandomData.getRandomPassword();
	    new UserDashboard().open().updateName(createUserRequest, newName)
				.checkAlertMessageAndAccept(BankAlert.FAILED_CHANGE_NAME);

		// проверяем что на API имя  не изменилось
		InfoGetUserResponse infoGetUserResponse = GetUserInfo.getInfo(createUserRequest);
		assertEquals(infoGetUserResponse.getName(), null);

	}
}

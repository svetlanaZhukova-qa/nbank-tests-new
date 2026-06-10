package iteration_1.ui;

import api.iteration_1.models.comparison.ModelAssertions;
import api.iteration_2.generators.RandomModelGenerator2Iteration;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserResponse;
import api.iteration_2.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import ui.pages.AdminPanel;
import ui.pages.BankAlert;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserTest extends BaseUITest{

	@Test
	public void adminCanCreateUser(){
		// логинимся под админом
		CreateUserRequest admin =CreateUserRequest.getAdmin();

	authAsUser(admin);
		// создаем пользователя
		CreateUserRequest newUser = RandomModelGenerator2Iteration.generate(CreateUserRequest.class);
		new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
				.checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY.getMessage())	// ШАГ 3: проверка, что алерт "✅ User created successfully!"
				.getAllUsers().findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldBe(Condition.visible);// ШАГ 4: проверка, что юзер отображается на UI

		// ШАГ 5: проверка, что юзер создан на API

		CreateUserResponse createdUser = AdminSteps.getAllUsers().stream().
				filter(user -> user.getUsername().equals(newUser.getUsername()))
				.findFirst().get();

		ModelAssertions.assertThatModels(newUser, createdUser).match();
	}

	@Test
	public void adminCannotCreateUserWithInvalidDataTest(){
		// логинимся под админом
		CreateUserRequest admin =CreateUserRequest.getAdmin();
		authAsUser(admin);

		// создаем пользователя
		CreateUserRequest newUser = RandomModelGenerator2Iteration.generate(CreateUserRequest.class);
		newUser.setUsername("a");
		new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
				.checkAlertMessageAndAccept(BankAlert.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.getMessage())// ШАГ 3: проверка, что алерт "✅ User created successfully!"
				.getAllUsers().findBy(Condition.exactText(newUser.getUsername() + "\nUSER")).shouldNotBe(Condition.exist);// ШАГ 4: проверка, что юзер не отображается на UI

		// ШАГ 5: проверка, что юзер не создан на API

		long usersWithSameUsernameAsNewUser = AdminSteps.getAllUsers().stream().filter(user -> user.getUsername().equals(newUser.getUsername())).count();

		assertThat(usersWithSameUsernameAsNewUser).isZero();
	}
}

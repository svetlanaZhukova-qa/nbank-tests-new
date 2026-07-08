package iteration_1.ui;

import api.iteration_1.generators.RandomModelGenerator;
import api.iteration_1.models.comparison.ModelAssertions;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserResponse;
import api.iteration_2.requests.steps.AdminSteps;
import common.annotations.AdminSession;
import org.junit.jupiter.api.Test;
import ui.elements.UserBage;
import ui.pages.AdminPanel;
import ui.pages.BankAlert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CreateUserTestUi extends BaseUITest{

	@Test
	@AdminSession
	public void adminCanCreateUser(){
		CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);

		UserBage newUserBage = new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
				.checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY)
				.findUserByUsername(newUser.getUsername());

		assertThat(newUserBage)
				.as("UserBage should exist on Dashboard after user creation").isNotNull();

		CreateUserResponse createdUser = AdminSteps.getAllUsers().stream()
				.filter(user -> user.getUsername().equals(newUser.getUsername()))
				.findFirst().get();

		ModelAssertions.assertThatModels(newUser, createdUser).match();
	}

	@Test
	@AdminSession
	public void adminCannotCreateUserWithInvalidDataTest(){
		CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);
		newUser.setUsername("a");

		assertTrue(new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
				.checkAlertMessageAndAccept(BankAlert.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS)
				.getAllUsers().stream().noneMatch(userBage -> userBage.getUsername().equals(newUser.getUsername())));

		long usersWithSameUsernameAsNewUser = AdminSteps.getAllUsers().stream()
				.filter(user -> user.getUsername().equals(newUser.getUsername())).count();

		assertThat(usersWithSameUsernameAsNewUser).isZero();
	}
}

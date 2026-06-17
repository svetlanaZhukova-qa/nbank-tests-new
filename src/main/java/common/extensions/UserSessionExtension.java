package common.extensions;

import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import api.iteration_2.requests.steps.AdminSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

import java.util.LinkedList;
import java.util.List;

public class UserSessionExtension implements BeforeEachCallback {
	@Override
	public void beforeEach(ExtensionContext extensionContext) throws Exception {
		// шаг 1: проверить, что у теста есть аннотация UserSession
		UserSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class);
		if(annotation != null){
			int userCount = annotation.value();
			SessionStorage.clear();

			List<CreateUserRequest> users = new LinkedList<>();

			for (int i = 0; i < userCount; i++) {
				CreateUserRequest user = AdminSteps.createUser();
				users.add(user);
			}

			SessionStorage.addUsers(users);

			int authAsUser = annotation.auth();

			BasePage.authAsUser(SessionStorage.getUser(authAsUser));

		}
	}
}

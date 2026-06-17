package common.extensions;

import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import common.annotations.AdminSession;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

public class AdminSessionExtension implements BeforeEachCallback {
	@Override
	public void beforeEach(ExtensionContext extensionContext) throws Exception {
		// шаг 1: проверка, если ли у метода аннотация AdminSession
		AdminSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(AdminSession.class);
		if(annotation != null){ // шаг 2: если есть, добавляем в local storage token админа
			BasePage.authAsUser(CreateUserRequest.getAdmin());
		}
	}
}

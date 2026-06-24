package iteration_1.ui;

import api.configs.Config;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import api.iteration_2.specs.RequestSpecs;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import common.extensions.AdminSessionExtension;
import common.extensions.BrowserMatchExtension;
import common.extensions.TimingExtension;
import common.extensions.UserSessionExtension;
import iteration_2.api.BaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

@ExtendWith(AdminSessionExtension.class)// расширяет класс с помощью созданного нами класса экстеншена
@ExtendWith(UserSessionExtension.class)// расширяет класс с помощью созданного нами класса экстеншена
@ExtendWith(BrowserMatchExtension.class)
@ExtendWith(TimingExtension.class)
public class BaseUITest extends BaseTest {
	@BeforeAll
	public static void setUpSelenoid(){
		Configuration.remote = Config.getProperty("uiRemote");
		Configuration.baseUrl = Config.getProperty("uiBaseUrl");
		Configuration.browser = Config.getProperty("browser");
		Configuration.browserVersion = Config.getProperty("browserVersion");
		Configuration.browserSize =  Config.getProperty("browserSize");
		Configuration.headless = true;// настройка для запуска ui-автотестов без поднятия сессии

		Map<String, Object> selenoidOptions = new HashMap<>();
		selenoidOptions.put("enableVNC", true);
		selenoidOptions.put("enableLog", true);
		Configuration.browserCapabilities.setCapability("selenoid:options", selenoidOptions);

	}
}

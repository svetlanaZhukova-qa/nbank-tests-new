package iteration_1.ui;

import api.configs.Config;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import api.iteration_2.specs.RequestSpecs;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import iteration_2.api.BaseTest;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class BaseUITest extends BaseTest {
	@BeforeAll
	public static void setUpSelenoid(){
		Configuration.remote = Config.getProperty("uiRemote");
		Configuration.baseUrl = Config.getProperty("uiBaseUrl");
		Configuration.browser = Config.getProperty("browser");
		Configuration.browserVersion = Config.getProperty("browserVersion");
		Configuration.browserSize =  Config.getProperty("browserSize");

//		Configuration.browserCapabilities.setCapability("selenoid:options",
//				Map.of("enableVNC", true, "enableLog", true)
//		);
		Map<String, Object> selenoidOptions = new HashMap<>();
		selenoidOptions.put("enableVNC", true);
		selenoidOptions.put("enableLog", true);
		Configuration.browserCapabilities.setCapability("selenoid:options", selenoidOptions);

	}
	public void authAsUser(String username, String password){
		Selenide.open("/");
		String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
		executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
	}

	public void authAsUser(CreateUserRequest createUserRequest){
		authAsUser(createUserRequest.getUsername(),createUserRequest.getPassword());
	}
}

package iteration_1.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.requests.steps.AdminSteps;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.codeborne.selenide.Selenide.$;

public class LoginUserTest {
	@BeforeAll
	public static void setUpSelenoid(){
		Configuration.remote = "http://localhost:4444/wd/hub";
		Configuration.baseUrl = "http://192.168.1.101:3000";
		Configuration.browser = "chrome";
		Configuration.browserVersion = "91.0";
		Configuration.browserSize = "1920x1080";
		Configuration.browserCapabilities.setCapability("selenoid:options",
				Map.of("enableVNC", true, "enableLog", true)
		);

	}
	
	@Test
	public void adminCanLoginWithCorrectData(){
		CreateUserRequest admin = CreateUserRequest.builder().username("admin").password("admin").build();

		Selenide.open("/login");
		$(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.getUsername());
		$(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.getPassword());
		$("button").click();

		$(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);
		Selenide.sleep(20000);

	}

	@Test
	public void userCanLoginWithCorrectDataTest(){
		CreateUserRequest createUserRequest = AdminSteps.createUser();

		Selenide.open("/login");
		$(Selectors.byAttribute("placeholder", "Username")).sendKeys(createUserRequest.getUsername());
		$(Selectors.byAttribute("placeholder", "Password")).sendKeys(createUserRequest.getPassword());
		$("button").click();

		$(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));


	}
}

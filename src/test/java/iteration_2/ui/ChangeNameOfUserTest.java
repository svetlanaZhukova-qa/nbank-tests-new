package iteration_2.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import iteration_1.models.comparison.ModelAssertions;
import iteration_2.generators.RandomData;
import iteration_2.models_body_JSON.change_name_user.InfoGetUserResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.models_body_JSON.create_user_and_accont.UserLoginAndGetTokenRequest;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.CrudRequester;
import iteration_2.requests.steps.AdminSteps;
import iteration_2.requests.steps.GetUserInfo;
import iteration_2.specs.RequestSpecs;
import iteration_2.specs.ResponseSpecs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import java.util.Map;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тесты на возможность изменить имя профиля пользователем")
public class ChangeNameOfUserTest {
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
	@Tag("positive")
	@DisplayName("Пользователь может меня свое имя в профиле.")
	public void userCanChangeTheirNameWithCorrectData(){
		// создаем пользователя и логинимся
		CreateUserRequest createUserRequest = AdminSteps.createUser();
		String userAuthHeader = new CrudRequester(
				RequestSpecs.unAuthUserSpec(),
				ResponseSpecs.requestReturnOk(), Endpoint.LOGIN_USER)
				.post(UserLoginAndGetTokenRequest.builder().username(createUserRequest.getUsername()).password(createUserRequest.getPassword()).build())
				.extract()
				.header("Authorization");

		Selenide.open("/");

		executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

		Selenide.open("/dashboard");
		$(byText(createUserRequest.getUsername())).click();
		String newName = RandomData.getRandomName();
		$(Selectors.byAttribute("placeholder", "Enter new name")).sendKeys(newName);
		$(byText("💾 Save Changes")).click();

		Alert alert = switchTo().alert();
		assertEquals(alert.getText(), "✅ Name updated successfully!");

		alert.accept();
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
		String userAuthHeader = new CrudRequester(
				RequestSpecs.unAuthUserSpec(),
				ResponseSpecs.requestReturnOk(), Endpoint.LOGIN_USER)
				.post(UserLoginAndGetTokenRequest.builder().username(createUserRequest.getUsername()).password(createUserRequest.getPassword()).build())
				.extract()
				.header("Authorization");

		Selenide.open("/");

		executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

		Selenide.open("/dashboard");
		$(byText(createUserRequest.getUsername())).click();
		String newName = RandomData.getRandomUserName();
		$(Selectors.byAttribute("placeholder", "Enter new name")).sendKeys(newName);
		$(byText("💾 Save Changes")).click();

		Alert alert = switchTo().alert();
		assertEquals(alert.getText(), "Name must contain two words with letters only");

		alert.accept();
		// проверяем что на API имя  не изменилось
		InfoGetUserResponse infoGetUserResponse = GetUserInfo.getInfo(createUserRequest);
		assertEquals(infoGetUserResponse.getName(), null);
	}
}

package iteration_1.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.models_body_JSON.create_user_and_accont.UserLoginAndGetTokenRequest;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.CrudRequester;
import iteration_2.requests.steps.AdminSteps;
import iteration_2.specs.RequestSpecs;
import iteration_2.specs.ResponseSpecs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest {
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
	public void userCanCreateAccountTest() {
		// ШАГИ ПО НАСТРОЙКЕ ОКРУЖЕНИЯ
		// ШАГ 1: админ логинится в банке
		// ШАГ 2: админ создает юзера
		// ШАГ 3: юзер логинится в банке

		CreateUserRequest user = AdminSteps.createUser();

		String userAuthHeader = new CrudRequester(
				RequestSpecs.unAuthUserSpec(),
				ResponseSpecs.requestReturnOk(),Endpoint.LOGIN_USER)
				.post(UserLoginAndGetTokenRequest.builder().username(user.getUsername()).password(user.getPassword()).build())
				.extract()
				.header("Authorization");

		Selenide.open("/");

		executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

		Selenide.open("/dashboard");

		// ШАГИ ТЕСТА
		// ШАГ 4: юзер создает аккаунт

		$(Selectors.byText("➕ Create New Account")).click();


		// ШАГ 5: проверка, что аккаунт создался на UI

		Alert alert = switchTo().alert();
		String alertText = alert.getText();

		assertThat(alertText).contains("✅ New Account Created! Account Number:");

		alert.accept();

		Pattern pattern = Pattern.compile("Account Number: (\\w+)");
		Matcher matcher = pattern.matcher(alertText);

		matcher.find();

		String createdAccNumber = matcher.group(1);

		// ШАГ 6: проверка, что аккаунт был создан на API

		CreateAccountResponse[] existingUserAccounts = given()
				.spec(RequestSpecs.authUserSpec(user.getUsername(), user.getPassword()))
				.get("http://localhost:4111/api/v1/customer/accounts")
				.then().assertThat()
				.extract().as(CreateAccountResponse[].class);

		assertThat(existingUserAccounts).hasSize(1);

		CreateAccountResponse createdAccount = existingUserAccounts[0];

		assertThat(createdAccount).isNotNull();
		assertThat(createdAccount.getBalance()).isZero();
	}
}

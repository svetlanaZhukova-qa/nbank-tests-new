package iteration_2.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import iteration_2.data.Account;
import iteration_2.generators.RandomData;
import iteration_2.models_body_JSON.change_name_user.InfoGetUserResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.models_body_JSON.create_user_and_accont.UserLoginAndGetTokenRequest;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.CrudRequester;
import iteration_2.requests.steps.AdminSteps;
import iteration_2.requests.steps.GetUserInfo;
import iteration_2.requests.steps.UserCreateAccount;
import iteration_2.specs.RequestSpecs;
import iteration_2.specs.ResponseSpecs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тесты на проверку возможности создания Депозита")
public class CreateDepositTest {
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
	@DisplayName("Пользователь может делать депозит")
	@Tag("positive")
	public void userCanCreateDeposit(){
		// создаем пользователя
		CreateUserRequest createUserRequest = AdminSteps.createUser();

		String userAuthHeader = new CrudRequester(
				RequestSpecs.unAuthUserSpec(),
				ResponseSpecs.requestReturnOk(), Endpoint.LOGIN_USER)
				.post(UserLoginAndGetTokenRequest.builder().username(createUserRequest.getUsername()).password(createUserRequest.getPassword()).build())
				.extract()
				.header("Authorization");

		Selenide.open("/");

		executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

		// создаем аккаунт
		CreateAccountResponse createAccountResponse = UserCreateAccount.userCreateAccount(createUserRequest);
		String accountNumber = createAccountResponse.getAccountNumber();
		long idAccount = createAccountResponse.getId();
		// создаем депозит
		Selenide.open("/deposit");
		$("select.account-selector").click();
		$$("select.account-selector option").findBy(text(accountNumber)).click();
		int depositAmount = RandomData.getRandomDeposit();
		String depositAmountStr = String.valueOf(depositAmount);

		$(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(depositAmountStr);
		$(byText("\uD83D\uDCB5 Deposit")).click();

		Alert alert = switchTo().alert();

		assertEquals(alert.getText(), "✅ Successfully deposited $" + depositAmount + " to account " + accountNumber + "!");

		alert.accept();

		// проверка, что депозит создан на API
		// запрашиваем информацию профиля
		InfoGetUserResponse infoGetUserResponse = GetUserInfo.getInfo(createUserRequest);

		List<Account> accounts = new CrudRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk(),
				Endpoint.USER_INFO).get().extract().jsonPath().getList("accounts", Account.class);;

		Optional<Account> account = accounts.stream().filter(a -> a.getId() == idAccount).findFirst();
		assertThat(account.get().getBalance()).isEqualTo(depositAmount);
		assertThat(account.get().getId()).isEqualTo(idAccount);

	}

	@Test
	@DisplayName("Пользователь не может делать депозит с невалидной суммой")
	@Tag("negative")
	public void userCannotCreateDepositWithNotValidSum(){
		// создаем пользователя
		CreateUserRequest createUserRequest = AdminSteps.createUser();

		String userAuthHeader = new CrudRequester(
				RequestSpecs.unAuthUserSpec(),
				ResponseSpecs.requestReturnOk(), Endpoint.LOGIN_USER)
				.post(UserLoginAndGetTokenRequest.builder().username(createUserRequest.getUsername()).password(createUserRequest.getPassword()).build())
				.extract()
				.header("Authorization");

		Selenide.open("/");

		executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);

		// создаем аккаунт
		CreateAccountResponse createAccountResponse = UserCreateAccount.userCreateAccount(createUserRequest);
		String accountNumber = createAccountResponse.getAccountNumber();
		long idAccount = createAccountResponse.getId();
		// создаем депозит
		Selenide.open("/deposit");
		$("select.account-selector").click();
		$$("select.account-selector option").findBy(text(accountNumber)).click();
		int notValidDeposit = getMaxDeposit() + 1;
		String depositToString = String.valueOf(notValidDeposit);
		$(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(depositToString);
		$(byText("\uD83D\uDCB5 Deposit")).click();

		Alert alert = switchTo().alert();

		assertEquals(alert.getText(), "❌ Please deposit less or equal to 5000$.");

		alert.accept();

		// проверка, что депозит не создан на API
		// запрашиваем информацию профиля
		InfoGetUserResponse infoGetUserResponse = GetUserInfo.getInfo(createUserRequest);

		List<Account> accounts = new CrudRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk(),
				Endpoint.USER_INFO).get().extract().jsonPath().getList("accounts", Account.class);;

		Optional<Account> account = accounts.stream().filter(a -> a.getId() == idAccount).findFirst();
		assertThat(account.get().getBalance()).isEqualTo(0);
		assertThat(account.get().getId()).isEqualTo(idAccount);

	}


	private static int getMaxDeposit(){
		return 5000;
	}


}

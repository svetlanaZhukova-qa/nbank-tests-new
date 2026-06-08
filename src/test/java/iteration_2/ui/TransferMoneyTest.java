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
import iteration_2.requests.steps.*;
import iteration_2.specs.RequestSpecs;
import iteration_2.specs.ResponseSpecs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import java.util.Locale;
import java.util.Map;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тесты на проверку возможности перевода денег с одного счета на другой")
public class TransferMoneyTest {
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
	@DisplayName("Пользователь может переводить деньги с одного счета на другой")
	@Tag("positive")
	public void userCanTransferMoneyFromOneAccountToAnother(){
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

		// создаем счет 1
		CreateAccountResponse createAccountResponse1 = UserCreateAccount.userCreateAccount(createUserRequest);
		String accountNumber1 = createAccountResponse1.getAccountNumber();
		// создаем счет 2
		CreateAccountResponse createAccountResponse2 = UserCreateAccount.userCreateAccount(createUserRequest);
		String accountNumber2 = createAccountResponse2.getAccountNumber();
		// создаем депозит
		int deposit = RandomData.getRandomDeposit();
		String depositToString = String.valueOf(deposit);
		UserCreateDeposit.createDeposit(createUserRequest, createAccountResponse1, deposit);
		// переводит деньги с одного счета на другой
		Selenide.open("/transfer");
		$("select.account-selector").click();
		$$("select.account-selector option").findBy(text(accountNumber1)).click();
		$(Selectors.byAttribute("placeholder", "Enter recipient account number")).sendKeys(accountNumber2);
		$(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(depositToString);
		$("#confirmCheck").setSelected(true);
		$(byText("\uD83D\uDE80 Send Transfer")).click();

		Alert alert = switchTo().alert();

		assertEquals(alert.getText(), "✅ Successfully transferred $" + deposit + " to account " + accountNumber2 + "!");

		alert.accept();

//		// проверяем по API что счет действительно пополнен
		InfoGetUserResponse infoGetUserResponse = GetUserInfo.getInfo(createUserRequest);

		Account account1 = infoGetUserResponse.getAccounts().stream()
				.filter(acc -> accountNumber1.equals(acc.getAccountNumber()))
				.findFirst()
				.orElseThrow(() -> new AssertionError("Account " + accountNumber1 + " not found"));

		Account account2 = infoGetUserResponse.getAccounts().stream()
				.filter(acc -> accountNumber2.equals(acc.getAccountNumber()))
				.findFirst()
				.orElseThrow(() -> new AssertionError("Account " + accountNumber2 + " not found"));

		// Счет 1: был депозит 500, перевод  прошел → баланс 450
		assertEquals(0, account1.getBalance(), 0.01);

		// Счет 2: прошел перевод -> баланс 50
		assertEquals(deposit, account2.getBalance(), 0.01);

	}

	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может переводить отрицательные суммы")
	public void UserCantTransferMoneyFromOneAccountToAnotherWithNotCorrectSum(){
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

		// создаем счет 1
		CreateAccountResponse createAccountResponse1 = UserCreateAccount.userCreateAccount(createUserRequest);
		String accountNumber1 = createAccountResponse1.getAccountNumber();
		// создаем счет 2
		CreateAccountResponse createAccountResponse2 = UserCreateAccount.userCreateAccount(createUserRequest);
		String accountNumber2 = createAccountResponse2.getAccountNumber();
		// создаем депозит
		int deposit = RandomData.getRandomDeposit();

		UserCreateDeposit.createDeposit(createUserRequest, createAccountResponse1, deposit);
		// переводит деньги с одного счета на другой
		Selenide.open("/transfer");
		$("select.account-selector").click();
		$$("select.account-selector option").findBy(text(accountNumber1)).click();
		$(Selectors.byAttribute("placeholder", "Enter recipient account number")).sendKeys(accountNumber2);
		int notValidSum = getMaxDeposit() - getMaxDeposit() - 1;
		String notValidSumToString = String.valueOf(notValidSum);
		$(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(notValidSumToString);
		$("#confirmCheck").setSelected(true);
		$(byText("\uD83D\uDE80 Send Transfer")).click();

		Alert alert = switchTo().alert();

		assertEquals(alert.getText(), "❌ Error: Transfer amount must be at least 0.01");

		alert.accept();

		// проверяем по API что счет действительно не пополнен
		InfoGetUserResponse infoGetUserResponse = GetUserInfo.getInfo(createUserRequest);

		Account account1 = infoGetUserResponse.getAccounts().stream()
				.filter(acc -> accountNumber1.equals(acc.getAccountNumber()))
				.findFirst()
				.orElseThrow(() -> new AssertionError("Account " + accountNumber1 + " not found"));

		Account account2 = infoGetUserResponse.getAccounts().stream()
				.filter(acc -> accountNumber2.equals(acc.getAccountNumber()))
				.findFirst()
				.orElseThrow(() -> new AssertionError("Account " + accountNumber2 + " not found"));

		// Счет 1: был депозит 500, перевод не прошел → баланс 500
		assertEquals((double) deposit, account1.getBalance(), 0.01);

		// Счет 2: ничего не поступало → баланс 0
		assertEquals(0.0, account2.getBalance(), 0.01);
	}

	@Test
	@Tag("positive")
	@DisplayName("Пользователь может отслеживать состояние своих учетных записей")
	public void userCanSeeTrackingOfTheirAccounts(){
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

		// создаем счет 1
		CreateAccountResponse createAccountResponse1 = UserCreateAccount.userCreateAccount(createUserRequest);
		String accountNumber1 = createAccountResponse1.getAccountNumber();
		// создаем счет 2
		CreateAccountResponse createAccountResponse2 = UserCreateAccount.userCreateAccount(createUserRequest);
		String accountNumber2 = createAccountResponse2.getAccountNumber();
		// создаем депозит
		int deposit = RandomData.getRandomDeposit();

		UserCreateDeposit.createDeposit(createUserRequest, createAccountResponse1, deposit);
		UserCreateTransfer.createTransfer(createUserRequest, createAccountResponse1, createAccountResponse2, deposit);

		// Переходим к просмотру транзакций
		Selenide.open("/transfer");
		$(byText("🔁 Transfer Again")).click();
		$(byAttribute("placeholder", "Enter name to find transactions")).sendKeys(createUserRequest.getUsername());
		$(byText("🔍 Search Transactions")).click();

		// Ждем появления заголовка
		$("h3.mt-4").shouldHave(text("Matching Transactions"));

		// Проверяем, что список содержит ровно 3 элемента
		$$(".list-group-item").shouldHave(size(3));

		// Форматируем сумму с точкой (Locale.US) — как в UI
		String depositFormatted = String.format(Locale.US, "%.2f", (double) deposit);

		// Проверяем каждую транзакцию отдельно
		// 1. DEPOSIT
		$$(".list-group-item").findBy(text("DEPOSIT")).shouldHave(
				text("DEPOSIT - $" + depositFormatted),
				text("🔍 Found under:")
		);

		// 2. TRANSFER_OUT
		$$(".list-group-item").findBy(text("TRANSFER_OUT")).shouldHave(
				text("TRANSFER_OUT - $" + depositFormatted),
				text("🔍 Found under:")
		);

		// 3. TRANSFER_IN
		$$(".list-group-item").findBy(text("TRANSFER_IN")).shouldHave(
				text("TRANSFER_IN - $" + depositFormatted),
				text("🔍 Found under:")
		);

		// Проверяем, что кнопки Repeat есть у всех
		$$(".list-group-item").forEach(item ->
				item.$("button").shouldHave(text("🔁 Repeat"))
		);
	}

	private static int getMaxDeposit(){
		return 5000;
	}


}

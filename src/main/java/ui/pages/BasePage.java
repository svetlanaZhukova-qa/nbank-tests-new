package ui.pages;

import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import api.iteration_2.specs.RequestSpecs;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Alert;
import ui.elements.BaseElement;

import java.util.List;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class BasePage<T extends BasePage> {
	protected SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Username"));
	protected SelenideElement passwordInput = $(Selectors.byAttribute("placeholder", "Password"));
	protected SelenideElement enterAmount = $(Selectors.byAttribute("placeholder", "Enter amount"));


	public abstract String url();

public T open(){
		return Selenide.open(url(), (Class<T>) this.getClass() );
	}

	public <T extends BasePage> T getPage(Class<T> pageClass){
        return Selenide.page(pageClass);
	}

	// Для констант
	public T checkAlertMessageAndAccept(BankAlert bankAlert) {
		return checkAlertMessageAndAccept(bankAlert.format());
	}

	// Для шаблонов
	public T checkAlertMessageAndAccept(BankAlert bankAlert, Object... args) {
		return checkAlertMessageAndAccept(bankAlert.format(args));
	}

	// Базовый метод
	public T checkAlertMessageAndAccept(String expectedMessage) {
		Alert alert = switchTo().alert();
		assertThat(alert.getText()).contains(expectedMessage);
		alert.accept();
		return (T) this;
	}

	public static void authAsUser(String username, String password){
		Selenide.open("/");
		String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
		executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
	}

	public static void authAsUser(CreateUserRequest createUserRequest){
		authAsUser(createUserRequest.getUsername(),createUserRequest.getPassword());
	}


	// ElementCollection -> List<BaseElement>
	protected <T extends BaseElement> List<T> generatePageElements(ElementsCollection elementsCollection, Function<SelenideElement, T> constructor) {
		return elementsCollection.stream().map(constructor).toList();
	}

}

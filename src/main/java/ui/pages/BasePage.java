package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Alert;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
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



}

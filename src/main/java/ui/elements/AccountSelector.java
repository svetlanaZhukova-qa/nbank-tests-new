package ui.elements;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class AccountSelector {
	private final SelenideElement selectAccount = $("select.account-selector");

	public AccountSelector selectAccountNumber(String accountNumber){
		selectAccount.click();
		$$("select.account-selector option").findBy(text(accountNumber)).click();
		return this;
	}

}

package ui.elements;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class AccountSelector extends BaseElement{

	public AccountSelector(SelenideElement element) {
		super(element);
	}

	public AccountSelector selectAccountNumber(String accountNumber){
		element.click();
		findAll("option").findBy(text(accountNumber)).click();
		return this;
	}

}

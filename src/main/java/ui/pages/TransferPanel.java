package ui.pages;

import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ui.elements.AccountSelector;

import java.util.Locale;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TransferPanel extends BasePage<TransferPanel>{

	private final AccountSelector accountSelector;

	public TransferPanel() {
		this.accountSelector = new AccountSelector($("select.account-selector"));
	}

	//  Создание трансфера
	private SelenideElement recipientAccount = $(byAttribute("placeholder", "Enter recipient account number"));
	private SelenideElement confirmCheck = $("#confirmCheck");
	private SelenideElement transferButton = $(byText("\uD83D\uDE80 Send Transfer"));

	// Просмотр транзакций
	private SelenideElement transferAgain = $(byText("🔁 Transfer Again"));
	private SelenideElement enterNameToFindTransactions = $(byAttribute("placeholder", "Enter name to find transactions"));
	private SelenideElement buttonSearchTransactions = $(byText("🔍 Search Transactions"));

	// результаты поиска
	private SelenideElement transactionsHeader = $("h3.mt-4");
	private ElementsCollection transactions = $$(".list-group-item");


	@Override
	public String url() {
		return "/transfer";
	}

	//  Создание трансфера

	/**
	 * Вводим аккаунт Получателя
	 * @return
	 */
	public TransferPanel enterRecipientAccount(String accountNumber2){
		recipientAccount.sendKeys(accountNumber2);
		return this;
	}

	/**
	 * Вводим сумму трансфера
	 * @return
	 */
	public TransferPanel enterAmountTransfer(String depositToString){
		enterAmount.sendKeys(depositToString);
		return this;
	}

	/**
	 * Активируем чекбокс трансфера
	 * @return
	 */
	public TransferPanel activeConfirmCheck(){
		confirmCheck.setSelected(true);
		return this;
	}

	/**
	 * Отправка трансфера
	 * @return
	 */
	public TransferPanel sendTransfer(){
		transferButton.click();
		return this;
	}


	public TransferPanel createTransfer(String accountNumber1, String accountNumber2, String depositToString){

		accountSelector.selectAccountNumber(accountNumber1);
		return enterRecipientAccount(accountNumber2).enterAmountTransfer(depositToString).activeConfirmCheck().sendTransfer();
	}

	// Просмотр транзакций

	public TransferPanel getAllTransactions(CreateUserRequest createUserRequest){
		transferAgain.click();
		enterNameToFindTransactions.sendKeys(createUserRequest.getUsername());
		buttonSearchTransactions.click();
		return this;
	}

	// Проверки
	public TransferPanel checkTransactionsHeaderVisible() {
		transactionsHeader.shouldHave(text("Matching Transactions"));
		return this;
	}

	public TransferPanel checkTransactionsCount(int expectedCount) {
		transactions.shouldHave(CollectionCondition.size(expectedCount));
		return this;
	}

	public TransferPanel checkTransactionExists(String type, double amount) {
		String formattedAmount = String.format(Locale.US, "%.2f", amount);
		transactions.findBy(text(type)).shouldHave(
				text(type + " - $" + formattedAmount),
				text("🔍 Found under:")
		);
		return this;
	}

	public TransferPanel checkAllTransactionsHaveRepeatButton() {
		transactions.forEach(item ->
				item.$("button").shouldHave(text("🔁 Repeat"))
		);
		return this;
	}
}

package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DepositPanel extends BasePage<DepositPanel> {
	private SelenideElement selectAccount = $("select.account-selector");
	private SelenideElement enterAmount = $(Selectors.byAttribute("placeholder", "Enter amount"));
	private SelenideElement depositButtton = $(byText("\uD83D\uDCB5 Deposit"));
	@Override
	public String url() {
		return "/deposit";
	}

	/**
	 * Выбираем аккаунт для депозита
	 * @param accountNumber
	 * @return
	 */
	public DepositPanel selectAccountNumber(String accountNumber){
		selectAccount.click();
		$$("select.account-selector option").findBy(text(accountNumber)).click();
		return this;
	}

	/**
	 * Вводим сумму депозита
	 * @return
	 */
	public DepositPanel enterAmountDeposit(int depositAmount){
		String depositAmountStr = String.valueOf(depositAmount);
		enterAmount.clear();
		enterAmount.sendKeys(depositAmountStr);
		return this;
	}

	/**
	 * Кликаем на кнопку Депозит
	 * @return
	 */
	public DepositPanel clickDepositButton(){
		depositButtton.click();
		return this;
	}


	/**
	 * Полный путь создания аккаунта
	 * @return
	 */
	public DepositPanel createDeposit(String accountNumber, int deposit){
		// 	// создаем депозит
		//		Selenide.open("/deposit");
		//		$("select.account-selector").click();
		//		$$("select.account-selector option").findBy(text(accountNumber)).click();
		//		int depositAmount = RandomData.getRandomDeposit();
		//		String depositAmountStr = String.valueOf(depositAmount);
		//
		//		$(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(depositAmountStr);
		//		$(byText("\uD83D\uDCB5 Deposit")).click();
		selectAccountNumber(accountNumber).enterAmountDeposit(deposit).clickDepositButton();
		return this;

	}
}

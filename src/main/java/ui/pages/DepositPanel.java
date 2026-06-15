package ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class DepositPanel extends BasePage<DepositPanel> {


	private SelenideElement depositButtton = $(byText("\uD83D\uDCB5 Deposit"));
	private final AccountSelector accountSelector = new AccountSelector();

	@Override
	public String url() {
		return "/deposit";
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

		accountSelector.selectAccountNumber(accountNumber);

		return enterAmountDeposit(deposit).clickDepositButton();

	}
}

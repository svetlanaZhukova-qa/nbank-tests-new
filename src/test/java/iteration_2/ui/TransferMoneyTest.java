package iteration_2.ui;

import api.iteration_2.data.Account;
import api.iteration_2.generators.RandomData;
import api.iteration_2.models_body_JSON.change_name_user.InfoGetUserResponse;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import api.iteration_2.requests.steps.GetUserInfo;
import api.iteration_2.requests.steps.UserCreateAccount;
import api.iteration_2.requests.steps.UserCreateDeposit;
import api.iteration_2.requests.steps.UserCreateTransfer;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import iteration_1.ui.BaseUITest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.TransferPanel;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тесты на проверку возможности перевода денег с одного счета на другой")
public class TransferMoneyTest extends BaseUITest {
	@Test
	@DisplayName("Пользователь может переводить деньги с одного счета на другой")
	@Tag("positive")
	@UserSession
	public void userCanTransferMoneyFromOneAccountToAnother(){
		// Пользователь уже создан и авторизован через @UserSession

		// создаем счет 1
		CreateAccountResponse createAccountResponse1 = UserCreateAccount.userCreateAccount(SessionStorage.getUser(1));
		String accountNumber1 = createAccountResponse1.getAccountNumber();

		// создаем счет 2
		CreateAccountResponse createAccountResponse2 = UserCreateAccount.userCreateAccount(SessionStorage.getUser(1));
		String accountNumber2 = createAccountResponse2.getAccountNumber();

		// создаем депозит
		int deposit = RandomData.getRandomDeposit();
		String depositToString = String.valueOf(deposit);
		UserCreateDeposit.createDeposit(SessionStorage.getUser(1), createAccountResponse1, deposit);

		// переводит деньги с одного счета на другой
		new TransferPanel().open().createTransfer(accountNumber1, accountNumber2, depositToString)
				.checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_TRANSFERRED, deposit, accountNumber2);

//		// проверяем по API что счет действительно пополнен
		InfoGetUserResponse infoGetUserResponse = GetUserInfo.getInfo(SessionStorage.getUser(1));

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
	@UserSession
	public void UserCantTransferMoneyFromOneAccountToAnotherWithNotCorrectSum(){
		// Пользователь уже создан и авторизован через @UserSession

		// создаем счет 1
		CreateAccountResponse createAccountResponse1 = UserCreateAccount.userCreateAccount(SessionStorage.getUser(1));
		String accountNumber1 = createAccountResponse1.getAccountNumber();

		// создаем счет 2
		CreateAccountResponse createAccountResponse2 = UserCreateAccount.userCreateAccount(SessionStorage.getUser(1));
		String accountNumber2 = createAccountResponse2.getAccountNumber();

		// создаем депозит
		int deposit = RandomData.getRandomDeposit();
		UserCreateDeposit.createDeposit(SessionStorage.getUser(1), createAccountResponse1, deposit);

		// переводит деньги с одного счета на другой
		int notValidSum = getMaxDeposit() - getMaxDeposit() - 1;
		String notValidSumToString = String.valueOf(notValidSum);
		new TransferPanel().open().createTransfer(accountNumber1, accountNumber2,notValidSumToString)
				.checkAlertMessageAndAccept(BankAlert.FAILED_TRANSFER);

		// проверяем по API что счет действительно не пополнен
		InfoGetUserResponse infoGetUserResponse = GetUserInfo.getInfo(SessionStorage.getUser(1));

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
	@UserSession
	public void userCanSeeTrackingOfTheirAccounts(){
		// Пользователь уже создан и авторизован через @UserSession

		// создаем счет 1
		CreateAccountResponse createAccountResponse1 = UserCreateAccount.userCreateAccount(SessionStorage.getUser(1));
		String accountNumber1 = createAccountResponse1.getAccountNumber();

		// создаем счет 2
		CreateAccountResponse createAccountResponse2 = UserCreateAccount.userCreateAccount(SessionStorage.getUser(1));
		String accountNumber2 = createAccountResponse2.getAccountNumber();

		// создаем депозит
		int deposit = RandomData.getRandomDeposit();
		UserCreateDeposit.createDeposit(SessionStorage.getUser(1), createAccountResponse1, deposit);
		UserCreateTransfer.createTransfer(SessionStorage.getUser(1), createAccountResponse1, createAccountResponse2, deposit);

		// Переходим к просмотру транзакций и делаем проверки
		new TransferPanel().open()
				.getAllTransactions(SessionStorage.getUser(1))
				.checkTransactionsHeaderVisible()
				.checkTransactionsCount(3)
				.checkTransactionExists("DEPOSIT", deposit)
				.checkTransactionExists("TRANSFER_OUT", deposit)
				.checkTransactionExists("TRANSFER_IN", deposit)
				.checkAllTransactionsHaveRepeatButton();

	}

	private static int getMaxDeposit(){
		return 5000;
	}


}

package iteration_2.ui;

import api.iteration_2.data.Account;
import api.iteration_2.generators.RandomData;
import api.iteration_2.models_body_JSON.create_user_and_accont.CreateAccountResponse;
import api.iteration_2.requests.skelethon.Endpoint;
import api.iteration_2.requests.skelethon.requesters.CrudRequester;
import api.iteration_2.requests.steps.UserCreateAccount;
import api.iteration_2.specs.RequestSpecs;
import api.iteration_2.specs.ResponseSpecs;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import iteration_1.ui.BaseUITest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.DepositPanel;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Тесты на проверку возможности создания Депозита")
public class CreateDepositTest extends BaseUITest {

	@Test
	@DisplayName("Пользователь может делать депозит")
	@Tag("positive")
	@UserSession
	public void userCanCreateDeposit(){
		// Пользователь уже создан и авторизован через @UserSession

		// создаем аккаунт
		CreateAccountResponse createAccountResponse = UserCreateAccount.userCreateAccount(SessionStorage.getUser(1));
		String accountNumber = createAccountResponse.getAccountNumber();
		long idAccount = createAccountResponse.getId();

		// создаем депозит
		int depositAmount = RandomData.getRandomDeposit();
		new DepositPanel().open().createDeposit(accountNumber, depositAmount)
				.checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_DEPOSITED, depositAmount, accountNumber);

		// проверка, что депозит создан на API
		List<Account> accounts = new CrudRequester(RequestSpecs.authUserSpec(SessionStorage.getUser(1).getUsername(), SessionStorage.getUser(1).getPassword()),
				ResponseSpecs.requestReturnOk(),
				Endpoint.USER_INFO).get().extract().jsonPath().getList("accounts", Account.class);;

		Optional<Account> account = accounts.stream().filter(a -> a.getId() == idAccount).findFirst();
		assertThat(account.get().getBalance()).isEqualTo(depositAmount);
		assertThat(account.get().getId()).isEqualTo(idAccount);

	}

	@Test
	@DisplayName("Пользователь не может делать депозит с невалидной суммой")
	@Tag("negative")
	@UserSession
	public void userCannotCreateDepositWithNotValidSum(){
		// Пользователь уже создан и авторизован через @UserSession

		// создаем аккаунт
		CreateAccountResponse createAccountResponse = UserCreateAccount.userCreateAccount(SessionStorage.getUser(1));
		String accountNumber = createAccountResponse.getAccountNumber();
		long idAccount = createAccountResponse.getId();

		// создаем депозит
		int notValidDeposit = getMaxDeposit() + 1;
		new DepositPanel().open().createDeposit(accountNumber, notValidDeposit)
				.checkAlertMessageAndAccept(BankAlert.FAILED_DEPOSIT);

		// проверка, что депозит не создан на API
	    List<Account> accounts = new CrudRequester(RequestSpecs.authUserSpec(SessionStorage.getUser(1).getUsername(), SessionStorage.getUser(1).getPassword()),
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

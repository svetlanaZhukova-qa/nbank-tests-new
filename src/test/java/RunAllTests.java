import iteration_1.ui.CreateAccountTest;
import iteration_1.ui.CreateUserTest;
import iteration_1.ui.LoginUserTest;
import iteration_2.api.ChangeNameOfUserTest;
import iteration_2.api.CreateDepositTest;
import iteration_2.api.TransferMoneyTest;
import iteration_2.ui.ChangeNameOfUserTestUI;
import iteration_2.ui.CreateDepositTestUI;
import iteration_2.ui.TransferMoneyTestUI;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
		ChangeNameOfUserTest.class,
		CreateDepositTest.class,
		TransferMoneyTest.class,
		ChangeNameOfUserTestUI.class,
		CreateDepositTestUI.class,
		TransferMoneyTestUI.class,
		iteration_1.api.CreateAccountTest.class,
		iteration_1.api.CreateUserTest.class,
		iteration_1.api.LoginUserTest.class,
		CreateAccountTest.class,
		CreateUserTest.class,
		LoginUserTest.class
})
public class RunAllTests {
}

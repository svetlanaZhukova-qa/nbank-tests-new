
import iteration_2.api.ChangeNameOfUserTest;
import iteration_2.api.CreateDepositTest;
import iteration_2.api.TransferMoneyTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
		ChangeNameOfUserTest.class,
		CreateDepositTest.class,
		TransferMoneyTest.class,
		iteration_1.api.CreateAccountTest.class,
		iteration_1.api.CreateUserTest.class,
		iteration_1.api.LoginUserTest.class,

})
public class RunAllAPITests {
}

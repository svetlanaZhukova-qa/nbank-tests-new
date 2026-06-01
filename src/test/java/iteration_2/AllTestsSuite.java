package iteration_2;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
		ChangeNameOfUserTest.class,
		CreateDepositTest.class,
		TransferMoneyTest.class
})
public class AllTestsSuite {
}

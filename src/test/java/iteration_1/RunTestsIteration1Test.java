package iteration_1;

import iteration_1.ui.CreateAccountTest;
import iteration_1.ui.CreateUserTest;
import iteration_1.ui.LoginUserTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
		iteration_1.api.CreateAccountTest.class,
		iteration_1.api.CreateUserTest.class,
		iteration_1.api.LoginUserTest.class,
		CreateAccountTest.class,
		CreateUserTest.class,
		LoginUserTest.class
})
public class RunTestsIteration1Test {
}

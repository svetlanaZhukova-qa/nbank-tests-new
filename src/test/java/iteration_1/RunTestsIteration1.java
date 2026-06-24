package iteration_1;

import iteration_1.api.CreateAccountTestApi;
import iteration_1.api.CreateUserTestApi;
import iteration_1.api.LoginUserTestApi;
import iteration_1.ui.CreateAccountTestUi;
import iteration_1.ui.CreateUserTestUi;
import iteration_1.ui.LoginUserTestUi;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
		CreateAccountTestApi.class,
		CreateUserTestApi.class,
		LoginUserTestApi.class,
		CreateAccountTestUi.class,
		CreateUserTestUi.class,
		LoginUserTestUi.class
})
public class RunTestsIteration1 {
}

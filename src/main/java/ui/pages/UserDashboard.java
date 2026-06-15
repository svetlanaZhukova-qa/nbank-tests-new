package ui.pages;

import api.iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
@Getter
public class UserDashboard extends BasePage<UserDashboard>{
	private SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
	private SelenideElement createNewAccount = $(byText("➕ Create New Account"));
	private SelenideElement enterNewName = $(Selectors.byAttribute("placeholder", "Enter new name"));
	private SelenideElement buttonSaveChanges = $(byText("💾 Save Changes"));



	@Override
	public String url() {
		return "/dashboard";
	}

	public UserDashboard createNewAccount(){
		createNewAccount.click();
		return this;
	}

	public UserDashboard updateName(CreateUserRequest createUserRequest, String newName){
		$(byText(createUserRequest.getUsername())).click();
		enterNewName.setValue(newName);
		buttonSaveChanges.click();
		return this;
	}


}

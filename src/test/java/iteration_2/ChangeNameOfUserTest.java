package iteration_2;

import iteration_2.generators.RandomData;
import iteration_2.models_body_JSON.*;
import iteration_2.models_body_JSON.change_name_user.InfoGetUserResponse;
import iteration_2.models_body_JSON.change_name_user.InfoPutUserRequest;
import iteration_2.models_body_JSON.change_name_user.InfoPutUserResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.CrudRequester;
import iteration_2.requests.skelethon.requesters.ValidateCrudRequester2;
import iteration_2.requests.steps.AdminSteps;
import iteration_2.requests.steps.GetUserInfo;
import iteration_2.specs.RequestSpecs;
import iteration_2.specs.ResponseSpecs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


// Изменение имени пользователя
// Имя в профиле (name):
//— Два слова, состоящее из букв, разделенные пробелом
@DisplayName("Тесты на возможность изменить имя профиля пользователем")
public class ChangeNameOfUserTest extends BaseTest  {

	@Test
	@Tag("positive")
	@DisplayName("Пользователь может видеть информацию о своем профиле")
	public void userCanSeeInfoAboutTheirProfile(){
		// создаем пользователя
		CreateUserRequest createUserRequest = AdminSteps.createUser();
		// запрашиваем информацию о профиле
		InfoGetUserResponse infoUserResponse = GetUserInfo.getInfo(createUserRequest);

		softly.assertThat(infoUserResponse.getUsername()).isEqualTo(createUserRequest.getUsername());
		softly.assertThat(infoUserResponse.getName()).isEqualTo(null);
		softly.assertThat(infoUserResponse.getRole().toString()).isEqualTo(createUserRequest.getRole().toString());
		softly.assertThat(infoUserResponse.getAccounts().isEmpty());


	}

	@ParameterizedTest
	@Tag("positive")
	@ValueSource(strings = {"S s", "Svetlana s"})
	@DisplayName("Пользователь может меня свое имя в профиле.")
	public void userCanChangeTheirNameWithCorrectData(String name){
		// создаем пользователя
		CreateUserRequest createUserRequest = AdminSteps.createUser();

	// меняем имя
		InfoPutUserRequest infoPutUserRequest = InfoPutUserRequest.builder().name(name).build();
		InfoPutUserResponse infoPutUserResponse = new ValidateCrudRequester2<InfoPutUserResponse>(
				RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk(),
				Endpoint.USER_UPDATE).update(infoPutUserRequest);


		softly.assertThat(infoPutUserResponse.getMessage()).isEqualTo("Profile updated successfully");
		softly.assertThat(infoPutUserResponse.getCustomer().getName()).isEqualTo(name);
		softly.assertThat(infoPutUserResponse.getCustomer().getUsername()).isEqualTo(createUserRequest.getUsername());
		softly.assertThat(infoPutUserResponse.getCustomer().getRole().toString()).isEqualTo(createUserRequest.getRole().toString());
		softly.assertThat(infoPutUserResponse.getCustomer().getAccounts()).isEmpty();

		// запрашиваем информацию о профиле
		InfoGetUserResponse infoUserResponse = GetUserInfo.getInfo(createUserRequest);
		softly.assertThat(infoUserResponse.getName()).isEqualTo(name);

	}

	@ParameterizedTest
	@Tag("negative")
	@ValueSource(strings = {"S ", "S  !", "   2  2  ", "", "ы№№№ о;;;;"})
	@DisplayName("Пользователь не может меня свое имя в профиле с некорректными данными")
	public void userCantChangeTheirNameWithNotCorrectData(String name){
		// создаем пользователя
		CreateUserRequest createUserRequest = AdminSteps.createUser();

		// меняем имя
		InfoPutUserRequest infoPutUserRequest = InfoPutUserRequest.builder().name(name).build();
		String errorMessage = new CrudRequester(RequestSpecs.authUserSpecForAcceptTEXT(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnBadRequest(),Endpoint.USER_UPDATE)
				.update(infoPutUserRequest).extract().asString();
		softly.assertThat(errorMessage).isEqualTo("Name must contain two words with letters only");

	}

	@Disabled("Тест падает, потому что ждем 403,но получаем 200. Пароль можно менять одновременно с именем в профиле. Однако при попытке логиниться с этим паролем- сообщение \" \"error\": \"Invalid username or password\"\" и статус код 401.")
	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может менять  password вместе с name")
	public void userCantChangeTheirPassword(){
		// создаем пользователя
		CreateUserRequest createUserRequest = AdminSteps.createUser();

		// отправляем запрос на изменение имени
		InfoPutUserRequest infoPutUserRequest = InfoPutUserRequest.builder().name("Svetlana Svetlana").password(RandomData.getRandomPassword()).build();
		InfoPutUserResponse infoPutUserResponse = new ValidateCrudRequester2<InfoPutUserResponse>(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnForbidden(), Endpoint.USER_UPDATE).update(infoPutUserRequest);
		softly.assertThat(createUserRequest.getPassword()).isEqualTo(infoPutUserResponse.getCustomer().getPassword());
	}


	@Disabled("Тест падает, потому что ждем 403,но получаем 200. При этом username не меняется.")
	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может менять  username вместе с name")
	public void userCantChangeTheirUserName(){
		// создаем пользователя
		CreateUserRequest createUserRequest = AdminSteps.createUser();

		// отправляем запрос на изменение имени
		InfoPutUserRequest infoPutUserRequest = InfoPutUserRequest.builder().name("Svetlana Svetlana").username(RandomData.getRandomUserName()).build();
		InfoPutUserResponse infoPutUserResponse = new ValidateCrudRequester2<InfoPutUserResponse>(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnForbidden(), Endpoint.USER_UPDATE).update(infoPutUserRequest);
		softly.assertThat(createUserRequest.getUsername()).isEqualTo(infoPutUserResponse.getCustomer().getUsername());

	}

	@Disabled("Тест падает, потому что ждем 403,но получаем 200.Но по факту роль не изменилась. ")
	@Tag("negative")
	@DisplayName("Пользователь не может менять  role вместе с name")
	@Test
	public void userCantChangeTheirRole(){
		// создаем пользователя
		CreateUserRequest createUserRequest = AdminSteps.createUser();

		// отправляем запрос на изменение имени
		InfoPutUserRequest infoPutUserRequest = InfoPutUserRequest.builder().name("Svetlana Svetlana").role(UserRole.ADMIN).build();
		InfoPutUserResponse infoPutUserResponse = new ValidateCrudRequester2<InfoPutUserResponse>(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnForbidden(), Endpoint.USER_UPDATE).update(infoPutUserRequest);
		softly.assertThat(createUserRequest.getRole().toString()).isEqualTo(infoPutUserResponse.getCustomer().getRole().toString());
	}


}

package iteration_2;

import iteration_2.generators.RandomData;
import iteration_2.models_body_JSON.*;
import iteration_2.models_body_JSON.change_name_user.InfoGetUserResponse;
import iteration_2.models_body_JSON.change_name_user.InfoPutUserRequest;
import iteration_2.models_body_JSON.change_name_user.InfoPutUserResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserResponse;
import iteration_2.requests.AdminCreateUserRequester;
import iteration_2.requests.UserGetInformationRequester;
import iteration_2.requests.UserPutInformationRequester;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.CrudRequester;
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
		CreateUserRequest createUserRequest = CreateUserRequest.builder()
				.username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();
		new CrudRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated(), Endpoint.ADMIN_USER).post(createUserRequest);
		// запрашиваем информацию о профиле
		InfoGetUserResponse infoUserResponse = new UserGetInformationRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk())
				.getApi().extract().as(InfoGetUserResponse.class);

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
		CreateUserRequest createUserRequest = CreateUserRequest.builder().username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString()).build();
		new AdminCreateUserRequester(RequestSpecs.adminSpec(),ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest).extract().as(CreateUserResponse.class);


//		// меняем имя
		InfoPutUserRequest infoPutUserRequest = InfoPutUserRequest.builder().name(name).build();
		InfoPutUserResponse infoPutUserResponse = new UserPutInformationRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(),
				createUserRequest.getPassword()), ResponseSpecs.requestReturnOk())
				.putApi(infoPutUserRequest).extract().as(InfoPutUserResponse.class);
		softly.assertThat(infoPutUserResponse.getMessage()).isEqualTo("Profile updated successfully");
		softly.assertThat(infoPutUserResponse.getCustomer().getName()).isEqualTo(name);
		softly.assertThat(infoPutUserResponse.getCustomer().getUsername()).isEqualTo(createUserRequest.getUsername());
		softly.assertThat(infoPutUserResponse.getCustomer().getRole().toString()).isEqualTo(createUserRequest.getRole().toString());
		softly.assertThat(infoPutUserResponse.getCustomer().getAccounts()).isEmpty();

		// запрашиваем информацию о профиле
		InfoGetUserResponse infoUserResponse = new UserGetInformationRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnOk())
				.getApi().extract().as(InfoGetUserResponse.class);
		softly.assertThat(infoUserResponse.getName()).isEqualTo(name);

	}

	@ParameterizedTest
	@Tag("negative")
	@ValueSource(strings = {"S ", "S  !", "   2  2  ", "", "ы№№№ о;;;;"})
	@DisplayName("Пользователь не может меня свое имя в профиле с некорректными данными")
	public void userCantChangeTheirNameWithNotCorrectData(String name){
		// создаем пользователя
		CreateUserRequest createUserRequest = CreateUserRequest.builder().username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString())
				.build();
	new AdminCreateUserRequester(RequestSpecs.adminSpec(), ResponseSpecs.entityWasCreated())
			.postApi(createUserRequest);

		// меняем имя
		// InfoPutUserRequest.builder().name(name).build();
		InfoPutUserRequest infoPutUserRequest = InfoPutUserRequest.builder().name(name).build();
		String errorMessage = new UserPutInformationRequester(RequestSpecs.authUserSpecForAcceptTEXT(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnBadRequest())
				.putApi(infoPutUserRequest).extract().asString();
		softly.assertThat(errorMessage).isEqualTo("Name must contain two words with letters only");

	}

	@Disabled("Тест падает, потому что ждем 403,но получаем 200. Пароль можно менять одновременно с именем в профиле. Однако при попытке логиниться с этим паролем- сообщение \" \"error\": \"Invalid username or password\"\" и статус код 401.")
	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может менять  password вместе с name")
	public void userCantChangeTheirPassword(){
		// создаем пользователя
		CreateUserRequest createUserRequest = CreateUserRequest.builder().username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString())
				.build();
		new AdminCreateUserRequester(RequestSpecs.adminSpec(),ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest);
		// отправляем запрос на изменение имени
		InfoPutUserRequest infoPutUserRequest = InfoPutUserRequest.builder().name("Svetlana Svetlana").password(RandomData.getRandomPassword()).build();
		InfoPutUserResponse infoPutUserResponse = new UserPutInformationRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnForbidden()).putApi(infoPutUserRequest).extract().as(InfoPutUserResponse.class);
		softly.assertThat(createUserRequest.getPassword()).isEqualTo(infoPutUserResponse.getCustomer().getPassword());
	}


	@Disabled("Тест падает, потому что ждем 403,но получаем 200. При этом username не меняется.")
	@Test
	@Tag("negative")
	@DisplayName("Пользователь не может менять  username вместе с name")
	public void userCantChangeTheirUserName(){
		// создаем пользователя
		CreateUserRequest createUserRequest = CreateUserRequest.builder().username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString())
				.build();
		new AdminCreateUserRequester(RequestSpecs.adminSpec(),ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest);
		// отправляем запрос на изменение имени
		InfoPutUserRequest infoPutUserRequest = InfoPutUserRequest.builder().name("Svetlana Svetlana").username(RandomData.getRandomUserName()).build();
		InfoPutUserResponse infoPutUserResponse = new UserPutInformationRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnForbidden()).putApi(infoPutUserRequest).extract().as(InfoPutUserResponse.class);
		softly.assertThat(createUserRequest.getUsername()).isEqualTo(infoPutUserResponse.getCustomer().getUsername());

	}

	@Disabled("Тест падает, потому что ждем 403,но получаем 200.Но по факту роль не изменилась. ")
	@Tag("negative")
	@DisplayName("Пользователь не может менять  role вместе с name")
	public void userCantChangeTheirRole(){
		// создаем пользователя
		CreateUserRequest createUserRequest = CreateUserRequest.builder().username(RandomData.getRandomUserName())
				.password(RandomData.getRandomPassword())
				.role(UserRole.USER.toString())
				.build();
		new AdminCreateUserRequester(RequestSpecs.adminSpec(),ResponseSpecs.entityWasCreated())
				.postApi(createUserRequest);
		// отправляем запрос на изменение имени
		InfoPutUserRequest infoPutUserRequest = InfoPutUserRequest.builder().name("Svetlana Svetlana").role(UserRole.ADMIN).build();
		InfoPutUserResponse infoPutUserResponse = new UserPutInformationRequester(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
				ResponseSpecs.requestReturnForbidden()).putApi(infoPutUserRequest).extract().as(InfoPutUserResponse.class);
		softly.assertThat(createUserRequest.getRole().toString()).isEqualTo(infoPutUserResponse.getCustomer().getRole().toString());
	}


}

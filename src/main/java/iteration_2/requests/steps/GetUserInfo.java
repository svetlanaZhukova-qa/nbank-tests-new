package iteration_2.requests.steps;

import iteration_2.models_body_JSON.change_name_user.InfoGetUserResponse;
import iteration_2.models_body_JSON.create_user_and_accont.CreateUserRequest;
import iteration_2.requests.skelethon.Endpoint;
import iteration_2.requests.skelethon.requesters.ValidateCrudRequester2;
import iteration_2.specs.RequestSpecs;
import iteration_2.specs.ResponseSpecs;

public class GetUserInfo {
	// 	// запрашиваем информацию профиля
	//		InfoGetUserResponse infoGetUserResponse = new ValidateCrudRequester2<InfoGetUserResponse>(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
	//				ResponseSpecs.requestReturnOk(),
	//				Endpoint.USER_INFO).get();
	public static InfoGetUserResponse getInfo(CreateUserRequest createUserRequest){
		InfoGetUserResponse infoGetUserResponse = new ValidateCrudRequester2<InfoGetUserResponse>
				(RequestSpecs.authUserSpec(createUserRequest.getUsername(), createUserRequest.getPassword()),
								ResponseSpecs.requestReturnOk(),
								Endpoint.USER_INFO).get();
		return infoGetUserResponse;
	}
}

package api.iteration_2.models_body_JSON.change_name_user;

import api.iteration_2.models_body_JSON.BaseModel;
import api.iteration_2.models_body_JSON.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoPutUserRequest extends BaseModel {
	private String name;
	private String username;
	private String password;
	private UserRole role;

}

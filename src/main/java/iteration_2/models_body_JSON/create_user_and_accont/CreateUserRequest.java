package iteration_2.models_body_JSON.create_user_and_accont;

import iteration_2.generators.GenerateRules;
import iteration_2.models_body_JSON.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest extends BaseModel {

	@GenerateRules(regex = "^[A-Za-z0-9]{3,15}$")
	private String username;

	@GenerateRules(regex = "^[A-Z]{3}[a-z]{4}[0-9]{3}[$%&]{2}$")
	private String password;

	@GenerateRules(regex = "^USER$")
	private String role;
}

package iteration_2.models_body_JSON.create_user_and_accont;

import iteration_2.models_body_JSON.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginAndGetTokenResponse extends BaseModel {
	private String username;
	private String role;
}

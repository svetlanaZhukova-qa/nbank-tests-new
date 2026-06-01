package iteration_2.models_body_JSON.create_user_and_accont;

import iteration_2.data.Account;
import iteration_2.models_body_JSON.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserResponse extends BaseModel {

	private long id;
	private String username;
	private String password;
	private String name;
	private String role;
	private List<Account> accounts;
}

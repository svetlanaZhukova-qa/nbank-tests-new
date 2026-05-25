package iteration_2.models_body_JSON.change_name_user;

import iteration_2.data.Account;
import iteration_2.models_body_JSON.BaseModel;
import iteration_2.models_body_JSON.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoGetUserResponse extends BaseModel {
	private long id;
	private String username;
	private String password;
	private String name;
	private UserRole role;
	private List<Account> accounts;
}

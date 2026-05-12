package iteration_2.models_body_JSON;

import iteration_2.data.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoGetUserResponse {
	private long id;
	private String username;
	private String password;
	private String name;
	private UserRole role;
	private List<Account> accounts;
}

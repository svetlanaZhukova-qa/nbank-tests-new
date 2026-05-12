package iteration_2.models_body_JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoPutUserRequest extends BaseModel{
	private String name;
	private String username;
	private String password;
	private UserRole role;

}

package iteration_2.models_body_JSON.change_name_user;

import iteration_2.data.Customer;
import iteration_2.models_body_JSON.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoPutUserResponse extends BaseModel {
	private String message;
	private Customer customer;
}

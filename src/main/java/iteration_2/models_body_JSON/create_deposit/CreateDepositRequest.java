package iteration_2.models_body_JSON.create_deposit;

import iteration_2.models_body_JSON.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateDepositRequest extends BaseModel {

	private int id;
	private int balance;
}

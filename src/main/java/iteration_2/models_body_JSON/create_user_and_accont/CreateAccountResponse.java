package iteration_2.models_body_JSON.create_user_and_accont;

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
public class CreateAccountResponse extends BaseModel {
	private int id;
	private String accountNumber;
	private double balance;
	private List<String> transactions;
}

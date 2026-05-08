package iteration_2.models_body_JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAccountResponse extends BaseModel{
	//{
	//    "id": 386,
	//    "accountNumber": "ACC386",
	//    "balance": 0.0,
	//    "transactions": []
	//}
	private int id;
	private String accountNumber;
	private double balance;
	private List<String> transactions;
}

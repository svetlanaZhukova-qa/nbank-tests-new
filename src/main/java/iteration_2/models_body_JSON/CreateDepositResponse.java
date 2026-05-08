package iteration_2.models_body_JSON;

import iteration_2.data.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateDepositResponse extends BaseModel{
	// {
	//    "id": 386,
	//    "accountNumber": "ACC386",
	//    "balance": 500.0,
	//    "transactions": [
	//        {
	//            "id": 423,
	//            "amount": 500.0,
	//            "type": "DEPOSIT",
	//            "timestamp": "Thu May 07 06:12:29 UTC 2026",
	//            "relatedAccountId": 386
	//        }
	//    ]
	//}
	private int id;
	private String accountNumber;
	private double balance;
	private List<Transaction> transactions;
}

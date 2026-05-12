package iteration_2.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
	//  "id": 4,
	//	//            "accountNumber": "ACC4",
	//	//            "balance": 14955.0,
	//	//            "transactions": [
	private long id;
	private String accountNumber;
	private double balance;
	private List<Transaction> transactionList;
}

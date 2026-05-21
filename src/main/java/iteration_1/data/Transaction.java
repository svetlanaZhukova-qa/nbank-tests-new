package iteration_1.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Transaction {
	private long id;
	private double amount;
	private String type;
	private String timestamp;
	private int relatedAccountId;
	//  {
	//        "id": 552,
	//        "amount": 4999.0,
	//        "type": "DEPOSIT",
	//        "timestamp": "Sun May 10 19:27:18 UTC 2026",
	//        "relatedAccountId": 549
	//    }
	//]
}

package iteration_2.models_body_JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransferResponse {
	// {
	//    "receiverAccountId": 2,
	//    "senderAccountId": 3,
	//    "message": "Transfer successful",
	//    "amount": 50.0
	//}
	private int receiverAccountId;
	private int senderAccountId;
	private String message;
	private double amount;
}

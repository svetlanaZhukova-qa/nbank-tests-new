package iteration_2.models_body_JSON.transfer_money;

import iteration_2.models_body_JSON.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransferResponse extends BaseModel {
	private int receiverAccountId;
	private int senderAccountId;
	private String message;
	private double amount;
}

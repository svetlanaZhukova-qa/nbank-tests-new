package iteration_2.data;

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
	private long relatedAccountId;
}

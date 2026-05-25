package iteration_1.models;

import iteration_1.data.Transaction;
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
	private long id;
	private String accountNumber;
	private double balance;
	private List<Transaction> transactions;

}

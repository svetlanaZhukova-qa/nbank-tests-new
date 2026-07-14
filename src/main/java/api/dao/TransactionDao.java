package api.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDao {

	private Long id;
	private BigDecimal amount;
	private String type;
	private Timestamp timestamp;
	private Long accountId;
	private Long relatedAccountId;
	private Timestamp createdAt;
}

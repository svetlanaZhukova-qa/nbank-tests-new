package iteration_2.models_body_JSON;

import iteration_2.data.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoPutUserResponse {
	private String message;
	private Customer customer;
}

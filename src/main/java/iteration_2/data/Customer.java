package iteration_2.data;

import iteration_2.models_body_JSON.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
	private long id;
	private String username;
	private String password;
	private String name;
	private UserRole role;
	private List<Account> accounts;
	//"customer": {
	//	//        "id": 427,
	//	//        "username": "kate1998",
	//	//        "password": "$2a$10$LhcPbycaXjh.fJkY2kuu7uenWVWLH/sFDPu9zjtAh8LMMlLS2vb3q",
	//	//        "name": "Swoya swoya",
	//	//        "role": "USER",
	//	//        "accounts": [
	//	//            {
	//	//                "id": 385,
	//	//                "accountNumber": "ACC385",
	//	//                "balance": 0.0,
	//	//                "transactions": []
	//	//            },
	//	//            {
	//	//                "id": 549,
	//	//                "accountNumber": "ACC549",
	//	//                "balance": 4999.0,
	//	//                "transactions": [
	//	//                    {
	//	//                        "id": 552,
	//	//                        "amount": 4999.0,
	//	//                        "type": "DEPOSIT",
	//	//                        "timestamp": "Sun May 10 19:27:18 UTC 2026",
	//	//                        "relatedAccountId": 549
	//	//                    }
	//	//                ]
	//	//            },
	//	//            {
	//	//                "id": 386,
	//	//                "accountNumber": "ACC386",
	//	//                "balance": 1000.0,
	//	//                "transactions": [
	//	//                    {
	//	//                        "id": 423,
	//	//                        "amount": 500.0,
	//	//                        "type": "DEPOSIT",
	//	//                        "timestamp": "Thu May 07 06:12:29 UTC 2026",
	//	//                        "relatedAccountId": 386
	//	//                    },
	//	//                    {
	//	//                        "id": 430,
	//	//                        "amount": 500.0,
	//	//                        "type": "DEPOSIT",
	//	//                        "timestamp": "Thu May 07 12:58:19 UTC 2026",
	//	//                        "relatedAccountId": 386
	//	//                    }
	//	//                ]
	//	//            }
	//	//        ]
	//	//    }
	//	//}
}

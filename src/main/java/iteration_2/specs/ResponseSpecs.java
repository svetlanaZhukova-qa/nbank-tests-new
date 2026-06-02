package iteration_2.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpecs {
	public static final String ERROR_MESSAGE_FORBIDDEN = "Unauthorized access to account";
	public static final String ERROR_MESSAGE_DEPOSIT_EXCEED_5000 = "Deposit amount cannot exceed 5000";
	public static final String ERROR_MESSAGE_DEPOSIT_LEAST_001 = "Deposit amount must be at least 0.01";
	public static final String ERROR_MESSAGE_NOT_VALID_NAME = "Name must contain two words with letters only";
	public static final String ERROR_MESSAGE_TRANSFER_EXCEED_10000 = "Transfer amount cannot exceed 10000";
	public static final String ERROR_MESSAGE_TRANSFER_LEAST_001 = "Transfer amount must be at least 0.01";
	public static final String ERROR_MESSAGE_FORBIDDEN_PERMISSION = "You do not have permission to access this account";
	public static final String MESSAGE_TRANSFER_SUCCESSFUL = "Transfer successful";
	public static final String MESSAGE_SUCCESSFUL_UPDATE_PROFILE = "Profile updated successfully";



	private ResponseSpecs(){}

	private static ResponseSpecBuilder defaultResponseSpecBuilder(){
		return new ResponseSpecBuilder();
	}

	public static ResponseSpecification entityWasCreated(){
		return defaultResponseSpecBuilder().expectStatusCode(201).build();
	}
	public static ResponseSpecification requestReturnOk(){
		return defaultResponseSpecBuilder().expectStatusCode(200).build();
	}

	public static ResponseSpecification requestReturnBadRequest(){
		return defaultResponseSpecBuilder().expectStatusCode(400).build();
	}

	public static ResponseSpecification requestReturnForbidden(){
		return defaultResponseSpecBuilder().expectStatusCode(403).build();
	}
}

package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
	USER_CREATED_SUCCESSFULLY("✅ User created successfully!", false),
	USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters", false),
	NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number: ", false),
	SUCCESSFULLY_DEPOSITED("✅ Successfully deposited $%s to account %s!", true),
	FAILED_DEPOSIT("❌ Please deposit less or equal to 5000$.", false),
	SUCCESSFULLY_TRANSFERRED("Successfully transferred $%s to account %s!", true),
	FAILED_TRANSFER("❌ Error: Transfer amount must be at least 0.01", false);


	private final String template;
	private final boolean isTemplate;

	BankAlert(String template, boolean isTemplate) {
		this.template = template;
		this.isTemplate = isTemplate;
	}

	public String format(Object... args) {
		if (!isTemplate && args.length > 0) {
			throw new IllegalArgumentException("Alert " + name() + " is constant, no args needed");
		}
		return String.format(template, args);
	}


}

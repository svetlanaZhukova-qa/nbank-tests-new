package iteration_2.generators;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomData {
	private RandomData(){}

	public static String getRandomUserName(){
		return RandomStringUtils.randomAlphabetic(10);
	}

	public static String getRandomPassword(){
		return RandomStringUtils.randomAlphabetic(3).toUpperCase() +
				RandomStringUtils.randomAlphabetic(5).toLowerCase() +
				RandomStringUtils.randomNumeric(3) + "$" ;
	}
}

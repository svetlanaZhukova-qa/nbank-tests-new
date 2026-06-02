package iteration_1.models.comparison;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ModelComparisonConfigLoader {

	private final Map<String, ComparisonRule> rules = new HashMap<>();

	public ModelComparisonConfigLoader(String configFile) {
		try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
			if (input == null) {
				throw new IllegalArgumentException("Config file not found: " + configFile);
			}
			Properties props = new Properties();
			props.load(input);
			for (String key : props.stringPropertyNames()) {
				// key теперь вида "ClassA->ClassB"
				String[] keyParts = key.split("->");
				if (keyParts.length != 2) {
					throw new IllegalArgumentException(
							"Invalid key format: '" + key + "'. Expected: 'ClassA->ClassB'"
					);
				}
				String requestClassName = keyParts[0].trim();
				String responseClassName = keyParts[1].trim();

				String value = props.getProperty(key);
				String[] target = value.split(":");
				if (target.length != 2) {
					throw new IllegalArgumentException(
							"Invalid value format: '" + value + "'. Expected: 'ResponseClass:field1=field1Resp,...'"
					);
				}

				String responseClassSimpleName = target[0].trim();
				List<String> fields = Arrays.asList(target[1].split(","));

				// Составной ключ: "ClassA->ClassB"
				rules.put(key.trim(), new ComparisonRule(responseClassSimpleName, fields));
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to load DTO comparison config", e);
		}
	}

	public ComparisonRule getRuleFor(Class<?> requestClass, Class<?> responseClass) {
		String key = requestClass.getSimpleName() + "->" + responseClass.getSimpleName();
		return rules.get(key);
	}

	public static class ComparisonRule {
		private final String responseClassSimpleName;
		private final Map<String, String> fieldMappings;

		public ComparisonRule(String responseClassSimpleName, List<String> fieldPairs) {
			this.responseClassSimpleName = responseClassSimpleName;
			this.fieldMappings = new HashMap<>();

			for (String pair : fieldPairs) {
				String[] parts = pair.split("=");
				if (parts.length == 2) {
					fieldMappings.put(parts[0].trim(), parts[1].trim());
				} else {
					fieldMappings.put(pair.trim(), pair.trim());
				}
			}
		}

		public String getResponseClassSimpleName() {
			return responseClassSimpleName;
		}

		public Map<String, String> getFieldMappings() {
			return fieldMappings;
		}
	}
}
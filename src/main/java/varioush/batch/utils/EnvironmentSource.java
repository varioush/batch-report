package varioush.batch.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import varioush.batch.constant.Constants;

@Component
public class EnvironmentSource {

	@Autowired
	Environment source;

	public String getAndFormat(String subject, String label) {

		String value = get(subject, label);

		return ExprEvaluator.process(value);
	}

	public String get(String subject, String label) {

		return get(subject + Constants.CHAR.DOT + label);
	}

	public <T> T get(String key, Class<T> clazz) {

		return source.getProperty(key, clazz);
	}

	public String get(String key) {

		return source.getProperty(key);
	}

	public String format(String expression) {
		
		return ExprEvaluator.process(expression);
	}

}

package varioush.batch.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

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

		return format(value);
	}

	public String get(String subject, String label) {

		return get(subject + Constants.CHAR_DOT + label);
	}

	public <T> T get(String key, Class<T> clazz) {

		return source.getProperty(key, clazz);
	}

	public String get(String key) {

		return source.getProperty(key);
	}

	public String format(String expression) {
		Matcher m = Constants.Z_PATTERN.matcher(expression);

		if (m.find()) {
			String format = m.group(2);
			DateFormat formatter = new SimpleDateFormat(format);
			return expression.replace(m.group(0),
					formatter.format(new Date(new Date().getTime() - Constants.MILLIS_IN_A_DAY)));
		}

		throw new RuntimeException();

	}

}

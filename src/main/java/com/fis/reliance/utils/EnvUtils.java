package com.fis.reliance.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fis.reliance.constant.Constants;

@Component
public class EnvUtils {

	@Autowired
	Environment env;

	public String getAndFormat(String subject, String label) {

		String value = get(subject, label);

		return format(value);
	}

	public String get(String subject, String label) {

		return get(subject + Constants.CHAR_DOT + label);
	}

	public <T> T get(String key, Class<T> clazz) {

		return env.getProperty(key, clazz);
	}

	public String get(String key) {

		return env.getProperty(key);
	}

	public String format(String expression) {
		Matcher m = Constants.pattern.matcher(expression);

		if (m.find()) {
			String format = m.group(2);
			DateFormat formatter = new SimpleDateFormat(format);
			return expression.replace(m.group(0),
					formatter.format(new Date(new Date().getTime() - Constants.MILLIS_IN_A_DAY)));
		}

		throw new RuntimeException();

	}

}

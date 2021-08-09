package varioush.batch.processor;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import varioush.batch.constant.Constants;
import varioush.batch.utils.EnvUtils;

@Component
@StepScope
public class CustomItemProcessor implements ItemProcessor<Map<String, Object>, String> {

	@SuppressWarnings(Constants.UNUSED)
	private static final Logger logger = LoggerFactory.getLogger(CustomItemProcessor.class);

	@Value(Constants.JOB_PARAM_SUBJECT)
	String subject;

	@Autowired
	EnvUtils env;

	@Override
	public String process(Map<String, Object> item) throws Exception {
		// no logger used to avoid over-logging
		
		String columns = env.get(subject, Constants.LABEL_COLUMNS);
		String prefix = env.get(subject,Constants.LABEL_PREFIX);
		String postfix = env.get(subject,Constants.LABEL_POSTFIX);

		String[] columnArray = Arrays.stream(columns.split(Constants.CHAR_COMMA)).map(String::trim).toArray(String[]::new);

		String content = Constants.CHAR_BLANK;

		String delimiter = env.get(env.get(subject, Constants.DELIMITER));

		if (prefix != null) {
			content = content.concat(prefix);
		}

		for (String column : columnArray) {
			Object obj = item.get(column);

			String value = Constants.CHAR_BLANK;
			if (obj != null) {
				value = obj.toString();
			}
			content = content.concat(delimiter).concat(value);

		}

		if (postfix != null) {
			content = content.concat(delimiter).concat(postfix);
		}

		return content;
	}

}

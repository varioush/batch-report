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
import varioush.batch.utils.EnvironmentSource;

@Component
@StepScope
public class CustomItemProcessor implements ItemProcessor<Map<String, Object>, String> {

	@SuppressWarnings(Constants.OTHER.UNUSED)
	private static final Logger logger = LoggerFactory.getLogger(CustomItemProcessor.class);

	@Value(Constants.JOB_DEF.JOB_PARAM_SUBJECT)
	String subject;

	@Autowired
	EnvironmentSource source;

	@Override
	public String process(Map<String, Object> item) throws Exception {
		// no logger used to avoid over-logging
		
		String columns = source.get(subject, Constants.LABEL.COLUMNS);
		String prefix = source.get(subject,Constants.LABEL.PREFIX);
		String postfix = source.get(subject,Constants.LABEL.POSTFIX);

		String[] columnArray = Arrays.stream(columns.split(Constants.CHAR.COMMA)).map(String::trim).toArray(String[]::new);

		String content = Constants.CHAR.BLANK;

		String delimiter = source.get(source.get(subject, Constants.LABEL.DELIMITER));

		if (prefix != null) {
			content = content.concat(prefix);
		}

		for (String column : columnArray) {
			Object obj = item.get(column);

			String value = Constants.CHAR.BLANK;
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

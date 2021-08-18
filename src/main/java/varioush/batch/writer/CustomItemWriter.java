package varioush.batch.writer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import varioush.batch.constant.Constants;
import varioush.batch.utils.Writer;

@Component
@StepScope
public class CustomItemWriter implements ItemWriter<String> {

	private static final Logger logger = LoggerFactory.getLogger(CustomItemWriter.class);

	@Value(Constants.JOB_DEF.JOB_PARAM_FILENAME)
	String filename;

	@Override
	public void write(List<? extends String> items) throws Exception {

		logger.info("Writing is in progress start!!!, filename:{}", filename);

		if (items != null) {
			String content = Constants.CHAR.BLANK;
			String newLine = Constants.OTHER.NEW_LINE;
			String delimeter = Constants.OTHER.NEW_LINE;
			for (Object item : items) {

				content = content.concat(delimeter).concat(item.toString());
				delimeter = newLine;

			}
			Writer writer = new Writer();
			writer.file(filename).content(content).build();

		}

		logger.info("Writing is in progress end!!!, filename:{}", filename);
	}

}
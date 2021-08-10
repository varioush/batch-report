package varioush.batch.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import varioush.batch.constant.Constants;
import varioush.batch.listener.InterceptingJobExecution;
import varioush.batch.processor.CustomItemProcessor;
import varioush.batch.reader.CustomItemReader;
import varioush.batch.utils.EnvironmentSource;
import varioush.batch.writer.CustomItemWriter;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

	@SuppressWarnings(Constants.UNUSED)
	private static final Logger logger = LoggerFactory.getLogger(JobConfiguration.class);

	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private CustomItemReader itemReader;

	@Autowired
	@StepScope
	private CustomItemWriter customItemWriter;

	@Autowired
	private CustomItemProcessor customItemProcessor;

	@Autowired
	private EnvironmentSource source;

	@StepScope
	@Bean(Constants.BEAN_READER_JOB)
	public JdbcPagingItemReader<Map<String, Object>> customItemReader() {

		return itemReader.read();
	}

	@Bean(name = Constants.BEAN_EXPORT_JOB)
	public Job exportBusinessDailyDataJob(@Autowired @Qualifier(Constants.BEAN_READER_STEP) Step studentStep,
			@Autowired InterceptingJobExecution interceptingJob) {

		return jobBuilderFactory.get(Constants.BEAN_JOB_IDENTIFIER).incrementer(new RunIdIncrementer())
				.flow(studentStep).end().listener(interceptingJob).build();// .listener(studentJobListener)
	}

	@Bean(Constants.BEAN_READER_STEP)
	public Step businessReaderStep(
			@Autowired @Qualifier(Constants.BEAN_READER_JOB) JdbcPagingItemReader<Map<String, Object>> reader) {
		return stepBuilderFactory.get(Constants.BEAN_READER_STEP)
				.<Map<String, Object>, String>chunk(Integer.parseInt(source.get(Constants.LABEL_FETCH_SIZE)))
				.reader(reader).writer(customItemWriter).processor(customItemProcessor).build();
	}

}

package varioush.batch.config;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import varioush.batch.constant.Constants;
import varioush.batch.utils.EnvironmentSource;

@Configuration
@EnableScheduling
public class SchedulerConfiguration implements SchedulingConfigurer {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerConfiguration.class);

	@Bean
	public Executor taskExecutor() {
		return Executors.newFixedThreadPool(executorThreadPoolSize);

	}

	@Value("${default.task.executor.size:4}")
	public void setExecutorThreadPoolSize(int executorThreadPoolSize) {
		this.executorThreadPoolSize = executorThreadPoolSize;
	}

	private int executorThreadPoolSize;

	@Autowired
	EnvironmentSource source;

	List<CronTask> cronTasks;

	@Autowired
	JobLauncher launcher;

	@Autowired
	@Qualifier(Constants.BEAN_EXPORT_JOB)
	Job ebdJob;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// "Calls scheduleTasks() at bean construction time" - docs

		String listOfSubjects = source.get(Constants.LABEL_SUBJECT_LIST);

		String[] subjects = listOfSubjects.split(Constants.CHAR_COMMA);

		for (String subject : subjects) {

			CronTask ct = new CronTask(new Runnable() {
				@Override
				public void run() {
					final JobParameters jobParameters = jobParameters(subject);

					try {
						launcher.run(ebdJob, jobParameters);
					} catch (JobExecutionException e) {
						logger.warn("cannot execute reportJob");
					}
				}
			}, source.get(source.get(subject, Constants.LABEL_CRON)));

			taskRegistrar.addCronTask(ct);
		}
		taskRegistrar.afterPropertiesSet();
	}

	public CronTask createCronTask(Runnable action, String expression) {
		return new CronTask(action, new CronTrigger(expression));
	}

	private JobParameters jobParameters(String subject) {
		String filename = source.getAndFormat(subject, Constants.LABEL_FILENAME);
		logger.info("Subject:{}, File Name is :{}", subject, filename);
		return new JobParametersBuilder().addLong(Constants.LABEL_DATE, new Date().getTime())
				.addString(Constants.LABEL_FILENAME, filename).addString(Constants.LABEL_SUBJECT, subject)
				.toJobParameters();

	}

}

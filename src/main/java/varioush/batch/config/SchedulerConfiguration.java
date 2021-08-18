package varioush.batch.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import varioush.batch.config.SFTPConfiguration.UploadGateway;
import varioush.batch.constant.Constants;
import varioush.batch.utils.EnvironmentSource;
import varioush.batch.utils.FileFunctions;

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
	@Qualifier(Constants.JOB_DEF.JOB_BEAN_EXPORT)
	Job ebdJob;

	@Value("${sftp.remote.directory:/}")
	private String sftpRemoteDirectory;

	@Autowired
	private UploadGateway gateway;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// "Calls scheduleTasks() at bean construction time" - docs

		String listOfSubjects = source.get(Constants.LABEL.SUBJECT_LIST);

		String[] subjects = listOfSubjects.split(Constants.CHAR.COMMA);

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
			}, source.get(source.get(subject, Constants.LABEL.CRON)));

			taskRegistrar.addCronTask(ct);
		}
		taskRegistrar.afterPropertiesSet();
	}

	public CronTask createCronTask(Runnable action, String expression) {
		return new CronTask(action, new CronTrigger(expression));
	}

	private JobParameters jobParameters(String subject) {
		String ftpPath = source.getAndFormat(subject, Constants.LABEL.FILENAME);
		String filename = Paths
				.get(FileFunctions.getPath(Constants.FOLDER.INITIATED.name()).toAbsolutePath().toString(), ftpPath)
				.toAbsolutePath().toString();
		logger.info("Subject:{}, File Name is :{}", subject, filename);
		return new JobParametersBuilder().addLong(Constants.LABEL.DATE, new Date().getTime())
				.addString(Constants.LABEL.FILENAME, filename).addString(Constants.LABEL.SUBJECT, subject)
				.addString(Constants.LABEL.FTP_PATH, ftpPath).toJobParameters();

	}

	@Scheduled(cron = Constants.OTHER.CRON_UPLOAD)
	public void scheduleUploadToSFtp() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
			JobRestartException, JobInstanceAlreadyCompleteException, IOException {
		
		
		logger.info("Processing SFTP Items");

		Path path = FileFunctions.getPath(Constants.FOLDER.PROCESSED.name());
		Path completedPath = FileFunctions.getPath(Constants.FOLDER.COMPLETED.name());

		if (Files.isDirectory(path)) {
			try (Stream<Path> dirs = Files.list(path)) {
				for (Iterator<Path> iterator = dirs.iterator(); iterator.hasNext();) {
					Path dirPath = iterator.next();
					if (Files.isDirectory(dirPath)) {
						try (Stream<Path> files = Files.list(dirPath)) {
							for (Iterator<Path> iteratorFile = files.iterator(); iteratorFile.hasNext();) {
								Path file = iteratorFile.next();
								try {
									if (Files.isRegularFile(file)) {
										gateway.upload(file.toFile(),
												sftpRemoteDirectory + "/" + dirPath.getFileName().toString());

										Path destFolder = Paths.get(completedPath.toAbsolutePath().toString(),
												dirPath.getFileName().toString());// +File.separator+file.getName());

										Files.createDirectories(destFolder);

										Path dest = Paths.get(destFolder.toAbsolutePath().toString(),
												file.getFileName().toString());

										logger.info("source:{}, dest:{}", file, dest);
										Files.move(file, dest, StandardCopyOption.REPLACE_EXISTING);

									}

								} catch (RuntimeException rx) {
									rx.printStackTrace();
									logger.warn("Will Retry because SFTP is down.");
								}
							}
						}
					}
				}
			} 
		}

	}

}

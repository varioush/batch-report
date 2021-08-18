package varioush.batch.listener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import varioush.batch.constant.Constants;
import varioush.batch.constant.Constants.FOLDER;
import varioush.batch.utils.EnvironmentSource;
import varioush.batch.utils.FileFunctions;
import varioush.batch.utils.Writer;

@Component
public class InterceptingJobExecution implements JobExecutionListener {

	private static final Logger logger = LoggerFactory.getLogger(InterceptingJobExecution.class);

	@Autowired
	private EnvironmentSource source;

	@Value("${sftp.remote.directory:/}")
	private String sftpRemoteDirectory;

	@Override
	public void beforeJob(JobExecution jobExecution) {

		logger.info("Before initializing job ");

		String filename = jobExecution.getJobParameters().getString(Constants.LABEL.FILENAME);

		String subject = jobExecution.getJobParameters().getString(Constants.LABEL.SUBJECT);

		logger.info("@BeforeJob : Subject:{}, File Name :{}", subject, filename);

		String content = source.get(subject, Constants.LABEL.HEADER);

		content = source.format(content);

		Writer writer = new Writer();
		writer.file(filename).content(content).build();

		logger.info("Finishing Intercepting Job Excution - Before Job!");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {

		logger.info("After Finishing job ");

		int noOfItemsProcessed = 0;
		for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
			noOfItemsProcessed += stepExecution.getWriteCount();
		}
		String ftpPath = jobExecution.getJobParameters().getString(Constants.LABEL.FTP_PATH);

		String filename = jobExecution.getJobParameters().getString(Constants.LABEL.FILENAME);

		String subject = jobExecution.getJobParameters().getString(Constants.LABEL.SUBJECT);

		logger.info("@AfterJob : Subject:{}, File Name :{}", subject, filename);

		String content = Constants.OTHER.NEW_LINE + source.get(subject, Constants.LABEL.FOOTER);

		content = content.replace(Constants.OTHER.EXP_COUNT, Integer.toString(noOfItemsProcessed));

		Writer writer = new Writer();
		
		writer.file(filename).content(content).build();
		
		
		String parentPath = ftpPath.substring(0, ftpPath.indexOf("/"));

		try
		{
			Path path = FileFunctions.getPath(FOLDER.PROCESSED.name());
			
			Path dirPath  = Paths.get(path.toAbsolutePath().toString(), parentPath);
			
			Files.createDirectories(dirPath);
			
			Path source =Paths.get(filename); 
			Path dest = Paths.get(path.toAbsolutePath().toString(),ftpPath);
			logger.info("source:{}, dest:{}", source, dest);
			Files.move(source,dest, StandardCopyOption.REPLACE_EXISTING);
			
		}
		catch(Exception ex)
		{
			logger.error("Error while finishing job:",ex);
		}
		logger.info("Finishing Intercepting Job Excution - After Job!");
	}

}

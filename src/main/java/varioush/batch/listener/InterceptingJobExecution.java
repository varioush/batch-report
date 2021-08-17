package varioush.batch.listener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import varioush.batch.config.SFTPConfiguration.UploadGateway;
import varioush.batch.constant.Constants;
import varioush.batch.utils.EnvironmentSource;
import varioush.batch.utils.Functions;

@Component
public class InterceptingJobExecution implements JobExecutionListener {

	private static final Logger logger = LoggerFactory.getLogger(InterceptingJobExecution.class);

	@Autowired
	private UploadGateway gateway;

	@Autowired
	private EnvironmentSource source;

	@Value("${sftp.remote.directory:/}")
	private String sftpRemoteDirectory;

	@Override
	public void beforeJob(JobExecution jobExecution) {

		logger.info("Before initializing job ");

		String filename = jobExecution.getJobParameters().getString(Constants.LABEL_FILENAME);

		String subject = jobExecution.getJobParameters().getString(Constants.LABEL_SUBJECT);

		logger.info("@BeforeJob : Subject:{}, File Name :{}", subject, filename);

		String header = source.get(subject, Constants.LABEL_HEADER);

		header = source.format(header);

		Functions.createAndWrite(filename, header);

		logger.info("Finishing Intercepting Job Excution - Before Job!");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {

		logger.info("After Finishing job ");

		int noOfItemsProcessed = 0;
		for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
			noOfItemsProcessed += stepExecution.getWriteCount();
		}
		String ftpPath = jobExecution.getJobParameters().getString(Constants.LABEL_FTP_PATH);

		String filename = jobExecution.getJobParameters().getString(Constants.LABEL_FILENAME);

		String subject = jobExecution.getJobParameters().getString(Constants.LABEL_SUBJECT);

		logger.info("@AfterJob : Subject:{}, File Name :{}", subject, filename);

		String footer = source.get(subject, Constants.LABEL_FOOTER);

		footer = footer.replace(Constants.EXP_COUNT, Integer.toString(noOfItemsProcessed));

		Functions.write(filename, footer);

		logger.info("Initializing Uploading to SFTP!, FileName is {}", filename);

		String parentPath = ftpPath.substring(0, ftpPath.indexOf("/"));
		String ftpFileName = ftpPath.substring(ftpPath.indexOf("/")+1);
		boolean isError = false;
		try {
			
			gateway.upload(new File(filename), sftpRemoteDirectory + "/" + parentPath);
			String path = Functions.path(Constants.FOLDER.DONE);
			File completed = new File(path);
			if(!completed.exists())
			{
				completed.mkdirs();
			}
			
			File donePath = new File(path+File.separator+parentPath);
			if(!donePath.exists())
			{
				donePath.mkdirs();
			}
			
			Files.move(Paths.get(filename),Paths.get(path+File.separator+ftpPath));
			
		//	File pendingPath = new File
		} catch (Exception ex) {
			isError = true;
			String path = Functions.path(Constants.FOLDER.PENDING);
			File completed = new File(path);
			if(!completed.exists())
			{
				completed.mkdirs();
			}
			
			File donePath = new File(path+File.separator+parentPath);
			if(!donePath.exists())
			{
				donePath.mkdirs();
			}
			
			try {
				Files.move(Paths.get(filename),Paths.get(path+File.separator+ftpPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			logger.error("The transferred of {} interuppted due to ", filename, ex);
		}
		if(isError) {
			logger.info("Successfully Uploaded to SFTP!, FileName is {}", filename);
		}
		else
		{
			logger.warn("Scheduler will try again after 15 minute to copy file");
		}
		logger.info("Finishing Intercepting Job Excution - After Job!");
	}

}

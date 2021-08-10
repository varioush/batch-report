package com.fis.reliance.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fis.reliance.config.SftpConfiguration.UploadGateway;
import com.fis.reliance.constant.Constants;
import com.fis.reliance.utils.EnvUtils;
import com.fis.reliance.utils.WriterUtils;

@Component
public class InterceptingJobExecution implements JobExecutionListener {

	private static final Logger logger = LoggerFactory.getLogger(InterceptingJobExecution.class);

	@Autowired
	private UploadGateway gateway;

	@Autowired
	private EnvUtils env;

	@Override
	public void beforeJob(JobExecution jobExecution) {

		logger.info("Before initializing job ");

		String filename = jobExecution.getJobParameters().getString(Constants.LABEL_FILENAME);

		String subject = jobExecution.getJobParameters().getString(Constants.LABEL_SUBJECT);

		logger.info("@BeforeJob : Subject:{}, File Name :{}", subject, filename);

		String header = env.get(subject, Constants.LABEL_HEADER);

		header = env.format(header);

		new WriterUtils().writeNew(filename, header);

		logger.info("Finishing Intercepting Job Excution - Before Job!");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {

		logger.info("After Finishing job ");

		int noOfItemsProcessed = 0;
		for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
			noOfItemsProcessed += stepExecution.getWriteCount();
		}

		String filename = jobExecution.getJobParameters().getString(Constants.LABEL_FILENAME);

		String subject = jobExecution.getJobParameters().getString(Constants.LABEL_SUBJECT);

		logger.info("@AfterJob : Subject:{}, File Name :{}", subject, filename);

		String footer = env.get(subject, Constants.LABEL_FOOTER);

		footer = footer.replace(Constants.EXP_COUNT, Integer.toString(noOfItemsProcessed));

		new WriterUtils().write(filename, footer);

		logger.info("Initializing Uploading to SFTP!, FileName is {}", filename);

		gateway.upload(new WriterUtils().getFile(filename));

		logger.info("Successfully Uploaded to SFTP!, FileName is {}", filename);

		logger.info("Finishing Intercepting Job Excution - After Job!");
	}

}

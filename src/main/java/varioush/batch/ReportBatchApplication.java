package varioush.batch;


import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import varioush.batch.config.SchedulerConfiguration;
import varioush.batch.constant.Constants;
import varioush.batch.constant.Constants.FOLDER;
import varioush.batch.utils.EnvironmentSource;
import varioush.batch.utils.Functions;

@SpringBootApplication

public class ReportBatchApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(ReportBatchApplication.class);

	public static void main(String[] args) {
		logger.info("Application Starting");
		SpringApplication.run(ReportBatchApplication.class, args);
	}

	
	@Autowired
	SchedulerConfiguration scheduler;
	
	
	@Autowired
	EnvironmentSource source;

	@Override
	public void run(String... args) throws Exception {
		
		
		for (FOLDER folder : Constants.FOLDER.values()) {
			File file = new File(Functions.path(folder.name()));
			if(file.exists() && file.isDirectory())
			{
				logger.info("{} Folder already exists!!",folder.name());
			}
			else
			{
				file.mkdirs();
			}
		}
		
		
//		try
//		{
//			
//			String retainDay = source.get(Constants.DAY_RETAIN);
//			Integer days = 30;
//			try
//			{
//				days = Integer.parseInt(retainDay);
//				logger.info("Removing stale record older than {} days", days);
//			}
//			catch(Exception ex)
//			{
//				logger.warn("Removing stale record older than {} days", days);
//			}
//		//	Functions.deleteFilesOlderThanNdays(days);
//		}
//		catch(Exception ex)
//		{
//			logger.warn("Nothing to worries!! Delete {} yourself", Constants.DIR_TEMP);
//		}
//		
		ScheduledTaskRegistrar taskRegistrar = new ScheduledTaskRegistrar();

		scheduler.configureTasks(taskRegistrar);
		Thread.sleep(51);

		taskRegistrar.destroy();
		taskRegistrar = null;

	}

	

}

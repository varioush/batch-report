package varioush.batch;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import varioush.batch.config.SchedulerConfiguration;
import varioush.batch.constant.Constants;
import varioush.batch.utils.EnvironmentSource;
import varioush.batch.utils.FileFunctions;

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
		
		
		FileFunctions.builtDirectoryStructure();
		
		
		try
		{
			
			String retainDay = source.get(Constants.LABEL.DAY_RETAIN);
			Long days = 40l;
			try
			{
				Long no_of_days = Long.parseLong(retainDay);
				
				if(no_of_days>days)
				{
					days = no_of_days;
				}
				logger.info("Removing stale record older than {} days", days);
			}
			catch(Exception ex)
			{
				
				logger.warn("Removing stale record older than {} days", days);
			}
			FileFunctions.deleteFilesOlderThanNdays(days);
		}
		catch(Exception ex)
		{
			logger.warn("Nothing to worries!! Delete {} yourself", Constants.OTHER.DIR_TEMP);
		}
		
		ScheduledTaskRegistrar taskRegistrar = new ScheduledTaskRegistrar();

		scheduler.configureTasks(taskRegistrar);
		Thread.sleep(51);

		taskRegistrar.destroy();
		taskRegistrar = null;

	}

	

}

package varioush.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import varioush.batch.config.SchedulerConfiguration;

@SpringBootApplication

public class ReportBatchApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(ReportBatchApplication.class);

	public static void main(String[] args) {
		logger.info("Application Starting");
		SpringApplication.run(ReportBatchApplication.class, args);
	}

	
	@Autowired
	SchedulerConfiguration scheduler;
	

	@Override
	public void run(String... args) throws Exception {

		ScheduledTaskRegistrar taskRegistrar = new ScheduledTaskRegistrar();

		scheduler.configureTasks(taskRegistrar);
		Thread.sleep(51);

		taskRegistrar.destroy();
		taskRegistrar = null;

	}

	

}

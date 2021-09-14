/*
 * 
 */

package varioush.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import varioush.batch.config.SchedulerConfiguration;
import varioush.batch.utils.Functions;

// TODO: Auto-generated Javadoc
/**
 * The Class ReportBatchApplication.
 */
@SpringBootApplication(scanBasePackages = {
        "varioush"
})

public class ReportBatchApplication implements CommandLineRunner {

    /** The log. */
    static Logger LOG = LoggerFactory.getLogger(ReportBatchApplication.class);

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        LOG.info("Application Starting");
        SpringApplication.run(ReportBatchApplication.class, args);
        
    }

    /** The scheduler. */
    @Autowired
    SchedulerConfiguration scheduler;

    /** The functions. */
    @Autowired
    Functions functions;

    /**
     * Run.
     *
     * @param args the args
     * @throws Exception the exception
     */
    @Override
    public void run(final String... args) throws Exception {

        Functions.builtDirectoryStructure();

        try {

            String retainDay = functions.get(Functions.DAY_RETAIN);
            Long days = 40l;
            try {
                Long no_of_days = Long.parseLong(retainDay);

                if (no_of_days > days) {
                    days = no_of_days;
                }
                LOG.info("Removing stale record older than {} days", days);
            } catch (Exception ex) {

                LOG.warn("Removing stale record older than {} days", days);
            }
            Functions.deleteFilesOlderThanNdays(days);
        } catch (Exception ex) {
            LOG.warn("Nothing to worries!! Delete {} yourself", Functions.DIR_TEMP);
        }

        ScheduledTaskRegistrar taskRegistrar = new ScheduledTaskRegistrar();

        scheduler.configureTasks(taskRegistrar);
        Thread.sleep(51);

        taskRegistrar.destroy();
        taskRegistrar = null;

    }

}

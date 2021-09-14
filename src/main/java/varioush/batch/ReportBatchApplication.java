/*
 * 
 */

package varioush.batch;

import static com.cronutils.model.field.expression.FieldExpressionFactory.always;
import static com.cronutils.model.field.expression.FieldExpressionFactory.on;
import static com.cronutils.model.field.expression.FieldExpressionFactory.questionMark;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.expression.FieldExpression;
import com.cronutils.model.field.expression.FieldExpressionFactory;

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
            Cron cron = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
                .withDoM(always())
                .withMonth(always())
                .withDoW(questionMark())
                .withHour(on(0))
                .withMinute(on(0))
                .withSecond(on(0))
                .instance();
            // Obtain the string expression
            String cronAsString = cron.asString(); // 0 * * L-3 * ? *
            System.out.println(cronAsString);
            
            LOG.info("Application Starting");
            cron = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
                    .withDoM(on(1))
                    .withMonth(always())
                    .withDoW(questionMark())
                    .withHour(on(0))
                    .withMinute(on(0))
                    .withSecond(on(0))
                    .instance();
                // Obtain the string expression
                cronAsString = cron.asString(); // 0 * * L-3 * ? *
                System.out.println(cronAsString);
                
                LOG.info("Application Starting");
                List<FieldExpression> fields = new ArrayList<>();
                fields.add(on(3));
                fields.add(on(6));
                fields.add(on(9));
                fields.add(on(15));
                fields.add(on(18));
                fields.add(on(21));
                
                
                cron = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
                        .withDoM(always())
                        .withMonth(always())
                        .withDoW(questionMark())
                        .withHour(FieldExpressionFactory.and(fields))
                        .withMinute(on(0))
                        .withSecond(on(0))
                        .instance();
                    // Obtain the string expression
                    cronAsString = cron.asString(); // 0 * * L-3 * ? *
                    System.out.println(cronAsString);   
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

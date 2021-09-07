/*
 * 
 */

package varioush.batch.config;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.Date;
import java.util.Iterator;
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
import varioush.batch.utils.Functions;

// TODO: Auto-generated Javadoc
/**
 * The Class SchedulerConfiguration.
 */
@Configuration
@EnableScheduling
public class SchedulerConfiguration implements SchedulingConfigurer {

    /** The log. */
    final Logger LOG = LoggerFactory.getLogger(SchedulerConfiguration.class);

    /**
     * Task executor.
     *
     * @return the executor
     */
    @Bean
    public Executor taskExecutor() {
        return Executors.newFixedThreadPool(this.executorThreadPoolSize);

    }

    /**
     * Sets the executor thread pool size.
     *
     * @param executorThreadPoolSize the new executor thread pool size
     */
    @Value("${default.task.executor.size:4}")
    public void setExecutorThreadPoolSize(final int executorThreadPoolSize) {
        this.executorThreadPoolSize = executorThreadPoolSize;
    }

    /** The executor thread pool size. */
    private int executorThreadPoolSize;

    /** The functions. */
    @Autowired
    private Functions functions;

    /** The launcher. */
    @Autowired
    private JobLauncher launcher;

    /** The ebd job. */
    @Autowired
    @Qualifier(Functions.JOB_BEAN_EXPORT)
    private Job ebdJob;

    /** The sftp remote directory. */
    @Value("${sftp.remote.directory:/}")
    private String sftpRemoteDirectory;

    /** The gateway. */
    @Autowired
    private UploadGateway gateway;

    /**
     * Configure tasks.
     *
     * @param taskRegistrar the task registrar
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // "Calls scheduleTasks() at bean construction time" - docs

        String listOfSubjects = functions.get(Functions.SUBJECT_LIST);

        String[] subjects = listOfSubjects.split(Functions.COMMA);

        for (String subject : subjects) {

            CronTask ct = new CronTask(new Runnable() {
                @Override
                public void run() {
                    final JobParameters jobParameters = jobParameters(subject);

                    try {
                        launcher.run(ebdJob, jobParameters);
                    } catch (JobExecutionException e) {
                        LOG.warn("cannot execute reportJob");
                    }
                }
            }, functions.get(functions.get(subject, Functions.CRON)));

            taskRegistrar.addCronTask(ct);
        }
        taskRegistrar.afterPropertiesSet();
    }

    /**
     * Creates the cron task.
     *
     * @param action     the action
     * @param expression the expression
     * @return the cron task
     */
    public CronTask createCronTask(Runnable action, String expression) {
        return new CronTask(action, new CronTrigger(expression));
    }

    /**
     * Job parameters.
     *
     * @param subject the subject
     * @return the job parameters
     */
    private JobParameters jobParameters(String subject) {
        String ftpFileName = functions.getAndFormat(subject, Functions.FILENAME);
        String ftpDir = functions.getAndFormat(subject, Functions.DIR);

        Path readPath = Functions.getPath(Functions.FOLDER.read.name());
        String filename = Paths.get(readPath.toAbsolutePath().toString(), ftpFileName).toAbsolutePath().toString();
        LOG.info("Subject:{}, File Name is :{}", subject, filename);

        String query = functions.get(subject, Functions.QUERY);
        String orderBy = functions.get(subject, Functions.ORDER_BY);
        Functions.SQL sql = Functions.process(query, orderBy);
        LOG.info("SQL Meta Data:{}", sql);
        functions.putSubject(subject, sql);
        return new JobParametersBuilder().addLong(Functions.DATE, new Date().getTime())
                .addString(Functions.FILENAME, filename).addString(Functions.SUBJECT, subject)
                .addString(Functions.FTP_FILE_NAME, ftpFileName).addString(Functions.FTP_PATH, ftpDir)
                .toJobParameters();

    }

    /**
     * Schedule upload to S ftp.
     *
     * @throws JobParametersInvalidException       the job parameters invalid
     *                                             exception
     * @throws JobExecutionAlreadyRunningException the job execution already running
     *                                             exception
     * @throws JobRestartException                 the job restart exception
     * @throws JobInstanceAlreadyCompleteException the job instance already complete
     *                                             exception
     * @throws IOException                         Signals that an I/O exception has
     *                                             occurred.
     */
    @Scheduled(cron = "${cronjob.upload}")
    public void scheduleUploadToSFtp() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException, IOException {

        LOG.info("Processing SFTP Items");

        Path path = Functions.getPath(Functions.FOLDER.write.name());

        if (Files.isDirectory(path)) {
            Stream<Path> files = Files.list(path);
            for (Iterator<Path> iteratorFile = files.iterator(); iteratorFile.hasNext();) {
                Path file = iteratorFile.next();
                transferPendingFile(file);
            }

        }

    }

    /**
     * Transfer pending file.
     *
     * @param dirPath the dir path
     * @param file    the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void transferPendingFile(Path file) throws IOException {

        Path completedPath = Functions.getPath(Functions.FOLDER.done.name());
        
        Path tempDirPath = Functions.getPath(Functions.FOLDER.temp.name());
        try {
            if (Files.isRegularFile(file)) {

                String[] dirAndFile = file.getFileName().toString().split(Functions.UNDERSCORE);

                Path tempFilePath = Paths.get(tempDirPath.toAbsolutePath().toString(), dirAndFile[1]);

                
                Files.copy(file, tempFilePath, StandardCopyOption.REPLACE_EXISTING);

                String dirName = dirAndFile[0];
             
                String ftpLocation = sftpRemoteDirectory + "/" + dirName;

                
                
                gateway.upload(tempFilePath.toFile(), ftpLocation);

                String absolutePath = completedPath.toAbsolutePath().toString();
                Path destFolder = Paths.get(absolutePath, dirName.toString());

                Files.createDirectories(destFolder);
                Path pathFileName = tempFilePath.getFileName();

                String destPath = destFolder.toAbsolutePath().toString();
                if (pathFileName != null) {
                    Path dest = Paths.get(destPath, pathFileName.toString());

                    LOG.info("source:{}, dest:{}", file, dest);
                    Files.move(file, dest, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    throw new RuntimeException(
                            "Something is wrong here!!!, " + "pathFileName is null. " + "Please contact Developer!!");
                }

            }

        } catch (RuntimeException rx) {
            rx.printStackTrace();
            LOG.warn("Will Retry because SFTP is down.");
        }

    }

}

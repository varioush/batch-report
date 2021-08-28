/*
 * 
 */

package varioush.batch.listener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import varioush.batch.utils.Functions;

// TODO: Auto-generated Javadoc
/**
 * The Class InterceptingJobExecution.
 */
@Component
public final class InterceptingJobExecution implements JobExecutionListener {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(InterceptingJobExecution.class);

    /** The functions. */
    @Autowired
    private Functions functions;

//	@Value("${sftp.remote.directory:/}")
//	private String sftpRemoteDirectory;

    /**
     * Before job.
     *
     * @param jobExecution the job execution
     */
    @Override
    public void beforeJob(final JobExecution jobExecution) {

        LOG.info("Before initializing job ");

        JobParameters jobParameters = jobExecution.getJobParameters();

        String filename = jobParameters.getString(Functions.FILENAME);

        String subject = jobParameters.getString(Functions.SUBJECT);

        LOG.info("@BeforeJob : Subject:{}, File Name :{}", subject, filename);

        String content = functions.get(subject, Functions.HEADER);

        content = Functions.process(content);

        Functions.write(filename, content);

        LOG.info("Finishing Intercepting Job Excution - Before Job!");
    }

    /**
     * After job.
     *
     * @param jobExecution the job execution
     */
    @Override
    public void afterJob(final JobExecution jobExecution) {

        LOG.info("After Finishing job ");

        int noOfItemsProcessed = 0;
        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            noOfItemsProcessed += stepExecution.getWriteCount();
        }

        JobParameters jobParameters = jobExecution.getJobParameters();

        String filename = jobParameters.getString(Functions.FILENAME);

        String subject = jobParameters.getString(Functions.SUBJECT);

        LOG.info("@AfterJob : Subject:{}, File Name :{}", subject, filename);

        String content = Functions.NEW_LINE + functions.get(subject, Functions.FOOTER);

        String countInText = Integer.toString(noOfItemsProcessed);

        content = content.replace(Functions.EXP_COUNT, countInText);

        Functions.write(filename, content);

        String ftpPath = jobParameters.getString(Functions.FTP_PATH);

        if (ftpPath != null) {

            String parentPath = ftpPath.substring(0, ftpPath.indexOf("/"));

            try {
                Path path = Functions.getPath(Functions.FOLDER.write.name());

                String absoluteDirPath = path.toAbsolutePath().toString();

                Path dirPath = Paths.get(absoluteDirPath, parentPath);

                Files.createDirectories(dirPath);

                Path source = Paths.get(filename);
                Path dest = Paths.get(absoluteDirPath, ftpPath);
                LOG.info("source:{}, dest:{}", source, dest);
                Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);

            } catch (Exception ex) {
                LOG.error("Error while finishing job:", ex);
            }
            LOG.info("Finishing Intercepting Job Excution - After Job!");
        } else {
            LOG.error("Error while finishing job, ftpPath is blank:");

        }
    }

}

/*
 * 
 */

package varioush.batch.config;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import varioush.batch.listener.InterceptingJobExecution;
import varioush.batch.processor.CustomItemProcessor;
import varioush.batch.reader.CustomItemReader;
import varioush.batch.utils.Functions;
import varioush.batch.writer.CustomItemWriter;

// TODO: Auto-generated Javadoc
/**
 * The Class JobConfiguration.
 */
@Configuration
@EnableBatchProcessing
public class JobConfiguration {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(JobConfiguration.class);

    /** The job builder factory. */
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    /** The step builder factory. */
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /** The item reader. */
    @Autowired
    private CustomItemReader itemReader;

    /** The custom item writer. */
    @Autowired
    @StepScope
    private CustomItemWriter customItemWriter;

    /** The custom item processor. */
    @Autowired
    private CustomItemProcessor customItemProcessor;

    /** The functions. */
    @Autowired
    private Functions functions;

    /**
     * Custom item reader.
     *
     * @return the jdbc paging item reader
     */
    @StepScope
    @Bean(Functions.READER_JOB)
    public JdbcPagingItemReader<Map<String, Object>> customItemReader() {

        return itemReader.read();
    }

    /**
     * Export data job.
     *
     * @param studentStep     the student step
     * @param interceptingJob the intercepting job
     * @return the job
     */
    @Bean(name = Functions.JOB_BEAN_EXPORT)
    public Job exportDataJob(@Autowired @Qualifier(Functions.READER_STEP) Step studentStep,
            @Autowired InterceptingJobExecution interceptingJob) {
        LOG.info("Configuring JOB");
        return jobBuilderFactory.get(Functions.JOB_IDENTIFIER).incrementer(new RunIdIncrementer()).flow(studentStep)
                .end().listener(interceptingJob).build();
    }

    /**
     * Reader step.
     *
     * @param reader the reader
     * @return the step
     */
    @Bean(Functions.READER_STEP)
    public Step readerStep(
            @Autowired @Qualifier(Functions.READER_JOB) JdbcPagingItemReader<Map<String, Object>> reader) {
        LOG.info("Configuring Reader Step");
        int fetchSize = Integer.parseInt(functions.get(Functions.FETCH_SIZE));
        return stepBuilderFactory.get(Functions.READER_STEP).<Map<String, Object>, String>chunk(fetchSize)
                .reader(reader).writer(customItemWriter).processor(customItemProcessor).build();
    }

}

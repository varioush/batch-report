/*
 * 
 */

package varioush.batch.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import varioush.batch.utils.Functions;

// TODO: Auto-generated Javadoc
/**
 * The Class DataSourceConfiguration.
 */
@Configuration
public class DataSourceConfiguration {

    /** The log. */
    final Logger LOG = LoggerFactory.getLogger(DataSourceConfiguration.class);

    /** The db url. */
    @Value("${spring.record.datasource.url}")
    private String dbUrl;

    /** The db driver class. */
    @Value("${spring.record.datasource.driver-class-name}")
    private String dbDriverClass;

    /** The db user. */
    @Value("${spring.record.datasource.username}")
    private String dbUser;

    /** The db pwd. */
    @Value("${spring.record.datasource.password}")
    private String dbPwd;

    /** The batch url. */
    @Value("${spring.batch.datasource.url}")
    private String batchUrl;

    /** The batch driver class. */
    @Value("${spring.batch.datasource.driver-class-name}")
    private String batchDriverClass;

    /** The batch user. */
    @Value("${spring.batch.datasource.username}")
    private String batchUser;

    /** The batch pwd. */
    @Value("${spring.batch.datasource.password}")
    private String batchPwd;

    /**
     * Gets the data source.
     *
     * @return the data source
     */
    @Bean(name = Functions.REPORT, destroyMethod = Functions.CLOSE)
    public DataSource getDataSource() {

        LOG.info("Initializing {} database", Functions.REPORT_DB);
        HikariConfig config = new HikariConfig();
        config.setPoolName(Functions.REPORT_DB);
        config.setMaximumPoolSize(10);

        config.setDriverClassName(dbDriverClass);
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPwd);
        LOG.info("Finished initialization of{} database", Functions.REPORT_DB);
        return new HikariDataSource(config);
    }

    /**
     * Batch data source.
     *
     * @return the data source
     */
    @Primary
    @Bean(name = Functions.BATCH, destroyMethod = Functions.CLOSE)
    public DataSource batchDataSource() {

        LOG.info("Initializing {} database", Functions.BATCH_DB);
        HikariConfig config = new HikariConfig();
        config.setPoolName(Functions.BATCH_DB);

        config.setMaximumPoolSize(10);

        config.setDriverClassName(batchDriverClass);
        config.setJdbcUrl(batchUrl);
        config.setUsername(batchUser);
        config.setPassword(batchPwd);

        LOG.info("Finished initialization of {} database", Functions.BATCH_DB);
        return new HikariDataSource(config);
    }

}

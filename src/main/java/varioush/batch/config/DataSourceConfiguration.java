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

import varioush.batch.constant.Constants;

@Configuration
public class DataSourceConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceConfiguration.class);

	@Value("${spring.record.datasource.url}")
	private String dbUrl;
	@Value("${spring.record.datasource.driver-class-name}")
	private String dbDriverClass;
	@Value("${spring.record.datasource.username}")
	private String dbUser;
	@Value("${spring.record.datasource.password}")
	private String dbPwd;
	
	@Value("${spring.batch.datasource.url}")
	private String batchUrl;
	@Value("${spring.batch.datasource.driver-class-name}")
	private String batchDriverClass;
	@Value("${spring.batch.datasource.username}")
	private String batchUser;
	@Value("${spring.batch.datasource.password}")
	private String batchPwd;

	@Bean(name = Constants.DATASOURCE.REPORT, destroyMethod = Constants.LABEL.CLOSE)
	public DataSource getDataSource() {
		
		logger.info("Initializing {} database", Constants.NAME.REPORT_DB);
		HikariConfig config = new HikariConfig();
		config.setPoolName(Constants.NAME.REPORT_DB);
		config.setMaximumPoolSize(10);
		
		config.setDriverClassName(dbDriverClass);
		config.setJdbcUrl(dbUrl);
		config.setUsername(dbUser);
		config.setPassword(dbPwd);
		logger.info("Finished initialization of{} database", Constants.NAME.REPORT_DB);
		return new HikariDataSource(config);
	}
	
	
	@Primary
	@Bean(name = Constants.DATASOURCE.BATCH, destroyMethod = Constants.LABEL.CLOSE)
	public DataSource batchDataSource() {
		
		logger.info("Initializing {} database", Constants.NAME.BATCH_DB);
		HikariConfig config = new HikariConfig();
		config.setPoolName(Constants.NAME.BATCH_DB);
		
		config.setMaximumPoolSize(10);
		
		config.setDriverClassName(batchDriverClass);
		config.setJdbcUrl(batchUrl);
		config.setUsername(batchUser);
		config.setPassword(batchPwd);
		
		logger.info("Finished initialization of {} database", Constants.NAME.BATCH_DB);
		return new HikariDataSource(config);
	}
	
}
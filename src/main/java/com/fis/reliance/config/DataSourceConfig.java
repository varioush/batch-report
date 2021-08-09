package com.fis.reliance.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fis.reliance.constant.Constants;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

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

	@Bean(name = Constants.DATASOURCE_REPORT, destroyMethod = Constants.LABEL_CLOSE)
	public DataSource getDataSource() {
		
		logger.info("Initializing {} database", Constants.NAME_REPORT_DB);
		HikariConfig config = new HikariConfig();
		config.setPoolName(Constants.NAME_REPORT_DB);
		config.setMaximumPoolSize(10);
		
		config.setDriverClassName(dbDriverClass);
		config.setJdbcUrl(dbUrl);
		config.setUsername(dbUser);
		config.setPassword(dbPwd);
		logger.info("Finished initialization of{} database", Constants.NAME_REPORT_DB);
		return new HikariDataSource(config);
	}
	
	
	@Primary
	@Bean(name = Constants.DATASOURCE_BATCH, destroyMethod = Constants.LABEL_CLOSE)
	public DataSource batchDataSource() {
		
		logger.info("Initializing {} database", Constants.NAME_BATCH_DB);
		HikariConfig config = new HikariConfig();
		config.setPoolName(Constants.NAME_BATCH_DB);
		
		config.setMaximumPoolSize(10);
		
		config.setDriverClassName(batchDriverClass);
		config.setJdbcUrl(batchUrl);
		config.setUsername(batchUser);
		config.setPassword(batchPwd);
		
		logger.info("Finished initialization of {} database", Constants.NAME_BATCH_DB);
		return new HikariDataSource(config);
	}
	
}
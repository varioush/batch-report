package varioush.batch.reader;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.stereotype.Component;

import varioush.batch.constant.Constants;
import varioush.batch.utils.EnvironmentSource;

@Component
@StepScope
public class CustomItemReader {

	private static final Logger logger = LoggerFactory.getLogger(CustomItemReader.class);

	@Autowired
	@Qualifier(Constants.DATASOURCE.REPORT)
	private DataSource dataSource;

	@Value(Constants.JOB_DEF.JOB_PARAM_FILENAME)
	String filename;
	@Value(Constants.JOB_DEF.JOB_PARAM_SUBJECT)
	String subject;

	@Value(Constants.JOB_DEF.JOB_PARAM_COLUMNS)
	String columns;

	@Value(Constants.JOB_DEF.JOB_PARAM_WHERE_CLAUSE)
	String whereClause;

	@Value(Constants.JOB_DEF.JOB_PARAM_FROM_CLAUSE)
	String fromClause;

	@Value(Constants.JOB_DEF.JOB_PARAM_ORDER_BY)
	String orderBy;

	@Autowired
	EnvironmentSource source;

	public JdbcPagingItemReader<Map<String, Object>> read() {

		logger.info("Reading is in progress start!!!, Subject:{}, filename:{}", subject, filename);
		JdbcPagingItemReader<Map<String, Object>> pagingItemReader = new JdbcPagingItemReader<>();
		pagingItemReader.setDataSource(dataSource);

		pagingItemReader.setFetchSize(source.get(Constants.LABEL.FETCH_SIZE, Integer.class));
		pagingItemReader.setRowMapper(new ColumnMapRowMapper());

		try {
			pagingItemReader.setQueryProvider(queryProvider().getObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Reading is in progress end!!!, Subject:{}, filename:{}", subject, filename);
		return pagingItemReader;

	}

	public SqlPagingQueryProviderFactoryBean queryProvider() {

		final Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put(orderBy, Order.ASCENDING);

		SqlPagingQueryProviderFactoryBean pagingQueryProvider = new SqlPagingQueryProviderFactoryBean();
		pagingQueryProvider.setSelectClause(columns);// "actor_id, first_name, last_name"
		pagingQueryProvider.setFromClause(fromClause);
		pagingQueryProvider.setWhereClause(whereClause);
		pagingQueryProvider.setSortKeys(sortKeys);
		pagingQueryProvider.setDataSource(dataSource);
		return pagingQueryProvider;
	}

}

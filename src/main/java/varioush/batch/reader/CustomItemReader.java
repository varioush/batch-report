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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Reading is in progress end!!!, Subject:{}, filename:{}", subject, filename);
		return pagingItemReader;

		// return null;
	}

	public SqlPagingQueryProviderFactoryBean queryProvider() {

	
		
		String columns = source.get(subject, Constants.LABEL.COLUMNS);
		String table = source.get(subject, Constants.LABEL.TABLE);
		String sortColumn = source.get(subject, Constants.LABEL.SORT);
		String whereClause = source.get(subject, Constants.LABEL.WHERE);

		final Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put(sortColumn, Order.ASCENDING);

		SqlPagingQueryProviderFactoryBean pagingQueryProvider = new SqlPagingQueryProviderFactoryBean();
		pagingQueryProvider.setSelectClause(columns);// "actor_id, first_name, last_name"
		pagingQueryProvider.setFromClause(Constants.SQL.FROM + table);
		pagingQueryProvider.setWhereClause(Constants.SQL.WHERE + whereClause);
		pagingQueryProvider.setSortKeys(sortKeys);
		pagingQueryProvider.setDataSource(dataSource);
		return pagingQueryProvider;
	}

}

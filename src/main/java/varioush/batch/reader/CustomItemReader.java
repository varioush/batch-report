package varioush.batch.reader;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.stereotype.Component;

import varioush.batch.utils.Functions;

@Component
@StepScope
public final class CustomItemReader {

    final Logger LOG = LoggerFactory.getLogger(CustomItemReader.class);

    @Autowired
    @Qualifier(Functions.REPORT)
    private DataSource dataSource;

    @Value(Functions.JOB_PARAM_FILENAME)
    private String filename;
    @Value(Functions.JOB_PARAM_SUBJECT)
    private String subject;

    @Autowired
    private Functions functions;

    public JdbcPagingItemReader<Map<String, Object>> read() {

        LOG.info("Reading is in progress start!!!, Subject:{}, filename:{}", subject, filename);

        JdbcPagingItemReader<Map<String, Object>> pagingItemReader = new JdbcPagingItemReader<>();
        pagingItemReader.setDataSource(dataSource);

        int fetchSize = functions.get(Functions.FETCH_SIZE, Integer.class);

        pagingItemReader.setFetchSize(fetchSize);
        pagingItemReader.setRowMapper(new ColumnMapRowMapper());

        try {

            SqlPagingQueryProviderFactoryBean factory = queryProvider();

            if (factory != null) {
                PagingQueryProvider provider = factory.getObject();

                if (provider != null) {
                    pagingItemReader.setQueryProvider(provider);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info("Reading is in progress end!!!, Subject:{}, filename:{}", subject, filename);
        return pagingItemReader;

    }

    public SqlPagingQueryProviderFactoryBean queryProvider() {

        Functions.SQL sql = functions.getSubject(subject);

        final Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put(sql.getOrderBy(), Order.ASCENDING);

        SqlPagingQueryProviderFactoryBean sqlQuery = new SqlPagingQueryProviderFactoryBean();
        sqlQuery.setSelectClause(sql.getColumns());
        sqlQuery.setFromClause(sql.getFromClause());
        sqlQuery.setWhereClause(sql.getWhereClause());
        sqlQuery.setSortKeys(sortKeys);
        sqlQuery.setDataSource(dataSource);
        return sqlQuery;
    }

}

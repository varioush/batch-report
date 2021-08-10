package varioush.batch.constant;

import java.util.regex.Pattern;

public class Constants {

	public static final String BEAN_EXPORT_JOB = "exportJob";

	public static final String BEAN_JOB_IDENTIFIER = "jobIdentifier";

	public static final String BEAN_READER_JOB = "readerJob";

	public static final String BEAN_READER_STEP = "readerStep";

	public static final String CHAR_BLANK = "";

	public static final String CHAR_COMMA = ",";

	public static final String CHAR_DOT = ".";

	public static final String CHAR_DRUM = "|~|";

	public static final String CRON_DAILY = "${cronjob.daily}";

	public static final String CRON_MONTHLY = "${cronjob.monthly}";

	public static final String DATASOURCE_BATCH = "batchDataSource";

	public static final String DATASOURCE_REPORT = "reportDataSource";

	public static final String DELIMITER = "delimiter";

	public static final String DIR_TEMP = "TEMP_DIR";

	public static final String EXP_COUNT = "%COUNT%";

	public static final String INTERVAL_DAILY = "daily";

	public static final String INTERVAL_MONTHLY = "monthly";

	public static final String JOB_PARAM_FILENAME = "#{jobParameters[filename]}";

	public static final String JOB_PARAM_SUBJECT = "#{jobParameters[subject]}";

	public static final String LABEL_CLOSE = "close";

	public static final String LABEL_COLUMNS = "columns";

	public static final String LABEL_CRON = "cron";

	public static final String LABEL_DATE = "date";

	public static final String LABEL_FETCH_SIZE = "fetch.size";

	public static final String LABEL_FILENAME = "filename";

	public static final String LABEL_FOOTER = "footer";

	public static final String LABEL_HEADER = "header";

	public static final String LABEL_INTERVAL = "interval";

	public static final String LABEL_POSTFIX = "postfix";

	public static final String LABEL_PREFIX = "prefix";

	public static final String LABEL_SORT = "sort";

	public static final String LABEL_SUBJECT = "subject";

	public static final String LABEL_SUBJECT_LIST = "subject.list";

	public static final String LABEL_TABLE = "table";

	public static final String LABEL_TOPIC = "topic";

	public static final String LABEL_WHERE = "where";

	public static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;

	public static final String NAME_BATCH_DB = "BATCH-DATABASE";

	public static final String NAME_REPORT_DB = "REPORT-DATABASE";

	public static final String REG_DATE = "(%)([dMyhms]*)(%)";

	public static final String REG_DOT = "\\.";

	public static final String SQL_FROM = "FROM ";

	public static final String SQL_WHERE = " where ";

	public static final String TOPIC_BUSINESS = "business";

	public static final String TOPIC_PRINCIPAL = "principal";
	
	public static final String DAY_RETAIN= "temp.retain.day";

	public static final String UNUSED = "unused";
	
	public static final Pattern Z_PATTERN = Pattern.compile(REG_DATE);

}

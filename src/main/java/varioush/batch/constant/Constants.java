package varioush.batch.constant;

public class Constants {

	public enum FOLDER
	{
		INITIATED, PROCESSED, COMPLETED
	}
	
    public static final String NEW_LINE = System.lineSeparator();

	public static final String BEAN_JOB_IDENTIFIER = "jobIdentifier";

	public static final String BEAN_READER_JOB = "readerJob";

	public static final String BEAN_READER_STEP = "readerStep";

	public static final String CHAR_BLANK = "";

	public static final String CHAR_COMMA = ",";

	public static final String CHAR_DOT = ".";

	//public static final String CRON_CLEANUP = "0 47 0/7 ? * *";

	public static final String CRON_CLEANUP = "43 * * ? * * ";
	
	//public static final String CRON_PENDING = "0 37 0/3 ? * *";
	
	public static final String CRON_UPLOAD = "23 * * ? * * ";
	
	public static final String DATASOURCE_BATCH = "batchDataSource";

	public static final String DATASOURCE_REPORT = "reportDataSource";

	public static final String DELIMITER = "delimiter";

	public static final String DIR_TEMP = "TEMP_DIR";

	public static final String EXP_COUNT = "{N}";

	public static final String JOB_BEAN_EXPORT = "exportJob";

	public static final String JOB_PARAM_FILENAME = "#{jobParameters[filename]}";

	public static final String JOB_PARAM_SUBJECT = "#{jobParameters[subject]}";

	public static final String LABEL_CLOSE = "close";

	public static final String LABEL_COLUMNS = "columns";

	public static final String LABEL_CRON = "cron";

	public static final String LABEL_DATE = "date";

	public static final String LABEL_FETCH_SIZE = "fetch.size";

	public static final String LABEL_FILENAME = "filename";

	public static final String LABEL_FOOTER = "footer";

	public static final String LABEL_FTP_PATH = "ftpPath";

	public static final String LABEL_HEADER = "header";

	public static final String LABEL_POSTFIX = "postfix";

	public static final String LABEL_PREFIX = "prefix";

	public static final String LABEL_SORT = "sort";

	public static final String LABEL_SUBJECT = "subject";

	public static final String LABEL_SUBJECT_LIST = "subject.list";

	public static final String LABEL_TABLE = "table";

	public static final String LABEL_TOPIC = "topic";

	public static final String LABEL_WHERE = "where";

	public static final String NAME_BATCH_DB = "BATCH-DATABASE";

	public static final String NAME_REPORT_DB = "REPORT-DATABASE";

	public static final String SQL_FROM = "FROM ";
	
	public static final String SQL_WHERE = " where ";

	public static final String UNUSED = "unused";
	
	public static final String DAY_RETAIN= "temp.retain.day";
}




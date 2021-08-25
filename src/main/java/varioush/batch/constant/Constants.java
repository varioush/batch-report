package varioush.batch.constant;

public class Constants {

	public enum FOLDER {
		COMPLETED, INITIATED, PROCESSED
	}

	public static class JOB_DEF {
		public static final String JOB_IDENTIFIER = "jobIdentifier";

		public static final String READER_JOB = "readerJob";

		public static final String READER_STEP = "readerStep";

		public static final String JOB_BEAN_EXPORT = "exportJob";

		public static final String JOB_PARAM_FILENAME = "#{jobParameters[filename]}";

		public static final String JOB_PARAM_SUBJECT = "#{jobParameters[subject]}";
		
		public static final String JOB_PARAM_COLUMNS = "#{jobParameters[columns]}";
		
		public static final String JOB_PARAM_FROM_CLAUSE = "#{jobParameters[fromClause]}";
		
		public static final String JOB_PARAM_WHERE_CLAUSE = "#{jobParameters[whereClause]}";
		
		public static final String JOB_PARAM_ORDER_BY = "#{jobParameters[orderBy]}";


	}

	public static class CHAR {
		public static final String BLANK = "";

		public static final String COMMA = ",";

		public static final String DOT = ".";

		public static final String SPACE = " ";

	}

	public static class DATASOURCE {
		public static final String BATCH = "batchDataSource";

		public static final String REPORT = "reportDataSource";

	}

	public static class OTHER {

		public static final String DIR_TEMP = "TEMP_DIR";

		public static final String EXP_COUNT = "{N}";

		public static final String NEW_LINE = System.lineSeparator();

		public static final String CRON_UPLOAD = "23 * * ? * * ";

		public static final String UNUSED = "unused";
	}

	public static class LABEL {

		public static final String CLOSE = "close";

		public static final String COLUMNS = "columns";

		public static final String CRON = "cron";

		public static final String DATE = "date";

		public static final String FETCH_SIZE = "fetch.size";

		public static final String FILENAME = "filename";

		public static final String FOOTER = "footer";

		public static final String FTP_PATH = "ftpPath";

		public static final String HEADER = "header";

		public static final String POSTFIX = "postfix";

		public static final String PREFIX = "prefix";

		public static final String ORDER_BY = "orderBy";

		public static final String SUBJECT = "subject";

		public static final String SUBJECT_LIST = "subject.list";

		public static final String FROM_CLAUSE = "fromClause";

		public static final String TOPIC = "topic";

		public static final String WHERE_CLAUSE = "whereClause";

		public static final String DAY_RETAIN = "temp.retain.day";

		public static final String DELIMITER = "delimiter";

		public static final String QUERY = "query";

	}

	public static final class NAME {
		public static final String BATCH_DB = "BATCH-DATABASE";

		public static final String REPORT_DB = "REPORT-DATABASE";

	}

	public static class SQL {
		public static final String FROM = "FROM";

		public static final String WHERE = "WHERE";
		
		public static final String SELECT = "SELECT";

	}

}

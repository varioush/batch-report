/*
 * 
 */
package varioush.batch.utils;

// TODO: Auto-generated Javadoc
/**
 * The Interface Constants.
 */
interface Constants {

    /**
     * The Enum FOLDER.
     */
    enum FOLDER {

        /** The done. */
        done,
        /** The read. */
        read,
        /** The write. */
        write
    }

    /** The job identifier. */
    String JOB_IDENTIFIER = "jobIdentifier";

    /** The reader job. */
    String READER_JOB = "readerJob";

    /** The reader step. */
    String READER_STEP = "readerStep";

    /** The job bean export. */
    String JOB_BEAN_EXPORT = "exportJob";

    /** The job param filename. */
    String JOB_PARAM_FILENAME = "#{jobParameters[filename]}";

    /** The job param subject. */
    String JOB_PARAM_SUBJECT = "#{jobParameters[subject]}";

    /** The blank. */
    String BLANK = "";

    /** The comma. */
    String COMMA = ",";

    /** The dot. */
    String DOT = ".";

    /** The space. */
    String SPACE = " ";

    /** The batch. */
    String BATCH = "batchDataSource";

    /** The report. */
    String REPORT = "reportDataSource";

    /** The dir temp. */
    String DIR_TEMP = "work";

    /** The exp count. */
    String EXP_COUNT = "{N}";

    /** The new line. */
    String NEW_LINE = System.lineSeparator();

    /** The cron upload. */
    String CRON_UPLOAD = "cronjob.upload";

    /** The close. */
    String CLOSE = "close";

    /** The columns. */
    String COLUMNS = "columns";

    /** The cron. */
    String CRON = "cron";

    /** The date. */
    String DATE = "date";

    /** The fetch size. */
    String FETCH_SIZE = "fetch.size";

    /** The filename. */
    String FILENAME = "filename";

    /** The footer. */
    String FOOTER = "footer";

    /** The ftp path. */
    String FTP_PATH = "ftpPath";

    /** The header. */
    String HEADER = "header";

    /** The postfix. */
    String POSTFIX = "postfix";

    /** The prefix. */
    String PREFIX = "prefix";

    /** The order by. */
    String ORDER_BY = "orderBy";

    /** The subject. */
    String SUBJECT = "subject";

    /** The subject list. */
    String SUBJECT_LIST = "subject.list";

    /** The from clause. */
    String FROM_CLAUSE = "fromClause";

    /** The topic. */
    String TOPIC = "topic";

    /** The where clause. */
    String WHERE_CLAUSE = "whereClause";

    /** The day retain. */
    String DAY_RETAIN = "temp.retain.day";

    /** The delimiter. */
    String DELIMITER = "delimiter";

    /** The query. */
    String QUERY = "query";

    /** The batch db. */
    String BATCH_DB = "BATCH-DATABASE";

    /** The report db. */
    String REPORT_DB = "REPORT-DATABASE";

    /** The from. */
    String FROM = "FROM";

    /** The where. */
    String WHERE = "WHERE";

    /** The select. */
    String SELECT = "SELECT";

}

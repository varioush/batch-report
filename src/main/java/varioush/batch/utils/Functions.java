/*
 * 
 */
package varioush.batch.utils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import varioush.batch.formatter.DatePreviousFormatter;
import varioush.batch.formatter.IFormatter;

// TODO: Auto-generated Javadoc
/**
 * The Class Functions.
 */
@Component
public class Functions implements Constants {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(Functions.class);





  

    /** The source. */
    @Autowired
    Environment source;

    /** The subjects. */
    private Map<String, SQL> subjects = new HashMap<>();

    /**
     * Gets the and format.
     *
     * @param subject the subject
     * @param label   the label
     * @return the and format
     */
    public String getAndFormat(String subject, String label) {

        String value = get(subject, label);

        return process(value);
    }

    /**
     * Gets the.
     *
     * @param subject the subject
     * @param label   the label
     * @return the string
     */
    public String get(String subject, String label) {

        String value = get(subject + Functions.DOT + label);
        
        if(value==null )
        {
            value=get(COMMON+ Functions.DOT + label);
        }
        
        return value;
    }

    /**
     * Gets the.
     *
     * @param <T>   the generic type
     * @param key   the key
     * @param clazz the clazz
     * @return the t
     */
    public <T> T get(String key, Class<T> clazz) {

        return source.getProperty(key, clazz);
    }

    /**
     * Gets the.
     *
     * @param key the key
     * @return the string
     */
    public String get(String key) {

        return source.getProperty(key);
        
    }

    /**
     * Creates the file and directory.
     *
     * @param path the path
     */
    public static void createFileAndDirectory(Path path) {

        if (path != null && Files.notExists(path)) {
            try {
                Path parentPath = path.getParent();

                try {

                    if (parentPath != null) {
                        Files.createDirectories(parentPath);
                    } else {
                        throw new Exception("Parent Path is Blank");
                    }
                } catch (Exception ex) {
                    LOG.error("Error while creating Directories:", ex);
                }
                Files.createFile(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Delete files older than ndays.
     *
     * @param daysBack the days back
     */
    public static void deleteFilesOlderThanNdays(long daysBack) {

        for (Functions.FOLDER folder : Functions.FOLDER.values()) {
            Path path = getPath(folder.name());
            try {
                boolean delSource = false;
                long purgeTime = System.currentTimeMillis() - (daysBack * 24 * 60 * 60 * 1000);

                if (Files.getLastModifiedTime(path).toMillis() < purgeTime) {
                    delSource = true;
                }
                if (Files.isDirectory(path)) {
                    try (Stream<Path> files = Files.list(path)) {
                        for (Iterator<Path> iterator = files.iterator(); iterator.hasNext();) {
                            Path filePath = iterator.next();
                            if (Files.getLastModifiedTime(filePath).toMillis() < purgeTime) {
                                Files.delete(filePath);
                            }
                        }
                    }

                }
                if (delSource && isEmpty(path)) {
                    Files.delete(path);
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }

    /**
     * Gets the path.
     *
     * @param name the name
     * @return the path
     */
    public static Path getPath(String name) {

        Path currentDir = Paths.get(Functions.BLANK);
        Path one = currentDir.resolve(Functions.DIR_TEMP);
        return one.resolve(name);

    }

    /**
     * Checks if is empty.
     *
     * @param path the path
     * @return true, if is empty
     * @throws Exception the exception
     */
    public static boolean isEmpty(Path path) throws Exception {
        if (Files.isDirectory(path)) {
            try (Stream<Path> entries = Files.list(path)) {
                return !entries.findFirst().isPresent();
            }
        }

        return false;
    }

    /**
     * Built directory structure.
     */
    public static void builtDirectoryStructure() {
        for (FOLDER folder : FOLDER.values()) {
            Path path = getPath(folder.name());

            if (Files.exists(path) && Files.isDirectory(path)) {
                LOG.info("Folder exists :{}", path.toAbsolutePath().toString());
            } else {
                try {
                    Path temp = Files.createDirectories(path);
                    LOG.info("Path :{}, isExists:{}", temp.toAbsolutePath().toString(), Files.exists(temp));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * Write.
     *
     * @param filename the filename
     * @param content  the content
     */
    public static void write(String filename, String content) {
        Path path = Paths.get(filename);
        try {
            if (!Files.exists(path)) {
                Functions.createFileAndDirectory(path);
            }
            Files.write(path, content.getBytes(Charset.forName("UTF-8")), StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to write file, filename:" + path);
        }

    }

    /**
     * Inflate.
     *
     * @param text        the text
     * @param beforeSpace the before space
     * @return the string
     */
    static String inflate(String text, boolean beforeSpace) {
        text = beforeSpace ? Functions.SPACE + text : text;
        text = text + Functions.SPACE;
        return text;
    }

    /**
     * Process.
     *
     * @param query   the query
     * @param orderBy the order by
     * @return the functions. SQL
     */
    public static Functions.SQL process(String query, String orderBy) {

        if (query == null) {
            throw new RuntimeException("Query is blank!!!");
        }
        query = query.trim().toUpperCase().replaceAll(";$", "");

        if (orderBy != null) {
            orderBy = orderBy.trim().toUpperCase().replaceAll(";$", "");
        }

        if (query.contains("*")) {
            throw new RuntimeException("Please provide columns.!!!");
        }
        String temp = query;

        String textSearch = Functions.inflate(Functions.SELECT, false);
        int indexOfSelect = temp.indexOf(textSearch) + textSearch.length();
        String[] fragement = new String[] { temp.substring(0, indexOfSelect), temp.substring(indexOfSelect) };
        temp = fragement[1];
        int indexOfFrom = temp.indexOf(Functions.inflate(Functions.FROM, true));
        fragement = new String[] { temp.substring(0, indexOfFrom), temp.substring(indexOfFrom) };

        String columns = fragement[0].trim();
        temp = fragement[1];

        int indexOfWhere = temp.lastIndexOf(Functions.inflate(Functions.WHERE, true));

        fragement = new String[] { temp.substring(0, indexOfWhere), temp.substring(indexOfWhere) };
        String fromClause = fragement[0];
        String whereClause = fragement[1];

        String[] columnArray = columns.split("\\s*,\\s*");

        for (int i = 0; i < columnArray.length; i++) {
            String name = columnArray[i];
            if (name.contains(inflate("AS", true))) {
                name = name.substring(name.trim().lastIndexOf(Functions.SPACE)).trim();
            } else {
                name = name.substring(name.indexOf(Functions.DOT) + 1, name.length());
            }
            columnArray[i] = name;

        }

        if (orderBy == null) {
            LOG.warn(
                    "As there is no key defined!!! By default, first column will be considered as default sort column");
            orderBy = columnArray[0].trim();

        } 
//        else {
//
//            String sortColumn = orderBy;
//            int i = orderBy.lastIndexOf('.');
//            if (i > 0) {
//                sortColumn = orderBy.substring(i + 1);
//            }
//
//            boolean isSortKeyValid = Arrays.stream(columnArray).anyMatch(sortColumn::equals);
//
//            if (!isSortKeyValid) {
//                throw new RuntimeException("Please verify sort Key. The sort key must be part of columns.!!!");
//
//            }
//        }

        Functions.SQL sql = new Functions.SQL(query, columns, fromClause, whereClause, orderBy, columnArray);

        return sql;
    }

    /**
     * Process.
     *
     * @param expression the expression
     * @return the string
     */
    public static String process(String expression) {

        String r = "";
        boolean n = false;
        for (char c : expression.toCharArray()) {
            if (c == '{') {
                n = true;
            } else if (c == '}') {
                expression = format(expression, r);
                r = "";
                n = false;
            } else {
                if (n) {
                    r = r + c;
                }
            }

        }

        return expression;

    }

    /**
     * Format.
     *
     * @param expression the expression
     * @param r          the r
     * @return the string
     */
    private static String format(String expression, String r) {

        String[] expr = r.split("%");

        String text = null;
        IFormatter iformatter = null;
        if (expr[0].equals("P")) {
            iformatter = new DatePreviousFormatter(expr[1]);

        }
        if (iformatter != null) {
            text = iformatter.format();
            if (text != null) {
                expression = expression.replace("{" + r + "}", text);

            }
        }
        return expression;
    }

    /**
     * The Class SQL.
     */
    public static class SQL {

        /** The from clause. */
        private String fromClause;

        /** The columns. */
        private String columns;

        /** The where clause. */
        private String whereClause;

        /** The order by. */
        private String orderBy;

        /** The query. */
        private String query;

        /** The column array. */
        private String[] columnArray;

        /**
         * Instantiates a new sql.
         *
         * @param query       the query
         * @param columns     the columns
         * @param fromClause  the from clause
         * @param whereClause the where clause
         * @param orderBy     the order by
         * @param columnArray the column array
         */
        public SQL(String query, String columns, String fromClause, String whereClause, String orderBy,
                String[] columnArray) {
            this.query = query;
            this.columns = columns;
            this.fromClause = fromClause;
            this.whereClause = whereClause;
            this.orderBy = orderBy;
            this.columnArray = Arrays.copyOf(columnArray, columnArray.length);
        }

        /**
         * Gets the from clause.
         *
         * @return the from clause
         */
        public String getFromClause() {
            return fromClause;
        }

        /**
         * Gets the columns.
         *
         * @return the columns
         */
        public String getColumns() {
            return columns;
        }

        /**
         * Gets the where clause.
         *
         * @return the where clause
         */
        public String getWhereClause() {
            return whereClause;
        }

        /**
         * Gets the order by.
         *
         * @return the order by
         */
        public String getOrderBy() {
            return orderBy;
        }

        /**
         * Gets the query.
         *
         * @return the query
         */
        public String getQuery() {
            return query;
        }

        /**
         * Gets the column array.
         *
         * @return the column array
         */
        public String[] getColumnArray() {
            return Arrays.copyOf(columnArray, columnArray.length);
        }

        /**
         * To string.
         *
         * @return the string
         */
        @Override
        public String toString() {
            return "SQL [fromClause=" + fromClause + ", columns=" + columns + ", whereClause=" + whereClause
                    + ", orderBy=" + orderBy + ", query=" + query + ", columnArray=" + Arrays.toString(columnArray)
                    + "]";
        }

    }

    /**
     * Put subject.
     *
     * @param subject the subject
     * @param sql     the sql
     */
    public void putSubject(String subject, SQL sql) {
        this.subjects.put(subject, sql);

    }

    /**
     * Gets the subject.
     *
     * @param subject the subject
     * @return the subject
     */
    public SQL getSubject(String subject) {
        return this.subjects.get(subject);

    }

}

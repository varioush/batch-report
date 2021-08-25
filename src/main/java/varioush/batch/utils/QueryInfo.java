package varioush.batch.utils;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varioush.batch.constant.Constants;

public class QueryInfo {

	private static final Logger logger = LoggerFactory.getLogger(QueryInfo.class);

	private String query;

	private String fromClause;

	private String columns;

	private String whereClause;

	private String orderBy;

	public QueryInfo(String query, String orderBy) {
		this.query = clean(query, false);
		this.orderBy = clean(orderBy, true);
	}

	public String getQuery() {
		return query;
	}

	public String getOrderBy() {
		return orderBy;
	}

	private String clean(String text, boolean isNullAccept) {
		if (text == null) {
			if (!isNullAccept)
				throw new RuntimeException("Query is blank!!!");
			return text;
		}
		return text.trim().toUpperCase().replaceAll(";$", "");
	}

	public QueryInfo read() {

		if (this.query.contains("*")) {
			throw new RuntimeException("Please provide columns.!!!");
		}
		String temp = this.query;
		
		String textSearch  = inflate(Constants.SQL.SELECT, false);
		int indexOfSelect = temp.indexOf(textSearch)+textSearch.length();
		String[] fragement = new String[]{temp.substring(0, indexOfSelect), temp.substring(indexOfSelect)};
		temp = fragement[1];
		int indexOfFrom = temp.indexOf(inflate(Constants.SQL.FROM, true));
		fragement = new String[]{temp.substring(0, indexOfFrom), temp.substring(indexOfFrom)};
		
		this.columns = fragement[0].trim();
		temp = fragement[1];
		
		int indexOfWhere = temp.lastIndexOf(inflate(Constants.SQL.WHERE, true));

		fragement = new String[]{temp.substring(0, indexOfWhere), temp.substring(indexOfWhere)};
		this.fromClause = fragement[0];
		this.whereClause = fragement[1];

		String[] columnArray = this.columns.split("\\s*,\\s*");

		if (orderBy == null) {
			logger.warn(
					"As there is no key defined!!! By default, first column will be considered as default sort column");
			this.orderBy = columnArray[0].trim();

		} else {
			boolean isSortKeyValid = Arrays.stream(columnArray).anyMatch(orderBy::equals);

			if (!isSortKeyValid) {
				throw new RuntimeException("Please verify sort Key. The sort key must be part of columns.!!!");

			}
		}

		return this;
	}

	private static String inflate(String text, boolean beforeSpace) {
		text = beforeSpace ? Constants.CHAR.SPACE + text : text;
		text = text + Constants.CHAR.SPACE;
		return text;
	}

	private String[] split(String query, String text) {

		
		return query.split(text, 2);
	}

	@Override
	public String toString() {
		return "QueryInfo [query=" + query + ", fromClause=" + fromClause + ", columns=" + columns + ", whereClause="
				+ whereClause + ", orderBy=" + orderBy + "]";
	}

	public String getFromClause() {
		return fromClause;
	}

	public String getColumns() {
		return columns;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public static String[] split(String text) {
		String[] columnArray = text.split("\\s*,\\s*");

		for (int i = 0; i < columnArray.length; i++) {
			String name = columnArray[i];
			if (name.contains(inflate("AS", true))) {
				name = name.substring(name.trim().lastIndexOf(Constants.CHAR.SPACE)).trim();
			} else {
				name = name.substring(name.indexOf(Constants.CHAR.DOT) + 1, name.length());
			}
			columnArray[i] = name;

		}

		return columnArray;

	}

}
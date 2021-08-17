package varioush.batch.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExprEvaluator {

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
}

interface IFormatter {
	public String format();
}

class DatePreviousFormatter implements IFormatter {

	String expr;

	public DatePreviousFormatter(String expr) {
		this.expr = expr;
	}

	@Override
	public String format() {
		DateFormat formatter = new SimpleDateFormat(expr);

		return formatter.format(new Date(new Date().getTime() - MILLIS_IN_A_DAY));
	}

	public static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;

}

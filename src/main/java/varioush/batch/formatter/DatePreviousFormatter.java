package varioush.batch.formatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

public class DatePreviousFormatter implements IFormatter {

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
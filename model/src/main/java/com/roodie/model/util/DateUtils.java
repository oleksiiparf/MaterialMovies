package com.roodie.model.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Roodie on 11.03.2016.
 */
public class DateUtils {

    public static Date parse(String dateFormatted, String dateFormat) {
        return parse(dateFormatted, dateFormat, false);
    }

    public static Date parse(String dateFormatted, String dateFormat, boolean useUtc) {
        return parse(dateFormatted, new SimpleDateFormat(dateFormat), useUtc);
    }

    public static Date parse(String dateFormatted, SimpleDateFormat dateFormat) {
        return parse(dateFormatted, dateFormat, false);
    }

    public static Date parse(String dateFormatted, SimpleDateFormat dateFormat, boolean useUtc) {
        Date date = null;
        if (!dateFormatted.isEmpty()) {
            try {
                if (useUtc) {
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                }
                date = dateFormat.parse(dateFormatted);
            } catch (Exception e) {
                throw new RuntimeException("Error parsing the dateFormatted: " + dateFormatted + " pattern: "
                        + dateFormat.toPattern(), e);
            }
        }
        return date;
    }

    public static String format(Date paramDate, String paramString) {

        return format(paramDate, new SimpleDateFormat(paramString));
    }

    public static String format(Date paramDate, DateFormat paramDateFormat) {
        if (paramDate != null)
            return paramDateFormat.format(paramDate);
        return null;
    }

    public static String formatDate(Date paramDate) {
        return format(paramDate, DateFormat.getDateInstance());
    }

    public static String formatDateTime(Date paramDate) {
        return format(paramDate, DateFormat.getDateTimeInstance());
    }

}

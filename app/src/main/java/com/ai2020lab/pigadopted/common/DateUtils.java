package com.ai2020lab.pigadopted.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Rocky on 16/4/13.
 */
public class DateUtils {

    public static final String DATE_PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN_yMd = "yyyy-MM-dd";

    public static final SimpleDateFormat DATE_FORMAT_DEFAULT = new SimpleDateFormat(
            DATE_PATTERN_DEFAULT, Locale.CHINA);
    public static final SimpleDateFormat DATE_FORMAT_yMd = new SimpleDateFormat(
            DATE_PATTERN_yMd, Locale.CHINA);

    public static Calendar toDefaultCalendar(String dateStr) throws ParseException {
        Date gmtDate = DATE_FORMAT_DEFAULT.parse(dateStr);
        return toCalendar(gmtDate);
    }

    public static Calendar toCalendar(String dateStr, SimpleDateFormat format) throws ParseException {
        Date gmtDate = format.parse(dateStr);
        return toCalendar(gmtDate);
    }

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static boolean isSameMonth(Calendar leftCalendar, Calendar rightCalendar) {
        return (leftCalendar.get(Calendar.YEAR) == rightCalendar.get(Calendar.YEAR) )
                && (leftCalendar.get(Calendar.MONTH) == rightCalendar.get(Calendar.MONTH));
    }

    public static boolean isSameWeek(Calendar leftCalendar, Calendar rightCalendar) {

        int leftYear = leftCalendar.get(Calendar.YEAR);
        int rightYear = rightCalendar.get(Calendar.YEAR);
        int leftWeek = leftCalendar.get(Calendar.WEEK_OF_YEAR);
        int rightWeek = rightCalendar.get(Calendar.WEEK_OF_YEAR);

        if (leftYear == rightYear) {
            return leftWeek == rightWeek;
        }

        // over one year
        if (Math.abs(leftYear - rightYear) == 1) {
            Calendar beforeCalendar = leftCalendar;
            Calendar afterCalendar = rightCalendar;

            if (leftYear - rightYear > 0) {
                beforeCalendar = rightCalendar;
                afterCalendar = leftCalendar;
            }

            return isInSameWeek(beforeCalendar, afterCalendar);
        } else {
            return false;
        }
    }


    private static boolean isInSameWeek(Calendar beforeYearCalendar, Calendar afterYearCalendar) {
        int week = afterYearCalendar.get(Calendar.WEEK_OF_YEAR);

        // not first week
        if (week != 1) {
            return false;
        }

        int beforeWeek = beforeYearCalendar.get(Calendar.WEEK_OF_YEAR);

        return week == beforeWeek;
    }

}

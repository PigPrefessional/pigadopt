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
    private static final SimpleDateFormat DATE_FORMAT_DEFAULT = new SimpleDateFormat(
            DATE_PATTERN_DEFAULT, Locale.CHINA);

    public static Calendar toDefaultCalendar(String dateStr) throws ParseException {
        Date gmtDate = DATE_FORMAT_DEFAULT.parse(dateStr);
        return toCalendar(gmtDate);
    }

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
    public static boolean isSameWeek(Calendar leftCalendar, Calendar rightCalendar) {
        return (leftCalendar.get(Calendar.YEAR) == rightCalendar.get(Calendar.YEAR)
                && leftCalendar.get(Calendar.WEEK_OF_YEAR) == rightCalendar.get(Calendar.WEEK_OF_YEAR));
    }

}

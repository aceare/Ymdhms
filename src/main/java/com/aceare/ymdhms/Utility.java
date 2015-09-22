package com.aceare.ymdhms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shreekant on 9/21/2015.
 */
public class Utility {

    public static String formatDayName(long dateInMillis) {
        // day of the week (e.g "Wednesday".
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        return dayFormat.format(dateInMillis);
    }

    static String formatDate(long dateInMillis) {
        Date date = new Date(dateInMillis);
        return DateFormat.getDateInstance().format(date);
    }
}

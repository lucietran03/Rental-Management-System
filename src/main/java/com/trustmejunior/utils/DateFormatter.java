package com.trustmejunior.utils;

/**
 * @author TrustMeJunior
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return date == null ? "" : formatter.format(date);
    }
}
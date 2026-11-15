/*
 * Decompiled with CFR 0.152.
 */
package kawaii.util;

import java.util.Date;
import kawaii.util.DateFormatter;

public class DateUtils {
    public static String formatDate(Date date) {
        DateFormatter dateFormatter = new DateFormatter("dd/MM/yyyy HH:mm");
        return dateFormatter.format(date);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package kawaii.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter
extends SimpleDateFormat {
    public DateFormatter() {
    }

    public String formatDate(Date date) {
        return super.format(date);
    }

    public DateFormatter(String pattern) {
        super(pattern);
    }
}


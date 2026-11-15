/*
 * Decompiled with CFR 0.152.
 */
package kawaii.util;

public class NumberParser {
    public static Double parseDouble(String text) {
        try {
            Double.parseDouble(text);
            return Double.parseDouble(text);
        }
        catch (NumberFormatException numberFormatException) {
            return null;
        }
    }

    public static Integer parseInteger(String text) {
        try {
            Integer.parseInt(text);
            return Integer.parseInt(text);
        }
        catch (NumberFormatException numberFormatException) {
            return null;
        }
    }
}


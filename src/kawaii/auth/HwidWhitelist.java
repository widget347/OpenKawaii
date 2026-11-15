/*
 * Decompiled with CFR 0.152.
 */
package kawaii.auth;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.security.MessageDigest;
import kawaii.ui.UiElement;
import kawaii.util.ClipboardSelection;

public class HwidWhitelist
implements UiElement {
    public static String copyHwid() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String hwid = HwidWhitelist.sha1(System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("COMPUTERNAME") + System.getProperty("user.name"));
        ClipboardSelection clipboardSelection = new ClipboardSelection(hwid);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(clipboardSelection, null);
        return hwid;
    }

    public static String sha1(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        byte[] byArray = new byte[40];
        messageDigest.update(input.getBytes("iso-8859-1"), 0, input.length());
        byArray = messageDigest.digest();
        return HwidWhitelist.toHex(byArray);
    }

    public static String toHex(byte[] byArray) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < byArray.length; ++i) {
            int nibble = byArray[i] >>> 4 & 0xF;
            int iteration = 0;
            do {
                if (nibble >= 0 && nibble <= 9) {
                    stringBuffer.append((char)(48 + nibble & 0xFFFF));
                } else {
                    stringBuffer.append((char)(97 + (nibble - 10) & 0xFFFF));
                }
                nibble = byArray[i] & 0xF;
            } while (iteration++ < 1);
        }
        return stringBuffer.toString();
    }

    public static void checkWhitelist() {
    }
}


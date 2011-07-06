package de.jowisoftware.sshclient.util;

public final class StringUtils {
    private StringUtils() {}

    public static String escapeHTML(final String text) {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }

    public static String multiLineText2JTextString(final String text) {
        return "<html>" + escapeHTML(text).replace("\n", "<br />") + "</html>";
    }

    public static String escapeForLogs(final byte[] buffer, final int start, final int count) {
        final StringBuilder builder = new StringBuilder();
        for (int i = start; i < count; ++i) {
            builder.append(escapeCharForLog(buffer[i] & 0xFF));
        }

        return builder.toString();
    }

    public static String escapeCharForLog(final int value) {
        if (value == '\n') {
            return "\\n";
        } else if (value == '\r') {
            return "\\r";
        } else if (value == '\\') {
            return "\\\\";
        } else if (value < 32) {
            final String hexString = Integer.toHexString(value);
            return "\\u0000".substring(0, 6 - hexString.length()) + hexString;
        } else {
            return Character.toString((char) value);
        }
    }

    public static String byteToHex(final byte value) {
        if ((value & 0xFF) < 16) {
            return "0" + Integer.toHexString(value & 0xFF);
        } else {
            return Integer.toHexString(value & 0xFF);
        }
    }
}

package de.jowisoftware.ssh.client.util;

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
            builder.append(escapeCharForLog(buffer[i]));
        }

        return builder.toString();
    }

    public static String escapeCharForLog(final int value) {
        if (value < 32) {
            return String.format("\\%02d", value);
        } else if (value == '\\') {
            return "\\\\";
        } else {
            return Character.toString((char) value);
        }
    }
}

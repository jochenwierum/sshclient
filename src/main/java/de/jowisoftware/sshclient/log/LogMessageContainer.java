package de.jowisoftware.sshclient.log;

public class LogMessageContainer {
    private final String message;
    private final String[] throwableStrRep;

    public LogMessageContainer(final String message,
            final String[] throwableStrRep) {
        this.message = message;
        this.throwableStrRep = throwableStrRep;
    }

    public String toHTML() {
        final StringBuilder builder = new StringBuilder();

        builder.append("<html>");
        builder.append("<b>");
        builder.append(message.trim().replace("\n", "<br />"));
        builder.append("</b>");

        if (throwableStrRep != null && throwableStrRep.length > 0) {
            builder.append("<br />");
            for (final String line : throwableStrRep) {
                builder.append("&nbsp;&nbsp;")
                        .append(line
                                .replaceAll("\\t",
                                        "&nbsp;&nbsp;&nbsp;&nbsp;")
                                .replaceAll("\\s", "&nbsp;")
                        )
                        .append("<br />");
            }
        }

        builder.append("</html>");

        return builder.toString();
    }
}

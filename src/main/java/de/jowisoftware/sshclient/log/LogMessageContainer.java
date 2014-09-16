package de.jowisoftware.sshclient.log;

public class LogMessageContainer {
    private final String message;

    public LogMessageContainer(final String message) {
        this.message = message;
    }

    public String toHTML() {
        String htmlMessage = message.trim().replace("\n", "<br />");

        if (htmlMessage.contains("Exception")) {
            final int index = htmlMessage.lastIndexOf("<br />", htmlMessage.indexOf("Exception"));
            htmlMessage = htmlMessage.substring(0, index) + "</b>" + htmlMessage.substring(index);
        } else {
            htmlMessage += "</b>";
        }

        return "<html><b>" + htmlMessage + "</html>";
    }
}

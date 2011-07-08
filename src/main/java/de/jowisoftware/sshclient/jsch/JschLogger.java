package de.jowisoftware.sshclient.jsch;

import org.apache.log4j.Logger;

public class JschLogger implements com.jcraft.jsch.Logger {
    private static final Logger LOGGER = Logger.getLogger(JschLogger.class);

    @Override
    public void log(final int level, final String message) {
        switch (level) {
        case DEBUG:
            LOGGER.debug(message);
            break;
        case ERROR:
            LOGGER.error(message);
            break;
        case INFO:
            LOGGER.info(message);
            break;
        case WARN:
            LOGGER.warn(message);
            break;
        case FATAL:
            LOGGER.fatal(message);
            break;
        }
    }

    @Override
    public boolean isEnabled(final int level) {
        return true;
    }
}

package de.jowisoftware.sshclient.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JschLogger implements com.jcraft.jsch.Logger {
    private static final Logger LOGGER = LoggerFactory.getLogger(JschLogger.class);

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
            LOGGER.error(message);
            break;
        }
    }

    @Override
    public boolean isEnabled(final int level) {
        return true;
    }
}

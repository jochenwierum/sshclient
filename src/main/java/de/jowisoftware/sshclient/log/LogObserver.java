package de.jowisoftware.sshclient.log;

import java.util.Observable;

public class LogObserver extends Observable {
    private static final LogObserver INSTANCE = new LogObserver();

    public static LogObserver getInstance() {
        return INSTANCE;
    }

    public void triggerLog(final String message) {
        this.setChanged();
        this.notifyObservers(message);
    }

    public void addObserver(final LogPanel observer) {
        super.addObserver(observer);
    }

    public void deleteObserver(final LogPanel observer) {
        super.deleteObserver(observer);
    }
}
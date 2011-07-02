package de.jowisoftware.sshclient.log;

import java.awt.EventQueue;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;

public final class LogPanel extends JScrollPane implements Observer {
    private static final long serialVersionUID = -8432032817990142150L;
    private static final int MAX_LENGTH = 2048;
    private final DefaultListModel listModel = new DefaultListModel();

    public LogPanel() {
        super(new JList());
        ((JList) getViewport().getView()).setModel(listModel);
        LogObserver.getInstance().addObserver(this);
    }

    public void dispose() {
        LogObserver.getInstance().deleteObserver(this);
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final String message = (String) arg;
        final String formattedMessage = createFormattedMessage(message);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                listModel.addElement(formattedMessage);
                if (listModel.getSize() > MAX_LENGTH) {
                    listModel.remove(0);
                }

                // TODO: scroll to bottom
                validate();
            }
        });
    }

    private String createFormattedMessage(final String message) {
        return message.replace("\n", " ").trim();
    }
}
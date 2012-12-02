package de.jowisoftware.sshclient.log;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.EventQueue;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import de.jowisoftware.sshclient.ui.tabpanel.Tab;
import de.jowisoftware.sshclient.ui.tabpanel.TabPanel;
import de.jowisoftware.sshclient.ui.tabpanel.closable.ClosableTabListener;
import de.jowisoftware.sshclient.ui.tabpanel.closable.ClosableTabTitleComponent;

public final class LogTab implements Tab, Observer {
    private static class Content extends JScrollPane {
        private static final long serialVersionUID = -5153117403605046800L;

        private static final int MAX_LENGTH = 2048;
        private final DefaultListModel listModel = new DefaultListModel();

        public Content() {
            super(new JList());
            ((JList) getViewport().getView()).setModel(listModel);
        }

        public void addMessage(final String message) {
            listModel.addElement(message);
            if (listModel.getSize() > MAX_LENGTH) {
                listModel.remove(0);
            }

            validate();
            scrollToBottom();
        }

        private void scrollToBottom() {
            final JScrollBar vertical = getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        }
    }

    private final ClosableTabTitleComponent title;
    private final Content content = new Content();

    public LogTab(final TabPanel parent) {
        title = new ClosableTabTitleComponent(this, new JLabel(t("mainwindow.tabs.logs", "logs")));
        title.addListener(new ClosableTabListener() {
            @Override
            public void closeTab(final Tab tab) {
                parent.closeTab(LogTab.this);
            }
        });

        LogObserver.getInstance().addObserver(this);
    }

    public void dispose() {
        LogObserver.getInstance().deleteObserver(this);
    }

    @Override
    public JComponent getContent() {
        return content;
    }

    @Override
    public JComponent getTitleContent() {
        return title;
    }

    @Override public void freeze() { }
    @Override public void unfreeze() { }

    @Override
    public void update(final Observable o, final Object arg) {
        final LogMessageContainer message = (LogMessageContainer) arg;

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                content.addMessage(message.toHTML());
            }
        });
    }
}
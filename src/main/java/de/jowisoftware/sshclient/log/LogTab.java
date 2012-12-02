package de.jowisoftware.sshclient.log;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
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
            final JList list = (JList) getViewport().getView();
            list.setModel(listModel);
            final Action action = createCopyAction(list);
            list.getActionMap().put("copy", action);
        }

        private Action createCopyAction(final JList list) {
            return new AbstractAction() {
                private static final long serialVersionUID = 2681501502636412512L;

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final Clipboard clipboard = Toolkit.getDefaultToolkit()
                            .getSystemClipboard();
                    final StringBuffer content = new StringBuffer();

                    for (final Object entry : list.getSelectedValues())
                    {
                        String line = (String) entry;
                        line = line.replaceAll("&nbsp;", " ");
                        line = line.replaceAll("<br />", "\n");
                        line = line.replaceAll("\\s*<[^>]+>\\s*", "");
                        content.append(line).append("\n");
                    }

                    clipboard.setContents(
                            new StringSelection(content.toString()),
                            null);
                }
            };
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
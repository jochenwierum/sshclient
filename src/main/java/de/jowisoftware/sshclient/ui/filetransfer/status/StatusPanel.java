package de.jowisoftware.sshclient.ui.filetransfer.status;

import de.jowisoftware.sshclient.async.StatusListener;
import de.jowisoftware.sshclient.filetransfer.JSchSftpConnection;
import de.jowisoftware.sshclient.filetransfer.operations.ExtendedProgressMonitor;
import de.jowisoftware.sshclient.filetransfer.operations.OperationCommand;
import de.jowisoftware.sshclient.ui.CloseButton;
import de.jowisoftware.sshclient.util.SwingUtils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class StatusPanel implements ExtendedProgressMonitor, OperationQueue {
    private final JPanel mainPanel = new JPanel();
    private final JPanel operationPanel = new JPanel();
    private final List<StatusRow> rows = new ArrayList<>();
    private final Component glue = Box.createHorizontalGlue();
    private final StatusBar statusBar = new StatusBar();
    private final JSchSftpConnection connection;

    public StatusPanel(final JSchSftpConnection connection) {
        this.connection = connection;
        mainPanel.setLayout(new BorderLayout());

        operationPanel.setLayout(new GridBagLayout());
        final JScrollPane scrollPane = new JScrollPane(operationPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(createAbortButton(), BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statusBar.getComponent(), BorderLayout.SOUTH);
    }

    private JComponent createAbortButton() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        final CloseButton button = new CloseButton();
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                abortAll();
            }
        });
        panel.add(button);
        return panel;
    }

    public JComponent getComponent() { return mainPanel; }

    @Override
    public void addOperation(final OperationCommand command) {
        connection.queue(command);
        final StatusRow statusObject = new StatusRow(command, this);
        rows.add(statusObject);
        readdComponents();
    }

    @Override
    public void init(final long id, final int op, final String src, final String dest, final long max) {
        SwingUtils.runDelayedInSwingThread(new Runnable() {
            @Override
            public void run() {
                final int row = findRow(id);
                final StatusRow rowObject = rows.get(row);
                rowObject.start(max);
            }
        });
    }

    @Override
    public void count(final long id, final long progressInBytes) {
        SwingUtils.runDelayedInSwingThread(new Runnable() {
            @Override
            public void run() {
                final int row = findRow(id);
                rows.get(row).updateStatus(progressInBytes);
            }
        });
    }

    @Override
    public void end(final long id) {
        SwingUtils.runDelayedInSwingThread(new Runnable() {
            @Override
            public void run() {
                final int row = findRow(id);
                rows.remove(row);
                readdComponents();
            }
        });
    }

    private void readdComponents() {
        operationPanel.removeAll();
        int row = 0;
        for (final Component component : rows) {
            operationPanel.add(component, createRowConstraint(row++));
        }
        operationPanel.add(glue, createLastRowConstraint(row));
        mainPanel.validate();
        mainPanel.repaint();
    }

    public void dequeueAndAbort(final OperationCommand command) {
        final int row = findRow(command.id());
        mainPanel.remove(rows.remove(row));
        readdComponents();
        connection.dequeue(command);
    }

    public void abortAll() {
        for (final StatusRow op : rows) {
            connection.dequeue(op.getCommand());
        }
        rows.clear();
    }

    private int findRow(final long id) {
        for (int i = 0; i < rows.size(); ++i) {
            if (rows.get(i).getCommand().id() == id) {
                return i;
            }
        }
        return -1;
    }

    private GridBagConstraints createRowConstraint(final int y) {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = y;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weighty = 0;
        return constraints;
    }

    private GridBagConstraints createLastRowConstraint(final int y) {
        final GridBagConstraints constraints = createRowConstraint(y);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        return constraints;
    }

    public StatusListener getStatusListener() {
        return statusBar;
    }
}

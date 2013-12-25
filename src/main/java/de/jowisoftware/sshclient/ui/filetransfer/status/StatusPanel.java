package de.jowisoftware.sshclient.ui.filetransfer.status;

import de.jowisoftware.sshclient.filetransfer.operations.ExtendedProgressMonitor;
import de.jowisoftware.sshclient.filetransfer.operations.OperationCommand;
import de.jowisoftware.sshclient.ui.SftpConnectionPanel;
import de.jowisoftware.sshclient.util.SwingUtils;

import javax.swing.Box;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

public class StatusPanel extends JPanel implements ExtendedProgressMonitor {
    private final List<StatusRow> rows = new ArrayList<>();
    private final SftpConnectionPanel parent;
    private final Component glue = Box.createHorizontalGlue();

    public StatusPanel(final SftpConnectionPanel connectionPanel) {
        this.parent = connectionPanel;
        setLayout(new GridBagLayout());
    }

    public void addRow(final OperationCommand command) {
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
        removeAll();
        int row = 0;
        for (final Component component : rows) {
            add(component, createRowConstraint(row++));
        }
        add(glue, createLastRowConstraint(row));
        getParent().validate();
        getParent().repaint();
    }

    public void dequeueAndAbort(final OperationCommand command) {
        final int row = findRow(command.id());
        remove(rows.remove(row));
        readdComponents();
        parent.dequeueAndAbort(command);
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
}

package de.jowisoftware.sshclient.ui.filetransfer.status;

import de.jowisoftware.sshclient.filetransfer.operations.OperationCommand;
import de.jowisoftware.sshclient.ui.CloseButton;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StatusRow extends JPanel {
    private final OperationCommand command;
    private final JProgressBar progressBar;
    private final StatusPanel parent;

    private long maxSize;

    public StatusRow(final OperationCommand command, final StatusPanel parent) {
        this.parent = parent;
        this.command = command;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(createIcon());
        add(createLabel());

        add(Box.createHorizontalGlue());

        progressBar = createProgressbar();
        add(progressBar);
        add(createAbortButton());
    }

    private Component createIcon() {
        final JLabel jLabel = new JLabel("<>");
        jLabel.setMaximumSize(new Dimension(16, 16));
        return jLabel;
    }

    private Component createLabel() {
        return new JLabel(command.toString());
    }

    private JButton createAbortButton() {
        final JButton button = new CloseButton();
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                parent.dequeueAndAbort(command);
            }
        });
        return button;
    }

    private JProgressBar createProgressbar() {
        final JProgressBar bar = new JProgressBar(0, 1000);
        bar.setBorderPainted(false);
        bar.setIndeterminate(true);
        bar.setMaximumSize(new Dimension(250, bar.getMaximumSize().height));
        return bar;
    }

    public void updateStatus(final long progressInBytes) {
        final int progressInThousands = (int) (1f * progressInBytes / maxSize * 1000);
        progressBar.setValue(progressInThousands);
    }

    public void start(final long max) {
        this.maxSize = max;
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
    }

    public OperationCommand getCommand() {
        return command;
    }
}

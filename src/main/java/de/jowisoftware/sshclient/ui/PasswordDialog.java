package de.jowisoftware.sshclient.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class PasswordDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = -2426474184438059732L;

    private final String OK = "ok";
    private final String CANCEL = "cancel";

    private char[] result;

    private final JPasswordField passwordField;

    public PasswordDialog(final JFrame parent, final String message) {
        super(parent, "Password required");

        passwordField = new JPasswordField();
        setLayout(new GridLayout(3, 1));
        add(new JLabel(message));
        add(passwordField);

        final JPanel buttons = new JPanel();
        final JButton ok = new JButton("OK");
        final JButton cancel = new JButton("Cancel");
        buttons.setLayout(new GridLayout(1, 2));
        buttons.add(ok);
        buttons.add(cancel);
        add(buttons);

        passwordField.setActionCommand(OK);
        passwordField.addActionListener(this);
        ok.setActionCommand(OK);
        ok.addActionListener(this);
        cancel.setActionCommand(CANCEL);
        cancel.addActionListener(this);

        setResizable(false);
        setSize(260, 110);
        final Dimension parentSize = parent.getSize();
        final Point p = parent.getLocation();
        setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
        setModalityType(ModalityType.APPLICATION_MODAL);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals(OK)) {
            result = passwordField.getPassword();
        } else {
            result = null;
        }
        dispose();
    }

    public String askPassword() {
        setVisible(true);

        if (result == null) {
            return null;
        } else {
            return new String(result);
        }
    }
}

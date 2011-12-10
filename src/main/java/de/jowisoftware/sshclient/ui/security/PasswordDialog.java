package de.jowisoftware.sshclient.ui.security;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * A simple password input dialog. The Dialog is not thread safe. The caller
 *  has to make sure that all methods are invoked in the swing thread.
 */
public class PasswordDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = -2426474184438059732L;

    private static final String OK = "ok";
    private static final String CANCEL = "cancel";

    private char[] result;
    private boolean saveFlag;

    private final JPasswordField passwordField = createPasswordField();
    private final JCheckBox save = createSaveCheckBox();

    public PasswordDialog(final JFrame parent, final String message,
            final boolean passwordIsSaveable) {
        super(parent, t("popups.password_required", "Password required"));

        setLayout(new GridLayout(4, 1));
        add(new JLabel(message));
        add(passwordField);
        add(save);
        add(createButtonPanel());

        save.setEnabled(passwordIsSaveable);

        updateWindowParameters(parent);
    }

    private JCheckBox createSaveCheckBox() {
        final JCheckBox checkbox = new JCheckBox(t("popups.password_save", "Save password"));
        checkbox.setMnemonic(m("popups.password_save", 's'));
        return checkbox;
    }

    private void updateWindowParameters(final JFrame parent) {
        setResizable(false);
        setSize(260, 110);
        final Dimension parentSize = parent.getSize();
        final Point p = parent.getLocation();
        setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
        setModalityType(ModalityType.APPLICATION_MODAL);
    }

    private JPasswordField createPasswordField() {
        final JPasswordField field = new JPasswordField();

        field.setActionCommand(OK);
        field.addActionListener(this);

        return field;
    }

    private JPanel createButtonPanel() {
        final JButton ok = createOkButton();
        final JButton cancel = createCancelButton();

        final JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 2));
        buttons.add(ok);
        buttons.add(cancel);

        return buttons;
    }

    private JButton createCancelButton() {
        final JButton cancel = new JButton(t("cancel", "Cancel"));
        cancel.setMnemonic(m("cancel", 'c'));
        cancel.setActionCommand(CANCEL);
        cancel.addActionListener(this);
        return cancel;
    }

    private JButton createOkButton() {
        final JButton ok = new JButton(t("ok", "OK"));
        ok.setMnemonic(m("ok", 'o'));
        ok.setActionCommand(OK);
        ok.addActionListener(this);
        return ok;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals(OK)) {
            result = passwordField.getPassword();
            saveFlag = save.isSelected();
        } else {
            result = null;
        }
        dispose();
    }

    public void showDialog() {
        setVisible(true);
    }

    public boolean getSaveFlag() {
        return saveFlag;
    }

    public char[] getPassword() {
        return result;
    }
}

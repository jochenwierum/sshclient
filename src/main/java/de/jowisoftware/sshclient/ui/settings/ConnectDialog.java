package de.jowisoftware.sshclient.ui.settings;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.jowisoftware.sshclient.application.validation.ValidationResult;
import de.jowisoftware.sshclient.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.ui.settings.validation.AWTProfileValidator;

public class ConnectDialog extends JDialog implements WindowListener {
    private static final long serialVersionUID = 4811060219661889812L;
    private final ProfilePanel settingsFrame;
    private AWTProfile profile = new AWTProfile();

    public ConnectDialog(final Frame parent) {
        super(parent, t("connect.connect.title", "Direct connect"));
        settingsFrame = new ProfilePanel(profile, "", false);

        addWindowListener(this);
        setLayout(new BorderLayout());
        addControls();

        applyVisibility(parent);
    }

    private void addControls() {
        add(settingsFrame, BorderLayout.CENTER);
        add(createButtonBar(), BorderLayout.SOUTH);
    }

    private JPanel createButtonBar() {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        final JButton okButton = createOKButton();
        panel.add(okButton);
        getRootPane().setDefaultButton(okButton);

        panel.add(createCancelButton());

        return panel;
    }

    private JButton createOKButton() {
        final JButton button = new JButton(t("ok", "OK"));
        button.setMnemonic(m("ok", 'o'));
        button.addActionListener(createActionListener(true));
        return button;
    }

    private JButton createCancelButton() {
        final JButton button = new JButton(t("cancel", "Cancel"));
        button.setMnemonic(m("cancel", 'c'));
        button.addActionListener(createActionListener(false));
        return button;
    }

    private ActionListener createActionListener(final boolean success) {
        return new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                close(success);
            }
        };
    }

    private void applyVisibility(final Frame parent) {
        setModalityType(ModalityType.APPLICATION_MODAL);
        setSize(400, 300);
        setResizable(false);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public void close(final boolean successfully) {
        if (!successfully) {
            profile = null;
            setVisible(false);
            return;
        }

        settingsFrame.applyUnboundValues();
        final ValidationResult errors = new AWTProfileValidator(profile).validateProfile();
        if (errors.hadErrors()) {
            final String message = buildErrorMessage(errors.getErrors());
            JOptionPane.showMessageDialog(this, message);
        } else {
            setVisible(false);
            return;
        }
    }

    private String buildErrorMessage(final Map<String, String> errors) {
        final StringBuilder message = new StringBuilder();

        message.append(t("profiles.errors.message",
                "this profile contains one or more errors:"));
        for (final Entry<String, String> error : errors.entrySet()) {
            message.append("\n");
            message.append(error.getValue());
        }

        return message.toString();
    }

    public AWTProfile createProfile() {
        return profile;
    }

    @Override
    public void windowClosing(final WindowEvent e) {
        close(false);
    }

    @Override public void windowOpened(final WindowEvent e) { /* ignored */ }
    @Override public void windowClosed(final WindowEvent e) { /* ignored */ }
    @Override public void windowIconified(final WindowEvent e) { /* ignored */ }
    @Override public void windowDeiconified(final WindowEvent e) { /* ignored */ }
    @Override public void windowActivated(final WindowEvent e) { /* ignored */ }
    @Override public void windowDeactivated(final WindowEvent e) { /* ignored */ }
}

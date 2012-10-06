package de.jowisoftware.sshclient.ui.about;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import de.jowisoftware.sshclient.util.ApplicationUtils;
import de.jowisoftware.sshclient.util.ApplicationUtils.VersionInformation;
import de.jowisoftware.sshclient.util.SwingUtils;

public class UpdateLabel extends JLabel implements UpdateCheckResult {
    private static final long serialVersionUID = -7448353164521009788L;

    private final UpdateCheckThread updateThread;
    private boolean deactivated = false;

    public UpdateLabel() {
        setText("Checking for update...");

        updateThread = new UpdateCheckThread(this);
        updateThread.start();
    }

    public void stopThread() {
        deactivated = true;
    }

    @Override
    public void reportResult(final VersionInformation version) {
        if (deactivated) {
            return;
        }

        SwingUtils.runInSwingThread(new Runnable() {
            @Override
            public void run() {
                if (deactivated) {
                    return;
                }

                if (version == null) {
                    reportUpToDate();
                } else if(version.isUpdatable()) {
                    reportUpdate(version);
                } else {
                    reportCustomVersion(version);
                }
            }
        });
    }

    protected void reportCustomVersion(final VersionInformation version) {
        setText(t("update.impossible",
                "This package has no version number - update check impossible"));

        final MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 1
                        && e.getButton() == MouseEvent.BUTTON1) {
                    showCustomUpdateHelp(version);
                }
            }
        };

        makeLink(mouseAdapter);
    }

    private void reportUpdate(final VersionInformation version) {
        setText("<html><u>" + t("update.link.update", "Update available!") +
                "</u></html>");

        final MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 1
                        && e.getButton() == MouseEvent.BUTTON1) {
                    showUpdateHelp(version);
                }
            }
        };

        makeLink(mouseAdapter);
    }

    private void makeLink(final MouseAdapter mouseAdapter) {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setForeground(Color.BLUE);
        setFocusable(true);
        addMouseListener(mouseAdapter);
    }

    private void reportUpToDate() {
        setText(t("update.link.uptodate", "Your version is up to date."));
    }

    private void showUpdateHelp(final VersionInformation version) {
        showUpdateMessage(version, t("update.help.default",
                "<b>Update available!</b><br /><br />"
                + "A new version is available:"));
    }

    private void showCustomUpdateHelp(final VersionInformation version) {
        showUpdateMessage(version, t("update.help.impossible",
                "<b>Unable to read the version of this software</b><br /><br />"
            + "However, a released version is available:"));
    }

    private void showUpdateMessage(final VersionInformation version,
            final String updateTitle) {
        final String updateHelpText;
        if (ApplicationUtils.isUsingLuja()) {
            updateHelpText = t("update.help.luja",
                    "Since you're using luja, you can simply wait for<br />"
                    + "the next update check or run this sofware with the<br />"
                    + "<code>-L:update</code><br />"
                    + "parameter to install the update now.");
        } else {
            updateHelpText = t("update.help.manual",
                    "You can install the update by overriding<br />"
            		+ "ssh.jar with the new version of the software.");
        }

        final String labelText = "<html><center>" + updateTitle + " <br />"
                + "<i>" + t("update.build", "Build") + ": " + version.date + "<br />"
                + t("update.revision", "Revision") + ": " + version.revision + "</i><br /><br />"
                + updateHelpText + "</center></html>";

        JOptionPane.showMessageDialog(this, labelText);
    }
}

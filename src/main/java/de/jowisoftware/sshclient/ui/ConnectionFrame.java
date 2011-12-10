package de.jowisoftware.sshclient.ui;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSchException;

import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.jsch.SSHUserInfo;
import de.jowisoftware.sshclient.terminal.JSchConnection;
import de.jowisoftware.sshclient.terminal.events.DisplayType;
import de.jowisoftware.sshclient.terminal.events.VisualEvent;
import de.jowisoftware.sshclient.ui.terminal.AWTProfile;

public class ConnectionFrame extends JPanel {
    private static final long serialVersionUID = 7873084199411017370L;

    private static final Logger LOGGER = Logger.getLogger(ConnectionFrame.class);
    private final AWTProfile profile;
    private final SessionMenu sessionMenu;

    private JSchConnection connection;
    private SSHTabComponent recentTabComponent;
    private SSHConsole console = null;

    private final Application application;

    public ConnectionFrame(final Application application, final AWTProfile profile) {
        this.profile = profile;
        this.application = application;

        setLayout(new BorderLayout());
        setContent(new InfoPane(t("connecting", "Connecting...")));

        console = new SSHConsole(profile);
        sessionMenu = new SessionMenu(console);
        registerListener();
    }

    private void registerListener() {
        console.getSession().getVisualFeedback().register(new VisualEvent() {
            @Override
            public void newTitle(final String title) {
                recentTabComponent.updateLabel(title);
            }

            @Override
            public void setDisplayType(final DisplayType displayType) {
                sessionMenu.updateMenuStates();
            }

            @Override public void bell() { /* ignored */ }
            @Override public void newInverseMode(final boolean active) { /* ignored */ }
        });
    }

    public void connect() {
        try {
            final SSHUserInfo userInfo = new SSHUserInfo(application.mainWindow,
                    application.passwordManager);
            connection = new JSchConnection(application.jsch, profile, userInfo, console);
            connection.connect();
            console.setOutputStream(connection.getOutputStream());
            console.setChannel(connection.getChannel());
            setContent(console);
            sessionMenu.updateMenuStates();
        } catch(final Exception e) {
            close();
            console = null;
            if (authWasCanceled(e)) {
                // TODO: close yourself
                setContent(new ErrorPane("Image i'm closed"));
            } else {
                setContent(new ErrorPane(t("error.could_not_establish_connection",
                        "Could not establish connection"), e));
                LOGGER.error("Could not connect", e);
            }
            return;
        }
    }

    private boolean authWasCanceled(final Exception e) {
        final String cancel_message = "Auth cancel";
        if (!(e instanceof JSchException)) {
            return false;
        }
        return cancel_message.equals(e.getMessage());
    }

    private void setContent(final JComponent comp) {
        removeAll();
        add(comp, BorderLayout.CENTER);
    }

    public void close() {
        removeAll();
        if (console != null) {
            console.dispose();
            console = null;
        }

        if (connection != null) {
            connection.close();
        }
    }

    public void redraw() {
        if (console != null) {
            console.redrawConsole();
        }
    }

    public JComponent createTabComponent(final JTabbedPane pane) {
        recentTabComponent = new SSHTabComponent(this, profile, pane);
        return recentTabComponent.create();
    }

    public void takeFocusWithKey(final KeyEvent e) {
        if (console != null) {
            console.takeFocus();
            console.processKey(e);
        }
    }

    public void takeFocus() {
        if (console != null) {
            console.takeFocus();
        }
    }

    public SessionMenu getSessionMenu() {
        return sessionMenu;
    }
}
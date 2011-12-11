package de.jowisoftware.sshclient.ui;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSchException;

import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.jsch.InputStreamEvent;
import de.jowisoftware.sshclient.jsch.SSHUserInfo;
import de.jowisoftware.sshclient.terminal.JSchConnection;
import de.jowisoftware.sshclient.terminal.events.DisplayType;
import de.jowisoftware.sshclient.terminal.events.VisualEvent;
import de.jowisoftware.sshclient.ui.terminal.AWTProfile;
import de.jowisoftware.sshclient.ui.terminal.CloseTabMode;

public class ConnectionFrame extends JPanel {
    private static final long serialVersionUID = 7873084199411017370L;

    private static final Logger LOGGER = Logger.getLogger(ConnectionFrame.class);
    private final AWTProfile profile;
    private final SessionMenu sessionMenu;

    private JSchConnection connection;
    private final SSHTabComponent tabComponent;
    private SSHConsole console = null;

    private final Application application;
    private final JTabbedPane parent;

    public ConnectionFrame(final Application application, final AWTProfile profile,
            final JTabbedPane parent) {
        this.parent = parent;
        this.profile = profile;
        this.application = application;

        setLayout(new BorderLayout());
        setContent(new InfoPane(t("connecting", "Connecting...")));

        console = new SSHConsole(profile);
        sessionMenu = new SessionMenu(console);
        tabComponent = new SSHTabComponent(this, profile, parent);
        registerVisualFeedbackListener();
    }

    private void registerVisualFeedbackListener() {
        console.getSession().getVisualFeedback().register(new VisualEvent.VisualEventAdapter() {
            @Override
            public void newTitle(final String title) {
                tabComponent.updateLabel(title);
            }

            @Override
            public void setDisplayType(final DisplayType displayType) {
                sessionMenu.updateMenuStates();
            }
        });
    }

    public void connect() {
        try {
            tryConnect();
        } catch(final Exception e) {
            close();
            console = null;
            if (authWasCanceled(e)) {
                closeTab();
            } else {
                setContent(new ErrorPane(t("error.could_not_establish_connection",
                        "Could not establish connection"), e));
                LOGGER.error("Could not connect", e);
            }
            return;
        }
    }

    private void tryConnect() throws JSchException, IOException {
        final SSHUserInfo userInfo = new SSHUserInfo(application.mainWindow,
                application.passwordManager);
        setupJSchConnection(userInfo);
        setupConsole();
        sessionMenu.updateMenuStates();
    }

    private void setupConsole() {
        console.setOutputStream(connection.getOutputStream());
        console.setChannel(connection.getChannel());
        setContent(console);
    }

    private void setupJSchConnection(final SSHUserInfo userInfo)
            throws JSchException, IOException {
        connection = new JSchConnection(application.jsch, profile, userInfo);
        connection.events().register(console);
        registerInputStreamEventListener();
        connection.connect();
    }

    private void registerInputStreamEventListener() {
        connection.events().register(new InputStreamEvent.InputStreamEventAdapter() {
            @Override
            public void streamClosed(final int exitCode) {
                final boolean successfullyClosed = exitCode == 0;
                final CloseTabMode closeMode = profile.getCloseTabMode();
                final boolean closeTab = closeMode == CloseTabMode.ALWAYS ||
                        (successfullyClosed && closeMode != CloseTabMode.NEVER);

                if (closeTab) {
                    closeTab();
                } else {
                    tabComponent.updateLabel(t("closedtab", "[closed] %s",
                            tabComponent.getLabel()));
                }
            }
        });
    }

    private void closeTab() {
        parent.removeTabAt(parent.indexOfComponent(this));
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

    public JComponent createTabComponent() {
        return tabComponent.create();
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
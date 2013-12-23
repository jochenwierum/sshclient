package de.jowisoftware.sshclient.ui;

import com.jcraft.jsch.JSchException;
import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.jsch.InputStreamEvent;
import de.jowisoftware.sshclient.jsch.SSHUserInfo;
import de.jowisoftware.sshclient.terminal.JSchConnection;
import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.events.DisplayType;
import de.jowisoftware.sshclient.terminal.events.VisualEvent;
import de.jowisoftware.sshclient.ui.tabpanel.redrawing.RedrawingTabPanel;
import de.jowisoftware.sshclient.ui.terminal.CloseTabMode;
import de.jowisoftware.sshclient.util.Constants;
import de.jowisoftware.sshclient.util.SwingUtils;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;

import static de.jowisoftware.sshclient.i18n.Translation.t;

public class ConnectionPanel extends AbstractSSHConnectionPanel<SSHTab> {
    private static final long serialVersionUID = 7873084199411017370L;

    private final AWTProfile profile;
    private final SessionMenu sessionMenu;

    private JSchConnection connection;
    private SSHConsole console = null;

    public ConnectionPanel(final Application application, final AWTProfile profile,
            final RedrawingTabPanel parent, final SSHTab tab) {
        super(application, parent, tab);
        this.profile = profile;

        console = new SSHConsole(profile);
        sessionMenu = new SessionMenu(console);
        registerVisualFeedbackListener();
    }

    private void registerVisualFeedbackListener() {
        final SSHSession session = console.getSession();
        session.getVisualFeedback().register(new VisualEvent.VisualEventAdapter() {
            @Override
            public void newTitle(final String title) {
                tab.getTitleContent().updateLabel(title);
            }

            @Override
            public void setDisplayType(final DisplayType displayType) {
                sessionMenu.updateMenuStates();
            }

            @Override
            public void bell() {
                switch(application.settings.getBellType()) {
                    case SOUND:
                        Toolkit.getDefaultToolkit().beep();
                        break;
                    case VISUAL:
                        session.getRenderer().invertFor(Constants.FLASH_TIMER);
                        parent.restartTimer();
                        break;
                    case NONE:
                }
            }
        });
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

                        markTabAsClosed(closeTab);
                connection.close();
            }
        });
    }

    private void markTabAsClosed(final boolean closeTab) {
        SwingUtils.runInSwingThread(new Runnable() {
            @Override
            public void run() {
                if (closeTab) {
                    parent.closeTab(tab);
                    close();
                } else {
                    tab.getTitleContent().updateLabel(
                            t("closedtab", "[closed] %s",
                                    tab.getTitleContent().getLabel()));
                }
            }
        });
    }


    @Override
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

    public void takeFocusWithKey(final KeyEvent e) {
        if (console != null) {
            console.takeFocus();
            console.keyPressed(e);
        }
    }

    @Override
    public void takeFocus() {
        if (console != null) {
            console.takeFocus();
        }
    }

    public SessionMenu getSessionMenu() {
        return sessionMenu;
    }

    @Override
    public void freeze() {
        console.freeze();
    }

    @Override
    protected void processConnectException(final Exception e) {
        console = null;
    }

    @Override
    public void unfreeze() {
        console.unfreeze();
    }

    @Override
    protected void tryConnect(final SSHUserInfo userInfo) throws JSchException, IOException {
        setupJSchConnection(userInfo);
        setupConsole();
        sessionMenu.updateMenuStates();
    }

}

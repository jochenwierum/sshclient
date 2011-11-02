package de.jowisoftware.sshclient.ui;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;

import de.jowisoftware.sshclient.jsch.SSHUserInfo;
import de.jowisoftware.sshclient.settings.AWTProfile;
import de.jowisoftware.sshclient.terminal.JSchConnection;
import de.jowisoftware.sshclient.terminal.events.DisplayType;
import de.jowisoftware.sshclient.terminal.events.VisualEvent;

public class ConnectionFrame extends JPanel {
    private static final long serialVersionUID = 7873084199411017370L;

    private static final Logger LOGGER = Logger.getLogger(ConnectionFrame.class);
    private final AWTProfile profile;
    private final JFrame parent;
    private final JSch jsch;
    private final SessionMenu sessionMenu;

    private JSchConnection connnection;
    private SSHTabComponent recentTabComponent;
    private SSHConsole console = null;

    public ConnectionFrame(final JFrame parent, final AWTProfile profile, final JSch jsch) {
        this.profile = profile;
        this.parent = parent;
        this.jsch = jsch;

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
            final SSHUserInfo userInfo = new SSHUserInfo(parent);
            connnection = new JSchConnection(jsch, profile, userInfo, console);
            connnection.connect();
            console.setOutputStream(connnection.getOutputStream());
            console.setChannel(connnection.getChannel());
            setContent(console);
            sessionMenu.updateMenuStates();
        } catch(final Exception e) {
            close();
            console = null;
            setContent(new ErrorPane(t("errors.could_not_establish_connection",
                    "Could not establish connection"), e));
            LOGGER.error("Could not connect", e);
            return;
        }
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

        if (connnection != null) {
            connnection.close();
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
        console.takeFocus();
    }

    public SessionMenu getSessionMenu() {
        return sessionMenu;
    }
}
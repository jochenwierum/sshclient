package de.jowisoftware.sshclient.ui;

import com.jcraft.jsch.JSchException;
import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.jsch.SSHUserInfo;
import de.jowisoftware.sshclient.ui.tabpanel.redrawing.RedrawingTabPanel;
import org.apache.log4j.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.io.IOException;

import static de.jowisoftware.sshclient.i18n.Translation.t;

public abstract class AbstractSSHConnectionPanel<T extends AbstractSSHTab<?>> extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(AbstractSSHConnectionPanel.class);

    protected final RedrawingTabPanel parent;
    protected final Application application;
    protected final T tab;

    protected AbstractSSHConnectionPanel(final Application application, final RedrawingTabPanel parent, final T tab) {
        this.application = application;
        this.parent = parent;
        this.tab = tab;

        setLayout(new BorderLayout());
        setContent(new InfoPane(t("connecting", "Connecting...")));
    }

    abstract public void close();

    public abstract void unfreeze();

    public abstract void freeze();

    public void connect() {
        try {
            final SSHUserInfo userInfo = new SSHUserInfo(application.mainWindow,
                    application.passwordManager);
            tryConnect(userInfo);
        } catch(final Exception e) {
            close();
            processConnectException(e);
            final boolean authWasCanceled = authWasCanceled(e);
            if (!authWasCanceled) {
                parent.closeTab(tab);
                setContent(new ErrorPane(t("error.could_not_establish_connection",
                        "Could not establish connection"), e));
                LOGGER.error("Could not connect", e);
            }
        }
    }

    protected abstract void processConnectException(final Exception e);

    protected boolean authWasCanceled(final Exception e) {
        final String cancel_message = "Auth cancel";
        //noinspection SimplifiableIfStatement
        if (!(e instanceof JSchException)) {
            return false;
        }
        return cancel_message.equals(e.getMessage());
    }

    protected abstract void tryConnect(SSHUserInfo userInfo) throws JSchException, IOException;

    public abstract void takeFocus();

    protected void setContent(final JComponent comp) {
        removeAll();
        add(comp, BorderLayout.CENTER);
    }
}

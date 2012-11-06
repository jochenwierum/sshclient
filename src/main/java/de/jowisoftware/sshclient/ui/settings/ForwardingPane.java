package de.jowisoftware.sshclient.ui.settings;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.util.StringUtils;

class ForwardingPane extends AbstractOptionPanel {
    private static final long serialVersionUID = 8242920505051622953L;

    private final AWTProfile profile;

    private final JCheckBox agentForwarding = createAgentForwardingCheckBox();
    private final JCheckBox x11Forwarding = createXForwardingCheckBox();
    private final JTextField x11Host = new JTextField();
    private final JTextField x11Display = new JTextField();

    public ForwardingPane(final AWTProfile profile) {
        this.profile = profile;
        setLayout(new GridLayout(4, 2, 5, 0));

        agentForwarding.setSelected(profile.getAgentForwarding());
        add(label("profiles.forwardings.agent", "Agent forwarding"));
        add(agentForwarding);

        x11Forwarding.setSelected(profile.getX11Forwarding());
        add(label("profiles.forwardings.x11", "X11 forwarding", 'o', x11Forwarding));
        add(x11Forwarding);

        x11Host.setText(profile.getX11Host());
        add(label("profiles.forwarding.x11host", "X11 host", 'h', x11Host));
        add(x11Host);

        x11Display.setText(Integer.toString(profile.getX11Display()));
        add(label("profiles.forwarding.x11display", "X11 display", 'd', x11Display));
        add(x11Display);
    }


    private JCheckBox createAgentForwardingCheckBox() {
        final JCheckBox checkBox = new JCheckBox(
                t("profiles.forwardings.agentfowarding",
                "Forward ssh agent"));
        checkBox.setMnemonic(m("profiles.forwardings.agentfowarding", 'f'));
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                profile.setAgentForwarding(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        return checkBox;
    }

    private JCheckBox createXForwardingCheckBox() {
        final JCheckBox checkBox = new JCheckBox(t("profiles.forwardings.xfowarding",
                "Forward X-Server"));
        checkBox.setMnemonic(m("profiles.forwardings.xfowarding", 'x'));
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                profile.setX11Forwarding(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        return checkBox;
    }

    @Override
    public void save() {
        profile.setX11Host(x11Host.getText());
        profile.setX11Display(StringUtils.getInteger(x11Display.getText(), 0));
    }


    @Override
    public String getTitle() {
        return t("profiles.forwardings.title", "forwardings");
    }
}

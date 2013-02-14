package de.jowisoftware.sshclient.ui.settings.profile;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import de.jowisoftware.sshclient.application.settings.Forwarding;
import de.jowisoftware.sshclient.application.settings.Forwarding.Direction;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.ui.settings.AbstractGridBagOptionPanel;
import de.jowisoftware.sshclient.util.StringUtils;
import de.jowisoftware.sshclient.util.SwingUtils;

class ForwardingPane extends AbstractGridBagOptionPanel {
    private static final long serialVersionUID = 8242920505051622953L;

    private final AWTProfile profile;

    private final JCheckBox agentForwarding = createAgentForwardingCheckBox();
    private final JCheckBox x11Forwarding = createXForwardingCheckBox();
    private final JTextField x11Host = new JTextField();
    private final JTextField x11Display = new JTextField();
    private final JTextField socksPort = new JTextField();
    private final JList<Forwarding> forwardingsList = new JList<>(
            new DefaultListModel<Forwarding>());

    public ForwardingPane(final AWTProfile profile, final Window parent) {
        super(parent);
        this.profile = profile;

        agentForwarding.setSelected(profile.getAgentForwarding());
        add(label("profiles.forwardings.agent", "Agent forwarding"),
                makeLabelConstraints(1));
        add(agentForwarding, makeConstraints(2, 1));

        x11Forwarding.setSelected(profile.getX11Forwarding());
        add(label("profiles.forwardings.x11", "X11 forwarding", 'o', x11Forwarding),
                makeLabelConstraints(2));
        add(x11Forwarding, makeConstraints(2, 2));

        x11Host.setText(profile.getX11Host());
        add(label("profiles.forwarding.x11host", "X11 host", 'h', x11Host),
                makeLabelConstraints(3));
        add(x11Host, makeConstraints(2, 3));

        x11Display.setText(Integer.toString(profile.getX11Display()));
        add(label("profiles.forwarding.x11display", "X11 display", 'i', x11Display),
                makeLabelConstraints(4));
        add(x11Display, makeConstraints(2, 4));

        add(label("profiles.forwardings.portForwardings", "Port forwardings:",
                'p', forwardingsList), makeLabelConstraints(5));
        final GridBagConstraints constraints = makeConstraints(2, 5);
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        add(createPortForwardingsPanel(), constraints);

        if (profile.getSocksPort() != null) {
            socksPort.setText(profile.getSocksPort().toString());
        }
        add(label("profiles.forwardings.socksport", "SOCKS 4/5 Port:", 'o',
                socksPort), makeLabelConstraints(6));
        add(socksPort, makeConstraints(2, 6));
    }

    private JPanel createPortForwardingsPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        final JPanel controls = createForwardingControlsPanel();

        updateForwardingsList();
        forwardingsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(forwardingsList), BorderLayout.CENTER);
        panel.add(controls, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createForwardingControlsPanel() {
        final JPanel controls = new JPanel(new GridLayout(6, 2));
        final ButtonGroup buttons = new ButtonGroup();

        final JRadioButton localForwarding = createForwardingsLocalRadioButton(buttons);
        controls.add(localForwarding);

        final JRadioButton remoteForwarding = createForwardingsRemoteRadioButton(buttons);
        controls.add(remoteForwarding);

        final JTextField sourceHostText = new JTextField("localhost");
        controls.add(label("profiles.forwarding.sourceHost", "Source host:", 'o', sourceHostText));
        controls.add(sourceHostText);

        final JTextField remoteHostText = new JTextField("localhost");
        controls.add(label("profiles.forwarding.remoteHost", "Remote host:", 'm', remoteHostText));
        controls.add(remoteHostText);

        final JTextField sourcePortText = new JTextField();
        controls.add(label("profiles.forwarding.sourcePort", "Source port:", 'u', sourcePortText));
        controls.add(sourcePortText);

        final JTextField remotePortText = new JTextField();
        controls.add(label("profiles.forwarding.remotePort", "Remote port:", 't', remotePortText));
        controls.add(remotePortText);

        controls.add(createAddForwardingButton(localForwarding,
                sourcePortText, remotePortText, sourceHostText, remoteHostText));
        controls.add(createDeleteForwardingButton());

        return controls;
    }

    private JButton createDeleteForwardingButton() {
        final JButton deleteButton = new JButton(t("delete", "Delete"));
        deleteButton.setMnemonic(m("delete", 'd'));

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Forwarding forwarding = forwardingsList.getSelectedValue();
                if (forwarding != null) {
                    profile.getPortForwardings().remove(forwarding);
                    updateForwardingsList();
                }
            }
        });

        return deleteButton;
    }

    private JButton createAddForwardingButton(final JRadioButton localForwarding,
            final JTextField sourcePortText, final JTextField remotePortText,
            final JTextField sourceHostText, final JTextField remoteHostText) {
        final JButton addButton = new JButton(t("add", "Add"));
        addButton.setMnemonic(m("add", 'a'));

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String sourceHost = sourceHostText.getText();
                final String remoteHost = remoteHostText.getText();
                final int sourcePort;
                final int remotePort;

                try {
                    sourcePort = Integer.parseInt(sourcePortText.getText());
                    remotePort = Integer.parseInt(remotePortText.getText());
                } catch(final NumberFormatException ex) {
                    SwingUtils.showMessage(parentWindow, t("profiles.forwardings.formatError",
                            "The ports must be numeric"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                final Direction direction = (localForwarding.isSelected() ? Direction.Local : Direction.Remote);
                final Forwarding forwarding = new Forwarding(direction,
                        sourceHost, sourcePort, remoteHost, remotePort);

                profile.getPortForwardings().add(forwarding);
                updateForwardingsList();
            }
        });
        return addButton;
    }

    private JRadioButton createForwardingsRemoteRadioButton(
            final ButtonGroup buttons) {
        final JRadioButton remoteForwarding = new JRadioButton(t("profiles.forwarding.remote", "Remote"));
        remoteForwarding.setMnemonic(m("profiles.forwarding.remote", 'e'));
        buttons.add(remoteForwarding);
        return remoteForwarding;
    }

    private JRadioButton createForwardingsLocalRadioButton(
            final ButtonGroup buttons) {
        final JRadioButton localForwarding = new JRadioButton(t("profiles.forwarding.local", "Local"));
        localForwarding.setMnemonic(m("profiles.forwarding.local", 'l'));
        localForwarding.setSelected(true);
        buttons.add(localForwarding);
        return localForwarding;
    }


    private void updateForwardingsList() {
        final DefaultListModel<Forwarding> model = (DefaultListModel<Forwarding>)
                forwardingsList.getModel();

        model.clear();
        for (final Forwarding forwarding : profile.getPortForwardings()) {
            model.addElement(forwarding);
        }

        validate();
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

        if (socksPort.getText().isEmpty()) {
            profile.setSocksPort(null);
        } else {
            try {
                profile.setSocksPort(Integer.parseInt(socksPort.getText()));
            } catch(final NumberFormatException e) {
                profile.setSocksPort(null);
            }
        }
    }


    @Override
    public String getTitle() {
        return t("profiles.forwardings.title", "forwardings");
    }
}

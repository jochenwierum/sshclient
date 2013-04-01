package de.jowisoftware.sshclient.ui.settings.profile;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.Window;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.SortedMap;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import de.jowisoftware.sshclient.application.settings.Profile;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.ui.settings.AbstractGridBagOptionPanel;
import de.jowisoftware.sshclient.util.StringUtils;

class MainPanel extends AbstractGridBagOptionPanel {
    private static final long serialVersionUID = -8272298107955330533L;

    private final Profile<?> profile;

    private final JTextField hostTextField = new JTextField();
    private final JTextField portTextField = new JTextField();
    private final JTextField userTextField = new JTextField();
    private final JComboBox<String> encodingBox = createEncodingsBox();
    private final JTextField timeoutTextField = new JTextField();
    private final JTextField profileNameTextField = new JTextField();
    private final JTextField commandTextField = new JTextField();
    private final JTextField keepAliveInterval = new JTextField();
    private final JTextField keepAliveCount = new JTextField();

    public MainPanel(final Profile<?> profile, final String profileName,
            final boolean profileNameSettable, final Window parent) {
        super(parent);
        this.profile = profile;
        int y = 0;

        add(label("profiles.general.profilename",
                "Profile name:", 'm', profileNameTextField), makeLabelConstraints(++y));
        profileNameTextField.setText(profileName);
        profileNameTextField.setEnabled(profileNameSettable);
        add(profileNameTextField, makeConstraints(2, y));

        add(label("profiles.general.host", "Host:", 'h', hostTextField),
                makeLabelConstraints(++y));
        hostTextField.setText(profile.getHost());
        add(hostTextField, makeConstraints(2, y));

        add(label("profiles.general.port", "Port:", 'p', portTextField),
                makeLabelConstraints(++y));
        portTextField.setText(Integer.toString(profile.getPort()));
        add(portTextField, makeConstraints(2, y));

        add(label("profiles.general.user", "User:", 'u', userTextField),
                makeLabelConstraints(++y));
        userTextField.setText(profile.getUser());
        add(userTextField, makeConstraints(2, y));

        add(blind(), makeLabelConstraints(++y));
        add(blind(), makeConstraints(2, y));

        add(label("profiles.general.command", "Command:", 'o', commandTextField),
                makeLabelConstraints(++y));
        commandTextField.setText(profile.getCommand());
        add(commandTextField, makeConstraints(2, y));

        add(label("profiles.general.command.info", "(disables forwardings!)"),
                makeLabelConstraints(++y));
        add(blind(), makeConstraints(2, y));

        add(blind(), makeLabelConstraints(++y));
        add(blind(), makeConstraints(2, y));

        add(label("profiles.general.encoding", "Encoding:", 'e', encodingBox),
                makeLabelConstraints(++y));
        encodingBox.setSelectedItem(profile.getCharset().name());
        add(encodingBox, makeConstraints(2, y));

        add(blind(),
                makeLabelConstraints(++y));
        add(blind(), makeConstraints(2, y));

        add(label("profiles.general.timeout", "Timeout (ms):", 't', timeoutTextField),
                makeLabelConstraints(++y));
        timeoutTextField.setText(Integer.toString(profile.getTimeout()));
        add(timeoutTextField, makeConstraints(2, y));

        add(label("profiles.general.keepalivecount",
                "Max. unanswerted keep alives:",
                'k', keepAliveCount), makeLabelConstraints(++y));
        keepAliveCount.setText(Integer.toString(profile.getKeepAliveCount()));
        add(keepAliveCount, makeConstraints(2, y));

        add(label("profiles.general.keepaliveinterval",
                "Keep alive interval (ms):",
                'i', keepAliveInterval), makeLabelConstraints(++y));
        keepAliveInterval.setText(Integer.toString(profile
                .getKeepAliveInterval()));
        add(keepAliveInterval, makeConstraints(2, y));

        fillToBottom(++y);
    }

    private JComboBox<String> createEncodingsBox() {
        final SortedMap<String, Charset> charSets = Charset.availableCharsets();
        final String names[] = new String[charSets.size()];

        int i = 0;
        for (final String name : charSets.keySet()) {
            names[i++] = name;
        }

        Arrays.sort(names);
        return new JComboBox<>(names);
    }

    @Override
    public void save() {
        profile.setCharsetName((String) encodingBox.getSelectedItem());
        profile.setUser(userTextField.getText());
        profile.setHost(hostTextField.getText());
        profile.setPort(StringUtils.getInteger(portTextField.getText(), -1));
        profile.setTimeout(StringUtils.getInteger(timeoutTextField.getText(),
                -1));
        profile.setCommand(commandTextField.getText());
        profile.setKeepAliveCount(StringUtils.getInteger(keepAliveCount.getText(),
                -1));
        profile.setKeepAliveInterval(StringUtils.getInteger(keepAliveInterval.getText(),
                -1));
    }

    public String getProfileName() {
        return profileNameTextField.getText();
    }

    @Override
    public String getTitle() {
        return t("profiles.general.title", "general");
    }
}

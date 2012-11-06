package de.jowisoftware.sshclient.ui.settings;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.GridLayout;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.SortedMap;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.util.StringUtils;

class MainPanel extends AbstractOptionPanel {
    private static final long serialVersionUID = -8272298107955330533L;

    private final AWTProfile profile;

    private final JTextField hostTextField = new JTextField();
    private final JTextField portTextField = new JTextField();
    private final JTextField userTextField = new JTextField();
    private final JComboBox encodingBox = createEncodingsBox();
    private final JTextField timeoutTextField = new JTextField();
    private final JTextField profileNameTextField = new JTextField();
    private final JTextField commandTextField = new JTextField();

    public MainPanel(final AWTProfile profile, final String profileName,
            final boolean profileNameSettable) {
        this.profile = profile;
        setLayout(new GridLayout(11, 2, 5, 0));

        add(label("profiles.general.profilename",
                "Profile name:", 'm', profileNameTextField));
        profileNameTextField.setText(profileName);
        profileNameTextField.setEnabled(profileNameSettable);
        add(profileNameTextField);

        add(label("profiles.general.host", "Host:", 'h', hostTextField));
        hostTextField.setText(profile.getHost());
        add(hostTextField);

        add(label("profiles.general.port", "Port:", 'p', portTextField));
        portTextField.setText(Integer.toString(profile.getPort()));
        add(portTextField);

        add(label("profiles.general.user", "User:", 'u', userTextField));
        userTextField.setText(profile.getUser());
        add(userTextField);

        add(blind());
        add(blind());

        add(label("profiles.general.command", "Command:", 'o', commandTextField));
        commandTextField.setText(profile.getCommand());
        add(commandTextField);

        add(label("profiles.general.command.info", "(disables forwardings!)"));
        add(blind());

        add(blind());
        add(blind());

        add(label("profiles.general.encoding", "Encoding:", 'e', encodingBox));
        encodingBox.setSelectedItem(profile.getCharset().name());
        add(encodingBox);

        add(blind());
        add(blind());

        add(label("profiles.general.timeout", "Timeout (ms):", 't', timeoutTextField));
        timeoutTextField.setText(Integer.toString(profile.getTimeout()));
        add(timeoutTextField);
    }

    private JComboBox createEncodingsBox() {
        final SortedMap<String, Charset> charSets = Charset.availableCharsets();
        final String names[] = new String[charSets.size()];

        int i = 0;
        for (final String name : charSets.keySet()) {
            names[i++] = name;
        }

        Arrays.sort(names);
        return new JComboBox(names);
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
    }

    public String getProfileName() {
        return profileNameTextField.getText();
    }

    @Override
    public String getTitle() {
        return t("profiles.general.title", "general");
    }
}

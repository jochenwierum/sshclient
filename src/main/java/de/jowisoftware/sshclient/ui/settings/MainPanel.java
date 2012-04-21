package de.jowisoftware.sshclient.ui.settings;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.GridLayout;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.SortedMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import de.jowisoftware.sshclient.ui.terminal.AWTProfile;
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

    public MainPanel(final AWTProfile profile, final String profileName,
            final boolean profileNameSettable) {
        this.profile = profile;
        setLayout(new GridLayout(7, 2));

        add(new JLabel(t("profiles.general.profilename",
                "profile name:")));
        profileNameTextField.setText(profileName);
        profileNameTextField.setEnabled(profileNameSettable);
        add(profileNameTextField);

        add(new JLabel(t("profiles.general.host", "host:")));
        hostTextField.setText(profile.getHost());
        add(hostTextField);

        add(new JLabel(t("profiles.general.port", "port:")));
        portTextField.setText(Integer.toString(profile.getPort()));
        add(portTextField);

        add(new JLabel(t("profiles.general.user", "user:")));
        userTextField.setText(profile.getUser());
        add(userTextField);

        add(new JLabel(""));
        add(new JLabel(""));

        add(new JLabel(t("profiles.general.encoding", "encoding:")));
        encodingBox.setSelectedItem(profile.getCharset().name());
        add(encodingBox);

        add(new JLabel(
                t("profiles.general.timeout", "timeout (ms):")));
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
    }

    public String getProfileName() {
        return profileNameTextField.getText();
    }

    @Override
    public String getTitle() {
        return t("profiles.general.title", "general");
    }
}

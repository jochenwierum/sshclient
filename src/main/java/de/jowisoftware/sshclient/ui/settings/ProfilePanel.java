package de.jowisoftware.sshclient.ui.settings;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import de.jowisoftware.sshclient.settings.AWTProfile;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.ui.terminal.AWTGfxInfo;
import de.jowisoftware.sshclient.util.FontUtils;
import de.jowisoftware.sshclient.util.KeyValue;

public class ProfilePanel extends JPanel {
    private static final long serialVersionUID = 663223636542133238L;
    private static final int COLOR_BUTTON_SIZE = 20;

    private static final String COLORTYPE_DEFAULT = "color";
    private static final String COLORTYPE_LIGHT = "lightcolor";
    private static final String COLORTYPE_CURSOR = "cursor";

    private final AWTProfile profile;
    private final JTabbedPane tabbedPane;

    private final JTextField hostTextField = new JTextField();
    private final JTextField portTextField = new JTextField();
    private final JTextField userTextField = new JTextField();
    private final JTextField boundaryTextField = new JTextField();
    private final JComboBox encodingBox = createEncodingsBox();
    private final JTextField timeoutTextField = new JTextField();
    private final JComboBox fontBox = createFontSelectionBox();
    private final JTextField fontSizeTextField = new JTextField(2);
    private final JTextField profileNameTextField = new JTextField();
    private final JComboBox antiAliasingBox = createAntiAliasingBox();
    private final JCheckBox agentForwarding = createAgentForwardingCheckBox();
    private final JCheckBox x11Forwarding = createXForwardingCheckBox();
    private final JTextField x11Host = new JTextField();
    private final JTextField x11Display = new JTextField();

    private final GridBagConstraints normalColumn = createSpacedColumnConstraints(16);
    private final GridBagConstraints spacedColumn = createSpacedColumnConstraints(45);
    private final GridBagConstraints lastColumn = createLastColumnConstraints();
    private JList environmentList;


    public ProfilePanel(final AWTProfile profile, final String profileName, final boolean profileNameSettable) {
        this.profile = profile;

        tabbedPane = new JTabbedPane();
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

        addMainTab(profileName, profileNameSettable);
        addColorTab();
        addForwardingTab();
        addAdvancedTab();
    }

    private void addForwardingTab() {
        final JPanel frame = createTabPane(t("profiles.forwardings.title", "forwardings"));
        addForwardingControls(frame);
    }

    private JPanel createTabPane(final String title) {
        final JPanel frame = new JPanel();
        final JScrollPane scrollPane = new JScrollPane(frame);

        tabbedPane.addTab(title, scrollPane);
        frame.setLayout(new GridBagLayout());

        return frame;
    }

    private void addForwardingControls(final JPanel frame) {
        agentForwarding.setSelected(profile.getAgentForwarding());
        addControl(frame, new JLabel(t("profiles.forwardings.agent", "Agent forwarding")), normalColumn);
        addControl(frame, agentForwarding, lastColumn);

        x11Forwarding.setSelected(profile.getX11Forwarding());
        addControl(frame, new JLabel(t("profiles.forwardings.x11", "X11 forwarding")), normalColumn);
        addControl(frame, x11Forwarding, lastColumn);

        x11Host.setText(profile.getX11Host());
        addControl(frame, new JLabel(t("profiles.forwarding.x11host", "X11 host")), normalColumn);
        addControl(frame, x11Host, lastColumn);

        x11Display.setText(Integer.toString(profile.getX11Display()));
        addControl(frame, new JLabel(t("profiles.forwarding.x11display", "X11 display")), normalColumn);
        addControl(frame, x11Display, lastColumn);

        fillToBottom(frame);
    }

    private JCheckBox createAgentForwardingCheckBox() {
        final JCheckBox checkBox = new JCheckBox(t("profiles.advanced.agentfowarding",
                "forward ssh agent"));
        checkBox.setMnemonic(m("profiles.advanced.agentfowarding", 'a'));
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                profile.setAgentForwarding(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        return checkBox;
    }

    private JCheckBox createXForwardingCheckBox() {
        final JCheckBox checkBox = new JCheckBox(t("profiles.advanced.xfowarding",
                "forward X-Server"));
        checkBox.setMnemonic(m("profiles.advanced.xfowarding", 'x'));
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                profile.setX11Forwarding(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        return checkBox;
    }

    private JComboBox createAntiAliasingBox() {
        final List<KeyValue<String, Object>> hints = FontUtils.getRenderingHintMap();
        final String names[] = new String[hints.size()];

        int i = 0;
        for (final KeyValue<String, Object> hint : hints) {
            names[i++] = t("profile.color.antialiasing." + hint.key, hint.key);
        }

        Arrays.sort(names);
        return new JComboBox(names);
    }

    private GridBagConstraints createSpacedColumnConstraints(final int space) {
        final GridBagConstraints newConstraints = new GridBagConstraints();
        newConstraints.fill = GridBagConstraints.HORIZONTAL;
        newConstraints.insets = new Insets(1, 1, 1, space);
        newConstraints.weightx = 0.0;
        newConstraints.anchor = GridBagConstraints.NORTH;
        return newConstraints;
    }

    private GridBagConstraints createLastColumnConstraints() {
        final GridBagConstraints newConstraints = new GridBagConstraints();
        newConstraints.fill = GridBagConstraints.HORIZONTAL;
        newConstraints.gridwidth = GridBagConstraints.REMAINDER;
        newConstraints.weightx = 1.0;
        newConstraints.anchor = GridBagConstraints.NORTH;
        return newConstraints;
    }

    private void addMainTab(final String profileName, final boolean profileNameSettable) {
        final JPanel frame = createTabPane(t("profiles.general.title", "general"));

        addMainControls(frame, profileName, profileNameSettable);
        fillToBottom(frame);
    }

    private void fillToBottom(final JPanel frame) {
        final GridBagConstraints newConstraints = new GridBagConstraints();
        newConstraints.fill = GridBagConstraints.VERTICAL;
        newConstraints.gridheight = GridBagConstraints.REMAINDER;
        newConstraints.weighty = 1.0;
        newConstraints.anchor = GridBagConstraints.NORTH;
        addControl(frame, new JLabel(""), newConstraints);
    }

    private void addMainControls(final JPanel frame, final String profileName,
            final boolean profileNameSettable) {
        addControl(frame, newFormattedLabel(t("profiles.general.profilename", "profile name:")), normalColumn);
        profileNameTextField.setText(profileName);
        profileNameTextField.setEnabled(profileNameSettable);
        addControl(frame, profileNameTextField, lastColumn);

        addControl(frame, newFormattedLabel(t("profiles.general.host", "host:")), normalColumn);
        hostTextField.setText(profile.getHost());
        addControl(frame, hostTextField, lastColumn);

        addControl(frame, newFormattedLabel(t("profiles.general.port", "port:")), normalColumn);
        portTextField.setText(Integer.toString(profile.getPort()));
        addControl(frame, portTextField, lastColumn);

        addControl(frame, newFormattedLabel(t("profiles.general.user", "user:")), normalColumn);
        userTextField.setText(profile.getUser());
        addControl(frame, userTextField, lastColumn);

        addVerticalSpacing(frame);

        addControl(frame, newFormattedLabel(t("profiles.general.encoding", "encoding:")), normalColumn);
        encodingBox.setSelectedItem(profile.getCharset().name());
        addControl(frame, encodingBox, lastColumn);

        addControl(frame, newFormattedLabel(t("profiles.general.timeout", "timeout (ms):")), normalColumn);
        timeoutTextField.setText(Integer.toString(profile.getTimeout()));
        addControl(frame, timeoutTextField, lastColumn);
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

    private void addVerticalSpacing(final JPanel parent) {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(24, 1, 1, 1);
        constraints.anchor = GridBagConstraints.NORTH;
        final JLabel label = new JLabel("");
        ((GridBagLayout) parent.getLayout()).setConstraints(label, constraints);
        parent.add(label);
    }

    private void addControl(final JPanel parent, final JComponent child,
            final GridBagConstraints constraints) {
        parent.add(child);
        ((GridBagLayout) parent.getLayout()).setConstraints(child, constraints);
    }

    private JLabel newFormattedLabel(final String text) {
        final JLabel label = new JLabel(text);
        label.setAlignmentX(RIGHT_ALIGNMENT);
        return label;
    }

    private void addColorTab() {
        final JPanel frame = createTabPane(t("profiles.gfx.title", "graphics"));

        addColorControls(frame);
        fillToBottom(frame);
    }

    private void addColorControls(final JPanel frame) {
        addFontControls(frame);
        addVerticalSpacing(frame);
        addCursorColorChooser(frame);
        addVerticalSpacing(frame);
        addColorChooserMatrix(frame);
    }

    private void addFontControls(final JPanel frame) {
        addControl(frame, newFormattedLabel(t("profiles.colors.font", "font:")),
                normalColumn);
        fontBox.setSelectedItem(profile.getGfxSettings().getFontName());
        addControl(frame, fontBox, lastColumn);

        addControl(frame, new JLabel(t("profiles.colors.font.size", "size:")), normalColumn);
        fontSizeTextField.setText(Integer.toString(
                profile.getGfxSettings().getFontSize()));

        addControl(frame, fontSizeTextField, lastColumn);

        antiAliasingBox.setSelectedIndex(profile.getGfxSettings().getAntiAliasingMode());
        addControl(frame, new JLabel(t("profile.color.antialiasing", "antialiasing:")), normalColumn);
        addControl(frame, antiAliasingBox, lastColumn);
    }

    private JComboBox createFontSelectionBox() {
        final String names[] = FontUtils.getCachedMonospacedFonts();
        Arrays.sort(names);
        return new JComboBox(names);
    }

    private void addColorChooserMatrix(final JPanel frame) {
        addColorChooserMatrixTitles(frame);
        addColors(frame);
    }

    private void addColorChooserMatrixTitles(final JPanel frame) {
        GridBagConstraints constraints = (GridBagConstraints) normalColumn.clone();
        constraints.gridwidth = 2;
        JLabel label = new JLabel(t("profiles.colors.normal", "normal colors"));
        ((GridBagLayout) frame.getLayout()).setConstraints(label, constraints);
        frame.add(label);

        constraints = (GridBagConstraints) constraints.clone();
        constraints.fill = GridBagConstraints.REMAINDER;
        label = new JLabel(t("profiles.colors.light", "light colors"));
        ((GridBagLayout) frame.getLayout()).setConstraints(label, constraints);
        frame.add(label);

        addControl(frame, new JLabel(), lastColumn);
    }

    private void addCursorColorChooser(final JPanel frame) {
        addControl(frame, newFormattedLabel(t("profiles.colors.cursor",
                "cursor color:")), normalColumn);
        addControl(frame, createColorButton(
                profile.getGfxSettings().getCursorColor(),
                COLORTYPE_CURSOR), normalColumn);
        addControl(frame, new JLabel(), lastColumn);
    }

    private void addColors(final JPanel frame) {
        for (final ColorName color : ColorName.values()) {
            addControl(frame, newFormattedLabel(
                    t(colorTranslation(color, COLORTYPE_DEFAULT),
                            color.niceName().toLowerCase())), normalColumn);
            addControl(frame, createColorButton(
                    profile.getGfxSettings().getColorMap().get(color),
                    COLORTYPE_DEFAULT + "." + color.name()), spacedColumn);

            addControl(frame, newFormattedLabel(
                    t(colorTranslation(color, COLORTYPE_LIGHT),
                            color.niceName().toLowerCase())), normalColumn);
            addControl(frame, createColorButton(
                    profile.getGfxSettings().getLightColorMap().get(color),
                    COLORTYPE_LIGHT + "." + color.name()), normalColumn);

            endColumn(frame);
        }
    }

    private void endColumn(final JPanel frame) {
        addControl(frame, new JLabel(), lastColumn);
    }

    private String colorTranslation(final ColorName color, final String colorPrefix) {
        return "profiles." + colorPrefix + "." + color.name();
    }

    private JButton createColorButton(final Color initialColor, final String colorName) {
        final JButton button = new JButton("change") {
            private static final long serialVersionUID = 2446946735787872705L;

            @Override
            protected void paintComponent(final Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
            }

        };
        button.setActionCommand(colorName);
        button.setBackground(initialColor);

        final Dimension size = getMinimumSize();
        size.height = COLOR_BUTTON_SIZE;
        size.width = COLOR_BUTTON_SIZE;
        button.setMinimumSize(size);
        button.setPreferredSize(size);

        button.addActionListener(createColorActionListener());
        return button;
    }

    private ActionListener createColorActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final JButton source = (JButton) e.getSource();
                final Color newColor = JColorChooser.showDialog(
                        ProfilePanel.this,
                        t("profiles.gfx.choosecolor", "choose color"),
                        source.getBackground());

                if (newColor != null) {
                    source.setBackground(newColor);
                    saveColor(e.getActionCommand(), newColor);
                }
            }
        };
    }

    protected void saveColor(final String actionCommand, final Color newColor) {
        final String[] colorTypes = actionCommand.split("\\.");

        final AWTGfxInfo gfxSettings = profile.getGfxSettings();
        if (COLORTYPE_CURSOR.equals(colorTypes[0])) {
            gfxSettings.setCursorColor(newColor);
        } else if (COLORTYPE_DEFAULT.equals(colorTypes[1])) {
            gfxSettings.getColorMap().put(
                    ColorName.valueOf(colorTypes[1]),
                    newColor);
        } else if (COLORTYPE_LIGHT.equals(colorTypes[0])) {
            gfxSettings.getLightColorMap().put(
                    ColorName.valueOf(colorTypes[1]), newColor);
        }
    }

    private void addAdvancedTab() {
        final JPanel frame = createTabPane(t("profiles.advanced.title", "advanced"));
        addAdvancedControls(frame);
    }

    private void addAdvancedControls(final JPanel frame) {
        addControl(frame, newFormattedLabel(
                t("profiles.advanced.environment", "environment:")), normalColumn);
        addControl(frame, createEnvironmentPanel(), lastColumn);

        boundaryTextField.setText(profile.getGfxSettings().getBoundaryChars());
        addControl(frame, newFormattedLabel(
                t("profiles.advanced.wordcharacters", "word characters:")), normalColumn);
        addControl(frame, boundaryTextField, lastColumn);
    }

    private JComponent createEnvironmentPanel() {
        final JPanel panel = new JPanel();

        Dimension minSize = panel.getMinimumSize();
        minSize.height = 400;
        panel.setMinimumSize(minSize);

        panel.setLayout(new BorderLayout());

        environmentList = new JList(new DefaultListModel());
        updateEnvironmentList();
        final JScrollPane scrollPane = new JScrollPane(environmentList);
        minSize = scrollPane.getMinimumSize();
        minSize.height = 80;
        scrollPane.setMinimumSize(minSize);
        scrollPane.setPreferredSize(minSize);
        panel.add(scrollPane, BorderLayout.CENTER);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        final JPanel textfieldPanel = new JPanel();
        textfieldPanel.setLayout(new GridLayout(1, 2));
        final JTextField key = new JTextField();
        final JTextField value = new JTextField();
        textfieldPanel.add(key);
        textfieldPanel.add(value);

        final JButton removeButton = createRemoveEnvironmentButton();
        final JButton addButton = createAddEnvironmentButton(key, value);

        buttonPanel.add(textfieldPanel, BorderLayout.CENTER);
        buttonPanel.add(addButton, BorderLayout.EAST);
        buttonPanel.add(removeButton, BorderLayout.SOUTH);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createRemoveEnvironmentButton() {
        final JButton button = new JButton(t("profiles.advanced.remove", "remove"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int index = environmentList.getSelectedIndex();
                if (index == -1) {
                    return;
                }

                final String key = getEnvironmentKeys()[index];
                profile.getEnvironment().remove(key);
                updateEnvironmentList();
            }
        });
        return button;
    }

    private JButton createAddEnvironmentButton(final JTextField key, final JTextField value) {
        final JButton button = new JButton(t("profiles.advanced.add", "add"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (Pattern.matches("[A-Za-z0-9_]+", key.getText())) {
                    profile.getEnvironment().put(
                            key.getText(), value.getText());
                    updateEnvironmentList();
                    key.setText("");
                    value.setText("");
                }
            }
        });
        return button;
    }

    private void updateEnvironmentList() {
        final DefaultListModel model = ((DefaultListModel) environmentList.getModel());

        final Map<String, String> envMap = profile.getEnvironment();
        final String[] keyList = getEnvironmentKeys();

        model.clear();
        for (int i = 0; i < keyList.length; ++i) {
            model.addElement(keyList[i] + " = " + envMap.get(keyList[i]));
        }
    }

    private String[] getEnvironmentKeys() {
        final Map<String, String> envMap = profile.getEnvironment();
        final String[] keyList = envMap.keySet().toArray(new String[0]);
        Arrays.sort(keyList);
        return keyList;
    }

    public void applyUnboundValues() {
        profile.setCharsetName((String) encodingBox.getSelectedItem());
        profile.setUser(userTextField.getText());
        profile.setHost(hostTextField.getText());
        profile.setPort(getInteger(portTextField.getText(), -1));
        profile.setTimeout(getInteger(timeoutTextField.getText(), -1));
        profile.getGfxSettings().setBoundaryChars(boundaryTextField.getText());
        profile.getGfxSettings().setAntiAliasingMode(antiAliasingBox.getSelectedIndex());
        profile.setX11Host(x11Host.getText());
        profile.setX11Display(getInteger(x11Display.getText(), 0));

        final AWTGfxInfo gfxSettings = profile.getGfxSettings();
        gfxSettings.setFont(
                (String) fontBox.getSelectedItem(),
                getInteger(fontSizeTextField.getText(), 10));
    }

    private int getInteger(final String string, final int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch(final Exception e) {
            return defaultValue;
        }
    }

    public String getProfileName() {
        return profileNameTextField.getText();
    }
}

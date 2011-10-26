package de.jowisoftware.sshclient.ui.settings;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import de.jowisoftware.sshclient.settings.Profile;
import de.jowisoftware.sshclient.terminal.ColorName;
import de.jowisoftware.sshclient.ui.terminal.GfxInfo;
import de.jowisoftware.sshclient.util.FontUtils;

public class SettingsPanel extends JPanel {
    private static final long serialVersionUID = 663223636542133238L;
    private static final int COLOR_BUTTON_SIZE = 20;

    private static final String COLORTYPE_DEFAULT = "color";
    private static final String COLORTYPE_LIGHT = "lightcolor";
    private static final String COLORTYPE_CURSOR = "cursor";

    private final Profile profile;
    private final JTabbedPane tabbedPane;

    private JTextField hostTextField;
    private JTextField portTextField;
    private JTextField userTextField;
    private JComboBox encodingBox;
    private JTextField timeoutTextField;
    private JComboBox fontBox;
    private JComboBox fontStyleBox;
    private JTextField fontSizeTextField;
    private JTextField profileNameTextField;

    private final GridBagConstraints normalColumn;
    private final GridBagConstraints spacedColumn;
    private final GridBagConstraints lastColumn;
    private JList environmentList;


    public SettingsPanel(final Profile profile, final String profileName, final boolean profileNameSettable) {
        this.profile = profile;

        tabbedPane = new JTabbedPane();
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

        normalColumn = createSpacedColumnConstraints(16);
        spacedColumn = createSpacedColumnConstraints(45);
        lastColumn = createLastColumnConstraints();

        addMainTab(profileName, profileNameSettable);
        addColorTab();
        addAdvancedTab();
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
        final JPanel frame = new JPanel();
        tabbedPane.addTab(t("profiles.general.title", "general"), frame);
        frame.setLayout(new GridBagLayout());

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
        profileNameTextField = new JTextField(profileName);
        profileNameTextField.setEnabled(profileNameSettable);
        addControl(frame, profileNameTextField, lastColumn);

        addControl(frame, newFormattedLabel(t("profiles.general.host", "host:")), normalColumn);
        hostTextField = new JTextField(profile.getHost());
        addControl(frame, hostTextField, lastColumn);

        addControl(frame, newFormattedLabel(t("profiles.general.port", "port:")), normalColumn);
        portTextField = new JTextField(Integer.toString(profile.getPort()));
        addControl(frame, portTextField, lastColumn);

        addControl(frame, newFormattedLabel(t("profiles.general.user", "user:")), normalColumn);
        userTextField = new JTextField(profile.getUser());
        addControl(frame, userTextField, lastColumn);

        addVerticalSpacing(frame);

        addControl(frame, newFormattedLabel(t("profiles.general.encoding", "encoding:")), normalColumn);
        encodingBox = createEncodingsBox();
        encodingBox.setSelectedItem(profile.getCharset().name());
        addControl(frame, encodingBox, lastColumn);

        addControl(frame, newFormattedLabel(t("profiles.general.timeout", "timeout (ms):")), normalColumn);
        timeoutTextField = new JTextField(Integer.toString(profile.getTimeout()));
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
        final JPanel frame = new JPanel();
        final JScrollPane scrollPane = new JScrollPane(frame);
        tabbedPane.addTab(t("profiles.gfx.title", "graphics"), scrollPane);
        frame.setLayout(new GridBagLayout());

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
        final Font font = profile.getGfxSettings().getFont();

        addControl(frame, newFormattedLabel(t("profiles.colors.font", "font:")),
                normalColumn);
        fontBox = createFontSelectionBox();
        fontBox.setSelectedItem(font.getName());
        addControl(frame, fontBox, lastColumn);

        addControl(frame, new JLabel(t("profiles.colors.font.size", "size:")), normalColumn);
        fontStyleBox = createFontStyleBox();
        fontStyleBox.setSelectedIndex(font.getStyle());
        addControl(frame, fontStyleBox, lastColumn);

        addControl(frame, new JLabel(t("profiles.colors.font.style", "style:")), normalColumn);
        fontSizeTextField = new JTextField(Integer.toString(font.getSize()), 2);
        addControl(frame, fontSizeTextField, lastColumn);
    }

    private JComboBox createFontStyleBox() {
        final String names[] = new String[] {
                t("profiles.colors.font.normal", "normal"),
                t("profiles.colors.font.bold", "bold"),
                t("profiles.colors.font.italic", "italic"),
                t("profiles.colors.font.bold+italic", "bold + italic")
        };
        return new JComboBox(names);
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
        addControl(frame, new JLabel(""), lastColumn);
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
                        SettingsPanel.this,
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

        final GfxInfo gfxSettings = profile.getGfxSettings();
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
        final JPanel frame = new JPanel();
        tabbedPane.addTab(t("profiles.advanced.title", "advanced"), frame);
        frame.setLayout(new GridBagLayout());

        addAdvancedControls(frame);
        fillToBottom(frame);
    }

    private void addAdvancedControls(final JPanel frame) {
        addControl(frame, newFormattedLabel(
                t("profiles.advanced.environment", "environment:")), normalColumn);

        addControl(frame, createEnvironmentPanel(), lastColumn);
    }

    private JComponent createEnvironmentPanel() {
        final JPanel panel = new JPanel();

        Dimension minSize = panel.getMinimumSize();
        minSize.height = 400;
        panel.setMinimumSize(minSize);

        panel.setLayout(new BorderLayout());

        environmentList = new JList(new DefaultListModel());
        updateeEnvironmentList();
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
                updateeEnvironmentList();
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
                    updateeEnvironmentList();
                    key.setText("");
                    value.setText("");
                }
            }
        });
        return button;
    }

    private void updateeEnvironmentList() {
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
        try {
            profile.setCharset(Charset.forName((String) encodingBox.getSelectedItem()));
        } catch(final Exception e) {
            profile.setCharset(null);
        }

        profile.setUser(userTextField.getText());
        profile.setHost(hostTextField.getText());
        profile.setPort(getInteger(portTextField.getText(), -1));
        profile.setTimeout(getInteger(timeoutTextField.getText(), -1));

        final GfxInfo gfxSettings = profile.getGfxSettings();
        gfxSettings.setFont(new Font(
                (String) fontBox.getSelectedItem(),
                fontStyleBox.getSelectedIndex(),
                getInteger(fontSizeTextField.getText(), 10)));
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

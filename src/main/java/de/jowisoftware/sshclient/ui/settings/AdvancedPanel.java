package de.jowisoftware.sshclient.ui.settings;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import de.jowisoftware.sshclient.ui.terminal.AWTProfile;
import de.jowisoftware.sshclient.ui.terminal.CloseTabMode;

class AdvancedPanel extends AbstractGridBagOptionPanel {
    private static final long serialVersionUID = -8918842950853608446L;

    private final AWTProfile profile;

    private JList environmentList;
    private final JTextField boundaryTextField = new JTextField();
    private final JComboBox closeTabBox = createCloseTabBox();

    public AdvancedPanel(final AWTProfile profile) {
        this.profile = profile;

        closeTabBox.setSelectedIndex(profile.getCloseTabMode().ordinal());
        add(new JLabel(t("profiles.advanced.close", "close tab:")),
                makeConstraints(1, 1));
        add(closeTabBox, makeConstraints(2, 1));

        add(new JLabel(t("profiles.advanced.environment", "environment:")),
                makeConstraints(1, 2));
        add(createEnvironmentPanel(), makeConstraints(2, 2));

        boundaryTextField.setText(profile.getGfxSettings().getBoundaryChars());
        add(new JLabel(t("profiles.advanced.wordcharacters", "word characters:")),
                makeConstraints(1, 3));
        add(boundaryTextField, makeConstraints(2, 3));

        fillToBottom(4);
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


    private JComboBox createCloseTabBox() {
        final CloseTabMode modes[] = CloseTabMode.values();
        final String names[] = new String[modes.length];

        int i = 0;
        for (final CloseTabMode mode : modes) {
            names[i++] = t("profiles.close." + mode.toString().toLowerCase(),
                    mode.niceName);
        }

        return new JComboBox(names);
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

    @Override
    public void save() {
        profile.getGfxSettings().setBoundaryChars(boundaryTextField.getText());
        profile.setCloseTabMode(CloseTabMode.values()[closeTabBox.getSelectedIndex()]);
    }

    @Override
    public String getTitle() {
        return t("profiles.advanced.title", "advanced");
    }
}

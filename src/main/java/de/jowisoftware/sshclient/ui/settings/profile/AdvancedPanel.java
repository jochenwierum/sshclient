package de.jowisoftware.sshclient.ui.settings.profile;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.ui.settings.AbstractGridBagOptionPanel;
import de.jowisoftware.sshclient.ui.terminal.CloseTabMode;

class AdvancedPanel extends AbstractGridBagOptionPanel {
    private static final long serialVersionUID = -8918842950853608446L;

    private final AWTProfile profile;

    private final JList<String> environmentList = new JList<>(
            new DefaultListModel<String>());
    private final JTextField boundaryTextField = new JTextField();
    private final JComboBox<String> closeTabBox = createCloseTabBox();

    public AdvancedPanel(final AWTProfile profile, final Window parent) {
        super(parent);
        this.profile = profile;

        int y = 0;

        closeTabBox.setSelectedIndex(profile.getCloseTabMode().ordinal());
        add(label("profiles.advanced.close", "Close tab:", 'c', closeTabBox),
                makeLabelConstraints(++y));
        add(closeTabBox, makeConstraints(2, y));

        add(label("profiles.advanced.environment", "Environment:"),
                makeLabelConstraints(++y));
        final GridBagConstraints constraints = makeConstraints(2, y);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        add(createEnvironmentPanel(), constraints);

        boundaryTextField.setText(profile.getGfxSettings().getBoundaryChars());
        add(label("profiles.advanced.wordcharacters", "Word characters:",
                'w', boundaryTextField), makeLabelConstraints(++y));
        add(boundaryTextField, makeConstraints(2, y));
    }

    private JComponent createEnvironmentPanel() {
        final JPanel panel = new JPanel(new BorderLayout());

        updateEnvironmentList();
        environmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(environmentList), BorderLayout.CENTER);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        final JPanel textfieldPanel = new JPanel();
        textfieldPanel.setLayout(new GridLayout(1, 2));
        final JTextField key = new JTextField();
        final JTextField value = new JTextField();
        textfieldPanel.add(key);
        textfieldPanel.add(value);

        final JButton deleteButton = createDeleteEnvironmentButton();
        final JButton addButton = createAddEnvironmentButton(key, value);

        buttonPanel.add(textfieldPanel, BorderLayout.CENTER);
        buttonPanel.add(addButton, BorderLayout.EAST);
        buttonPanel.add(deleteButton, BorderLayout.SOUTH);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createAddEnvironmentButton(final JTextField key, final JTextField value) {
        final JButton button = new JButton(t("add", "Add"));
        button.setMnemonic(m("add", 'a'));
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
        final DefaultListModel<String> model = ((DefaultListModel<String>) environmentList
                .getModel());

        final Map<String, String> envMap = profile.getEnvironment();
        final String[] keyList = getEnvironmentKeys();

        model.clear();
        for (final String key : keyList) {
            model.addElement(key + " = " + envMap.get(key));
        }
    }

    private String[] getEnvironmentKeys() {
        final Map<String, String> envMap = profile.getEnvironment();
        final String[] keyList = envMap.keySet().toArray(new String[envMap.size()]);
        Arrays.sort(keyList);
        return keyList;
    }


    private JComboBox<String> createCloseTabBox() {
        final CloseTabMode modes[] = CloseTabMode.values();
        final String names[] = new String[modes.length];

        int i = 0;
        for (final CloseTabMode mode : modes) {
            names[i++] = t("profiles.close." + mode.toString().toLowerCase(),
                    mode.niceName);
        }

        return new JComboBox<>(names);
    }

    private JButton createDeleteEnvironmentButton() {
        final JButton button = new JButton(t("delete", "Delete"));
        button.setMnemonic(m("delete", 'd'));
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

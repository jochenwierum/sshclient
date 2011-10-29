package de.jowisoftware.sshclient.ui.settings;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import de.jowisoftware.sshclient.settings.AWTProfile;
import de.jowisoftware.sshclient.settings.ApplicationSettings;
import de.jowisoftware.sshclient.settings.validation.ValidationResult;
import de.jowisoftware.sshclient.ui.settings.validation.AWTProfileValidator;

public class ProfilesDialog extends JDialog {
    private static final long serialVersionUID = 4811060219661889812L;
    private final ApplicationSettings settings;
    private SettingsPanel settingsFrame;
    private JPanel editPanel;
    private JList selectionList;
    private JButton closeButton;
    private JButton saveButton;
    private JButton revertButton;
    private JButton addButton;
    private JButton removeButton;
    private JButton editButton;
    private AWTProfile profileUnderConstruction;
    private String profileUnderConstructionName;
    private JPanel buttonPanel;

    public ProfilesDialog(final Frame parent, final ApplicationSettings settings) {
        super(parent, t("profiles.title", "Profiles"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.settings = settings;
        this.settingsFrame = null;

        setLayout(new BorderLayout());
        addControls();

        applyVisibility(parent);
    }

    private void addControls() {
        editPanel = createEditPanel();
        add(editPanel, BorderLayout.CENTER);
        add(createSelectionFrame(), BorderLayout.WEST);
        add(createButtonBar(), BorderLayout.SOUTH);
    }

    private JPanel createEditPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(""), BorderLayout.CENTER);
        buttonPanel = createEditButtonBar();
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createEditButtonBar() {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        saveButton = createSaveButton();
        panel.add(saveButton);

        revertButton = createRevertButton();
        panel.add(revertButton);

        return panel;
    }

    private JButton createRevertButton() {
        final JButton button = new JButton(t("profiles.revert", "Revert"));
        button.setMnemonic(m("profiles.revert", 'r'));
        button.setEnabled(false);
        button.addActionListener(createEditButtonListener(false));
        return button;
    }

    private JButton createSaveButton() {
        final JButton button = new JButton(t("profiles.save", "Save"));
        button.setMnemonic(m("profiles.save", 's'));
        button.setEnabled(false);
        button.addActionListener(createEditButtonListener(true));
        return button;
    }

    private ActionListener createEditButtonListener(final boolean save) {
        return new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (save) {
                    settingsFrame.applyUnboundValues();
                    final ValidationResult errors =
                            new AWTProfileValidator(profileUnderConstruction)
                                .validateProfile();
                    if (errors.hadErrors()) {
                        final String message = buildErrorMessage(errors.getErrors());
                        JOptionPane.showMessageDialog(ProfilesDialog.this, message);
                        return;
                    } else {
                        settings.getProfiles().remove(profileUnderConstructionName);
                        settings.getProfiles().put(settingsFrame.getProfileName(),
                                profileUnderConstruction);
                    }
                }

                updateWindowState(false);
            }
        };
    }

    private Component createSelectionFrame() {
        final JPanel panel = new JPanel();
        final GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        final GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weighty = 1;

        final JComponent component = prepareSelectionList();
        layout.addLayoutComponent(component, constraints);
        panel.add(component);

        constraints.weighty = 0.1;

        editButton = createEditButton();
        layout.addLayoutComponent(editButton, constraints);
        panel.add(editButton);

        removeButton = createRemoveButton();
        layout.addLayoutComponent(removeButton, constraints);
        panel.add(removeButton);

        addButton = createAddButton();
        layout.addLayoutComponent(addButton, constraints);
        panel.add(addButton);

        return panel;
    }

    private JButton createAddButton() {
        final JButton button = new JButton(t("new", "New"));
        button.setMnemonic(m("new", 'n'));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                createProfile();
            }
        });
        return button;
    }

    private JButton createRemoveButton() {
        final JButton button = new JButton(t("delete", "Delete"));
        button.setMnemonic(m("delete", 'd'));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (selectionList.getSelectedIndex() != -1) {
                    delete((String) selectionList.getSelectedValue());
                }
            }
        });
        return button;
    }

    private JButton createEditButton() {
        final JButton button = new JButton(t("edit", "Edit"));
        button.setMnemonic(m("edit", 'e'));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (selectionList.getSelectedIndex() != -1) {
                    edit((String) selectionList.getSelectedValue());
                }
            }
        });
        return button;
    }

    private void edit(final String selectedValue) {
        profileUnderConstructionName = selectedValue;
        profileUnderConstruction = (AWTProfile) settings.getProfiles().get(selectedValue).clone();

        settingsFrame = new SettingsPanel(profileUnderConstruction,
                selectedValue, true);

        updateWindowState(true);
    }

    private void updateWindowState(final boolean editingProfile) {
        if (editingProfile) {
            editPanel.removeAll();
            editPanel.add(settingsFrame, BorderLayout.CENTER);
            editPanel.add(buttonPanel, BorderLayout.SOUTH);
            getRootPane().setDefaultButton(saveButton);
        } else {
            settingsFrame = null;
            editPanel.removeAll();
            editPanel.add(new JLabel(""), BorderLayout.CENTER);
            editPanel.add(buttonPanel, BorderLayout.SOUTH);
            updateSelectionList();
            getRootPane().setDefaultButton(closeButton);
        }
        validate();

        addButton.setEnabled(!editingProfile);
        removeButton.setEnabled(!editingProfile);
        editButton.setEnabled(!editingProfile);
        selectionList.setEnabled(!editingProfile);
        closeButton.setEnabled(!editingProfile);
        saveButton.setEnabled(editingProfile);
        revertButton.setEnabled(editingProfile);
    }

    private void delete(final String selectedValue) {
        settings.getProfiles().remove(selectedValue);
        updateSelectionList();
    }

    private void createProfile() {
        final AWTProfile profile = new AWTProfile();
        String name = t("profiles.new", "new profile");

        if (settings.getProfiles().containsKey(name)) {
            int counter = 1;
            while (settings.getProfiles().containsKey(name + counter)) {
                ++counter;
            }
            name = name + counter;
        }

        settings.getProfiles().put(name, profile);
        edit(name);
    }

    private JScrollPane prepareSelectionList() {
        selectionList = new JList();
        selectionList.setModel(new DefaultListModel());
        final JScrollPane pane = new JScrollPane(selectionList);
        selectionList.addMouseListener(createListMouseListener());
        updateSelectionList();
        return pane;
    }

    private MouseListener createListMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if(e.getClickCount() == 2){
                    final int index = selectionList.locationToIndex(e.getPoint());
                    final ListModel model = selectionList.getModel();
                    selectionList.ensureIndexIsVisible(index);
                    edit((String) model.getElementAt(index));
                }
            }
        };
    }

    private void updateSelectionList() {
        final List<String> names = new ArrayList<String>(settings.getProfiles().keySet());
        Collections.sort(names);
        final DefaultListModel model = ((DefaultListModel) selectionList.getModel());
        model.clear();

        for (final String name : names) {
            model.addElement(name);
        }
        validate();
    }

    private JPanel createButtonBar() {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        closeButton = createCloseButton();
        panel.add(closeButton);
        getRootPane().setDefaultButton(closeButton);

        return panel;
    }

    private JButton createCloseButton() {
        final JButton button = new JButton(t("close", "Close"));
        button.setMnemonic(m("close", 'c'));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        return button;
    }

    private void applyVisibility(final Frame parent) {
        setModalityType(ModalityType.APPLICATION_MODAL);
        setSize(600, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private String buildErrorMessage(final Map<String, String> errors) {
        final StringBuilder message = new StringBuilder();

        message.append(t("profiles.errors.message",
                "this profile contains one or more errors:"));
        for (final Entry<String, String> error : errors.entrySet()) {
            message.append("\n");
            message.append(error.getValue());
        }

        return message.toString();
    }

    public void showSettings() {
        setVisible(true);
    }
}

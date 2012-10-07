package de.jowisoftware.sshclient.ui.settings;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.application.ApplicationSettings;
import de.jowisoftware.sshclient.application.validation.ValidationResult;
import de.jowisoftware.sshclient.encryption.CryptoException;
import de.jowisoftware.sshclient.ui.settings.validation.AWTProfileValidator;
import de.jowisoftware.sshclient.ui.terminal.AWTProfile;

public class ProfilesDialog extends JDialog {
    private static final Logger LOGGER = Logger.getLogger(ProfilesDialog.class);
    private static final long serialVersionUID = 4811060219661889812L;

    private final JList selectionList = new JList(new DefaultListModel());
    private final JButton closeButton = createCloseButton();
    private final JButton saveButton = createSaveButton();
    private final JButton revertButton = createRevertButton();
    private final JButton addButton = createAddButton();
    private final JButton removeButton = createRemoveButton();
    private final JButton editButton = createEditButton();
    private final JButton templateButton = createTemplateButton();
    private final JPanel buttonPanel = createEditButtonBar();
    private final JPanel editPanel = createEditPanel();

    private AWTProfile profileUnderConstruction;
    private String profileUnderConstructionName;
    private ProfilePanel settingsFrame;

    private final ApplicationSettings settings;
    private boolean profileUnderConstructionIsTemplate;

    public ProfilesDialog(final JFrame parent, final ApplicationSettings settings) {
        super(parent, t("profiles.title", "Profiles"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.settings = settings;
        this.settingsFrame = null;

        setLayout(new BorderLayout());
        addControls();

        applyVisibility(parent);
    }

    private void addControls() {
        add(editPanel, BorderLayout.CENTER);
        add(createSelectionFrame(), BorderLayout.WEST);
        add(createButtonBar(), BorderLayout.SOUTH);
    }

    private JPanel createEditPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(""), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createEditButtonBar() {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panel.add(saveButton);
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
                        if (!profileUnderConstructionIsTemplate) {
                            settings.getProfiles().remove(profileUnderConstructionName);
                            settings.getProfiles().put(settingsFrame.getProfileName(),
                                    profileUnderConstruction);
                        } else {
                            settings.setDefaultProfile(profileUnderConstruction);
                        }
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

        layout.addLayoutComponent(editButton, constraints);
        panel.add(editButton);

        layout.addLayoutComponent(removeButton, constraints);
        panel.add(removeButton);

        layout.addLayoutComponent(addButton, constraints);
        panel.add(addButton);

        layout.addLayoutComponent(templateButton, constraints);
        panel.add(templateButton);

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
        updateButtonDimension(button);
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
        updateButtonDimension(button);
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
        updateButtonDimension(button);
        return button;
    }

    private JButton createTemplateButton() {
        final JButton button = new JButton(t("settings.edittemplate", "Edit template"));
        button.setMnemonic(m("settings.edittemplate", 't'));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                editTemplate();
            }
        });
        updateButtonDimension(button);
        return button;
    }

    private void updateButtonDimension(final JButton button) {
        final int width = (int) (button.getPreferredSize().width * 1.2);
        final int height = button.getMinimumSize().height;
        button.setPreferredSize(new Dimension(width, height));
    }

    private void edit(final String selectedValue) {
        profileUnderConstructionName = selectedValue;
        profileUnderConstructionIsTemplate = false;
        profileUnderConstruction = new AWTProfile(settings.getProfiles().get(selectedValue));

        settingsFrame = new ProfilePanel(profileUnderConstruction,
                selectedValue, true);

        updateWindowState(true);
    }

    private void editTemplate() {
        profileUnderConstructionIsTemplate = true;
        profileUnderConstruction = new AWTProfile(settings.getDefaultProfile());

        settingsFrame = new ProfilePanel(profileUnderConstruction,
                "(default)", false);

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
        templateButton.setEnabled(!editingProfile);
        closeButton.setEnabled(!editingProfile);
        saveButton.setEnabled(editingProfile);
        revertButton.setEnabled(editingProfile);
    }

    private void delete(final String selectedValue) {
        final int result = JOptionPane.showConfirmDialog(this, t("profiles.delete.question",
                "You are about to delete '%s'. Are you sure?", selectedValue),
                getTitle(), JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            deletePasswordIfUnused(selectedValue);
            settings.getProfiles().remove(selectedValue);
            updateSelectionList();
        }
    }

    private void deletePasswordIfUnused(final String selectedValue) {
        final String passwordId = passwordId(settings.getProfiles().get(selectedValue));

        for (final Entry<String, AWTProfile> profileEntry : settings.getProfiles().entrySet()) {
            if (!profileEntry.getKey().equals(selectedValue)
                    && passwordId.equals(passwordId(profileEntry.getValue()))) {
                return;
            }
        }

        try {
            settings.getPasswordStorage().deletePassword(passwordId);
        } catch (final CryptoException e) {
            LOGGER.error("Could not remove password: " + selectedValue, e);
        }
    }

    private String passwordId(final AWTProfile p) {
        return p.getUser() + "@" + p.getHost();
    }

    private void createProfile() {
        final AWTProfile profile = new AWTProfile(settings.getDefaultProfile());
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
        final JScrollPane pane = new JScrollPane(selectionList);
        selectionList.addMouseListener(createListMouseListener());
        updateSelectionList();

        final Dimension size = selectionList.getPreferredSize();
        selectionList.setPreferredSize(new Dimension((int) (size.width * 1.3), size.height));
        return pane;
    }

    private MouseListener createListMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if(e.getClickCount() == 2 && selectionList.isEnabled()){
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

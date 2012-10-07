package de.jowisoftware.sshclient.ui.settings.global;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.jowisoftware.sshclient.application.ApplicationSettings;
import de.jowisoftware.sshclient.application.ApplicationSettings.TabState;
import de.jowisoftware.sshclient.application.BellType;
import de.jowisoftware.sshclient.i18n.Translation;

public class SettingsDialog extends JDialog {
    private static final long serialVersionUID = -5205216274422883565L;
    private final ApplicationSettings settings;
    private JComboBox logTabStateCombobox;
    private JComboBox keyTabStateCombobox;
    private JComboBox languageCombobox;
    private JComboBox bellTypeCombobox;
    private JCheckBox unlockKeysCheckbox;

    private static class Language {
        private final String name;
        public final String key;

        public Language(final String name, final String key) {
            this.name = name;
            this.key = key;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public SettingsDialog(final Window parent, final ApplicationSettings settings) {
        super(parent, t("settings.title", "Settings"));
        this.settings = settings;

        addContent();
        setupWindow(parent);
    }

    private void addContent() {
        setLayout(new BorderLayout());

        add(createMainContent(), BorderLayout.CENTER);
        add(createButtonBar(), BorderLayout.SOUTH);
    }

    private Component createMainContent() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 8, 0));

        addLanguageComponents(panel);
        addLogTabStateComponents(panel);
        addKeyTabStateComponents(panel);
        addUnlockKeysComponents(panel);
        addBellComponents(panel);

        return panel;
    }

    private void addBellComponents(final JPanel panel) {
        panel.add(label(t("settings.belltype", "Bell type:"), m("settings.belltype", 'b')));
        bellTypeCombobox = createBellTypeCombobox();
        panel.add(bellTypeCombobox);
    }

    private JComboBox createBellTypeCombobox() {
        final BellType values[] = BellType.values();
        final String texts[] = new String[values.length];
        int selectedIndex = 0;

        for (int i = 0; i < values.length; ++i) {
            texts[i] = t("settings.belltype."+values[i].name().toLowerCase(),
                    values[i].name().toLowerCase());

            if (values[i] == settings.getBellType()) {
                selectedIndex = i;
            }
        }

        final JComboBox combobox = new JComboBox(texts);
        combobox.setSelectedIndex(selectedIndex);
        return combobox;
    }

    private void addUnlockKeysComponents(final JPanel panel) {
        panel.add(label(t("settings.unlockkeys", "Unlock SSH keys:")));
        unlockKeysCheckbox = createUnlockKeysCheckbox();
        panel.add(unlockKeysCheckbox);
    }

    private JCheckBox createUnlockKeysCheckbox() {
        final JCheckBox checkbox = new JCheckBox(t("settings.unlockkeys.description",
                "unlock SSH keys on startup"), settings.getUnlockKeysOnStartup());
        checkbox.setMnemonic(m("settings.unlockkeys.description", 'u'));
        return checkbox;
    }

    private void addLanguageComponents(final JPanel panel) {
        panel.add(label(t("settings.laguage", "Language (requires restart):"),
                m("settings.language", 'l')));
        languageCombobox = createLanguageCombobox();
        panel.add(languageCombobox);
    }

    private JComboBox createLanguageCombobox() {
        final LinkedList<Language> languages = new LinkedList<Language>();
        int selectedIndex = 0;
        int i = 0;

        for(final Map.Entry<String, String> translation :
                Translation.getAvailableLanguages().entrySet()) {
            languages.add(new Language(translation.getKey(), translation.getValue()));

            if (translation.getValue().equals(settings.getLanguage())) {
                selectedIndex = i;
            }
            i++;
        }

        final JComboBox combobox = new JComboBox(languages.toArray(new Language[languages.size()]));
        combobox.setSelectedIndex(selectedIndex);
        return combobox;
    }

    private void addLogTabStateComponents(final JPanel panel) {
        panel.add(label(t("settings.tabstate.logtab", "Log Tab:"),
                m("settings.tabstate.logtab", 'g')));
        logTabStateCombobox = createTabStateCombobox(settings.getLogTabState());
        panel.add(logTabStateCombobox);
    }

    private void addKeyTabStateComponents(final JPanel panel) {
        panel.add(label(t("settings.tabstate.keytab", "Keys Tab:"),
                m("settings.tabstate.keytab", 'k')));
        keyTabStateCombobox = createTabStateCombobox(settings.getKeyTabState());
        panel.add(keyTabStateCombobox);
    }

    private JComboBox createTabStateCombobox(final TabState selectedState) {
        final String values[] = new String[3];
        final int selected;

        values[0] = t("settings.states.restore", "restore last state");
        values[1] = t("settings.states.restore", "opened on start");
        values[2] = t("settings.states.restore", "closed on start");

        switch(selectedState) {
            case ALWAYS_CLOSED: selected = 2; break;
            case ALYWAYS_OPEN: selected = 1; break;
            default: selected = 0; break;
        }

        final JComboBox combobox = new JComboBox(values);
        combobox.setSelectedIndex(selected);
        return combobox;
    }

    private JLabel label(final String text) {
        final JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JLabel label(final String text, final int mnemonic) {
        final JLabel label = label(text);
        label.setDisplayedMnemonic(mnemonic);
        return label;
    }

    private JPanel createButtonBar() {
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        final JButton okButton = createOkButton();

        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        buttonPane.add(createCancelButton());

        return buttonPane;
    }

    private JButton createOkButton() {
        final JButton okButton = new JButton(t("ok", "OK"));
        okButton.setMnemonic(m("ok", 'o'));

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                save();
                dispose();
            }
        });

        return okButton;
    }


    private Component createCancelButton() {
        final JButton cancelButton = new JButton(t("cancel", "Cancel"));
        cancelButton.setMnemonic(m("cancel", 'c'));

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispose();
            }
        });

        return cancelButton;
    }


    private void setupWindow(final Window parent) {
        setModal(true);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public void showSettings() {
        setVisible(true);
    }

    private void save() {
        settings.setLanguage(((Language) languageCombobox.getSelectedItem()).key);
        settings.setBellType(BellType.values()[bellTypeCombobox.getSelectedIndex()]);
        settings.setUnlockKeysOnStartup(unlockKeysCheckbox.isSelected());

        settings.setLogTabState(comboBoxToTabState(logTabStateCombobox));
        settings.setKeyTabState(comboBoxToTabState(keyTabStateCombobox));
    }

    private TabState comboBoxToTabState(final JComboBox tabStateCombobox) {
        switch(tabStateCombobox.getSelectedIndex()) {
        case 1: return TabState.ALYWAYS_OPEN;
        case 2: return TabState.ALWAYS_CLOSED;
        default: return TabState.CLOSED;
        }
    }
}

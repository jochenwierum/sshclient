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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.jowisoftware.sshclient.application.settings.ApplicationSettings;
import de.jowisoftware.sshclient.application.settings.BellType;
import de.jowisoftware.sshclient.application.settings.TabState;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.i18n.Translation;

public class SettingsDialog extends JDialog {
    private static final long serialVersionUID = -5205216274422883565L;
    private final ApplicationSettings<AWTProfile> settings;
    private JComboBox<String> logTabStateCombobox;
    private JComboBox<String> keyTabStateCombobox;
    private JComboBox<Language> languageCombobox;
    private JComboBox<String> bellTypeCombobox;

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

    public SettingsDialog(final Window parent, final ApplicationSettings<AWTProfile> settings) {
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
        panel.setLayout(new GridLayout(4, 2, 8, 0));

        addLanguageComponents(panel);
        addLogTabStateComponents(panel);
        addKeyTabStateComponents(panel);
        addBellComponents(panel);

        return panel;
    }

    private void addBellComponents(final JPanel panel) {
        bellTypeCombobox = createBellTypeCombobox();
        panel.add(label("settings.belltype", "Bell type:", 'b', bellTypeCombobox));
        panel.add(bellTypeCombobox);
    }

    private JComboBox<String> createBellTypeCombobox() {
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

        final JComboBox<String> combobox = new JComboBox<>(texts);
        combobox.setSelectedIndex(selectedIndex);
        return combobox;
    }

    private void addLanguageComponents(final JPanel panel) {
        languageCombobox = createLanguageCombobox();
        panel.add(label("settings.laguage", "Language (requires restart):",
                'l', languageCombobox));
        panel.add(languageCombobox);
    }

    private JComboBox<Language> createLanguageCombobox() {
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

        final JComboBox<Language> combobox = new JComboBox<>(
                languages.toArray(new Language[languages.size()]));
        combobox.setSelectedIndex(selectedIndex);
        return combobox;
    }

    private void addLogTabStateComponents(final JPanel panel) {
        logTabStateCombobox = createTabStateCombobox(settings.getLogTabState());
        panel.add(label("settings.tabstate.logtab", "Log Tab:",
                'g', logTabStateCombobox));
        panel.add(logTabStateCombobox);
    }

    private void addKeyTabStateComponents(final JPanel panel) {
        keyTabStateCombobox = createTabStateCombobox(settings.getKeyTabState());
        panel.add(label("settings.tabstate.keytab", "Keys Tab:",
                'k', keyTabStateCombobox));
        panel.add(keyTabStateCombobox);
    }

    private JComboBox<String> createTabStateCombobox(
            final TabState selectedState) {
        final String values[] = new String[3];
        final int selected;

        values[0] = t("settings.states.restore", "restore last state");
        values[1] = t("settings.states.opened", "opened on start");
        values[2] = t("settings.states.closed", "closed on start");

        switch(selectedState) {
            case AlwaysClosed: selected = 2; break;
            case AlwaysOpen: selected = 1; break;
            default: selected = 0; break;
        }

        final JComboBox<String> combobox = new JComboBox<>(values);
        combobox.setSelectedIndex(selected);
        return combobox;
    }

    private JLabel label(final String key, final String text) {
        final JLabel label = new JLabel(t(key, text));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private Component label(final String key, final String text, final char mnemonic,
            final Component targetComponent) {
        final JLabel label = label(key, text);
        label.setDisplayedMnemonic(mnemonic);
        label.setLabelFor(targetComponent);
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

        settings.setLogTabState(comboBoxToTabState(logTabStateCombobox));
        settings.setKeyTabState(comboBoxToTabState(keyTabStateCombobox));
    }

    private TabState comboBoxToTabState(final JComboBox<String> tabStateCombobox) {
        switch(tabStateCombobox.getSelectedIndex()) {
        case 1: return TabState.AlwaysOpen;
        case 2: return TabState.AlwaysClosed;
        default: return TabState.Closed;
        }
    }
}

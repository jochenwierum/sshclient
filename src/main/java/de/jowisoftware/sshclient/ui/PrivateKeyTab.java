package de.jowisoftware.sshclient.ui;

import com.jcraft.jsch.JSchException;
import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.application.settings.KeyManagerEvents;
import de.jowisoftware.sshclient.ui.tabpanel.Tab;
import de.jowisoftware.sshclient.ui.tabpanel.TabPanel;
import de.jowisoftware.sshclient.ui.tabpanel.closable.ClosableTabListener;
import de.jowisoftware.sshclient.ui.tabpanel.closable.ClosableTabTitleComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

public class PrivateKeyTab implements KeyManagerEvents, Tab {
    private static class Content extends JPanel {
        private static final long serialVersionUID = -7617579828457943846L;

        private final DefaultListModel<String> listModel = new DefaultListModel<>();
        private final JList<String> list = new JList<>(listModel);

        private final Application application;

        private Content(final Application application) {
            this.application = application;

            setLayout(new BorderLayout());
            addList();
            addButtons();

            updateListModel();
        }

        private void updateListModel() {
            listModel.clear();

            final int count;
            try {
                @SuppressWarnings("unchecked")
                final Vector<String> identityNames = application.jsch.getIdentityNames();
                count = identityNames.size();

                for (int i = 0; i < count; ++i) {
                    listModel.addElement(identityNames.get(i));
                }
            } catch (final JSchException e) {
                LOGGER.error("Error while fetching identities", e);
            }
        }

        private void addList() {
            final JScrollPane scrollPane = new JScrollPane(list);
            add(scrollPane, BorderLayout.CENTER);
        }

        private void addButtons() {
            final JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(1, 3));

            addAddButton(buttonPanel);
            addSaveButton(buttonPanel);
            addRemoveButton(buttonPanel);

            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void addAddButton(final JPanel buttonPanel) {
            final JButton button = new JButton(t("mainwindow.tabs.keys.add", "Add..."));
            button.setMnemonic(m("mainwindow.tabs.keys.add", 'a'));
            buttonPanel.add(button);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final JFileChooser chooser = new JFileChooser();
                    chooser.setAcceptAllFileFilterUsed(true);
                    final File projectDir = new File(
                            new File(System.getProperty("user.home")), ".ssh");
                    chooser.setCurrentDirectory(projectDir);

                    final int result = chooser.showOpenDialog(getParent());
                    if (result == JFileChooser.APPROVE_OPTION) {
                        final File file = chooser.getSelectedFile();
                        LOGGER.info("Adding private key: {}", file.getAbsolutePath());
                        application.keyManager.loadKey(file.getAbsolutePath());
                    }
                }
            });
        }

        private void addRemoveButton(final JPanel buttonPanel) {
            final JButton button = new JButton(t("mainwindow.tabs.keys.remove", "Remove"));
            button.setMnemonic(m("mainwindow.tabs.keys.remove", 'r'));
            buttonPanel.add(button);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final List<String> toRemove = new LinkedList<>();
                    for (final int index : list.getSelectedIndices()) {
                        toRemove.add(list.getModel().getElementAt(index));
                    }

                    for (final String name : toRemove) {
                        LOGGER.info("Removing private key: {}", name);
                        application.keyManager.removeIdentity(name);
                    }

                    updateListModel();
                }
            });
        }

        private void addSaveButton(final JPanel buttonPanel) {
            final JButton button = new JButton(t("mainwindow.tabs.keys.save", "Save"));
            button.setMnemonic(m("mainwindow.tabs.keys.save", 's'));
            buttonPanel.add(button);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    application.keyManager.persistKeyListToSettings();
                }
            });
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PrivateKeyTab.class);

    private final ClosableTabTitleComponent title;
    private final Content content;

    public PrivateKeyTab(final Application application, final TabPanel parent) {
        this.title = new ClosableTabTitleComponent(this,
                new JLabel(t("mainwindow.tabs.keys", "keys")));
        this.content = new Content(application);
        application.keyManager.eventListeners().register(this);

        title.addListener(new ClosableTabListener() {
            @Override
            public void closeTab(final Tab tab) {
                parent.closeTab(PrivateKeyTab.this);
            }
        });
    }

    @Override
    public void keysUpdated() {
        content.updateListModel();
    }

    @Override
    public JComponent getContent() {
        return content;
    }

    @Override
    public JComponent getTitleContent() {
        return title;
    }

    @Override public void freeze() { }
    @Override public void unfreeze() { }
}

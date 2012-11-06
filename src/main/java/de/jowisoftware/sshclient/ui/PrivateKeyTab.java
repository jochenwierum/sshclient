package de.jowisoftware.sshclient.ui;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSchException;

import de.jowisoftware.sshclient.application.Application;
import de.jowisoftware.sshclient.settings.KeyManagerEvents;

public class PrivateKeyTab extends JPanel implements KeyManagerEvents {
    private static final long serialVersionUID = -5696019301183886041L;

    private static final Logger LOGGER = Logger.getLogger(PrivateKeyTab.class);

    private final Application application;

    private final DefaultListModel listModel = new DefaultListModel();
    private final JList list = new JList(listModel);

    public PrivateKeyTab(final Application application, final MainWindow mainWindow) {
        this.application = application;
        setLayout(new BorderLayout());
        addList();
        addButtons();

        application.keyManager.eventListeners().register(this);
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
                    LOGGER.info("Adding private key: " + file.getAbsolutePath());
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
                final List<String> toRemove = new LinkedList<String>();
                for (final int index : list.getSelectedIndices()) {
                    toRemove.add((String) list.getModel().getElementAt(index));
                }

                for (final String name : toRemove) {
                    LOGGER.info("Removing private key: " + name);
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

    @Override
    public void keysUpdated() {
        updateListModel();
    }
}

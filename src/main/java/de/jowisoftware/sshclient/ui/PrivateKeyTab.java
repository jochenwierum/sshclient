package de.jowisoftware.sshclient.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import de.jowisoftware.sshclient.jsch.KeyAgentManager;

public class PrivateKeyTab extends JPanel {
    private static final long serialVersionUID = -5696019301183886041L;

    private static final Logger LOGGER = Logger.getLogger(PrivateKeyTab.class);

    private final File projectDir = new File(new File(System.getProperty("user.home")), ".ssh");
    private final JSch jsch;
    private JList list;
    private final DefaultListModel listModel = new DefaultListModel();

    public PrivateKeyTab(final JSch jsch) {
        this.jsch = jsch;

        updateListModel();
        setLayout(new BorderLayout());
        addList();
        addButtons();
    }

    private void updateListModel() {
        listModel.clear();

        final int count;
        try {
            count = jsch.getIdentityNames().size();

            for (int i = 0; i < count; ++i) {
                listModel.addElement(jsch.getIdentityNames().get(i));
            }
        } catch (final JSchException e) {
            LOGGER.error("Error while fetching identities", e);
            return;
        }
    }

    private void addList() {
        list = new JList(listModel);
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
        final JButton button = new JButton("Add...");
        button.setMnemonic('a');
        buttonPanel.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final JFileChooser chooser = new JFileChooser();
                chooser.setAcceptAllFileFilterUsed(true);
                chooser.setCurrentDirectory(projectDir);

                final int result = chooser.showOpenDialog(getParent());
                if (result == JFileChooser.APPROVE_OPTION) {
                    final File file = chooser.getSelectedFile();
                    LOGGER.info("Adding private key: " + file.getAbsolutePath());
                    try {
                        jsch.addIdentity(file.getAbsolutePath());
                    } catch(final JSchException e2) {
                        LOGGER.error("Error while adding identity", e2);
                    }
                    updateListModel();
                }
            }
        });
    }

    private void addRemoveButton(final JPanel buttonPanel) {
        final JButton button = new JButton("Remove");
        button.setMnemonic('r');
        buttonPanel.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (list.isSelectionEmpty()) {
                    return;
                }

                final String name = (String) list.getSelectedValue();
                LOGGER.info("Removing private key: " + name);
                try {
                    jsch.removeIdentity(name);
                } catch(final JSchException e2) {
                    LOGGER.error("Error while removing identity", e2);
                }
                updateListModel();
            }
        });
    }

    private void addSaveButton(final JPanel buttonPanel) {
        final JButton button = new JButton("Save");
        button.setMnemonic('s');
        buttonPanel.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                new KeyAgentManager(jsch).persistKeyListToFile(
                        new File(projectDir, "keyagent"));
            }
        });
    }
}

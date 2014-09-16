package de.jowisoftware.sshclient.ui;

import de.jowisoftware.sshclient.application.PasswordManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.SortedMap;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

public class PasswordManagerGUI extends JDialog {
    private class PasswordTableModel extends AbstractTableModel {
        private static final long serialVersionUID = -4809595654738952907L;

        private Map.Entry<String, String>[] ids;
        private boolean showPasswords;

        @Override
        public String getColumnName(final int column) {
            if (column == 0) {
                return t("passwordmanager.key", "Key");
            } else {
                return t("passwordmanager.password", "Password");
            }
        }

        @Override
        public boolean isCellEditable(final int rowIndex, final int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            if (columnIndex == 0) {
                return ids[rowIndex].getKey();
            } else {
                return ids[rowIndex].getValue();
            }
        }

        @Override
        public int getRowCount() {
            return ids.length;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @SuppressWarnings("unchecked")
        public void update() {
            final SortedMap<String, String> passwordMap = passwordManager.getPasswords(showPasswords);
            ids = passwordMap.entrySet().toArray(new Map.Entry[passwordMap.size()]);
            this.fireTableDataChanged();
        }

        private void showPasswords() {
            showPasswords = true;
            update();
        }
    }

    private static final long serialVersionUID = -2481464078956060566L;

    private final PasswordManager passwordManager;
    private final PasswordTableModel model = new PasswordTableModel();
    private final JTable passwordTable = createTable(model);
    private final JButton deleteButton = createDeleteButton();
    private final JButton showPasswordsButton = createShowPasswordsButton();

    public PasswordManagerGUI(final JFrame parent, final PasswordManager passwordManager) {
        super(parent);

        this.passwordManager = passwordManager;
        model.update();

        setTitle(t("passwordmanager.title", "Password Manager"));
        addControls();
        setupWindow(parent);
    }

    private void addControls() {
        setLayout(new BorderLayout());

        add(createPasswordPanel(), BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JComponent createPasswordPanel() {
        return new JScrollPane(passwordTable);
    }

    private JTable createTable(final TableModel model) {
        final JTable table = new JTable(model);
        table.getSelectionModel().addListSelectionListener(createSelectionBehaviour(table));
        return table;
    }

    private ListSelectionListener createSelectionBehaviour(final JTable table) {
        return new ListSelectionListener() {
            private boolean ignore = false;

            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (!ignore) {
                    final int row = table.getSelectedRow();
                    if (row >= 0) {
                        ignore = true;
                        deleteButton.setEnabled(true);
                        table.setRowSelectionInterval(row, row);
                        ignore = false;
                    }
                }
            }
        };
    }

    private JPanel createButtonsPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(createEditPanel());
        panel.add(createCloseButtonPanel());
        return panel;
    }

    private JPanel createCloseButtonPanel() {
        return createRightAlignedPanel(createCloseButton());
    }

    private JButton createCloseButton() {
        final JButton button = new JButton(t("close", "Close"));
        button.setMnemonic(m("close", 'c'));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                PasswordManagerGUI.this.dispose();
            }
        });
        return button;
    }

    private JButton createMasterPasswordButton() {
        final JButton button = new JButton(t("passwordmanager.changemasterpassword",
                "Change master password"));
        button.setMnemonic(m("passwordmanager.changemasterpassword", 'm'));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                passwordManager.changeMasterPassword();
            }
        });
        return button;
    }

    private JPanel createEditPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        panel.add(deleteButton);
        panel.add(showPasswordsButton);
        panel.add(Box.createHorizontalStrut(32));
        panel.add(createMasterPasswordButton());
        return panel;
    }

    private JButton createShowPasswordsButton() {
        final JButton button = new JButton(t("passwordmanager.showpasswords", "Show passwords"));
        button.setMnemonic(m("passwordmanager.showpasswords", 'u'));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                showPasswords();
            }
        });
        return button;
    }

    private void showPasswords() {
        model.showPasswords();
        showPasswordsButton.setEnabled(false);
    }

    private JButton createDeleteButton() {
        final JButton button = new JButton(t("delete", "Delete"));
        button.setMnemonic(m("delete", 'd'));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                deletePassword(e);
            }
        });
        button.setEnabled(false);
        return button;
    }

    private void deletePassword(final ActionEvent e) {
        final int row = passwordTable.getSelectedRow();
        if (row >= 0) {
            final String key = (String) passwordTable.getModel().getValueAt(row, 0);
            passwordManager.deletePassword(key);

            model.update();
            passwordTable.clearSelection();
            ((JButton) e.getSource()).setEnabled(false);
        }
    }

    private JPanel createRightAlignedPanel(final JComponent ... components) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalGlue());
        for (final JComponent component : components) {
            panel.add(component);
        }
        return panel;
    }

    private void setupWindow(final JFrame parent) {
        setSize(640, 240);
        setModal(true);
        setLocationRelativeTo(parent);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void showDialog() {
        setVisible(true);
    }
}

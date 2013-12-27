package de.jowisoftware.sshclient.ui.filetransfer;

import de.jowisoftware.sshclient.filetransfer.AbstractTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.ChildrenProvider;
import de.jowisoftware.sshclient.filetransfer.FileInfo;
import de.jowisoftware.sshclient.util.SwingUtils;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Component;

import static de.jowisoftware.sshclient.i18n.Translation.t;

public class FileTable<S extends AbstractTreeNodeItem<?>, T extends ChildrenProvider<S>> extends JTable implements TreeSelectionListener {
    private final T provider;
    private final LazyUpdater<S,T> updater;

    public FileTable(final T provider, final DirectoryTree<S, T> tree, final LazyUpdater<S, T> updater) {
        this.provider = provider;
        this.updater = updater;
        tree.addTreeSelectionListener(this);

        setModel(createModel());
        getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setRowSelectionAllowed(true);

        setupColumnRendering();

        setAutoResizeMode(AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    }

    private void setupColumnRendering() {
        final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.RIGHT);

        for (int i = 1; i < getColumnCount(); ++i) {
            getColumnModel().getColumn(i).setResizable(false);
            getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        getTableHeader().setReorderingAllowed(false);
    }

    private DefaultTableModel createModel() {
        final DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(final int row, final int column) {
                return false;
            }
        };
        createColumns(model);
        return model;
    }

    private void createColumns(final DefaultTableModel model) {
        model.addColumn(t("file.name", "Name"));
        model.addColumn(t("file.size", "Size"));
        model.addColumn(t("file.permissions", "Permissions"));
        model.addColumn(t("file.owner", "Owner"));
        model.addColumn(t("file.group", "Group"));
        model.addColumn(t("file.modified", "Modified"));
    }

    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        final Object selected = e.getPath().getLastPathComponent();
        @SuppressWarnings("unchecked")
        final S item = (S) ((DefaultMutableTreeNode) selected).getUserObject();
        populate(item);
    }

    private void populate(final S item) {
        final DefaultTableModel model = (DefaultTableModel) getModel();
        updater.queueUpdate(new Runnable() {
            @Override
            public void run() {
                final FileInfo[] files = provider.getFiles(item);
                SwingUtils.runDelayedInSwingThread(new Runnable() {
                    @Override
                    public void run() {
                        model.setRowCount(0);
                        for (final FileInfo info : files) {
                            model.addRow(new Object[] {
                                    info.getName(), info.getSize(),
                                    info.getPermissions(), info.getOwner(), info.getGroup(),
                                    info.getModified()
                            });
                        }
                        resizeColumns();
                    }
                });
            }
        });
    }

    private void resizeColumns() {
        int globalWidth = 0;
        for (int column = 1; column < getColumnCount(); ++column) {
            int width = 0;
            for (int row = 0; row < getRowCount(); row++) {
                final TableCellRenderer renderer = getCellRenderer(row, column);
                final Component comp = prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width, width);
            }

            width += 10;
            globalWidth += width;
            getColumnModel().getColumn(column).setPreferredWidth(width);
        }
        getColumnModel().getColumn(0).setPreferredWidth(getWidth() - globalWidth);
        resizeAndRepaint();
    }
}

package de.jowisoftware.sshclient.ui.filetransfer;

import de.jowisoftware.sshclient.async.AsyncQueueRunner;
import de.jowisoftware.sshclient.filetransfer.AbstractTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.ChildrenProvider;
import de.jowisoftware.sshclient.filetransfer.FileInfo;
import de.jowisoftware.sshclient.ui.filetransfer.dnd.AbstractDragDropHelper;
import de.jowisoftware.sshclient.ui.filetransfer.dnd.SftpTransferHandler;
import de.jowisoftware.sshclient.util.SwingUtils;

import javax.swing.DropMode;
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

public class FileTable<S extends AbstractTreeNodeItem<?>, T extends ChildrenProvider<S>> {
    private final T provider;
    private final AsyncQueueRunner<Runnable> updater;
    private final JTable table;

    public FileTable(final T provider, final DirectoryTree<S, T> tree, final AsyncQueueRunner<Runnable> updater, final AbstractDragDropHelper dragDropHandler) {
        this.provider = provider;
        this.updater = updater;
        tree.addTreeSelectionListener(createTreeSelectionListener());

        table = new JTable(createModel());
        table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setDragEnabled(true);

        table.setDropMode(DropMode.ON);
        table.setTransferHandler(new SftpTransferHandler(dragDropHandler));

        setupColumnRendering();

        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    }

    private TreeSelectionListener createTreeSelectionListener() {
        return new TreeSelectionListener() {
            @Override
            public void valueChanged(final TreeSelectionEvent e) {
                final Object selected = e.getPath().getLastPathComponent();
                @SuppressWarnings("unchecked")
                final S item = (S) ((DefaultMutableTreeNode) selected).getUserObject();
                populate(item);
            }
        };
    }

    private void setupColumnRendering() {
        table.removeColumn(table.getColumnModel().getColumn(0));

        final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.RIGHT);

        for (int i = 1; i < table.getColumnCount(); ++i) {
            table.getColumnModel().getColumn(i).setResizable(false);
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        table.getTableHeader().setReorderingAllowed(false);
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
        model.addColumn("");
        model.addColumn(t("file.name", "Name"));
        model.addColumn(t("file.size", "Size"));
        model.addColumn(t("file.permissions", "Permissions"));
        model.addColumn(t("file.owner", "Owner"));
        model.addColumn(t("file.group", "Group"));
        model.addColumn(t("file.modified", "Modified"));
    }

    private void populate(final S item) {
        final DefaultTableModel model = (DefaultTableModel) table.getModel();
        updater.queue(new Runnable() {
            @Override
            public void run() {
                final FileInfo[] files = provider.getFiles(item);
                SwingUtils.runDelayedInSwingThread(new Runnable() {
                    @Override
                    public void run() {
                        model.setRowCount(0);
                        for (final FileInfo info : files) {
                            model.addRow(new Object[]{
                                    info,
                                    info.getName(), info.getSize(),
                                    info.getPermissions(), info.getOwner(), info.getGroup(),
                                    info.getModified()
                            });
                        }
                        resizeColumns();
                    }
                });
            }

            @Override
            public String toString() {
                return "Update: " + item.toString();
            }
        });
    }

    private void resizeColumns() {
        int globalWidth = 0;
        for (int column = 1; column < table.getColumnCount(); ++column) {
            int width = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                final TableCellRenderer renderer = table.getCellRenderer(row, column);
                final Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width, width);
            }

            width += 10;
            globalWidth += width;
            table.getColumnModel().getColumn(column).setPreferredWidth(width);
        }
        table.getColumnModel().getColumn(0).setPreferredWidth(table.getWidth() - globalWidth);
        table.revalidate();
        table.repaint();
    }

    public JTable getTable() {
        return table;
    }
}

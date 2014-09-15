package de.jowisoftware.sshclient.ui.filetransfer.dnd;

import de.jowisoftware.sshclient.filetransfer.FileInfo;
import de.jowisoftware.sshclient.filetransfer.operations.OperationCommand;
import de.jowisoftware.sshclient.ui.filetransfer.status.OperationQueue;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDragDropHelper {
    private final OperationQueue operationQueue;
    protected JTree tree;

    protected AbstractDragDropHelper(final OperationQueue operationQueue) {
        this.operationQueue = operationQueue;
    }

    public SftpTransferInfo createTransferInfo(final Component component) {
        if (component instanceof JTable) {
            return createFromTable((JTable) component);
        } else {
            throw new IllegalArgumentException("Unsupported control: " + component.getClass().getName());
        }
    }

    protected SftpTransferInfo createFromTable(final JTable jTable) {
        final ListSelectionModel selectionModel = jTable.getSelectionModel();
        final List<FileInfo> selectedFiles = new ArrayList<>();
        for (int i = selectionModel.getMinSelectionIndex(); i <= selectionModel.getMaxSelectionIndex(); ++i) {
            if (selectionModel.isSelectedIndex(i)) {
                selectedFiles.add((FileInfo) jTable.getModel().getValueAt(i, 0));
            }
        }
        return getTransferFiles(selectedFiles);
    }

    protected void queue(final OperationCommand operation) {
        operationQueue.addOperation(operation);
    }

    protected abstract SftpTransferInfo getTransferFiles(final List<FileInfo> selectedFiles);

    public abstract void doTransfer(SftpTransferInfo data, final JComponent component);

    public void setTree(final JTree tree) {
        this.tree = tree;
    }

    protected DefaultMutableTreeNode getTargetTreeNode(final JComponent component) {
        final TreePath selectionPath;
        if (component == tree) {
            selectionPath = (((JTree) component).getDropLocation()).getPath();
        } else {
            selectionPath = tree.getSelectionModel().getSelectionPath();
        }

        return (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
    }
}

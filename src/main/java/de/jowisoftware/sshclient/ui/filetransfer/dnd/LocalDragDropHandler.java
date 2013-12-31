package de.jowisoftware.sshclient.ui.filetransfer.dnd;

import de.jowisoftware.sshclient.filetransfer.FileInfo;
import de.jowisoftware.sshclient.filetransfer.FileSystemTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.operations.DownloadOperationCommand;
import de.jowisoftware.sshclient.ui.filetransfer.status.OperationQueue;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.io.File;
import java.util.List;

public class LocalDragDropHandler extends AbstractDragDropHelper {
    public LocalDragDropHandler(final OperationQueue operationQueue) {
        super(operationQueue);
    }

    @Override
    protected SftpTransferInfo getTransferFiles(final List<FileInfo> selectedFiles) {
        return new SftpTransferInfo(selectedFiles, true);
    }

    @Override
    public void doTransfer(final SftpTransferInfo data) {
        if (data.isLocal()) {
// TODO
        } else {
            final File targetDir = getTargetDir();
            String basePath = null;
            File path = targetDir;
            for (final FileInfo file : data.getFiles()) {
                if (basePath == null && file.isDirectory()) {
                    basePath = file.getFullName();
                }

                if (file.isDirectory()) {
                    assert basePath != null;
                    path = new File(targetDir, file.getFullName().substring(basePath.length()));
                    path.mkdirs();
                } else {
                    queue(new DownloadOperationCommand(file.getFullName(), new File(path, file.getName())));
                }
            }
        }
    }

    public File getTargetDir() {
        final TreePath selectionPath = tree.getSelectionModel().getSelectionPath();
        final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
        return ((FileSystemTreeNodeItem) treeNode.getUserObject()).getFile();
    }
}

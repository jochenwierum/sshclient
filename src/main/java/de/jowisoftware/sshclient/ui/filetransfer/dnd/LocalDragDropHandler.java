package de.jowisoftware.sshclient.ui.filetransfer.dnd;

import de.jowisoftware.sshclient.filetransfer.FileInfo;
import de.jowisoftware.sshclient.filetransfer.FileSystemTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.operations.DownloadOperationCommand;
import de.jowisoftware.sshclient.filetransfer.operations.OperationCommand;
import de.jowisoftware.sshclient.ui.filetransfer.status.OperationQueue;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
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
    public void doTransfer(final SftpTransferInfo data, final JComponent component) {
        final File targetDir = getTargetDir(component);
        if (data.isLocal()) {
// TODO
        } else {
            doRemoteTransfer(data, targetDir);
        }
    }

    private void doRemoteTransfer(final SftpTransferInfo data, final File targetDir) {
        for (final FileInfo file : data.getFiles()) {
            final OperationCommand operation;
            if (file.isDirectory()) {
                /*
                assert basePath != null;
                path = new File(targetDir, file.getFullName().substring(basePath.length()));
                operation = new MakeLocalDirCommand(path);
                */
                throw new UnsupportedOperationException("Not implemented yet");
            } else {
                operation = new DownloadOperationCommand(file.getFullName(), new File(targetDir, file.getName()));
            }
            queue(operation);
        }
    }

    public File getTargetDir(final JComponent component) {
        final DefaultMutableTreeNode treeNode = getTargetTreeNode(component);
        return ((FileSystemTreeNodeItem) treeNode.getUserObject()).getFile();
    }
}

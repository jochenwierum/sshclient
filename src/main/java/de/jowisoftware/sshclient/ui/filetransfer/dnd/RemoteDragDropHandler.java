package de.jowisoftware.sshclient.ui.filetransfer.dnd;

import de.jowisoftware.sshclient.filetransfer.FileInfo;
import de.jowisoftware.sshclient.ui.filetransfer.status.OperationQueue;

import java.util.List;

public class RemoteDragDropHandler extends AbstractDragDropHelper {
    public RemoteDragDropHandler(final OperationQueue operationQueue) {
        super(operationQueue);
    }

    @Override
    protected SftpTransferInfo getTransferFiles(final List<FileInfo> selectedFiles) {
        return new SftpTransferInfo(selectedFiles, false);
    }

    @Override
    public void doTransfer(final SftpTransferInfo data) {

    }
}

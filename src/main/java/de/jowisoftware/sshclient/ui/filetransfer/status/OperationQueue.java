package de.jowisoftware.sshclient.ui.filetransfer.status;

import de.jowisoftware.sshclient.filetransfer.operations.OperationCommand;

public interface OperationQueue {
    void addOperation(OperationCommand command);
}

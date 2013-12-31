package de.jowisoftware.sshclient.ui.filetransfer.dnd;

import de.jowisoftware.sshclient.filetransfer.FileInfo;

import java.util.List;

public class SftpTransferInfo {
    private final FileInfo[] fileInfos;
    private final boolean isLocal;

    public SftpTransferInfo(final List<FileInfo> selectedFiles, final boolean isLocal) {
        this.fileInfos = selectedFiles.toArray(new FileInfo[selectedFiles.size()]);
        this.isLocal = isLocal;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public FileInfo[] getFiles() {
        return fileInfos;
    }
}

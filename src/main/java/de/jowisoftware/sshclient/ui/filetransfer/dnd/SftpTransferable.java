package de.jowisoftware.sshclient.ui.filetransfer.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class SftpTransferable implements Transferable {
    private final SftpTransferInfo data;

    public SftpTransferable(final SftpTransferInfo data) {
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {
                new DataFlavor(SftpTransferInfo.class, "SftpTransferInfo")
        };
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return flavor.getDefaultRepresentationClass().equals(SftpTransferInfo.class);
    }

    @Override
    public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return data;
    }
}

package de.jowisoftware.sshclient.ui.filetransfer.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class SftpTransferable implements Transferable {
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(SftpTransferInfo.class, "SftpTransferInfo");
    private final SftpTransferInfo data;

    public SftpTransferable(final SftpTransferInfo data) {
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DATA_FLAVOR };
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return flavor.equals(DATA_FLAVOR);
    }

    @Override
    public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return data;
    }
}

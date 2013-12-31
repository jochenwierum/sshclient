package de.jowisoftware.sshclient.ui.filetransfer.dnd;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class SftpTransferHandler extends TransferHandler {
    private final AbstractDragDropHelper helper;

    public SftpTransferHandler(final AbstractDragDropHelper helper) {
        this.helper = helper;
    }

    @Override
    protected Transferable createTransferable(final JComponent c) {
        final SftpTransferInfo transferInfo = helper.createTransferInfo(c);
        return new SftpTransferable(transferInfo);
    }

    @Override
    public int getSourceActions(final JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean canImport(final JComponent comp, final DataFlavor[] transferFlavors) {
        return findSupportedFlavor(transferFlavors) != null;
    }

    private DataFlavor findSupportedFlavor(final DataFlavor[] transferFlavors) {
        for (final DataFlavor flavor : transferFlavors) {
            if (flavor.getRepresentationClass().equals(SftpTransferInfo.class)) {
                return flavor;
            }
        }
        return null;
    }

    @Override
    public boolean importData(final JComponent comp, final Transferable t) {
        try {
            final SftpTransferInfo data = (SftpTransferInfo) t.getTransferData(
                    findSupportedFlavor(t.getTransferDataFlavors()));
            helper.doTransfer(data);
            return true;
        } catch (final UnsupportedFlavorException | IOException e) {
            throw new RuntimeException("Could not receive transferable", e);
        }
    }
}

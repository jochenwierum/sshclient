package de.jowisoftware.sshclient.ui.filetransfer.dnd;

import org.apache.log4j.Logger;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class SftpTransferHandler extends TransferHandler {
    private final Logger LOGGER = Logger.getLogger(SftpTransferHandler.class);
    private final AbstractDragDropHelper helper;

    public SftpTransferHandler(final AbstractDragDropHelper helper) {
        this.helper = helper;
    }

    @Override
    public boolean canImport(final JComponent comp, final DataFlavor[] transferFlavors) {
        for (final DataFlavor flavor : transferFlavors) {
            if (flavor.equals(SftpTransferable.DATA_FLAVOR)) {
                return true;
            }
        }
        return false;
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
    public boolean importData(final JComponent comp, final Transferable t) {
        try {
            final SftpTransferInfo data = (SftpTransferInfo) t.getTransferData(SftpTransferable.DATA_FLAVOR);
            helper.doTransfer(data, comp);
            return true;
        } catch (final UnsupportedFlavorException | IOException e) {
            LOGGER.error("Could not receive transferable", e);
            return false;
        }
    }
}

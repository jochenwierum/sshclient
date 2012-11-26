package de.jowisoftware.sshclient.ui.tabpanel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TabTransferable implements Transferable {
    public final static DataFlavor FLAVOUR = new DataFlavor(Tab.class, "Tab");
    private final Tab tab;

    public TabTransferable(final Tab tab) {
        this.tab = tab;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{FLAVOUR};
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return flavor.getRepresentationClass().equals(Tab.class);
    }

    @Override
    public Object getTransferData(final DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        return tab;
    }
}

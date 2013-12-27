package de.jowisoftware.sshclient.ui.filetransfer;

import de.jowisoftware.sshclient.ui.AbstractToolbarCreator;

import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileTransferToolBar extends AbstractToolbarCreator {
    public FileTransferToolBar(final DirectoryTree<?, ?> tree) {
        super("filetransfer");

        toolBar.add(createRefreshButton(tree));
    }

    private JButton createRefreshButton(final DirectoryTree<?, ?> tree) {
        final JButton button = createButton("refresh", "refresh");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                tree.updateSelected();
            }
        });
        return button;
    }
}

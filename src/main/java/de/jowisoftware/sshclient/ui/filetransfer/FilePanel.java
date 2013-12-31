package de.jowisoftware.sshclient.ui.filetransfer;

import de.jowisoftware.sshclient.async.AsyncQueueRunner;
import de.jowisoftware.sshclient.async.StatusListener;
import de.jowisoftware.sshclient.filetransfer.AbstractTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.ChildrenProvider;
import de.jowisoftware.sshclient.ui.filetransfer.dnd.AbstractDragDropHelper;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;

public class FilePanel<S extends AbstractTreeNodeItem<?>, T extends ChildrenProvider<S>> extends JPanel {
    private final AsyncQueueRunner<Runnable> updater;

    public FilePanel(final String name, final T fileSystemChildrenProvider, final AbstractDragDropHelper dragDropHandler, final StatusListener statusListener) {
        setLayout(new BorderLayout());

        updater = new AsyncQueueRunner<>(name + " async sftp updater");
        final DirectoryTree<S, T> tree = createTreeView(fileSystemChildrenProvider);
        dragDropHandler.setTree(tree);
        add(createMainPanel(tree, fileSystemChildrenProvider, dragDropHandler), BorderLayout.CENTER);
        add(new FileTransferToolBar(tree).getToolBar(), BorderLayout.NORTH);
        updater.setListener(statusListener);
    }

    private JSplitPane createMainPanel(final DirectoryTree<S, T> directoryTree,
            final T fileSystemChildrenProvider, final AbstractDragDropHelper dragDropHandler) {
        final JSplitPane mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainPanel.add(wrapScrollable(directoryTree));
        final FileTable<S, T> listing = createFileListing(directoryTree, fileSystemChildrenProvider, dragDropHandler);
        mainPanel.add(wrapScrollable(listing.getTable()));
        mainPanel.setResizeWeight(0.5);
        return mainPanel;
    }

    private JScrollPane wrapScrollable(final JComponent component) {
        return new JScrollPane(component);
    }

    private DirectoryTree<S, T> createTreeView(final T provider) {
        return new DirectoryTree<>(provider, updater);
    }

    private FileTable<S, T> createFileListing(final DirectoryTree<S, T> tree, final T fileSystemChildrenProvider, final AbstractDragDropHelper dragDropHandler) {
        return new FileTable<>(fileSystemChildrenProvider, tree, updater, dragDropHandler);
    }

    public void close() {
        updater.shutdown();
    }
}

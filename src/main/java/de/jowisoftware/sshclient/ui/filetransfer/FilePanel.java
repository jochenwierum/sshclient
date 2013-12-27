package de.jowisoftware.sshclient.ui.filetransfer;

import de.jowisoftware.sshclient.filetransfer.AbstractTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.ChildrenProvider;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;

public class FilePanel<S extends AbstractTreeNodeItem<?>, T extends ChildrenProvider<S>> extends JPanel {
    private final DirectoryTree<S, T> tree;
    private final LazyUpdater<S, T> updater;

    public FilePanel(final T fileSystemChildrenProvider) {
        setLayout(new BorderLayout());

        updater = new LazyUpdater<>();
        tree = createTreeView(fileSystemChildrenProvider);
        add(createMainPanel(tree, fileSystemChildrenProvider), BorderLayout.CENTER);
        add(new FileTransferToolBar(tree).getToolBar(), BorderLayout.NORTH);
    }

    private JSplitPane createMainPanel(final DirectoryTree<S, T> directoryTree,
            final T fileSystemChildrenProvider) {
        final JSplitPane mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainPanel.add(wrapScrollable(directoryTree));
        mainPanel.add(wrapScrollable(createFileListing(directoryTree, fileSystemChildrenProvider)));
        mainPanel.setResizeWeight(0.5);
        return mainPanel;
    }

    private JScrollPane wrapScrollable(final JComponent component) {
        return new JScrollPane(component);
    }

    private DirectoryTree<S, T> createTreeView(final T provider) {
        return new DirectoryTree<>(provider, updater);
    }

    private FileTable<S, T> createFileListing(final DirectoryTree<S, T> tree, final T fileSystemChildrenProvider) {
        return new FileTable<>(fileSystemChildrenProvider, tree, updater);
    }

    public void close() {
        updater.shutdown();
    }
}

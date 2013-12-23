package de.jowisoftware.sshclient.ui.filetransfer;

import de.jowisoftware.sshclient.filetransfer.AbstractTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.ChildrenProvider;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class FilePanel<S extends AbstractTreeNodeItem, T extends ChildrenProvider<S>> extends JSplitPane {
    private final DirectoryTree<S, T> tree;

    public FilePanel(final T fileSystemChildrenProvider) {
        super(VERTICAL_SPLIT);

        tree = createTreeView(fileSystemChildrenProvider);
        add(wrapScrollable(tree));
        add(wrapScrollable(createFileListing(tree, fileSystemChildrenProvider)));

        setResizeWeight(0.5);
    }

    private JScrollPane wrapScrollable(final JComponent component) {
        return new JScrollPane(component);
    }

    private DirectoryTree<S, T> createTreeView(final T provider) {
        return new DirectoryTree<>(provider);
    }

    private FileTable createFileListing(final DirectoryTree<S, T> tree, final T fileSystemChildrenProvider) {
        FileTable fileTable = new FileTable<>(fileSystemChildrenProvider);
        tree.addTreeSelectionListener(fileTable);
        return fileTable;
    }

    public void updateSelected() {
        tree.updateSelected();
    }
}

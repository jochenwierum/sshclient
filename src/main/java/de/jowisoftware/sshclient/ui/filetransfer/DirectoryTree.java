package de.jowisoftware.sshclient.ui.filetransfer;

import de.jowisoftware.sshclient.filetransfer.AbstractTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.ChildrenProvider;
import de.jowisoftware.sshclient.util.SwingUtils;
import org.apache.log4j.Logger;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class DirectoryTree<S extends AbstractTreeNodeItem<?>, T extends ChildrenProvider<S>> extends JTree {
    private static final Logger LOGGER = Logger.getLogger(LazyUpdater.class);

    private final T provider;
    private final LazyUpdater<S, T> updater;

    public DirectoryTree(final T provider, final LazyUpdater<S, T> updater) {
        this.provider = provider;
        this.updater = updater;
        final DefaultTreeModel model = setupModel();
        initRoots(model);
        setupRendering();
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    private DefaultTreeModel setupModel() {
        final DefaultTreeModel model = createModel();
        setModel(model);
        addTreeExpansionListener(createExpansionListener());
        return model;
    }

    private void setupRendering() {
        setRootVisible(false);
        setShowsRootHandles(true);

        final DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(renderer.getClosedIcon());
        setCellRenderer(renderer);
    }

    private TreeExpansionListener createExpansionListener() {
        return new TreeExpansionListener() {
            @Override
            public void treeExpanded(final TreeExpansionEvent event) {
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                for (int i = node.getChildCount(); i > 0; --i) {
                    updateModel((DefaultMutableTreeNode) node.getChildAt(i - 1));
                }
            }

            @Override public void treeCollapsed(final TreeExpansionEvent event) { }
        };
    }

    private DefaultTreeModel createModel() {
        final MutableTreeNode rootNode = new DefaultMutableTreeNode("File Systems", true);
        return new DefaultTreeModel(rootNode);
    }

    private void initRoots(final DefaultTreeModel model) {
        int i = 0;
        for (final S root : provider.getRoots()) {
            final DefaultMutableTreeNode fileSystemNode = new DefaultMutableTreeNode(root);
            model.insertNodeInto(fileSystemNode, (DefaultMutableTreeNode) model.getRoot(), i++);
            updateModel(fileSystemNode);
        }
    }

    private void updateModel(final DefaultMutableTreeNode parent) {
        @SuppressWarnings("unchecked")
        final S treeNodeItem = (S) parent.getUserObject();
        if (!treeNodeItem.isLoaded()) {
            treeNodeItem.markAsLoaded();
            updateChildrenAsync(parent);
        }
    }

    private void updateChildrenAsync(final DefaultMutableTreeNode parent) {
        updater.queueUpdate(new Runnable() {
            @Override
            public void run() {
                @SuppressWarnings("unchecked")
                final S item = (S) parent.getUserObject();
                final S[] children = provider.getChildrenOf(item);
                LOGGER.debug("Found " + children.length + " items in " + item);
                SwingUtils.runDelayedInSwingThread(new Runnable() {
                    @Override
                    public void run() {
                        addTreeItems(children, parent);
                    }
                });
            }
        });
    }

    private void addTreeItems(final S[] children, final DefaultMutableTreeNode parent) {
        int i = 0;
        final int[] indices = new int[children.length];
        LOGGER.debug("Adding items now");
        final DefaultTreeModel model = (DefaultTreeModel) getModel();
        for (final S child : children) {
            final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
            indices[i] = i;
            model.insertNodeInto(childNode, parent, i++);
        }
        LOGGER.debug("Finished, notifying parent....");
        model.nodesWereInserted(parent, indices);

        expandInvisibleRootNodes(parent);
    }

    private void expandInvisibleRootNodes(final DefaultMutableTreeNode node) {
        final DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        if (parent != null && parent.isRoot()) {
            expandPath(new TreePath(parent.getPath()));
        }
    }

    private void removeChildren(final DefaultTreeModel model, final DefaultMutableTreeNode parent) {
        for (int i = model.getChildCount(parent); i > 0; --i) {
            model.removeNodeFromParent((MutableTreeNode) model.getChild(parent, i - 1));
        }
    }

    public void updateSelected() {
        final TreePath selectionPath = getSelectionModel().getSelectionPath();
        if (selectionPath != null) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
            final DefaultTreeModel model = (DefaultTreeModel) getModel();
            removeChildren(model, node);
            updateChildrenAsync(node);
            getSelectionModel().clearSelection();
            getSelectionModel().setSelectionPath(selectionPath);
        }
    }
}

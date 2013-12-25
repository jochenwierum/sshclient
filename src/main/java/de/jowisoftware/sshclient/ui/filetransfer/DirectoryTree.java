package de.jowisoftware.sshclient.ui.filetransfer;

import de.jowisoftware.sshclient.filetransfer.AbstractTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.ChildrenProvider;

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
    private final T provider;
    private final LazySubtreeUpdater<S, T> updater;

    public DirectoryTree(final T provider) {
        this.provider = provider;
        final DefaultTreeModel model = setupModel();
        updater = new LazySubtreeUpdater<>(provider, model, this);
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

            @Override
            public void treeCollapsed(final TreeExpansionEvent event) {

            }
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
            //createChildNodes(model, parent);
            //model.reload(parent);
            updater.queueUpdate(parent);
        }
    }

    private void createChildNodes(final DefaultTreeModel model, final DefaultMutableTreeNode parent) {
        int i = 0;
        @SuppressWarnings("unchecked")
        final S parentObject = (S) parent.getUserObject();
        for (final S child : provider.getChildrenOf(parentObject)) {
            final DefaultMutableTreeNode node = new DefaultMutableTreeNode(child, true);
            model.insertNodeInto(node, parent, i++);
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
            createChildNodes(model, node);
            model.reload(node);
            getSelectionModel().clearSelection();
            getSelectionModel().setSelectionPath(selectionPath);
        }
    }

    public void close() {
        updater.shutdown();
    }
}

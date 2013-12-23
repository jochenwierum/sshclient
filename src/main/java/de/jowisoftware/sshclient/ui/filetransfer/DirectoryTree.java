package de.jowisoftware.sshclient.ui.filetransfer;

import de.jowisoftware.sshclient.filetransfer.AbstractTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.ChildrenProvider;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class DirectoryTree<S extends AbstractTreeNodeItem, T extends ChildrenProvider<S>> extends JTree {
    private final T provider;

    public DirectoryTree(final T provider) {
        this.provider = provider;
        setupModel();
        setupRendering();
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    private void setupModel() {
        final DefaultTreeModel model = createModel();
        setModel(model);
        addTreeWillExpandListener(createExpansionListener(model));
    }

    private void setupRendering() {
        setRootVisible(false);
        setShowsRootHandles(true);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(renderer.getClosedIcon());
        setCellRenderer(renderer);
    }

    private TreeWillExpandListener createExpansionListener(final DefaultTreeModel model) {
        return new TreeWillExpandListener() {

            @Override
            public void treeWillExpand(final TreeExpansionEvent event) throws ExpandVetoException {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                for (int i = node.getChildCount(); i > 0; --i) {
                    updateModel(model, (DefaultMutableTreeNode) node.getChildAt(i - 1));
                }
            }

            @Override
            public void treeWillCollapse(final TreeExpansionEvent event) throws ExpandVetoException {
            }
        };
    }

    private DefaultTreeModel createModel() {
        final MutableTreeNode rootNode = new DefaultMutableTreeNode("File Systems", true);
        final DefaultTreeModel model = new DefaultTreeModel(rootNode);

        int i = 0;
        for (final S root : provider.getRoots()) {
            DefaultMutableTreeNode fileSystemNode = new DefaultMutableTreeNode(
                    root, true);
            model.insertNodeInto(fileSystemNode, rootNode, i++);
            updateModel(model, fileSystemNode);
        }

        return model;
    }

    private void updateModel(DefaultTreeModel model, DefaultMutableTreeNode parent) {
        @SuppressWarnings("unchecked")
        final S treeNodeItem = (S) parent.getUserObject();
        if (!treeNodeItem.isLoaded()) {
            createChildNodes(model, parent);
            treeNodeItem.markAsLoaded();
            model.reload(parent);
        }
    }

    private void createChildNodes(final DefaultTreeModel model, final DefaultMutableTreeNode parent) {
        int i = 0;
        @SuppressWarnings("unchecked")
        final S parentObject = (S) parent.getUserObject();
        for (final S child : provider.getChildrenOf(parentObject)) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(child, true);
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
}

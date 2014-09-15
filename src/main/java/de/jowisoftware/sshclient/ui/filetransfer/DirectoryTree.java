package de.jowisoftware.sshclient.ui.filetransfer;

import de.jowisoftware.sshclient.async.AsyncQueueRunner;
import de.jowisoftware.sshclient.filetransfer.AbstractTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.ChildrenProvider;
import de.jowisoftware.sshclient.ui.filetransfer.dnd.SftpTransferHandler;
import de.jowisoftware.sshclient.util.SwingUtils;
import org.apache.log4j.Logger;

import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;

public class DirectoryTree<S extends AbstractTreeNodeItem<?>, T extends ChildrenProvider<S>> extends JTree implements Autoscroll {
    private static final Logger LOGGER = Logger.getLogger(AsyncQueueRunner.class);
    private static final int AUTOSCROLL_MARGIN = 24;
    private int autoscrollLastRowOver = -1;

    private final T provider;
    private final AsyncQueueRunner<Runnable> updater;

    public DirectoryTree(final T provider, final AsyncQueueRunner<Runnable> updater, final SftpTransferHandler transferHandler) {
        this.provider = provider;
        this.updater = updater;

        final DefaultTreeModel model = setupModel();
        initRoots(model);
        setupRendering();
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        setDropMode(DropMode.ON);
        setTransferHandler(transferHandler);
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
        updater.queue(new Runnable() {
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

            @Override
            public String toString() {
                return "Update " + parent.getUserObject().toString();
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

    @Override
    public Insets getAutoscrollInsets() {
        final Rectangle outer = getBounds();
        final Rectangle inner = getParent().getBounds();
        return new Insets(
                inner.y - outer.y + AUTOSCROLL_MARGIN, inner.x - outer.x + AUTOSCROLL_MARGIN,
                outer.height - inner.height - inner.y + outer.y + AUTOSCROLL_MARGIN,
                outer.width - inner.width - inner.x + outer.x + AUTOSCROLL_MARGIN);
    }

    @Override
    public void autoscroll(final Point p){
        final int currentRow = this.getClosestRowForLocation(p.x, p.y);
        if (autoscrollLastRowOver != -1) {
            if(currentRow > autoscrollLastRowOver) {
                scrollRowToVisible(currentRow + 1);
            } else if(currentRow > 0) {
                scrollRowToVisible(currentRow - 1);
            }
        }

        autoscrollLastRowOver = currentRow;

    }
}

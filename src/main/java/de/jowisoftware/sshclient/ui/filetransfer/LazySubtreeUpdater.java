package de.jowisoftware.sshclient.ui.filetransfer;

import de.jowisoftware.sshclient.filetransfer.AbstractTreeNodeItem;
import de.jowisoftware.sshclient.filetransfer.ChildrenProvider;
import de.jowisoftware.sshclient.util.SwingUtils;
import org.apache.log4j.Logger;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class LazySubtreeUpdater<S extends AbstractTreeNodeItem<?>, T extends ChildrenProvider<S>> extends Thread {
    private static final Logger LOGGER = Logger.getLogger(LazySubtreeUpdater.class);
    private final Object sync = new Object();
    private final JTree tree;
    private volatile boolean isRunning = true;
    private final List<DefaultMutableTreeNode> items = new ArrayList<>();
    private final T provider;
    private final DefaultTreeModel model;

    public LazySubtreeUpdater(final T provider, final DefaultTreeModel model, final JTree tree) {
        this.provider = provider;
        this.model = model;
        this.tree = tree;
        start();
    }

    @Override
    public void run() {
        DefaultMutableTreeNode item;
        do {
            synchronized (sync) {
                while (items.isEmpty() && isRunning) {
                    try {
                        LOGGER.debug("Waiting for work");
                        sync.wait();
                    } catch (final InterruptedException e) {
                        // we handle this as "normal" notify
                    }
                }

                if (!isRunning) {
                    break;
                }

                item = items.remove(0);
            }

            LOGGER.debug("Updating " + item);
            process(item);
        } while(item != null);

        LOGGER.info("Lazy updater thread ended");
    }

    private void process(final DefaultMutableTreeNode parent) {
        @SuppressWarnings("unchecked")
        final S item = (S) parent.getUserObject();
        final S[] children = provider.getChildrenOf(item);
        LOGGER.debug("Found " + children.length + " items in " + item);
        SwingUtils.runDelayedInSwingThread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                final int[] indices = new int[children.length];
                LOGGER.debug("Adding items now");
                for (final S child : children) {
                    final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
                    indices[i] = i;
                    model.insertNodeInto(childNode, parent, i++);
                }
                LOGGER.debug("Finished, notifying parent....");
                model.nodesWereInserted(parent, indices);

                expandInvisibleRootNodes(parent);
            }
        });
    }

    private void expandInvisibleRootNodes(final DefaultMutableTreeNode node) {
        final DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        if (parent.isRoot()) {
            tree.expandPath(new TreePath(parent.getPath()));
        }
    }

    public void queueUpdate(final DefaultMutableTreeNode item) {
        synchronized (sync) {
            LOGGER.debug("Queuing update for " + item);
            items.add(0, item);
            sync.notify();
            LOGGER.debug("Queued!");
        }
    }

    public void shutdown() {
        synchronized (sync) {
            isRunning = false;
            sync.notify();
        }
    }
}

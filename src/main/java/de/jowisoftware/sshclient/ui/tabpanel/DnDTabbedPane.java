package de.jowisoftware.sshclient.ui.tabpanel;

import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

public class DnDTabbedPane extends JTabbedPane {
    private static final long serialVersionUID = 4520682692007233910L;

    private final TabbedPaneWrapper parent;
    private final TabPanel root;

    public DnDTabbedPane(final TabbedPaneWrapper parent, final TabPanel root) {
        this.parent = parent;
        this.root = root;

        setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
        setBorder(BorderFactory.createEmptyBorder());
        setOpaque(false);

        final DragGestureListener dgl = createDragGestureListener();
        new DragSource().createDefaultDragGestureRecognizer(this,
                DnDConstants.ACTION_MOVE, dgl);
    }

    private DragGestureListener createDragGestureListener() {
        return new DragGestureListener() {
            @Override
            public void dragGestureRecognized(final DragGestureEvent dge) {
                final Point dragOrigin = dge.getDragOrigin();
                final int index = indexAtLocation(dragOrigin.x, dragOrigin.y);

                if (index < 0) {
                    return;
                }

                final Tab tab = parent.getTab(index);
                root.getRoot().lock();
                dge.startDrag(DragSource.DefaultMoveDrop, new TabTransferable(tab),
                        createDragSourceListener());
            }
        };
    }

    private DragSourceListener createDragSourceListener() {
        return new DragSourceListener() {

            @Override
            public void dropActionChanged(final DragSourceDragEvent dsde) {
            }

            @Override
            public void dragOver(final DragSourceDragEvent dsde) {
            }

            @Override
            public void dragExit(final DragSourceEvent dse) {
            }

            @Override
            public void dragEnter(final DragSourceDragEvent dsde) {
            }

            @Override
            public void dragDropEnd(final DragSourceDropEvent dsde) {
                root.getRoot().unlock();
            }
        };
    }

    public TabbedPaneWrapper getTabbedPane() {
        return parent;
    }

    public void explode() {
        parent.unfreezeTabs();
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    public void collapse() {
        parent.freezeTabs();
        setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
    }
}
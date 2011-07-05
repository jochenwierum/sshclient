package de.jowisoftware.sshclient.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/*
 * Source:
 * http://java-swing-tips.blogspot.com/2008/04/drag-and-drop-tabs-in-jtabbedpane.html
 */

public class DnDTabbedPane extends JTabbedPane {
    private static final long serialVersionUID = 3149094146571044566L;
    private static final int LINEWIDTH = 3;
    private static final String NAME = "test";
    private final GhostGlassPane glassPane = new GhostGlassPane();
    private final Rectangle lineRect = new Rectangle();
    private final Color lineColor = new Color(0, 100, 255);
    private int dragTabIndex = -1;

    private void clickArrowButton(final String actionKey) {
        final ActionMap map = getActionMap();
        if (map != null) {
            final Action action = map.get(actionKey);
            if (action != null && action.isEnabled()) {
                action.actionPerformed(new ActionEvent(this,
                        ActionEvent.ACTION_PERFORMED, null, 0, 0));
            }
        }
    }

    private static Rectangle rBackward = new Rectangle();
    private static Rectangle rForward = new Rectangle();
    private static int rwh = 20;
    private static int buttonsize = 30;// XXX: magic number of scroll button
                                       // size

    private void autoScrollTest(final Point glassPt) {
        final Rectangle r = getTabAreaBounds();
        final int tabPlacement = getTabPlacement();
        if (tabPlacement == TOP || tabPlacement == BOTTOM) {
            rBackward.setBounds(r.x, r.y, rwh, r.height);
            rForward.setBounds(r.x + r.width - rwh - buttonsize, r.y, rwh
                    + buttonsize, r.height);
        } else if (tabPlacement == LEFT || tabPlacement == RIGHT) {
            rBackward.setBounds(r.x, r.y, r.width, rwh);
            rForward.setBounds(r.x, r.y + r.height - rwh - buttonsize, r.width,
                    rwh + buttonsize);
        }
        rBackward = SwingUtilities.convertRectangle(getParent(), rBackward,
                glassPane);
        rForward = SwingUtilities.convertRectangle(getParent(), rForward,
                glassPane);
        if (rBackward.contains(glassPt)) {
            // System.out.println(new java.util.Date() + "Backward");
            clickArrowButton("scrollTabsBackwardAction");
        } else if (rForward.contains(glassPt)) {
            // System.out.println(new java.util.Date() + "Forward");
            clickArrowButton("scrollTabsForwardAction");
        }
    }

    public DnDTabbedPane() {
        super();
        final DragSourceListener dsl = new DragSourceListener() {
            @Override
            public void dragEnter(final DragSourceDragEvent e) {
                e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
            }

            @Override
            public void dragExit(final DragSourceEvent e) {
                e.getDragSourceContext()
                        .setCursor(DragSource.DefaultMoveNoDrop);
                lineRect.setRect(0, 0, 0, 0);
                glassPane.setPoint(new Point(-1000, -1000));
                glassPane.repaint();
            }

            @Override
            public void dragOver(final DragSourceDragEvent e) {
                final Point glassPt = e.getLocation();
                SwingUtilities.convertPointFromScreen(glassPt, glassPane);
                final int targetIdx = getTargetTabIndex(glassPt);
                // if(getTabAreaBounds().contains(tabPt) && targetIdx>=0 &&
                if (getTabAreaBounds().contains(glassPt) && targetIdx >= 0
                        && targetIdx != dragTabIndex
                        && targetIdx != dragTabIndex + 1) {
                    e.getDragSourceContext().setCursor(
                            DragSource.DefaultMoveDrop);
                    glassPane.setCursor(DragSource.DefaultMoveDrop);
                } else {
                    e.getDragSourceContext().setCursor(
                            DragSource.DefaultMoveNoDrop);
                    glassPane.setCursor(DragSource.DefaultMoveNoDrop);
                }
            }

            @Override
            public void dragDropEnd(final DragSourceDropEvent e) {
                lineRect.setRect(0, 0, 0, 0);
                dragTabIndex = -1;
                glassPane.setVisible(false);
                if (hasGhost()) {
                    glassPane.setVisible(false);
                    glassPane.setImage(null);
                }
            }

            @Override
            public void dropActionChanged(final DragSourceDragEvent e) {
            }
        };
        final Transferable t = new Transferable() {
            private final DataFlavor FLAVOR = new DataFlavor(
                    DataFlavor.javaJVMLocalObjectMimeType, NAME);

            @Override
            public Object getTransferData(final DataFlavor flavor) {
                return DnDTabbedPane.this;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                final DataFlavor[] f = new DataFlavor[1];
                f[0] = this.FLAVOR;
                return f;
            }

            @Override
            public boolean isDataFlavorSupported(final DataFlavor flavor) {
                return flavor.getHumanPresentableName().equals(NAME);
            }
        };
        final DragGestureListener dgl = new DragGestureListener() {
            @Override
            public void dragGestureRecognized(final DragGestureEvent e) {
                if (getTabCount() <= 1) {
                    return;
                }
                final Point tabPt = e.getDragOrigin();
                dragTabIndex = indexAtLocation(tabPt.x, tabPt.y);
                // "disabled tab problem".
                if (dragTabIndex < 0 || !isEnabledAt(dragTabIndex)) {
                    return;
                }
                initGlassPane(e.getComponent(), e.getDragOrigin());
                try {
                    e.startDrag(DragSource.DefaultMoveDrop, t, dsl);
                } catch (final InvalidDnDOperationException idoe) {
                    idoe.printStackTrace();
                }
            }
        };
        new DropTarget(glassPane, DnDConstants.ACTION_COPY_OR_MOVE,
                new CDropTargetListener(), true);
        new DragSource().createDefaultDragGestureRecognizer(this,
                DnDConstants.ACTION_COPY_OR_MOVE, dgl);
    }

    class CDropTargetListener implements DropTargetListener {
        @Override
        public void dragEnter(final DropTargetDragEvent e) {
            if (isDragAcceptable(e)) {
                e.acceptDrag(e.getDropAction());
            } else {
                e.rejectDrag();
            }
        }

        @Override
        public void dragExit(final DropTargetEvent e) {
        }

        @Override
        public void dropActionChanged(final DropTargetDragEvent e) {
        }

        private Point _glassPt = new Point();

        @Override
        public void dragOver(final DropTargetDragEvent e) {
            final Point glassPt = e.getLocation();
            if (getTabPlacement() == SwingConstants.TOP
                    || getTabPlacement() == SwingConstants.BOTTOM) {
                initTargetLeftRightLine(getTargetTabIndex(glassPt));
            } else {
                initTargetTopBottomLine(getTargetTabIndex(glassPt));
            }
            if (hasGhost()) {
                glassPane.setPoint(glassPt);
            }
            if (!_glassPt.equals(glassPt)) {
                glassPane.repaint();
            }
            _glassPt = glassPt;
            autoScrollTest(glassPt);
        }

        @Override
        public void drop(final DropTargetDropEvent e) {
            if (isDropAcceptable(e)) {
                convertTab(dragTabIndex, getTargetTabIndex(e.getLocation()));
                e.dropComplete(true);
            } else {
                e.dropComplete(false);
            }
            repaint();
        }

        private boolean isDragAcceptable(final DropTargetDragEvent e) {
            final Transferable t = e.getTransferable();
            if (t == null) {
                return false;
            }
            final DataFlavor[] f = e.getCurrentDataFlavors();
            if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
                return true;
            }
            return false;
        }

        private boolean isDropAcceptable(final DropTargetDropEvent e) {
            final Transferable t = e.getTransferable();
            if (t == null) {
                return false;
            }
            final DataFlavor[] f = t.getTransferDataFlavors();
            if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
                return true;
            }
            return false;
        }
    }

    private boolean hasGhost = true;

    public void setPaintGhost(final boolean flag) {
        hasGhost = flag;
    }

    public boolean hasGhost() {
        return hasGhost;
    }

    private boolean isPaintScrollArea = true;

    public void setPaintScrollArea(final boolean flag) {
        isPaintScrollArea = flag;
    }

    public boolean isPaintScrollArea() {
        return isPaintScrollArea;
    }

    private int getTargetTabIndex(final Point glassPt) {
        final Point tabPt = SwingUtilities.convertPoint(glassPane, glassPt,
                DnDTabbedPane.this);
        final boolean isTB = getTabPlacement() == SwingConstants.TOP
                || getTabPlacement() == SwingConstants.BOTTOM;
        for (int i = 0; i < getTabCount(); i++) {
            final Rectangle r = getBoundsAt(i);
            if (isTB) {
                r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
            } else {
                r.setRect(r.x, r.y - r.height / 2, r.width, r.height);
            }
            if (r.contains(tabPt)) {
                return i;
            }
        }
        final Rectangle r = getBoundsAt(getTabCount() - 1);
        if (isTB) {
            r.setRect(r.x + r.width / 2, r.y, r.width, r.height);
        } else {
            r.setRect(r.x, r.y + r.height / 2, r.width, r.height);
        }
        return r.contains(tabPt) ? getTabCount() : -1;
    }

    private void convertTab(final int prev, final int next) {
        if (next < 0 || prev == next) {
            return;
        }
        final Component cmp = getComponentAt(prev);
        final Component tab = getTabComponentAt(prev);
        final String str = getTitleAt(prev);
        final Icon icon = getIconAt(prev);
        final String tip = getToolTipTextAt(prev);
        final boolean flg = isEnabledAt(prev);
        final int tgtindex = prev > next ? next : next - 1;
        remove(prev);
        insertTab(str, icon, cmp, tip, tgtindex);
        setEnabledAt(tgtindex, flg);
        // When you drag'n'drop a disabled tab, it finishes enabled and
        // selected.
        // pointed out by dlorde
        if (flg) {
            setSelectedIndex(tgtindex);
        }

        // I have a component in all tabs (jlabel with an X to close the tab)
        // and when i move a tab the component disappear.
        // pointed out by Daniel Dario Morales Salas
        setTabComponentAt(tgtindex, tab);
    }

    private void initTargetLeftRightLine(final int next) {
        if (next < 0 || dragTabIndex == next || next - dragTabIndex == 1) {
            lineRect.setRect(0, 0, 0, 0);
        } else if (next == 0) {
            final Rectangle r = SwingUtilities.convertRectangle(this,
                    getBoundsAt(0), glassPane);
            lineRect.setRect(r.x - LINEWIDTH / 2, r.y, LINEWIDTH, r.height);
        } else {
            final Rectangle r = SwingUtilities.convertRectangle(this,
                    getBoundsAt(next - 1), glassPane);
            lineRect.setRect(r.x + r.width - LINEWIDTH / 2, r.y, LINEWIDTH,
                    r.height);
        }
    }

    private void initTargetTopBottomLine(final int next) {
        if (next < 0 || dragTabIndex == next || next - dragTabIndex == 1) {
            lineRect.setRect(0, 0, 0, 0);
        } else if (next == 0) {
            final Rectangle r = SwingUtilities.convertRectangle(this,
                    getBoundsAt(0), glassPane);
            lineRect.setRect(r.x, r.y - LINEWIDTH / 2, r.width, LINEWIDTH);
        } else {
            final Rectangle r = SwingUtilities.convertRectangle(this,
                    getBoundsAt(next - 1), glassPane);
            lineRect.setRect(r.x, r.y + r.height - LINEWIDTH / 2, r.width,
                    LINEWIDTH);
        }
    }

    private void initGlassPane(final Component c, final Point tabPt) {
        getRootPane().setGlassPane(glassPane);
        if (hasGhost()) {
            final Rectangle rect = getBoundsAt(dragTabIndex);
            BufferedImage image = new BufferedImage(c.getWidth(),
                    c.getHeight(), BufferedImage.TYPE_INT_ARGB);
            final Graphics g = image.getGraphics();
            c.paint(g);
            rect.x = rect.x < 0 ? 0 : rect.x;
            rect.y = rect.y < 0 ? 0 : rect.y;
            image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
            glassPane.setImage(image);
        }
        final Point glassPt = SwingUtilities.convertPoint(c, tabPt, glassPane);
        glassPane.setPoint(glassPt);
        glassPane.setVisible(true);
    }

    private Rectangle getTabAreaBounds() {
        final Rectangle tabbedRect = getBounds();
        // pointed out by daryl. NullPointerException: i.e. addTab("Tab",null)
        // Rectangle compRect = getSelectedComponent().getBounds();
        Component comp = getSelectedComponent();
        int idx = 0;
        while (comp == null && idx < getTabCount()) {
            comp = getComponentAt(idx++);
        }
        final Rectangle compRect = (comp == null) ? new Rectangle() : comp
                .getBounds();
        final int tabPlacement = getTabPlacement();
        if (tabPlacement == TOP) {
            tabbedRect.height = tabbedRect.height - compRect.height;
        } else if (tabPlacement == BOTTOM) {
            tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
            tabbedRect.height = tabbedRect.height - compRect.height;
        } else if (tabPlacement == LEFT) {
            tabbedRect.width = tabbedRect.width - compRect.width;
        } else if (tabPlacement == RIGHT) {
            tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
            tabbedRect.width = tabbedRect.width - compRect.width;
        }
        tabbedRect.grow(2, 2);
        return tabbedRect;
    }

    class GhostGlassPane extends JPanel {
        private static final long serialVersionUID = 551018231278424824L;
        private final AlphaComposite composite;
        private Point location = new Point(0, 0);
        private BufferedImage draggingGhost = null;

        public GhostGlassPane() {
            setOpaque(false);
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    0.5f);
            // http://bugs.sun.com/view_bug.do?bug_id=6700748
            // setCursor(null);
        }

        public void setImage(final BufferedImage draggingGhost) {
            this.draggingGhost = draggingGhost;
        }

        public void setPoint(final Point location) {
            this.location = location;
        }

        @Override
        public void paintComponent(final Graphics g) {
            final Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(composite);
            if (isPaintScrollArea()
                    && getTabLayoutPolicy() == SCROLL_TAB_LAYOUT) {
                g2.setPaint(Color.RED);
                g2.fill(rBackward);
                g2.fill(rForward);
            }
            if (draggingGhost != null) {
                final double xx = location.getX()
                        - (draggingGhost.getWidth(this) / 2d);
                final double yy = location.getY()
                        - (draggingGhost.getHeight(this) / 2d);
                g2.drawImage(draggingGhost, (int) xx, (int) yy, null);
            }
            if (dragTabIndex >= 0) {
                g2.setPaint(lineColor);
                g2.fill(lineRect);
            }
        }
    }
}
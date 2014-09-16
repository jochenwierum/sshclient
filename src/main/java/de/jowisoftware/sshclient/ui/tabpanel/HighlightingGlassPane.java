package de.jowisoftware.sshclient.ui.tabpanel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class HighlightingGlassPane extends JPanel {
    private static final long serialVersionUID = 3113694897611056798L;

    private final RootContainerWrapper root;

    private Rectangle highlightRect;
    private DnDTabbedPane selectedTabPane;
    private int selectedIndex;
    private Rectangle tabRectangle;

    public HighlightingGlassPane(final RootContainerWrapper rootContainer) {
        this.root = rootContainer;

        setOpaque(false);
        setFocusTraversalKeysEnabled(false);

        addKeyListener(createBlockingKeyAdapter());
        new DropTarget(this, DnDConstants.ACTION_MOVE, createDropListener(), true);
    }

    private DropTargetListener createDropListener() {
        return new DropTargetListener() {
            private Point lastPoint;

            @Override
            public void dropActionChanged(final DropTargetDragEvent dtde) {
                dragEnter(dtde);
            }

            @Override
            public void dragExit(final DropTargetEvent dte) {
                highlightRect = null;
                selectTabPane(null);
                repaint();
            }

            @Override
            public void dragEnter(final DropTargetDragEvent dtde) {
                if (isAccaptable(dtde.getCurrentDataFlavors())) {
                    dragOver(dtde);
                } else {
                    setCursor(DragSource.DefaultMoveNoDrop);
                    dtde.rejectDrag();
                }
            }

            @Override
            public void drop(final DropTargetDropEvent dtde) {
                if (isAccaptable(dtde.getCurrentDataFlavors()) && selectedTabPane != null) {
                    final Tab tab;
                    try {
                        tab = (Tab) dtde.getTransferable().getTransferData(TabTransferable.FLAVOUR);
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }

                    final TabbedPaneWrapper owner = root.findTabOwner(tab);

                    if (owner.getComponent() == selectedTabPane) {
                        owner.moveTab(tab, selectedIndex);
                    } else {
                        owner.remove(tab);
                        selectedTabPane.getTabbedPane().add(tab, selectedIndex);
                    }

                    dtde.dropComplete(true);
                } else {
                    dtde.dropComplete(false);
                }

                highlightRect = null;
                selectTabPane(null);
            }

            private boolean isAccaptable(final DataFlavor[] flavours) {
                return flavours.length == 1 &&
                        flavours[0].getRepresentationClass().equals(Tab.class);
            }

            @Override
            public void dragOver(final DropTargetDragEvent dtde) {
                findComponentUnderCursor(dtde.getLocation());

                if (!dtde.getLocation().equals(lastPoint)) {
                    repaint();
                    lastPoint = dtde.getLocation();
                }

                if (selectedTabPane != null) {
                    setCursor(DragSource.DefaultMoveDrop);
                    dtde.acceptDrag(DnDConstants.ACTION_MOVE);
                } else {
                    setCursor(DragSource.DefaultMoveNoDrop);
                    dtde.rejectDrag();
                }
            }
        };
    }


    private void findComponentUnderCursor(final Point point) {
        final JComponent observedComponent = root.getComponent();
        final JRootPane rootPane = observedComponent.getRootPane();

        final Point mousePoint = SwingUtilities.convertPoint(
                rootPane, point, observedComponent);

        Component component = SwingUtilities.getDeepestComponentAt(
                observedComponent, mousePoint.x, mousePoint.y);

        while (component != null && !(component instanceof DnDTabbedPane)) {
            component = component.getParent();
        }

        if (component == null) {
            selectTabPane(null);
            highlightRect = null;
            tabRectangle = null;
        } else {
            final Point componentPointOnRoot = SwingUtilities.convertPoint(
                    component.getParent(), component.getLocation(), rootPane);
            final Point mousePointOnComponent = SwingUtilities.convertPoint(
                    observedComponent, mousePoint, component);
            highlightRect = component.getBounds();
            highlightRect.setLocation(componentPointOnRoot);

            selectTabPane((DnDTabbedPane) component);
            findInsertPosition(mousePointOnComponent, selectedTabPane);
        }
    }

    private void selectTabPane(final DnDTabbedPane component) {
        final boolean selectionChanged = selectedTabPane != component;

        if (selectionChanged && selectedTabPane != null) {
            selectedTabPane.explode();
        }

        selectedTabPane = component;

        if (selectionChanged && component != null) {
            selectedTabPane.collapse();
        }
    }

    private void findInsertPosition(final Point mousePoint, final JTabbedPane tabbedPane) {
        updateSelectedIndex(mousePoint, tabbedPane);

        final Rectangle paneRectangle = getBounds(tabbedPane, tabbedPane.getRootPane());
        final Insets insets = UIManager.getInsets("TabbedPane.tabInsets");

        if (selectedIndex == 0) {
            tabRectangle = getBounds(tabbedPane.getTabComponentAt(0),
                    tabbedPane.getRootPane());
            tabRectangle.x = paneRectangle.x;
        } else {
            final Component c = tabbedPane.getTabComponentAt(selectedIndex - 1);
            tabRectangle = getBounds(c, tabbedPane.getRootPane());

            final boolean isLast = selectedIndex == tabbedPane.getTabCount();
            if (isLast) {
                final boolean isMultiLine = tabbedPane.getTabRunCount() > 1;
                if (isMultiLine) {
                    tabRectangle.x = paneRectangle.x + paneRectangle.width - 2;
                } else {
                    tabRectangle.x += tabRectangle.width + insets.right;
                }
            } else {
                final Rectangle nextRectangle = getBounds(
                        tabbedPane.getTabComponentAt(selectedIndex),
                        tabbedPane.getRootPane());

                final boolean inSameLine = Math.abs(nextRectangle.y - tabRectangle.y) < tabRectangle.width / 3;
                if (inSameLine) {
                    tabRectangle.x += tabRectangle.width + (nextRectangle.x -
                            (tabRectangle.x + tabRectangle.width)) / 2;
                } else {
                    tabRectangle.x = paneRectangle.x + paneRectangle.width - 2;
                }
            }
        }

        tabRectangle.width = 4;
    }

    private void updateSelectedIndex(final Point mousePoint,
            final JTabbedPane tabbedPane) {
        final int position = tabbedPane.indexAtLocation(mousePoint.x, mousePoint.y);

        if (position < 0) {
            selectedIndex = tabbedPane.getTabCount();
        } else {
            final Component component = tabbedPane.getTabComponentAt(position);
            final Rectangle rect = getBounds(component, tabbedPane);
            final boolean left = mousePoint.x <= rect.x + rect.width / 2;

            if (left) {
                selectedIndex = position;
            } else {
                selectedIndex = position + 1;
            }
        }
    }

    private Rectangle getBounds(final Component component, final Component reference) {
        return SwingUtilities.convertRectangle(component.getParent(),
                component.getBounds(), reference);
    }

    private KeyAdapter createBlockingKeyAdapter() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                e.consume();
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                e.consume();
            }
        };
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final JComponent component = root.getComponent();
        final Point topLeft = SwingUtilities.convertPoint(component.getParent(),
                component.getLocation(), component.getRootPane());

        final int componentWidth = component.getWidth();
        final int componentHeight = component.getHeight();

        drawGreyParts(g, topLeft, componentWidth, componentHeight);
        drawTransparentParts(g, topLeft, componentWidth, componentHeight);

        drawHighlight(g);
        drawTabRectangle(g);
    }

    private void drawTabRectangle(final Graphics g) {
        if (tabRectangle != null) {
            g.setColor(new Color(0, 0, 255));
            fillRect(g, tabRectangle);
        }
    }

    private void drawHighlight(final Graphics g) {
        if (highlightRect != null) {
            g.setColor(color(180));
            drawRect(g, highlightRect);
        }
    }

    private void drawTransparentParts(final Graphics g, final Point topLeft,
            final int componentWidth, final int componentHeight) {
        g.setColor(color(240));
        fillRect(g, new Rectangle(topLeft.x, topLeft.y, componentWidth, componentHeight));
    }

    private void drawGreyParts(final Graphics g, final Point topLeft,
            final int componentWidth, final int componentHeight) {
        final int height = getHeight();
        final int width = getWidth();

        final Rectangle leftGrey = new Rectangle(
                0, 0,
                topLeft.x, height);
        final Rectangle rightGrey = new Rectangle(
                topLeft.x + componentWidth, 0,
                width - (topLeft.x + componentWidth), height);
        final Rectangle topGrey = new Rectangle(
                leftGrey.width, 0,
                componentWidth, topLeft.y);
        final Rectangle bottomGrey = new Rectangle(
                leftGrey.width, topLeft.y + componentHeight,
                componentWidth, height - (topLeft.y + componentHeight));

        g.setColor(color(200));
        fillRect(g, leftGrey);
        fillRect(g, rightGrey);
        fillRect(g, topGrey);
        fillRect(g, bottomGrey);
    }

    private void fillRect(final Graphics g, final Rectangle rect) {
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    private void drawRect(final Graphics g, final Rectangle rect) {
        g.drawRect(rect.x, rect.y, rect.width, rect.height);
    }

    private Color color(final int g) {
        return new Color(g, g, g, 160);
    }
}

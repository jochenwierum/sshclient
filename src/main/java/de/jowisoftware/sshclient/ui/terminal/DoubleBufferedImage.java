package de.jowisoftware.sshclient.ui.terminal;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.jowisoftware.sshclient.debug.PerformanceLogger;
import de.jowisoftware.sshclient.debug.PerformanceType;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;

public class DoubleBufferedImage implements Renderer {
    private final AWTGfxInfo gfxInfo;
    private final JPanel parent;

    private BufferedImage[] images;
    private Graphics2D[] graphics;
    private int currentImage = 0;

    private int width;
    private int height;
    private int charWidth = 1;
    private int charHeight = 1;
    private int baseLinePos = 1;

    private boolean queued = false;

    private boolean renderInverted = false;

    private Position currentSelectionStart;
    private Position currentSelectionEnd;

    public DoubleBufferedImage(final AWTGfxInfo gfxInfo, final JPanel parent) {
        this.gfxInfo = gfxInfo;
        this.parent = parent;
    }

    public synchronized void dispose() {
        images = null;
        if (graphics != null) {
            graphics[0].dispose();
            graphics[1].dispose();
        }
        graphics = null;
    }

    public synchronized void setDimensions(final int width, final int height) {
        this.width = width;
        this.height = height;

        dispose();
        images = new BufferedImage[2];
        graphics = new Graphics2D[2];

        for (int i = 0; i < 2; ++i) {
            images[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            graphics[i] = images[i].createGraphics();
            graphics[i].setBackground(gfxInfo.mapColor(ColorName.DEFAULT_BACKGROUND, false));
            graphics[i].setFont(gfxInfo.getFont());
        }

        final FontMetrics fontMetrics = graphics[0].getFontMetrics();
        this.charWidth = fontMetrics.charWidth('m');
        this.charHeight = fontMetrics.getHeight();
        this.baseLinePos = fontMetrics.getAscent() + fontMetrics.getLeading();
    }

    @Override
    public synchronized void renderChars(final GfxChar characters[][],
            final Position cursorPosition) {
        final Position selectionStart = this.currentSelectionStart;
        final Position selectionEnd = this.currentSelectionEnd;
        final int baseRenderFlags = createRenderFlas();

        if (images != null) {
            for (int y = 0; y < characters.length; ++y) {
                for (int x = 0; x < characters[0].length; ++x) {
                    final int posx = x * charWidth;
                    final int posy = y * charHeight;
                    final int renderFlags = baseRenderFlags |
                            updateRenderFlags(
                                    cursorPosition,
                                    new Position(x + 1, y + 1),
                                    selectionStart, selectionEnd);

                    final Rectangle rect = new Rectangle(posx, posy,
                            charWidth, charHeight);
                    ((AWTGfxChar)(characters[y][x])).drawAt(rect, baseLinePos,
                            graphics[1 - currentImage], renderFlags);
                }
            }
            swap();
        }
    }

    public int createRenderFlas() {
        if (renderInverted) {
            return RenderFlag.INVERTED.flag;
        }
        return 0;
    }

    private int updateRenderFlags(
            final Position cursorPosition, final Position renderPosition,
            final Position selectionStart, final Position selectionEnd) {

        int newFlags = 0;
        if (cursorPosition != null && cursorPosition.equals(renderPosition)) {
            newFlags |= RenderFlag.CURSOR.flag;
        }

        if (selectionStart != null && selectionEnd != null &&
                isInSelectionRange(renderPosition, selectionStart,
                        selectionEnd)) {
            newFlags |= RenderFlag.SELECTED.flag;
        }

        return newFlags;
    }

    // TODO: introduce Position.smaller, Position.bigger
    private boolean isInSelectionRange(final Position currentPosition,
            final Position selectionStart, final Position selectionEnd) {
        if (currentPosition.y < selectionStart.y ||
                (currentPosition.y == selectionStart.y &&
                    currentPosition.x < selectionStart.x)) {
            return false;
        } else if(currentPosition.y > selectionEnd.y ||
                (currentPosition.y == selectionEnd.y &&
                    currentPosition.x > selectionEnd.x)) {
            return false;
        } else {
            return true;
        }
    }

    private synchronized void swap() {
        if (images != null) {
            currentImage = 1 - currentImage;
        }
        requestRepaint();
    }

    private void requestRepaint() {
        if (!queued) {
            queued = true;
            PerformanceLogger.start(PerformanceType.REQUEST_TO_RENDER);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    parent.repaint();
                }
            });
        }
    }

    public synchronized Image getImage() {
        if (images != null) {
            queued = false;
            return images[currentImage];
        } else {
            return null;
        }
    }

    @Override
    public synchronized int getLines() {
        return height / charHeight;
    }

    @Override
    public synchronized int getCharsPerLine() {
        return width / charWidth;
    }

    @Override
    public synchronized Position translateMousePosition(final int x, final int y) {
        final int charx = (x + charWidth / 2) / charWidth + 1;
        final int chary = y / charHeight + 1;

        return new Position(charx, chary).moveInRange(
                        new Position(getCharsPerLine() + 1, getLines()).toRange());
    }

    @Override
    public void renderInverted(final boolean inverted) {
        renderInverted = inverted;
    }

    @Override
    public void clearSelection() {
        this.currentSelectionStart = null;
        this.currentSelectionEnd = null;
    }

    @Override
    public void setSelection(final Position pos1, final Position pos2) {
        this.currentSelectionStart = pos1;
        this.currentSelectionEnd = pos2;
    }
}
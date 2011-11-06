package de.jowisoftware.sshclient.ui.terminal;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;

public class DoubleBufferedImage implements Renderer {
    private static final long serialVersionUID = 422553425796222913L;

    private static final Logger LOGGER = Logger
            .getLogger(DoubleBufferedImage.class);

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
    public synchronized void clear() {
        queued = false;
        if (images != null) {
            graphics[1 - currentImage].clearRect(0, 0, width, height);
        }
    }

    @Override
    public synchronized void renderChars(final GfxChar characters[][],
            final Position cursorPosition) {
        final Set<RenderFlag> renderFlags = createRenderFlas();
        final Position selectionStart = this.currentSelectionStart;
        final Position selectionEnd = this.currentSelectionEnd;

        if (images != null) {
            for (int y = 0; y < characters.length; ++y) {
                for (int x = 0; x < characters[0].length; ++x) {
                    final int posx = x * charWidth;
                    final int posy = y * charHeight;
                    updateRenderFlags(renderFlags, cursorPosition,
                            new Position(x + 1, y + 1),
                            selectionStart, selectionEnd);

                    final Rectangle rect = new Rectangle(posx, posy,
                            charWidth, charHeight);
                    ((AWTGfxChar)(characters[y][x])).drawAt(rect, baseLinePos,
                            graphics[1 - currentImage], renderFlags);
                }
            }
        }
    }

    public HashSet<RenderFlag> createRenderFlas() {
        final HashSet<RenderFlag> renderFlags = new HashSet<RenderFlag>();
        if (renderInverted) {
            renderFlags.add(RenderFlag.INVERTED);
        }
        return renderFlags;
    }

    private void updateRenderFlags(final Set<RenderFlag> renderFlags,
            final Position cursorPosition, final Position renderPosition,
            final Position selectionStart, final Position selectionEnd) {

        if (cursorPosition != null && cursorPosition.equals(renderPosition)) {
            renderFlags.add(RenderFlag.CURSOR);
        } else {
            renderFlags.remove(RenderFlag.CURSOR);
        }

        if (selectionStart != null && selectionEnd != null &&
                isInSelectionRange(renderPosition, selectionStart,
                        selectionEnd)) {
            renderFlags.add(RenderFlag.SELECTED);
        } else {
            renderFlags.remove(RenderFlag.SELECTED);
        }
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

    @Override
    public synchronized void swap() {
        if (images != null) {
            currentImage = 1 - currentImage;
        }
        requestRepaint();
    }

    private void requestRepaint() {
        if (!queued) {
            final long startTime = System.currentTimeMillis();
            queued = true;

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    parent.repaint();

                    if (LOGGER.isTraceEnabled()){
                        LOGGER.trace("Repainting took "
                                + (System.currentTimeMillis() - startTime)
                                + " ms until rendered");
                    }
                }
            });
        }
    }

    public synchronized Image getImage() {
        if (images != null) {
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
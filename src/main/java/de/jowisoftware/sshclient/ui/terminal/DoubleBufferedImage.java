package de.jowisoftware.sshclient.ui.terminal;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import de.jowisoftware.sshclient.debug.PerformanceLogger;
import de.jowisoftware.sshclient.debug.PerformanceType;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.util.FontUtils;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

@SuppressWarnings("IS2_INCONSISTENT_SYNC")
public class DoubleBufferedImage implements Renderer {
    private final AWTGfxInfo gfxInfo;
    private final JPanel parent;

    private BufferedImage[] images;
    private Graphics2D[] graphics;
    private int currentImage = 0;
    private final Object imageLock = new Object();

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
        setDimensions(1, 1);
    }

    public synchronized void dispose() {
        synchronized (imageLock) {
            images = null;
            if (graphics != null) {
                graphics[0].dispose();
                graphics[1].dispose();
            }
            graphics = null;
        }
    }

    public synchronized void setDimensions(final int width, final int height) {
        synchronized (imageLock) {
            this.width = width;
            this.height = height;

            dispose();
            images = new BufferedImage[2];
            graphics = new Graphics2D[2];

            for (int i = 0; i < 2; ++i) {
                images[i] = GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice().getDefaultConfiguration()
                        .createCompatibleImage(width, height, Transparency.OPAQUE);
                graphics[i] = images[i].createGraphics();
                graphics[i].setBackground(gfxInfo.mapColor(
                        ColorName.DEFAULT_BACKGROUND, false));
                graphics[i].setFont(gfxInfo.getFont());

                final int aaModeId = gfxInfo.getAntiAliasingMode();
                final Object aaModeValue = FontUtils.getRenderingHintMap().get(aaModeId).value;

                graphics[i].setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        aaModeValue);
            }

            final FontMetrics fontMetrics = graphics[0].getFontMetrics();
            this.charWidth = fontMetrics.charWidth('m');
            this.charHeight = fontMetrics.getHeight();
            this.baseLinePos = fontMetrics.getAscent() + fontMetrics.getLeading();
        }
    }

    @Override
    public synchronized void renderSnapshot(final Snapshot snapshot) {
        final Position selectionStart = this.currentSelectionStart;
        final Position selectionEnd = this.currentSelectionEnd;
        final int baseRenderFlags = createGlobalRenderFlags();

        if (images != null) {
            PerformanceLogger.start(PerformanceType.BACKGROUND_RENDER);
            for (int y = 0; y < snapshot.content.length; ++y) {
                final int yPos = y * charHeight;
                int x;

                for (x = 0; x < snapshot.content[y].length;
                        x += snapshot.content[y][x].getCharCount()) {
                    final int xPos = x * charWidth;
                    final int renderFlags = baseRenderFlags |
                            createCharRenderFlags(
                                    snapshot.cursorPosition,
                                    new Position(x + 1, y + 1),
                                    selectionStart, selectionEnd);

                    final GfxChar gfxChar = snapshot.content[y][x];
                    final Rectangle rect = new Rectangle(xPos, yPos,
                            charWidth * gfxChar.getCharCount(), charHeight);
                    ((AWTGfxChar)gfxChar).drawAt(rect,
                            baseLinePos, graphics[1 - currentImage], renderFlags);
                }
                final AWTGfxChar lastChar = (AWTGfxChar)snapshot.content[y][snapshot.content[y].length - 1];
                fillLine(yPos, x, lastChar.getBackground());
            }
            swap();
            PerformanceLogger.end(PerformanceType.BACKGROUND_RENDER);
        }
    }

    private void fillLine(final int yPos, final int x, final Color background) {
        final int xPos = x * charWidth;
        graphics[1 - currentImage].setColor(background);
        graphics[1 - currentImage].fillRect(xPos, yPos, width - xPos, charHeight);
    }

    private int createGlobalRenderFlags() {
        int flags = 0;
        if (renderInverted) {
            flags |= RenderFlag.INVERTED.flag;
        }
        if (blinkIsForeground()) {
            flags |= RenderFlag.BLINKING.flag;
        }
        return flags;
    }


    private boolean blinkIsForeground() {
        return (System.currentTimeMillis() / 400) % 2 == 0;
    }

    private int createCharRenderFlags(
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

    private boolean isInSelectionRange(final Position currentPosition,
            final Position selectionStart, final Position selectionEnd) {
        final boolean outOfRange = currentPosition.isBefore(selectionStart)
                        || currentPosition.isAfter(selectionEnd);
        return !outOfRange;
    }

    private void swap() {
        synchronized (imageLock) {
            if (images != null) {
                currentImage = 1 - currentImage;
            }
        }
        requestRepaint();
    }

    private void requestRepaint() {
        if (!queued) {
            queued = true;
            PerformanceLogger.start(PerformanceType.REQUEST_TO_RENDER);
            parent.repaint(); // repaint is thread-safe!
        }
    }

    public Image getImage() {
        synchronized (imageLock) {
            if (images == null) {
                return null;
            }

            queued = false;
            return images[currentImage];
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

        return new Position(charx, chary);
    }

    @Override
    public synchronized void renderInverted(final boolean inverted) {
        renderInverted = inverted;
    }

    @Override
    public synchronized void clearSelection() {
        this.currentSelectionStart = null;
        this.currentSelectionEnd = null;
    }

    @Override
    public synchronized void setSelection(final Position pos1, final Position pos2) {
        this.currentSelectionStart = pos1;
        this.currentSelectionEnd = pos2;
    }
}
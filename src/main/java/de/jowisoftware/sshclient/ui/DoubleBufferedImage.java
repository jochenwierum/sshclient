package de.jowisoftware.sshclient.ui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.ColorName;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.RenderFlag;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.ui.terminal.AWTGfxInfo;
import de.jowisoftware.sshclient.ui.terminal.GfxAwtChar;

public class DoubleBufferedImage implements Renderer {
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

    public DoubleBufferedImage(final AWTGfxInfo gfxInfo, final JPanel parent) {
        this.gfxInfo = gfxInfo;
        this.parent = parent;
    }

    public void dispose() {
        images = null;
        graphics = null;
    }

    public synchronized void setDimensions(final int width, final int height) {
        this.width = width;
        this.height = height;

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
    public synchronized void renderChar(final GfxChar character,
            final int x, final int y, final Set<RenderFlag> flags) {
        if (images != null) {
            final int posx = x * charWidth;
            final int posy = y * charHeight;

            final Rectangle rect = new Rectangle(posx, posy,
                    charWidth, charHeight);
            ((GfxAwtChar)character).drawAt(rect, baseLinePos, graphics[1 - currentImage],
                    flags);
        }
    }

    @Override
    public void swap() {
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

    public Image getImage() {
        if (images != null) {
            return images[currentImage];
        } else {
            return null;
        }
    }

    @Override
    public int getLines() {
        return height / charHeight;
    }

    @Override
    public int getCharsPerLine() {
        return width / charWidth;
    }

    @Override
    public Position translateMousePosition(final int x, final int y) {
        final int charx = x / charWidth + 1;
        final int chary = y / charHeight + 1;

        return new Position(charx, chary).moveInRange(
                        new Position(getCharsPerLine(), getLines()).toRange());
    }
}
package de.jowisoftware.sshclient.ui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.jowisoftware.sshclient.terminal.TerminalColor;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.ui.terminal.GfxAwtChar;
import de.jowisoftware.sshclient.ui.terminal.GfxInfo;

public class DoubleBufferedImage implements Renderer<GfxAwtChar> {
    private final GfxInfo gfxInfo;
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

    public DoubleBufferedImage(final GfxInfo gfxInfo, final JPanel parent) {
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
            graphics[i].setBackground(gfxInfo.mapColor(TerminalColor.DEFAULTBG, false));
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
    public synchronized void renderChar(final GfxAwtChar character,
            final int x, final int y, final boolean isCursor) {
        if (images != null) {
            final int posx = x * charWidth;
            final int posy = y * charHeight;

            character.drawBackground(posx, posy, charWidth,
                    charHeight, graphics[1 - currentImage]);
            character.drawAt(posx, posy + baseLinePos,
                    charWidth, graphics[1 - currentImage]);

            if (isCursor) {
                graphics[1 - currentImage].setColor(gfxInfo.getCursorColor());
                graphics[1 - currentImage].drawRect(posx, posy,
                        charWidth - 1, charHeight - 1);
            }
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
            queued = true;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    parent.repaint();
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
}

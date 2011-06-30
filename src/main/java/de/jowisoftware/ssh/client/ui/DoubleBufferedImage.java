package de.jowisoftware.ssh.client.ui;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import de.jowisoftware.ssh.client.terminal.Color;
import de.jowisoftware.ssh.client.terminal.Renderer;

public class DoubleBufferedImage implements Renderer<GfxAwtChar> {
    private final GfxInfo gfxInfo;

    private BufferedImage[] images;
    private Graphics2D[] graphics;
    private int currentImage = 0;

    private int width;
    private int height;
    private int charWidth = 1;
    private int charHeight = 1;
    private int baseLinePos = 1;


    public DoubleBufferedImage(final GfxInfo gfxInfo) {
        this.gfxInfo = gfxInfo;
    }

    public void dispose() {
        images = null;
        graphics = null;
    }

    public void setDimensions(final int width, final int height) {
        this.width = width;
        this.height = height;

        images = new BufferedImage[2];
        graphics = new Graphics2D[2];

        for (int i = 0; i < 2; ++i) {
            images[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            graphics[i] = images[i].createGraphics();
            graphics[i].setBackground(gfxInfo.mapColor(Color.DEFAULTBG, false));
            graphics[i].setFont(gfxInfo.getFont());
        }

        final FontMetrics fontMetrics = graphics[0].getFontMetrics();
        this.charWidth = fontMetrics.charWidth('m');
        this.charHeight = fontMetrics.getHeight();
        this.baseLinePos = fontMetrics.getAscent() + fontMetrics.getLeading();
    }

    @Override
    public void clear() {
        if (images != null) {
            graphics[1 - currentImage].clearRect(0, 0, width, height);
        }
    }

    @Override
    public void renderChar(final GfxAwtChar character, final int x, final int y) {
        if (images != null) {
            final int posy = y * charHeight;
            final int posx = x * charWidth;
            character.drawBackground(posx, posy, charWidth, charHeight, graphics[1 - currentImage]);
            character.drawAt(posx, posy + baseLinePos, charWidth, graphics[1 - currentImage]);
        }
    }

    @Override
    public void swap() {
        if (images != null) {
            currentImage = 1 - currentImage;
        }
    }

    public Image getImage() {
        if (images != null)
            return images[currentImage];
        else return null;
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

package de.jowisoftware.ssh.client.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import de.jowisoftware.ssh.client.ConnectionInfo;
import de.jowisoftware.ssh.client.jsch.AsyncInputStreamReaderThread.Callback;
import de.jowisoftware.ssh.client.tty.ArrayListBuffer;
import de.jowisoftware.ssh.client.tty.Buffer;
import de.jowisoftware.ssh.client.tty.GfxCharSetup;
import de.jowisoftware.ssh.client.tty.GfxCharSetup.Colors;
import de.jowisoftware.ssh.client.tty.controlsequences.CharacterProcessor;
import de.jowisoftware.ssh.client.tty.controlsequences.DisplayAttributeControlSequence;
import de.jowisoftware.ssh.client.tty.controlsequences.EraseControlSequence;
import de.jowisoftware.ssh.client.tty.controlsequences.MovementControlSequence;
import de.jowisoftware.ssh.client.util.StringUtils;

public class SSHConsole extends JPanel implements Callback, ComponentListener, KeyListener, MouseListener {
    private static final long serialVersionUID = 5102110929763645596L;
    private static final Logger LOGGER = Logger.getLogger(SSHConsole.class);

    private final Buffer<GfxAwtChar> buffer = new ArrayListBuffer<GfxAwtChar>();
    private final GfxCharSetup<GfxAwtChar> setup;
    private final CharacterProcessor<GfxAwtChar> processor;

    private BufferedImage[] images;
    private Graphics2D[] graphics;
    private int currentImage = 0;
    boolean forceNewImages = true;

    private FontMetrics fontMetrics;

    private final ConnectionInfo connectionInfo;

    private OutputStream responseStream;

    public SSHConsole(final ConnectionInfo info) {
        this.connectionInfo = info;
        setup = new GfxAwtCharSetup(info.getGfxSettings());
        processor = new CharacterProcessor<GfxAwtChar>(buffer, setup, info.getCharset());
        initializeProcessor();

        this.addComponentListener(this);
        this.addKeyListener(this);
        this.addMouseListener(this);

        setFocusable(true);
        setRequestFocusEnabled(true);
    }

    private void initializeProcessor() {
        processor.addControlSequence(new DisplayAttributeControlSequence<GfxAwtChar>());
        processor.addControlSequence(new MovementControlSequence<GfxAwtChar>());
        processor.addControlSequence(new EraseControlSequence<GfxAwtChar>());
    }

    @Override
    public void paintComponent(final Graphics g) {
        if (images == null || forceNewImages) {
            synchronized (this) {
                if (images == null || forceNewImages) {
                    drawImage();
                }
            }
        }

        g.drawImage(images[currentImage], 0, 0, this);
    }

    private void drawImage() {
        final int width = getWidth();
        final int height = getHeight();
        final int i = (currentImage + 1) % 2;

        if (images == null || forceNewImages) {
            setupImage(width, height);
            forceNewImages = false;
        }

        final Graphics2D graphics = this.graphics[i];
        graphics.clearRect(0, 0, width, height);
        drawText(graphics, width, height);
        currentImage = (currentImage + 1) % 2;;
    }

    private void drawText(final Graphics2D graphics, final int width, final int height) {
        graphics.setColor(Color.white);

        // hasUniformLineMetrics() !!
        final int charWidth = fontMetrics.charWidth('m');
        final int charHeight = fontMetrics.getHeight();
        final int charsPerLine = width / charWidth;
        final int charsPerColumn = height / charHeight;
        final int baseLinePos = fontMetrics.getAscent() + fontMetrics.getLeading();

        final int startRow = Math.max(buffer.rows() - charsPerColumn, 0);
        for (int row = startRow; row < buffer.rows(); ++row) {
            final int endCol = Math.min(charsPerLine, buffer.lengthOfLine(row));

            for (int col = 0; col < endCol; ++col) {
                final int posy = (row - startRow) * charHeight;
                final int posx = col * charWidth;
                final GfxAwtChar character = buffer.getCharacter(col, row);
                if (character != null) {
                    character.drawBackground(posx, posy, charWidth, charHeight, graphics);
                    character.drawAt(posx, posy + baseLinePos, charWidth, graphics);
                }
            }
        }
    }

    private void setupImage(final int width, final int height) {
        images = new BufferedImage[2];
        graphics = new Graphics2D[2];

        for (int i = 0; i < 2; ++i) {
            images[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            graphics[i] = images[i].createGraphics();
            graphics[i].setBackground(connectionInfo.getGfxSettings().mapColor(
                    Colors.DEFAULTBG, false));
            graphics[i].setFont(new Font("Consolas", 0, 11));
        }

        fontMetrics = getFontMetrics(graphics[0].getFont());
    }

    public void dispose() {
        images = null;
        graphics = null;
        fontMetrics = null;
    }

    @Override
    public void gotChars(final byte[] buffer, final int count) {
        processCharacters(buffer, count);
        synchronized(this) {
            drawImage();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }

    // TODO better name
    public void redraw() {
        synchronized(this) {
            drawImage();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }

    public void setOutputStream(final OutputStream outputStream) {
        this.responseStream = outputStream;
    }

    private void processCharacters(final byte[] chars, final int count) {
        for (int i = 0; i < count; ++i) {
            processor.processByte(chars[i]);
        }
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        forceNewImages = true;
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        if (responseStream != null) {
            try {
                LOGGER.trace("Sending: " + StringUtils.escapeCharForLog(e.getKeyChar()));
                responseStream.write(e.getKeyChar());
                responseStream.flush();
            } catch (final IOException e1) {
                LOGGER.warn("Failed to send keypress", e1);
            }
        }

        e.consume();
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            requestFocusInWindow();
        }
    }

    @Override public void mouseReleased(final MouseEvent e) { }
    @Override public void mouseClicked(final MouseEvent e) { }
    @Override public void componentMoved(final ComponentEvent e) { }
    @Override public void componentShown(final ComponentEvent e) { }
    @Override public void componentHidden(final ComponentEvent e) { }
    @Override public void keyTyped(final KeyEvent e) { }
    @Override public void keyReleased(final KeyEvent e) { }
    @Override public void mouseEntered(final MouseEvent e) { }
    @Override public void mouseExited(final MouseEvent e) { }
}

package de.jowisoftware.ssh.client.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.OutputStream;

import javax.swing.JPanel;

import de.jowisoftware.ssh.client.ConnectionInfo;
import de.jowisoftware.ssh.client.jsch.AsyncInputStreamReaderThread.Callback;
import de.jowisoftware.ssh.client.terminal.ArrayBuffer;
import de.jowisoftware.ssh.client.terminal.Buffer;
import de.jowisoftware.ssh.client.terminal.GfxCharSetup;
import de.jowisoftware.ssh.client.terminal.controlsequences.CharacterProcessor;
import de.jowisoftware.ssh.client.terminal.controlsequences.DisplayAttributeControlSequence;
import de.jowisoftware.ssh.client.terminal.controlsequences.EraseControlSequence;
import de.jowisoftware.ssh.client.terminal.controlsequences.KeyboardControlSequence;
import de.jowisoftware.ssh.client.terminal.controlsequences.CursorControlSequence;

public class SSHConsole extends JPanel implements Callback, ComponentListener, MouseListener {
    private static final long serialVersionUID = 5102110929763645596L;

    private final DoubleBufferedImage renderer;
    private final Buffer<GfxAwtChar> buffer;
    private final GfxCharSetup<GfxAwtChar> setup;
    private final CharacterProcessor<GfxAwtChar> outputProcessor;
    private final KeyboardProcessor inputProcessor;

    public SSHConsole(final ConnectionInfo info) {
        setup = new GfxAwtCharSetup(info.getGfxSettings());
        inputProcessor = new KeyboardProcessor();

        renderer = new DoubleBufferedImage(info.getGfxSettings(), this);
        buffer = new ArrayBuffer<GfxAwtChar>(renderer,
                info.getGfxSettings().getEmptyChar(), 80, 24);
        outputProcessor = new CharacterProcessor<GfxAwtChar>(buffer, setup,
                info.getCharset(), new GfxFeedback(), inputProcessor);

        initializeProcessor();

        this.addComponentListener(this);
        this.addMouseListener(this);
        this.addKeyListener(inputProcessor);

        setFocusable(true);
        setRequestFocusEnabled(true);
        setFocusTraversalKeysEnabled(false);
    }

    private void initializeProcessor() {
        outputProcessor.addControlSequence(new DisplayAttributeControlSequence<GfxAwtChar>());
        outputProcessor.addControlSequence(new CursorControlSequence<GfxAwtChar>());
        outputProcessor.addControlSequence(new EraseControlSequence<GfxAwtChar>());
        outputProcessor.addControlSequence(new KeyboardControlSequence<GfxAwtChar>());
    }

    @Override
    public void paintComponent(final Graphics g) {
        final Image image = renderer.getImage();
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }

    public void dispose() {
        renderer.dispose();
    }

    /**
     * Adds new characters to the buffer and triggers a repaint.
     */
    @Override
    public void gotChars(final byte[] chars, final int count) {
        processCharacters(chars, count);
        buffer.render();
    }

    private void processCharacters(final byte[] chars, final int count) {
        for (int i = 0; i < count; ++i) {
            outputProcessor.processByte(chars[i]);
        }
    }

    public void redrawConsole() {
        buffer.render();
    }

    public void setOutputStream(final OutputStream outputStream) {
        inputProcessor.setOutputStream(outputStream);
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        renderer.setDimensions(getWidth(), getHeight());
        buffer.render();
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            requestFocusInWindow();
        }
    }

    @Override public void componentMoved(final ComponentEvent e) { }
    @Override public void componentShown(final ComponentEvent e) { }
    @Override public void componentHidden(final ComponentEvent e) { }
    @Override public void mouseReleased(final MouseEvent e) { }
    @Override public void mouseClicked(final MouseEvent e) { }
    @Override public void mouseEntered(final MouseEvent e) { }
    @Override public void mouseExited(final MouseEvent e) { }
}

package de.jowisoftware.ssh.client.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.OutputStream;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.jowisoftware.ssh.client.ConnectionInfo;
import de.jowisoftware.ssh.client.jsch.AsyncInputStreamReaderThread.Callback;
import de.jowisoftware.ssh.client.terminal.ArrayBuffer;
import de.jowisoftware.ssh.client.terminal.Buffer;
import de.jowisoftware.ssh.client.terminal.GfxCharSetup;
import de.jowisoftware.ssh.client.terminal.controlsequences.CharacterProcessor;
import de.jowisoftware.ssh.client.terminal.controlsequences.DisplayAttributeControlSequence;
import de.jowisoftware.ssh.client.terminal.controlsequences.EraseControlSequence;
import de.jowisoftware.ssh.client.terminal.controlsequences.MovementControlSequence;

public class SSHConsole extends JPanel implements Callback, ComponentListener, MouseListener {
    private static final long serialVersionUID = 5102110929763645596L;

    private final DoubleBufferedImage renderer;
    private final Buffer<GfxAwtChar> buffer;
    private final GfxCharSetup<GfxAwtChar> setup;
    private final CharacterProcessor<GfxAwtChar> outputProcessor;

    private boolean forceNewImages = true;

    public SSHConsole(final ConnectionInfo info) {
        renderer = new DoubleBufferedImage(info.getGfxSettings());
        buffer = new ArrayBuffer<GfxAwtChar>(renderer,
                info.getGfxSettings().getEmptyChar(), 80, 24);
        setup = new GfxAwtCharSetup(info.getGfxSettings());
        outputProcessor = new CharacterProcessor<GfxAwtChar>(buffer, setup,
                info.getCharset(), new GfxFeedback());

        initializeProcessor();

        this.addComponentListener(this);
        this.addMouseListener(this);

        setFocusable(true);
        setRequestFocusEnabled(true);
        setFocusTraversalKeysEnabled(false);
    }

    private void initializeProcessor() {
        outputProcessor.addControlSequence(new DisplayAttributeControlSequence<GfxAwtChar>());
        outputProcessor.addControlSequence(new MovementControlSequence<GfxAwtChar>());
        outputProcessor.addControlSequence(new EraseControlSequence<GfxAwtChar>());
    }

    @Override
    public void paintComponent(final Graphics g) {
        if (forceNewImages) {
            renderConsole(false);
        }

        final Image image = renderer.getImage();
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }

    private synchronized void renderConsole(final boolean force) {
        if (forceNewImages || force) {
            buffer.render();
            forceNewImages = false;
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
        forceNewImages = true;
        queueRedraw();
    }

    private void processCharacters(final byte[] chars, final int count) {
        for (int i = 0; i < count; ++i) {
            outputProcessor.processByte(chars[i]);
        }
    }

    /**
     * Generates a new console image and triggers a repaint.
     * This method is threadsafe.
     */
    public void redrawConsole() {
        renderConsole(true);
        queueRedraw();
    }

    /**
     * Triggers a redraw in the form's owner thread.
     */
    private void queueRedraw() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }

    public void setOutputStream(final OutputStream outputStream) {
        for (final KeyListener keyListener : this.getKeyListeners()) {
            this.removeKeyListener(keyListener);
        }

        this.addKeyListener(new KeyboardProcessor(outputStream));
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        renderer.setDimensions(getWidth(), getHeight());
        forceNewImages = true;
        queueRedraw();
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

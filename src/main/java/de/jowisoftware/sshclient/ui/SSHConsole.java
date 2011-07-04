package de.jowisoftware.sshclient.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.OutputStream;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelShell;

import de.jowisoftware.sshclient.ConnectionInfo;
import de.jowisoftware.sshclient.jsch.AsyncInputStreamReaderThread.Callback;
import de.jowisoftware.sshclient.terminal.ArrayBuffer;
import de.jowisoftware.sshclient.terminal.Buffer;
import de.jowisoftware.sshclient.terminal.CharacterProcessor;
import de.jowisoftware.sshclient.terminal.CursorPosition;
import de.jowisoftware.sshclient.terminal.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.KeyboardFeedback;
import de.jowisoftware.sshclient.terminal.SessionInfo;
import de.jowisoftware.sshclient.terminal.VisualFeedback;
import de.jowisoftware.sshclient.terminal.controlsequences.CursorControlSequence;
import de.jowisoftware.sshclient.terminal.controlsequences.KeyboardControlSequence;
import de.jowisoftware.sshclient.terminal.controlsequences.OperatingSystemCommandSequence;

public class SSHConsole extends JPanel implements Callback, ComponentListener,
        MouseListener, SessionInfo<GfxAwtChar> {
    private static final long serialVersionUID = 5102110929763645596L;
    private static final Logger LOGGER = Logger.getLogger(SSHConsole.class);

    private final DoubleBufferedImage renderer;
    private final Buffer<GfxAwtChar> buffer;
    private final GfxCharSetup<GfxAwtChar> setup;
    private final CharacterProcessor<GfxAwtChar> outputProcessor;
    private final KeyboardProcessor keyboardProcessor;
    private final VisualFeedback visualFeedback;

    private ChannelShell channel;

    public SSHConsole(final ConnectionInfo info) {
        setup = new GfxAwtCharSetup(info.getGfxSettings());
        visualFeedback = new GfxFeedback();
        keyboardProcessor = new KeyboardProcessor();

        renderer = new DoubleBufferedImage(info.getGfxSettings(), this);
        buffer = new ArrayBuffer<GfxAwtChar>(renderer,
                info.getGfxSettings().getEmptyChar(), 80, 24);
        outputProcessor = new CharacterProcessor<GfxAwtChar>(this, info.getCharset());

        initializeProcessor();

        this.addComponentListener(this);
        this.addMouseListener(this);
        this.addKeyListener(keyboardProcessor);

        setFocusable(true);
        setRequestFocusEnabled(true);
        setFocusTraversalKeysEnabled(false);
    }

    private void initializeProcessor() {
        outputProcessor.addControlSequence(new CursorControlSequence<GfxAwtChar>());
        outputProcessor.addControlSequence(new KeyboardControlSequence<GfxAwtChar>());
        outputProcessor.addControlSequence(new OperatingSystemCommandSequence<GfxAwtChar>());
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
        keyboardProcessor.setOutputStream(outputStream);
    }

    public void setChannel(final ChannelShell channel) {
        this.channel = channel;
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        final int pw = getWidth();
        final int ph = getHeight();
        renderer.setDimensions(pw, ph);
        final int cw = renderer.getCharsPerLine();
        final int ch = renderer.getLines();
        LOGGER.debug("Reporting new window size: " + cw + "/" + ch + " "
                + pw + "/" + ph);
        buffer.newSize(cw, ch);
        buffer.setAbsoluteCursorPosition(new CursorPosition(1, 1));
        buffer.render();

        if (channel != null) {
            channel.setPtySize(cw, ch, pw, ph);
            try {
                channel.sendSignal("SIGWINCH");
            } catch(final Exception e2) {
                LOGGER.error("Could not send SIGWINCH", e2);
            }
        }
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

    @Override
    public Buffer<GfxAwtChar> getBuffer() {
        return buffer;
    }

    @Override
    public KeyboardFeedback getKeyboardFeedback() {
        return keyboardProcessor;
    }

    @Override
    public VisualFeedback getVisualFeedback() {
        return visualFeedback;
    }

    @Override
    public GfxCharSetup<GfxAwtChar> getCharSetup() {
        return setup;
    }
}

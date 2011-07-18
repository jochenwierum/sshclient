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

import de.jowisoftware.sshclient.jsch.AsyncInputStreamReaderThread.Callback;
import de.jowisoftware.sshclient.settings.ConnectionInfo;
import de.jowisoftware.sshclient.terminal.CharacterProcessor;
import de.jowisoftware.sshclient.terminal.DefaultSession;
import de.jowisoftware.sshclient.terminal.DisplayType;
import de.jowisoftware.sshclient.terminal.VisualFeedback;
import de.jowisoftware.sshclient.terminal.buffer.DefaultBuffer;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.controlsequences.CursorControlSequence;
import de.jowisoftware.sshclient.terminal.controlsequences.DebugControlSequence;
import de.jowisoftware.sshclient.terminal.controlsequences.KeyboardControlSequence;
import de.jowisoftware.sshclient.terminal.controlsequences.OperatingSystemCommandSequence;

public class SSHConsole extends JPanel implements Callback, ComponentListener,
        MouseListener {
    private static final long serialVersionUID = 5102110929763645596L;
    private static final Logger LOGGER = Logger.getLogger(SSHConsole.class);

    private final DefaultSession<GfxAwtChar> session;
    private final DoubleBufferedImage renderer;
    private final CharacterProcessor<GfxAwtChar> outputProcessor;
    private ChannelShell channel;
    private DisplayType displayType = DisplayType.DYNAMIC;

    public SSHConsole(final ConnectionInfo info) {
        final GfxAwtCharSetup charSetup = new GfxAwtCharSetup(info.getGfxSettings());
        final VisualFeedback visualFeedback = new GfxFeedback(this);
        final KeyboardProcessor keyboardProcessor = new KeyboardProcessor();
        final Buffer<GfxAwtChar> buffer = new DefaultBuffer<GfxAwtChar>(
                info.getGfxSettings().getEmptyChar(), 80, 24);
        session = new DefaultSession<GfxAwtChar>(buffer,
                keyboardProcessor, visualFeedback, charSetup);
        keyboardProcessor.setSession(session);

        renderer = new DoubleBufferedImage(info.getGfxSettings(), this);
        outputProcessor = new CharacterProcessor<GfxAwtChar>(session, info.getCharset());

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
        outputProcessor.addControlSequence(new DebugControlSequence<GfxAwtChar>());
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
        session.getBuffer().render(renderer);
    }

    private void processCharacters(final byte[] chars, final int count) {
        for (int i = 0; i < count; ++i) {
            try {
                outputProcessor.processByte(chars[i]);
            } catch(final RuntimeException e) {
                LOGGER.error("Error while processing byte " + (chars[i] & 0xff), e);
            }
        }
    }

    public void redrawConsole() {
        session.getBuffer().render(renderer);
    }

    public void setOutputStream(final OutputStream outputStream) {
        session.setOutputStream(outputStream);
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

        if (displayType == DisplayType.DYNAMIC) {
            session.getBuffer().newSize(cw, ch);
        }
        session.getBuffer().render(renderer);

        if (channel != null) {
            LOGGER.debug("Reporting new window size: " + cw + "/" + ch + " "
                    + pw + "/" + ph);
            channel.setPtySize(cw, ch, pw, ph);
            try {
                channel.sendSignal("WINCH");
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

    public void setDisplayType(final DisplayType displayType) {
        this.displayType = displayType;
        LOGGER.info("Setting new terminal display type: " + displayType);
        switch(displayType) {
        case DYNAMIC:
            componentResized(null); break;
        case FIXED132X24:
            session.getBuffer().newSize(132, 24); break;
        case FIXED80X24:
            session.getBuffer().newSize(80, 24); break;
        }
        session.getBuffer().render(renderer);
    }

    @Override public void componentMoved(final ComponentEvent e) { /* ignored */ }
    @Override public void componentShown(final ComponentEvent e) { /* ignored */ }
    @Override public void componentHidden(final ComponentEvent e) { /* ignored */ }
    @Override public void mouseReleased(final MouseEvent e) { /* ignored */ }
    @Override public void mouseClicked(final MouseEvent e) { /* ignored */ }
    @Override public void mouseEntered(final MouseEvent e) { /* ignored */ }
    @Override public void mouseExited(final MouseEvent e) { /* ignored */ }
}

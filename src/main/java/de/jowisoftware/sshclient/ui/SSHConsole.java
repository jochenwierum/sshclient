package de.jowisoftware.sshclient.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.OutputStream;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelShell;

import de.jowisoftware.sshclient.jsch.AsyncInputStreamReaderThread.Callback;
import de.jowisoftware.sshclient.settings.AWTProfile;
import de.jowisoftware.sshclient.terminal.CharacterProcessor;
import de.jowisoftware.sshclient.terminal.DefaultSession;
import de.jowisoftware.sshclient.terminal.DisplayType;
import de.jowisoftware.sshclient.terminal.VisualEvent;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.DefaultBuffer;
import de.jowisoftware.sshclient.terminal.controlsequences.CharsetControlSequence;
import de.jowisoftware.sshclient.terminal.controlsequences.CursorControlSequence;
import de.jowisoftware.sshclient.terminal.controlsequences.DebugControlSequence;
import de.jowisoftware.sshclient.terminal.controlsequences.DefaultSequenceRepository;
import de.jowisoftware.sshclient.terminal.controlsequences.KeyboardControlSequence;
import de.jowisoftware.sshclient.terminal.controlsequences.OperatingSystemCommandSequence;
import de.jowisoftware.sshclient.ui.terminal.GfxAwtCharSetup;
import de.jowisoftware.sshclient.ui.terminal.GfxFeedback;

public class SSHConsole extends JPanel implements Callback, ComponentListener,
        MouseListener {
    private static final long serialVersionUID = 5102110929763645596L;
    private static final Logger LOGGER = Logger.getLogger(SSHConsole.class);

    private final DefaultSession session;
    private final DoubleBufferedImage renderer;
    private final CharacterProcessor outputProcessor;
    private ChannelShell channel;
    private DisplayType displayType = DisplayType.DYNAMIC;

    public SSHConsole(final AWTProfile profile) {
        final GfxAwtCharSetup charSetup = new GfxAwtCharSetup(profile.getGfxSettings());
        final VisualEvent gfxFeedback = new GfxFeedback(this);
        final KeyboardProcessor keyboardProcessor = new KeyboardProcessor();
        final Buffer buffer = new DefaultBuffer(
                charSetup.createClearChar(), 80, 24);
        session = new DefaultSession(buffer, charSetup);
        session.getKeyboardFeedback().register(keyboardProcessor);
        session.getVisualFeedback().register(gfxFeedback);
        keyboardProcessor.setSession(session);

        renderer = new DoubleBufferedImage(profile.getGfxSettings(), this);
        outputProcessor = initializeProcessor(profile);

        this.addComponentListener(this);
        this.addMouseListener(this);
        this.addKeyListener(keyboardProcessor);

        setFocusable(true);
        setRequestFocusEnabled(true);
        setFocusTraversalKeysEnabled(false);
    }

    public DefaultSession getSession() {
        return session;
    }

    private CharacterProcessor initializeProcessor(final AWTProfile profile) {
        final DefaultSequenceRepository repository = new DefaultSequenceRepository();
        repository.addControlSequence(new CursorControlSequence());
        repository.addControlSequence(new KeyboardControlSequence());
        repository.addControlSequence(new OperatingSystemCommandSequence());
        repository.addControlSequence(new DebugControlSequence());
        repository.addControlSequence(new CharsetControlSequence());
        return new CharacterProcessor(session, profile.getCharset(), repository);
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

        final int cw;
        final int ch;
        renderer.setDimensions(pw, ph);
        if (displayType.equals(DisplayType.DYNAMIC)) {
            cw = renderer.getCharsPerLine();
            ch = renderer.getLines();
            session.getBuffer().newSize(cw, ch);
        } else {
            ch = 24;
            if(displayType.equals(DisplayType.FIXED132X24)) {
                cw = 132;
            } else {
                cw = 80;
            }
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

            System.out.println(e.getX() + "/" + e.getY());
            System.out.println(renderer.translateMousePosition(e.getX(), e.getY()));
        }
    }

    public void setDisplayType(final DisplayType displayType) {
        this.displayType = displayType;
        LOGGER.info("Setting new terminal display type: " + displayType);
        switch(displayType) {
        case DYNAMIC:
             break;
        case FIXED132X24:
            session.getBuffer().newSize(132, 24); break;
        case FIXED80X24:
            session.getBuffer().newSize(80, 24); break;
        }
        componentResized(null);
        session.getBuffer().render(renderer);
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public void takeFocus() {
        this.requestFocusInWindow();
    }

    public void processKey(final KeyEvent e) {
        ((KeyListener) session.getKeyboardFeedback()).keyPressed(e);
    }

    @Override public void componentMoved(final ComponentEvent e) { /* ignored */ }
    @Override public void componentShown(final ComponentEvent e) { /* ignored */ }
    @Override public void componentHidden(final ComponentEvent e) { /* ignored */ }
    @Override public void mouseReleased(final MouseEvent e) { /* ignored */ }
    @Override public void mouseClicked(final MouseEvent e) { /* ignored */ }
    @Override public void mouseEntered(final MouseEvent e) { /* ignored */ }
    @Override public void mouseExited(final MouseEvent e) { /* ignored */ }
}

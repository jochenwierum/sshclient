package de.jowisoftware.sshclient.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.OutputStream;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelShell;

import de.jowisoftware.sshclient.debug.PerformanceLogger;
import de.jowisoftware.sshclient.debug.PerformanceType;
import de.jowisoftware.sshclient.jsch.InputStreamEvent;
import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.SimpleSSHSession;
import de.jowisoftware.sshclient.terminal.buffer.ArrayListBackedTabStopManager;
import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.SynchronizedBuffer;
import de.jowisoftware.sshclient.terminal.buffer.WordBoundaryLocator;
import de.jowisoftware.sshclient.terminal.events.DisplayType;
import de.jowisoftware.sshclient.terminal.input.ByteProcessor;
import de.jowisoftware.sshclient.terminal.input.CharacterProcessor;
import de.jowisoftware.sshclient.terminal.input.CharsetByteProcessor;
import de.jowisoftware.sshclient.terminal.input.SequenceSupportingCharacterProcessor;
import de.jowisoftware.sshclient.terminal.input.controlsequences.DefaultSequenceRepository;
import de.jowisoftware.sshclient.terminal.mouse.DefaultMouseCursorManager;
import de.jowisoftware.sshclient.terminal.mouse.MouseCursorManager;
import de.jowisoftware.sshclient.ui.terminal.AWTGfxCharSetup;
import de.jowisoftware.sshclient.ui.terminal.AWTProfile;
import de.jowisoftware.sshclient.ui.terminal.DoubleBufferedImage;

public class SSHConsole extends JPanel implements InputStreamEvent, ComponentListener {
    private static final long serialVersionUID = 5102110929763645596L;
    private static final Logger LOGGER = Logger.getLogger(SSHConsole.class);

    private final SimpleSSHSession session;
    private final DoubleBufferedImage renderer;
    private final ByteProcessor outputProcessor;
    private final MouseCursorManager mouseCursorManager;
    private final AWTClipboard clipboard;

    private final SSHConsoleHistory history;
    private final JPanel image;
    private final SSHConsoleKeyListener keyListener;

    private ChannelShell channel;
    private DisplayType displayType = DisplayType.DYNAMIC;

    public SSHConsole(final AWTProfile profile) {
        renderer = new DoubleBufferedImage(profile.getGfxSettings(), this);

        final AWTGfxCharSetup charSetup = new AWTGfxCharSetup(profile.getGfxSettings());
        final KeyboardProcessor keyboardProcessor = new KeyboardProcessor();
        final ArrayListBackedTabStopManager tabstopManager = new ArrayListBackedTabStopManager(80);
        final Buffer buffer = SynchronizedBuffer.createBuffer(
                charSetup.createClearChar(), 80, 24, 1000, tabstopManager);

        session = new SimpleSSHSession(profile.getDefaultTitle(),
                buffer, renderer, charSetup, tabstopManager, profile.getCharset());
        session.getKeyboardFeedback().register(keyboardProcessor);
        session.getVisualFeedback().register(new GfxFeedback(this, renderer));

        history = new SSHConsoleHistory(session);
        clipboard = new AWTClipboard(session);
        mouseCursorManager = createCursorManager(profile, buffer);

        outputProcessor = initializeInputProcessor(profile);
        keyListener = new SSHConsoleKeyListener(session, history);

        keyboardProcessor.setSession(session);
        image = createImagePane(keyboardProcessor, keyListener);

        setLayout(new BorderLayout());
        add(image, BorderLayout.CENTER);
        add(history.getScrollBar(), BorderLayout.EAST);

        session.startRenderer();
    }

    private JPanel createImagePane(final KeyboardProcessor keyboardProcessor,
            final SSHConsoleKeyListener keyListener2) {
        return new JPanel() {
            private static final long serialVersionUID = -2670879662532285317L;

            {
                this.addComponentListener(SSHConsole.this);
                final SSHConsoleMouseListener mouseListener = new SSHConsoleMouseListener(
                        this, mouseCursorManager, session, clipboard);
                this.addMouseListener(mouseListener);
                this.addMouseMotionListener(mouseListener);
                this.addKeyListener(keyListener2);
                this.addMouseWheelListener(history);

                setFocusable(true);
                setRequestFocusEnabled(true);
                setFocusTraversalKeysEnabled(false);
                setCursor(new Cursor(Cursor.TEXT_CURSOR));
            }

            @Override
            public void paintComponent(final Graphics g) {
                final Image image = renderer.getImage();
                if (image != null) {
                    g.drawImage(image, 0, 0, this);
                    PerformanceLogger.end(PerformanceType.REQUEST_TO_RENDER);
                    PerformanceLogger.end(PerformanceType.REVEICE_CHAR_TO_RENDER);
                    PerformanceLogger.end(PerformanceType.SELECT_TO_RENDER);
                }
            }

        };
    }

    private DefaultMouseCursorManager createCursorManager(final AWTProfile profile,
            final Buffer buffer) {
        final WordBoundaryLocator boundaryLocator = createWordBoundaryLocator(profile, buffer);
        return new DefaultMouseCursorManager(buffer, renderer,
                clipboard, boundaryLocator);
    }

    private WordBoundaryLocator createWordBoundaryLocator(final AWTProfile profile, final Buffer buffer) {
        final WordBoundaryLocator wordBoundaryLocator = new WordBoundaryLocator(buffer);
        wordBoundaryLocator.setSelectionChars(profile.getGfxSettings().getBoundaryChars());
        return wordBoundaryLocator;
    }

    public SSHSession getSession() {
        return session;
    }

    private ByteProcessor initializeInputProcessor(final AWTProfile profile) {
        final DefaultSequenceRepository repository = new DefaultSequenceRepository();
        final CharacterProcessor processor = new SequenceSupportingCharacterProcessor(session, repository);
        return new CharsetByteProcessor(processor, profile.getCharset());
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
        history.updateHistorySize(session.getBuffer().getHistorySize());
        session.setRenderOffset(0);
        session.render();
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
        session.render();
    }

    public void setOutputStream(final OutputStream outputStream) {
        session.setOutputStream(outputStream);
    }

    public void setChannel(final ChannelShell channel) {
        this.channel = channel;
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        session.pauseRendering();
        final int pw = image.getWidth();
        final int ph = image.getHeight();

        final int cw;
        final int ch;
        renderer.setDimensions(pw, ph);
        if (displayType.equals(DisplayType.DYNAMIC)) {
            cw = renderer.getCharsPerLine();
            ch = renderer.getLines();
            session.getBuffer().newSize(cw, ch);
            session.getTabStopManager().newWidth(cw);
        } else {
            ch = 24;
            if(displayType.equals(DisplayType.FIXED132X24)) {
                cw = 132;
            } else {
                cw = 80;
            }
        }
        history.updateWindowSize(ch);

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
        session.resumeRendering();
    }

    public void updateSelection(final MouseEvent e) {
        final Position charPosition =
                session.translateMousePositionToCharacterPosition(e.getX(), e.getY());
        mouseCursorManager.updateSelectionEnd(charPosition);
        session.render();
    }

    public void setDisplayType(final DisplayType displayType) {
        LOGGER.info("Setting new terminal display type: " + displayType);
        this.displayType = displayType;

        switch(displayType) {
        case DYNAMIC:
             break;
        case FIXED132X24:
            session.getBuffer().newSize(132, 24);
            session.getTabStopManager().newWidth(132);
            break;
        case FIXED80X24:
            session.getBuffer().newSize(80, 24);
            session.getTabStopManager().newWidth(80);
            break;
        }

        componentResized(null);
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public void takeFocus() {
        image.requestFocusInWindow();
    }

    public void keyPressed(final KeyEvent e) {
        keyListener.keyPressed(e);
    }

    @Override public void componentMoved(final ComponentEvent e) { /* ignored */ }
    @Override public void componentShown(final ComponentEvent e) { /* ignored */ }
    @Override public void componentHidden(final ComponentEvent e) { /* ignored */ }
    @Override public void streamClosed(final int exitCode) { /* ignored */ }
}

package de.jowisoftware.sshclient.ui;

import java.awt.Label;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;

// TODO: the whole test class and keyboard processor is a mess
// try to reduce complexity here, get rid of AWT and make the tests more expressive
public class KeyboardProcessorTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private final static char ESC = (char) 27;

    private KeyboardProcessor processor;
    private Renderer renderer;
    private SSHSession session;

    @Before
    public void setUp() {
        session = context.mock(SSHSession.class);
        renderer = context.mock(Renderer.class);
        processor = new KeyboardProcessor();
        processor.setSession(session);

        context.checking(new Expectations() {{
            allowing(session).getRenderer(); will(returnValue(renderer));
        }});
    }

    private void assertText(final String t, final KeyEvent... events) {
        allowRenderingInteraction(t.length());
        expectStringInChars(t);
        pressKeys(events);
    }

    private void allowRenderingInteraction(final int count) {
        context.checking(new Expectations() {{
            allowing(renderer);
        }});
    }

    private void expectStringInChars(final String t) {
        for (final char c : t.toCharArray()) {
            expectString(Character.toString(c));
        }
    }

    private void expectString(final String s) {
        context.checking(new Expectations() {{
            oneOf(session).sendToServer(s);
        }});
    }

    private void expectChars(final char[] chars) {
        final byte[] bytes = new byte[chars.length];
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte) chars[i];
        }

        context.checking(new Expectations() {{
            oneOf(session).rawSendToServer(bytes);
        }});
    }

    private void assertSequence(final KeyEvent e, final char... chars) {
        allowRenderingInteraction(3);
        expectString("x");
        expectChars(chars);
        expectString("x");

        final KeyEvent x = new KeyEvent(new Label(), 0, 0, 0, KeyEvent.VK_X,
                'x');
        pressKeys(x, e, x);
    }

    private void assertSequence(final KeyEvent e, final String text) {
        allowRenderingInteraction(3);
        expectString("x");
        expectString(text);
        expectString("x");

        final KeyEvent x = new KeyEvent(new Label(), 0, 0, 0, KeyEvent.VK_X,
                'x');
        pressKeys(x, e, x);
    }

    private void pressKeys(final KeyEvent... events) {
        for (final KeyEvent e : events) {
            processor.keyPressed(e);
        }
    }

    private KeyEvent makeEvent(final int code, final char character) {
        int mod = 0;
        if (Character.isUpperCase(character)) {
            mod = InputEvent.SHIFT_DOWN_MASK;
        }
        return new KeyEvent(new Label(), 0, 0, mod, code, character);
    }

    private KeyEvent makeEvent(final int code) {
        return makeEvent(code, 0, 0, ' ');
    }

    private KeyEvent makeEvent(final int code, final int location) {
        return makeEvent(code, location, 0, ' ');
    }

    private KeyEvent makeEvent(final int code, final int location, final int modifier, final char text) {
        // FIXME: new Label() is a problem with headless testing!
        //noinspection MagicConstant
        return new KeyEvent(new Label(), 0, 0, modifier, code, text, location);
    }

    @Test
    public void testSimpleText() {
        assertText("Test ", makeEvent(KeyEvent.VK_T, 'T'),
                makeEvent(KeyEvent.VK_E, 'e'), makeEvent(KeyEvent.VK_S, 's'),
                makeEvent(KeyEvent.VK_T, 't'), makeEvent(KeyEvent.VK_SPACE, ' '));
    }

    @Test
    public void testSpecialKeys() {
        assertSequence(makeEvent(KeyEvent.VK_ENTER), '\n');
        assertSequence(makeEvent(KeyEvent.VK_BACK_SPACE), (char) 8);
        assertSequence(makeEvent(KeyEvent.VK_ESCAPE), ESC);
        assertSequence(makeEvent(KeyEvent.VK_DELETE), (char) 127);
        assertSequence(makeEvent(KeyEvent.VK_HOME), ESC, '[', '1', '~');
        assertSequence(makeEvent(KeyEvent.VK_END), ESC, '[', '4', '~');
        assertSequence(makeEvent(KeyEvent.VK_INSERT), ESC, '[', '2', '~');
        assertSequence(makeEvent(KeyEvent.VK_PAGE_UP), ESC, '[', '5', '~');
        assertSequence(makeEvent(KeyEvent.VK_PAGE_DOWN), ESC, '[', '6', '~');
        assertSequence(makeEvent(KeyEvent.VK_F1), ESC, 'O', 'P');
        assertSequence(makeEvent(KeyEvent.VK_F2), ESC, 'O', 'Q');
        assertSequence(makeEvent(KeyEvent.VK_F3), ESC, 'O', 'R');
        assertSequence(makeEvent(KeyEvent.VK_F4), ESC, 'O', 'S');
        assertSequence(makeEvent(KeyEvent.VK_F5), ESC, '[', '1', '5', '~');
        assertSequence(makeEvent(KeyEvent.VK_F6), ESC, '[', '1', '7', '~');
        assertSequence(makeEvent(KeyEvent.VK_F7), ESC, '[', '1', '8', '~');
        assertSequence(makeEvent(KeyEvent.VK_F8), ESC, '[', '1', '9', '~');
        assertSequence(makeEvent(KeyEvent.VK_F9), ESC, '[', '2', '0', '~');
        assertSequence(makeEvent(KeyEvent.VK_F10), ESC, '[', '2', '1', '~');
        assertSequence(makeEvent(KeyEvent.VK_F11), ESC, '[', '2', '3', '~');
        assertSequence(makeEvent(KeyEvent.VK_F12), ESC, '[', '2', '4', '~');
        assertSequence(makeEvent(KeyEvent.VK_TAB), (char) 9);
    }

    @Test
    public void testCursorKeys() {
        assertSequence(makeEvent(KeyEvent.VK_UP), ESC, '[', 'A');
        assertSequence(makeEvent(KeyEvent.VK_DOWN), ESC, '[', 'B');
        assertSequence(makeEvent(KeyEvent.VK_RIGHT), ESC, '[', 'C');
        assertSequence(makeEvent(KeyEvent.VK_LEFT), ESC, '[', 'D');

        processor.newCursorKeysIsAppMode(true);
        assertSequence(makeEvent(KeyEvent.VK_UP), ESC, 'O', 'A');
        assertSequence(makeEvent(KeyEvent.VK_DOWN), ESC, 'O', 'B');
        assertSequence(makeEvent(KeyEvent.VK_RIGHT), ESC, 'O', 'C');
        assertSequence(makeEvent(KeyEvent.VK_LEFT), ESC, 'O', 'D');

        processor.newCursorKeysIsAppMode(false);
        assertSequence(makeEvent(KeyEvent.VK_UP), ESC, '[', 'A');
    }

    // TODO: check ENTER
    @Test
    public void testNumPad() {
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD0), '0');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD1), '1');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD2), '2');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD3), '3');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD4), '4');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD5), '5');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD6), '6');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD7), '7');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD8), '8');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD9), '9');
        assertSequence(makeEvent(KeyEvent.VK_DIVIDE, KeyEvent.KEY_LOCATION_NUMPAD), '/');
        assertSequence(makeEvent(KeyEvent.VK_MULTIPLY, KeyEvent.KEY_LOCATION_NUMPAD), '*');
        assertSequence(makeEvent(KeyEvent.VK_PLUS, KeyEvent.KEY_LOCATION_NUMPAD), '+');
        assertSequence(makeEvent(KeyEvent.VK_MINUS, KeyEvent.KEY_LOCATION_NUMPAD), '-');
        assertSequence(makeEvent(KeyEvent.VK_COMMA, KeyEvent.KEY_LOCATION_NUMPAD), ',');

        processor.newNumblockAppMode(true);
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD0), ESC, 'O', 'p');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD1), ESC, 'O', 'q');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD2), ESC, 'O', 'r');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD3), ESC, 'O', 's');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD4), ESC, 'O', 't');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD5), ESC, 'O', 'u');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD6), ESC, 'O', 'v');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD7), ESC, 'O', 'w');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD8), ESC, 'O', 'x');
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD9), ESC, 'O', 'y');
        assertSequence(makeEvent(KeyEvent.VK_PLUS, KeyEvent.KEY_LOCATION_NUMPAD), ESC, 'O', 'M');
        assertSequence(makeEvent(KeyEvent.VK_MINUS, KeyEvent.KEY_LOCATION_NUMPAD), ESC, 'O', 'm');
        assertSequence(makeEvent(KeyEvent.VK_MULTIPLY, KeyEvent.KEY_LOCATION_NUMPAD), ESC, 'O', 'l');
        assertSequence(makeEvent(KeyEvent.VK_DIVIDE, KeyEvent.KEY_LOCATION_NUMPAD), '/');
        assertSequence(makeEvent(KeyEvent.VK_COMMA, KeyEvent.KEY_LOCATION_NUMPAD), ESC, 'O', 'n');

        processor.newNumblockAppMode(false);
        assertSequence(makeEvent(KeyEvent.VK_NUMPAD0), '0');
    }

    @Test
    public void errorAreIgnoredHandling() {
        processor.setSession(null);
        pressKeys(makeEvent(KeyEvent.VK_UP));
    }

    @Test
    public void testUnderscore() {
        assertSequence(makeEvent(KeyEvent.VK_MINUS, KeyEvent.KEY_LOCATION_NUMPAD), '-');
        assertSequence(makeEvent(KeyEvent.VK_MINUS, KeyEvent.KEY_LOCATION_STANDARD, 0, '-'), "-");
        assertSequence(makeEvent(KeyEvent.VK_MINUS, KeyEvent.KEY_LOCATION_STANDARD, InputEvent.SHIFT_DOWN_MASK, '_'), "_");
    }
}

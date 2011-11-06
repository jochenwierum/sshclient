package de.jowisoftware.sshclient.terminal.mouse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.util.StringUtils;

@RunWith(JMock.class)
public class DefaultMouseCursorManagerTest {
    private final Mockery context = new JUnit4Mockery();
    private Buffer buffer;
    private Renderer renderer;
    private ClipboardManager clipboard;
    private DefaultMouseCursorManager manager;

    @Before
    public void setUp() {
        buffer = context.mock(Buffer.class);
        renderer = context.mock(Renderer.class);
        clipboard = context.mock(ClipboardManager.class);
        manager = new DefaultMouseCursorManager(buffer, renderer, clipboard);
    }

    private void expectCopy(final String string) {
        context.checking(new Expectations() {{
            oneOf(clipboard).copyPlaintext(string);
        }});
    }

    private void allowRenderer() {
        context.checking(new Expectations() {{
            allowing(renderer);
        }});
    }

    private void allowCharRequests(final int line, final int from, final int to,
            final String string) {
        final char[] chars = string.toCharArray();
        final GfxChar character = context.mock(GfxChar.class, "char-" + line +
                "-" + from + "-" + to);

        context.checking(new Expectations() {{
            for (int i = from; i <= to; ++i) {
                oneOf(buffer).getCharacter(line, i);
                will(returnValue(character));
                oneOf(character).getChar(); will(returnValue(chars[i - from]));
            }
        }});

    }

    private void allowSize(final int width, final int height) {
        context.checking(new Expectations() {{
            allowing(buffer).getSize();
            will(returnValue(new Position(width, height)));
        }});
    }

    @Test
    public void selectionIsCopiedToClipboard() {
        allowRenderer();
        manager.startSelection(new Position(4, 10));
        manager.updateSelectionEnd(new Position(8, 12));

        allowSize(80, 24);
        allowCharRequests(10, 4, 80,
                " a test" + StringUtils.repeat(" ", 70));
        allowCharRequests(11, 1, 80,
                "line two: some Text!" + StringUtils.repeat(" ", 60));
        allowCharRequests(12, 1, 8,
                "last one");
        expectCopy(" a test\nline two: some Text!\nlast one");

        manager.copySelection();
    }

    @Test
    public void invertedSelectionIsCopiedToClipboard() {
        allowRenderer();
        manager.startSelection(new Position(9, 5));
        manager.updateSelectionEnd(new Position(2, 4));

        allowSize(40, 24);
        allowCharRequests(4, 2, 40,
                "output" + StringUtils.repeat(" ", 33));
        allowCharRequests(5, 1, 9,
                "1234     ");
        expectCopy("output\n1234");

        manager.copySelection();
    }

    @Test
    public void selectionInSinleLineIsCopiedToClipboard() {
        allowRenderer();
        manager.startSelection(new Position(12, 2));
        manager.updateSelectionEnd(new Position(7, 2));

        allowCharRequests(2, 7, 12, "works!");
        expectCopy("works!");

        manager.copySelection();
    }

    @Test
    public void settingSelectionIsForwardedToRenderer() {
        context.checking(new Expectations() {{
            oneOf(renderer).setSelection(new Position(2, 5),
                    new Position(5, 5));

            oneOf(renderer).setSelection(new Position(7, 1),
                    new Position(2, 5));

            oneOf(renderer).setSelection(new Position(5, 2),
                    new Position(7, 2));
        }});

        manager.startSelection(new Position(2, 5));
        manager.updateSelectionEnd(new Position(5, 5));
        manager.updateSelectionEnd(new Position(7, 1));

        manager.startSelection(new Position(7, 2));
        manager.updateSelectionEnd(new Position(5, 2));
    }
}


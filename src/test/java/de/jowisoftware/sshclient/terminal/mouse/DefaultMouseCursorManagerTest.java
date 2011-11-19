package de.jowisoftware.sshclient.terminal.mouse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.terminal.buffer.BoundaryLocator;
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
    private BoundaryLocator boundaries;

    @Before
    public void setUp() {
        buffer = context.mock(Buffer.class);
        renderer = context.mock(Renderer.class);
        clipboard = context.mock(ClipboardManager.class);
        boundaries = context.mock(BoundaryLocator.class);
        manager = new DefaultMouseCursorManager(buffer, renderer, clipboard,
                boundaries);
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
                exactly(2).of(buffer).getCharacter(line, i);
                will(returnValue(character));
                oneOf(character).getCharAsString(); will(
                        returnValue(Character.toString(chars[i - from])));
                oneOf(character).getCharCount(); will(
                        returnValue(1));
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
        manager.startSelection(new Position(4, 10), 1);
        manager.updateSelectionEnd(new Position(10, 12));

        allowSize(80, 24);
        allowCharRequests(10, 4, 80,
                " a test" + StringUtils.repeat(" ", 70));
        allowCharRequests(11, 1, 80,
                "line two: some Text!" + StringUtils.repeat(" ", 60));
        allowCharRequests(12, 1, 9,
                "last one ");
        expectCopy(" a test\nline two: some Text!\nlast one");

        manager.copySelection();
    }

    @Test
    public void invertedSelectionIsCopiedToClipboard() {
        allowRenderer();
        manager.startSelection(new Position(10, 5), 1);
        manager.updateSelectionEnd(new Position(2, 4));

        allowSize(40, 24);
        allowCharRequests(4, 2, 40,
                "output" + StringUtils.repeat(" ", 33));
        allowCharRequests(5, 1, 9,
                "123456789");
        expectCopy("output\n123456789");

        manager.copySelection();
    }

    @Test
    public void selectionInSingleLineIsCopiedToClipboard() {
        allowRenderer();
        manager.startSelection(new Position(13, 2), 1);
        manager.updateSelectionEnd(new Position(7, 2));

        allowCharRequests(2, 7, 12, "works!");
        expectCopy("works!");

        manager.copySelection();
    }

    @Test
    public void settingSelectionIsForwardedToRenderer() {
        context.checking(new Expectations() {{
            oneOf(renderer).setSelection(new Position(2, 5),
                    new Position(4, 5));

            oneOf(renderer).setSelection(new Position(7, 1),
                    new Position(1, 5));

            oneOf(renderer).setSelection(new Position(5, 2),
                    new Position(6, 2));
        }});

        manager.startSelection(new Position(2, 5), 1);
        manager.updateSelectionEnd(new Position(5, 5));
        manager.updateSelectionEnd(new Position(7, 1));

        manager.startSelection(new Position(7, 2), 1);
        manager.updateSelectionEnd(new Position(5, 2));
    }

    @Test
    public void selectingNothingIsNotForwarded() {
        context.checking(new Expectations() {{
            oneOf(renderer).clearSelection();
        }});

        manager.startSelection(new Position(1, 2), 1);
        manager.updateSelectionEnd(new Position(1, 2));
    }

    @Test
    public void doubleSelectingTheSameEndDoesNothing() {
        context.checking(new Expectations() {{
            oneOf(renderer).setSelection(new Position(2, 5),
                    new Position(4, 5));
        }});

        manager.startSelection(new Position(2, 5), 1);
        manager.updateSelectionEnd(new Position(5, 5));
        manager.updateSelectionEnd(new Position(5, 5));
    }

    @Test
    public void bug25CopyWithoutSelectionDoesNothing() {
        manager.copySelection();

        context.checking(new Expectations() {{
            allowing(renderer).clearSelection();
        }});
        manager.startSelection(new Position(7, 5), 1);
        manager.updateSelectionEnd(new Position(7, 5));
        manager.copySelection();
    }

    @Test
    public void trippleClickOnSamePositionSelectsLine() {
        allowCharRequests(5, 1, 80, "It works!" + StringUtils.repeat(" ", 71));
        expectCopy("It works!");

        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(renderer).setSelection(new Position(1, 5),
                    new Position(80, 5));
        }});

        manager.startSelection(new Position(5, 5), 3);
        manager.updateSelectionEnd(new Position(5, 5));
        manager.copySelection();
    }

    @Test
    public void trippleClickWithNegativeOffsetSelectsLines() {
        allowCharRequests(7, 1, 80, "Some text" + StringUtils.repeat(" ", 71));
        allowCharRequests(8, 1, 80, "Copy me :-)" + StringUtils.repeat(" ", 70));
        expectCopy("Some text\nCopy me :-)");

        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(renderer).setSelection(new Position(1, 7),
                    new Position(80, 8));
        }});

        manager.startSelection(new Position(9, 8), 3);
        manager.updateSelectionEnd(new Position(15, 7));
        manager.copySelection();
    }

    @Test
    public void trippleClickWithPositiveOffsetSelectsLines() {
        allowCharRequests(7, 1, 80, "More text" + StringUtils.repeat(" ", 71));
        allowCharRequests(8, 1, 80, "Boring text" + StringUtils.repeat(" ", 69));
        expectCopy("More text\nBoring text");

        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(renderer).setSelection(new Position(1, 7),
                    new Position(80, 8));
        }});

        manager.startSelection(new Position(9, 7), 3);
        manager.updateSelectionEnd(new Position(15, 8));
        manager.copySelection();
    }

    @Test
    public void doubleClickSelectsWord() {
        allowCharRequests(7, 9, 12, "some");
        expectCopy("some");

        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(renderer).setSelection(new Position(9, 7),
                    new Position(12, 7));
            allowing(boundaries).findStartOfWord(new Position(9, 7));
                will(returnValue(new Position(9, 7)));
            allowing(boundaries).findEndOfWord(new Position(9, 7));
                will(returnValue(new Position(12, 7)));
        }});

        manager.startSelection(new Position(9, 7), 2);
        manager.updateSelectionEnd(new Position(9, 7));
        manager.copySelection();
    }

    @Test
    public void doubleClickSelectsMultipleWords() {
        allowCharRequests(7, 9, 80, "these words" + StringUtils.repeat(" ", 61));
        allowCharRequests(8, 1, 12, "are selected" + StringUtils.repeat(" ", 68));
        expectCopy("these words\nare selected");

        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(renderer).setSelection(new Position(9, 7),
                    new Position(12, 8));
            allowing(boundaries).findStartOfWord(new Position(9, 7));
                will(returnValue(new Position(9, 7)));
            allowing(boundaries).findEndOfWord(new Position(10, 8));
                will(returnValue(new Position(12, 8)));
        }});

        manager.startSelection(new Position(9, 7), 2);
        manager.updateSelectionEnd(new Position(10, 8));
        manager.copySelection();
    }

    @Test
    public void doubleClickSelectsMultipleWordsBackwards() {
        allowCharRequests(7, 9, 80, "these words" + StringUtils.repeat(" ", 61));
        allowCharRequests(8, 1, 12, "are selected" + StringUtils.repeat(" ", 68));
        expectCopy("these words\nare selected");

        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
            oneOf(renderer).setSelection(new Position(9, 7),
                    new Position(12, 8));
            allowing(boundaries).findStartOfWord(new Position(9, 7));
                will(returnValue(new Position(9, 7)));
            allowing(boundaries).findEndOfWord(new Position(10, 8));
                will(returnValue(new Position(12, 8)));
        }});

        manager.startSelection(new Position(10, 8), 2);
        manager.updateSelectionEnd(new Position(9, 7));
        manager.copySelection();
    }
}

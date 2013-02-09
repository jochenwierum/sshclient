package de.jowisoftware.sshclient.terminal.mouse;

import org.jmock.Expectations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.jowisoftware.sshclient.JMockTest;
import de.jowisoftware.sshclient.terminal.buffer.BoundaryLocator;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.Renderer;
import de.jowisoftware.sshclient.util.StringUtils;

public class DefaultMouseCursorManagerTest extends JMockTest {
    private Renderer renderer;
    private ClipboardManager clipboard;
    private BoundaryLocator boundaries;

    @BeforeMethod
    public void setUp() {
        renderer = context.mock(Renderer.class);
        clipboard = context.mock(ClipboardManager.class);
        boundaries = context.mock(BoundaryLocator.class);
    }

    private DefaultMouseCursorManager aManager() {
        return aManager(new BufferMockHelper(context, 0, 0), 0);
    }

    private DefaultMouseCursorManager aManager(final BufferMockHelper bufferHelper) {
        return aManager(bufferHelper, 0);
    }

    private DefaultMouseCursorManager aManager(final BufferMockHelper bufferHelper,
            final int scrollBack) {
        return new DefaultMouseCursorManager(bufferHelper.finishMock(scrollBack),
                renderer, clipboard, boundaries);
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


    @Test
    public void selectionIsCopiedToClipboard() {
        allowRenderer();
        final BufferMockHelper bufferHelper = new BufferMockHelper(context, 80, 24);

        bufferHelper.allowCharRequests(10, 4,
                " a test" + StringUtils.repeat(" ", 70));
        bufferHelper.allowCharRequests(11, 1,
                "line two: some Text!" + StringUtils.repeat(" ", 60));
        bufferHelper.allowCharRequests(12, 1, "last one ");
        expectCopy(" a test\nline two: some Text!\nlast one");

        final DefaultMouseCursorManager manager = aManager(bufferHelper);
        manager.startSelection(new Position(4, 10), 1);
        manager.updateSelectionEnd(new Position(10, 12));
        manager.copySelection();
    }

    @Test
    public void invertedSelectionIsCopiedToClipboard() {
        allowRenderer();

        final BufferMockHelper bufferHelper = new BufferMockHelper(context, 40, 24);
        bufferHelper.allowCharRequests(4, 2,
                "output" + StringUtils.repeat(" ", 33));
        bufferHelper.allowCharRequests(5, 1,
                "123456789");
        expectCopy("output\n123456789");

        final DefaultMouseCursorManager manager = aManager(bufferHelper);
        manager.startSelection(new Position(10, 5), 1);
        manager.updateSelectionEnd(new Position(2, 4));
        manager.copySelection();
    }

    @Test
    public void selectionInSingleLineIsCopiedToClipboard() {
        allowRenderer();

        final BufferMockHelper bufferHelper = new BufferMockHelper(context, 15, 3);
        bufferHelper.allowCharRequests(2, 7, "works!");

        expectCopy("works!");

        final DefaultMouseCursorManager manager = aManager(bufferHelper);
        manager.startSelection(new Position(13, 2), 1);
        manager.updateSelectionEnd(new Position(7, 2));
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

        final BufferMockHelper bufferHelper =
                new BufferMockHelper(context, 0, 0);
        final DefaultMouseCursorManager manager = aManager(bufferHelper);
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

        final DefaultMouseCursorManager manager = aManager();
        manager.startSelection(new Position(1, 2), 1);
        manager.updateSelectionEnd(new Position(1, 2));
    }

    @Test
    public void doubleSelectingTheSameEndDoesNothing() {
        context.checking(new Expectations() {{
            oneOf(renderer).setSelection(new Position(2, 5),
                    new Position(4, 5));
        }});

        final DefaultMouseCursorManager manager = aManager();
        manager.startSelection(new Position(2, 5), 1);
        manager.updateSelectionEnd(new Position(5, 5));
        manager.updateSelectionEnd(new Position(5, 5));
    }

    @Test
    public void bug25CopyWithoutSelectionDoesNothing() {
        context.checking(new Expectations() {{
            allowing(renderer).clearSelection();
        }});

        final DefaultMouseCursorManager manager = aManager();
        manager.copySelection();
        manager.startSelection(new Position(7, 5), 1);
        manager.updateSelectionEnd(new Position(7, 5));
        manager.copySelection();
    }

    @Test
    public void trippleClickOnSamePositionSelectsLine() {
        final BufferMockHelper bufferHelper = new BufferMockHelper(context, 80, 24);

        bufferHelper.allowCharRequests(5, 1, "It works!" + StringUtils.repeat(" ", 71));
        expectCopy("It works!");

        context.checking(new Expectations() {{
            oneOf(renderer).setSelection(new Position(1, 5),
                    new Position(80, 5));
        }});

        final DefaultMouseCursorManager manager = aManager(bufferHelper);
        manager.startSelection(new Position(5, 5), 3);
        manager.updateSelectionEnd(new Position(5, 5));
        manager.copySelection();
    }

    @Test
    public void trippleClickWithNegativeOffsetSelectsLines() {
        final BufferMockHelper bufferHelper = new BufferMockHelper(context, 80, 24);

        bufferHelper.allowCharRequests(7, 1, "Some text" + StringUtils.repeat(" ", 71));
        bufferHelper.allowCharRequests(8, 1, "Copy me :-)" + StringUtils.repeat(" ", 69));
        expectCopy("Some text\nCopy me :-)");

        context.checking(new Expectations() {{
            oneOf(renderer).setSelection(new Position(1, 7),
                    new Position(80, 8));
        }});

        final DefaultMouseCursorManager manager = aManager(bufferHelper);
        manager.startSelection(new Position(9, 8), 3);
        manager.updateSelectionEnd(new Position(15, 7));
        manager.copySelection();
    }

    @Test
    public void trippleClickWithPositiveOffsetSelectsLines() {
        final BufferMockHelper bufferHelper = new BufferMockHelper(context, 80, 24);

        bufferHelper.allowCharRequests(7, 1, "More text" + StringUtils.repeat(" ", 71));
        bufferHelper.allowCharRequests(8, 1, "Boring text" + StringUtils.repeat(" ", 69));
        expectCopy("More text\nBoring text");

        context.checking(new Expectations() {{
            oneOf(renderer).setSelection(new Position(1, 7),
                    new Position(80, 8));
        }});

        final DefaultMouseCursorManager manager = aManager(bufferHelper);
        manager.startSelection(new Position(9, 7), 3);
        manager.updateSelectionEnd(new Position(15, 8));
        manager.copySelection();
    }

    @Test
    public void doubleClickSelectsWord() {
        final BufferMockHelper bufferHelper = new BufferMockHelper(context, 80, 24);

        bufferHelper.allowCharRequests(7, 9, "some");
        expectCopy("some");

        context.checking(new Expectations() {{
            oneOf(renderer).setSelection(new Position(9, 7),
                    new Position(12, 7));
            allowing(boundaries).findStartOfWord(new Position(9, 7));
                will(returnValue(new Position(9, 7)));
            allowing(boundaries).findEndOfWord(new Position(9, 7));
                will(returnValue(new Position(12, 7)));
        }});

        final DefaultMouseCursorManager manager = aManager(bufferHelper);
        manager.startSelection(new Position(9, 7), 2);
        manager.updateSelectionEnd(new Position(9, 7));
        manager.copySelection();
    }

    @Test
    public void doubleClickSelectsMultipleWords() {
        final BufferMockHelper bufferHelper = new BufferMockHelper(context, 80, 24);
        bufferHelper.allowCharRequests(7, 9, "these words" + StringUtils.repeat(" ", 61));
        bufferHelper.allowCharRequests(8, 1, "are selected");

        expectCopy("these words\nare selected");

        context.checking(new Expectations() {{
            oneOf(renderer).setSelection(new Position(9, 7),
                    new Position(12, 8));
            allowing(boundaries).findStartOfWord(new Position(9, 7));
                will(returnValue(new Position(9, 7)));
            allowing(boundaries).findEndOfWord(new Position(10, 8));
                will(returnValue(new Position(12, 8)));
        }});

        final DefaultMouseCursorManager manager = aManager(bufferHelper);
        manager.startSelection(new Position(9, 7), 2);
        manager.updateSelectionEnd(new Position(10, 8));
        manager.copySelection();
    }

    @Test
    public void doubleClickSelectsMultipleWordsBackwards() {
        final BufferMockHelper bufferHelper = new BufferMockHelper(context, 80, 24);
        bufferHelper.allowCharRequests(7, 9, "these words" + StringUtils.repeat(" ", 61));
        bufferHelper.allowCharRequests(8, 1, "are selected");
        expectCopy("these words\nare selected");

        context.checking(new Expectations() {{
            oneOf(renderer).setSelection(new Position(9, 7),
                    new Position(12, 8));
            allowing(boundaries).findStartOfWord(new Position(9, 7));
                will(returnValue(new Position(9, 7)));
            allowing(boundaries).findEndOfWord(new Position(10, 8));
                will(returnValue(new Position(12, 8)));
        }});

        final DefaultMouseCursorManager manager = aManager(bufferHelper);
        manager.startSelection(new Position(10, 8), 2);
        manager.updateSelectionEnd(new Position(9, 7));
        manager.copySelection();
    }

    @Test public void
    renderOffsetIsForwarded() {
        allowRenderer();
        final BufferMockHelper bufferHelper = new BufferMockHelper(context, 80, 24);
        bufferHelper.allowCharRequests(7, 9, "test");
        expectCopy("test");

        final DefaultMouseCursorManager manager = aManager(bufferHelper, 2);
        manager.setRenderOffset(2);
        manager.startSelection(new Position(9, 7), 1);
        manager.updateSelectionEnd(new Position(13, 7));
        manager.copySelection();
    }


    @Test public void
    renderOffsetWithSmallerHistoryIsSupported() {
        allowRenderer();
        final BufferMockHelper bufferHelper = new BufferMockHelper(context, 80, 24);
        bufferHelper.allowCharRequests(7, 9, "test" + StringUtils.repeat(" ", 8));
        bufferHelper.reduceLengthOfLine(7, 20);
        bufferHelper.allowCharRequests(8, 1, "test");
        expectCopy("test\ntest");

        final DefaultMouseCursorManager manager = aManager(bufferHelper, 4);
        manager.setRenderOffset(4);
        manager.startSelection(new Position(9, 7), 1);
        manager.updateSelectionEnd(new Position(5, 8));
        manager.copySelection();
    }
}

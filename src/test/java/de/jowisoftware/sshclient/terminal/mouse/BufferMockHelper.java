package de.jowisoftware.sshclient.terminal.mouse;

import org.jmock.Expectations;
import org.jmock.Mockery;

import de.jowisoftware.sshclient.terminal.buffer.Buffer;
import de.jowisoftware.sshclient.terminal.buffer.Position;
import de.jowisoftware.sshclient.terminal.buffer.Snapshot;
import de.jowisoftware.sshclient.terminal.buffer.SnapshotWithHistory;
import de.jowisoftware.sshclient.terminal.gfx.GfxChar;

public class BufferMockHelper {
    private final Mockery context;
    private final GfxChar[][] result;

    private final Buffer buffer;

    public BufferMockHelper(final Mockery context, final int width, final int height) {
        this.context = context;
        result = new GfxChar[height][width];

        buffer = context.mock(Buffer.class);
        allowSize(width, height);
    }

    public Buffer finishMock(final int scrollBack) {
        final SnapshotWithHistory snapshot = context.mock(SnapshotWithHistory.class);

        context.checking(new Expectations() {{
           allowing(buffer).createSnapshot(); will(returnValue(snapshot));
           allowing(snapshot).createSimpleSnapshot(scrollBack);
            will(returnValue(new Snapshot(result, null)));
        }});

        return buffer;
    }

    public void allowCharRequests(final int line, final int from,
            final String string) {
        final char[] chars = string.toCharArray();
        final int to = from + chars.length - 1;

        final GfxChar character = context.mock(GfxChar.class, "char-" + line +
                "-" + from + "-" + to);

        context.checking(new Expectations() {{
            for (int i = from; i <= to; ++i) {
                oneOf(character).getCharAsString(); will(
                        returnValue(Character.toString(chars[i - from])));
                oneOf(character).getCharCount(); will(
                        returnValue(1));

                result[line - 1][i - 1] = character;
            }
        }});
    }

    private void allowSize(final int width, final int height) {
        context.checking(new Expectations() {{
            allowing(buffer).getSize();
            will(returnValue(new Position(width, height)));
        }});
    }

    public void reduceLengthOfLine(final int line, final int newLength) {
        final GfxChar newLine[] = new GfxChar[newLength];
        System.arraycopy(result[line - 1], 0, newLine, 0, newLength);
        result[line - 1] = newLine;
    }
}

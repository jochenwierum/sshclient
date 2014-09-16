package de.jowisoftware.sshclient.terminal.buffer;

import de.jowisoftware.sshclient.terminal.gfx.GfxChar;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class WordBoundaryLocatorTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private BoundaryLocator locator;
    private Buffer buffer;

    @Before
    public void setUp() {
        buffer = context.mock(Buffer.class);
        locator = new WordBoundaryLocator(buffer);
        context.checking(new Expectations() {{
            allowing(buffer).getSize(); will(returnValue(new Position(80, 24)));
        }});
    }

    private Position aPosition(final int x, final int y) {
        return new Position(x, y);
    }

    private void prepareBuffer(final int y, final int firstCol, final String text) {
        context.checking(new Expectations(){{
            for (int i = 0; i < text.length(); ++i) {
                final int x = firstCol + i;

                final GfxChar gfxChar = context.mock(GfxChar.class,
                        "char-" + x + "-" + y);

                allowing(buffer).getCharacter(y, x); will(returnValue(gfxChar));
                allowing(gfxChar).getCharAsString();
                    will(returnValue(text.substring(i, i + 1)));
            }
        }});
    }

    @Test
    public void nonWordCharsAreReportedAsIs() {
        prepareBuffer(5, 7, "=");
        assertThat(locator.findStartOfWord(aPosition(7, 5)), is(equalTo(aPosition(7, 5))));
        prepareBuffer(6, 7, "=");
        assertThat(locator.findEndOfWord(aPosition(7, 6)), is(equalTo(aPosition(7, 6))));

        prepareBuffer(7, 9, "*");
        assertThat(locator.findStartOfWord(aPosition(9, 7)), is(equalTo(aPosition(9, 7))));
        prepareBuffer(8, 9, "*");
        assertThat(locator.findEndOfWord(aPosition(9, 8)), is(equalTo(aPosition(9, 8))));
    }

    @Test
    public void findStartOfWordFindsWordBoundary() {
        prepareBuffer(5, 11, "+this");
        assertThat(locator.findStartOfWord(aPosition(13, 5)), is(equalTo(aPosition(12, 5))));
    }

    @Test
    public void findStartOfWordFindsWordBoundaryAtBeginningOfLine() {
        prepareBuffer(3, 1, "that");
        assertThat(locator.findStartOfWord(aPosition(2, 3)), is(equalTo(aPosition(1, 3))));
    }

    @Test
    public void findStartOfWordsFindWordBoundaryAfterEndOfLine() {
        prepareBuffer(3, 76, " last");
        assertThat(locator.findStartOfWord(aPosition(81, 3)), is(equalTo(aPosition(77, 3))));
    }

    @Test
    public void findStartWithSelectionChars() {
        prepareBuffer(5, 11, " ver%y+cool");
        locator.setSelectionChars("%+");
        assertThat(locator.findStartOfWord(aPosition(21, 5)), is(equalTo(aPosition(12, 5))));
    }

    @Test
    public void findEndOfWordFindsWordBoundary() {
        prepareBuffer(5, 11, "this-");
        assertThat(locator.findEndOfWord(aPosition(13, 5)), is(equalTo(aPosition(14, 5))));
    }

    @Test
    public void findEndOfWordFindsWordBoundaryAtBeginningOfLine() {
        prepareBuffer(3, 77, "that");
        assertThat(locator.findEndOfWord(aPosition(78, 3)), is(equalTo(aPosition(80, 3))));
    }

    @Test
    public void findEndOfWordFindsWordBoundaryAfterEndOfLine() {
        prepareBuffer(3, 77, "last");
        assertThat(locator.findEndOfWord(aPosition(81, 3)), is(equalTo(aPosition(80, 3))));
    }

    @Test
    public void findEndOfWordFindsWordBoundaryAfterEndOfLineWithSpace() {
        prepareBuffer(3, 76, "last ");
        assertThat(locator.findEndOfWord(aPosition(81, 3)), is(equalTo(aPosition(80, 3))));
    }

    @Test
    public void findEndWithSelectionChars() {
        prepareBuffer(5, 11, "ver%y+cool ");
        locator.setSelectionChars("%+");
        assertThat(locator.findEndOfWord(aPosition(12, 5)), is(equalTo(aPosition(20, 5))));
    }

}

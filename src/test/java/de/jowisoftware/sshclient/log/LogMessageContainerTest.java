package de.jowisoftware.sshclient.log;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LogMessageContainerTest {
    @Test public void
    simpleStringsAreTransformedToHtml() {
        assertThat(new LogMessageContainer("Hello, World").toHTML(), is(equalTo("<html><b>Hello, World</b></html>")));
    }

    @Test public void
    newLinesAreConvertedToBreaks() {
        assertThat(new LogMessageContainer("Hello\nWorld").toHTML(), is(equalTo("<html><b>Hello<br />World</b></html>")));
    }

    @Test public void
    stackTracesAreNotBold() {
        assertThat(new LogMessageContainer("Hello, World\na.stupid.Exception at\nmy.Class").toHTML(),
                is(equalTo("<html><b>Hello, World</b><br />a.stupid.Exception at<br />my.Class</html>")));
    }
}

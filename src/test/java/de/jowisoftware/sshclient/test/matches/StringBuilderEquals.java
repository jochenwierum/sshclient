package de.jowisoftware.sshclient.test.matches;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class StringBuilderEquals extends TypeSafeMatcher<StringBuilder> {
    private final String s;

    public StringBuilderEquals(final String s) {
        this.s = s;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("the string " + s);
    }

    @Override
    public boolean matchesSafely(final StringBuilder item) {
        return item.toString().equals(s);
    }
}
package de.jowisoftware.sshclient;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class HamcrestHelper {
    public static <T, S extends Iterable<T>> Matcher<T> containsElementThat(
            final Matcher<T> matcher) {
        return new BaseMatcher<T>() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean matches(final Object item) {
                for (final T listItem : (S) item) {
                    if (matcher.matches(listItem)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("None of the items in the list ");
                description.appendDescriptionOf(matcher);
            }
        };
    }
}

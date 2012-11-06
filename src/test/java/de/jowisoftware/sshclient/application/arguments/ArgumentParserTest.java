package de.jowisoftware.sshclient.application.arguments;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsArray.array;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.jowisoftware.sshclient.application.settings.ApplicationSettings;
import de.jowisoftware.sshclient.application.settings.Profile;

@RunWith(JMock.class)
public class ArgumentParserTest {
    private JUnit4Mockery context;
    @SuppressWarnings("rawtypes")
    private ArgumentParserCallback callback;
    private ApplicationSettings<?> settings;
    private ArgumentParser<?> parser;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Before
    public void setUp() {
        context = new JUnit4Mockery();
        callback = context.mock(ArgumentParserCallback.class);
        settings = context.mock(ApplicationSettings.class);
        parser = new ArgumentParser(callback, settings);
    }

    @SuppressWarnings("unchecked")
    @Test public void
    unknownArgumentsCauseErrors() {
        context.checking(new Expectations() {{
            oneOf(callback).reportArgumentError(with(array(is("-arg1"), is("-arg2"), is("@:"))));
        }});

        parser.processArguments(new String[]{"-arg1", "-arg2", "@:"});
    }

    @SuppressWarnings("unchecked")
    @Test public void
    profilesAreFound() {
        final Profile<?> profile1 = context.mock(Profile.class, "profile1");
        final Profile<?> profile2 = context.mock(Profile.class, "profile2");

        final Map<String, Profile<?>> profileMap = new HashMap<String, Profile<?>>();
        profileMap.put("p1", profile1);
        profileMap.put("profile2", profile2);

        context.checking(new Expectations() {{
            allowing(settings).getProfiles(); will(returnValue(profileMap));

            oneOf(callback).openConnection(profile1);
            oneOf(callback).openConnection(profile2);
            oneOf(callback).reportArgumentError(with(array(is("+p3"))));
        }});

        parser.processArguments(new String[]{"+p1", "+profile2", "+p3"});
    }

    @SuppressWarnings("unchecked")
    @Test public void
    sshURIsAreParsed() {
        final Profile<?> profile = context.mock(Profile.class, "profile");

        context.checking(new Expectations() {{
            allowing(settings).newDefaultProfile(); will(returnValue(profile));

            oneOf(profile).setHost("host");
            oneOf(callback).openConnection(profile);
        }});

        parser.processArguments(new String[]{"host"});
    }

    @SuppressWarnings("unchecked")
    @Test public void
    complexSshURIsAreParsed() {
        final Profile<?> profile = context.mock(Profile.class, "profile");

        context.checking(new Expectations() {{
            allowing(settings).newDefaultProfile(); will(returnValue(profile));

            oneOf(profile).setHost("hostname");
            oneOf(profile).setUser("user");
            oneOf(profile).setPort(123);
            oneOf(callback).openConnection(profile);
        }});

        parser.processArguments(new String[]{"user:password@hostname:123"});
    }

    @Test public void
    keysCanBeLoaded() {
        context.checking(new Expectations(){{
            oneOf(callback).loadKey("/path/to/key1");
            oneOf(callback).loadKey("/path/to/key2");
        }});

        parser.processArguments(new String[]{"-key", "/path/to/key1",
                "-key", "/path/to/key2"});
    }
}

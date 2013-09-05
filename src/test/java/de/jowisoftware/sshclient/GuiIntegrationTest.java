package de.jowisoftware.sshclient;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.image.ScreenshotTaker;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import javax.swing.JFrame;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

@RunWith(GuiIntegrationTest.MavenGUITestRunner.class)
public abstract class GuiIntegrationTest {
    public static class MavenGUITestRunner extends BlockJUnit4ClassRunner {
        private static final File screenshotDir = new File("target/guitest-screenshots");
        private final ScreenshotTaker screenshotTaker;

        public MavenGUITestRunner(Class<?> testClass) throws InitializationError {
            super(testClass);
            screenshotDir.mkdirs();
            screenshotTaker = new ScreenshotTaker();
        }

        @Override
        protected Statement methodInvoker(final FrameworkMethod method, final Object test) {
            final Method realMethod = method.getMethod();
            final String testName = realMethod.getDeclaringClass() + "." + realMethod;
            final File screenshotFile = new File(screenshotDir, testName + ".png");

            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    screenshotFile.delete();

                    try {
                        method.invokeExplosively(test);
                        ((GuiIntegrationTest) test).checkAssertions();
                    } catch (Throwable t) {
                        screenshotTaker.saveDesktopAsPng(screenshotFile.getAbsolutePath());
                        throw t;
                    }
                }
            };
        }
    }


    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setThreadingPolicy(new Synchroniser());
    }};

    private final List<AssertionError> errors = new LinkedList<>();

    protected JFrame parent;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setupParent() {
        parent = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            @Override
            protected JFrame executeInEDT() throws Throwable {
                final JFrame frame = new JFrame("test - parent form");
                frame.setVisible(true);
                return frame;
            }
        });
    }

    @After
    public void cleanUp() {
        if (parent != null) {
            parent.setVisible(false);
            parent.dispose();
        }
    }

    public <T> void chainedAssertThat(T actual, Matcher<? super T> matcher) {
        chainedAssertThat("", actual, matcher);
    }

    public <T> void chainedAssertThat(String reason, T actual, Matcher<? super T> matcher) {
        try {
            MatcherAssert.assertThat(reason, actual, matcher);
        } catch(AssertionError e) {
            errors.add(e);
        }
    }

    public void checkAssertions() {
        if (!errors.isEmpty()) {
            throw new AssertionError(formatErrors(errors));
        }
    }

    private String formatErrors(final List<AssertionError> errors) {
        final StringWriter writer = new StringWriter();
        writer.append("One or more assertions failed:\n");

        final PrintWriter printWriter = new PrintWriter(writer) {
            @Override
            public void println(final Object o) {
                super.print("\t");
                super.println(o.toString().replaceAll("\\n", "\n\t"));
            }
        };

        for (final AssertionError error : errors) {
            error.printStackTrace(printWriter);
        }

        final Pattern pattern = Pattern.compile(
                "^\\t\\tat (org\\.hamcrest\\.MatcherAssert|"
                + "de\\.jowisoftware\\.sshclient\\.GuiIntegrationTest)\\.assertThat.+?\\n",
                Pattern.MULTILINE);
        return pattern.matcher(writer.toString()).replaceAll("");
    }
}

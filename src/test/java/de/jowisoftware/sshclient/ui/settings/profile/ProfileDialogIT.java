package de.jowisoftware.sshclient.ui.settings.profile;

import de.jowisoftware.sshclient.application.settings.ApplicationSettings;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.DialogFixture;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.*;

import javax.swing.JFrame;
import java.awt.Dialog;
import java.util.HashMap;
import java.util.Map;

public class ProfileDialogIT {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery() {{
       setThreadingPolicy(new Synchroniser());
    }};

    private JFrame parent;
    private DialogFixture dialogFixture;
    private ApplicationSettings<AWTProfile> applicationSettings;


    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUpWindow() {
        applicationSettings = createAppSettings();

        final Dialog dialog = GuiActionRunner.execute(new GuiQuery<Dialog>() {
            @Override
            protected Dialog executeInEDT() throws Throwable {
                parent = new JFrame("parent");
                parent.setVisible(true);
                return new ProfilesDialog(parent, applicationSettings);
            }
        });

        dialogFixture = new DialogFixture(dialog);
        dialogFixture.show();
    }

    private ApplicationSettings<AWTProfile> createAppSettings() {
        final Map<String, AWTProfile> profiles = new HashMap<>();
        profiles.put("myProfile", new AWTProfile());
        final AWTProfile newProfile = new AWTProfile();

        @SuppressWarnings("unchecked")
        final ApplicationSettings<AWTProfile> applicationSettingsMock = context.mock(ApplicationSettings.class);

        context.checking(new Expectations() {{
            allowing(applicationSettingsMock).getProfiles();
                will(returnValue(profiles));
            allowing(applicationSettingsMock).newDefaultProfile();
                will(returnValue(newProfile));
        }});

        return applicationSettingsMock;
    }

    @After
    public void cleanUp() {
        if (parent != null) {
            parent.setVisible(false);
            parent.dispose();
        }
    }

    @Test
    public void closeFrame() {
        dialogFixture.button("close").click();
    }
}

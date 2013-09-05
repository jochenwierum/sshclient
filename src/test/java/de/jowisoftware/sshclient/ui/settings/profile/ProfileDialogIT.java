package de.jowisoftware.sshclient.ui.settings.profile;

import de.jowisoftware.sshclient.GuiIntegrationTest;
import de.jowisoftware.sshclient.application.settings.ApplicationSettings;
import de.jowisoftware.sshclient.application.settings.Forwarding;
import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.CursorStyle;
import de.jowisoftware.sshclient.terminal.gfx.awt.AWTGfxInfo;
import de.jowisoftware.sshclient.util.FontUtils;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.hamcrest.CoreMatchers;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.swing.core.KeyPressInfo.keyCode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProfileDialogIT extends GuiIntegrationTest {

    private DialogFixture dialogFixture;
    private ApplicationSettings<AWTProfile> applicationSettings;
    private int dialogs = 0;

    @Before
    public void setUpWindow() {
        applicationSettings = createAppSettings();

        final Dialog dialog = GuiActionRunner.execute(new GuiQuery<Dialog>() {
            @Override
            protected Dialog executeInEDT() throws Throwable {
                return new ProfilesDialog(parent, applicationSettings);
            }
        });

        dialogFixture = new DialogFixture(dialog);
        dialogFixture.show(new Dimension(640, 480));
        FontUtils.fillAsyncCache();
    }

    @After
    public void tearDown() {
        dialogFixture.cleanUp();
    }

    private ApplicationSettings<AWTProfile> createAppSettings() {
        final Map<String, AWTProfile> profiles = new HashMap<>();
        final AWTProfile profile = new AWTProfile();
        profiles.put("myProfile", profile);
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

    @Test
    public void settingsAreSaved() throws Exception {
        dialogFixture.list("profileSelectionList").clickItem(0);
        dialogFixture.button("edit").click();

        doGeneralTab();
        doGraphicsTab();
        doForwardingTab();

        dialogFixture.button("save").click();
        dialogFixture.button("close").click();

        checkAsserts();
    }

    private void doForwardingTab() {
        dialogFixture.tabbedPane("tabs").selectTab(2);

        dialogFixture.checkBox("agent forwarding").check();
        dialogFixture.checkBox("x11 forwarding").check();
        dialogFixture.textBox("x11 host").setText("192.168.0.1");
        dialogFixture.textBox("x11 display").setText("7");

        dialogFixture.radioButton("remote").check();
        dialogFixture.textBox("source host").setText("192.168.1.2");
        dialogFixture.textBox("remote host").setText("192.168.2.1");
        dialogFixture.textBox("source port").setText("22");
        dialogFixture.textBox("remote port").setText("2222");
        dialogFixture.button("add forwarding").click();

        dialogFixture.radioButton("local").check();
        dialogFixture.textBox("source host").setText("192.168.11.2");
        dialogFixture.textBox("remote host").setText("192.168.12.1");
        dialogFixture.textBox("source port").setText("122");
        dialogFixture.textBox("remote port").setText("1222");
        dialogFixture.button("add forwarding").click();

        dialogFixture.radioButton("local").check();
        dialogFixture.textBox("source host").setText("192.168.11.23");
        dialogFixture.textBox("remote host").setText("192.168.12.13");
        dialogFixture.textBox("source port").setText("1223");
        dialogFixture.textBox("remote port").setText("1223");
        dialogFixture.button("add forwarding").click();

        dialogFixture.list("forwardings").selectItem(2);
        dialogFixture.button("delete forwarding").click();

        dialogFixture.textBox("socks port").setText("9812");
    }

    private void checkAsserts() {
        final AWTProfile awtProfile = applicationSettings.getProfiles().get("myProfile");
        assertThat(awtProfile, is(notNullValue()));

        final AWTGfxInfo gfxSettings = awtProfile.getGfxSettings();

        chainedAssertThat(awtProfile.getHost(), is(equalTo("hostname")));
        chainedAssertThat(awtProfile.getPort(), is(335));
        chainedAssertThat(awtProfile.getUser(), is(equalTo("user5")));
        chainedAssertThat(awtProfile.getCommand(), is(equalTo("/bin/command")));
        chainedAssertThat(awtProfile.getCharsetName(), is(equalTo("ISO-8859-1")));
        chainedAssertThat(awtProfile.getCharset(), is(equalTo(Charset.forName("ISO-8859-1"))));
        chainedAssertThat(awtProfile.getTimeout(), is(100));
        chainedAssertThat(awtProfile.getKeepAliveInterval(), is(2000));
        chainedAssertThat(awtProfile.getKeepAliveCount(), is(30));
        chainedAssertThat(gfxSettings.getFontName(), is(equalTo("Courier New")));
        chainedAssertThat(gfxSettings.getFontSize(), is(11));
        chainedAssertThat(gfxSettings.getFont().getFontName(), is(equalTo("Courier New")));
        final int realFontSize = (int)Math.round(11 * Toolkit.getDefaultToolkit().getScreenResolution() / 72.0);
        chainedAssertThat(gfxSettings.getFont().getSize(), is(realFontSize));
        chainedAssertThat(gfxSettings.getAntiAliasingMode(), is(1));
        chainedAssertThat(gfxSettings.getCursorColor(), is(Color.RED));
        chainedAssertThat(gfxSettings.getCursorStyle(), is(CursorStyle.UNDERLINE));
        chainedAssertThat(gfxSettings.getColorMap().get(ColorName.BLUE), is(new Color(0, 0, 200)));
        chainedAssertThat(gfxSettings.getLightColorMap().get(ColorName.RED), is(new Color(128, 0, 0)));
        chainedAssertThat(gfxSettings.getLightColorMap().get(ColorName.BLUE), is(new Color(0, 0, 127)));
        chainedAssertThat(awtProfile.getAgentForwarding(), is(true));
        chainedAssertThat(awtProfile.getX11Forwarding(), is(true));
        chainedAssertThat(awtProfile.getX11Host(), is(equalTo("192.168.0.1")));
        chainedAssertThat(awtProfile.getX11Display(), is(equalTo(7)));
        chainedAssertThat(awtProfile.getSocksPort(), is(equalTo(9812)));

        final List<Forwarding> forwardings = awtProfile.getPortForwardings();
        chainedAssertThat(forwardings.size(), is(2));
        chainedAssertThat(forwardings, CoreMatchers.hasItems(
                new Forwarding(Forwarding.Direction.REMOTE, "192.168.1.2", 22, "192.168.2.1", 2222),
                new Forwarding(Forwarding.Direction.LOCAL, "192.168.11.2", 122, "192.168.12.1", 1222)
                ));
    }

    private void doGeneralTab() {
        dialogFixture.textBox("profile name").requireText("myProfile");
        dialogFixture.textBox("host name").setText("hostname");
        dialogFixture.textBox("port").setText("335");
        dialogFixture.textBox("user name").setText("user5");
        dialogFixture.textBox("command").setText("/bin/command");
        dialogFixture.comboBox("encoding").selectItem("ISO-8859-1");
        dialogFixture.textBox("timeout").setText("100");
        dialogFixture.textBox("keep alive interval").setText("2000");
        dialogFixture.textBox("keep alive count").setText("30");
    }

    private void doGraphicsTab() throws Exception {
        dialogFixture.tabbedPane("tabs").selectTab(1);
        dialogFixture.comboBox("font").selectItem("Courier New");
        dialogFixture.textBox("font size").setText("11");
        dialogFixture.comboBox("anti aliasing").selectItem(1);

        selectColorAndClose("cursor color", 255, 0, 0);

        dialogFixture.comboBox("cursor style").selectItem("underline");
        dialogFixture.checkBox("cursor blinks").uncheck();

        selectColorAndClose("color lightcolor.RED", 128, 0, 0);
        selectColorAndClose("color lightcolor.BLUE", 0, 0, 127);
        selectColorAndClose("color color.BLUE", 0, 0, 200);
    }

    private void selectColorAndClose(final String button, final int r, final int g, final int b) throws Exception {
        dialogFixture.button(button).click();
        dialogs += 1;
        final DialogFixture colorChooser = WindowFinder.findDialog("dialog" + dialogs).using(dialogFixture.robot);

        colorChooser.focus();
        colorChooser.tabbedPane().selectTab(3);
        colorChooser.textBox(nThTextbox(0)).setText(Integer.toString(r));
        colorChooser.textBox(nThTextbox(1)).setText(Integer.toString(g));
        colorChooser.textBox(nThTextbox(2)).setText(Integer.toString(b));
        colorChooser.pressAndReleaseKey(keyCode(KeyEvent.VK_ENTER));
    }

    private NthMatcher<JTextComponent> nThTextbox(final int n) {
        return new NthMatcher<>(JTextComponent.class, n);
    }
}

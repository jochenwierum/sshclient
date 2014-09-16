package de.jowisoftware.sshclient.application.settings.validation;

import de.jowisoftware.sshclient.application.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.GfxInfo;
import org.junit.Before;
import org.junit.Test;

public class ColorValidatorTest extends AbstractValidationTest<AWTProfile> {
    @Before
    public void setUp() {
        validator = new ColorValidator<>();
    }

    @Test
    public void testMissingColor() {
        final GfxInfo<?> settings = profile.getGfxSettings();
        settings.getColorMap().remove(ColorName.RED);

        assertError("gfx.colors", "missing color: red");
    }

    @Test
    public void testMissingLightColor() {
        final GfxInfo<?> settings = profile.getGfxSettings();
        settings.getLightColorMap().remove(ColorName.GREEN);

        assertError("gfx.lightcolors", "missing color: green");
    }

    @Test
    public void testAllOk() {
        assertNoError();

        profile.getGfxSettings().getColorMap().put(ColorName.RED,
                new java.awt.Color(10, 20, 30));
        assertNoError();

        profile.getGfxSettings().getLightColorMap().put(ColorName.RED,
                new java.awt.Color(10, 20, 30));
        assertNoError();
    }

    @Override
    protected AWTProfile newProfile() {
        return new AWTProfile();
    }
}

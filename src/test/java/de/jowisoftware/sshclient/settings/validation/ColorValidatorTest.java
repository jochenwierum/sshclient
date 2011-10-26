package de.jowisoftware.sshclient.settings.validation;

import org.junit.Before;
import org.junit.Test;

import de.jowisoftware.sshclient.terminal.ColorName;
import de.jowisoftware.sshclient.ui.terminal.GfxInfo;

public class ColorValidatorTest extends ValidationTest {
    @Before
    public void setUp() {
        validator = new ColorValidator();
    }

    @Test
    public void testMissingColor() {
        final GfxInfo settings = profile.getGfxSettings();
        settings.getColorMap().remove(ColorName.RED);

        assertError("gfx.colors", "missing color: red");
    }

    @Test
    public void testMissingLightColor() {
        final GfxInfo settings = profile.getGfxSettings();
        settings.getLightColorMap().remove(ColorName.GREEN);

        assertError("gfx.lightcolors", "missing light color: green");
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
}

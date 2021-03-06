package de.jowisoftware.sshclient.terminal.gfx.awt;

import de.jowisoftware.sshclient.terminal.gfx.ColorFactory;
import de.jowisoftware.sshclient.terminal.gfx.ColorName;
import de.jowisoftware.sshclient.terminal.gfx.TerminalColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AWTColorFactory implements ColorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AWTColorFactory.class);

    private final AWTGfxInfo gfxInfo;
    private final Map<Integer, CustomColor> customColors =
            new HashMap<>();
    private final Map<Integer, SystemColor> systemColors =
            new HashMap<>();

    public AWTColorFactory(final AWTGfxInfo gfxInfo) {
        this.gfxInfo = gfxInfo;
        initSystemColors();
        initCustomColors();
    }

    private void initSystemColors() {
        final int colorCount = ColorName.values().length;
        for (int i = 0; i < colorCount; ++i) {
            systemColors.put(i,
                    new SystemColor(ColorName.values()[i], false, gfxInfo));
        }
        for (int i = 0; i < colorCount; ++i) {
            systemColors.put(colorCount + i,
                    new SystemColor(ColorName.values()[i], true, gfxInfo));
        }

        systemColors.get(0).inverseColor = systemColors.get(1);
        systemColors.get(1).inverseColor = systemColors.get(0);
        systemColors.get(colorCount).inverseColor = systemColors.get(colorCount + 1);
        systemColors.get(colorCount + 1).inverseColor = systemColors.get(colorCount);
    }

    private void initCustomColors() {
        for (int red = 0; red < 6; red++) {
            for (int green = 0; green < 6; green++) {
                for (int blue = 0; blue < 6; blue++) {
                    final int colorKey = (red * 36) + (green * 6) + blue + 16;
                    customColors.put(colorKey,
                            new CustomColor(
                            new Color(
                            (red > 0 ? (red * 40 + 55) : 0),
                            (green > 0 ? (green * 40 + 55) : 0),
                            (blue > 0 ? (blue * 40 + 55) : 0))));
                }
            }
        }

        final int NUMBER_OF_COLORS = 232;
        for (int i = 0; i < 24; i++) {
            final int rgb = (i * 10) + 8;
            customColors.put(NUMBER_OF_COLORS + i, new CustomColor(
                    new Color(rgb, rgb, rgb)));
        }
    }

    @Override
    public TerminalColor createStandardColor(final ColorName color,
            final boolean isBright) {
        final int colorIndex = color.ordinal();

        if (isBright) {
            final int colorCount = ColorName.values().length;
            return systemColors.get(colorIndex + colorCount);
        } else {
            return systemColors.get(colorIndex);
        }
    }

    @Override
    public TerminalColor createCustomColor(final int colorCode) {
        if (colorCode < 16) {
            final int systemColorCode = colorCode % 8 + ColorName.BLACK.ordinal();
            final boolean isBright = colorCode < 8;
            return new SystemColor(ColorName.values()[systemColorCode],
                    isBright, gfxInfo);
        }
        return customColors.get(colorCode);
    }

    @Override
    public void updateCustomColor(final int colorCode,
            final int red, final int green, final int blue) {
        LOGGER.debug("Setting new cucstom color {} to {}/{}/{}", colorCode, red, green, blue);
        customColors.get(colorCode).color = new Color(red, green, blue);
    }
}

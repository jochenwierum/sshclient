package de.jowisoftware.ssh.client.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import de.jowisoftware.ssh.client.tty.GfxCharSetup.Colors;

public class GfxInfo {
    private final Map<Colors, Color> colors = new HashMap<Colors, Color>();
    private final Map<Colors, Color> brightColors = new HashMap<Colors, Color>();

    // TODO: get this from config
    public GfxInfo() {
       colors.put(Colors.BLACK, Color.BLACK);
       colors.put(Colors.BLUE, Color.BLUE);
       colors.put(Colors.CYAN, Color.CYAN);
       colors.put(Colors.DEFAULT, Color.LIGHT_GRAY);
       colors.put(Colors.DEFAULTBG, Color.BLACK);
       colors.put(Colors.GREEN, Color.GREEN);
       colors.put(Colors.MAGENTA, Color.MAGENTA);
       colors.put(Colors.RED, Color.RED);
       colors.put(Colors.WHITE, Color.WHITE);
       colors.put(Colors.YELLOW, Color.YELLOW);
       colors.put(Colors.BLACK, Color.BLACK);

       brightColors.put(Colors.BLUE, Color.BLUE.brighter().brighter());
       brightColors.put(Colors.CYAN, Color.CYAN.brighter().brighter());
       brightColors.put(Colors.DEFAULT, Color.WHITE);
       brightColors.put(Colors.DEFAULTBG, Color.BLACK);
       brightColors.put(Colors.GREEN, Color.GREEN.brighter().brighter());
       brightColors.put(Colors.MAGENTA, Color.MAGENTA.brighter().brighter());
       brightColors.put(Colors.RED, Color.RED.brighter().brighter());
       brightColors.put(Colors.WHITE, Color.WHITE.brighter().brighter());
       brightColors.put(Colors.YELLOW, Color.YELLOW.brighter().brighter());
    }

    public Color mapColor(final Colors color, final boolean light) {
        if (!light) {
            return colors.get(color);
        } else {
            return brightColors.get(color);
        }
    }
}

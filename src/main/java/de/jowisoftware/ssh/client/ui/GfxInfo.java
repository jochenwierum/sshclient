package de.jowisoftware.ssh.client.ui;

import java.util.HashMap;
import java.util.Map;

import de.jowisoftware.ssh.client.terminal.Color;

public class GfxInfo {
    private final Map<Color, java.awt.Color> colors = new HashMap<Color, java.awt.Color>();
    private final Map<Color, java.awt.Color> brightColors = new HashMap<Color, java.awt.Color>();

    // TODO: get this from config
    public GfxInfo() {
       colors.put(Color.BLACK, java.awt.Color.BLACK);
       colors.put(Color.BLUE, java.awt.Color.BLUE);
       colors.put(Color.CYAN, java.awt.Color.CYAN);
       colors.put(Color.DEFAULT, java.awt.Color.LIGHT_GRAY);
       colors.put(Color.DEFAULTBG, java.awt.Color.BLACK);
       colors.put(Color.GREEN, java.awt.Color.GREEN);
       colors.put(Color.MAGENTA, java.awt.Color.MAGENTA);
       colors.put(Color.RED, java.awt.Color.RED);
       colors.put(Color.WHITE, java.awt.Color.WHITE);
       colors.put(Color.YELLOW, java.awt.Color.YELLOW);
       colors.put(Color.BLACK, java.awt.Color.BLACK);

       brightColors.put(Color.BLUE, java.awt.Color.BLUE.brighter().brighter());
       brightColors.put(Color.CYAN, java.awt.Color.CYAN.brighter().brighter());
       brightColors.put(Color.DEFAULT, java.awt.Color.WHITE);
       brightColors.put(Color.DEFAULTBG, java.awt.Color.BLACK);
       brightColors.put(Color.GREEN, java.awt.Color.GREEN.brighter().brighter());
       brightColors.put(Color.MAGENTA, java.awt.Color.MAGENTA.brighter().brighter());
       brightColors.put(Color.RED, java.awt.Color.RED.brighter().brighter());
       brightColors.put(Color.WHITE, java.awt.Color.WHITE.brighter().brighter());
       brightColors.put(Color.YELLOW, java.awt.Color.YELLOW.brighter().brighter());
    }

    public java.awt.Color mapColor(final Color color, final boolean light) {
        if (!light) {
            return colors.get(color);
        } else {
            return brightColors.get(color);
        }
    }
}

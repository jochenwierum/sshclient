package de.jowisoftware.sshclient.terminal.gfx;

public enum ColorName {
    DEFAULT("default foreground", 39, -1),
    DEFAULT_BACKGROUND("default background", -1, 49),
    BLACK("black", 30, 40),
    RED("red", 31, 41),
    GREEN("green", 32, 42),
    YELLOW("yellow", 33, 43),
    BLUE("blue", 34, 44),
    MAGENTA("magenta", 35, 45),
    CYAN("cyan", 36, 46),
    WHITE("white", 37, 47);

    private final String name;
    private int fgCode;
    private int bgCode;

    private ColorName(final String name, final int fgCode, final int bgCode) {
        this.name = name;
        this.fgCode = fgCode;
        this.bgCode = bgCode;
    }

    private ColorName() {
        this.name = name().toLowerCase();
    }

    public String niceName() {
        return name;
    }

    private int foregroundColorCode() {
        return fgCode;
    }

    private int backgroundColorCode() {
        return bgCode;
    }

    public static ColorName find(final int id) {
        for (final ColorName color : ColorName.values()) {
            if (color.foregroundColorCode() == id
                    || color.backgroundColorCode() == id) {
                return color;
            }
        }
        return null;
    }

    public static boolean isForeground(final int i) {
        return i < 40;
    }
}

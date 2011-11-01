package de.jowisoftware.sshclient.terminal.input.controlsequences;

import org.apache.log4j.Logger;

import de.jowisoftware.sshclient.terminal.SSHSession;
import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.gfx.Attribute;
import de.jowisoftware.sshclient.terminal.gfx.ColorFactory;
import de.jowisoftware.sshclient.terminal.gfx.GfxCharSetup;
import de.jowisoftware.sshclient.terminal.gfx.TerminalColor;
import de.jowisoftware.sshclient.util.StringUtils;

public class ANSISequencem implements ANSISequence {
    private static final Logger LOGGER = Logger.getLogger(ANSISequencem.class);

    private static final int CUSTOM_FOREGROUND_COLOR = 38;
    private static final int CUSTOM_BACKGROUND_COLOR = 48;

    @Override
    public void process(final SSHSession sessionInfo, final String... args) {
        if (args.length == 0) {
            processPartialSequence(sessionInfo, 0);
        } else {
            processSequence(sessionInfo, args);
        }
    }

    private void processSequence(final SSHSession sessionInfo,
            final String... args) {
        for (int i = 0; i < args.length; ++i) {
            if (processableCustomColor(sessionInfo, args, i)) {
                i += 2;
            } else {
                final Integer arg = parseArg(args[i]);
                if (arg != null) {
                    processPartialSequence(sessionInfo, arg);
                }
            }
        }
    }

    private Integer parseArg(final String argument) {
        if (argument.length() == 0) {
            return null;
        }
        return Integer.parseInt(argument);
    }

    private boolean processableCustomColor(final SSHSession sessionInfo,
            final String[] args, final int i) {
        if (isCustomColor(args[i])) {
            if (canProcessCustomColor(args, i)) {
                parseCustomColor(sessionInfo,
                        Integer.parseInt(args[i]),
                        Integer.parseInt(args[i + 2]));
            } else {
                LOGGER.warn("Missing costom color code: ESC["
                        + StringUtils.join(";", args) + "m");
            }
            return true;
        }

        return false;
    }

    private boolean isCustomColor(final String colorCode) {
        return colorCode.equals(Integer.toString(CUSTOM_FOREGROUND_COLOR))
                || colorCode.equals(Integer.toString(CUSTOM_BACKGROUND_COLOR));
    }

    private boolean canProcessCustomColor(final String[] args, final int i) {
        return i + 2 < args.length;
    }

    private void parseCustomColor(final SSHSession sessionInfo,
            final int colorCode, final int customColorCode) {
        final GfxCharSetup charSetup = sessionInfo.getCharSetup();
        final boolean isForeground = colorCode == CUSTOM_FOREGROUND_COLOR;
        final TerminalColor color = charSetup.getColorFactory()
                .createCustomColor(customColorCode, isForeground);

        if (isForeground) {
            charSetup.setForeground(color);
        } else {
            charSetup.setBackground(color);
        }
    }

    private void processPartialSequence(final SSHSession sessionInfo, final int seq) {
        if (seq == 0) {
            resetAttributes(sessionInfo);
        } else if (!processAttributes(sessionInfo, seq) &&
            !processDefaultColors(sessionInfo, seq)) {
                LOGGER.error("Unknown attribute: <ESC>[" + seq + "m");
        }
    }

    private void resetAttributes(final SSHSession sessionInfo) {
        sessionInfo.getCharSetup().reset();
        final GfxChar clearChar = sessionInfo.getCharSetup().createClearChar();
        sessionInfo.getBuffer().setClearChar(clearChar);
    }

    private boolean processDefaultColors(final SSHSession sessionInfo, final int seq) {
        final GfxCharSetup charSetup = sessionInfo.getCharSetup();
        final ColorFactory factory = charSetup.getColorFactory();
        final TerminalColor color = factory.createStandardColor(seq);

        if (color == null) {
            return false;
        }

        if (color.isForeground()) {
            charSetup.setForeground(color);
        } else {
            charSetup.setBackground(color);
            final GfxChar clearChar = charSetup.createClearChar();
            sessionInfo.getBuffer().setClearChar(clearChar);
        }
        return true;
    }

    private boolean processAttributes(final SSHSession sessionInfo,
            final int seq) {
        for (final Attribute attr : Attribute.values()) {
            if (attr.isActivateSequence(seq)) {
                sessionInfo.getCharSetup().setAttribute(attr);
                return true;
            } else if (attr.isDeactivateSequence(seq)) {
                sessionInfo.getCharSetup().removeAttribute(attr);
                return true;
            }
        }
        return false;
    }
}

package de.jowisoftware.sshclient.terminal;

public interface ColorResolver {
    int resolveColor(
            int colorId,
            boolean isSystemColor,
            boolean isForegroundColor);
}

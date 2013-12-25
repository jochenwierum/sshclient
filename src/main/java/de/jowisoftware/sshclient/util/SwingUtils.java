package de.jowisoftware.sshclient.util;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public final class SwingUtils {
    private static final Map<Character, Integer> VKMappings = new HashMap<>();

    static {
        VKMappings.put('a', KeyEvent.VK_A);
        VKMappings.put('b', KeyEvent.VK_B);
        VKMappings.put('c', KeyEvent.VK_C);
        VKMappings.put('d', KeyEvent.VK_D);
        VKMappings.put('e', KeyEvent.VK_E);
        VKMappings.put('f', KeyEvent.VK_F);
        VKMappings.put('g', KeyEvent.VK_G);
        VKMappings.put('h', KeyEvent.VK_H);
        VKMappings.put('i', KeyEvent.VK_I);
        VKMappings.put('j', KeyEvent.VK_J);
        VKMappings.put('k', KeyEvent.VK_K);
        VKMappings.put('l', KeyEvent.VK_L);
        VKMappings.put('m', KeyEvent.VK_M);
        VKMappings.put('n', KeyEvent.VK_N);
        VKMappings.put('o', KeyEvent.VK_O);
        VKMappings.put('p', KeyEvent.VK_P);
        VKMappings.put('q', KeyEvent.VK_Q);
        VKMappings.put('r', KeyEvent.VK_R);
        VKMappings.put('s', KeyEvent.VK_S);
        VKMappings.put('t', KeyEvent.VK_T);
        VKMappings.put('u', KeyEvent.VK_U);
        VKMappings.put('v', KeyEvent.VK_V);
        VKMappings.put('w', KeyEvent.VK_W);
        VKMappings.put('x', KeyEvent.VK_X);
        VKMappings.put('y', KeyEvent.VK_Y);
        VKMappings.put('z', KeyEvent.VK_Z);
        VKMappings.put('0', KeyEvent.VK_0);
        VKMappings.put('1', KeyEvent.VK_1);
        VKMappings.put('2', KeyEvent.VK_2);
        VKMappings.put('3', KeyEvent.VK_3);
        VKMappings.put('4', KeyEvent.VK_4);
        VKMappings.put('5', KeyEvent.VK_5);
        VKMappings.put('6', KeyEvent.VK_6);
        VKMappings.put('7', KeyEvent.VK_7);
        VKMappings.put('8', KeyEvent.VK_8);
        VKMappings.put('9', KeyEvent.VK_9);
    }

    private SwingUtils() {
        /* Util classes will not be instanciated */
    }

    public static void runInSwingThread(final Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (final InvocationTargetException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void runDelayedInSwingThread(final Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    public static void showMessage(final Window parent, final String message,
            final String title, final int messageType) {

        runInSwingThread(new Runnable() {
            @Override
            public void run() {
                //noinspection MagicConstant
                JOptionPane.showMessageDialog(parent, message, title,
                        messageType);
            }
        });
    }

    public static int charToVK(final char character) {
        if (VKMappings.containsKey(character)) {
            return VKMappings.get(character);
        } else {
            throw new IllegalArgumentException("The character '"
                    + character + "' is not mapped to a virtual key");
        }
    }
}

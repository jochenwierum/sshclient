package de.jowisoftware.ssh.client;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class App {
    public static void main(final String[] args) {
        try {
            final String nativeLF = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(nativeLF);
        } catch (final ClassNotFoundException e) {
        } catch (final InstantiationException e) {
        } catch (final IllegalAccessException e) {
        } catch (final UnsupportedLookAndFeelException e) {
        }

        new MainWindow();
    }
}

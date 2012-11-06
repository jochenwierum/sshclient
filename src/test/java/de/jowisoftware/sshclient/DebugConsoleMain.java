package de.jowisoftware.sshclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.apache.commons.io.IOUtils;

import de.jowisoftware.sshclient.settings.awt.AWTProfile;
import de.jowisoftware.sshclient.terminal.events.DisplayType;
import de.jowisoftware.sshclient.ui.SSHConsole;

public class DebugConsoleMain {
    public static void main(final String args[]) throws Exception {
        new DebugConsoleMain().run();
    }

    public void run() throws Exception {
        final InputStream stream = getClass().getClassLoader().getResourceAsStream("debug.txt");
        if (stream == null) {
            JOptionPane.showMessageDialog(null, "place debug.txt in src/test/resource (and re-run mvn package)");
            return;
        }

        final byte[] text = readFile(stream);
        final SSHConsole console = showFrame();

        (new Thread("network-simulator") {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
                console.gotChars(text, text.length);
            }
        }).start();
    }

    private SSHConsole showFrame() {
        final JFrame frame = new JFrame("test");
        final SSHConsole console = new SSHConsole(new AWTProfile());
        frame.add(console);
        startTimer(console);

        frame.setSize(630, 480);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        console.setOutputStream(System.out);
        console.setDisplayType(DisplayType.FIXED80X24);
        return console;
    }

    private void startTimer(final SSHConsole console) {
        final Timer timer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                console.redrawConsole();
            }
        });

        timer.setRepeats(true);
        timer.start();
    }

    private byte[] readFile(final InputStream stream) throws IOException {
        String text = IOUtils.toString(stream, "UTF-8");
        IOUtils.closeQuietly(stream);

        if (text.contains("\n---EOF---")) {
            text = text.substring(0, text.indexOf("\n---EOF---"));
        }
        text = text.replaceAll("\\n---[^\\n]+(?:\\n|$)", "");
        text = text.replace("\n", "").replace("\r", "");

        final StringBuffer builder = new StringBuffer();
        final Matcher m = Pattern.compile("(\\\\(\\\\|n|r|u[0-9a-fA-F]{4}))").matcher(text);
        while(m.find()) {
            final String g = m.group(2);
            final String rep;
            if (g.equals("n")) {
                rep = "\n";
            } else if (g.equals("r")) {
                rep = "\r";
            } else if (g.equals("\\")) {
                rep = "\\";
            } else {
                rep = Character.toString((char) Integer.parseInt(g.substring(1).toUpperCase(), 16));
            }
            m.appendReplacement(builder, rep.replace("\\", "\\\\"));
        }
        m.appendTail(builder);
        return builder.toString().getBytes("UTF-8");
    }
}

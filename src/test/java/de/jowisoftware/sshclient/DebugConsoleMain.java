package de.jowisoftware.sshclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import com.google.common.io.Closeables;

import de.jowisoftware.sshclient.settings.Profile;
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

        final String text = readFile(stream);
        final SSHConsole console = showFrame(text);
        console.gotChars(text.getBytes(), text.getBytes().length);
    }

    private SSHConsole showFrame(final String text) {
        final JFrame frame = new JFrame("test");
        final SSHConsole console = new SSHConsole(new Profile());
        frame.add(console);
        startTimer(console);

        frame.setSize(630, 480);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        console.setOutputStream(System.out);
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

    private String readFile(final InputStream stream) throws IOException {
        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, Charset.forName("UTF-8")));
        final String text = getStreamContent(reader)
                .replace("\n", "").replace("\r", "");
        Closeables.closeQuietly(reader);

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
        String result = builder.toString();
        if (result.contains("---EOF---")) {
            result = result.substring(0, result.indexOf("---EOF---"));
        }
        return result;
    }

    private String getStreamContent(final BufferedReader reader) throws IOException {
        final StringBuilder builder = new StringBuilder();
        String line = reader.readLine();
        while(line != null) {
            builder.append(line);
            line = reader.readLine();
        }
        return builder.toString();
    }
}

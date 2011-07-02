package de.jowisoftware.ssh.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.CharacterCodingException;

import javax.swing.JFrame;
import javax.swing.Timer;

import de.jowisoftware.ssh.client.ui.SSHConsole;

public class App2 {
    public static void main(final String args[]) throws CharacterCodingException {
        final JFrame frame = new JFrame("test");
        final SSHConsole console = new SSHConsole(new ConnectionInfo());
        frame.add(console);

        final Timer timer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                console.redrawConsole();
            }
        });

        timer.setRepeats(true);
        timer.start();

        final String text="Last login: Mon Jun 27 18:22:49 2011 from 192.168.0.98" +
                "\n\nEnvironment:\n  USER=gast\n  LOGNAME=gast\n  HOME=/home/" +
                "gast\n  PATH=/usr/local/bin:/usr/bin:/bin:/usr/bin/X11:/usr/" +
                "games\n  MAIL=/var/mail/gast\n  SHELL=/bin/bash\n  SSH_CLIEN" +
                "T=192.168.0.98 40133 223\n  SSH_CONNECTION=192.168.0.98 4013" +
                "3  192.168.0.1 223\n  SSH_TTY=/dev/pts/1\n  TERM=vt100\n\n" +
                "\u001b[0;5;36mgast\u001b[1;37m@\u001b[0;33mjowi\u001b[1;37m:" +
                "\u001b[1;34m~\u001b[1;37m$ \u001b[0m\n";

        frame.setSize(630, 480);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        console.gotChars(text.getBytes(), text.getBytes().length);
        console.setOutputStream(System.out);
    }
}

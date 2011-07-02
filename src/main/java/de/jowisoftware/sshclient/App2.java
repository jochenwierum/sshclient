package de.jowisoftware.sshclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.CharacterCodingException;

import javax.swing.JFrame;
import javax.swing.Timer;

import de.jowisoftware.sshclient.ui.SSHConsole;

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

        final String text=
            "\u001b[0;36mjwieru2s\u001b[0m\u001b[1;37m@\u001b[0;33mux-2s05\u001b[1;37m:\u001b[1;34m~\u001b[1;37m$\u001b[0m \r\u001b[0;36mjwieru2s\u001b[0m\u001b[1;37m@\u001b[0;33mux-2s05\u001b[1;37m:\u001b[1;34m~\u001b[1;37m$\u001b[0m\r\u001b[0;36mjwieru2s\u001b[0m\u001b[1;37m@\u001b[0;33mux-2s05\u001b[1;37m:\u001b[1;34m~\u001b[1;37m$\u001b[0m v\r\u001b[0;36mjwieru2s\u001b[0m\u001b[1;37m@\u001b[0;33mux-2s05\u001b[1;37m:\u001b[1;34m~\u001b[1;37m$\u001b[0m \r\u001b[0;36mjwieru2s\u001b[0m\u001b[1;37m@\u001b[0;33mux-2s05\u001b[1;37m:\u001b[1;34m~\u001b[1;37m$\u001b[0m v\r\u001b[0;36mjwieru2s\u001b[0m\u001b[1;37m@\u001b[0;33mux-2s05\u001b[1;37m:\u001b[1;34m~\u001b[1;37m$\u001b[0m \r\u001b[0;36mjwieru2s\u001b[0m\u001b[1;37m@\u001b[0;33mux-2s05\u001b[1;37m:\u001b[1;34m~\u001b[1;37m$\u001b[0m vi\r" +
            "\u001b[0;36mjwieru2s\u001b[0m\u001b[1;37m@\u001b[0;33mux-2s05\u001b[1;37m:\u001b[1;34m~\u001b[1;37m$\u001b[0m \r" +
            "\u001b[0;36mjwieru2s\u001b[0m\u001b[1;37m@\u001b[0;33mux-2s05\u001b[1;37m:\u001b[1;34m~\u001b[1;37m$\u001b[0m vi" +
            "\u001b[?1h" +
            "\u001b=" +
            "\u001b[1;24r" +
            "\u001b[m" +
            "\u001b[m" +
            "\u001b[0m" +
            "\u001b[H\u001b[J" +
            "\u001b[2;1H\u001b[1m~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~\r\n~" +
            "\u001b[0m\r\n\u001b[7m[No Name] [F=unix] [T=] [A=000] [H=00] [P=\u001b[0m0000\u001b[7m,0001][100%] [L=1]                " +
            "\u001b[23;1H[No Name] [F=unix] [T=] [A=000] [H=00] [P=\u001b[0m0000\u001b[7m,0001][100%] [L=1]                " +
            "\u001b[1;1H\u001b[0m\u001b[24;70H:         \u001b[1;1H\u001b[24;70H\u001b[K\u001b[24;1H:" +

            "";


        frame.setSize(630, 480);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        console.gotChars(text.getBytes(), text.getBytes().length);
        console.setOutputStream(System.out);
    }
}

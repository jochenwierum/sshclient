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
            "\r\n" +
            "\u001b[?1h\u001b=\u001b[1;24r\u001b[m\u001b[m\u001b[0m\u001b[H\u001b[J\u001b[24;1H\".bashrc\" \r\".bashrc\" 145L, 3191C" +
            "\u001b[1;1H\u001b[1m#xterm*|rxvt*)\r\n#    PROMPT_COMMAND='echo -ne \"\\033]0;${USER}@${HOSTNAME}: " +
            "${PWD/$HOME/~}\\007\"'\r\n#    ;;\r\n#*)\r\n#    ;;\r\n#esac\u001b[0m\r\n\r\n_promptcommand\u001b[1m()" +
            "\r\n{\r\n\u001b[0m    \u001b[1mcase\u001b[0m \u001b[4m$TERM\u001b[m \u001b[1min\u001b[0m\u001b[11;9Hxterm" +
            "*|rxvt|Eterm|eterm\u001b[1m)\u001b[0m\u001b[12;13H\u001b[4mPROMPT_COMMAND\u001b[m\u001b[1m='\u001b[0m\u001b" +
            "[4mhistory -a && echo -ne \"\\033]0;${USER}@              \u001b[13;1H${HOSTNAME%%.*}:${PWD/$HOME/~}\\007\"" +
            "\u001b[m\u001b[1m'\u001b[14;13H;;\u001b[0m\u001b[15;9Hscreen\u001b[1m)\u001b[0m\u001b[16;13H\u001b[4mPROMPT_" +
            "COMMAND\u001b[m\u001b[1m='\u001b[0m\u001b[4mecho -ne \"\\033_${USER}@${HOSTNAME%%.*}:${PWD/$HOME/  \u001b[17;" +
            "1H~}\\033\\\\\"\u001b[m\u001b[1m'\u001b[18;13H;;\r\n\u001b[0m    \u001b[1mesac\r\n}\r\n\r\n# we do not want" +
            " coredumps\u001b[0m\r\n\u001b[7m~/.bashrc [F=unix] [T=SH] [A=120] [H=78] [P=\u001b[0m0044\u001b[7m,0009][30%]" +
            " [L=145]             \u001b[23;1H~/.bashrc [F=unix] [T=SH] [A=120] [H=78] [P=\u001b[0m0044\u001b[7m,0009]" +
            "[30%] [L=145]             \u001b[11;9H" +

            "\u001b[0m\u001b[24;70H^[        \u001b[11;9H\u001b[24;70H          \u001b[11;9H\u001b[24;70H^[        \u001b[11;9H\u001b[24;70H^[O       \u001b[11;9H\u001b[24;70H          \u001b[11;9H\u001b[24;70H~@k       \u001b[11;9H\u001b[24;70H          \u001b[12;12H\u001b[23;1H\u001b[7m~/.bashrc [F=unix] [T=SH] [A=009] [H=09] [P=\u001b[0m0045\u001b[7m,0012][31%] [L=145]             \u001b[12;12H"+
            "\u001b[0m\u001b[24;70H^[        \u001b[12;12H\u001b[24;70H          \u001b[12;12H\u001b[24;70H^[        \u001b[12;12H\u001b[24;70H^[O       \u001b[12;12H\u001b[24;70H          \u001b[12;12H\u001b[24;70H~@k       \u001b[12;12H\u001b[24;70H          \u001b[14;12H\u001b[23;1H\u001b[7m~/.bashrc [F=unix] [T=SH] [A=009] [H=09] [P=\u001b[0m0046\u001b[7m,0012][31%] [L=145]             \u001b[14;12H"+
            "\u001b[0m\u001b[24;70H^[        \u001b[14;12H\u001b[24;70H          \u001b[14;12H\u001b[24;70H^[        \u001b[14;12H\u001b[24;70H^[O       \u001b[14;12H"+
            "\u001b[24;70H          \u001b[14;12H\u001b[24;70H~@k       \u001b[14;12H\u001b[24;70H          " +
            "\u001b[15;9H\u001b[23;1H\u001b[7m~/.bashrc [F=unix] [T=SH] [A=115] [H=73] [P=\u001b[0m0047\u001b[7m,0009][32%] [L=145]             \u001b[15;9H" +
            "\u001b[0m\u001b[24;70H^[        \u001b[15;9H\u001b[24;70H          \u001b[15;9H\u001b[24;70H^[        \u001b[15;9H\u001b[24;70H^[O       \u001b[15;9H\u001b[24;70H          \u001b[15;9H\u001b[24;70H~@k       \u001b[15;9H\u001b[24;70H          \u001b[16;12H\u001b[23;1H\u001b[7m~/.bashrc [F=unix] [T=SH] [A=009] [H=09] [P=\u001b[0m0048\u001b[7m,0012][33%] [L=145]             \u001b[16;12H" +
            "\u001b[0m\u001b[24;70H^[        \u001b[16;12H\u001b[24;70H          \u001b[16;12H\u001b[24;70H^[        \u001b[16;12H\u001b[24;70H^[O       \u001b[16;12H\u001b[24;70H          \u001b[16;12H\u001b[24;70H~@k       \u001b[16;12H\u001b[24;70H          \u001b[18;12H\u001b[23;1H\u001b[7m~/.bashrc [F=unix] [T=SH] [A=009] [H=09] [P=\u001b[0m0049\u001b[7m,0012][33%] [L=145]             \u001b[18;12H" +
            "\u001b[0m\u001b[24;70H^[        \u001b[18;12H\u001b[24;70H          \u001b[18;12H\u001b[24;70H^[        \u001b[18;12H\u001b[24;70H^[O       \u001b[18;12H\u001b[24;70H          \u001b[18;12H\u001b[24;70H~@k       \u001b[18;12H\u001b[24;70H          \u001b[19;8H\u001b[23;1H\u001b[7m~/.bashrc [F=unix] [T=SH] [A=099] [H=63] [P=\u001b[0m0050\u001b[7m,0008][34%] [L=145]             \u001b[19;8H" +



            "\u001b[0m\u001b[24;70H^[        " +
            "\u001b[19;8H\u001b[24;70H          " +
            "\u001b[19;8H\u001b[24;70H^[        " +
            "\u001b[19;8H\u001b[24;70H^[O       " +
            "\u001b[19;8H\u001b[24;70H          " +
            "\u001b[19;8H\u001b[24;70H~@k       " +
            "\u001b[19;8H\u001b[24;70H          " +

            "\u001b[19;1H\u001b[1;22r" +
            "\u001b[22;1H\r\n" +
            "\u001b[1;24r\u001b[8;1H\u001b[1m\u001b[7m{\u001b[19;1H}\u001b[0m\r\n\r\n\r\n\u001b[1mulimit\u001b[0m \u001b[1m-S\u001b[0m \u001b[1m-c\u001b[0m \u001b[4m0\u001b[m\u001b[24;1H\u001b[K\u001b[23;1H\u001b[7m~/.bashrc [F=unix] [T=SH] [A=125] [H=7D] [P=\u001b[0m0051\u001b[7m,0001][35%] [L=145]             \u001b[19;1H" +
//            "\u001b[0m\u001b[24;70H^[        \u001b[19;1H\u001b[24;70H          \u001b[19;1H\u001b[24;70H^[        \u001b[19;1H\u001b[24;70H^[O       \u001b[19;1H"
//            "\u001b[24;70H          \u001b[19;1H\u001b[24;70H~@k       \u001b[19;1H\u001b[24;70H          \u001b[19;1H\u001b[1;22r\u001b[22;1H\r\n\u001b[1;24r\u001b[7;1H\u001b[1m{\u001b[18;1H}\u001b[0m\u001b[23;1H\u001b[7m~/.bashrc [F=unix] [T=SH] [A=000] [H=00] [P=\u001b[0m0052\u001b[7m,0001][35%] [L=145]             \u001b[19;1H"
//            "\u001b[0m\u001b[24;70H^[        \u001b[19;1H\u001b[24;70H          \u001b[19;1H\u001b[24;70H^[        \u001b[19;1H\u001b[24;70H^[O       \u001b[19;1H\u001b[24;70H          \u001b[19;1H\u001b[24;70H~@k       \u001b[19;1H\u001b[24;70H          \u001b[19;9H\u001b[1;22r\u001b[22;1H\r\n\u001b[1;24r\u001b[22;1H\u001b[1mif\u001b[0m \u001b[1m[\u001b[0m \u001b[1m-f\u001b[0m /etc/bash_aliases \u001b[1m];\u001b[0m \u001b[1mthen\u001b[0m\r\n\u001b[7m~/.bashrc [F=unix] [T=SH] [A=110] [H=6E] [P=\u001b[0m0053\u001b[7m,0009][36%] [L=145]             \u001b[19;9H"
            "";


        frame.setSize(630, 480);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        console.gotChars(text.getBytes(), text.getBytes().length);
        console.setOutputStream(System.out);
    }
}

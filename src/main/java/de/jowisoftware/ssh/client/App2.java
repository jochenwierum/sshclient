package de.jowisoftware.ssh.client;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

public class App2 {
    public static void main(final String args[]) throws CharacterCodingException {
        final ByteBuffer temp = Charset.forName("UTF-8").encode(CharBuffer.wrap("Ä"));
        System.out.println(temp.get());
        System.out.println(temp.get());


        final ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) -61);
        final ByteBuffer b2 = buffer.duplicate(); b2.flip();
        //final CharBuffer result = Charset.forName("UTF-8").decode(b2);

        final CharsetDecoder dec = Charset.forName("UTF-8").newDecoder();
        final CharBuffer result = CharBuffer.allocate(2);
        final CoderResult tmp = dec.decode(b2, result, false);

        System.out.println(b2.remaining());
        /*
        buffer.put((byte) 65);
        b2 = buffer.duplicate(); b2.flip();
        result = Charset.forName("UTF-8").decode(b2);
        */

        result.flip();
        System.out.println(result.toString());

        /*
        final Pattern pattern = Pattern.compile("test");
        final Matcher matcher = pattern.matcher("tes");
        System.out.println(matcher.matches());
        System.out.println(matcher.end());
        /*
        final JFrame frame = new JFrame("test");
        final SSHConsole console = new SSHConsole(new ConnectionInfo(null));
        frame.add(console);

        final String text="Last login: Mon Jun 27 18:22:49 2011 from 192.168.0.98" +
                "\n\nEnvironment:\n  USER=gast\n  LOGNAME=gast\n  HOME=/home/" +
                "gast\n  PATH=/usr/local/bin:/usr/bin:/bin:/usr/bin/X11:/usr/" +
                "games\n  MAIL=/var/mail/gast\n  SHELL=/bin/bash\n  SSH_CLIEN" +
                "T=192.168.0.98 40133 223\n  SSH_CONNECTION=192.168.0.98 4013" +
                "3  192.168.0.1 223\n  SSH_TTY=/dev/pts/1\n  TERM=vt100\n\n" +
                "\u001b[0;36mgast\u001b[1;37m@\u001b[0;33mjowi\u001b[1;37m:" +
                "\u001b[1;34m~\u001b[1;37m$ \u001b[0m\n";

        frame.setSize(630, 480);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        console.gotChars(text.getBytes(), text.getBytes().length);
        */
    }
}

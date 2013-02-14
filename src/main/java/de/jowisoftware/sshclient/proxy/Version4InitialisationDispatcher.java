package de.jowisoftware.sshclient.proxy;

import java.io.UnsupportedEncodingException;

public class Version4InitialisationDispatcher implements SocksDispatcher {
    private final int OUTGOING_CONNECTION = 1;

    private final byte ip[] = new byte[4];
    private final byte port[] = new byte[2];
    private int pos;

    private String dnsName = "";

    private final ConfigurableSocksByteProcessor processor;

    public Version4InitialisationDispatcher(
            final ConfigurableSocksByteProcessor processor) {
        this.processor = processor;
    }

    @Override
    public byte[] process(final byte c) {
        switch (pos) {
        case 0:
            if (c != OUTGOING_CONNECTION) {
                throw new IllegalArgumentException(
                        "Only outgoing requests are supported, got: " + c);
            }
            break;

        case 1:
        case 2:
            port[pos - 1] = c;
            break;

        case 3:
        case 4:
        case 5:
        case 6:
            ip[pos - 3] = c;
            break;

        case 7:
            if (c != 0) {
                throw new IllegalArgumentException(
                        "Only anonymous connections are supported, got: " + c);
            }

            if (!dnsNameFollows()) {
                return setupConnection();
            }
            break;

        case 8:

            if (c == 0) {
                return setupConnection();
            } else {
                try {
                    dnsName += new String(new byte[] { c }, "ISO-8859-1");
                    pos -= 1;
                } catch (final UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

            break;
        }

        pos += 1;
        return new byte[0];
    }

    private boolean dnsNameFollows() {
        return ip[0] == 0 && ip[1] == 0 && ip[2] == 0 && ip[3] != 0;
    }

    private byte[] setupConnection() {
        if (dnsNameFollows()) {
            processor.finishSetup(dnsName, port[0] * 256 + port[1]);
            return new byte[] { 0, 90, port[0], port[1], (byte) 127, 0, 0, 1 };
        } else {
            processor.finishSetup(ip[0] + "." + ip[1] + "." + ip[2] + "."
                    + ip[3], port[0] * 256 + port[1]);
            return new byte[] { 0, 90, port[0], port[1], ip[0], ip[1], ip[2],
                    ip[3] };
        }
    }
}

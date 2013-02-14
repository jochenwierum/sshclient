package de.jowisoftware.sshclient.proxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;

public class Version5InitialisationTargetDispatcher implements SocksDispatcher {
    private static enum Position {
        VERSION, CMD, RESERVED, ADRESS_TYPE, DESTINATION_VALUE, DESTINATION_PORT1, DESTINATION_PORT2
    }

    private static enum AddressType {
        IPV4((byte) 1), IPV6((byte) 4), DNS((byte) 3);

        public final byte value;

        private AddressType(final byte value) {
            this.value = value;
        }

        public static AddressType find(final byte value) {
            for (final AddressType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return null;
        }
    }

    private static final byte COMMAND_OUTGOING_TCP = 1;
    private static final byte REQUIRED_VERSION = 5;
    private static final byte RESERVED = 0;
    private static final byte REPLY_SUCCESS = 0x00;

    private final ConfigurableSocksByteProcessor processor;

    private Position pos = Position.VERSION;

    private AddressType addressType;
    private byte[] address = null;
    private int addressPos = 0;

    private int port;

    public Version5InitialisationTargetDispatcher(
            final ConfigurableSocksByteProcessor processor) {
        this.processor = processor;
    }

    @Override
    public byte[] process(final byte c) {
        byte[] answer = new byte[0];

        switch (pos) {
        case VERSION:
            processVersionByte(c);
            break;

        case CMD:
            processCommandByte(c);
            break;

        case RESERVED:
            processReservedByte(c);
            break;

        case ADRESS_TYPE:
            processAddressTypeByte(c);
            break;

        case DESTINATION_VALUE:
            processAdressByte(c);
            break;

        case DESTINATION_PORT1:
            processDestinationPortByte1(c);
            break;

        case DESTINATION_PORT2:
            processDestionationPortType2(c);
            try {
                answer = finishSetup();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            break;
        }

        return answer;
    }

    private void processVersionByte(final byte c) {
        if (c != REQUIRED_VERSION) {
            throw new IllegalArgumentException("Unsupported verion: "
                    + (int) c);
        }

        pos = Position.CMD;
    }

    private void processCommandByte(final byte c) {
        if (c != COMMAND_OUTGOING_TCP) {
            throw new IllegalArgumentException(
                    "Only outgoing TCP Connections are supported");
        }

        pos = Position.RESERVED;
    }

    private void processReservedByte(final byte c) {
        if (c != RESERVED) {
            throw new IllegalArgumentException("Reserved byte must be \0");
        }

        pos = Position.ADRESS_TYPE;
    }

    private void processAddressTypeByte(final byte c) {
        addressType = AddressType.find(c);
        if (addressType == null) {
            throw new IllegalArgumentException("Adress type unsupported: " + c);
        }

        pos = Position.DESTINATION_VALUE;
    }

    private void processAdressByte(final byte c) {
        boolean consume = true;

        if (address == null) {
            switch (addressType) {
            case DNS:
                address = new byte[c];
                consume = false;
                break;
            case IPV4:
                address = new byte[4];
                break;
            case IPV6:
                address = new byte[16];
                break;
            }
        }

        if (consume) {
            address[addressPos] = c;
            ++addressPos;
        }

        if (addressPos == address.length) {
            pos = Position.DESTINATION_PORT1;
        }
    }

    private void processDestinationPortByte1(final byte c) {
        port = c * 256;
        pos = Position.DESTINATION_PORT2;
    }

    private void processDestionationPortType2(final byte c) {
        port += c;
    }

    private byte[] finishSetup() throws IOException {
        try {
            processor.finishSetup(createAdressString(), port);
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return createAnswer();
    }

    private String createAdressString() throws IOException {
        switch (addressType) {
        case IPV4:
        case IPV6:
            return InetAddress.getByAddress(address).getHostAddress();
        default:
            return new String(address, "ISO-8859-1");
        }
    }

    private byte[] createAnswer() {
        final byte result[];
        final int offset;

        if (addressType != AddressType.DNS) {
            result = new byte[6 + address.length];
            offset = 4;
        } else {
            result = new byte[7 + address.length];
            offset = 5;
        }

        result[0] = REQUIRED_VERSION;
        result[1] = REPLY_SUCCESS;
        result[2] = RESERVED;
        result[3] = addressType.value;

        if (addressType == AddressType.DNS) {
            result[4] = (byte) address.length;
        }

        for (int i = 0; i < address.length; ++i) {
            result[i + offset] = address[i];
        }

        result[result.length - 2] = (byte) ((port & 0xff00) >> 8);
        result[result.length - 1] = (byte) (port & 0xff);

        return result;
    }
}

package de.jowisoftware.sshclient.proxy;

public class Version5InitialisationDispatcher implements SocksDispatcher {
    private int pos;
    private int currentAuthCount = 0;
    private byte[] authMethods;
    private final ConfigurableSocksByteProcessor processor;

    public Version5InitialisationDispatcher(
            final ConfigurableSocksByteProcessor processor) {
        this.processor = processor;
    }

    @Override
    public byte[] process(final byte c) {
        switch (pos) {
        case 0:
            authMethods = new byte[c];

            if (c == 0) {
                throw new IllegalArgumentException(
                        "At least one authentification method is needed");
            }

            ++pos;
            break;
        case 1:
            authMethods[currentAuthCount] = c;
            ++currentAuthCount;

            if (currentAuthCount == authMethods.length) {
                boolean found = false;
                for (final byte method : authMethods) {
                    if (method == 0) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    throw new IllegalStateException("No supported authentification method could be found");
                } else {
                    ++pos;

                    processor
                            .setNextDispatcher(new Version5InitialisationTargetDispatcher(
                                    processor));
                    return new byte[] { 5, 0 };
                }
            }
            break;
        }

        return new byte[0];
    }
}

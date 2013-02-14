package de.jowisoftware.sshclient.proxy;

public class VersionSelectionDispatcher implements SocksDispatcher {
    private final ConfigurableSocksByteProcessor processor;

    public VersionSelectionDispatcher(
            final ConfigurableSocksByteProcessor processor) {
        this.processor = processor;
    }

    @Override
    public byte[] process(final byte c) {
        if (c == 4) {
            final SocksDispatcher next = new Version4InitialisationDispatcher(
                    processor);
            processor.setNextDispatcher(next);
        } else if (c == 5) {
            final SocksDispatcher next = new Version5InitialisationDispatcher(
                    processor);
            processor.setNextDispatcher(next);
        } else {
            throw new IllegalArgumentException("Unexpected Version: " + c);
        }

        return new byte[0];
    }
}

package de.jowisoftware.sshclient.terminal.controlsequences;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class DefaultSequenceRepositoryTest {
    @Test
    public void testInvalidSequence() {
        assertNotNull(
                new DefaultSequenceRepository().getANSISequence('?'));
    }
}

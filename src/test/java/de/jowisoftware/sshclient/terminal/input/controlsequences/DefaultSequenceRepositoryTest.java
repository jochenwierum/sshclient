package de.jowisoftware.sshclient.terminal.input.controlsequences;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.jowisoftware.sshclient.terminal.input.controlsequences.DefaultSequenceRepository;

public class DefaultSequenceRepositoryTest {
    @Test
    public void testInvalidSequence() {
        assertNotNull(
                new DefaultSequenceRepository().getANSISequence('?'));
    }
}

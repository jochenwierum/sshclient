package de.jowisoftware.sshclient.terminal.controlsequences;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.jowisoftware.sshclient.terminal.buffer.GfxChar;
import de.jowisoftware.sshclient.terminal.controlsequences.DefaultSequenceRepository;

public class DefaultSequenceRepositoryTest {
    @Test
    public void testInvalidSequence() {
        assertNotNull(
                new DefaultSequenceRepository<GfxChar>().getANSISequence('?'));
    }
}

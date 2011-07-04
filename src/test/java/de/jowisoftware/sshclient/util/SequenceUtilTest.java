package de.jowisoftware.sshclient.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.jowisoftware.sshclient.util.SequenceUtils;

public class SequenceUtilTest {
    @Test
    public void testInvalidSequence() {
        assertNotNull(SequenceUtils.getANSISequence('?'));
    }
}

package chapter05.hashTable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VoterRegistryTest {

    @Test
    void firstTimeAllowed_repeatRejected() {
        VoterRegistry registry = new VoterRegistry(8);
        assertTrue(registry.tryRegister("Tom"));
        assertFalse(registry.tryRegister("Tom"));
        assertTrue(registry.tryRegister("Jerry"));
        assertFalse(registry.tryRegister("Tom"));
    }
}

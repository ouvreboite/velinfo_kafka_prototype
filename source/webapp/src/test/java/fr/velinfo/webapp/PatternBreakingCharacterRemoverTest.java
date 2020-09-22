package fr.velinfo.webapp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatternBreakingCharacterRemoverTest {

    @Test
    void strip_shouldRemoveBreakingCharacters() {
        assertEquals("a_b_c_d", PatternBreakingCharacterRemover.strip("a\nb\rc\td"));
    }
}
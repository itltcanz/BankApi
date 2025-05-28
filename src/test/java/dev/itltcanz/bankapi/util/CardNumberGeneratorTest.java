package dev.itltcanz.bankapi.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardNumberGeneratorTest {
    @Test
    void calculateCheckDigit() {
        assertEquals("7", CardNumberGenerator.calculateCheckDigit("456126121234546"));
        assertEquals("2", CardNumberGenerator.calculateCheckDigit("506282123456789"));
        assertEquals("2", CardNumberGenerator.calculateCheckDigit("5062821234567892"));
        assertEquals("6", CardNumberGenerator.calculateCheckDigit("50628212345678922"));
        assertEquals("7", CardNumberGenerator.calculateCheckDigit("506282173456789"));
        assertEquals("9", CardNumberGenerator.calculateCheckDigit("400000123456789"));
        assertEquals("3", CardNumberGenerator.calculateCheckDigit("456126121234548"));
        assertEquals("0", CardNumberGenerator.calculateCheckDigit("451126124594549"));
        assertThrows(IllegalArgumentException.class, () -> CardNumberGenerator.calculateCheckDigit("400000abc"));
        assertThrows(IllegalArgumentException.class, () -> CardNumberGenerator.calculateCheckDigit(null));
    }
}

package dev.itltcanz.bankapi.util;

import java.security.SecureRandom;

public class CardNumberGenerator {

    private static final String BANK_IIN = "400000";
    private static final int MAX_ACCOUNT_NUMBER = 999999999;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generate the card number.
     * @return A card number as a string.
     */
    public static String generateCardNumber() {
        var cardNumber = BANK_IIN + String.format("%09d", random.nextInt(MAX_ACCOUNT_NUMBER) + 1);
        return cardNumber + calculateCheckDigit(cardNumber);
    }

    /**
     * Calculates the check digit for the card number using the Luhn algorithm.
     *
     * @param cardNumber The card number without a check digit (digits only).
     * @return A check digit as a string.
     * @throws IllegalArgumentException If the number contains non-numeric characters or null.
     */
    public static String calculateCheckDigit(String cardNumber) {
        if (cardNumber == null || !cardNumber.matches("\\d+")) {
            throw new IllegalArgumentException("The card number must contain only numbers.");
        }
        var sum = 0;
        var isEven = true;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            var number = cardNumber.charAt(i) - '0';
            if (isEven) {
                number *= 2;
                if (number > 9) number -= 9;
            }
            sum += number;
            isEven = !isEven;
        }
        return String.valueOf((10 - sum % 10) % 10);
    }
}

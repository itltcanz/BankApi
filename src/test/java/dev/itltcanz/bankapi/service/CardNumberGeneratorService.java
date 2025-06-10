package dev.itltcanz.bankapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.itltcanz.bankapi.repository.CardRepo;
import dev.itltcanz.bankapi.service.impl.CardNumberGeneratorServiceImpl;
import dev.itltcanz.bankapi.util.CardNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CardNumberGeneratorServiceTest {

  @Mock
  private CardRepo cardRepo;

  @InjectMocks
  private CardNumberGeneratorServiceImpl cardNumberGeneratorService;

  @BeforeEach
  void setUp() {
    // Mock static method in CardNumberGenerator
    try (var mockedStatic = mockStatic(CardNumberGenerator.class)) {
      mockedStatic.when(CardNumberGenerator::generateCardNumber).thenReturn("4000001234567890");
    }
  }

  @Test
  void generateCardNumber_uniqueNumber_success() {
    when(cardRepo.existsByNumber(anyString())).thenReturn(false);

    String cardNumber = cardNumberGeneratorService.generateCardNumber();

    // Проверяем формат номера
    assertNotNull(cardNumber);
    assertEquals(16, cardNumber.length());
    assertTrue(cardNumber.startsWith("400000"));
    assertTrue(cardNumber.matches("\\d+")); // Только цифры

    // Проверяем валидность по алгоритму Луна
    String checkDigit = CardNumberGenerator.calculateCheckDigit(cardNumber.substring(0, 15));
    assertEquals(cardNumber.substring(15), checkDigit);

    verify(cardRepo).existsByNumber(anyString());
  }

  @Test
  void generateCardNumber_numberExists_triesAgain() {
    when(cardRepo.existsByNumber("4000001234567890")).thenReturn(true, false);

    try (var mockedStatic = mockStatic(CardNumberGenerator.class)) {
      mockedStatic.when(CardNumberGenerator::generateCardNumber)
          .thenReturn("4000001234567890", "4000009876543210");

      String cardNumber = cardNumberGeneratorService.generateCardNumber();

      assertEquals("4000009876543210", cardNumber);
      verify(cardRepo, times(2)).existsByNumber(anyString());
    }
  }
}

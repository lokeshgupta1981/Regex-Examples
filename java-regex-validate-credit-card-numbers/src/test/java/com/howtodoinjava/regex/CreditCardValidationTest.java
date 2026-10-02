package com.howtodoinjava.regex;

import static com.howtodoinjava.regex.CreditCardValidation.CARD;
import static com.howtodoinjava.regex.CreditCardValidation.OLD_CARD;
import static com.howtodoinjava.regex.CreditCardValidation.brand;
import static com.howtodoinjava.regex.CreditCardValidation.digitsOnly;
import static com.howtodoinjava.regex.CreditCardValidation.isValidCardNumber;
import static com.howtodoinjava.regex.CreditCardValidation.passesLuhn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Card numbers are Cybersource sandbox test numbers. Prefix-boundary tests use
 * prefix-plus-zeros strings that only exercise the regex; they are not card numbers.
 */
class CreditCardValidationTest {

  @ParameterizedTest
  @CsvSource({
      "4111111111111111, visa",
      "4622943127013705, visa",
      "5555555555554444, mastercard",
      "2222420000001113, mastercard",
      "2222630000001125, mastercard",
      "378282246310005, amex",
      "6011111111111117, discover",
      "3566111111111113, jcb"})
  void publishedTestCardsMatchTheirBrandAndPassLuhn(String number, String expectedBrand) {
    assertEquals(Optional.of(expectedBrand), brand(number));
    assertTrue(passesLuhn(number));
    assertTrue(isValidCardNumber(number));
  }

  @ParameterizedTest
  @CsvSource({
      "2221000000000000, mastercard", "2720990000000000, mastercard", "5100000000000000, mastercard",
      "6440000000000000, discover", "6500000000000000000, discover",
      "36000000000000, diners", "30500000000000, diners",
      "3528000000000000, jcb", "3589000000000000000, jcb",
      "4000000000000, visa", "4000000000000000000, visa"})
  void prefixBoundariesMatch(String digits, String expectedBrand) {
    assertEquals(Optional.of(expectedBrand), brand(digits));
  }

  @ParameterizedTest
  @CsvSource({"2220990000000000", "2721000000000000", "5600000000000000", "3527000000000000",
      "3590000000000000", "30600000000000", "40000000000000", "312345678901", "0000000000000000"})
  void prefixesOutsideTheRangesDoNotMatch(String digits) {
    assertEquals(Optional.empty(), brand(digits));
  }

  @Test
  void oldPatternProblems() {
    assertFalse(OLD_CARD.matcher("2222420000001113").matches());
    assertTrue(CARD.matcher("2222420000001113").matches());
    assertTrue(OLD_CARD.matcher("312345678901").matches());
    assertFalse(CARD.matcher("312345678901").matches());
    assertTrue(OLD_CARD.matcher("6011111111111117").matches());
  }

  @Test
  void cleansInput() {
    assertEquals(Optional.of("4111111111111111"), digitsOnly("4111 1111 1111 1111"));
    assertEquals(Optional.of("4111111111111111"), digitsOnly("4111-1111-1111-1111"));
    assertEquals(Optional.of("378282246310005"), digitsOnly(" 3782 822463 10005 "));
    assertEquals(Optional.empty(), digitsOnly("4111.1111.1111.1111"));
    assertEquals(Optional.empty(), digitsOnly("4111 1111 1111 111O"));
    assertEquals(Optional.empty(), digitsOnly(null));
  }

  @Test
  void luhn() {
    assertTrue(passesLuhn("378282246310005"));
    assertFalse(passesLuhn("378282246310006"));
    assertFalse(passesLuhn("4111111111111112"));
    assertFalse(passesLuhn("4111111111111121"));
    assertTrue(passesLuhn("0000000000000000"));
  }

  @Test
  void luhnDetectsEverySingleDigitChange() {
    String number = "4111111111111111";
    for (int pos = 0; pos < number.length(); pos++) {
      for (char d = '0'; d <= '9'; d++) {
        if (d != number.charAt(pos)) {
          String changed = number.substring(0, pos) + d + number.substring(pos + 1);
          assertFalse(passesLuhn(changed), changed);
        }
      }
    }
  }

  @Test
  void combinedCheck() {
    assertTrue(isValidCardNumber("4111 1111 1111 1111"));
    assertTrue(isValidCardNumber("4111-1111-1111-1111"));
    assertTrue(isValidCardNumber("5555-5555-5555-4444"));
    assertFalse(isValidCardNumber("4111111111111112"));
    assertFalse(isValidCardNumber("0000000000000000"));
    assertFalse(isValidCardNumber("4111 1111"));
    assertFalse(isValidCardNumber(""));
    assertFalse(isValidCardNumber(null));
  }
}

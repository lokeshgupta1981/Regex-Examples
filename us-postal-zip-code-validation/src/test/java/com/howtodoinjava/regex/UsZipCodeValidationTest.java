package com.howtodoinjava.regex;

import static com.howtodoinjava.regex.UsZipCodeValidation.ZIP;
import static com.howtodoinjava.regex.UsZipCodeValidation.findZips;
import static com.howtodoinjava.regex.UsZipCodeValidation.findZipsAfterState;
import static com.howtodoinjava.regex.UsZipCodeValidation.isValidZip;
import static com.howtodoinjava.regex.UsZipCodeValidation.normalizeZip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UsZipCodeValidationTest {

  @ParameterizedTest
  @ValueSource(strings = {"12345", "01234", "12345-6789", "54321", "00000", "99999"})
  void acceptsFiveDigitAndZipPlusFour(String zip) {
    assertTrue(isValidZip(zip));
  }

  @ParameterizedTest
  @ValueSource(strings = {"1234", "123456", "12345-678", "12345-67890", "1234-56789", "12345 6789",
      "123456789", "ABCDE", "12345-", " 12345", "", "12345\n"})
  void rejectsWrongLengthsAndSeparators(String zip) {
    assertFalse(isValidZip(zip));
  }

  @Test
  void rejectsNull() {
    assertFalse(isValidZip(null));
  }

  @Test
  void stringMatchesNeedsNoAnchors() {
    assertTrue("12345".matches("\\d{5}(-\\d{4})?"));
    assertTrue("12345-6789".matches("\\d{5}(-\\d{4})?"));
    assertFalse("12345-6789".matches("[0-9]{5}"));
  }

  @Test
  void dollarAllowsTrailingNewlineWithFind() {
    assertFalse(ZIP.matcher("12345\n").matches());
    assertTrue(ZIP.matcher("12345\n").find());
  }

  @Test
  void matchPredicate() {
    Predicate<String> isZip = ZIP.asMatchPredicate();
    assertTrue(isZip.test("54321"));
    assertFalse(isZip.test("5432"));
    assertEquals(List.of("12345", "12345-6789"),
        List.of("12345", "9876", "12345-6789").stream().filter(isZip).toList());
  }

  @Test
  void normalizesUserInput() {
    assertEquals(Optional.of("12345"), normalizeZip("12345"));
    assertEquals(Optional.of("12345-6789"), normalizeZip("12345-6789"));
    assertEquals(Optional.of("12345-6789"), normalizeZip("12345 6789"));
    assertEquals(Optional.of("12345-6789"), normalizeZip(" 123456789 "));
    assertEquals(Optional.empty(), normalizeZip("1234-5678"));
    assertEquals(Optional.empty(), normalizeZip(null));
  }

  @Test
  void namedGroups() {
    Matcher m = UsZipCodeValidation.ZIP_LENIENT.matcher("12345 6789");
    assertTrue(m.matches());
    assertEquals("12345", m.group("zip"));
    assertEquals("6789", m.group("plus4"));
  }

  @Test
  void findsZipCodesInText() {
    assertEquals(List.of("12345", "54321-0001"), findZips("Ship to 12345 or 54321-0001"));
    assertEquals(List.of(), findZips("Order 123456 shipped"));
    assertEquals(List.of("55555", "12345"), findZips("Invoice 55555 for Anytown, NY 12345"));
    assertEquals(List.of("12345"), findZipsAfterState("Invoice 55555 for Anytown, NY 12345"));
  }

  @Test
  void intDropsLeadingZero() {
    assertEquals(1234, Integer.parseInt("01234"));
    assertFalse(isValidZip(String.valueOf(1234)));
  }
}

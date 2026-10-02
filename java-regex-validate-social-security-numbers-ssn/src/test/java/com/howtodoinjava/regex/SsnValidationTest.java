package com.howtodoinjava.regex;

import static com.howtodoinjava.regex.SsnValidation.SSN_FLEXIBLE;
import static com.howtodoinjava.regex.SsnValidation.SSN_PARTS;
import static com.howtodoinjava.regex.SsnValidation.SSN_SHAPE;
import static com.howtodoinjava.regex.SsnValidation.findSsns;
import static com.howtodoinjava.regex.SsnValidation.isValidSsn;
import static com.howtodoinjava.regex.SsnValidation.isValidSsnWithoutLookahead;
import static com.howtodoinjava.regex.SsnValidation.maskSsns;
import static com.howtodoinjava.regex.SsnValidation.normalizeSsn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** All numbers in this test are made up. */
class SsnValidationTest {

  @ParameterizedTest
  @ValueSource(strings = {"123-45-6789", "001-45-6789", "899-45-6789", "123-01-6789", "123-45-0001",
      "066-45-6789", "660-45-6789"})
  void acceptsNumbersOutsideTheInvalidRanges(String ssn) {
    assertTrue(isValidSsn(ssn));
    assertTrue(isValidSsnWithoutLookahead(ssn));
  }

  @ParameterizedTest
  @ValueSource(strings = {"000-45-6789", "666-45-6789", "900-45-6789", "999-45-6789", "123-00-6789",
      "123-45-0000", "123456789", "123 45 6789", "12-345-6789", "123-456-789", "123-45-67890",
      "abc-de-fghi", "6667-45-6789", ""})
  void rejectsInvalidNumbersAndFormats(String ssn) {
    assertFalse(isValidSsn(ssn));
    assertFalse(isValidSsnWithoutLookahead(ssn));
  }

  @Test
  void regexAndPlainJavaAgreeOnEveryAreaGroupAndSerialBoundary() {
    for (int area = 0; area <= 999; area++) {
      for (String group : List.of("00", "01", "99")) {
        for (String serial : List.of("0000", "0001", "9999")) {
          String ssn = String.format("%03d-%s-%s", area, group, serial);
          assertEquals(isValidSsnWithoutLookahead(ssn), isValidSsn(ssn), ssn);
        }
      }
    }
  }

  @Test
  void shapeOnlyAcceptsInvalidNumbers() {
    assertTrue(SSN_SHAPE.matcher("123-45-6789").matches());
    assertTrue(SSN_SHAPE.matcher("000-00-0000").matches());
    assertTrue(SSN_SHAPE.matcher("666-45-6789").matches());
  }

  @Test
  void normalizesSeparators() {
    assertEquals(Optional.of("123-45-6789"), normalizeSsn("123-45-6789"));
    assertEquals(Optional.of("123-45-6789"), normalizeSsn("123 45 6789"));
    assertEquals(Optional.of("123-45-6789"), normalizeSsn("123456789"));
    assertEquals(Optional.of("123-45-6789"), normalizeSsn(" 123456789 "));
    assertEquals(Optional.empty(), normalizeSsn("123-45 6789"));
    assertEquals(Optional.empty(), normalizeSsn("12345-6789"));
    assertEquals(Optional.empty(), normalizeSsn("000456789"));
    assertEquals(Optional.empty(), normalizeSsn(null));
    assertFalse(SSN_FLEXIBLE.matcher("123-45 6789").matches());
  }

  @Test
  void namedGroups() {
    Matcher m = SSN_PARTS.matcher("123-45-6789");
    assertTrue(m.matches());
    assertEquals("123", m.group("area"));
    assertEquals("45", m.group("group"));
    assertEquals("6789", m.group("serial"));
  }

  @Test
  void findsAndMasksNumbersInText() {
    String log = "user=lokesh ssn=123-45-6789 ref=123-45-6780 order=1234-56-7890";
    assertEquals(List.of("123-45-6789", "123-45-6780"), findSsns(log));
    assertEquals("user=lokesh ssn=***-**-6789 ref=***-**-6780 order=1234-56-7890", maskSsns(log));
    assertEquals("SSN ***-**-6789 on file", maskSsns("SSN 123-45-6789 on file"));
    assertEquals("id ***-**-3456", maskSsns("id 000-12-3456"));
  }
}

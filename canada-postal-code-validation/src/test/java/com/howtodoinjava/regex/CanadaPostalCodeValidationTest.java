package com.howtodoinjava.regex;

import static com.howtodoinjava.regex.CanadaPostalCodeValidation.POSTAL_CODE;
import static com.howtodoinjava.regex.CanadaPostalCodeValidation.POSTAL_CODE_LOOKAHEAD;
import static com.howtodoinjava.regex.CanadaPostalCodeValidation.POSTAL_CODE_PARTS;
import static com.howtodoinjava.regex.CanadaPostalCodeValidation.SHAPE_ONLY;
import static com.howtodoinjava.regex.CanadaPostalCodeValidation.isRural;
import static com.howtodoinjava.regex.CanadaPostalCodeValidation.isValidPostalCode;
import static com.howtodoinjava.regex.CanadaPostalCodeValidation.normalizePostalCode;
import static com.howtodoinjava.regex.CanadaPostalCodeValidation.region;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CanadaPostalCodeValidationTest {

  @ParameterizedTest
  @ValueSource(strings = {"A1A 1A1", "K1K 1K1", "T2W 3Z4", "X0A 1B2", "Y1A 9V9", "Y9Z 9Z9"})
  void acceptsValidPostalCodes(String code) {
    assertTrue(isValidPostalCode(code));
    assertTrue(POSTAL_CODE_LOOKAHEAD.matcher(code).matches());
  }

  @ParameterizedTest
  @ValueSource(strings = {"D1A 1A1", "W1A 1A1", "Z1A 1A1", "A1D 1A1", "A1A 1O1", "A1A 1U1",
      "A1A1A1", "A1A-1A1", "A1A  1A1", "a1a 1a1", "1A1 A1A", "A1A 1A", ""})
  void rejectsInvalidPostalCodes(String code) {
    assertFalse(isValidPostalCode(code));
    assertFalse(POSTAL_CODE_LOOKAHEAD.matcher(code).matches());
  }

  @Test
  void bothPatternsAgreeOnEveryCodeOfTheShape() {
    String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    for (char first : letters.toCharArray()) {
      for (char third : letters.toCharArray()) {
        String code = first + "1" + third + " 1" + third + "1";
        assertEquals(POSTAL_CODE.matcher(code).matches(),
            POSTAL_CODE_LOOKAHEAD.matcher(code).matches(), code);
      }
    }
  }

  @Test
  void shapeOnlyPatternIsTooLoose() {
    assertTrue(SHAPE_ONLY.matcher("D1A 1A1").matches());
    assertTrue(SHAPE_ONLY.matcher("W1A 1A1").matches());
    assertTrue(SHAPE_ONLY.matcher("A1A 1O1").matches());
    assertFalse(POSTAL_CODE.matcher("D1A 1A1").matches());
  }

  @Test
  void optionalSpaceAndCaseInsensitiveFlag() {
    assertTrue("A1A1A1".matches("[ABCEGHJ-NPRSTVXY][0-9][ABCEGHJ-NPRSTV-Z] ?[0-9][ABCEGHJ-NPRSTV-Z][0-9]"));
    Pattern ci = Pattern.compile(POSTAL_CODE.pattern(), Pattern.CASE_INSENSITIVE);
    assertTrue(ci.matcher("a1a 1a1").matches());
    assertFalse(ci.matcher("d1a 1a1").matches());
  }

  @Test
  void normalizesToCanadaPostForm() {
    assertEquals(Optional.of("A1A 1A1"), normalizePostalCode("A1A 1A1"));
    assertEquals(Optional.of("A1A 1A1"), normalizePostalCode("a1a1a1"));
    assertEquals(Optional.of("A1A 1A1"), normalizePostalCode("a1a-1a1"));
    assertEquals(Optional.of("T2W 3Z4"), normalizePostalCode("  t2w 3z4 "));
    assertEquals(Optional.empty(), normalizePostalCode("A1A  1A1"));
    assertEquals(Optional.empty(), normalizePostalCode("W1A 1A1"));
    assertEquals(Optional.empty(), normalizePostalCode(null));
  }

  @Test
  void splitsIntoFsaAndLdu() {
    Matcher m = POSTAL_CODE_PARTS.matcher("K1K 1K1");
    assertTrue(m.matches());
    assertEquals("K1K", m.group("fsa"));
    assertEquals("1K1", m.group("ldu"));
  }

  @Test
  void regionAndRural() {
    assertEquals("Ontario", region("K1K 1K1"));
    assertEquals("Alberta", region("T2W 3Z4"));
    assertEquals("Northwest Territories or Nunavut", region("X0A 1B2"));
    assertTrue(isRural("X0A 1B2"));
    assertFalse(isRural("T2W 3Z4"));
  }
}

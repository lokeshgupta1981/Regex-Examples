package com.howtodoinjava.regex;

import static com.howtodoinjava.regex.UkPostcodeValidation.GOV_UK_REGEX;
import static com.howtodoinjava.regex.UkPostcodeValidation.GOV_UK_REGEX_FIXED;
import static com.howtodoinjava.regex.UkPostcodeValidation.POSTCODE;
import static com.howtodoinjava.regex.UkPostcodeValidation.POSTCODE_PARTS;
import static com.howtodoinjava.regex.UkPostcodeValidation.POSTCODE_STRICT;
import static com.howtodoinjava.regex.UkPostcodeValidation.isValidPostcode;
import static com.howtodoinjava.regex.UkPostcodeValidation.normalizePostcode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.regex.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UkPostcodeValidationTest {

  @ParameterizedTest
  @ValueSource(strings = {"M1 1AA", "B33 8TH", "CR2 6XH", "DN55 1PT", "W1A 0AX", "EC1A 1BB", "GIR 0AA"})
  void bothPatternsAcceptTheSixFormatsAndGir(String code) {
    assertTrue(isValidPostcode(code));
    assertTrue(POSTCODE_STRICT.matcher(code).matches());
  }

  @ParameterizedTest
  @ValueSource(strings = {"QA1 1AA", "AI1 1AA", "W1L 1AA", "SW1C 1AA"})
  void onlyStrictPatternRejectsUnusedPositionLetters(String code) {
    assertTrue(isValidPostcode(code));
    assertFalse(POSTCODE_STRICT.matcher(code).matches());
  }

  @ParameterizedTest
  @ValueSource(strings = {"M1 1CA", "M1 1AO", "SW1A1BB", "sw1a 1bb", "M1  1AA", "M1 1AAA", "123 4AB", "ASCN 1ZZ", ""})
  void bothPatternsReject(String code) {
    assertFalse(isValidPostcode(code));
    assertFalse(POSTCODE_STRICT.matcher(code).matches());
  }

  @Test
  void inwardCodeNeverUsesCikmov() {
    for (char c : "CIKMOV".toCharArray()) {
      assertFalse(isValidPostcode("M1 1A" + c), "letter " + c);
      assertFalse(isValidPostcode("M1 1" + c + "A"), "letter " + c);
    }
  }

  @Test
  void overseasTerritories() {
    assertFalse(isValidPostcode("ASCN 1ZZ"));
    assertTrue(isValidPostcode("GX11 1AA"));
    assertTrue(POSTCODE_STRICT.matcher("GX11 1AA").matches());
  }

  @Test
  void normalizesUserInput() {
    assertEquals(Optional.of("EC1A 1BB"), normalizePostcode("ec1a1bb"));
    assertEquals(Optional.of("EC1A 1BB"), normalizePostcode(" ec1a 1bb "));
    assertEquals(Optional.of("CR2 6XH"), normalizePostcode("Cr2  6xh"));
    assertEquals(Optional.of("M1 1AA"), normalizePostcode("m11aa"));
    assertEquals(Optional.empty(), normalizePostcode("M1 1CA"));
    assertEquals(Optional.empty(), normalizePostcode("SW1A 1BBX"));
    assertEquals(Optional.empty(), normalizePostcode("M1"));
    assertEquals(Optional.empty(), normalizePostcode(null));
  }

  @Test
  void namedGroups() {
    Matcher m = POSTCODE_PARTS.matcher("DN55 1PT");
    assertTrue(m.matches());
    assertEquals("DN55", m.group("outward"));
    assertEquals("DN", m.group("area"));
    assertEquals("55", m.group("district"));
    assertEquals("1PT", m.group("inward"));
    assertEquals("1", m.group("sector"));
    assertEquals("PT", m.group("unit"));
  }

  @Test
  void govUkRegexAnchorsApplyToOneSideOnly() {
    assertTrue(GOV_UK_REGEX.matcher("EC1A 1BB").matches());
    assertTrue(GOV_UK_REGEX.matcher("ec1a 1bb").matches());
    assertTrue(GOV_UK_REGEX.matcher("Ref 99 EC1A 1BB").find());
    assertTrue(GOV_UK_REGEX.matcher("GIR 0AA, flat 2").find());
    assertFalse(GOV_UK_REGEX_FIXED.matcher("Ref 99 EC1A 1BB").find());
    assertFalse(GOV_UK_REGEX_FIXED.matcher("GIR 0AA, flat 2").find());
    assertTrue(GOV_UK_REGEX_FIXED.matcher("EC1A 1BB").find());
  }
}

package com.howtodoinjava.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.regex.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InternationalPhoneNumbersTest {

  static final PhoneNumberUtil UTIL = PhoneNumberUtil.getInstance();

  @ParameterizedTest(name = "E164 {0} -> {1}")
  @CsvSource(delimiter = '|', textBlock = """
      +442079460958     | true
      +12025550123      | true
      +61255501234      | true
      +12               | true
      +999123456789     | true
      +1234567890123456 | false
      +0442079460958    | false
      442079460958      | false
      +44 20 7946 0958  | false
      +44-20-7946-0958  | false
      """)
  void e164(String input, boolean expected) {
    assertEquals(expected, InternationalPhoneNumbers.E164.matcher(input).matches());
  }

  @ParameterizedTest(name = "E123 [{0}] -> {1}")
  @CsvSource(delimiter = '|', ignoreLeadingAndTrailingWhitespace = false, textBlock = """
      +44 20 7946 0958|true
      +1 202 555 0123|true
      +442079460958|true
      +44  20 7946 0958|false
      +44 20 7946 0958 |false
      +44-20-7946-0958|false
      +12 345|false
      """)
  void e123(String input, boolean expected) {
    assertEquals(expected, InternationalPhoneNumbers.E123.matcher(input).matches());
  }

  @Test
  void normalizeToE164() {
    assertEquals("+442079460958", InternationalPhoneNumbers.toE164("+44 (20) 7946-0958"));
    assertEquals("+12025550123", InternationalPhoneNumbers.toE164("+1.202.555.0123"));
    assertEquals("+12025550123", InternationalPhoneNumbers.toE164("+1 (202) 555-0123"));
    assertEquals("+442079460958", InternationalPhoneNumbers.toE164("0044 20 7946 0958"));
    assertNull(InternationalPhoneNumbers.toE164("+44 20 7946 0958 ext 12"));
  }

  @Test
  void eppNamedGroups() {
    Matcher m = InternationalPhoneNumbers.EPP.matcher("+44.2079460958x123");
    assertTrue(m.matches());
    assertEquals("44", m.group("cc"));
    assertEquals("2079460958", m.group("number"));
    assertEquals("123", m.group("ext"));
    assertTrue(InternationalPhoneNumbers.EPP.matcher("+1.2025550123").matches());
    assertFalse(InternationalPhoneNumbers.EPP.matcher("+1 202 555 0123").matches());
    assertFalse(InternationalPhoneNumbers.EPP.matcher("+1234.2025550123").matches());
  }

  @Test
  void regexPassesNumbersThatLibphonenumberRejects() {
    assertTrue(InternationalPhoneNumbers.E164.matcher("+999123456789").matches());
    assertFalse(InternationalPhoneNumbers.isValid("+999123456789"));
    assertTrue(InternationalPhoneNumbers.E164.matcher("+4420794609").matches());
    assertFalse(InternationalPhoneNumbers.isValid("+4420794609"));
    assertTrue(InternationalPhoneNumbers.isValid("+44 20 7946 0958"));
    assertFalse(InternationalPhoneNumbers.isValid("+44 20 7946 095"));
  }

  @Test
  void libphonenumberParsesAndFormats() throws NumberParseException {
    PhoneNumber n = UTIL.parse("+44 20 7946 0958", null);
    assertEquals(44, n.getCountryCode());
    assertEquals(2079460958L, n.getNationalNumber());
    assertTrue(UTIL.isValidNumber(n));
    assertEquals("GB", UTIL.getRegionCodeForNumber(n));
    assertEquals(PhoneNumberType.FIXED_LINE, UTIL.getNumberType(n));
    assertEquals("+442079460958", UTIL.format(n, PhoneNumberFormat.E164));
    assertEquals("+44 20 7946 0958", UTIL.format(n, PhoneNumberFormat.INTERNATIONAL));
    assertEquals("020 7946 0958", UTIL.format(n, PhoneNumberFormat.NATIONAL));
    assertEquals("+442079460958", UTIL.format(UTIL.parse("020 7946 0958", "GB"), PhoneNumberFormat.E164));
    PhoneNumber us = UTIL.parse("(202) 555-0123", "US");
    assertEquals("+12025550123", UTIL.format(us, PhoneNumberFormat.E164));
    assertTrue(UTIL.isValidNumber(us));
    NumberParseException e = assertThrows(NumberParseException.class, () -> UTIL.parse("hello", null));
    assertEquals(NumberParseException.ErrorType.NOT_A_NUMBER, e.getErrorType());
  }
}

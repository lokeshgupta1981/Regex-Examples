package com.howtodoinjava.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NorthAmericanPhoneNumbersTest {

  /** Columns: input, BASIC, NANP, formatted (empty = null). Same table as in the article. */
  @ParameterizedTest(name = "{0}")
  @CsvSource(delimiter = '|', textBlock = """
      2025550123       | true  | true  | (202) 555-0123
      202-555-0123     | true  | true  | (202) 555-0123
      202.555.0123     | true  | true  | (202) 555-0123
      202 555 0123     | true  | true  | (202) 555-0123
      (202) 555-0123   | true  | true  | (202) 555-0123
      (202)555-0123    | true  | true  | (202) 555-0123
      +1 202 555 0123  | false | true  | (202) 555-0123
      1-202-555-0123   | false | true  | (202) 555-0123
      (202-555-0123    | true  | false |
      202) 555-0123    | true  | false |
      123-456-7890     | true  | false |
      202-155-0123     | true  | false |
      202-555-012      | false | false |
      202--555-0123    | false | false |
      +44 20 7946 0958 | false | false |
      """)
  void basicVersusNanp(String input, boolean basic, boolean nanp, String formatted) {
    assertEquals(basic, NorthAmericanPhoneNumbers.BASIC.matcher(input).matches(), "BASIC");
    assertEquals(nanp, NorthAmericanPhoneNumbers.NANP.matcher(input).matches(), "NANP");
    assertEquals(formatted, NorthAmericanPhoneNumbers.format(input), "format");
  }

  @Test
  void quickReference() {
    assertTrue(NorthAmericanPhoneNumbers.BASIC.matcher("123-456-7890").matches());
    assertFalse(NorthAmericanPhoneNumbers.NANP.matcher("123-456-7890").matches());
    assertTrue(NorthAmericanPhoneNumbers.NANP.matcher("+1 (202) 555-0123").matches());
    assertEquals("(202) 555-0123", NorthAmericanPhoneNumbers.format("202.555.0123"));
    assertEquals("+12025550123", NorthAmericanPhoneNumbers.toE164("(202) 555-0123"));
    assertEquals("Call (202) 555-0123 or (312) 555-0199.",
        NorthAmericanPhoneNumbers.reformatInText("Call 202.555.0123 or 312 555 0199."));
  }

  @Test
  void basicReplaceFirst() {
    assertEquals("(202) 555-0123", NorthAmericanPhoneNumbers.BASIC.matcher("202.555.0123").replaceFirst("($1) $2-$3"));
    assertEquals("(202) 555-0123", NorthAmericanPhoneNumbers.BASIC.matcher("(202-555-0123").replaceFirst("($1) $2-$3"));
    assertEquals("hello", NorthAmericanPhoneNumbers.BASIC.matcher("hello").replaceFirst("($1) $2-$3"));
  }

  @Test
  void groups() {
    Matcher m = NorthAmericanPhoneNumbers.NANP.matcher("(202) 555-0123");
    assertTrue(m.matches());
    assertEquals("202", m.group(1));
    assertNull(m.group(2));
    assertEquals("555", m.group(3));
    assertEquals("0123", m.group(4));
    Matcher m2 = NorthAmericanPhoneNumbers.NANP.matcher("202-555-0123");
    assertTrue(m2.matches());
    assertNull(m2.group(1));
    assertEquals("202", m2.group(2));
  }

  @Test
  void outputFormats() {
    String s = "202 555 0123";
    assertEquals("(202) 555-0123", NorthAmericanPhoneNumbers.NANP.matcher(s).replaceFirst("($1$2) $3-$4"));
    assertEquals("202-555-0123", NorthAmericanPhoneNumbers.NANP.matcher(s).replaceFirst("$1$2-$3-$4"));
    assertEquals("202.555.0123", NorthAmericanPhoneNumbers.NANP.matcher(s).replaceFirst("$1$2.$3.$4"));
    assertEquals("+12025550123", NorthAmericanPhoneNumbers.NANP.matcher(s).replaceFirst("+1$1$2$3$4"));
  }

  @ParameterizedTest(name = "{0} -> {1}")
  @CsvSource(delimiter = '|', textBlock = """
      202-555-0123 x45      | 45
      202-555-0123 ext. 45  | 45
      (202) 555-0123 EXT 45 | 45
      202-555-0123x45       | 45
      202-555-0123          |
      """)
  void extensions(String input, String ext) {
    Matcher m = NorthAmericanPhoneNumbers.NANP_EXT.matcher(input);
    assertTrue(m.matches());
    assertEquals(ext, m.group(5));
  }

  @Test
  void extensionKeywordWithoutDigitsFails() {
    assertFalse(NorthAmericanPhoneNumbers.NANP_EXT.matcher("202-555-0123 ext").matches());
  }

  @Test
  void numbersInsideText() {
    String text = "Call 202.555.0123 or 312 555 0199. Order 1234567890 ships today.";
    String expected = "Call (202) 555-0123 or (312) 555-0199. Order 1234567890 ships today.";
    assertEquals(2, NorthAmericanPhoneNumbers.IN_TEXT.matcher(text).results().count());
    assertEquals(expected, NorthAmericanPhoneNumbers.IN_TEXT.matcher(text).replaceAll("($1) $2-$3"));
    assertEquals(expected, NorthAmericanPhoneNumbers.IN_TEXT.matcher(text)
        .replaceAll(r -> "(" + r.group(1) + ") " + r.group(2) + "-" + r.group(3)));
  }
}

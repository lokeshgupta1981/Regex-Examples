package com.howtodoinjava.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.IDN;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EmailValidationTest {

  /** Columns: input, AT_SIGN, DOT_IN_DOMAIN, HTML5, STRICT (same table as in the article). */
  @ParameterizedTest(name = "{0}")
  @CsvSource(delimiter = '|', quoteCharacter = '\u0000', textBlock = """
      user@example.com         | true  | true  | true  | true
      first.last@example.co.uk | true  | true  | true  | true
      user+news@example.com    | true  | true  | true  | true
      o'neil@example.org       | true  | true  | true  | true
      user@localhost           | true  | false | true  | false
      user@example.c           | true  | true  | true  | false
      .user@example.com        | true  | true  | true  | false
      user..name@example.com   | true  | true  | true  | false
      user@-example.com        | true  | true  | false | false
      user@example..com        | true  | true  | false | false
      user@[192.0.2.1]         | true  | true  | false | false
      "john doe"@example.com   | false | false | false | false
      user@@example.com        | false | false | false | false
      user name@example.com    | false | false | false | false
      user.example.com         | false | false | false | false
      @example.com             | false | false | false | false
      """)
  void eachRegexAcceptsAndRejects(String input, boolean atSign, boolean dot, boolean html5, boolean strict) {
    assertEquals(atSign, EmailValidation.AT_SIGN.matcher(input).matches(), "AT_SIGN");
    assertEquals(dot, EmailValidation.DOT_IN_DOMAIN.matcher(input).matches(), "DOT_IN_DOMAIN");
    assertEquals(html5, EmailValidation.HTML5.matcher(input).matches(), "HTML5");
    assertEquals(strict, EmailValidation.STRICT.matcher(input).matches(), "STRICT");
  }

  @Test
  void quickReference() {
    assertTrue(EmailValidation.AT_SIGN.matcher("user@example").matches());
    assertFalse(EmailValidation.DOT_IN_DOMAIN.matcher("user@example").matches());
    assertTrue(EmailValidation.HTML5.matcher("user..name@example.com").matches());
    assertFalse(EmailValidation.STRICT.matcher("user..name@example.com").matches());
    assertTrue(EmailValidation.IS_EMAIL.test("user@example.com"));
    assertFalse(EmailValidation.IS_EMAIL.test("user@example.c"));
  }

  @Test
  void lengthLimits() {
    String local64 = "a".repeat(64) + "@example.com";
    String local65 = "a".repeat(65) + "@example.com";
    String total255 = "user@" + "a".repeat(63) + "." + "b".repeat(63) + "." + "c".repeat(63) + "."
        + "d".repeat(54) + ".com";
    assertEquals(255, total255.length());
    assertTrue(EmailValidation.STRICT.matcher(local64).matches());
    assertTrue(EmailValidation.HTML5.matcher(local65).matches());
    assertFalse(EmailValidation.STRICT.matcher(local65).matches());
    assertTrue(EmailValidation.HTML5.matcher(total255).matches());
    assertFalse(EmailValidation.STRICT.matcher(total255).matches());
  }

  @Test
  void normalizeTrimsAndLowercasesOnlyTheDomain() {
    assertEquals("User@example.com", EmailValidation.normalize("  User@Example.COM "));
    assertTrue(EmailValidation.isValidEmail("  User@Example.COM "));
    assertFalse(EmailValidation.isValidEmail(null));
  }

  @Test
  void internationalizedDomain() {
    String email = "user@caf\u00e9.example";
    assertFalse(EmailValidation.STRICT.matcher(email).matches());
    assertEquals("xn--caf-dma.example", IDN.toASCII("caf\u00e9.example"));
    assertTrue(EmailValidation.isValidEmail(email));
  }
}

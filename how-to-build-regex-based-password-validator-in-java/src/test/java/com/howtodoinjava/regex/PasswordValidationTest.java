package com.howtodoinjava.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PasswordValidationTest {

  /** Columns: password, then the result after each step: length only, +lower, +upper, +digit, +special. */
  @ParameterizedTest(name = "{0}")
  @CsvSource(delimiter = '|', ignoreLeadingAndTrailingWhitespace = false, textBlock = """
      Apple@123|true|true|true|true|true
      apple@123|true|true|false|false|false
      APPLE@123|true|false|false|false|false
      Apple@abc|true|true|true|false|false
      Apple1234|true|true|true|true|false
      Ap@1|false|false|false|false|false
      Apple @123|true|true|true|true|true
      """)
  void oneLookaheadAtATime(String password, boolean s0, boolean s1, boolean s2, boolean s3, boolean s4) {
    assertEquals(s0, password.matches("^.{8,64}$"));
    assertEquals(s1, password.matches("^(?=.*[a-z]).{8,64}$"));
    assertEquals(s2, password.matches("^(?=.*[a-z])(?=.*[A-Z]).{8,64}$"));
    assertEquals(s3, password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,64}$"));
    assertEquals(s4, PasswordValidation.STRONG.matcher(password).matches());
    assertEquals(s4, PasswordValidation.STRONG_NEGATED.matcher(password).matches(), "negated form");
  }

  @Test
  void lookaheadsAreZeroWidth() {
    Matcher m = Pattern.compile("(?=.*\\d)").matcher("abc1");
    assertTrue(m.find());
    assertEquals("", m.group());
    assertEquals(0, m.start());
    assertFalse("abc1".matches("(?=.*\\d)"));
    assertTrue("abc1".matches("(?=.*\\d).*"));
    assertFalse("abc1".matches("(?=\\d).*"));
  }

  @Test
  void oldRegexChecksTheLetterD() {
    assertTrue(PasswordValidation.OLD.matcher("Password@").matches());
    assertFalse(PasswordValidation.OLD.matcher("Apple@123").matches());
  }

  @Test
  void negativeLookaheads() {
    assertFalse("Apple @123".matches("^(?!.*\\s).+$"));
    assertTrue("Apple@123".matches("^(?!.*\\s).+$"));
    assertFalse("Appple@123".matches("^(?!.*(.)\\1\\1).+$"));
    assertTrue("Apple@123".matches("^(?!.*(.)\\1\\1).+$"));
    Pattern noUser = Pattern.compile("^(?!.*(?i:" + Pattern.quote("lokesh") + ")).+$");
    assertEquals("^(?!.*(?i:\\Qlokesh\\E)).+$", noUser.pattern());
    assertFalse(noUser.matcher("Lokesh@2026").matches());
    assertTrue(noUser.matcher("Apple@2026").matches());
  }

  @Test
  void configurablePolicy() {
    PasswordPolicy policy = PasswordPolicy.builder()
        .length(8, 64).requireLowercase().requireUppercase().requireDigit().requireSpecial()
        .forbidWord("lokesh").build();
    assertEquals("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9])(?!.*(?i:\\Qlokesh\\E)).{8,64}$", policy.regex());
    assertTrue(policy.isValid("Apple@123"));
    assertEquals(List.of(), policy.violations("Apple@123"));
    assertFalse(policy.isValid("apple123"));
    assertEquals(List.of("needs an uppercase letter", "needs a character that is not a letter or digit"),
        policy.violations("apple123"));
    assertEquals(List.of("must be 8 to 64 characters long"), policy.violations("Ap@1"));
    assertEquals(List.of("must not contain \"lokesh\""), policy.violations("Lokesh@123"));
    assertFalse(policy.isValid(null));
  }

  @Test
  void forbidWhitespaceRule() {
    PasswordPolicy policy = PasswordPolicy.builder().length(8, 64).forbidWhitespace().build();
    assertFalse(policy.isValid("Apple @123"));
    assertEquals(List.of("must not contain whitespace"), policy.violations("Apple @123"));
  }

  @Test
  void lengthCountsCodePoints() {
    String emoji = new String(Character.toChars(0x1F600));
    assertEquals(2, emoji.length());
    assertEquals(1, emoji.codePointCount(0, emoji.length()));
    assertTrue(emoji.matches("."));
  }

  @Test
  void nistStyleCheck() {
    assertTrue(PasswordValidation.NIST_LENGTH.matcher("green apple tree").matches());
    assertEquals(List.of(), PasswordValidation.nistViolations("green apple tree", "lokesh"));
    assertEquals(List.of("must be 15 to 64 characters long"), PasswordValidation.nistViolations("apple tree", "lokesh"));
    assertEquals(List.of("must be 15 to 64 characters long", "is a commonly used password"),
        PasswordValidation.nistViolations("PASSWORD123", "lokesh"));
    assertEquals(List.of("must not contain the user name"),
        PasswordValidation.nistViolations("lokesh likes apples", "lokesh"));
    assertEquals(List.of("must be 15 to 64 characters long"), PasswordValidation.nistViolations("Apple@123", "lokesh"));
  }
}

package com.howtodoinjava.regex;

import static com.howtodoinjava.regex.Alphanumeric.ALPHANUMERIC;
import static com.howtodoinjava.regex.Alphanumeric.IS_ALPHANUMERIC;
import static com.howtodoinjava.regex.Alphanumeric.LETTER_AND_DIGIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AlphanumericTest {

  @ParameterizedTest
  @ValueSource(strings = {"Lokesh", "Lokesh123", "2026"})
  void validSamples(String input) {
    assertTrue(ALPHANUMERIC.matcher(input).matches());
  }

  @ParameterizedTest
  @ValueSource(strings = {"Lokesh123-", "Lokesh 123", "user_1", "", "caf\u00e9"})
  void invalidSamples(String input) {
    assertFalse(ALPHANUMERIC.matcher(input).matches());
  }

  @Test
  void quickReference() {
    assertTrue("abc123".matches("[a-zA-Z0-9]+"));
    assertFalse("abc 123".matches("[a-zA-Z0-9]+"));
    assertFalse("".matches("[a-zA-Z0-9]+"));
    assertFalse("caf\u00e9".matches("[a-zA-Z0-9]+"));
    assertTrue("caf\u00e9".matches("(?U)\\p{Alnum}+"));
  }

  @Test
  void matchPredicateAndFind() {
    assertTrue(IS_ALPHANUMERIC.test("Lokesh123"));
    assertFalse(IS_ALPHANUMERIC.test("Lokesh123-"));
    assertTrue(Pattern.compile("[a-zA-Z0-9]+").matcher("Lokesh123-").find());
  }

  @Test
  void emptyInputAndLength() {
    assertTrue("".matches("[a-zA-Z0-9]*"));
    assertFalse("".matches("[a-zA-Z0-9]+"));
    assertFalse("ab".matches("[a-zA-Z0-9]{3,16}"));
    assertTrue("Lokesh".matches("[a-zA-Z0-9]{3,16}"));
    assertFalse("a".repeat(17).matches("[a-zA-Z0-9]{3,16}"));
  }

  @Test
  void variants() {
    assertTrue("ABC123".matches("(?i)[a-z0-9]+"));
    assertTrue("user_1".matches("\\w+"));
    assertTrue("user-1".matches("[a-zA-Z0-9_-]+"));
    assertTrue("abc 123".matches("[a-zA-Z0-9 ]+"));
    assertFalse("abc\t123".matches("[a-zA-Z0-9 ]+"));
    assertTrue("abc\t123".matches("[a-zA-Z0-9\\s]+"));
    assertTrue("abc123".matches("[a-z0-9]+"));
    assertFalse("Abc123".matches("[a-z0-9]+"));
  }

  @Test
  void atLeastOneLetterAndOneDigit() {
    assertTrue("abc123".matches(LETTER_AND_DIGIT));
    assertFalse("abc".matches(LETTER_AND_DIGIT));
    assertFalse("123".matches(LETTER_AND_DIGIT));
    assertFalse("abc123!".matches(LETTER_AND_DIGIT));
    assertTrue("abc123".matches("(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]+"));
    assertFalse("abc".matches("(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]+"));
  }

  @Test
  void unicode() {
    assertFalse("\u00e9".matches("\\p{Alnum}"));
    assertTrue("\u00e9".matches("(?U)\\p{Alnum}"));
    assertFalse("\u0663".matches("\\d"));
    assertTrue("\u0663".matches("(?U)\\p{Alnum}"));
    assertFalse("\u00bd".matches("(?U)\\p{Alnum}"));
    assertTrue("\u00bd".matches("[\\p{L}\\p{N}]"));
    assertTrue("\u00e9".matches("[\\p{L}\\p{Nd}]"));
    assertTrue("\u00b2".matches("[\\p{L}\\p{N}]"));
    assertFalse("\u00b2".matches("[\\p{L}\\p{Nd}]"));
  }

  @Test
  void removingOtherCharacters() {
    assertEquals("HelloWorld123", "Hello, World! 123".replaceAll("[^a-zA-Z0-9]", ""));
    assertEquals("a-b-c-d", "a-b_c d".replaceAll("[^a-zA-Z0-9]+", "-"));
  }

  @Test
  void withoutARegex() {
    assertTrue("Lokesh123".chars().allMatch(Character::isLetterOrDigit));
    assertTrue("caf\u00e9".chars().allMatch(Character::isLetterOrDigit));
    assertTrue("".chars().allMatch(Character::isLetterOrDigit));
  }
}

package com.howtodoinjava.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MinMaxLengthTest {

  /** Columns: regex, then the result for "", "hi", "hello", "hello world". */
  @ParameterizedTest(name = "{0}")
  @CsvSource(delimiter = '|', textBlock = """
      ^.{5}$    | false | false | true | false
      ^.{3,}$   | false | false | true | true
      ^.{3,10}$ | false | false | true | false
      ^.{0,10}$ | true  | true  | true | false
      ^.{1,10}$ | false | true  | true | false
      """)
  void quantifierForms(String regex, boolean empty, boolean hi, boolean hello, boolean helloWorld) {
    assertEquals(empty, "".matches(regex));
    assertEquals(hi, "hi".matches(regex));
    assertEquals(hello, "hello".matches(regex));
    assertEquals(helloWorld, "hello world".matches(regex));
  }

  @ParameterizedTest(name = "UPPER_1_TO_10 [{0}] -> {1}")
  @CsvSource({"LOKESH, true", "JAVACRAZY, true", "LOKESHGUPTAINDIA, false", "LOKESH123, false", "lokesh, false",
      "'', false"})
  void upperCaseOneToTen(String input, boolean expected) {
    assertEquals(expected, MinMaxLength.UPPER_1_TO_10.matcher(input).matches());
  }

  @Test
  void lineBreaks() {
    assertFalse("hello\nworld".matches("^.{1,20}$"));
    assertTrue("hello\nworld".matches("(?s)^.{1,20}$"));
    assertTrue("hello\nworld".matches("^[\\s\\S]{1,20}$"));
    assertFalse("hello\n".matches("^.{1,10}$"));
    assertTrue(Pattern.compile("^.{1,10}$").matcher("hello\n").find());
    assertFalse(Pattern.compile("^.{1,10}\\z").matcher("hello\n").find());
  }

  @ParameterizedTest(name = "USERNAME {0} -> {1}")
  @CsvSource({"lokesh_42, true", "lo, false", "lokesh_gupta_2026, false", "42lokesh, false", "lokesh-42, false",
      "abc, true"})
  void usernameWithLengthLookahead(String input, boolean expected) {
    assertEquals(expected, MinMaxLength.USERNAME.matcher(input).matches());
  }

  @ParameterizedTest(name = "NON_SPACE_5_TO_10 [{0}] -> {1}")
  @CsvSource(ignoreLeadingAndTrailingWhitespace = false, value = {
      "hello,true", "h e l l o,true", "  hello  ,true", "hi,false", "hello world,true"})
  void nonWhitespaceCount(String input, boolean expected) {
    assertEquals(expected, MinMaxLength.NON_SPACE_5_TO_10.matcher(input).matches());
  }

  @Test
  void unicodeLengths() {
    String flag = new String(Character.toChars(0x1F1EE)) + new String(Character.toChars(0x1F1F3));
    String precomposed = "caf" + (char) 0xE9;
    String combining = "cafe" + (char) 0x0301;
    assertEquals(4, flag.length());
    assertEquals(2, flag.codePointCount(0, flag.length()));
    assertTrue(flag.matches(".{2}"));
    assertTrue(flag.matches("\\X"));
    assertEquals(4, precomposed.length());
    assertEquals(5, combining.length());
    assertTrue(precomposed.matches("^.{4}$"));
    assertFalse(combining.matches("^.{4}$"));
    assertTrue(combining.matches("^\\X{4}$"));
    assertTrue(MinMaxLength.GRAPHEMES_1_TO_5.matcher(combining).matches());
  }

  @Test
  void patternFromSettings() {
    assertEquals("^.{2,4}$", MinMaxLength.lengthPattern(2, 4).pattern());
    assertTrue(MinMaxLength.lengthPattern(2, 4).matcher("abc").matches());
    assertFalse(MinMaxLength.lengthPattern(2, 4).matcher("abcde").matches());
    assertThrows(IllegalArgumentException.class, () -> MinMaxLength.lengthPattern(4, 2));
    PatternSyntaxException e = assertThrows(PatternSyntaxException.class, () -> Pattern.compile("^.{10,1}$"));
    assertEquals("Illegal repetition range", e.getDescription());
    assertEquals(7, e.getIndex());
    e = assertThrows(PatternSyntaxException.class, () -> Pattern.compile("^.{,10}$"));
    assertEquals("Illegal repetition", e.getDescription());
  }

  @Test
  void plainJavaCheck() {
    assertTrue(MinMaxLength.isLengthBetween("hello", 3, 10));
    assertFalse(MinMaxLength.isLengthBetween("hi", 3, 10));
  }
}

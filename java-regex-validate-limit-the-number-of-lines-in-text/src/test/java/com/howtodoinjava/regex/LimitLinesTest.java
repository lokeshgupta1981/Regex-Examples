package com.howtodoinjava.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class LimitLinesTest {

  @Test
  void lineBreakClasses() {
    assertTrue("\r\n".matches("\\R"));
    assertTrue("\u2028".matches("\\R"));
    assertTrue("\u000b".matches("\\R"));
    assertTrue("\u000b".matches("."));
    assertFalse("\u000b".matches("\\V"));
    assertTrue("a".matches("\\V"));
  }

  @Test
  void vIsExactlyTheComplementOfSingleCharLineBreaks() {
    for (int c = 0; c < 0x10000; c++) {
      String s = String.valueOf((char) c);
      assertEquals(!s.matches("\\R"), s.matches("\\V"), "char " + c);
    }
  }

  @Test
  void atMostThreeLines() {
    Pattern p = LimitLines.MAX_3;
    assertTrue(p.matcher("").matches());
    assertTrue(p.matcher("one").matches());
    assertTrue(p.matcher("one\ntwo\nthree").matches());
    assertFalse(p.matcher("one\ntwo\nthree\nfour").matches());
    assertTrue(p.matcher("one\r\ntwo\rthree").matches());
    assertTrue(p.matcher("one\u2028two\u0085three").matches());
    assertFalse(p.matcher("\n\n\n").matches());
    assertTrue(p.matcher("one\ntwo\nthree\nfour").find());
  }

  @Test
  void classicCookbookPatternMissesUnicodeBreaks() {
    assertTrue(LimitLines.CLASSIC_MAX_4.matcher("a\nb\nc\nd").matches());
    assertFalse(LimitLines.CLASSIC_MAX_4.matcher("a\nb\nc\nd\ne").matches());
    assertTrue(LimitLines.CLASSIC_MAX_4.matcher("a\u2028b\u2028c\u2028d\u2028e").matches());
  }

  @Test
  void textBlocksAndTrailingBreak() {
    String poem = """
        roses are red
        violets are blue
        """;
    assertTrue(poem.endsWith("\n"));
    assertFalse(LimitLines.MAX_2.matcher(poem).matches());
    assertTrue(LimitLines.MAX_2_TRAILING.matcher(poem).matches());
    String oneLine = """
        roses are red""";
    assertTrue(LimitLines.MAX_2.matcher(oneLine).matches());
    String three = """
        roses are red
        violets are blue
        sugar is sweet
        """;
    assertFalse(LimitLines.MAX_2_TRAILING.matcher(three).matches());
  }

  @Test
  void minimumAndMaximum() {
    Pattern p = LimitLines.TWO_TO_FOUR;
    assertFalse(p.matcher("one").matches());
    assertTrue(p.matcher("one\ntwo").matches());
    assertTrue(p.matcher("one\r\ntwo").matches());
    assertTrue(p.matcher("a\nb\nc\nd").matches());
    assertFalse(p.matcher("a\nb\nc\nd\ne").matches());
    assertTrue("\r\n".matches("\\R\\n"));
    assertTrue(LimitLines.THREE_TO_FOUR_BUGGY.matcher("a\r\nb").matches());
    assertFalse(LimitLines.lineRange(3, 4).matcher("a\r\nb").matches());
    assertTrue(LimitLines.lineRange(3, 4).matcher("a\r\nb\r\nc").matches());
    assertEquals("\\V*(?:(?>\\R)\\V*){2,3}", LimitLines.lineRange(3, 4).pattern());
  }

  @Test
  void lineLength() {
    assertTrue(LimitLines.THREE_LINES_OF_10.matcher("apple\nbanana").matches());
    assertFalse(LimitLines.THREE_LINES_OF_10.matcher("apple\nwatermelons").matches());
  }

  @Test
  void counting() {
    assertEquals(3, LimitLines.countLines("a\nb\r\nc"));
    assertEquals(1, LimitLines.countLines(""));
    assertEquals(3, "one\ntwo\r\nthree".split("\\R", -1).length);
    assertEquals(3, "a\nb\r\nc".split("\\R", -1).length);
    assertEquals(3, "a\nb\n".split("\\R", -1).length);
    assertEquals(2, "a\nb\n".split("\\R").length);
    assertEquals(2, "one\ntwo\n".lines().count());
    assertEquals(2, "a\nb\n".lines().count());
    assertEquals(0, "".lines().count());
    assertEquals(1, "a\u2028b".lines().count());
    assertTrue("a\nb\nc".lines().count() <= 3);
  }
}

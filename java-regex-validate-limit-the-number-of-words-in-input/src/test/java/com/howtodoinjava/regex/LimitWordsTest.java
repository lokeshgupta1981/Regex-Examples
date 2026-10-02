package com.howtodoinjava.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class LimitWordsTest {

  @Test
  void betweenTwoAndTenWords() {
    Pattern p = LimitWords.WORDS_2_TO_10;
    assertTrue(p.matcher("Hello, world!").matches());
    assertTrue(p.matcher("  apple ,  banana  ").matches());
    assertTrue(p.matcher("one-two-three 4 5").matches());
    assertFalse(p.matcher("Hello").matches());
    assertFalse(p.matcher("").matches());
    assertTrue(p.matcher("This is a really long sentence with too many words").matches());
    assertFalse(p.matcher("one two three four five six seven eight nine ten eleven").matches());
    assertTrue(p.matcher("one two three four five six seven eight nine ten eleven").find());
  }

  @Test
  void wordBoundaryStopsSplittingOneWord() {
    assertTrue(LimitWords.WORDS_2_TO_10_NO_BOUNDARY.matcher("Hello").matches());
    assertFalse(LimitWords.WORDS_2_TO_10.matcher("Hello").matches());
    assertFalse(LimitWords.WORDS_2_TO_10_POSSESSIVE.matcher("Hello").matches());
  }

  @Test
  void wordBoundaryKeepsLongInputFast() {
    String longInput = "abcdefghijklmnopqrst ".repeat(40) + "x y z";
    Pattern boundary = Pattern.compile("\\W*(?:\\w+\\b\\W*){0,41}");
    assertTimeoutPreemptively(Duration.ofSeconds(1), () -> assertFalse(boundary.matcher(longInput).matches()));
    Pattern possessive = Pattern.compile("\\W*(?:\\w++\\W*){0,41}");
    assertTimeoutPreemptively(Duration.ofSeconds(1), () -> assertFalse(possessive.matcher(longInput).matches()));
  }

  @Test
  void unicodeApostrophesAndHyphens() {
    String coffee = "caf\u00e9 cr\u00e8me";
    assertFalse(LimitWords.WORDS_1_TO_2.matcher(coffee).matches());
    assertTrue(LimitWords.WORDS_1_TO_2_UNICODE.matcher(coffee).matches());
    assertEquals(3, LimitWords.count("\\w+", coffee));
    assertEquals(2, LimitWords.count("(?U)\\w+", coffee));
    assertEquals(3, LimitWords.count("\\w+", "don't stop"));
    assertEquals(2, LimitWords.count("\\S+", "don't stop"));
    assertEquals(5, LimitWords.count("\\w+", "don't stop well-known"));
    assertEquals(3, LimitWords.count("\\S+", "don't stop well-known"));
  }

  @Test
  void whitespaceSeparatedTokens() {
    Pattern p = LimitWords.TOKENS_1_TO_3;
    assertTrue(p.matcher("don't stop now").matches());
    assertTrue(p.matcher("well-known C++ tips").matches());
    assertFalse(p.matcher("a b c d").matches());
    assertFalse(p.matcher("   ").matches());
    assertTrue(p.matcher("Hello").matches());
  }

  @Test
  void countingWords() {
    String sentence = "don't stop well-known, 42 caf\u00e9!";
    assertEquals(7, LimitWords.count("\\w+", sentence));
    assertEquals(7, LimitWords.count("(?U)\\w+", sentence));
    assertEquals(5, LimitWords.count("\\S+", sentence));
    assertEquals(List.of("don't", "stop", "well-known", "42", "caf\u00e9"),
        LimitWords.NATURAL_WORD.matcher(sentence).results().map(r -> r.group()).toList());
    assertEquals(5, LimitWords.countWithBreakIterator(sentence));
    assertTrue(LimitWords.isWithinWordLimit(sentence, 1, 5));
    assertFalse(LimitWords.isWithinWordLimit(sentence, 1, 4));
    assertEquals(1, "".split("\\s+").length);
    assertEquals(3, " a b".split("\\s+").length);
    assertEquals(2, " a b".strip().split("\\s+").length);
  }
}

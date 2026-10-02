package com.howtodoinjava.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.Test;

class TrademarkSymbolTest {

  static final String TEXT = "Java\u2122 and Duke\u2122 rule";

  @Test
  void quickReference() {
    assertTrue("Java\u2122".matches("Java\\u2122"));
    assertTrue(TrademarkSymbol.TM.matcher(TEXT).find());
    assertFalse(TrademarkSymbol.TM.matcher("Java").find());
    assertEquals(2, TrademarkSymbol.TM.matcher(TEXT).results().count());
    assertEquals(List.of("Java", "Duke"), TrademarkSymbol.BRAND.matcher(TEXT).results().map(r -> r.group(1)).toList());
    assertEquals("Java and Duke rule", TEXT.replaceAll("\\u2122", ""));
    assertEquals("Java\u2122", "Java(TM)".replaceAll("\\((?i:tm)\\)", "\u2122"));
  }

  @Test
  void positions() {
    assertEquals(List.of(4, 14), TrademarkSymbol.TM.matcher(TEXT).results().map(MatchResult::start).toList());
    assertEquals(List.of(5, 15), TrademarkSymbol.TM.matcher(TEXT).results().map(MatchResult::end).toList());
    assertEquals(4, TEXT.indexOf('\u2122'));
    assertTrue(TEXT.contains("\u2122"));
  }

  @Test
  void spellings() {
    String tm = "\u2122";
    for (String regex : List.of("\u2122", "\\u2122", "\\x{2122}", "\\N{TRADE MARK SIGN}", "\\p{So}",
        "\\p{InLetterlikeSymbols}")) {
      assertTrue(Pattern.compile(regex).matcher(tm).matches(), regex);
    }
    assertFalse(tm.matches("\\p{Sc}"));
    assertFalse(tm.matches("\\x2122"));
    assertTrue("!22".matches("\\x2122"));
    PatternSyntaxException e = assertThrows(PatternSyntaxException.class, () -> Pattern.compile("\\u{2122}"));
    assertEquals("Illegal Unicode escape sequence", e.getDescription());
  }

  @Test
  void brandsAndBoundaries() {
    assertEquals(List.of(0, 10), TrademarkSymbol.BRAND.matcher(TEXT).results().map(MatchResult::start).toList());
    assertEquals(List.of("Duke"), TrademarkSymbol.BRAND.matcher("Duke \u2122").results().map(r -> r.group(1)).toList());
    assertFalse(Pattern.compile("\\bJava\\u2122\\b").matcher("Java\u2122").find());
    assertTrue(Pattern.compile("\\bJava\\u2122").matcher("Java\u2122").find());
  }

  @Test
  void relatedSymbols() {
    for (int c : new int[] {0x2122, 0x00AE, 0x00A9, 0x2120, 0x2117}) {
      String s = new String(Character.toChars(c));
      assertTrue(s.matches("\\p{So}"));
      assertTrue(TrademarkSymbol.MARKS.matcher(s).matches());
    }
    String legal = "Java\u2122, Duke\u00ae, \u00a9 2026";
    assertEquals(List.of("\u2122", "\u00ae", "\u00a9"),
        TrademarkSymbol.MARKS.matcher(legal).results().map(MatchResult::group).toList());
    assertEquals("Java, Duke,  2026", TrademarkSymbol.MARKS.matcher(legal).replaceAll(""));
  }

  @Test
  void otherSpellingsAndNormalization() {
    String mixed = "Java(TM), Duke (tm), Kotlin&trade; and Scala&#8482;";
    assertEquals("Java\u2122, Duke\u2122, Kotlin\u2122 and Scala\u2122",
        TrademarkSymbol.TM_SPELLINGS.matcher(mixed).replaceAll("\u2122"));
    String emojiStyle = "\u2122\ufe0f";
    assertFalse(emojiStyle.matches("\\u2122"));
    assertTrue(emojiStyle.matches("\\u2122\\ufe0f?"));
    assertTrue(TrademarkSymbol.TM.matcher(emojiStyle).find());
    assertEquals("TM", Normalizer.normalize("\u2122", Normalizer.Form.NFKC));
    assertEquals("SM", Normalizer.normalize("\u2120", Normalizer.Form.NFKC));
    assertEquals("\u00ae", Normalizer.normalize("\u00ae", Normalizer.Form.NFKC));
  }
}

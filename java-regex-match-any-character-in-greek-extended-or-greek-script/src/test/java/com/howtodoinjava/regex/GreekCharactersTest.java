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

class GreekCharactersTest {

  @Test
  void scriptAndBlockBasics() {
    assertTrue("\u03b1".matches("\\p{IsGreek}"));
    assertTrue("\u1f00".matches("\\p{IsGreek}"));
    assertTrue("\u03b1".matches("\\p{InGreek}"));
    assertFalse("\u1f00".matches("\\p{InGreek}"));
    assertTrue("\u1f00".matches("\\p{InGreekExtended}"));
    assertFalse("\u03b1".matches("\\p{InGreekExtended}"));
    assertTrue("\u03e2".matches("\\p{InGreek}"));
    assertFalse("\u03e2".matches("\\p{IsGreek}"));
    assertTrue("\u03b1".matches("[\\u0370-\\u03FF\\u1F00-\\u1FFF]"));
    assertTrue("\u1f00".matches("[\\p{InGreek}\\p{InGreekExtended}]"));
  }

  @Test
  void findGreekLettersWithPositions() {
    List<MatchResult> found = GreekCharacters.GREEK_SCRIPT.matcher("a + b = \u03b1 + \u03b2").results().toList();
    assertEquals(2, found.size());
    assertEquals("\u03b1", found.get(0).group());
    assertEquals(8, found.get(0).start());
    assertEquals(9, found.get(0).end());
    assertEquals("\u03b2", found.get(1).group());
    assertEquals(12, found.get(1).start());
    assertFalse(GreekCharacters.GREEK_SCRIPT.matcher("hello").find());

    List<MatchResult> ext = GreekCharacters.GREEK_EXTENDED_BLOCK.matcher("\u1fb2 \u1fa8").results().toList();
    assertEquals(2, ext.size());
    assertEquals(0, ext.get(0).start());
    assertEquals(2, ext.get(1).start());
  }

  @Test
  void scriptVersusBlockEdgeCases() {
    assertTrue("\u037e".matches("\\p{InGreek}"));
    assertFalse("\u037e".matches("\\p{IsGreek}"));
    assertTrue("\u0378".matches("\\p{InGreek}"));
    assertFalse("\u0378".matches("\\p{IsGreek}"));
    assertFalse("\u2126".matches("\\p{InGreek}"));
    assertTrue("\u2126".matches("\\p{IsGreek}"));
    assertTrue(GreekCharacters.cp(0x10140).matches("\\p{IsGreek}"));
    assertFalse("\u00b5".matches("\\p{IsGreek}"));
    assertEquals(Character.UnicodeBlock.GREEK, Character.UnicodeBlock.of(0x03e2));
    assertEquals(Character.UnicodeScript.COPTIC, Character.UnicodeScript.of(0x03e2));
  }

  @Test
  void codePointCountsOnJava25() {
    long greekScript = 0;
    long outsideBlocks = 0;
    for (int c = 0; c <= Character.MAX_CODE_POINT; c++) {
      if (Character.UnicodeScript.of(c) == Character.UnicodeScript.GREEK) {
        greekScript++;
        Character.UnicodeBlock b = Character.UnicodeBlock.of(c);
        if (b != Character.UnicodeBlock.GREEK && b != Character.UnicodeBlock.GREEK_EXTENDED) {
          outsideBlocks++;
        }
        assertTrue(GreekCharacters.cp(c).matches("\\p{IsGreek}"));
      }
    }
    assertEquals(518, greekScript);
    assertEquals(168, outsideBlocks);
  }

  @Test
  void propertyNameSpellings() {
    for (String ok : List.of("\\p{IsGreek}", "\\p{script=Greek}", "\\p{sc=Greek}", "\\p{IsGrek}", "\\p{InGreek}",
        "\\p{InGreekandCoptic}", "\\p{block=Greek}")) {
      assertTrue(GreekCharacters.matches(ok, "\u03b1"), ok);
    }
    assertFalse(GreekCharacters.matches("\\p{blk=GreekExtended}", "\u03b1"));
    assertFalse(GreekCharacters.matches("\\p{InGreek_Extended}", "\u03b1"));
    assertTrue(GreekCharacters.matches("\\p{InGreek_Extended}", "\u1f00"));
    PatternSyntaxException e = assertThrows(PatternSyntaxException.class, () -> Pattern.compile("\\p{Greek}"));
    assertEquals("Unknown character property name {Greek}", e.getDescription());
    assertThrows(PatternSyntaxException.class, () -> Pattern.compile("\\p{InGreek_and_Coptic}"));
    assertTrue(GreekCharacters.matches("\\p{IsGREEK}", "\u03b1"));
    assertThrows(PatternSyntaxException.class, () -> Pattern.compile("\\p{isgreek}"));
    assertTrue(GreekCharacters.matches("\\p{InGreek_Extended}", "\u1f00"));
  }

  @Test
  void wordsAccentsAndCase() {
    String kalimera = "\u039a\u03b1\u03bb\u03b7\u03bc\u03ad\u03c1\u03b1";
    String nfd = Normalizer.normalize(kalimera, Normalizer.Form.NFD);
    assertEquals(8, kalimera.length());
    assertEquals(9, nfd.length());
    assertTrue(kalimera.matches("\\p{IsGreek}+"));
    assertFalse(nfd.matches("\\p{IsGreek}+"));
    assertTrue(GreekCharacters.GREEK_WORD.matcher(nfd).matches());
    String mixed = "Hello " + kalimera + " and \u03b1\u03b2\u03b3!";
    assertEquals(List.of(kalimera, "\u03b1\u03b2\u03b3"),
        GreekCharacters.GREEK_WORD.matcher(mixed).results().map(MatchResult::group).toList());
    assertFalse(GreekCharacters.matches("(?i)\\u03b1", "\u0391"));
    assertTrue(GreekCharacters.matches("(?iu)\\u03b1", "\u0391"));
    assertTrue(Pattern.compile("\u03b1", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher("\u0391").matches());
    assertTrue(GreekCharacters.matches("(?iu)\\u03a3", "\u03c2"));
    assertEquals("\u03a9", Normalizer.normalize("\u2126", Normalizer.Form.NFC));
    assertEquals("\u03bc", Normalizer.normalize("\u00b5", Normalizer.Form.NFKC));
  }
}

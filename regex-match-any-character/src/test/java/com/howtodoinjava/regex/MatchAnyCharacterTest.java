package com.howtodoinjava.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class MatchAnyCharacterTest {

  @Test
  void dotMatchesExactlyOneCharacter() {
    assertTrue(Pattern.matches(".", "a"));
    assertTrue(Pattern.matches(".", "#"));
    assertFalse(Pattern.matches(".", "ab"));
    assertFalse("AB".matches("A.B"));
  }

  @Test
  void dotDoesNotMatchLineTerminatorsByDefault() {
    for (char c : new char[] {'\n', '\r', '\u0085', (char) 0x2028, (char) 0x2029}) {
      assertFalse(Pattern.matches(".", String.valueOf(c)), "char " + (int) c);
    }
    assertTrue(Pattern.matches(".", "\t"));
  }

  @Test
  void dotallInlineFlagAndCharacterClassMatchNewlines() {
    String text = "line1\nline2";
    assertFalse(Pattern.matches(".*", text));
    assertTrue(Pattern.compile(".*", Pattern.DOTALL).matcher(text).matches());
    assertTrue(Pattern.matches("(?s).*", text));
    assertTrue(Pattern.matches("[\\s\\S]*", text));
  }

  @Test
  void quantifiers() {
    assertTrue("".matches(".*"));
    assertFalse("".matches(".+"));
    assertTrue("abc".matches(".{3}"));
    assertFalse("abcde".matches(".{2,4}"));
  }

  @Test
  void greedyVersusLazy() {
    String html = "<b>Java</b> and <b>Regex</b>";
    assertEquals("<b>Java</b> and <b>Regex</b>", MatchAnyCharacter.firstMatch("<b>.*</b>", html));
    assertEquals("<b>Java</b>", MatchAnyCharacter.firstMatch("<b>.*?</b>", html));
    List<String> inside = Pattern.compile("\\((.*?)\\)").matcher("call(a) and (b)")
        .results().map(r -> r.group(1)).toList();
    assertEquals(List.of("a", "b"), inside);
  }

  @Test
  void matchesNeedsTheWholeInputButFindDoesNot() {
    assertFalse(Pattern.compile(".").matcher("ab").matches());
    assertTrue(Pattern.compile(".").matcher("ab").find());
    assertEquals("cat", MatchAnyCharacter.firstMatch("c.t", "the cat sat"));
    assertTrue("hello".matches("h.{4}"));
  }

  @Test
  void literalDotNeedsEscaping() {
    assertTrue("abc".matches("a.c"));
    assertFalse("abc".matches("a\\.c"));
    assertTrue("a.c".matches("a\\.c"));
    assertTrue("a.c".matches("a[.]c"));
    assertEquals("\\Qa.c\\E", Pattern.quote("a.c"));
    assertFalse("abc".matches(Pattern.quote("a.c")));
  }

  @Test
  void characterClasses() {
    assertTrue("b".matches("[abc]"));
    assertFalse("d".matches("[abc]"));
    assertTrue("x".matches("[^abc]"));
    assertTrue("\n".matches("[^abc]"));
    assertTrue("abc123".matches("[a-zA-Z0-9]+"));
    assertFalse("abc 123".matches("[a-zA-Z0-9]+"));
    assertTrue("@".matches("[^a-zA-Z0-9\\s]"));
    assertTrue("_".matches("\\p{Punct}"));
  }

  @Test
  void lettersOutsideAsciiNeedUnicodeClasses() {
    String eAcute = "é";
    assertFalse(eAcute.matches("[a-zA-Z]"));
    assertTrue(eAcute.matches("\\p{L}"));
    assertFalse(eAcute.matches("\\w"));
    assertTrue(Pattern.compile("\\w", Pattern.UNICODE_CHARACTER_CLASS).matcher(eAcute).matches());
  }
}

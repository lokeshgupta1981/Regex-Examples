package com.howtodoinjava.regex;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.Test;

class MetaCharactersTest {

  @Test
  void contextCharactersAreLiteralOutsideTheirContext() {
    assertTrue("a-b".matches("a-b"));
    assertTrue("a=b".matches("a=b"));
    assertTrue("a}".matches("a}"));
    assertTrue("@#".matches("@#"));
  }

  @Test
  void dot() {
    assertTrue("cat".matches("c.t"));
    assertFalse("ct".matches("c.t"));
    assertFalse("c\nt".matches("c.t"));
    assertEquals(List.of("cat", "bat", "rat"), MetaCharacters.allMatches(".at", "cat bat rat"));
  }

  @Test
  void characterClasses() {
    assertTrue("b".matches("[abc]"));
    assertTrue("x".matches("[^abc]"));
    assertTrue("e".matches("[a-f]"));
    assertTrue("k".matches("[a-z&&[^aeiou]]"));
    assertFalse("a".matches("[a-z&&[^aeiou]]"));
    assertTrue("-".matches("[a-z-]"));
    assertTrue("^".matches("[a^]"));
    assertTrue("]".matches("[\\]]"));
    assertTrue(".".matches("[.]"));
  }

  @Test
  void predefinedClasses() {
    assertTrue("7".matches("\\d"));
    assertTrue("a".matches("\\D"));
    assertTrue("user_1".matches("\\w+"));
    assertTrue("-".matches("\\W"));
    assertTrue("\t".matches("\\s"));
    assertTrue("a".matches("\\S"));
    assertTrue("\u00e9".matches("\\p{L}"));
    assertTrue("A".matches("\\p{Lu}"));
    assertTrue("!".matches("\\p{Punct}"));
  }

  @Test
  void quantifiers() {
    assertTrue("".matches("a*"));
    assertFalse("".matches("a+"));
    assertTrue("color".matches("colou?r"));
    assertTrue("colour".matches("colou?r"));
    assertTrue("aaa".matches("a{3}"));
    assertTrue("aaaa".matches("a{2,}"));
    assertFalse("aaaa".matches("a{2,3}"));
  }

  @Test
  void greedyLazyPossessive() {
    assertEquals("aaa", MetaCharacters.firstMatch("a+", "aaa"));
    assertEquals("a", MetaCharacters.firstMatch("a+?", "aaa"));
    assertTrue("aaa".matches("a*a"));
    assertFalse("aaa".matches("a*+a"));
  }

  @Test
  void anchorsAndBoundaries() {
    assertTrue(Pattern.compile("^cat").matcher("cat sat").find());
    assertFalse(Pattern.compile("^sat").matcher("cat sat").find());
    assertTrue(Pattern.compile("sat$").matcher("cat sat").find());
    assertTrue(Pattern.compile("\\bcat\\b").matcher("the cat").find());
    assertFalse(Pattern.compile("\\bcat\\b").matcher("category").find());
    assertTrue(Pattern.compile("\\Bcat").matcher("concat").find());
  }

  @Test
  void groupsAndAlternation() {
    assertTrue("abab".matches("(ab)+"));
    assertFalse("abab".matches("ab+"));
    assertTrue("abbb".matches("ab+"));
    assertTrue("gray".matches("gr(a|e)y"));
    assertFalse("gray".matches("gra|ey"));
    assertTrue("dog".matches("cat|dog"));

    Matcher name = Pattern.compile("(?<first>\\w+) (?<last>\\w+)").matcher("Lokesh Gupta");
    assertTrue(name.matches());
    assertEquals("Lokesh", name.group(1));
    assertEquals("Gupta", name.group("last"));
    assertEquals(2, name.groupCount());
    assertEquals(0, Pattern.compile("(?:ab)+").matcher("abab").groupCount());

    assertTrue("hello hello".matches("(\\w+) \\1"));
    assertFalse("hello world".matches("(\\w+) \\1"));
    assertEquals("Gupta, Lokesh", "Lokesh Gupta".replaceAll("(\\w+) (\\w+)", "$2, $1"));
  }

  @Test
  void lookaround() {
    assertEquals("10", MetaCharacters.firstMatch("\\d+(?= USD)", "5 EUR 10 USD"));
    assertEquals("25", MetaCharacters.firstMatch("(?<=\\$)\\d+", "cost $25"));
    assertTrue("abc1".matches("(?=.*\\d).+"));
    assertFalse("abc".matches("(?=.*\\d).+"));
    assertTrue("cat".matches("(?!dog)\\w+"));
    assertEquals("Look-behind group does not have an obvious maximum length",
        assertThrows(PatternSyntaxException.class, () -> Pattern.compile("(?<=(ab)+)c")).getDescription());
  }

  @Test
  void escaping() {
    assertFalse("1+1".matches("1+1"));
    assertTrue("11".matches("1+1"));
    assertTrue("1+1".matches("1\\+1"));
    assertTrue("1+1".matches("1[+]1"));
    assertTrue("1+1".matches("\\Q1+1\\E"));
    assertEquals("\\Q1+1\\E", Pattern.quote("1+1"));
    assertTrue("1+1".matches(Pattern.quote("1+1")));
    assertTrue("C:\\temp".matches("C:\\\\temp"));
    assertTrue("(1)".matches("\\(1\\)"));
  }

  @Test
  void splitTakesARegex() {
    assertArrayEquals(new String[0], "a.b.c".split("."));
    assertArrayEquals(new String[] {"a", "b", "c"}, "a.b.c".split("\\."));
    assertArrayEquals(new String[] {"a", "|", "b"}, "a|b".split("|"));
    assertArrayEquals(new String[] {"a", "b"}, "a|b".split("\\|"));
    PatternSyntaxException e = assertThrows(PatternSyntaxException.class, () -> "1+1".split("+"));
    assertEquals("Dangling meta character '+'", e.getDescription());
  }

  @Test
  void replacementString() {
    IndexOutOfBoundsException e = assertThrows(IndexOutOfBoundsException.class,
        () -> "cost 5".replaceAll("\\d+", "$10"));
    assertEquals("No group 1", e.getMessage());
    assertEquals("cost $10", "cost 5".replaceAll("\\d+", "\\$10"));
    assertEquals("cost $10", "cost 5".replaceAll("\\d+", Matcher.quoteReplacement("$10")));
    assertEquals("\\$10", Matcher.quoteReplacement("$10"));
  }

  @Test
  void invalidPatterns() {
    assertEquals("Dangling meta character '*'",
        assertThrows(PatternSyntaxException.class, () -> Pattern.compile("*abc")).getDescription());
    assertEquals("Unclosed character class",
        assertThrows(PatternSyntaxException.class, () -> Pattern.compile("[abc")).getDescription());
    assertEquals("Unclosed group",
        assertThrows(PatternSyntaxException.class, () -> Pattern.compile("(abc")).getDescription());
  }

  @Test
  void inlineFlags() {
    assertTrue("HELLO".matches("(?i)hello"));
    assertFalse("a\nb".matches("a.b"));
    assertTrue("a\nb".matches("(?s)a.b"));
    assertTrue(Pattern.compile("(?m)^b").matcher("a\nb").find());
    assertTrue("123-4567".matches("(?x) \\d{3} - \\d{4}  # number"));
    assertTrue("HELLO world".matches("(?i:hello) world"));
    assertFalse("HELLO WORLD".matches("(?i:hello) world"));
  }
}

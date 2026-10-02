package com.howtodoinjava.regex;

import static com.howtodoinjava.regex.WordBoundary.LOG;
import static com.howtodoinjava.regex.WordBoundary.all;
import static com.howtodoinjava.regex.WordBoundary.find;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class WordBoundaryTest {

  @Test
  void quickReference() {
    assertTrue(find("\\bcat\\b", "the cat sat"));
    assertFalse(find("\\bcat\\b", "category"));
  }

  @Test
  void whereWordBoundaryMatches() {
    assertEquals("|cat| |sat|", "cat sat".replaceAll("\\b", "|"));
    assertEquals("a c|a|t", "a cat".replaceAll("\\B", "|"));
    assertEquals(List.of("h", "b", "w"), all("\\b\\w", "hello big world"));
    assertEquals(List.of("o", "g", "d"), all("\\w\\b", "hello big world"));
  }

  @Test
  void wordCharacters() {
    assertEquals("|e|-|mail|", "e-mail".replaceAll("\\b", "|"));
    assertEquals("|don|'|t|", "don't".replaceAll("\\b", "|"));
    assertEquals("|user_1|", "user_1".replaceAll("\\b", "|"));
    assertTrue(find("\\bmail\\b", "my e-mail"));
    assertFalse(find("(?<![\\w-])mail(?![\\w-])", "my e-mail"));
    assertTrue(find("(?<![\\w-])mail(?![\\w-])", "mail box"));
    String ete = "\u00e9t\u00e9";
    assertEquals(List.of("t"), all("\\b\\w+\\b", ete));
    assertEquals(List.of(ete), all("(?U)\\b\\w+\\b", ete));
  }

  @Test
  void prefix() {
    assertEquals(List.of("undo", "unknown"), all("\\bun\\w*", "undo the unknown fun"));
    assertEquals(List.of("undo", "unknown", "un"), all("un\\w*", "undo the unknown fun"));
    assertEquals(List.of("Undo", "unknown"), all("(?i)\\bun\\w*", "Undo the unknown fun"));
    assertEquals(List.of("Lokesh", "Delhi"), all("\\b[A-Z]\\w*", "Lokesh lives in Delhi"));
  }

  @Test
  void suffix() {
    assertEquals(List.of("walking", "talking", "morning"),
        all("\\w+ing\\b", "walking and talking in the morning"));
    assertEquals(List.of("singing"), all("\\w+ing\\b", "singing birds and a kingdom"));
    assertEquals(List.of("singing", "king"), all("\\w+ing", "singing birds and a kingdom"));
  }

  @Test
  void linesThatStartOrEndWithAWord() {
    assertEquals(List.of("ERROR disk full", "ERRORS: 0"), all("(?m)^ERROR.*", LOG));
    assertEquals(List.of("ERROR disk full"), all("(?m)^ERROR\\b.*", LOG));
    assertEquals(List.of("WARN retry done"), all("(?m)^.*\\bdone$", LOG));
  }

  @Test
  void nonWordBoundary() {
    assertEquals(List.of("cat"), all("\\Bcat\\B", "concatenate cat"));
    assertTrue(find("\\Bcat", "concat"));
    assertFalse(find("\\Bcat", "cat"));
  }

  @Test
  void endOfPreviousMatch() {
    assertEquals(List.of("1", "2", "3"), all("\\G\\d", "123abc456"));
    assertEquals(List.of("1", "2", "3", "4", "5", "6"), all("\\d", "123abc456"));
    assertEquals(List.of("dog"), all("\\Gdog", "dog dog"));
  }

  @Test
  void commonMistakes() {
    assertFalse(find("\bcat\b", "the cat"));
    assertEquals("\u0008", "\b");
    assertFalse("the cat".matches("\\bcat\\b"));
    assertFalse(find("\\bC\\+\\+\\b", "I like C++ code"));
    assertTrue(find("(?<!\\w)C\\+\\+(?!\\w)", "I like C++ code"));
    assertFalse(find("(?<!\\w)C\\+\\+(?!\\w)", "I like C++11"));
  }
}

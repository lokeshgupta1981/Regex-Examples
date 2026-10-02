package com.howtodoinjava.regex;

import static com.howtodoinjava.regex.ExactWord.CAT;
import static com.howtodoinjava.regex.ExactWord.all;
import static com.howtodoinjava.regex.ExactWord.find;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ExactWordTest {

  @Test
  void quickReference() {
    assertTrue(find("\\bcat\\b", "The cat is cute"));
    assertFalse(find("\\bcat\\b", "The category is empty"));
    assertTrue(find("(?i)\\bcat\\b", "CAT and dog"));
    assertEquals(List.of("cat", "category", "noncategory"),
        all("\\b\\w*cat\\w*\\b", "cat, category, noncategory"));
    assertTrue("a cat and a dog".matches("(?=.*\\bcat\\b)(?=.*\\bdog\\b).*"));
  }

  @Test
  void oneExactWord() {
    assertTrue(CAT.matcher("The cat is cute").find());
    assertFalse(CAT.matcher("The category is empty").find());
    assertFalse(CAT.matcher("The noncategory is empty").find());
    assertTrue(CAT.matcher("a cat-like toy").find());
    assertEquals(List.of(2, 11), CAT.matcher("a cat, the cat.").results().map(MatchResult::start).toList());
  }

  @Test
  void exactWordOrExactString() {
    assertTrue("cat".matches("cat"));
    assertFalse("the cat".matches("cat"));
    assertTrue("the cat".matches(".*\\bcat\\b.*"));
  }

  @Test
  void ignoringCase() {
    assertTrue(Pattern.compile("\\bcat\\b", Pattern.CASE_INSENSITIVE).matcher("My Cat").find());
    assertTrue(find("(?i)\\bcat\\b", "CAT"));
    assertFalse("CAF\u00c9".matches("(?i)caf\u00e9"));
    assertTrue("CAF\u00c9".matches("(?iu)caf\u00e9"));
  }

  @Test
  void wordsContainingASubstring() {
    assertEquals(List.of("cat", "category", "noncategory"),
        all("\\b\\w*cat\\w*\\b", "The cat, a category and a noncategory"));
    assertEquals(List.of("category"), all("\\b\\w*cat\\w*\\b", "the non-category"));
    assertEquals(List.of("Catalog", "Bobcat"), all("(?i)\\b\\w*cat\\w*\\b", "Catalog and Bobcat"));
    assertTrue("category".contains("cat"));
  }

  @Test
  void anyOfSeveralWords() {
    assertEquals(List.of("dog", "cat"), all("\\b(?:cat|dog)\\b", "hotdog, a dog and a cat"));
    assertEquals(List.of("dog", "dog", "cat"), all("\\bcat|dog\\b", "hotdog, a dog and a cat"));
    String anyWord = List.of("cat", "dog").stream()
        .map(Pattern::quote)
        .collect(Collectors.joining("|", "\\b(?:", ")\\b"));
    assertEquals("\\b(?:\\Qcat\\E|\\Qdog\\E)\\b", anyWord);
    assertEquals(List.of("dog", "cat"), all(anyWord, "hotdog, a dog and a cat"));
  }

  @Test
  void allOfSeveralWords() {
    Pattern both = Pattern.compile("(?s)(?=.*\\bcat\\b)(?=.*\\bdog\\b).*");
    assertTrue(both.matcher("the dog chased the cat").matches());
    assertFalse(both.matcher("the dog chased the category").matches());
    assertTrue(both.matcher("a dog\nand a cat").matches());
    String text = "the dog chased the cat";
    assertTrue(List.of("cat", "dog").stream()
        .allMatch(w -> Pattern.compile("\\b" + Pattern.quote(w) + "\\b").matcher(text).find()));
  }

  @Test
  void wordFromUserInput() {
    String version = "v1.2";
    assertTrue(Pattern.compile("\\b" + version + "\\b").matcher("v152").find());
    assertFalse(Pattern.compile("\\b" + Pattern.quote(version) + "\\b").matcher("v152").find());
    assertTrue(Pattern.compile("\\b" + Pattern.quote(version) + "\\b").matcher("see v1.2 notes").find());
    assertFalse(Pattern.compile("\\b" + Pattern.quote(version) + "\\b").matcher("see v1.25 notes").find());
  }

  @Test
  void countingAndReplacing() {
    assertEquals(2, CAT.matcher("cat category cat").results().count());
    assertEquals("dog category dog", "cat category cat".replaceAll("\\bcat\\b", "dog"));
    assertEquals("dog dogegory dog", "cat category cat".replace("cat", "dog"));
  }

  @Test
  void withoutARegex() {
    assertTrue("The category is empty".contains("cat"));
    assertFalse("The category is empty".equals("cat"));
    assertFalse("The category is empty".equalsIgnoreCase("cat"));
    assertTrue(Arrays.asList("The cat, is cute".split("\\W+")).contains("cat"));
  }
}

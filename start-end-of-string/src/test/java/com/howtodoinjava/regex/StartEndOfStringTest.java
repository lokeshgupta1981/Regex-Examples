package com.howtodoinjava.regex;

import static com.howtodoinjava.regex.StartEndOfString.FRUITS;
import static com.howtodoinjava.regex.StartEndOfString.SENTENCES;
import static com.howtodoinjava.regex.StartEndOfString.all;
import static com.howtodoinjava.regex.StartEndOfString.find;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class StartEndOfStringTest {

  @Test
  void quickReference() {
    assertTrue(find("^The", "The sun is shining"));
    assertTrue(find("ing$", "The sun is shining"));
    assertFalse(find("^sun", "The sun is shining"));
    assertFalse(find("^banana$", FRUITS));
    assertTrue(Pattern.compile("^banana$", Pattern.MULTILINE).matcher(FRUITS).find());
    assertEquals(List.of("apple", "banana", "cherry"), all("(?m)^\\w+", FRUITS));
    assertTrue(find("cat$", "cat\n"));
    assertFalse(find("cat\\z", "cat\n"));
    assertArrayEquals(new String[] {"a", "b", "c"}, "a\r\nb\nc".split("\\R"));
  }

  @Test
  void anchorsMatchAPosition() {
    assertEquals(">cat", "cat".replaceAll("^", ">"));
    assertEquals("cat!", "cat".replaceAll("$", "!"));
    Matcher end = Pattern.compile("$").matcher("cat");
    assertTrue(end.find());
    assertEquals(3, end.start());
    assertEquals("", end.group());
  }

  @Test
  void startOfString() {
    assertTrue(find("^\\d", "1st place"));
    assertTrue(find("^[a-zA-Z]", "Lokesh"));
    assertTrue(find("^Hello", "Hello world"));
    assertFalse(find("^Hello", "Say Hello"));
    assertTrue(find("^[^a-zA-Z0-9]", "#tag"));
    assertTrue(find("(?i)^hello", "HELLO there"));
    assertTrue("Hello world".startsWith("Hello"));
  }

  @Test
  void endOfString() {
    assertTrue(find("\\d$", "order 42"));
    assertTrue(find("world$", "Hello world"));
    assertFalse(find("world$", "world peace"));
    assertTrue(find("[.!?]$", "Done!"));
    assertTrue(find("\\.(jpg|png)$", "photo.png"));
    assertFalse(find("\\.(jpg|png)$", "photo.png.txt"));
    assertTrue("photo.png".endsWith(".png"));
  }

  @Test
  void wholeString() {
    assertTrue(find("[a-z]+", "abc123"));
    assertFalse(find("^[a-z]+$", "abc123"));
    assertTrue("abc".matches("[a-z]+"));
    assertTrue(find("^g.*g$", "gang"));
    assertTrue(find("^$", ""));
  }

  @Test
  void multilineMode() {
    assertEquals(List.of("The sun is shining", "The moon is bright"),
        Pattern.compile("^The.*", Pattern.MULTILINE).matcher(SENTENCES).results()
            .map(r -> r.group()).toList());
    assertEquals(List.of("The sun is shining", "Birds are singing"), all("(?m)^.*ing$", SENTENCES));
    assertEquals(List.of(), all("^.*ing$", SENTENCES));
    assertEquals(List.of("The sun is shining", "The moon is bright"),
        SENTENCES.lines().filter(line -> line.startsWith("The")).toList());
  }

  @Test
  void trailingLineBreak() {
    assertTrue(find("cat$", "cat\n"));
    assertFalse("cat\n".matches("cat$"));
    assertEquals("a\nb!\n!", "a\nb\n".replaceAll("$", "!"));
    assertEquals("a!\nb!\n!", "a\nb\n".replaceAll("(?m)$", "!"));
    assertEquals("> a\n> b\n", "a\nb\n".replaceAll("(?m)^", "> "));
  }

  @Test
  void anchorPositionsWithAFinalLineBreak() {
    assertEquals("!abc\n", "abc\n".replaceAll("^", "!"));
    assertEquals("!abc\n", "abc\n".replaceAll("(?m)^", "!"));
    assertEquals("abc!\n!", "abc\n".replaceAll("$", "!"));
    assertEquals("!abc\n", "abc\n".replaceAll("\\A", "!"));
    assertEquals("abc!\n!", "abc\n".replaceAll("\\Z", "!"));
    assertEquals("abc\n!", "abc\n".replaceAll("\\z", "!"));
  }

  @Test
  void inputAnchors() {
    assertTrue(find("(?m)^banana", FRUITS));
    assertFalse(find("(?m)\\Abanana", FRUITS));
    assertTrue(find("(?m)apple$", FRUITS));
    assertFalse(find("(?m)apple\\z", FRUITS));
    assertTrue(find("cat\\Z", "cat\n"));
    assertFalse(find("cat\\z", "cat\n"));
    assertTrue(find("^\\d+$", "123\n"));
    assertFalse(find("\\A\\d+\\z", "123\n"));
    assertFalse("123\n".matches("\\d+"));
  }

  @Test
  void lineTerminators() {
    assertTrue(find("(?m)cat$", "cat\r\ndog"));
    assertFalse(find("(?md)cat$", "cat\r\ndog"));
    assertTrue(find("(?m)^dog", "cat\u2028dog"));
    assertArrayEquals(new String[] {"a", "b", "c", "d"}, "a\r\nb\nc\rd".split("\\R"));
    assertArrayEquals(new String[] {"a\r", "b"}, "a\r\nb".split("\n"));
  }
}

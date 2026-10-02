package com.howtodoinjava.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class CleanAsciiTextTest {

  @Test
  void characterClasses() {
    assertTrue("A".matches("\\p{ASCII}"));
    assertFalse("\u00e9".matches("\\p{ASCII}"));
    assertTrue("\t".matches("\\p{Cntrl}"));
    assertFalse("\u0085".matches("\\p{Cntrl}"));
    assertTrue(" ".matches("\\p{Print}"));
    assertTrue("~".matches("\\p{Print}"));
    assertFalse("\u00e9".matches("\\p{Print}"));
    assertTrue("\u0085".matches("\\p{C}"));
    assertTrue("\u200b".matches("\\p{C}"));
    assertFalse("\u00a0".matches("\\p{C}"));
  }

  @Test
  void cntrlIsExactlyAsciiControlRange() {
    for (int c = 0; c < 0x10000; c++) {
      boolean expected = c <= 0x1F || c == 0x7F;
      assertEquals(expected, String.valueOf((char) c).matches("\\p{Cntrl}"), "char " + c);
    }
  }

  @Test
  void printIsExactlyAscii20To7E() {
    for (int c = 0; c < 0x10000; c++) {
      boolean expected = c >= 0x20 && c <= 0x7E;
      assertEquals(expected, String.valueOf((char) c).matches("\\p{Print}"), "char " + c);
    }
  }

  @Test
  void removeNonAscii() {
    assertEquals("caf", "caf\u00e9".replaceAll("\\P{ASCII}", ""));
    assertEquals("caf", "caf\u00e9".replaceAll("[^\\x00-\\x7F]", ""));
    assertEquals("nave caf", "na\u00efve caf\u00e9".replaceAll("\\P{ASCII}", ""));
    assertEquals("nave caf", "na\u00efve caf\u00e9".replaceAll("[^\\x00-\\x7F]", ""));
    assertEquals("na?ve caf?", "na\u00efve caf\u00e9".replaceAll("\\P{ASCII}", "?"));
    String smile = new String(Character.toChars(0x1F600));
    assertEquals(4, ("hi" + smile).length());
    assertEquals("hi", ("hi" + smile).replaceAll("\\P{ASCII}", ""));
  }

  @Test
  void removeControlCharacters() {
    assertEquals("abc", "a\u0000b\u0007c".replaceAll("\\p{Cntrl}", ""));
    assertEquals("abc", "a\tb\nc".replaceAll("\\p{Cntrl}", ""));
    assertEquals("a\tb\nc", "a\u0007\tb\nc".replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", ""));
    assertEquals("abcd", "a\u0000b\u001Bc\u007Fd".replaceAll("\\p{Cntrl}", ""));
    assertEquals("line1\r\nline2", "line1\r\nline2\u0007".replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", ""));
  }

  @Test
  void keepPrintableAscii() {
    assertEquals("cafbar", "caf\u00e9\tbar".replaceAll("\\P{Print}", ""));
    assertEquals("Price: 10", "Price: 10\u20ac\t".replaceAll("\\P{Print}", ""));
    assertEquals("Price: 10", "Price: 10\u20ac\t".replaceAll("[^\\x20-\\x7E]", ""));
    assertEquals("a\tb\nc", "a\u00e9\tb\nc".replaceAll("[^\\p{Print}\\r\\n\\t]", ""));
  }

  @Test
  void removeInvisibleUnicodeKeepAccents() {
    assertEquals("caf\u00e9", "caf\u00e9\u200b".replaceAll("\\p{C}", ""));
    assertEquals("hello", "\ufeffhello".replaceAll("\\p{C}", ""));
    assertEquals("coop", "co\u00adop\u200b".replaceAll("\\p{Cf}", ""));
    assertEquals("caf\u00e9bar", "caf\u00e9\u200b\nbar".replaceAll("\\p{C}", ""));
    assertEquals("caf\u00e9\nbar", "caf\u00e9\u200b\nbar".replaceAll("[\\p{C}&&[^\\r\\n\\t]]", ""));
    assertEquals("a b", "a\u00a0b".replaceAll("\\h", " "));
    assertTrue(Pattern.compile("\\p{Print}", Pattern.UNICODE_CHARACTER_CLASS).matcher("\u00e9").matches());
    assertTrue(Pattern.compile("\\p{Cntrl}", Pattern.UNICODE_CHARACTER_CLASS).matcher("\u0085").matches());
  }

  @Test
  void stripAccentsWithNormalizer() {
    String nfd = Normalizer.normalize("\u00e9", Normalizer.Form.NFD);
    assertEquals(2, nfd.length());
    assertEquals("e\u0301", nfd);
    assertEquals("cafe naive", CleanAsciiText.stripAccents("caf\u00e9 na\u00efve"));
    assertEquals("Creme brulee", CleanAsciiText.stripAccents("Cr\u00e8me br\u00fbl\u00e9e"));
    assertEquals("nandu", CleanAsciiText.stripAccents("\u00f1and\u00fa"));
    assertEquals("\u00f8 \u00df \u00e6", CleanAsciiText.stripAccents("\u00f8 \u00df \u00e6"));
    assertEquals("\ufb01", CleanAsciiText.stripAccents("\ufb01"));
    assertEquals("fi", Normalizer.normalize("\ufb01", Normalizer.Form.NFKD));
    assertEquals("A", Normalizer.normalize("\uff21", Normalizer.Form.NFKD));
  }

  @Test
  void fullCleaning() {
    assertEquals("Ete fine!", CleanAsciiText.toAscii("\u00c9t\u00e9\u00a0\ufb01ne\u200b!"));
    String dirty = "\ufeffCaf\u00e9\u00a0menu\u200b:\u0007 cr\u00e8me br\u00fbl\u00e9e, \ufb01gs\r\nTotal: 5\u20ac";
    assertEquals("Cafe menu: creme brulee, figs\r\nTotal: 5", CleanAsciiText.toAscii(dirty));
    String filtered = dirty.codePoints().filter(c -> c < 128)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    assertEquals("Cafmenu:\u0007 crme brle, gs\r\nTotal: 5", filtered);
  }

  @Test
  void wrongCharsetCannotBeRepairedByRegex() {
    String wrong = new String("caf\u00e9".getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    assertEquals("caf\u00c3\u00a9", wrong);
    assertEquals("cafA", CleanAsciiText.toAscii(wrong));
  }
}

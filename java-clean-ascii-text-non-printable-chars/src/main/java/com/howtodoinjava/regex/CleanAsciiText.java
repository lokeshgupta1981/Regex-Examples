package com.howtodoinjava.regex;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Non-ASCII characters in the output are printed as Java escapes so the console encoding does not matter.
 * Source code for https://howtodoinjava.com/java/regex/java-clean-ascii-text-non-printable-chars/
 */
public class CleanAsciiText {

  private static final Pattern NON_ASCII = Pattern.compile("\\P{ASCII}");
  private static final Pattern MARKS = Pattern.compile("\\p{M}");
  private static final Pattern CONTROL_EXCEPT_WHITESPACE = Pattern.compile("[\\p{C}&&[^\\r\\n\\t]]");

  /** Converts any text to plain ASCII: accents removed, invisible characters removed, line breaks kept. */
  public static String toAscii(String text) {
    String decomposed = Normalizer.normalize(text, Normalizer.Form.NFKD);
    String noMarks = MARKS.matcher(decomposed).replaceAll("");
    String noControls = CONTROL_EXCEPT_WHITESPACE.matcher(noMarks).replaceAll("");
    return NON_ASCII.matcher(noControls).replaceAll("");
  }

  /** Removes accents only and keeps every other character. */
  public static String stripAccents(String text) {
    return MARKS.matcher(Normalizer.normalize(text, Normalizer.Form.NFD)).replaceAll("");
  }

  public static void main(String[] args) {

    section("Quick reference");
    show("\"caf\\u00e9\".replaceAll(\"\\\\P{ASCII}\", \"\")", "caf\u00e9".replaceAll("\\P{ASCII}", ""));
    show("\"caf\\u00e9\".replaceAll(\"[^\\\\x00-\\\\x7F]\", \"\")", "caf\u00e9".replaceAll("[^\\x00-\\x7F]", ""));
    show("\"a\\u0000b\\u0007c\".replaceAll(\"\\\\p{Cntrl}\", \"\")", "a\u0000b\u0007c".replaceAll("\\p{Cntrl}", ""));
    show("\"a\\tb\\nc\".replaceAll(\"\\\\p{Cntrl}\", \"\")", "a\tb\nc".replaceAll("\\p{Cntrl}", ""));
    show("\"a\\u0007\\tb\\nc\".replaceAll(\"[\\\\p{Cntrl}&&[^\\\\r\\\\n\\\\t]]\", \"\")",
        "a\u0007\tb\nc".replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", ""));
    show("\"caf\\u00e9\\tbar\".replaceAll(\"\\\\P{Print}\", \"\")", "caf\u00e9\tbar".replaceAll("\\P{Print}", ""));
    show("\"caf\\u00e9\\u200b\".replaceAll(\"\\\\p{C}\", \"\")", "caf\u00e9\u200b".replaceAll("\\p{C}", ""));
    show("stripAccents(\"caf\\u00e9 na\\u00efve\")", stripAccents("caf\u00e9 na\u00efve"));
    show("toAscii(\"\\u00c9t\\u00e9\\u00a0\\ufb01ne\\u200b!\")", toAscii("\u00c9t\u00e9\u00a0\ufb01ne\u200b!"));

    section("1. Character classes");
    show("\"A\".matches(\"\\\\p{ASCII}\")", "A".matches("\\p{ASCII}"));
    show("\"\\u00e9\".matches(\"\\\\p{ASCII}\")", "\u00e9".matches("\\p{ASCII}"));
    show("\"\\t\".matches(\"\\\\p{Cntrl}\")", "\t".matches("\\p{Cntrl}"));
    show("\"\\u0085\".matches(\"\\\\p{Cntrl}\")", "\u0085".matches("\\p{Cntrl}"));
    show("\" \".matches(\"\\\\p{Print}\")", " ".matches("\\p{Print}"));
    show("\"~\".matches(\"\\\\p{Print}\")", "~".matches("\\p{Print}"));
    show("\"\\u00e9\".matches(\"\\\\p{Print}\")", "\u00e9".matches("\\p{Print}"));
    show("\"\\u0085\".matches(\"\\\\p{C}\")", "\u0085".matches("\\p{C}"));
    show("\"\\u200b\".matches(\"\\\\p{C}\")", "\u200b".matches("\\p{C}"));
    show("\"\\u00a0\".matches(\"\\\\p{C}\")", "\u00a0".matches("\\p{C}"));

    section("2. Remove non-ASCII characters");
    show("\"na\\u00efve caf\\u00e9\".replaceAll(\"\\\\P{ASCII}\", \"\")", "na\u00efve caf\u00e9".replaceAll("\\P{ASCII}", ""));
    show("\"na\\u00efve caf\\u00e9\".replaceAll(\"[^\\\\x00-\\\\x7F]\", \"\")", "na\u00efve caf\u00e9".replaceAll("[^\\x00-\\x7F]", ""));
    show("\"na\\u00efve caf\\u00e9\".replaceAll(\"\\\\P{ASCII}\", \"?\")", "na\u00efve caf\u00e9".replaceAll("\\P{ASCII}", "?"));
    String smile = new String(Character.toChars(0x1F600));
    show("(\"hi\" + smile).length()", ("hi" + smile).length());
    show("(\"hi\" + smile).replaceAll(\"\\\\P{ASCII}\", \"\")", ("hi" + smile).replaceAll("\\P{ASCII}", ""));

    section("3. Remove control characters");
    show("\"a\\u0000b\\u001Bc\\u007Fd\".replaceAll(\"\\\\p{Cntrl}\", \"\")",
        "a\u0000b\u001Bc\u007Fd".replaceAll("\\p{Cntrl}", ""));
    show("\"line1\\r\\nline2\\u0007\".replaceAll(\"[\\\\p{Cntrl}&&[^\\\\r\\\\n\\\\t]]\", \"\")",
        "line1\r\nline2\u0007".replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", ""));

    section("4. Keep printable ASCII only");
    show("\"Price: 10\\u20ac\\t\".replaceAll(\"\\\\P{Print}\", \"\")", "Price: 10\u20ac\t".replaceAll("\\P{Print}", ""));
    show("\"Price: 10\\u20ac\\t\".replaceAll(\"[^\\\\x20-\\\\x7E]\", \"\")", "Price: 10\u20ac\t".replaceAll("[^\\x20-\\x7E]", ""));
    show("\"a\\u00e9\\tb\\nc\".replaceAll(\"[^\\\\p{Print}\\\\r\\\\n\\\\t]\", \"\")",
        "a\u00e9\tb\nc".replaceAll("[^\\p{Print}\\r\\n\\t]", ""));

    section("5. Remove invisible Unicode characters, keep accents");
    show("\"\\ufeffhello\".replaceAll(\"\\\\p{C}\", \"\")", "\ufeffhello".replaceAll("\\p{C}", ""));
    show("\"co\\u00adop\\u200b\".replaceAll(\"\\\\p{Cf}\", \"\")", "co\u00adop\u200b".replaceAll("\\p{Cf}", ""));
    show("\"caf\\u00e9\\u200b\\nbar\".replaceAll(\"\\\\p{C}\", \"\")", "caf\u00e9\u200b\nbar".replaceAll("\\p{C}", ""));
    show("\"caf\\u00e9\\u200b\\nbar\".replaceAll(\"[\\\\p{C}&&[^\\\\r\\\\n\\\\t]]\", \"\")",
        "caf\u00e9\u200b\nbar".replaceAll("[\\p{C}&&[^\\r\\n\\t]]", ""));
    show("\"a\\u00a0b\".replaceAll(\"\\\\h\", \" \")", "a\u00a0b".replaceAll("\\h", " "));
    show("UNICODE_CHARACTER_CLASS \\p{Print} on \"\\u00e9\"",
        Pattern.compile("\\p{Print}", Pattern.UNICODE_CHARACTER_CLASS).matcher("\u00e9").matches());
    show("UNICODE_CHARACTER_CLASS \\p{Cntrl} on \"\\u0085\"",
        Pattern.compile("\\p{Cntrl}", Pattern.UNICODE_CHARACTER_CLASS).matcher("\u0085").matches());

    section("6. Replace accented letters with ASCII letters");
    String nfd = Normalizer.normalize("\u00e9", Normalizer.Form.NFD);
    show("Normalizer.normalize(\"\\u00e9\", NFD).length()", nfd.length());
    show("Normalizer.normalize(\"\\u00e9\", NFD)", nfd);
    show("stripAccents(\"Cr\\u00e8me br\\u00fbl\\u00e9e\")", stripAccents("Cr\u00e8me br\u00fbl\u00e9e"));
    show("stripAccents(\"\\u00f1and\\u00fa\")", stripAccents("\u00f1and\u00fa"));
    show("stripAccents(\"\\u00f8 \\u00df \\u00e6\")", stripAccents("\u00f8 \u00df \u00e6"));
    show("NFD of \"\\ufb01\" (fi ligature) without marks", stripAccents("\ufb01"));
    show("NFKD of \"\\ufb01\" (fi ligature)", Normalizer.normalize("\ufb01", Normalizer.Form.NFKD));
    show("NFKD of \"\\uff21\" (full-width A)", Normalizer.normalize("\uff21", Normalizer.Form.NFKD));

    section("7. Full cleaning method");
    String dirty = "\ufeffCaf\u00e9\u00a0menu\u200b:\u0007 cr\u00e8me br\u00fbl\u00e9e, \ufb01gs\r\nTotal: 5\u20ac";
    show("dirty", dirty);
    show("toAscii(dirty)", toAscii(dirty));
    String wrong = new String("caf\u00e9".getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    show("UTF-8 bytes of \"caf\\u00e9\" decoded as ISO-8859-1", wrong);
    show("toAscii(wronglyDecoded)", toAscii(wrong));
    show("dirty.codePoints().filter(c -> c < 128)",
        dirty.codePoints().filter(c -> c < 128)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString());
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    String value = result instanceof String s ? "\"" + escape(s) + "\"" : String.valueOf(result);
    System.out.printf("%-70s -> %s%n", expression, value);
  }

  /** Prints control and non-ASCII characters as Java escapes, for example \t or \u00e9. */
  static String escape(String s) {
    StringBuilder sb = new StringBuilder();
    for (char c : s.toCharArray()) {
      switch (c) {
        case '\t' -> sb.append("\\t");
        case '\n' -> sb.append("\\n");
        case '\r' -> sb.append("\\r");
        default -> {
          if (c < 0x20 || c > 0x7E) {
            sb.append(String.format("\\u%04x", (int) c));
          } else {
            sb.append(c);
          }
        }
      }
    }
    return sb.toString();
  }
}

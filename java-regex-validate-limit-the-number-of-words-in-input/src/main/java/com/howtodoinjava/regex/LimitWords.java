package com.howtodoinjava.regex;

import java.text.BreakIterator;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/java-regex-validate-limit-the-number-of-words-in-input/
 */
public class LimitWords {

  /** Between 2 and 10 words; a word is a run of \w characters. */
  static final Pattern WORDS_2_TO_10 = Pattern.compile("\\W*(?:\\w+\\b\\W*){2,10}");

  /** Same pattern without the word boundary: wrong for minimums and slow on long input. */
  static final Pattern WORDS_2_TO_10_NO_BOUNDARY = Pattern.compile("\\W*(?:\\w+\\W*){2,10}");

  /** Same limit with a possessive quantifier in place of \b. */
  static final Pattern WORDS_2_TO_10_POSSESSIVE = Pattern.compile("\\W*(?:\\w++\\W*){2,10}");

  /** 1 to 2 words, ASCII \w. */
  static final Pattern WORDS_1_TO_2 = Pattern.compile("\\W*(?:\\w+\\b\\W*){1,2}");

  /** 1 to 2 words, Unicode \w through the inline (?U) flag. */
  static final Pattern WORDS_1_TO_2_UNICODE = Pattern.compile("(?U)\\W*(?:\\w+\\b\\W*){1,2}");

  /** 1 to 3 words, where a word is any run of non-whitespace characters. */
  static final Pattern TOKENS_1_TO_3 = Pattern.compile("\\s*(?:\\S++\\s*){1,3}");

  /** A word made of letters and digits, with inner apostrophes or hyphens allowed. */
  static final Pattern NATURAL_WORD = Pattern.compile("[\\p{L}\\p{N}]+(?:['-][\\p{L}\\p{N}]+)*");

  static long count(String regex, String text) {
    return Pattern.compile(regex).matcher(text).results().count();
  }

  static long countWithBreakIterator(String text) {
    BreakIterator words = BreakIterator.getWordInstance(Locale.ENGLISH);
    words.setText(text);
    long count = 0;
    int start = words.first();
    for (int end = words.next(); end != BreakIterator.DONE; start = end, end = words.next()) {
      if (Character.isLetterOrDigit(text.codePointAt(start))) {
        count++;
      }
    }
    return count;
  }

  static boolean isWithinWordLimit(String text, int min, int max) {
    long words = NATURAL_WORD.matcher(text).results().count();
    return words >= min && words <= max;
  }

  public static void main(String[] args) {

    section("Quick reference");
    show("WORDS_2_TO_10 \"Hello, world!\"", WORDS_2_TO_10.matcher("Hello, world!").matches());
    show("WORDS_2_TO_10 \"Hello\"", WORDS_2_TO_10.matcher("Hello").matches());
    show("WORDS_2_TO_10 \"one two ... eleven\"",
        WORDS_2_TO_10.matcher("one two three four five six seven eight nine ten eleven").matches());
    show("TOKENS_1_TO_3 \"don't stop now\"", TOKENS_1_TO_3.matcher("don't stop now").matches());
    show("count(\"\\\\w+\", \"don't stop\")", count("\\w+", "don't stop"));
    show("count(\"\\\\S+\", \"don't stop\")", count("\\S+", "don't stop"));

    section("1. Between 2 and 10 words");
    show("\"Hello, world!\"", WORDS_2_TO_10.matcher("Hello, world!").matches());
    show("\"  apple ,  banana  \"", WORDS_2_TO_10.matcher("  apple ,  banana  ").matches());
    show("\"one-two-three 4 5\"", WORDS_2_TO_10.matcher("one-two-three 4 5").matches());
    show("\"Hello\"", WORDS_2_TO_10.matcher("Hello").matches());
    show("\"\"", WORDS_2_TO_10.matcher("").matches());
    show("\"This is a really long sentence with too many words\"",
        WORDS_2_TO_10.matcher("This is a really long sentence with too many words").matches());
    show("\"one two three four five six seven eight nine ten eleven\"",
        WORDS_2_TO_10.matcher("one two three four five six seven eight nine ten eleven").matches());
    show("find() on 11 words", WORDS_2_TO_10.matcher("one two three four five six seven eight nine ten eleven").find());

    section("2. Why \\b matters");
    show("NO_BOUNDARY \"Hello\"", WORDS_2_TO_10_NO_BOUNDARY.matcher("Hello").matches());
    show("WITH \\b \"Hello\"", WORDS_2_TO_10.matcher("Hello").matches());
    show("POSSESSIVE \"Hello\"", WORDS_2_TO_10_POSSESSIVE.matcher("Hello").matches());
    String longInput = "abcdefghijklmnopqrst ".repeat(6) + "x y z";
    Pattern noBoundary7 = Pattern.compile("\\W*(?:\\w+\\W*){0,7}");
    Pattern boundary7 = Pattern.compile("\\W*(?:\\w+\\b\\W*){0,7}");
    long t1 = System.nanoTime();
    boolean r1 = noBoundary7.matcher(longInput).matches();
    long ms1 = (System.nanoTime() - t1) / 1_000_000;
    long t2 = System.nanoTime();
    boolean r2 = boundary7.matcher(longInput).matches();
    long ms2 = (System.nanoTime() - t2) / 1_000_000;
    show("9 words, limit 7, without \\b", r1 + " in " + ms1 + " ms");
    show("9 words, limit 7, with \\b", r2 + " in " + ms2 + " ms");

    section("3. Words with accents, apostrophes and hyphens");
    String coffee = "caf\u00e9 cr\u00e8me";
    show("WORDS_1_TO_2 \"caf\\u00e9 cr\\u00e8me\"", WORDS_1_TO_2.matcher(coffee).matches());
    show("WORDS_1_TO_2_UNICODE \"caf\\u00e9 cr\\u00e8me\"", WORDS_1_TO_2_UNICODE.matcher(coffee).matches());
    show("count(\"\\\\w+\", coffee)", count("\\w+", coffee));
    show("count(\"(?U)\\\\w+\", coffee)", count("(?U)\\w+", coffee));
    show("count(\"\\\\w+\", \"don't stop well-known\")", count("\\w+", "don't stop well-known"));
    show("count(\"\\\\S+\", \"don't stop well-known\")", count("\\S+", "don't stop well-known"));

    section("4. Whitespace-separated words");
    show("TOKENS_1_TO_3 \"don't stop now\"", TOKENS_1_TO_3.matcher("don't stop now").matches());
    show("TOKENS_1_TO_3 \"well-known C++ tips\"", TOKENS_1_TO_3.matcher("well-known C++ tips").matches());
    show("TOKENS_1_TO_3 \"a b c d\"", TOKENS_1_TO_3.matcher("a b c d").matches());
    show("TOKENS_1_TO_3 \"   \"", TOKENS_1_TO_3.matcher("   ").matches());

    section("5. Counting words");
    String sentence = "don't stop well-known, 42 caf\u00e9!";
    show("count(\"\\\\w+\", sentence)", count("\\w+", sentence));
    show("count(\"(?U)\\\\w+\", sentence)", count("(?U)\\w+", sentence));
    show("count(\"\\\\S+\", sentence)", count("\\S+", sentence));
    show("NATURAL_WORD count", NATURAL_WORD.matcher(sentence).results().count());
    show("NATURAL_WORD words", NATURAL_WORD.matcher(sentence).results().map(r -> r.group()).toList());
    show("countWithBreakIterator(sentence)", countWithBreakIterator(sentence));
    show("isWithinWordLimit(sentence, 1, 5)", isWithinWordLimit(sentence, 1, 5));
    show("\"\".split(\"\\\\s+\").length", "".split("\\s+").length);
    show("\" a b\".split(\"\\\\s+\").length", " a b".split("\\s+").length);
    show("\" a b\".strip().split(\"\\\\s+\").length", " a b".strip().split("\\s+").length);
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    String value = escape(String.valueOf(result));
    System.out.printf("%-62s -> %s%n", escape(expression), value);
  }

  static String escape(String s) {
    StringBuilder sb = new StringBuilder();
    for (char c : s.toCharArray()) {
      sb.append(c > 0x7E ? String.format("\\u%04x", (int) c) : String.valueOf(c));
    }
    return sb.toString();
  }
}

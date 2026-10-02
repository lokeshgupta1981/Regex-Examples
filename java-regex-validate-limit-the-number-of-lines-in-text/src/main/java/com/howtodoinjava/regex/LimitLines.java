package com.howtodoinjava.regex;

import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/java-regex-validate-limit-the-number-of-lines-in-text/
 */
public class LimitLines {

  /** At most 3 lines. */
  static final Pattern MAX_3 = Pattern.compile("\\V*(?:\\R\\V*){0,2}");

  /** At most 2 lines, one trailing line break allowed. */
  static final Pattern MAX_2_TRAILING = Pattern.compile("\\V*(?:\\R\\V*){0,1}\\R?");

  /** At most 2 lines, no trailing break handling. */
  static final Pattern MAX_2 = Pattern.compile("\\V*(?:\\R\\V*){0,1}");

  /** Between 2 and 4 lines; the atomic group stops \R from splitting \r\n into two breaks. */
  static final Pattern TWO_TO_FOUR = Pattern.compile("\\V*(?:(?>\\R)\\V*){1,3}");

  /** Between 3 and 4 lines without the atomic group: wrong for \r\n, which can count as two breaks. */
  static final Pattern THREE_TO_FOUR_BUGGY = Pattern.compile("\\V*(?:\\R\\V*){2,3}");

  /** At most 3 lines of at most 10 characters each. */
  static final Pattern THREE_LINES_OF_10 = Pattern.compile("\\V{0,10}(?:\\R\\V{0,10}){0,2}");

  /** Old pattern from the Regular Expressions Cookbook: at most 4 lines, \r\n, \r and \n only. */
  static final Pattern CLASSIC_MAX_4 = Pattern.compile("\\A(?>[^\\r\\n]*(?>\\r\\n?|\\n)){0,3}[^\\r\\n]*\\z");

  /** Builds a pattern that accepts between min and max lines (min >= 1). */
  static Pattern lineRange(int min, int max) {
    return Pattern.compile("\\V*(?:(?>\\R)\\V*){" + (min - 1) + "," + (max - 1) + "}");
  }

  static long countLines(String text) {
    return Pattern.compile("\\R").matcher(text).results().count() + 1;
  }

  public static void main(String[] args) {

    section("Quick reference");
    show("MAX_3 \"one\\ntwo\\nthree\"", MAX_3.matcher("one\ntwo\nthree").matches());
    show("MAX_3 \"one\\ntwo\\nthree\\nfour\"", MAX_3.matcher("one\ntwo\nthree\nfour").matches());
    show("MAX_3 \"one\\r\\ntwo\\rthree\"", MAX_3.matcher("one\r\ntwo\rthree").matches());
    show("TWO_TO_FOUR \"one\"", TWO_TO_FOUR.matcher("one").matches());
    show("TWO_TO_FOUR \"one\\r\\ntwo\"", TWO_TO_FOUR.matcher("one\r\ntwo").matches());
    show("\"one\\ntwo\\r\\nthree\".split(\"\\\\R\", -1).length", "one\ntwo\r\nthree".split("\\R", -1).length);
    show("\"one\\ntwo\\n\".lines().count()", "one\ntwo\n".lines().count());

    section("1. How \\R and \\V see line breaks");
    show("\"\\r\\n\".matches(\"\\\\R\")", "\r\n".matches("\\R"));
    show("\"\\u2028\".matches(\"\\\\R\")", "\u2028".matches("\\R"));
    show("\"\\u000b\".matches(\"\\\\R\")", "\u000b".matches("\\R"));
    show("\"\\u000b\".matches(\".\")", "\u000b".matches("."));
    show("\"\\u000b\".matches(\"\\\\V\")", "\u000b".matches("\\V"));
    show("\"a\".matches(\"\\\\V\")", "a".matches("\\V"));

    section("2. At most 3 lines");
    show("MAX_3 \"\"", MAX_3.matcher("").matches());
    show("MAX_3 \"one\"", MAX_3.matcher("one").matches());
    show("MAX_3 \"one\\ntwo\\nthree\"", MAX_3.matcher("one\ntwo\nthree").matches());
    show("MAX_3 \"one\\ntwo\\nthree\\nfour\"", MAX_3.matcher("one\ntwo\nthree\nfour").matches());
    show("MAX_3 \"one\\r\\ntwo\\rthree\"", MAX_3.matcher("one\r\ntwo\rthree").matches());
    show("MAX_3 \"one\\u2028two\\u0085three\"", MAX_3.matcher("one\u2028two\u0085three").matches());
    show("MAX_3 \"\\n\\n\\n\"", MAX_3.matcher("\n\n\n").matches());
    show("MAX_3.matcher(\"one\\ntwo\\nthree\\nfour\").find()", MAX_3.matcher("one\ntwo\nthree\nfour").find());
    show("CLASSIC_MAX_4 \"a\\nb\\nc\\nd\"", CLASSIC_MAX_4.matcher("a\nb\nc\nd").matches());
    show("CLASSIC_MAX_4 \"a\\u2028b\\u2028c\\u2028d\\u2028e\"",
        CLASSIC_MAX_4.matcher("a\u2028b\u2028c\u2028d\u2028e").matches());

    section("3. Text blocks and the trailing line break");
    String poem = """
        roses are red
        violets are blue
        """;
    show("poem.endsWith(\"\\n\")", poem.endsWith("\n"));
    show("MAX_2.matcher(poem).matches()", MAX_2.matcher(poem).matches());
    show("MAX_2_TRAILING.matcher(poem).matches()", MAX_2_TRAILING.matcher(poem).matches());
    String oneLine = """
        roses are red""";
    show("MAX_2.matcher(oneLine).matches()", MAX_2.matcher(oneLine).matches());
    String three = """
        roses are red
        violets are blue
        sugar is sweet
        """;
    show("MAX_2_TRAILING.matcher(three).matches()", MAX_2_TRAILING.matcher(three).matches());

    section("4. A minimum and a maximum");
    show("TWO_TO_FOUR \"one\"", TWO_TO_FOUR.matcher("one").matches());
    show("TWO_TO_FOUR \"one\\ntwo\"", TWO_TO_FOUR.matcher("one\ntwo").matches());
    show("TWO_TO_FOUR \"a\\nb\\nc\\nd\"", TWO_TO_FOUR.matcher("a\nb\nc\nd").matches());
    show("TWO_TO_FOUR \"a\\nb\\nc\\nd\\ne\"", TWO_TO_FOUR.matcher("a\nb\nc\nd\ne").matches());
    show("\"\\r\\n\".matches(\"\\\\R\\\\n\")", "\r\n".matches("\\R\\n"));
    show("THREE_TO_FOUR_BUGGY \"a\\r\\nb\"", THREE_TO_FOUR_BUGGY.matcher("a\r\nb").matches());
    show("lineRange(3, 4) \"a\\r\\nb\"", lineRange(3, 4).matcher("a\r\nb").matches());
    show("lineRange(3, 4).pattern()", lineRange(3, 4).pattern());

    section("5. Limit lines and line length");
    show("THREE_LINES_OF_10 \"apple\\nbanana\"", THREE_LINES_OF_10.matcher("apple\nbanana").matches());
    show("THREE_LINES_OF_10 \"apple\\nwatermelons\"", THREE_LINES_OF_10.matcher("apple\nwatermelons").matches());

    section("6. Counting lines without a fixed limit");
    show("countLines(\"a\\nb\\r\\nc\")", countLines("a\nb\r\nc"));
    show("countLines(\"\")", countLines(""));
    show("\"a\\nb\\r\\nc\".split(\"\\\\R\", -1).length", "a\nb\r\nc".split("\\R", -1).length);
    show("\"a\\nb\\n\".split(\"\\\\R\", -1).length", "a\nb\n".split("\\R", -1).length);
    show("\"a\\nb\\n\".split(\"\\\\R\").length", "a\nb\n".split("\\R").length);
    show("\"a\\nb\\n\".lines().count()", "a\nb\n".lines().count());
    show("\"\".lines().count()", "".lines().count());
    show("\"a\\u2028b\".lines().count()", "a\u2028b".lines().count());
    show("\"a\\nb\\nc\".lines().count() <= 3", "a\nb\nc".lines().count() <= 3);
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-60s -> %s%n", expression, result);
  }
}

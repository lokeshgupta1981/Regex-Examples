package com.howtodoinjava.regex;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/java-regex-validate-the-minmax-length-of-input-text/
 */
public class MinMaxLength {

  /** 1 to 10 uppercase letters A-Z. */
  static final Pattern UPPER_1_TO_10 = Pattern.compile("^[A-Z]{1,10}$");

  /** A user name: 3 to 16 characters, starts with a letter, then letters, digits or _. */
  static final Pattern USERNAME = Pattern.compile("^(?=.{3,16}$)[a-zA-Z][a-zA-Z0-9_]*$");

  /** 5 to 10 non-whitespace characters; whitespace anywhere is allowed and not counted. */
  static final Pattern NON_SPACE_5_TO_10 = Pattern.compile("^\\s*(?:\\S\\s*){5,10}$");

  /** 1 to 5 user-perceived characters (grapheme clusters). */
  static final Pattern GRAPHEMES_1_TO_5 = Pattern.compile("^\\X{1,5}$");

  public static void main(String[] args) {

    String flag = new String(Character.toChars(0x1F1EE)) + new String(Character.toChars(0x1F1F3));
    String precomposed = "caf" + (char) 0xE9;
    String combining = "cafe" + (char) 0x0301;

    section("Quick reference");
    show("\"hello\".matches(\"^.{3,10}$\")", "hello".matches("^.{3,10}$"));
    show("\"hi\".matches(\"^.{3,10}$\")", "hi".matches("^.{3,10}$"));
    show("\"hello\".matches(\"^.{5}$\")", "hello".matches("^.{5}$"));
    show("\"hello\".matches(\"^.{3,}$\")", "hello".matches("^.{3,}$"));
    show("\"\".matches(\"^.{0,10}$\")", "".matches("^.{0,10}$"));
    show("\"LOKESH\".matches(\"^[A-Z]{1,10}$\")", "LOKESH".matches("^[A-Z]{1,10}$"));
    show("\"hello\\nworld\".matches(\"^.{1,20}$\")", "hello\nworld".matches("^.{1,20}$"));
    show("\"hello\\nworld\".matches(\"(?s)^.{1,20}$\")", "hello\nworld".matches("(?s)^.{1,20}$"));
    show("USERNAME lokesh_42", USERNAME.matcher("lokesh_42").matches());

    section("Quantifier forms");
    for (String regex : List.of("^.{5}$", "^.{3,}$", "^.{3,10}$", "^.{0,10}$", "^.{1,10}$")) {
      show("\"\" / \"hi\" / \"hello\" / \"hello world\" with " + regex,
          "".matches(regex) + " / " + "hi".matches(regex) + " / " + "hello".matches(regex) + " / "
              + "hello world".matches(regex));
    }

    section("Length plus allowed characters");
    for (String s : List.of("LOKESH", "JAVACRAZY", "LOKESHGUPTAINDIA", "LOKESH123", "lokesh", "")) {
      show("UPPER_1_TO_10 \"" + s + "\"", UPPER_1_TO_10.matcher(s).matches());
    }

    section("Line breaks");
    show("\"hello\\nworld\".matches(\"^.{1,20}$\")", "hello\nworld".matches("^.{1,20}$"));
    show("\"hello\\nworld\".matches(\"(?s)^.{1,20}$\")", "hello\nworld".matches("(?s)^.{1,20}$"));
    show("\"hello\\nworld\".matches(\"^[\\\\s\\\\S]{1,20}$\")", "hello\nworld".matches("^[\\s\\S]{1,20}$"));
    show("\"hello\\n\".matches(\"^.{1,10}$\")", "hello\n".matches("^.{1,10}$"));
    show("find ^.{1,10}$ in \"hello\\n\"", Pattern.compile("^.{1,10}$").matcher("hello\n").find());
    show("find ^.{1,10}\\z in \"hello\\n\"", Pattern.compile("^.{1,10}\\z").matcher("hello\n").find());

    section("Length as one rule: lookahead");
    for (String s : List.of("lokesh_42", "lo", "lokesh_gupta_2026", "42lokesh", "lokesh-42", "abc")) {
      show("USERNAME \"" + s + "\"", USERNAME.matcher(s).matches());
    }

    section("Counting only non-whitespace characters");
    for (String s : List.of("hello", "h e l l o", "  hello  ", "hi", "hello world")) {
      show("NON_SPACE_5_TO_10 \"" + s + "\"", NON_SPACE_5_TO_10.matcher(s).matches());
    }

    section("Unicode: chars, code points and graphemes");
    show("flag.length()", flag.length());
    show("flag.codePointCount(0, flag.length())", flag.codePointCount(0, flag.length()));
    show("flag.matches(\".{2}\")", flag.matches(".{2}"));
    show("flag.matches(\"\\\\X\")", flag.matches("\\X"));
    show("precomposed.length()", precomposed.length());
    show("combining.length()", combining.length());
    show("precomposed.matches(\"^.{4}$\")", precomposed.matches("^.{4}$"));
    show("combining.matches(\"^.{4}$\")", combining.matches("^.{4}$"));
    show("combining.matches(\"^\\\\X{4}$\")", combining.matches("^\\X{4}$"));
    show("GRAPHEMES_1_TO_5 combining", GRAPHEMES_1_TO_5.matcher(combining).matches());

    section("Building the pattern from settings");
    show("lengthPattern(2, 4).pattern()", lengthPattern(2, 4).pattern());
    show("lengthPattern(2, 4) \"abc\"", lengthPattern(2, 4).matcher("abc").matches());
    show("lengthPattern(2, 4) \"abcde\"", lengthPattern(2, 4).matcher("abcde").matches());
    try {
      Pattern.compile("^.{,10}$");
    } catch (PatternSyntaxException e) {
      System.out.println("Pattern.compile(\"^.{,10}$\") throws PatternSyntaxException: " + e.getDescription());
    }
    try {
      Pattern.compile("^.{10,1}$");
    } catch (PatternSyntaxException e) {
      System.out.println("Pattern.compile(\"^.{10,1}$\") throws PatternSyntaxException:");
      System.out.println(e.getMessage());
    }

    section("Without regex");
    show("isLengthBetween(\"hello\", 3, 10)", isLengthBetween("hello", 3, 10));
    show("isLengthBetween(\"hi\", 3, 10)", isLengthBetween("hi", 3, 10));
  }

  /** Builds ^.{min,max}$ (with DOTALL) from configuration values. */
  static Pattern lengthPattern(int min, int max) {
    if (min < 0 || max < min) {
      throw new IllegalArgumentException("invalid range " + min + ".." + max);
    }
    return Pattern.compile("^.{" + min + "," + max + "}$", Pattern.DOTALL);
  }

  /** The plain Java check, counting code points like the regex dot does. */
  static boolean isLengthBetween(String s, int min, int max) {
    int n = s.codePointCount(0, s.length());
    return n >= min && n <= max;
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-58s -> %s%n", expression, result);
  }
}

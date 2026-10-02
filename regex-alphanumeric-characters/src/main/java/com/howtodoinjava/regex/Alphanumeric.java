package com.howtodoinjava.regex;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/regex-alphanumeric-characters/
 */
public class Alphanumeric {

  static final Pattern ALPHANUMERIC = Pattern.compile("^[a-zA-Z0-9]+$");

  static final Predicate<String> IS_ALPHANUMERIC = Pattern.compile("[a-zA-Z0-9]+").asMatchPredicate();

  static final String LETTER_AND_DIGIT = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]+$";

  /** Inputs for the valid and invalid samples table. */
  static final List<String> SAMPLES = List.of(
      "Lokesh", "Lokesh123", "2026", "Lokesh123-", "Lokesh 123", "user_1", "", "caf\u00e9");

  public static void main(String[] args) {

    section("Quick reference");
    show("\"abc123\".matches(\"[a-zA-Z0-9]+\")", "abc123".matches("[a-zA-Z0-9]+"));
    show("\"abc 123\".matches(\"[a-zA-Z0-9]+\")", "abc 123".matches("[a-zA-Z0-9]+"));
    show("\"\".matches(\"[a-zA-Z0-9]+\")", "".matches("[a-zA-Z0-9]+"));
    show("\"Lokesh\".matches(\"[a-zA-Z0-9]{3,16}\")", "Lokesh".matches("[a-zA-Z0-9]{3,16}"));
    show("\"abc123\".matches(LETTER_AND_DIGIT)", "abc123".matches(LETTER_AND_DIGIT));
    show("\"abc\".matches(LETTER_AND_DIGIT)", "abc".matches(LETTER_AND_DIGIT));
    show("\"abc123\".matches(\"(?=.*[a-zA-Z])(?=.*\\\\d)[a-zA-Z0-9]+\")",
        "abc123".matches("(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]+"));
    show("\"abc\".matches(\"(?=.*[a-zA-Z])(?=.*\\\\d)[a-zA-Z0-9]+\")",
        "abc".matches("(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]+"));
    show("\"caf\\u00e9\".matches(\"[a-zA-Z0-9]+\")", "caf\u00e9".matches("[a-zA-Z0-9]+"));
    show("\"caf\\u00e9\".matches(\"(?U)\\\\p{Alnum}+\")", "caf\u00e9".matches("(?U)\\p{Alnum}+"));
    show("\"Hello, World! 123\".replaceAll(\"[^a-zA-Z0-9]\", \"\")",
        "Hello, World! 123".replaceAll("[^a-zA-Z0-9]", ""));

    section("2. Valid and invalid samples");
    for (String s : SAMPLES) {
      show("ALPHANUMERIC.matcher(\"" + escape(s) + "\").matches()", ALPHANUMERIC.matcher(s).matches());
    }
    show("IS_ALPHANUMERIC.test(\"Lokesh123\")", IS_ALPHANUMERIC.test("Lokesh123"));
    show("IS_ALPHANUMERIC.test(\"Lokesh123-\")", IS_ALPHANUMERIC.test("Lokesh123-"));
    show("find [a-zA-Z0-9]+ in \"Lokesh123-\"",
        Pattern.compile("[a-zA-Z0-9]+").matcher("Lokesh123-").find());

    section("3. Empty input and length limits");
    show("\"\".matches(\"[a-zA-Z0-9]*\")", "".matches("[a-zA-Z0-9]*"));
    show("\"\".matches(\"[a-zA-Z0-9]+\")", "".matches("[a-zA-Z0-9]+"));
    show("\"ab\".matches(\"[a-zA-Z0-9]{3,16}\")", "ab".matches("[a-zA-Z0-9]{3,16}"));
    show("\"Lokesh\".matches(\"[a-zA-Z0-9]{3,16}\")", "Lokesh".matches("[a-zA-Z0-9]{3,16}"));
    show("\"a\".repeat(17).matches(\"[a-zA-Z0-9]{3,16}\")", "a".repeat(17).matches("[a-zA-Z0-9]{3,16}"));

    section("4. Variants");
    show("\"ABC123\".matches(\"(?i)[a-z0-9]+\")", "ABC123".matches("(?i)[a-z0-9]+"));
    show("\"user_1\".matches(\"\\\\w+\")", "user_1".matches("\\w+"));
    show("\"user-1\".matches(\"[a-zA-Z0-9_-]+\")", "user-1".matches("[a-zA-Z0-9_-]+"));
    show("\"abc 123\".matches(\"[a-zA-Z0-9 ]+\")", "abc 123".matches("[a-zA-Z0-9 ]+"));
    show("\"abc\\t123\".matches(\"[a-zA-Z0-9 ]+\")", "abc\t123".matches("[a-zA-Z0-9 ]+"));
    show("\"abc\\t123\".matches(\"[a-zA-Z0-9\\\\s]+\")", "abc\t123".matches("[a-zA-Z0-9\\s]+"));
    show("\"abc123\".matches(\"[a-z0-9]+\")", "abc123".matches("[a-z0-9]+"));
    show("\"Abc123\".matches(\"[a-z0-9]+\")", "Abc123".matches("[a-z0-9]+"));

    section("5. At least one letter and one digit");
    for (String s : List.of("abc123", "abc", "123", "abc123!")) {
      show("\"" + s + "\".matches(LETTER_AND_DIGIT)", s.matches(LETTER_AND_DIGIT));
    }

    section("6. Letters and digits in any language");
    String eAcute = "\u00e9";
    String arabicThree = "\u0663";
    String oneHalf = "\u00bd";
    show("\"\\u00e9\".matches(\"\\\\p{Alnum}\")", eAcute.matches("\\p{Alnum}"));
    show("\"\\u00e9\".matches(\"(?U)\\\\p{Alnum}\")", eAcute.matches("(?U)\\p{Alnum}"));
    show("\"\\u0663\".matches(\"\\\\d\")", arabicThree.matches("\\d"));
    show("\"\\u0663\".matches(\"(?U)\\\\p{Alnum}\")", arabicThree.matches("(?U)\\p{Alnum}"));
    show("\"\\u00bd\".matches(\"(?U)\\\\p{Alnum}\")", oneHalf.matches("(?U)\\p{Alnum}"));
    show("\"\\u00bd\".matches(\"[\\\\p{L}\\\\p{N}]\")", oneHalf.matches("[\\p{L}\\p{N}]"));
    show("\"\\u00e9\".matches(\"[\\\\p{L}\\\\p{Nd}]\")", eAcute.matches("[\\p{L}\\p{Nd}]"));
    show("\"\\u00b2\".matches(\"[\\\\p{L}\\\\p{N}]\")", "\u00b2".matches("[\\p{L}\\p{N}]"));
    show("\"\\u00b2\".matches(\"[\\\\p{L}\\\\p{Nd}]\")", "\u00b2".matches("[\\p{L}\\p{Nd}]"));

    section("7. Removing other characters");
    show("\"Hello, World! 123\".replaceAll(\"[^a-zA-Z0-9]\", \"\")",
        "Hello, World! 123".replaceAll("[^a-zA-Z0-9]", ""));
    show("\"a-b_c d\".replaceAll(\"[^a-zA-Z0-9]+\", \"-\")", "a-b_c d".replaceAll("[^a-zA-Z0-9]+", "-"));

    section("8. Without a regex");
    show("\"Lokesh123\".chars().allMatch(Character::isLetterOrDigit)",
        "Lokesh123".chars().allMatch(Character::isLetterOrDigit));
    show("\"caf\\u00e9\".chars().allMatch(Character::isLetterOrDigit)",
        "caf\u00e9".chars().allMatch(Character::isLetterOrDigit));
    show("\"\".chars().allMatch(Character::isLetterOrDigit)",
        "".chars().allMatch(Character::isLetterOrDigit));
  }

  static String escape(String s) {
    StringBuilder sb = new StringBuilder();
    for (char c : s.toCharArray()) {
      sb.append(c < 128 ? String.valueOf(c) : String.format("\\u%04x", (int) c));
    }
    return sb.toString();
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-58s -> %s%n", expression, result);
  }
}

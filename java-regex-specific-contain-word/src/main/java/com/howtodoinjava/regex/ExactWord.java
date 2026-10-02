package com.howtodoinjava.regex;

import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/java-regex-specific-contain-word/
 */
public class ExactWord {

  static final Pattern CAT = Pattern.compile("\\bcat\\b");

  public static void main(String[] args) {

    section("Quick reference");
    show("find \\bcat\\b in \"The cat is cute\"", find("\\bcat\\b", "The cat is cute"));
    show("find \\bcat\\b in \"The category is empty\"", find("\\bcat\\b", "The category is empty"));
    show("find (?i)\\bcat\\b in \"CAT and dog\"", find("(?i)\\bcat\\b", "CAT and dog"));
    show("all \\b\\w*cat\\w*\\b in \"cat, category, noncategory\"",
        all("\\b\\w*cat\\w*\\b", "cat, category, noncategory"));
    show("all \\b(?:cat|dog)\\b in \"hotdog, a dog and a cat\"",
        all("\\b(?:cat|dog)\\b", "hotdog, a dog and a cat"));
    show("\"a cat and a dog\".matches(lookaheads)",
        "a cat and a dog".matches("(?=.*\\bcat\\b)(?=.*\\bdog\\b).*"));
    show("\"cat category cat\".replaceAll(\\bcat\\b, dog)",
        "cat category cat".replaceAll("\\bcat\\b", "dog"));

    section("1. One exact word");
    show("CAT.matcher(\"The cat is cute\").find()", CAT.matcher("The cat is cute").find());
    show("CAT.matcher(\"The category is empty\").find()", CAT.matcher("The category is empty").find());
    show("CAT.matcher(\"The noncategory is empty\").find()",
        CAT.matcher("The noncategory is empty").find());
    show("CAT.matcher(\"a cat-like toy\").find()", CAT.matcher("a cat-like toy").find());
    show("start positions in \"a cat, the cat.\"",
        CAT.matcher("a cat, the cat.").results().map(MatchResult::start).toList());

    section("2. Exact word or exact string");
    show("\"cat\".matches(\"cat\")", "cat".matches("cat"));
    show("\"the cat\".matches(\"cat\")", "the cat".matches("cat"));
    show("\"the cat\".matches(\".*\\\\bcat\\\\b.*\")", "the cat".matches(".*\\bcat\\b.*"));
    show("\"cat\".equals(\"cat\")", "cat".equals("cat"));

    section("3. Ignoring case");
    show("CASE_INSENSITIVE, \"My Cat\"",
        Pattern.compile("\\bcat\\b", Pattern.CASE_INSENSITIVE).matcher("My Cat").find());
    show("find (?i)\\bcat\\b in \"CAT\"", find("(?i)\\bcat\\b", "CAT"));

    show("\"CAF\\u00c9\".matches(\"(?i)caf\\u00e9\")", "CAF\u00c9".matches("(?i)caf\u00e9"));
    show("\"CAF\\u00c9\".matches(\"(?iu)caf\\u00e9\")", "CAF\u00c9".matches("(?iu)caf\u00e9"));

    section("4. Words that contain a substring");
    show("all \\b\\w*cat\\w*\\b in \"The cat, a category and a noncategory\"",
        all("\\b\\w*cat\\w*\\b", "The cat, a category and a noncategory"));
    show("all \\b\\w*cat\\w*\\b in \"the non-category\"", all("\\b\\w*cat\\w*\\b", "the non-category"));
    show("all (?i)\\b\\w*cat\\w*\\b in \"Catalog and Bobcat\"",
        all("(?i)\\b\\w*cat\\w*\\b", "Catalog and Bobcat"));
    show("\"category\".contains(\"cat\")", "category".contains("cat"));

    section("5. Any of several words");
    show("all \\b(?:cat|dog)\\b in \"hotdog, a dog and a cat\"",
        all("\\b(?:cat|dog)\\b", "hotdog, a dog and a cat"));
    show("all \\bcat|dog\\b in \"hotdog, a dog and a cat\"", all("\\bcat|dog\\b", "hotdog, a dog and a cat"));
    List<String> words = List.of("cat", "dog");
    String anyWord = words.stream()
        .map(Pattern::quote)
        .collect(Collectors.joining("|", "\\b(?:", ")\\b"));
    show("anyWord", anyWord);
    show("all anyWord in \"hotdog, a dog and a cat\"", all(anyWord, "hotdog, a dog and a cat"));

    section("6. All of several words, in any order");
    Pattern both = Pattern.compile("(?s)(?=.*\\bcat\\b)(?=.*\\bdog\\b).*");
    show("both.matcher(\"the dog chased the cat\").matches()",
        both.matcher("the dog chased the cat").matches());
    show("both.matcher(\"the dog chased the category\").matches()",
        both.matcher("the dog chased the category").matches());
    show("both.matcher(\"a dog\\nand a cat\").matches()", both.matcher("a dog\nand a cat").matches());
    String text = "the dog chased the cat";
    show("words.stream().allMatch(...)", words.stream()
        .allMatch(w -> Pattern.compile("\\b" + Pattern.quote(w) + "\\b").matcher(text).find()));

    section("7. A word from user input");
    String version = "v1.2";
    show("find \\b + v1.2 + \\b in \"v152\"",
        Pattern.compile("\\b" + version + "\\b").matcher("v152").find());
    show("find \\b + quote(v1.2) + \\b in \"v152\"",
        Pattern.compile("\\b" + Pattern.quote(version) + "\\b").matcher("v152").find());
    show("find \\b + quote(v1.2) + \\b in \"see v1.2 notes\"",
        Pattern.compile("\\b" + Pattern.quote(version) + "\\b").matcher("see v1.2 notes").find());
    show("find \\b + quote(v1.2) + \\b in \"see v1.25 notes\"",
        Pattern.compile("\\b" + Pattern.quote(version) + "\\b").matcher("see v1.25 notes").find());

    section("8. Counting and replacing a whole word");
    show("CAT.matcher(\"cat category cat\").results().count()",
        CAT.matcher("cat category cat").results().count());
    show("\"cat category cat\".replaceAll(\"\\\\bcat\\\\b\", \"dog\")",
        "cat category cat".replaceAll("\\bcat\\b", "dog"));
    show("\"cat category cat\".replace(\"cat\", \"dog\")", "cat category cat".replace("cat", "dog"));

    section("9. Without a regex");
    show("\"The category is empty\".equals(\"cat\")", "The category is empty".equals("cat"));
    show("\"The category is empty\".equalsIgnoreCase(\"cat\")", "The category is empty".equalsIgnoreCase("cat"));
    show("\"The category is empty\".contains(\"cat\")", "The category is empty".contains("cat"));
    show("split(\"\\\\W+\") then contains(\"cat\")",
        Arrays.asList("The cat, is cute".split("\\W+")).contains("cat"));
  }

  static boolean find(String regex, String input) {
    return Pattern.compile(regex).matcher(input).find();
  }

  static List<String> all(String regex, String input) {
    return Pattern.compile(regex).matcher(input).results().map(MatchResult::group).toList();
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-60s -> %s%n", expression, result);
  }
}

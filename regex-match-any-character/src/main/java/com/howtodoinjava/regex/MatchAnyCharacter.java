package com.howtodoinjava.regex;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/match-any-set-of-characters/
 */
public class MatchAnyCharacter {

  public static void main(String[] args) {

    section("1. The dot matches any single character");
    show("Pattern.matches(\".\", \"a\")", Pattern.matches(".", "a"));
    show("Pattern.matches(\".\", \"7\")", Pattern.matches(".", "7"));
    show("Pattern.matches(\".\", \"#\")", Pattern.matches(".", "#"));
    show("Pattern.matches(\".\", \"ab\")", Pattern.matches(".", "ab"));
    show("\"A1B\".matches(\"A.B\")", "A1B".matches("A.B"));
    show("\"AB\".matches(\"A.B\")", "AB".matches("A.B"));

    section("2. The dot and line breaks");
    show("Pattern.matches(\".\", \"\\n\")", Pattern.matches(".", "\n"));
    show("Pattern.matches(\".*\", \"line1\\nline2\")", Pattern.matches(".*", "line1\nline2"));
    show("DOTALL: compile(\".*\", DOTALL).matcher(\"line1\\nline2\").matches()",
        Pattern.compile(".*", Pattern.DOTALL).matcher("line1\nline2").matches());
    show("Pattern.matches(\"(?s).*\", \"line1\\nline2\")", Pattern.matches("(?s).*", "line1\nline2"));
    show("Pattern.matches(\"[\\\\s\\\\S]*\", \"line1\\nline2\")", Pattern.matches("[\\s\\S]*", "line1\nline2"));

    section("3. Any number of characters");
    show("\"\".matches(\".*\")", "".matches(".*"));
    show("\"\".matches(\".+\")", "".matches(".+"));
    show("\"abc\".matches(\".{3}\")", "abc".matches(".{3}"));
    show("\"abcde\".matches(\".{2,4}\")", "abcde".matches(".{2,4}"));
    show("\"ID-A7K2\".matches(\"ID-.{4}\")", "ID-A7K2".matches("ID-.{4}"));

    section("4. Greedy and lazy");
    String html = "<b>Java</b> and <b>Regex</b>";
    show("greedy <b>.*</b>", firstMatch("<b>.*</b>", html));
    show("lazy   <b>.*?</b>", firstMatch("<b>.*?</b>", html));
    show("text inside parentheses", Pattern.compile("\\((.*?)\\)").matcher("call(a) and (b)")
        .results().map(r -> r.group(1)).toList());

    section("5. matches() versus find()");
    show("Pattern.compile(\".\").matcher(\"ab\").matches()", Pattern.compile(".").matcher("ab").matches());
    show("Pattern.compile(\".\").matcher(\"ab\").find()", Pattern.compile(".").matcher("ab").find());
    show("find ID-.{4} in \"Order ID-A7K2 shipped\"", firstMatch("ID-.{4}", "Order ID-A7K2 shipped"));

    section("6. A literal dot");
    show("\"abc\".matches(\"a.c\")", "abc".matches("a.c"));
    show("\"abc\".matches(\"a\\\\.c\")", "abc".matches("a\\.c"));
    show("\"a.c\".matches(\"a\\\\.c\")", "a.c".matches("a\\.c"));
    show("\"a.c\".matches(\"a[.]c\")", "a.c".matches("a[.]c"));
    show("Pattern.quote(\"a.c\")", Pattern.quote("a.c"));
    show("\"abc\".matches(Pattern.quote(\"a.c\"))", "abc".matches(Pattern.quote("a.c")));

    section("7. A set or range of characters");
    show("\"b\".matches(\"[abc]\")", "b".matches("[abc]"));
    show("\"d\".matches(\"[abc]\")", "d".matches("[abc]"));
    show("\"e\".matches(\"[a-f]\")", "e".matches("[a-f]"));
    show("\"B\".matches(\"[a-zA-Z]\")", "B".matches("[a-zA-Z]"));
    show("\"x\".matches(\"[^abc]\")", "x".matches("[^abc]"));
    show("\"a\".matches(\"[^abc]\")", "a".matches("[^abc]"));
    show("\"2026\".matches(\"\\\\d+\")", "2026".matches("\\d+"));
    show("\"user_42\".matches(\"\\\\w+\")", "user_42".matches("\\w+"));
    show("\"abc123\".matches(\"[a-zA-Z0-9]+\")", "abc123".matches("[a-zA-Z0-9]+"));
    show("\"abc 123\".matches(\"[a-zA-Z0-9]+\")", "abc 123".matches("[a-zA-Z0-9]+"));
    show("\"@\".matches(\"[^a-zA-Z0-9\\\\s]\")", "@".matches("[^a-zA-Z0-9\\s]"));
    show("\"#\".matches(\"\\\\p{Punct}\")", "#".matches("\\p{Punct}"));

    section("8. Letters outside A-Z");
    String eAcute = "\u00e9";
    show("\"e-acute\".matches(\"[a-zA-Z]\")", eAcute.matches("[a-zA-Z]"));
    show("\"e-acute\".matches(\"\\\\p{L}\")", eAcute.matches("\\p{L}"));
    show("\"e-acute\".matches(\"\\\\w\")", eAcute.matches("\\w"));
    show("\"e-acute\" with UNICODE_CHARACTER_CLASS and \\w",
        Pattern.compile("\\w", Pattern.UNICODE_CHARACTER_CLASS).matcher(eAcute).matches());
  }

  static String firstMatch(String regex, String input) {
    Matcher m = Pattern.compile(regex).matcher(input);
    return m.find() ? m.group() : "(no match)";
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-62s -> %s%n", expression, result);
  }

  static void show(String expression, List<?> result) {
    show(expression, (Object) result);
  }
}

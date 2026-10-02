package com.howtodoinjava.regex;

import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/regular-expressions-meta-characters/
 */
public class MetaCharacters {

  public static void main(String[] args) {

    section("Quick reference");
    show("\"cat\".matches(\"c.t\")", "cat".matches("c.t"));
    show("\"b\".matches(\"[abc]\")", "b".matches("[abc]"));
    show("\"2026\".matches(\"\\\\d+\")", "2026".matches("\\d+"));
    show("\"color\".matches(\"colou?r\")", "color".matches("colou?r"));
    show("\"abab\".matches(\"(ab)+\")", "abab".matches("(ab)+"));
    show("\"gray\".matches(\"gr(a|e)y\")", "gray".matches("gr(a|e)y"));
    show("find(\"^cat\") in \"cat sat\"", Pattern.compile("^cat").matcher("cat sat").find());
    show("find(\"\\\\bcat\\\\b\") in \"category\"", Pattern.compile("\\bcat\\b").matcher("category").find());
    show("\"1+1\".matches(\"1+1\")", "1+1".matches("1+1"));
    show("\"1+1\".matches(\"1\\\\+1\")", "1+1".matches("1\\+1"));
    show("Pattern.quote(\"1+1\")", Pattern.quote("1+1"));
    show("\"HELLO\".matches(\"(?i)hello\")", "HELLO".matches("(?i)hello"));

    section("1. Characters that are literal outside their context");
    show("\"a-b\".matches(\"a-b\")", "a-b".matches("a-b"));
    show("\"a=b\".matches(\"a=b\")", "a=b".matches("a=b"));
    show("\"a}\".matches(\"a}\")", "a}".matches("a}"));
    show("\"@#\".matches(\"@#\")", "@#".matches("@#"));

    section("2. The dot");
    show("\"cat\".matches(\"c.t\")", "cat".matches("c.t"));
    show("\"ct\".matches(\"c.t\")", "ct".matches("c.t"));
    show("\"c\\nt\".matches(\"c.t\")", "c\nt".matches("c.t"));
    show("all .at words in \"cat bat rat\"", allMatches(".at", "cat bat rat"));

    section("3. Character classes");
    show("\"b\".matches(\"[abc]\")", "b".matches("[abc]"));
    show("\"x\".matches(\"[^abc]\")", "x".matches("[^abc]"));
    show("\"e\".matches(\"[a-f]\")", "e".matches("[a-f]"));
    show("\"k\".matches(\"[a-z&&[^aeiou]]\")", "k".matches("[a-z&&[^aeiou]]"));
    show("\"a\".matches(\"[a-z&&[^aeiou]]\")", "a".matches("[a-z&&[^aeiou]]"));
    show("\"-\".matches(\"[a-z-]\")", "-".matches("[a-z-]"));
    show("\"^\".matches(\"[a^]\")", "^".matches("[a^]"));
    show("\"]\".matches(\"[\\\\]]\")", "]".matches("[\\]]"));
    show("\".\".matches(\"[.]\")", ".".matches("[.]"));

    section("3.1. Predefined classes");
    show("\"7\".matches(\"\\\\d\")", "7".matches("\\d"));
    show("\"a\".matches(\"\\\\D\")", "a".matches("\\D"));
    show("\"user_1\".matches(\"\\\\w+\")", "user_1".matches("\\w+"));
    show("\"-\".matches(\"\\\\W\")", "-".matches("\\W"));
    show("\"\\t\".matches(\"\\\\s\")", "\t".matches("\\s"));
    show("\"a\".matches(\"\\\\S\")", "a".matches("\\S"));
    show("\"\\u00e9\".matches(\"\\\\p{L}\")", "\u00e9".matches("\\p{L}"));
    show("\"A\".matches(\"\\\\p{Lu}\")", "A".matches("\\p{Lu}"));
    show("\"!\".matches(\"\\\\p{Punct}\")", "!".matches("\\p{Punct}"));

    section("4. Quantifiers");
    show("\"\".matches(\"a*\")", "".matches("a*"));
    show("\"\".matches(\"a+\")", "".matches("a+"));
    show("\"color\".matches(\"colou?r\")", "color".matches("colou?r"));
    show("\"colour\".matches(\"colou?r\")", "colour".matches("colou?r"));
    show("\"aaa\".matches(\"a{3}\")", "aaa".matches("a{3}"));
    show("\"aaaa\".matches(\"a{2,}\")", "aaaa".matches("a{2,}"));
    show("\"aaaa\".matches(\"a{2,3}\")", "aaaa".matches("a{2,3}"));

    section("4.1. Greedy, lazy and possessive");
    show("first match of a+ in \"aaa\"", firstMatch("a+", "aaa"));
    show("first match of a+? in \"aaa\"", firstMatch("a+?", "aaa"));
    show("\"aaa\".matches(\"a*a\")", "aaa".matches("a*a"));
    show("\"aaa\".matches(\"a*+a\")", "aaa".matches("a*+a"));

    section("5. Anchors and boundaries");
    show("find(\"^cat\") in \"cat sat\"", Pattern.compile("^cat").matcher("cat sat").find());
    show("find(\"^sat\") in \"cat sat\"", Pattern.compile("^sat").matcher("cat sat").find());
    show("find(\"sat$\") in \"cat sat\"", Pattern.compile("sat$").matcher("cat sat").find());
    show("find(\"\\\\bcat\\\\b\") in \"the cat\"", Pattern.compile("\\bcat\\b").matcher("the cat").find());
    show("find(\"\\\\bcat\\\\b\") in \"category\"", Pattern.compile("\\bcat\\b").matcher("category").find());
    show("find(\"\\\\Bcat\") in \"concat\"", Pattern.compile("\\Bcat").matcher("concat").find());

    section("6. Groups and alternation");
    show("\"abab\".matches(\"(ab)+\")", "abab".matches("(ab)+"));
    show("\"abab\".matches(\"ab+\")", "abab".matches("ab+"));
    show("\"abbb\".matches(\"ab+\")", "abbb".matches("ab+"));
    show("\"gray\".matches(\"gr(a|e)y\")", "gray".matches("gr(a|e)y"));
    show("\"gray\".matches(\"gra|ey\")", "gray".matches("gra|ey"));
    show("\"dog\".matches(\"cat|dog\")", "dog".matches("cat|dog"));

    Matcher name = Pattern.compile("(?<first>\\w+) (?<last>\\w+)").matcher("Lokesh Gupta");
    show("name.matches()", name.matches());
    show("name.group(1)", name.group(1));
    show("name.group(\"last\")", name.group("last"));
    show("name.groupCount()", name.groupCount());
    show("non-capturing (?:ab)+ groupCount",
        Pattern.compile("(?:ab)+").matcher("abab").groupCount());
    show("\"hello hello\".matches(\"(\\\\w+) \\\\1\")", "hello hello".matches("(\\w+) \\1"));
    show("\"hello world\".matches(\"(\\\\w+) \\\\1\")", "hello world".matches("(\\w+) \\1"));
    show("\"Lokesh Gupta\".replaceAll(...)",
        "Lokesh Gupta".replaceAll("(\\w+) (\\w+)", "$2, $1"));

    section("6.1. Lookahead and lookbehind");
    show("first \\d+(?= USD) in \"5 EUR 10 USD\"", firstMatch("\\d+(?= USD)", "5 EUR 10 USD"));
    show("first (?<=\\$)\\d+ in \"cost $25\"", firstMatch("(?<=\\$)\\d+", "cost $25"));
    show("\"abc1\".matches(\"(?=.*\\\\d).+\")", "abc1".matches("(?=.*\\d).+"));
    show("\"abc\".matches(\"(?=.*\\\\d).+\")", "abc".matches("(?=.*\\d).+"));
    show("\"cat\".matches(\"(?!dog)\\\\w+\")", "cat".matches("(?!dog)\\w+"));
    show("Pattern.compile(\"(?<=(ab)+)c\")", errorOf(() -> Pattern.compile("(?<=(ab)+)c")));

    section("7. Escaping metacharacters");
    show("\"1+1\".matches(\"1+1\")", "1+1".matches("1+1"));
    show("\"11\".matches(\"1+1\")", "11".matches("1+1"));
    show("\"1+1\".matches(\"1\\\\+1\")", "1+1".matches("1\\+1"));
    show("\"1+1\".matches(\"1[+]1\")", "1+1".matches("1[+]1"));
    show("\"1+1\".matches(\"\\\\Q1+1\\\\E\")", "1+1".matches("\\Q1+1\\E"));
    show("Pattern.quote(\"1+1\")", Pattern.quote("1+1"));
    show("\"1+1\".matches(Pattern.quote(\"1+1\"))", "1+1".matches(Pattern.quote("1+1")));
    show("\"C:\\\\temp\".matches(\"C:\\\\\\\\temp\")", "C:\\temp".matches("C:\\\\temp"));
    show("\"(1)\".matches(\"\\\\(1\\\\)\")", "(1)".matches("\\(1\\)"));

    section("7.1. split() takes a regex");
    show("\"a.b.c\".split(\".\")", Arrays.toString("a.b.c".split(".")));
    show("\"a.b.c\".split(\"\\\\.\")", Arrays.toString("a.b.c".split("\\.")));
    show("\"a|b\".split(\"|\")", Arrays.toString("a|b".split("|")));
    show("\"a|b\".split(\"\\\\|\")", Arrays.toString("a|b".split("\\|")));
    show("\"1+1\".split(\"+\")", errorOf(() -> "1+1".split("+")));

    section("7.2. Metacharacters in the replacement string");
    show("\"cost 5\".replaceAll(\"\\\\d+\", \"$10\")", errorOf(() -> "cost 5".replaceAll("\\d+", "$10")));
    show("\"cost 5\".replaceAll(\"\\\\d+\", \"\\\\$10\")", "cost 5".replaceAll("\\d+", "\\$10"));
    show("\"cost 5\".replaceAll(\"\\\\d+\", Matcher.quoteReplacement(\"$10\"))",
        "cost 5".replaceAll("\\d+", Matcher.quoteReplacement("$10")));
    show("Matcher.quoteReplacement(\"$10\")", Matcher.quoteReplacement("$10"));

    section("7.3. Invalid patterns");
    show("Pattern.compile(\"*abc\")", errorOf(() -> Pattern.compile("*abc")));
    show("Pattern.compile(\"[abc\")", errorOf(() -> Pattern.compile("[abc")));
    show("Pattern.compile(\"(abc\")", errorOf(() -> Pattern.compile("(abc")));

    section("8. Inline flags");
    show("\"HELLO\".matches(\"(?i)hello\")", "HELLO".matches("(?i)hello"));
    show("\"a\\nb\".matches(\"a.b\")", "a\nb".matches("a.b"));
    show("\"a\\nb\".matches(\"(?s)a.b\")", "a\nb".matches("(?s)a.b"));
    show("find(\"(?m)^b\") in \"a\\nb\"", Pattern.compile("(?m)^b").matcher("a\nb").find());
    show("\"123-4567\".matches(\"(?x) \\\\d{3} - \\\\d{4}  # number\")",
        "123-4567".matches("(?x) \\d{3} - \\d{4}  # number"));
    show("\"HELLO world\".matches(\"(?i:hello) world\")", "HELLO world".matches("(?i:hello) world"));
    show("\"HELLO WORLD\".matches(\"(?i:hello) world\")", "HELLO WORLD".matches("(?i:hello) world"));
  }

  static String firstMatch(String regex, String input) {
    Matcher m = Pattern.compile(regex).matcher(input);
    return m.find() ? m.group() : "(no match)";
  }

  static List<String> allMatches(String regex, String input) {
    return Pattern.compile(regex).matcher(input).results().map(MatchResult::group).toList();
  }

  /** Runs the code and returns the exception it throws, as "ClassName: message". */
  static String errorOf(Runnable code) {
    try {
      code.run();
      return "(no exception)";
    } catch (PatternSyntaxException e) {
      return "PatternSyntaxException: " + e.getMessage().replace("\n", " | ");
    } catch (RuntimeException e) {
      return e.getClass().getSimpleName() + ": " + e.getMessage();
    }
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-58s -> %s%n", expression, result);
  }
}

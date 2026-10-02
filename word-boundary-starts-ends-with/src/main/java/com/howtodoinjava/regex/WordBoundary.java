package com.howtodoinjava.regex;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/word-boundary-starts-ends-with/
 */
public class WordBoundary {

  static final String LOG = """
      ERROR disk full
      INFO started
      ERRORS: 0
      WARN retry done""";

  public static void main(String[] args) {

    section("Quick reference");
    show("find \\bcat\\b in \"the cat sat\"", find("\\bcat\\b", "the cat sat"));
    show("find \\bcat\\b in \"category\"", find("\\bcat\\b", "category"));
    show("all \\bun\\w* in \"undo the unknown fun\"", all("\\bun\\w*", "undo the unknown fun"));
    show("all \\w+ing\\b in \"walking and talking in the morning\"",
        all("\\w+ing\\b", "walking and talking in the morning"));
    show("all \\Bcat\\B in \"concatenate cat\"", all("\\Bcat\\B", "concatenate cat"));
    show("all (?m)^ERROR\\b.* in LOG", all("(?m)^ERROR\\b.*", LOG));

    section("1. Where \\b matches");
    show("\"cat sat\".replaceAll(\"\\\\b\", \"|\")", "cat sat".replaceAll("\\b", "|"));
    show("\"a cat\".replaceAll(\"\\\\B\", \"|\")", "a cat".replaceAll("\\B", "|"));
    show("all \\b\\w in \"hello big world\"", all("\\b\\w", "hello big world"));
    show("all \\w\\b in \"hello big world\"", all("\\w\\b", "hello big world"));

    section("2. What counts as a word character");
    show("\"e-mail\".replaceAll(\"\\\\b\", \"|\")", "e-mail".replaceAll("\\b", "|"));
    show("\"don't\".replaceAll(\"\\\\b\", \"|\")", "don't".replaceAll("\\b", "|"));
    show("\"user_1\".replaceAll(\"\\\\b\", \"|\")", "user_1".replaceAll("\\b", "|"));
    show("find \\bmail\\b in \"my e-mail\"", find("\\bmail\\b", "my e-mail"));
    show("find (?<![\\w-])mail(?![\\w-]) in \"my e-mail\"", find("(?<![\\w-])mail(?![\\w-])", "my e-mail"));
    show("find (?<![\\w-])mail(?![\\w-]) in \"mail box\"", find("(?<![\\w-])mail(?![\\w-])", "mail box"));
    String ete = "\u00e9t\u00e9";
    show("all \\b\\w+\\b in \"ete\" (accented e)", all("\\b\\w+\\b", ete).size() + " match: "
        + ascii(all("\\b\\w+\\b", ete)));
    show("all (?U)\\b\\w+\\b in \"ete\" (accented e)", all("(?U)\\b\\w+\\b", ete).size() + " match: "
        + ascii(all("(?U)\\b\\w+\\b", ete)));

    section("3. Words that start with a prefix");
    show("all \\bun\\w* in \"undo the unknown fun\"", all("\\bun\\w*", "undo the unknown fun"));
    show("all un\\w* in \"undo the unknown fun\"", all("un\\w*", "undo the unknown fun"));
    show("all (?i)\\bun\\w* in \"Undo the unknown fun\"", all("(?i)\\bun\\w*", "Undo the unknown fun"));
    show("all \\b[A-Z]\\w* in \"Lokesh lives in Delhi\"", all("\\b[A-Z]\\w*", "Lokesh lives in Delhi"));

    section("4. Words that end with a suffix");
    show("all \\w+ing\\b in \"walking and talking in the morning\"",
        all("\\w+ing\\b", "walking and talking in the morning"));
    show("all \\w+ing\\b in \"singing birds and a kingdom\"",
        all("\\w+ing\\b", "singing birds and a kingdom"));
    show("all \\w+ing in \"singing birds and a kingdom\"",
        all("\\w+ing", "singing birds and a kingdom"));

    section("5. Lines that start or end with a word");
    show("all (?m)^ERROR.* in LOG", all("(?m)^ERROR.*", LOG));
    show("all (?m)^ERROR\\b.* in LOG", all("(?m)^ERROR\\b.*", LOG));
    show("all (?m)^.*\\bdone$ in LOG", all("(?m)^.*\\bdone$", LOG));

    section("6. Inside a word: \\B");
    show("all \\Bcat\\B in \"concatenate cat\"", all("\\Bcat\\B", "concatenate cat"));
    show("find \\Bcat in \"concat\"", find("\\Bcat", "concat"));
    show("find \\Bcat in \"cat\"", find("\\Bcat", "cat"));

    section("7. \\G: right after the previous match");
    show("all \\G\\d in \"123abc456\"", all("\\G\\d", "123abc456"));
    show("all \\d in \"123abc456\"", all("\\d", "123abc456"));
    show("all \\Gdog in \"dog dog\"", all("\\Gdog", "dog dog"));

    section("8. Common mistakes");
    show("find \"\\bcat\\b\" (backspace) in \"the cat\"", find("\bcat\b", "the cat"));
    show("\"\\b\".equals(\"\\u0008\")", "\b".equals("\u0008"));
    show("\"the cat\".matches(\"\\\\bcat\\\\b\")", "the cat".matches("\\bcat\\b"));
    show("find \\bC\\+\\+\\b in \"I like C++ code\"", find("\\bC\\+\\+\\b", "I like C++ code"));
    show("find (?<!\\w)C\\+\\+(?!\\w) in \"I like C++ code\"",
        find("(?<!\\w)C\\+\\+(?!\\w)", "I like C++ code"));
    show("find (?<!\\w)C\\+\\+(?!\\w) in \"I like C++11\"",
        find("(?<!\\w)C\\+\\+(?!\\w)", "I like C++11"));
  }

  static boolean find(String regex, String input) {
    return Pattern.compile(regex).matcher(input).find();
  }

  static List<String> all(String regex, String input) {
    return Pattern.compile(regex).matcher(input).results().map(MatchResult::group).toList();
  }

  /** Prints non-ASCII letters as Unicode escapes, so the console encoding does not matter. */
  static String ascii(List<String> words) {
    StringBuilder sb = new StringBuilder("[");
    for (String w : words) {
      if (sb.length() > 1) {
        sb.append(", ");
      }
      for (char c : w.toCharArray()) {
        sb.append(c < 128 ? String.valueOf(c) : String.format("\\u%04x", (int) c));
      }
    }
    return sb.append(']').toString();
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-58s -> %s%n", expression, result);
  }
}

package com.howtodoinjava.regex;

import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/start-end-of-string/
 */
public class StartEndOfString {

  static final String FRUITS = "apple\nbanana\ncherry";

  static final String SENTENCES = """
      The sun is shining
      Apples are delicious
      The moon is bright
      Birds are singing""";

  public static void main(String[] args) {

    section("Quick reference");
    show("find ^The in \"The sun is shining\"", find("^The", "The sun is shining"));
    show("find ing$ in \"The sun is shining\"", find("ing$", "The sun is shining"));
    show("find ^sun in \"The sun is shining\"", find("^sun", "The sun is shining"));
    show("find ^banana$ in FRUITS", find("^banana$", FRUITS));
    show("find ^banana$ in FRUITS with MULTILINE",
        Pattern.compile("^banana$", Pattern.MULTILINE).matcher(FRUITS).find());
    show("all (?m)^\\w+ in FRUITS", all("(?m)^\\w+", FRUITS));
    show("find cat$ in \"cat\\n\"", find("cat$", "cat\n"));
    show("find cat\\z in \"cat\\n\"", find("cat\\z", "cat\n"));
    show("\"a\\r\\nb\\nc\".split(\"\\\\R\")", Arrays.toString("a\r\nb\nc".split("\\R")));

    section("1. Anchors match a position");
    show("\"cat\".replaceAll(\"^\", \">\")", "cat".replaceAll("^", ">"));
    show("\"cat\".replaceAll(\"$\", \"!\")", "cat".replaceAll("$", "!"));
    Matcher end = Pattern.compile("$").matcher("cat");
    show("end.find()", end.find());
    show("end.start()", end.start());
    show("end.group()", "\"" + end.group() + "\"");

    section("2. Start of the string");
    show("find ^\\d in \"1st place\"", find("^\\d", "1st place"));
    show("find ^[a-zA-Z] in \"Lokesh\"", find("^[a-zA-Z]", "Lokesh"));
    show("find ^Hello in \"Hello world\"", find("^Hello", "Hello world"));
    show("find ^Hello in \"Say Hello\"", find("^Hello", "Say Hello"));
    show("find ^[^a-zA-Z0-9] in \"#tag\"", find("^[^a-zA-Z0-9]", "#tag"));
    show("find (?i)^hello in \"HELLO there\"", find("(?i)^hello", "HELLO there"));
    show("\"Hello world\".startsWith(\"Hello\")", "Hello world".startsWith("Hello"));

    section("3. End of the string");
    show("find \\d$ in \"order 42\"", find("\\d$", "order 42"));
    show("find world$ in \"Hello world\"", find("world$", "Hello world"));
    show("find world$ in \"world peace\"", find("world$", "world peace"));
    show("find [.!?]$ in \"Done!\"", find("[.!?]$", "Done!"));
    show("find \\.(jpg|png)$ in \"photo.png\"", find("\\.(jpg|png)$", "photo.png"));
    show("find \\.(jpg|png)$ in \"photo.png.txt\"", find("\\.(jpg|png)$", "photo.png.txt"));
    show("\"photo.png\".endsWith(\".png\")", "photo.png".endsWith(".png"));

    section("4. Both anchors: the whole string");
    show("find [a-z]+ in \"abc123\"", find("[a-z]+", "abc123"));
    show("find ^[a-z]+$ in \"abc123\"", find("^[a-z]+$", "abc123"));
    show("\"abc\".matches(\"[a-z]+\")", "abc".matches("[a-z]+"));
    show("find ^g.*g$ in \"gang\"", find("^g.*g$", "gang"));
    show("find ^$ in \"\"", find("^$", ""));

    section("5. MULTILINE mode");
    show("^The.* with MULTILINE",
        Pattern.compile("^The.*", Pattern.MULTILINE).matcher(SENTENCES).results()
            .map(MatchResult::group).toList());
    show("(?m)^.*ing$", all("(?m)^.*ing$", SENTENCES));
    show("^.*ing$ without MULTILINE", all("^.*ing$", SENTENCES));
    show("lines().filter(startsWith(\"The\"))",
        SENTENCES.lines().filter(line -> line.startsWith("The")).toList());

    section("6. A trailing line break");
    show("find cat$ in \"cat\\n\"", find("cat$", "cat\n"));
    show("\"cat\\n\".matches(\"cat$\")", "cat\n".matches("cat$"));
    show("\"a\\nb\\n\".replaceAll(\"$\", \"!\")", visible("a\nb\n".replaceAll("$", "!")));
    show("\"a\\nb\\n\".replaceAll(\"(?m)$\", \"!\")", visible("a\nb\n".replaceAll("(?m)$", "!")));
    show("\"a\\nb\\n\".replaceAll(\"(?m)^\", \"> \")", visible("a\nb\n".replaceAll("(?m)^", "> ")));

    section("7. \\A, \\z and \\Z");
    for (String anchor : List.of("^", "(?m)^", "$", "\\A", "\\Z", "\\z")) {
      show("\"abc\\n\".replaceAll(\"" + anchor + "\", \"!\")", visible("abc\n".replaceAll(anchor, "!")));
    }
    show("find (?m)^banana in FRUITS", find("(?m)^banana", FRUITS));
    show("find (?m)\\Abanana in FRUITS", find("(?m)\\Abanana", FRUITS));
    show("find (?m)apple$ in FRUITS", find("(?m)apple$", FRUITS));
    show("find (?m)apple\\z in FRUITS", find("(?m)apple\\z", FRUITS));
    show("find cat\\Z in \"cat\\n\"", find("cat\\Z", "cat\n"));
    show("find cat\\z in \"cat\\n\"", find("cat\\z", "cat\n"));
    show("find ^\\d+$ in \"123\\n\"", find("^\\d+$", "123\n"));
    show("find \\A\\d+\\z in \"123\\n\"", find("\\A\\d+\\z", "123\n"));
    show("\"123\\n\".matches(\"\\\\d+\")", "123\n".matches("\\d+"));

    section("8. Line terminators");
    show("find (?m)cat$ in \"cat\\r\\ndog\"", find("(?m)cat$", "cat\r\ndog"));
    show("find (?md)cat$ in \"cat\\r\\ndog\"", find("(?md)cat$", "cat\r\ndog"));
    show("find (?m)^dog in \"cat\\u2028dog\"", find("(?m)^dog", "cat\u2028dog"));
    show("\"a\\r\\nb\\nc\\rd\".split(\"\\\\R\")", Arrays.toString("a\r\nb\nc\rd".split("\\R")));
    show("\"a\\r\\nb\".split(\"\\n\")", visible(Arrays.toString("a\r\nb".split("\n"))));
  }

  static boolean find(String regex, String input) {
    return Pattern.compile(regex).matcher(input).find();
  }

  static List<String> all(String regex, String input) {
    return Pattern.compile(regex).matcher(input).results().map(MatchResult::group).toList();
  }

  /** Shows line breaks as escape sequences, so the output stays on one line. */
  static String visible(String s) {
    return "\"" + s.replace("\r", "\\r").replace("\n", "\\n") + "\"";
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-48s -> %s%n", expression, result);
  }
}

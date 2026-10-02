package com.howtodoinjava.regex;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Non-ASCII characters in the output are printed as Java escapes so the console encoding does not matter.
 * Source code for https://howtodoinjava.com/java/regex/java-regex-match-trademark-symbol/
 */
public class TrademarkSymbol {

  /** The trademark sign written as a regex escape. */
  static final Pattern TM = Pattern.compile("\\u2122");

  /** A word directly followed by the trademark sign, with an optional space. */
  static final Pattern BRAND = Pattern.compile("(\\w+)\\s?\\u2122");

  /** Trademark, registered, copyright, service mark and sound recording copyright signs. */
  static final Pattern MARKS = Pattern.compile("[\\u2122\\u00ae\\u00a9\\u2120\\u2117]");

  /** Plain-text and HTML spellings that stand for the trademark sign. */
  static final Pattern TM_SPELLINGS = Pattern.compile("\\s?\\((?i:tm)\\)|&(?:trade|#8482|#x2122);");

  public static void main(String[] args) {

    String text = "Java\u2122 and Duke\u2122 rule";

    section("Quick reference");
    show("\"Java\\u2122\".matches(\"Java\\\\u2122\")", "Java\u2122".matches("Java\\u2122"));
    show("TM.matcher(text).find()", TM.matcher(text).find());
    show("TM.matcher(\"Java\").find()", TM.matcher("Java").find());
    show("TM.matcher(text).results().count()", TM.matcher(text).results().count());
    show("BRAND words in text", BRAND.matcher(text).results().map(r -> r.group(1)).toList());
    show("text.replaceAll(\"\\\\u2122\", \"\")", text.replaceAll("\\u2122", ""));
    show("\"Java(TM)\".replaceAll(\"\\\\((?i:tm)\\\\)\", \"\\u2122\")", "Java(TM)".replaceAll("\\((?i:tm)\\)", "\u2122"));

    section("1. Find the trademark sign and its position");
    TM.matcher(text).results().forEach(r -> show("found at", r.start() + "-" + r.end()));
    show("text.indexOf('\\u2122')", text.indexOf('\u2122'));
    show("text.contains(\"\\u2122\")", text.contains("\u2122"));

    section("2. Ways to write the trademark sign in a Java regex");
    String tm = "\u2122";
    for (String regex : List.of("\u2122", "\\u2122", "\\x{2122}", "\\N{TRADE MARK SIGN}", "\\p{So}",
        "\\p{InLetterlikeSymbols}", "\\p{Sc}", "\\x2122", "\\u{2122}")) {
      String label = regex.equals("\u2122") ? "(the character itself)" : regex;
      try {
        show(label, Pattern.compile(regex).matcher(tm).matches());
      } catch (PatternSyntaxException e) {
        show(label, "PatternSyntaxException: " + e.getDescription());
      }
    }
    show("\"!22\".matches(\"\\\\x2122\")", "!22".matches("\\x2122"));

    section("3. Brand names marked with the sign");
    BRAND.matcher(text).results()
        .forEach(r -> show("brand " + r.group(1), r.start() + "-" + r.end()));
    show("BRAND on \"Duke \\u2122\"", BRAND.matcher("Duke \u2122").results().map(r -> r.group(1)).toList());
    show("\\bJava\\u2122\\b find in \"Java\\u2122\"", Pattern.compile("\\bJava\\u2122\\b").matcher("Java\u2122").find());
    show("\\bJava\\u2122 find in \"Java\\u2122\"", Pattern.compile("\\bJava\\u2122").matcher("Java\u2122").find());
    show("\\bJava\\u2122(?!\\w) find in \"Java\\u2122s\"",
        Pattern.compile("\\bJava\\u2122(?!\\w)").matcher("Java\u2122s").find());

    section("4. Related legal symbols");
    for (int c : new int[] {0x2122, 0x00AE, 0x00A9, 0x2120, 0x2117}) {
      String s = new String(Character.toChars(c));
      show(String.format("U+%04X %s", c, Character.getName(c)),
          "So=" + s.matches("\\p{So}") + " MARKS=" + MARKS.matcher(s).matches());
    }
    String legal = "Java\u2122, Duke\u00ae, \u00a9 2026";
    show("MARKS found in legal", MARKS.matcher(legal).results().map(r -> r.group()).toList());
    show("legal.replaceAll(MARKS)", MARKS.matcher(legal).replaceAll(""));

    section("5. Other spellings of the sign");
    String mixed = "Java(TM), Duke (tm), Kotlin&trade; and Scala&#8482;";
    show("TM_SPELLINGS replaced", TM_SPELLINGS.matcher(mixed).replaceAll("\u2122"));
    String emojiStyle = "\u2122\ufe0f";
    show("\"\\u2122\\ufe0f\".matches(\"\\\\u2122\")", emojiStyle.matches("\\u2122"));
    show("\"\\u2122\\ufe0f\".matches(\"\\\\u2122\\\\ufe0f?\")", emojiStyle.matches("\\u2122\\ufe0f?"));
    show("TM.matcher(\"\\u2122\\ufe0f\").find()", TM.matcher(emojiStyle).find());
    show("NFKC of \\u2122", Normalizer.normalize("\u2122", Normalizer.Form.NFKC));
    show("NFKC of \\u2120", Normalizer.normalize("\u2120", Normalizer.Form.NFKC));
    show("NFKC of \\u00ae", Normalizer.normalize("\u00ae", Normalizer.Form.NFKC));
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-62s -> %s%n", escape(expression), escape(String.valueOf(result)));
  }

  static String escape(String s) {
    StringBuilder sb = new StringBuilder();
    s.codePoints().forEach(c -> sb.append(c > 0x7E ? String.format("\\u%04x", c) : String.valueOf((char) c)));
    return sb.toString();
  }
}

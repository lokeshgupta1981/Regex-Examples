package com.howtodoinjava.regex;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/java-regex-validate-and-format-north-american-phone-numbers/
 */
public class NorthAmericanPhoneNumbers {

  /** 1. Any 10 digits in groups of 3-3-4, with optional parentheses and separators. */
  static final Pattern BASIC = Pattern.compile("^\\(?(\\d{3})\\)?[-. ]?(\\d{3})[-. ]?(\\d{4})$");

  /** 2. NANP rules: area code and exchange start with 2-9; optional +1 or 1; balanced parentheses. */
  static final Pattern NANP = Pattern.compile(
      "^(?:\\+?1[-. ]?)?"
      + "(?:\\(([2-9]\\d{2})\\)|([2-9]\\d{2}))"
      + "[-. ]?([2-9]\\d{2})[-. ]?(\\d{4})$");

  /** 3. Same as NANP plus an optional extension: x123, ext 123 or ext. 123. */
  static final Pattern NANP_EXT = Pattern.compile(
      "^(?:\\+?1[-. ]?)?"
      + "(?:\\(([2-9]\\d{2})\\)|([2-9]\\d{2}))"
      + "[-. ]?([2-9]\\d{2})[-. ]?(\\d{4})"
      + "(?: ?(?:x|ext\\.?) ?(\\d{1,5}))?$", Pattern.CASE_INSENSITIVE);

  /** 4. Finds numbers inside longer text: word boundaries instead of anchors. */
  static final Pattern IN_TEXT = Pattern.compile("\\b([2-9]\\d{2})[-. ]([2-9]\\d{2})[-. ](\\d{4})\\b");

  static final List<String> SAMPLES = List.of(
      "2025550123",
      "202-555-0123",
      "202.555.0123",
      "202 555 0123",
      "(202) 555-0123",
      "(202)555-0123",
      "+1 202 555 0123",
      "1-202-555-0123",
      "(202-555-0123",
      "202) 555-0123",
      "123-456-7890",
      "202-155-0123",
      "202-555-012",
      "202--555-0123",
      "+44 20 7946 0958");

  public static void main(String[] args) {

    section("Quick reference");
    show("BASIC: \"123-456-7890\"", BASIC.matcher("123-456-7890").matches());
    show("NANP: \"123-456-7890\"", NANP.matcher("123-456-7890").matches());
    show("NANP: \"+1 (202) 555-0123\"", NANP.matcher("+1 (202) 555-0123").matches());
    show("format(\"202.555.0123\")", format("202.555.0123"));
    show("toE164(\"(202) 555-0123\")", toE164("(202) 555-0123"));
    show("reformatInText(\"Call 202.555.0123 or 312 555 0199.\")",
        reformatInText("Call 202.555.0123 or 312 555 0199."));

    section("BASIC versus NANP");
    System.out.printf("%-20s %-6s %-6s %s%n", "input", "BASIC", "NANP", "NANP formatted");
    for (String s : SAMPLES) {
      System.out.printf("%-20s %-6s %-6s %s%n", "\"" + s + "\"", BASIC.matcher(s).matches(),
          NANP.matcher(s).matches(), format(s));
    }

    section("BASIC with replaceFirst");
    show("\"202.555.0123\" -> ($1) $2-$3", BASIC.matcher("202.555.0123").replaceFirst("($1) $2-$3"));
    show("\"(202-555-0123\" -> ($1) $2-$3", BASIC.matcher("(202-555-0123").replaceFirst("($1) $2-$3"));
    show("\"hello\" -> ($1) $2-$3", BASIC.matcher("hello").replaceFirst("($1) $2-$3"));

    section("Groups");
    Matcher m = NANP.matcher("(202) 555-0123");
    show("matches()", m.matches());
    show("group(1)", m.group(1));
    show("group(2)", m.group(2));
    show("group(3)", m.group(3));
    show("group(4)", m.group(4));
    Matcher m2 = NANP.matcher("202-555-0123");
    show("202-555-0123: matches()", m2.matches());
    show("202-555-0123: group(1)", m2.group(1));
    show("202-555-0123: group(2)", m2.group(2));

    section("Several output formats");
    show("\"202 555 0123\" -> ($1$2) $3-$4", NANP.matcher("202 555 0123").replaceFirst("($1$2) $3-$4"));
    show("\"202 555 0123\" -> $1$2-$3-$4", NANP.matcher("202 555 0123").replaceFirst("$1$2-$3-$4"));
    show("\"202 555 0123\" -> $1$2.$3.$4", NANP.matcher("202 555 0123").replaceFirst("$1$2.$3.$4"));
    show("\"202 555 0123\" -> +1$1$2$3$4", NANP.matcher("202 555 0123").replaceFirst("+1$1$2$3$4"));

    section("Extensions");
    for (String s : List.of("202-555-0123 x45", "202-555-0123 ext. 45", "(202) 555-0123 EXT 45", "202-555-0123x45",
        "202-555-0123 ext", "202-555-0123")) {
      Matcher e = NANP_EXT.matcher(s);
      show("NANP_EXT \"" + s + "\"", e.matches() ? "true, ext=" + e.group(5) : "false");
    }

    section("Numbers inside text");
    String text = "Call 202.555.0123 or 312 555 0199. Order 1234567890 ships today.";
    show("IN_TEXT.matcher(text).results().count()", IN_TEXT.matcher(text).results().count());
    show("replaceAll(\"($1) $2-$3\")", IN_TEXT.matcher(text).replaceAll("($1) $2-$3"));
    show("replaceAll with a function", IN_TEXT.matcher(text)
        .replaceAll(r -> "(" + r.group(1) + ") " + r.group(2) + "-" + r.group(3)));
  }

  /** Returns the number as (NXX) NXX-XXXX, or null when it is not a valid NANP number. */
  static String format(String input) {
    Matcher m = NANP.matcher(input.strip());
    return m.matches() ? m.replaceFirst("($1$2) $3-$4") : null;
  }

  /** Returns the number in E.164 form (+1 and 10 digits), or null. */
  static String toE164(String input) {
    Matcher m = NANP.matcher(input.strip());
    return m.matches() ? m.replaceFirst("+1$1$2$3$4") : null;
  }

  /** Rewrites every separated 10-digit number in a text as (NXX) NXX-XXXX. */
  static String reformatInText(String text) {
    return IN_TEXT.matcher(text).replaceAll("($1) $2-$3");
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-48s -> %s%n", expression, result);
  }
}

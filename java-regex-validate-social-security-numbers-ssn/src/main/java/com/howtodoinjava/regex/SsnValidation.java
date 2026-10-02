package com.howtodoinjava.regex;

import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * All sample numbers are made up. Source code for
 * https://howtodoinjava.com/java/regex/java-regex-validate-social-security-numbers-ssn/
 */
public class SsnValidation {

  /** AAA-GG-SSSS with the SSA rules for invalid area, group and serial numbers. */
  static final Pattern SSN =
      Pattern.compile("^(?!000|666)[0-8][0-9]{2}-(?!00)[0-9]{2}-(?!0000)[0-9]{4}$");

  /** Format only, no SSA rules. */
  static final Pattern SSN_SHAPE = Pattern.compile("^[0-9]{3}-[0-9]{2}-[0-9]{4}$");

  /** Hyphens, spaces or nothing, but the same separator in both places (backreference \1). */
  static final Pattern SSN_FLEXIBLE = Pattern.compile(
      "^(?!000|666)[0-8][0-9]{2}([- ]?)(?!00)[0-9]{2}\\1(?!0000)[0-9]{4}$");

  /** Named groups for the three parts. */
  static final Pattern SSN_PARTS = Pattern.compile(
      "(?<area>[0-9]{3})-(?<group>[0-9]{2})-(?<serial>[0-9]{4})");

  /** An SSN-like number inside text, for masking logs or finding sensitive data. */
  static final Pattern SSN_IN_TEXT = Pattern.compile("\\b[0-9]{3}-[0-9]{2}-([0-9]{4})\\b");

  static boolean isValidSsn(String input) {
    return input != null && SSN.matcher(input).matches();
  }

  /** The same rules in plain Java, without lookaheads. */
  static boolean isValidSsnWithoutLookahead(String input) {
    if (input == null) {
      return false;
    }
    Matcher m = SSN_PARTS.matcher(input);
    if (!m.matches()) {
      return false;
    }
    int area = Integer.parseInt(m.group("area"));
    int group = Integer.parseInt(m.group("group"));
    int serial = Integer.parseInt(m.group("serial"));
    return area != 0 && area != 666 && area < 900 && group != 0 && serial != 0;
  }

  /** Accepts "123-45-6789", "123 45 6789" and "123456789"; returns "123-45-6789". */
  static Optional<String> normalizeSsn(String input) {
    if (input == null) {
      return Optional.empty();
    }
    String trimmed = input.strip();
    if (!SSN_FLEXIBLE.matcher(trimmed).matches()) {
      return Optional.empty();
    }
    String digits = trimmed.replaceAll("[^0-9]", "");
    return Optional.of(digits.substring(0, 3) + "-" + digits.substring(3, 5) + "-" + digits.substring(5));
  }

  /** Replaces every SSN-like number in the text with ***-**-1234. */
  static String maskSsns(String text) {
    return SSN_IN_TEXT.matcher(text).replaceAll("***-**-$1");
  }

  static List<String> findSsns(String text) {
    return SSN_IN_TEXT.matcher(text).results().map(MatchResult::group).toList();
  }

  static final List<String> SAMPLES = List.of(
      "123-45-6789", "001-45-6789", "899-45-6789", "123-01-6789", "123-45-0001",
      "000-45-6789", "666-45-6789", "900-45-6789", "999-45-6789", "123-00-6789", "123-45-0000",
      "123456789", "123 45 6789", "12-345-6789", "123-456-789", "123-45-67890", "abc-de-fghi");

  public static void main(String[] args) {

    section("Quick reference");
    show("isValidSsn(\"123-45-6789\")", isValidSsn("123-45-6789"));
    show("isValidSsn(\"000-45-6789\")", isValidSsn("000-45-6789"));
    show("isValidSsn(\"666-45-6789\")", isValidSsn("666-45-6789"));
    show("isValidSsn(\"900-45-6789\")", isValidSsn("900-45-6789"));
    show("isValidSsn(\"123-00-6789\")", isValidSsn("123-00-6789"));
    show("isValidSsn(\"123-45-0000\")", isValidSsn("123-45-0000"));
    show("normalizeSsn(\"123 45 6789\")", normalizeSsn("123 45 6789"));
    show("maskSsns(\"SSN 123-45-6789 on file\")", maskSsns("SSN 123-45-6789 on file"));

    section("2. Shape only");
    show("SSN_SHAPE.matcher(\"123-45-6789\").matches()", SSN_SHAPE.matcher("123-45-6789").matches());
    show("SSN_SHAPE.matcher(\"000-00-0000\").matches()", SSN_SHAPE.matcher("000-00-0000").matches());
    show("SSN_SHAPE.matcher(\"666-45-6789\").matches()", SSN_SHAPE.matcher("666-45-6789").matches());

    section("3/4. Samples: regex / plain Java");
    for (String s : SAMPLES) {
      show("\"" + s + "\"", isValidSsn(s) + " / " + isValidSsnWithoutLookahead(s));
    }

    section("4. Lookahead detail");
    show("\"6667-45-6789\" (four-digit area)", isValidSsn("6667-45-6789"));
    show("\"066-45-6789\"", isValidSsn("066-45-6789"));
    show("\"660-45-6789\"", isValidSsn("660-45-6789"));

    section("5. Optional separators");
    for (String s : List.of("123-45-6789", "123 45 6789", "123456789", "123-45 6789", "12345-6789",
        "000456789", " 123456789 ")) {
      show("normalizeSsn(\"" + s + "\")", normalizeSsn(s));
    }
    show("SSN_FLEXIBLE.matcher(\"123-45 6789\").matches()", SSN_FLEXIBLE.matcher("123-45 6789").matches());

    section("6. Parts");
    Matcher m = SSN_PARTS.matcher("123-45-6789");
    show("m.matches()", m.matches());
    show("m.group(\"area\")", m.group("area"));
    show("m.group(\"group\")", m.group("group"));
    show("m.group(\"serial\")", m.group("serial"));

    section("7. Finding and masking SSNs in text");
    String log = "user=lokesh ssn=123-45-6789 ref=123-45-6780 order=1234-56-7890";
    show("findSsns(log)", findSsns(log));
    show("maskSsns(log)", maskSsns(log));
    show("maskSsns(\"id 000-12-3456\")", maskSsns("id 000-12-3456"));
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-50s -> %s%n", expression, result);
  }
}

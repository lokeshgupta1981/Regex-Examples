package com.howtodoinjava.regex;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/canada-postal-code-validation/
 */
public class CanadaPostalCodeValidation {

  /** Shape only: letter, digit, letter, space, digit, letter, digit. */
  static final Pattern SHAPE_ONLY = Pattern.compile("^[A-Z][0-9][A-Z] [0-9][A-Z][0-9]$");

  /** Canada Post rules written as character classes. */
  static final Pattern POSTAL_CODE =
      Pattern.compile("^[ABCEGHJ-NPRSTVXY][0-9][ABCEGHJ-NPRSTV-Z] [0-9][ABCEGHJ-NPRSTV-Z][0-9]$");

  /** The same rules written with a negative lookahead. */
  static final Pattern POSTAL_CODE_LOOKAHEAD =
      Pattern.compile("^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] [0-9][A-Z][0-9]$");

  /** Named groups for the two halves, optional space or hyphen, any case. */
  static final Pattern POSTAL_CODE_PARTS = Pattern.compile(
      "(?<fsa>[ABCEGHJ-NPRSTVXY][0-9][ABCEGHJ-NPRSTV-Z])[ -]?(?<ldu>[0-9][ABCEGHJ-NPRSTV-Z][0-9])",
      Pattern.CASE_INSENSITIVE);

  static boolean isValidPostalCode(String input) {
    return input != null && POSTAL_CODE.matcher(input).matches();
  }

  /** Accepts lower case, a missing space or a hyphen; returns the Canada Post form "A1A 1A1". */
  static Optional<String> normalizePostalCode(String input) {
    if (input == null) {
      return Optional.empty();
    }
    Matcher m = POSTAL_CODE_PARTS.matcher(input.strip());
    if (!m.matches()) {
      return Optional.empty();
    }
    return Optional.of((m.group("fsa") + " " + m.group("ldu")).toUpperCase());
  }

  /** The first letter of the forward sortation area names the province or territory. */
  static String region(String postalCode) {
    return switch (postalCode.charAt(0)) {
      case 'A' -> "Newfoundland and Labrador";
      case 'B' -> "Nova Scotia";
      case 'C' -> "Prince Edward Island";
      case 'E' -> "New Brunswick";
      case 'G', 'H', 'J' -> "Quebec";
      case 'K', 'L', 'M', 'N', 'P' -> "Ontario";
      case 'R' -> "Manitoba";
      case 'S' -> "Saskatchewan";
      case 'T' -> "Alberta";
      case 'V' -> "British Columbia";
      case 'X' -> "Northwest Territories or Nunavut";
      case 'Y' -> "Yukon";
      default -> "unknown";
    };
  }

  static boolean isRural(String postalCode) {
    return postalCode.charAt(1) == '0';
  }

  static final List<String> SAMPLES = List.of(
      "A1A 1A1", "K1K 1K1", "T2W 3Z4", "X0A 1B2", "Y1A 9V9",
      "D1A 1A1", "W1A 1A1", "Z1A 1A1", "A1D 1A1", "A1A 1O1", "A1A 1U1",
      "A1A1A1", "A1A-1A1", "A1A  1A1", "a1a 1a1", "1A1 A1A", "A1A 1A");

  public static void main(String[] args) {

    section("Quick reference");
    show("isValidPostalCode(\"A1A 1A1\")", isValidPostalCode("A1A 1A1"));
    show("isValidPostalCode(\"T2W 3Z4\")", isValidPostalCode("T2W 3Z4"));
    show("isValidPostalCode(\"W1A 1A1\")", isValidPostalCode("W1A 1A1"));
    show("isValidPostalCode(\"A1A 1O1\")", isValidPostalCode("A1A 1O1"));
    show("isValidPostalCode(\"A1A1A1\")", isValidPostalCode("A1A1A1"));
    show("normalizePostalCode(\"a1a-1a1\")", normalizePostalCode("a1a-1a1"));
    show("region(\"K1K 1K1\")", region("K1K 1K1"));

    section("2. Shape only versus Canada Post rules");
    for (String s : List.of("A1A 1A1", "D1A 1A1", "W1A 1A1", "A1A 1O1")) {
      show("SHAPE_ONLY  \"" + s + "\"", SHAPE_ONLY.matcher(s).matches());
      show("POSTAL_CODE \"" + s + "\"", POSTAL_CODE.matcher(s).matches());
    }

    section("3. Samples: character classes and lookahead");
    for (String s : SAMPLES) {
      show("\"" + s + "\"  classes / lookahead",
          POSTAL_CODE.matcher(s).matches() + " / " + POSTAL_CODE_LOOKAHEAD.matcher(s).matches());
    }

    section("4. Case, spaces and hyphens");
    show("\"A1A1A1\".matches(\"...[ABCEGHJ-NPRSTV-Z] ?[0-9]...\")",
        "A1A1A1".matches("[ABCEGHJ-NPRSTVXY][0-9][ABCEGHJ-NPRSTV-Z] ?[0-9][ABCEGHJ-NPRSTV-Z][0-9]"));
    show("POSTAL_CODE_CI.matcher(\"a1a 1a1\").matches()",
        Pattern.compile(POSTAL_CODE.pattern(), Pattern.CASE_INSENSITIVE).matcher("a1a 1a1").matches());
    show("POSTAL_CODE_CI.matcher(\"d1a 1a1\").matches()",
        Pattern.compile(POSTAL_CODE.pattern(), Pattern.CASE_INSENSITIVE).matcher("d1a 1a1").matches());
    show("normalizePostalCode(\"A1A 1A1\")", normalizePostalCode("A1A 1A1"));
    show("normalizePostalCode(\"a1a1a1\")", normalizePostalCode("a1a1a1"));
    show("normalizePostalCode(\"a1a-1a1\")", normalizePostalCode("a1a-1a1"));
    show("normalizePostalCode(\"  t2w 3z4 \")", normalizePostalCode("  t2w 3z4 "));
    show("normalizePostalCode(\"A1A  1A1\")", normalizePostalCode("A1A  1A1"));
    show("normalizePostalCode(\"W1A 1A1\")", normalizePostalCode("W1A 1A1"));

    section("5. Forward sortation area and local delivery unit");
    Matcher m = POSTAL_CODE_PARTS.matcher("K1K 1K1");
    m.matches();
    show("m.group(\"fsa\")", m.group("fsa"));
    show("m.group(\"ldu\")", m.group("ldu"));
    show("region(\"K1K 1K1\")", region("K1K 1K1"));
    show("region(\"T2W 3Z4\")", region("T2W 3Z4"));
    show("region(\"X0A 1B2\")", region("X0A 1B2"));
    show("isRural(\"X0A 1B2\")", isRural("X0A 1B2"));
    show("\"X0A 1B2\".charAt(1) == '0'", "X0A 1B2".charAt(1) == '0');
    show("isRural(\"T2W 3Z4\")", isRural("T2W 3Z4"));

    section("6. Format check only");
    show("isValidPostalCode(\"Y9Z 9Z9\")", isValidPostalCode("Y9Z 9Z9"));
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-62s -> %s%n", expression, result);
  }
}

package com.howtodoinjava.regex;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/uk-postcode-validation/
 */
public class UkPostcodeValidation {

  /** The six formats plus GIR 0AA, with the inward-code letter rule. */
  static final Pattern POSTCODE =
      Pattern.compile("^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}$");

  /** Position rules from the Royal Mail Programmers' Guide and BS 7666. */
  static final Pattern POSTCODE_STRICT = Pattern.compile(
      "^(?:(?:[A-PR-UWYZ][0-9]{1,2}"
          + "|[A-PR-UWYZ][A-HK-Y][0-9]{1,2}"
          + "|[A-PR-UWYZ][0-9][A-HJKPSTUW]"
          + "|[A-PR-UWYZ][A-HK-Y][0-9][ABEHMNPRV-Y])"
          + " [0-9][ABD-HJLNP-UW-Z]{2}"
          + "|GIR 0AA)$");

  /** Outward code split into area and district, inward code split into sector and unit. */
  static final Pattern POSTCODE_PARTS = Pattern.compile(
      "(?<outward>(?<area>[A-Z]{1,2})(?<district>[0-9][0-9A-Z]?)) (?<inward>(?<sector>[0-9])(?<unit>[A-Z]{2}))");

  /** The regex published by the UK Home Office for bulk data transfer (note the anchors). */
  static final Pattern GOV_UK_REGEX = Pattern.compile(
      "^([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})"
          + "|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z])))) [0-9][A-Za-z]{2})$");

  /** The same regex with the alternation wrapped in a group, so both anchors apply to all of it. */
  static final Pattern GOV_UK_REGEX_FIXED = Pattern.compile(
      "^(?:([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})"
          + "|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z])))) [0-9][A-Za-z]{2}))$");

  static boolean isValidPostcode(String input) {
    return input != null && POSTCODE.matcher(input).matches();
  }

  /**
   * Removes all whitespace, converts to upper case and puts one space before the last three
   * characters, because the inward code always has three characters.
   */
  static Optional<String> normalizePostcode(String input) {
    if (input == null) {
      return Optional.empty();
    }
    String compact = input.replaceAll("\\s+", "").toUpperCase();
    if (compact.length() < 5 || compact.length() > 7) {
      return Optional.empty();
    }
    String spaced = compact.substring(0, compact.length() - 3) + " " + compact.substring(compact.length() - 3);
    return POSTCODE.matcher(spaced).matches() ? Optional.of(spaced) : Optional.empty();
  }

  static final List<String> SAMPLES = List.of(
      "M1 1AA", "B33 8TH", "CR2 6XH", "DN55 1PT", "W1A 0AX", "EC1A 1BB", "GIR 0AA",
      "QA1 1AA", "AI1 1AA", "W1L 1AA", "SW1C 1AA", "M1 1CA", "M1 1AO",
      "SW1A1BB", "sw1a 1bb", "M1  1AA", "M1 1AAA", "123 4AB", "ASCN 1ZZ");

  public static void main(String[] args) {

    section("Quick reference");
    show("POSTCODE.matcher(\"M1 1AA\").matches()", POSTCODE.matcher("M1 1AA").matches());
    show("POSTCODE.matcher(\"EC1A 1BB\").matches()", POSTCODE.matcher("EC1A 1BB").matches());
    show("POSTCODE.matcher(\"DN55 1PT\").matches()", POSTCODE.matcher("DN55 1PT").matches());
    show("POSTCODE.matcher(\"M1 1CA\").matches()", POSTCODE.matcher("M1 1CA").matches());
    show("POSTCODE.matcher(\"EC1A1BB\").matches()", POSTCODE.matcher("EC1A1BB").matches());
    show("POSTCODE_STRICT.matcher(\"QA1 1AA\").matches()", POSTCODE_STRICT.matcher("QA1 1AA").matches());
    show("normalizePostcode(\" ec1a1bb \")", normalizePostcode(" ec1a1bb "));

    section("3. Samples: POSTCODE / POSTCODE_STRICT");
    for (String s : SAMPLES) {
      show("\"" + s + "\"", POSTCODE.matcher(s).matches() + " / " + POSTCODE_STRICT.matcher(s).matches());
    }

    section("4. Normalizing user input");
    for (String s : List.of("ec1a1bb", " ec1a 1bb ", "Cr2  6xh", "m11aa", "M1 1CA", "SW1A 1BBX", "M1")) {
      show("normalizePostcode(\"" + s + "\")", normalizePostcode(s));
    }

    section("5. Outward and inward code");
    Matcher m = POSTCODE_PARTS.matcher("DN55 1PT");
    show("m.matches()", m.matches());
    for (String g : List.of("outward", "area", "district", "inward", "sector", "unit")) {
      show("m.group(\"" + g + "\")", m.group(g));
    }

    section("7. Overseas territories");
    show("isValidPostcode(\"ASCN 1ZZ\")", isValidPostcode("ASCN 1ZZ"));
    show("isValidPostcode(\"GX11 1AA\")", isValidPostcode("GX11 1AA"));
    show("POSTCODE_STRICT.matcher(\"GX11 1AA\").matches()", POSTCODE_STRICT.matcher("GX11 1AA").matches());

    section("6. The anchors in the gov.uk regex");
    show("GOV_UK_REGEX.matcher(\"EC1A 1BB\").matches()", GOV_UK_REGEX.matcher("EC1A 1BB").matches());
    show("GOV_UK_REGEX.matcher(\"ec1a 1bb\").matches()", GOV_UK_REGEX.matcher("ec1a 1bb").matches());
    show("GOV_UK_REGEX.matcher(\"Ref 99 EC1A 1BB\").find()", GOV_UK_REGEX.matcher("Ref 99 EC1A 1BB").find());
    show("GOV_UK_REGEX.matcher(\"GIR 0AA, flat 2\").find()", GOV_UK_REGEX.matcher("GIR 0AA, flat 2").find());
    show("GOV_UK_REGEX_FIXED.matcher(\"Ref 99 EC1A 1BB\").find()", GOV_UK_REGEX_FIXED.matcher("Ref 99 EC1A 1BB").find());
    show("GOV_UK_REGEX_FIXED.matcher(\"GIR 0AA, flat 2\").find()", GOV_UK_REGEX_FIXED.matcher("GIR 0AA, flat 2").find());
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-58s -> %s%n", expression, result);
  }
}

package com.howtodoinjava.regex;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/us-postal-zip-code-validation/
 */
public class UsZipCodeValidation {

  /** Five digits, optionally followed by a hyphen and four digits (ZIP+4). */
  static final Pattern ZIP = Pattern.compile("^[0-9]{5}(?:-[0-9]{4})?$");

  /** Lenient input: hyphen, space or nothing before the four-digit add-on. */
  static final Pattern ZIP_LENIENT = Pattern.compile("(?<zip>\\d{5})(?:[-\\s]?(?<plus4>\\d{4}))?");

  /** A ZIP code inside longer text. */
  static final Pattern ZIP_IN_TEXT = Pattern.compile("\\b\\d{5}(?:-\\d{4})?\\b");

  /** A ZIP code that follows a two-letter state abbreviation. */
  static final Pattern ZIP_AFTER_STATE = Pattern.compile("\\b[A-Z]{2}\\s+(\\d{5}(?:-\\d{4})?)\\b");

  static boolean isValidZip(String input) {
    return input != null && ZIP.matcher(input).matches();
  }

  /** Accepts "12345", "12345-6789", "12345 6789" and "123456789", returns the USPS form. */
  static Optional<String> normalizeZip(String input) {
    if (input == null) {
      return Optional.empty();
    }
    Matcher m = ZIP_LENIENT.matcher(input.strip());
    if (!m.matches()) {
      return Optional.empty();
    }
    String plus4 = m.group("plus4");
    return Optional.of(plus4 == null ? m.group("zip") : m.group("zip") + "-" + plus4);
  }

  static List<String> findZips(String text) {
    return ZIP_IN_TEXT.matcher(text).results().map(MatchResult::group).toList();
  }

  static List<String> findZipsAfterState(String text) {
    return ZIP_AFTER_STATE.matcher(text).results().map(r -> r.group(1)).toList();
  }

  public static void main(String[] args) {

    section("Quick reference");
    show("ZIP.matcher(\"12345\").matches()", ZIP.matcher("12345").matches());
    show("ZIP.matcher(\"12345-6789\").matches()", ZIP.matcher("12345-6789").matches());
    show("ZIP.matcher(\"1234\").matches()", ZIP.matcher("1234").matches());
    show("ZIP.matcher(\"123456\").matches()", ZIP.matcher("123456").matches());
    show("ZIP.matcher(\"12345 6789\").matches()", ZIP.matcher("12345 6789").matches());
    show("\"12345\".matches(\"\\\\d{5}(-\\\\d{4})?\")", "12345".matches("\\d{5}(-\\d{4})?"));
    show("normalizeZip(\" 123456789 \")", normalizeZip(" 123456789 "));
    show("findZips(\"Ship to 12345 or 54321-0001\")", findZips("Ship to 12345 or 54321-0001"));

    section("1. Valid and invalid samples");
    for (String s : List.of("12345", "01234", "12345-6789", "1234", "123456", "12345-678",
        "12345-67890", "1234-56789", "12345 6789", "123456789", "ABCDE", "12345-", " 12345")) {
      show("isValidZip(\"" + s + "\")", isValidZip(s));
    }
    show("isValidZip(null)", isValidZip(null));

    section("2. String.matches() and a reused Pattern");
    show("\"12345-6789\".matches(\"\\\\d{5}(-\\\\d{4})?\")", "12345-6789".matches("\\d{5}(-\\d{4})?"));
    show("\"12345-6789\".matches(\"[0-9]{5}\")", "12345-6789".matches("[0-9]{5}"));
    Predicate<String> isZip = ZIP.asMatchPredicate();
    show("isZip.test(\"54321\")", isZip.test("54321"));
    show("isZip.test(\"5432\")", isZip.test("5432"));
    show("List.of(\"12345\", \"9876\", \"12345-6789\").stream().filter(isZip).toList()",
        List.of("12345", "9876", "12345-6789").stream().filter(isZip).toList());
    show("ZIP.matcher(\"12345\\n\").matches()", ZIP.matcher("12345\n").matches());
    show("ZIP.matcher(\"12345\\n\").find()", ZIP.matcher("12345\n").find());

    section("3. Normalizing user input");
    show("normalizeZip(\"12345\")", normalizeZip("12345"));
    show("normalizeZip(\"12345-6789\")", normalizeZip("12345-6789"));
    show("normalizeZip(\"12345 6789\")", normalizeZip("12345 6789"));
    show("normalizeZip(\" 123456789 \")", normalizeZip(" 123456789 "));
    show("normalizeZip(\"1234-5678\")", normalizeZip("1234-5678"));
    Matcher m = ZIP_LENIENT.matcher("12345 6789");
    m.matches();
    show("m.group(\"zip\")", m.group("zip"));
    show("m.group(\"plus4\")", m.group("plus4"));

    section("4. Finding ZIP codes in text");
    show("findZips(\"Ship to 12345 or 54321-0001\")", findZips("Ship to 12345 or 54321-0001"));
    show("findZips(\"Order 123456 shipped\")", findZips("Order 123456 shipped"));
    show("findZips(\"Invoice 55555 for Anytown, NY 12345\")", findZips("Invoice 55555 for Anytown, NY 12345"));
    show("findZipsAfterState(\"Invoice 55555 for Anytown, NY 12345\")",
        findZipsAfterState("Invoice 55555 for Anytown, NY 12345"));

    section("5. What the regex does not check");
    show("isValidZip(\"00000\")", isValidZip("00000"));
    show("isValidZip(\"99999\")", isValidZip("99999"));
    show("Integer.parseInt(\"01234\")", Integer.parseInt("01234"));
    show("isValidZip(String.valueOf(1234))", isValidZip(String.valueOf(1234)));
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-70s -> %s%n", expression, result);
  }
}

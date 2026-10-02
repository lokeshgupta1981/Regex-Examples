package com.howtodoinjava.regex;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/java-regex-validate-international-standard-book-number-isbns/
 */
public class IsbnValidation {

  /** An optional "ISBN", "ISBN-10" or "ISBN-13" label, with an optional colon and spaces. */
  static final Pattern LABEL = Pattern.compile("^ISBN(?:-1[03])?:?\\s*");

  /** Hyphens and whitespace between the elements. */
  static final Pattern SEPARATORS = Pattern.compile("[-\\s]");

  /** Compact forms, after the label and separators are removed. */
  static final Pattern ISBN10 = Pattern.compile("^[0-9]{9}[0-9X]$");
  static final Pattern ISBN13 = Pattern.compile("^97[89][0-9]{10}$");

  /** Formatted ISBN-10 or ISBN-13 in one regex (format only, from the Regular Expressions Cookbook). */
  static final Pattern ISBN10_FORMATTED = Pattern.compile(
      "^(?:ISBN(?:-10)?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$)"
          + "[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$");

  static final Pattern ISBN13_FORMATTED = Pattern.compile(
      "^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)"
          + "97[89][- ]?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9]$");

  static final Pattern ISBN_FORMATTED = Pattern.compile(
      "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$"
          + "|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)"
          + "(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$");

  /** Removes the label, hyphens and spaces: "ISBN 978-92-95055-12-4" becomes "9789295055124". */
  static String compact(String input) {
    String noLabel = LABEL.matcher(input.strip().toUpperCase()).replaceFirst("");
    return SEPARATORS.matcher(noLabel).replaceAll("");
  }

  /** ISBN-10: weights 10 down to 1, the sum must be divisible by 11; X stands for 10. */
  static boolean isValidIsbn10(String digits) {
    if (!ISBN10.matcher(digits).matches()) {
      return false;
    }
    int sum = 0;
    for (int i = 0; i < 10; i++) {
      char c = digits.charAt(i);
      int value = (c == 'X') ? 10 : c - '0';
      sum += value * (10 - i);
    }
    return sum % 11 == 0;
  }

  /** ISBN-13: weights 1 and 3 alternately, the sum must be divisible by 10. */
  static boolean isValidIsbn13(String digits) {
    if (!ISBN13.matcher(digits).matches()) {
      return false;
    }
    int sum = 0;
    for (int i = 0; i < 13; i++) {
      int value = digits.charAt(i) - '0';
      sum += (i % 2 == 0) ? value : value * 3;
    }
    return sum % 10 == 0;
  }

  /** Format and check digit, for any of the usual written forms. */
  static boolean isValidIsbn(String input) {
    if (input == null) {
      return false;
    }
    String digits = compact(input);
    return switch (digits.length()) {
      case 10 -> isValidIsbn10(digits);
      case 13 -> isValidIsbn13(digits);
      default -> false;
    };
  }

  /** Check digit for the first 12 digits of an ISBN-13. */
  static int isbn13CheckDigit(String first12) {
    int sum = 0;
    for (int i = 0; i < 12; i++) {
      int value = first12.charAt(i) - '0';
      sum += (i % 2 == 0) ? value : value * 3;
    }
    return (10 - sum % 10) % 10;
  }

  /** Check character for the first 9 digits of an ISBN-10. */
  static char isbn10CheckDigit(String first9) {
    int sum = 0;
    for (int i = 0; i < 9; i++) {
      sum += (first9.charAt(i) - '0') * (10 - i);
    }
    int check = (11 - sum % 11) % 11;
    return check == 10 ? 'X' : (char) ('0' + check);
  }

  /** ISBN-10 to ISBN-13: prefix 978, drop the old check digit, compute a new one. */
  static Optional<String> toIsbn13(String isbn10) {
    String digits = compact(isbn10);
    if (!isValidIsbn10(digits)) {
      return Optional.empty();
    }
    String first12 = "978" + digits.substring(0, 9);
    return Optional.of(first12 + isbn13CheckDigit(first12));
  }

  /** ISBN-13 to ISBN-10: only for the 978 prefix. */
  static Optional<String> toIsbn10(String isbn13) {
    String digits = compact(isbn13);
    if (!isValidIsbn13(digits) || !digits.startsWith("978")) {
      return Optional.empty();
    }
    String first9 = digits.substring(3, 12);
    return Optional.of(first9 + isbn10CheckDigit(first9));
  }

  public static void main(String[] args) {

    section("Quick reference");
    show("compact(\"ISBN 978-92-95055-12-4\")", compact("ISBN 978-92-95055-12-4"));
    show("ISBN13.matcher(\"9789295055124\").matches()", ISBN13.matcher("9789295055124").matches());
    show("ISBN10.matcher(\"123456789X\").matches()", ISBN10.matcher("123456789X").matches());
    show("\"9789295055124\".matches(\"97[89][0-9]{10}\")", "9789295055124".matches("97[89][0-9]{10}"));
    show("\"123456789X\".matches(\"[0-9]{9}[0-9X]\")", "123456789X".matches("[0-9]{9}[0-9X]"));
    show("isValidIsbn(\"ISBN 978-92-95055-12-4\")", isValidIsbn("ISBN 978-92-95055-12-4"));
    show("isValidIsbn(\"978-92-95055-12-5\")", isValidIsbn("978-92-95055-12-5"));
    show("isValidIsbn(\"ISBN-10: 0-306-40615-2\")", isValidIsbn("ISBN-10: 0-306-40615-2"));
    show("isValidIsbn(\"123456789X\")", isValidIsbn("123456789X"));
    show("toIsbn13(\"0-306-40615-2\")", toIsbn13("0-306-40615-2"));

    section("3. Compact form");
    for (String s : List.of("ISBN 978-92-95055-12-4", "ISBN-13: 978 92 95055 12 4", "isbn 978-92-95055-12-4",
        "ISBN-10 0-306-40615-2", "0-306-40615-2", "12345-6789-x")) {
      show("compact(\"" + s + "\")", compact(s));
    }
    show("ISBN13.matcher(\"9799295055124\").matches()", ISBN13.matcher("9799295055124").matches());
    show("ISBN13.matcher(\"9779295055124\").matches()", ISBN13.matcher("9779295055124").matches());
    show("ISBN10.matcher(\"X123456789\").matches()", ISBN10.matcher("X123456789").matches());

    section("4. ISBN-10 check digit");
    show("isValidIsbn10(\"0306406152\")", isValidIsbn10("0306406152"));
    show("isValidIsbn10(\"0306406153\")", isValidIsbn10("0306406153"));
    show("isValidIsbn10(\"123456789X\")", isValidIsbn10("123456789X"));
    show("isValidIsbn10(\"1234567890\")", isValidIsbn10("1234567890"));
    show("isbn10CheckDigit(\"030640615\")", isbn10CheckDigit("030640615"));
    show("isbn10CheckDigit(\"123456789\")", isbn10CheckDigit("123456789"));

    section("5. ISBN-13 check digit");
    show("isbn13CheckDigit(\"978929505512\")", isbn13CheckDigit("978929505512"));
    show("isValidIsbn13(\"9789295055124\")", isValidIsbn13("9789295055124"));
    show("isValidIsbn13(\"9789295055125\")", isValidIsbn13("9789295055125"));
    show("isValidIsbn13(\"9780306406157\")", isValidIsbn13("9780306406157"));
    show("isValidIsbn13(\"9789295055142\")", isValidIsbn13("9789295055142"));
    show("isValidIsbn13(\"9789290555124\")", isValidIsbn13("9789290555124"));
    show("isValidIsbn10(\"0306406125\")", isValidIsbn10("0306406125"));

    section("6. One regex for formatted ISBNs");
    List<String> samples = List.of(
        "ISBN 978-92-95055-12-4", "ISBN-13: 978-92-95055-12-4", "978 92 95055 12 4", "9789295055124",
        "ISBN-10 0-306-40615-2", "ISBN-10: 0-306-40615-2", "0 306 40615 2", "0306406152",
        "978-92-95055-12-5", "0-306-40615-3",
        "ISBN-13 0-306-40615-2", "ISBN-12: 978-92-95055-12-4", "0-3061-40615-2", "978-92-95055124",
        "97892-95055-12-4-1", "ISBN 978--92-95055-12-4");
    for (String s : samples) {
      show("\"" + s + "\"  regex / isValidIsbn",
          ISBN_FORMATTED.matcher(s).matches() + " / " + isValidIsbn(s));
    }
    show("ISBN10_FORMATTED \"0-306-40615-2\"", ISBN10_FORMATTED.matcher("0-306-40615-2").matches());
    show("ISBN10_FORMATTED \"ISBN-13 0-306-40615-2\"", ISBN10_FORMATTED.matcher("ISBN-13 0-306-40615-2").matches());
    show("ISBN13_FORMATTED \"ISBN 978-92-95055-12-4\"", ISBN13_FORMATTED.matcher("ISBN 978-92-95055-12-4").matches());
    show("ISBN13_FORMATTED \"978 10 595 05512 4\"", ISBN13_FORMATTED.matcher("978 10 595 05512 4").matches());

    section("7. Converting between ISBN-10 and ISBN-13");
    show("toIsbn13(\"0-306-40615-2\")", toIsbn13("0-306-40615-2"));
    show("toIsbn13(\"123456789X\")", toIsbn13("123456789X"));
    show("toIsbn13(\"92-95055-12-X\")", toIsbn13("92-95055-12-X"));
    show("toIsbn10(\"978-92-95055-12-4\")", toIsbn10("978-92-95055-12-4"));
    show("toIsbn10(\"9780306406157\")", toIsbn10("9780306406157"));
    show("toIsbn10(\"979-12-34567-89-6\")", toIsbn10("979-12-34567-89-6"));
    show("isValidIsbn(\"979-12-34567-89-6\")", isValidIsbn("979-12-34567-89-6"));
    show("isbn13CheckDigit(\"979123456789\")", isbn13CheckDigit("979123456789"));
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-62s -> %s%n", expression, result);
  }
}

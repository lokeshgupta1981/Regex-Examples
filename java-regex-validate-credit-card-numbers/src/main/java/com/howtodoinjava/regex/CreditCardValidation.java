package com.howtodoinjava.regex;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * The card numbers are the sandbox test numbers published by Cybersource (a Visa company);
 * they cannot be charged. Source code for
 * https://howtodoinjava.com/java/regex/java-regex-validate-credit-card-numbers/
 */
public class CreditCardValidation {

  /** Card number patterns per brand, for digits only (no spaces or hyphens). */
  static final Pattern CARD = Pattern.compile(
      "^(?:(?<visa>4[0-9]{12}(?:[0-9]{3}){0,2})"
          + "|(?<mastercard>(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12})"
          + "|(?<amex>3[47][0-9]{13})"
          + "|(?<discover>(?:6011|64[4-9][0-9]|65[0-9]{2})[0-9]{12,15})"
          + "|(?<diners>3(?:0[0-5]|[68][0-9])[0-9]{11})"
          + "|(?<jcb>35(?:2[89]|[3-8][0-9])[0-9]{12,15}))$");

  /** The pattern from the old version of the article, kept to show its problems. */
  static final Pattern OLD_CARD = Pattern.compile(
      "^(?:(?<visa>4[0-9]{12}(?:[0-9]{3})?)"
          + "|(?<mastercard>5[1-5][0-9]{14})"
          + "|(?<discover>6(?:011|5[0-9]{2})[0-9]{12})"
          + "|(?<amex>3[47][0-9]{13})"
          + "|(?<diners>3(?:0[0-5]|[68][0-9])?[0-9]{11})"
          + "|(?<jcb>(?:2131|1800|35[0-9]{3})[0-9]{11}))$");

  /** Digits, spaces and hyphens only. */
  static final Pattern ALLOWED_INPUT = Pattern.compile("^[0-9 -]+$");

  /** Removes spaces and hyphens; returns empty for any other character. */
  static Optional<String> digitsOnly(String input) {
    if (input == null || !ALLOWED_INPUT.matcher(input.strip()).matches()) {
      return Optional.empty();
    }
    return Optional.of(input.replaceAll("[ -]", ""));
  }

  /** Luhn (mod 10) check: double every second digit from the right. */
  static boolean passesLuhn(String digits) {
    int sum = 0;
    boolean doubleIt = false;
    for (int i = digits.length() - 1; i >= 0; i--) {
      int d = digits.charAt(i) - '0';
      if (doubleIt) {
        d *= 2;
        if (d > 9) {
          d -= 9;
        }
      }
      sum += d;
      doubleIt = !doubleIt;
    }
    return sum % 10 == 0;
  }

  /** The name of the matching brand group, or empty when no brand pattern matches. */
  static Optional<String> brand(String digits) {
    Matcher m = CARD.matcher(digits);
    if (!m.matches()) {
      return Optional.empty();
    }
    return m.namedGroups().keySet().stream()
        .filter(name -> m.group(name) != null)
        .findFirst();
  }

  /** Format, brand and Luhn check together. */
  static boolean isValidCardNumber(String input) {
    return digitsOnly(input)
        .filter(d -> CARD.matcher(d).matches())
        .filter(CreditCardValidation::passesLuhn)
        .isPresent();
  }

  static final List<String> TEST_CARDS = List.of(
      "4111111111111111", "4622943127013705", "5555555555554444", "2222420000001113",
      "2222630000001125", "378282246310005", "6011111111111117", "3566111111111113");

  public static void main(String[] args) {

    section("Quick reference");
    show("digitsOnly(\"4111 1111 1111 1111\")", digitsOnly("4111 1111 1111 1111"));
    show("CARD.matcher(\"4111111111111111\").matches()", CARD.matcher("4111111111111111").matches());
    show("brand(\"5555555555554444\")", brand("5555555555554444"));
    show("brand(\"2222420000001113\")", brand("2222420000001113"));
    show("brand(\"378282246310005\")", brand("378282246310005"));
    show("passesLuhn(\"4111111111111111\")", passesLuhn("4111111111111111"));
    show("passesLuhn(\"4111111111111112\")", passesLuhn("4111111111111112"));
    show("isValidCardNumber(\"4111-1111-1111-1111\")", isValidCardNumber("4111-1111-1111-1111"));

    section("2/3. Test cards: brand / Luhn");
    for (String c : TEST_CARDS) {
      show(c, brand(c).orElse("none") + " / " + passesLuhn(c));
    }

    for (String c : List.of("4622943127013705", "2222630000001125", "6011111111111117", "3566111111111113")) {
      show("CARD.matcher(\"" + c + "\").matches()", CARD.matcher(c).matches());
    }

    section("3. Problems in the old pattern");
    show("OLD_CARD 2222420000001113 (Mastercard 2-series)", OLD_CARD.matcher("2222420000001113").matches());
    show("CARD     2222420000001113", CARD.matcher("2222420000001113").matches());
    show("OLD_CARD 312345678901 (12 digits)", OLD_CARD.matcher("312345678901").matches());
    show("CARD     312345678901", CARD.matcher("312345678901").matches());
    show("OLD_CARD 6011111111111117", OLD_CARD.matcher("6011111111111117").matches());

    section("4. Cleaning input");
    for (String s : List.of("4111 1111 1111 1111", "4111-1111-1111-1111", " 3782 822463 10005 ",
        "4111.1111.1111.1111", "4111 1111 1111 111O")) {
      show("digitsOnly(\"" + s + "\")", digitsOnly(s));
    }

    section("5. Luhn");
    show("passesLuhn(\"378282246310005\")", passesLuhn("378282246310005"));
    show("passesLuhn(\"378282246310006\")", passesLuhn("378282246310006"));
    show("passesLuhn(\"4111111111111121\")", passesLuhn("4111111111111121"));
    show("passesLuhn(\"0000000000000000\")", passesLuhn("0000000000000000"));

    section("6. Combined check");
    for (String s : List.of("4111 1111 1111 1111", "5555-5555-5555-4444", "6011111111111117",
        "4111111111111112", "0000000000000000", "4111 1111", "")) {
      show("isValidCardNumber(\"" + s + "\")", isValidCardNumber(s));
    }
    show("passesLuhn(\"0000000000000000\")", passesLuhn("0000000000000000"));
    show("brand(\"0000000000000000\")", brand("0000000000000000"));
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-50s -> %s%n", expression, result);
  }
}

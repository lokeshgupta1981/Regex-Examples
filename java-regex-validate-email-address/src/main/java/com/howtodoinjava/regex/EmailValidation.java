package com.howtodoinjava.regex;

import java.net.IDN;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/java-regex-validate-email-address/
 */
public class EmailValidation {

  /** 1. One "@" with no whitespace around it. */
  static final Pattern AT_SIGN = Pattern.compile("^[^@\\s]+@[^@\\s]+$");

  /** 2. Same as 1, plus at least one dot in the domain. */
  static final Pattern DOT_IN_DOMAIN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  /** 3. The regex from the HTML Standard (what browsers use for input type="email"). */
  static final Pattern HTML5 = Pattern.compile(
      "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+"
      + "@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?"
      + "(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");

  /** 4. Strict: dot rules in the local part, a letter-only TLD and the RFC 5321 length limits. */
  static final Pattern STRICT = Pattern.compile(
      "^(?=.{1,254}$)(?=.{1,64}@)"
      + "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*"
      + "@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$");

  static final Predicate<String> IS_EMAIL = STRICT.asMatchPredicate();

  static final List<String> SAMPLES = List.of(
      "user@example.com",
      "first.last@example.co.uk",
      "user+news@example.com",
      "o'neil@example.org",
      "user@localhost",
      "user@example.c",
      ".user@example.com",
      "user..name@example.com",
      "user@-example.com",
      "user@example..com",
      "user@[192.0.2.1]",
      "\"john doe\"@example.com",
      "user@@example.com",
      "user name@example.com",
      "user.example.com",
      "@example.com");

  public static void main(String[] args) {

    section("Quick reference");
    show("AT_SIGN.matcher(\"user@example\").matches()", AT_SIGN.matcher("user@example").matches());
    show("DOT_IN_DOMAIN.matcher(\"user@example\").matches()", DOT_IN_DOMAIN.matcher("user@example").matches());
    show("HTML5.matcher(\"user..name@example.com\").matches()", HTML5.matcher("user..name@example.com").matches());
    show("STRICT.matcher(\"user..name@example.com\").matches()", STRICT.matcher("user..name@example.com").matches());
    show("IS_EMAIL.test(\"user@example.com\")", IS_EMAIL.test("user@example.com"));
    show("IS_EMAIL.test(\"user@example.c\")", IS_EMAIL.test("user@example.c"));

    section("Every sample against every regex");
    System.out.printf("%-26s %-8s %-14s %-6s %-6s%n", "input", "AT_SIGN", "DOT_IN_DOMAIN", "HTML5", "STRICT");
    for (String s : SAMPLES) {
      System.out.printf("%-26s %-8s %-14s %-6s %-6s%n", s,
          AT_SIGN.matcher(s).matches(), DOT_IN_DOMAIN.matcher(s).matches(),
          HTML5.matcher(s).matches(), STRICT.matcher(s).matches());
    }

    section("Length limits (RFC 5321)");
    String local64 = "a".repeat(64) + "@example.com";
    String local65 = "a".repeat(65) + "@example.com";
    String total255 = "user@" + "a".repeat(63) + "." + "b".repeat(63) + "." + "c".repeat(63) + "." + "d".repeat(54) + ".com";
    show("64-char local part, HTML5", HTML5.matcher(local64).matches());
    show("64-char local part, STRICT", STRICT.matcher(local64).matches());
    show("65-char local part, HTML5", HTML5.matcher(local65).matches());
    show("65-char local part, STRICT", STRICT.matcher(local65).matches());
    show("total length " + total255.length() + ", HTML5", HTML5.matcher(total255).matches());
    show("total length " + total255.length() + ", STRICT", STRICT.matcher(total255).matches());

    section("Normalize before validating");
    show("isValidEmail(\"  User@Example.COM \")", isValidEmail("  User@Example.COM "));
    show("normalize(\"  User@Example.COM \")", normalize("  User@Example.COM "));
    show("isValidEmail(null)", isValidEmail(null));

    section("Internationalized domain names");
    String unicodeDomain = "user@caf\u00e9.example";
    show("STRICT on user@caf\\u00e9.example", STRICT.matcher(unicodeDomain).matches());
    show("IDN.toASCII(\"caf\\u00e9.example\")", IDN.toASCII("caf\u00e9.example"));
    show("isValidEmail(\"user@caf\\u00e9.example\")", isValidEmail(unicodeDomain));
  }

  /** Trims the input, converts the domain to its ASCII (punycode) form and lowercases it. */
  static String normalize(String email) {
    String trimmed = email.strip();
    int at = trimmed.lastIndexOf('@');
    if (at < 0) {
      return trimmed;
    }
    String local = trimmed.substring(0, at);
    String domain = trimmed.substring(at + 1);
    try {
      domain = IDN.toASCII(domain);
    } catch (IllegalArgumentException e) {
      return trimmed;
    }
    return local + "@" + domain.toLowerCase();
  }

  static boolean isValidEmail(String email) {
    return email != null && IS_EMAIL.test(normalize(email));
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-55s -> %s%n", expression, result);
  }
}

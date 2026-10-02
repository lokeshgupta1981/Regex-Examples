package com.howtodoinjava.regex;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/how-to-build-regex-based-password-validator-in-java/
 */
public class PasswordValidation {

  /** Lowercase, uppercase, digit, special character, 8 to 64 characters. */
  static final Pattern STRONG = Pattern.compile(
      "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,64}$");

  /** The regex from the old version of this article, with the missing backslash in (?=.*d). */
  static final Pattern OLD = Pattern.compile("((?=.*[a-z])(?=.*d)(?=.*[@#$%])(?=.*[A-Z]).{6,16})");

  /** Same rules as STRONG, written with negated classes instead of .* inside each lookahead. */
  static final Pattern STRONG_NEGATED = Pattern.compile(
      "^(?=[^a-z]*[a-z])(?=[^A-Z]*[A-Z])(?=\\D*\\d)(?=[a-zA-Z0-9]*[^a-zA-Z0-9]).{8,64}$");

  /** Length only, following NIST SP 800-63B-4 for a password used as a single factor. */
  static final Pattern NIST_LENGTH = Pattern.compile("^.{15,64}$");

  static final List<String> SAMPLES = List.of(
      "Apple@123", "apple@123", "APPLE@123", "Apple@abc", "Apple1234", "Ap@1", "Apple @123");

  /** A tiny example blocklist; real systems use a list of breached and common passwords. */
  static final Set<String> BLOCKLIST = Set.of("password", "password123", "qwerty123", "iloveyou");

  public static void main(String[] args) {

    section("Quick reference");
    show("STRONG Apple@123", STRONG.matcher("Apple@123").matches());
    show("STRONG apple@123", STRONG.matcher("apple@123").matches());
    show("STRONG Apple1234", STRONG.matcher("Apple1234").matches());
    show("STRONG Ap@1", STRONG.matcher("Ap@1").matches());
    PasswordPolicy policy = PasswordPolicy.builder()
        .length(8, 64).requireLowercase().requireUppercase().requireDigit().requireSpecial()
        .forbidWord("lokesh").build();
    show("policy.violations(\"apple123\")", policy.violations("apple123"));
    show("NIST_LENGTH \"green apple tree\"", NIST_LENGTH.matcher("green apple tree").matches());

    section("STRONG on every sample");
    for (String s : SAMPLES) {
      show("STRONG \"" + s + "\"", STRONG.matcher(s).matches());
    }

    section("Adding one lookahead at a time");
    String[] steps = {
        "^.{8,64}$",
        "^(?=.*[a-z]).{8,64}$",
        "^(?=.*[a-z])(?=.*[A-Z]).{8,64}$",
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,64}$",
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,64}$"};
    for (String regex : steps) {
      Pattern p = Pattern.compile(regex);
      StringBuilder row = new StringBuilder();
      for (String s : SAMPLES) {
        row.append(p.matcher(s).matches() ? "T " : "F ");
      }
      System.out.printf("%-58s %s%n", regex, row);
    }

    section("Lookaheads are zero-width");
    Matcher m = Pattern.compile("(?=.*\\d)").matcher("abc1");
    show("find()", m.find());
    show("group()", "\"" + m.group() + "\"");
    show("start()", m.start());
    show("\"abc1\".matches(\"(?=.*\\\\d)\")", "abc1".matches("(?=.*\\d)"));
    show("\"abc1\".matches(\"(?=.*\\\\d).*\")", "abc1".matches("(?=.*\\d).*"));
    show("\"abc1\".matches(\"(?=\\\\d).*\")", "abc1".matches("(?=\\d).*"));

    section("The old regex: (?=.*d) means the letter d");
    show("OLD Password@ (no digit)", OLD.matcher("Password@").matches());
    show("OLD Apple@123 (digits, no letter d)", OLD.matcher("Apple@123").matches());

    section("Negative lookaheads");
    show("\"Apple @123\".matches(\"^(?!.*\\\\s).+$\")", "Apple @123".matches("^(?!.*\\s).+$"));
    show("\"Apple@123\".matches(\"^(?!.*\\\\s).+$\")", "Apple@123".matches("^(?!.*\\s).+$"));
    show("\"Appple@123\".matches(\"^(?!.*(.)\\\\1\\\\1).+$\")", "Appple@123".matches("^(?!.*(.)\\1\\1).+$"));
    show("\"Apple@123\".matches(\"^(?!.*(.)\\\\1\\\\1).+$\")", "Apple@123".matches("^(?!.*(.)\\1\\1).+$"));
    String user = "lokesh";
    Pattern noUser = Pattern.compile("^(?!.*(?i:" + Pattern.quote(user) + ")).+$");
    show("noUser.pattern()", noUser.pattern());
    show("noUser \"Lokesh@2026\"", noUser.matcher("Lokesh@2026").matches());
    show("noUser \"Apple@2026\"", noUser.matcher("Apple@2026").matches());

    section("Negated classes give the same results");
    for (String s : SAMPLES) {
      show("STRONG_NEGATED \"" + s + "\"", STRONG_NEGATED.matcher(s).matches());
    }

    section("Configurable policy");
    show("policy.regex()", policy.regex());
    for (String s : List.of("Apple@123", "apple123", "Ap@1", "Lokesh@123")) {
      show("isValid(\"" + s + "\")", policy.isValid(s));
      show("violations(\"" + s + "\")", policy.violations(s));
    }

    section("Length counts code points");
    String emoji = new String(Character.toChars(0x1F600));
    show("emoji.length()", emoji.length());
    show("emoji.codePointCount(0, emoji.length())", emoji.codePointCount(0, emoji.length()));
    show("emoji.matches(\".\")", emoji.matches("."));

    section("NIST SP 800-63B-4 style check");
    for (String s : List.of("green apple tree", "apple tree", "password123", "PASSWORD123", "lokesh likes apples",
        "Apple@123")) {
      show("nistViolations(\"" + s + "\", \"lokesh\")", nistViolations(s, "lokesh"));
    }
  }

  /** Length 15-64 code points, not on the blocklist, does not contain the user name. No composition rules. */
  static List<String> nistViolations(String password, String username) {
    var failed = new java.util.ArrayList<String>();
    int length = password.codePointCount(0, password.length());
    if (length < 15 || length > 64) {
      failed.add("must be 15 to 64 characters long");
    }
    if (BLOCKLIST.contains(password.toLowerCase(Locale.ROOT))) {
      failed.add("is a commonly used password");
    }
    if (password.toLowerCase(Locale.ROOT).contains(username.toLowerCase(Locale.ROOT))) {
      failed.add("must not contain the user name");
    }
    return failed;
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-48s -> %s%n", expression, result);
  }
}

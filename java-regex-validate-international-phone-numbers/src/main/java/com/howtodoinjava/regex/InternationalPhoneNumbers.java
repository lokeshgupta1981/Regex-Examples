package com.howtodoinjava.regex;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/java-regex-validate-international-phone-numbers/
 */
public class InternationalPhoneNumbers {

  /** E.164: a plus sign, a first digit 1-9, then up to 14 more digits (15 in total). */
  static final Pattern E164 = Pattern.compile("^\\+[1-9]\\d{1,14}$");

  /** E.123 international notation: digit groups separated by single spaces, 7 to 15 digits. */
  static final Pattern E123 = Pattern.compile("^\\+[1-9](?: ?\\d){6,14}$");

  /** EPP (RFC 5733) style: +country code, a dot, the number, and an optional xNNNN extension. */
  static final Pattern EPP = Pattern.compile("^\\+(?<cc>[1-9]\\d{0,2})\\.(?<number>\\d{4,14})(?:x(?<ext>\\d{1,6}))?$");

  /** Characters people type between digits. */
  static final Pattern SEPARATORS = Pattern.compile("[\\s().-]");

  static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

  public static void main(String[] args) {

    section("Quick reference");
    show("E164: +442079460958", E164.matcher("+442079460958").matches());
    show("E164: +44 20 7946 0958", E164.matcher("+44 20 7946 0958").matches());
    show("E123: +44 20 7946 0958", E123.matcher("+44 20 7946 0958").matches());
    show("toE164(\"+44 (20) 7946-0958\")", toE164("+44 (20) 7946-0958"));
    show("EPP: +44.2079460958x123", EPP.matcher("+44.2079460958x123").matches());
    show("isValid(\"+44 20 7946 0958\")", isValid("+44 20 7946 0958"));
    show("isValid(\"+44 20 7946 095\")", isValid("+44 20 7946 095"));

    section("E.164 regex");
    for (String s : List.of("+442079460958", "+12025550123", "+61255501234", "+1234567890123456",
        "+0442079460958", "442079460958", "+44 20 7946 0958", "+44-20-7946-0958", "+12")) {
      show("E164 " + s, E164.matcher(s).matches());
    }

    section("E.123 regex (spaces allowed)");
    for (String s : List.of("+44 20 7946 0958", "+1 202 555 0123", "+442079460958", "+44  20 7946 0958",
        "+44 20 7946 0958 ", "+44-20-7946-0958", "+12 345")) {
      show("E123 \"" + s + "\"", E123.matcher(s).matches());
    }

    section("Normalize to E.164");
    for (String s : List.of("+44 (20) 7946-0958", "+1.202.555.0123", "+1 (202) 555-0123", "0044 20 7946 0958",
        "+44 20 7946 0958 ext 12")) {
      show("toE164(\"" + s + "\")", toE164(s));
    }

    section("EPP format with named groups");
    Matcher m = EPP.matcher("+44.2079460958x123");
    show("matches()", m.matches());
    show("group(\"cc\")", m.group("cc"));
    show("group(\"number\")", m.group("number"));
    show("group(\"ext\")", m.group("ext"));
    show("EPP: +1.2025550123", EPP.matcher("+1.2025550123").matches());
    show("EPP: +1 202 555 0123", EPP.matcher("+1 202 555 0123").matches());
    show("EPP: +1234.2025550123", EPP.matcher("+1234.2025550123").matches());

    section("What the regex cannot know");
    show("E164: +999123456789 (unassigned country code)", E164.matcher("+999123456789").matches());
    show("isValid(\"+999123456789\")", isValid("+999123456789"));
    show("E164: +4420794609 (too short for the UK)", E164.matcher("+4420794609").matches());
    show("isValid(\"+4420794609\")", isValid("+4420794609"));

    section("libphonenumber");
    try {
      PhoneNumber n = PHONE_UTIL.parse("+44 20 7946 0958", null);
      show("getCountryCode()", n.getCountryCode());
      show("getNationalNumber()", n.getNationalNumber());
      show("isValidNumber()", PHONE_UTIL.isValidNumber(n));
      show("getRegionCodeForNumber()", PHONE_UTIL.getRegionCodeForNumber(n));
      show("getNumberType()", PHONE_UTIL.getNumberType(n));
      show("format(E164)", PHONE_UTIL.format(n, PhoneNumberFormat.E164));
      show("format(INTERNATIONAL)", PHONE_UTIL.format(n, PhoneNumberFormat.INTERNATIONAL));
      show("format(NATIONAL)", PHONE_UTIL.format(n, PhoneNumberFormat.NATIONAL));

      PhoneNumber local = PHONE_UTIL.parse("020 7946 0958", "GB");
      show("parse(\"020 7946 0958\", \"GB\") as E164", PHONE_UTIL.format(local, PhoneNumberFormat.E164));
      PhoneNumber us = PHONE_UTIL.parse("(202) 555-0123", "US");
      show("parse(\"(202) 555-0123\", \"US\") as E164", PHONE_UTIL.format(us, PhoneNumberFormat.E164));
      show("isValidNumber(us)", PHONE_UTIL.isValidNumber(us));
    } catch (NumberParseException e) {
      show("NumberParseException", e.getMessage());
    }
    try {
      PHONE_UTIL.parse("hello", null);
    } catch (NumberParseException e) {
      show("parse(\"hello\", null)", e.getErrorType() + ": " + e.getMessage());
    }
  }

  /** Removes spaces, dots, dashes and parentheses, turns a leading 00 into +, then checks E.164. */
  static String toE164(String input) {
    String digits = SEPARATORS.matcher(input).replaceAll("");
    if (digits.startsWith("00")) {
      digits = "+" + digits.substring(2);
    }
    return E164.matcher(digits).matches() ? digits : null;
  }

  /** Full validation with libphonenumber: country code, length and number ranges. */
  static boolean isValid(String input) {
    try {
      return PHONE_UTIL.isValidNumber(PHONE_UTIL.parse(input, null));
    } catch (NumberParseException e) {
      return false;
    }
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-50s -> %s%n", expression, result);
  }
}

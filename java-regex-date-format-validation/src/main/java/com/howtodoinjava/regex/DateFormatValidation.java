package com.howtodoinjava.regex;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/java-regex-date-format-validation/
 */
public class DateFormatValidation {

  /** Shape only: 4 digits, hyphen, 2 digits, hyphen, 2 digits. */
  static final Pattern ISO_SHAPE = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

  /** ISO yyyy-MM-dd with month 01-12 and day 01-31, named groups. */
  static final Pattern ISO_DATE = Pattern.compile(
      "^(?<year>\\d{4})-(?<month>0[1-9]|1[0-2])-(?<day>0[1-9]|[12]\\d|3[01])$");

  /** US style MM/dd/yyyy with leading zeros. */
  static final Pattern US_DATE = Pattern.compile("^(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])/\\d{4}$");

  /** European style dd/MM/yyyy with leading zeros. */
  static final Pattern EU_DATE = Pattern.compile("^(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[0-2])/\\d{4}$");

  /** US style with optional leading zeros: M/d/yyyy. */
  static final Pattern US_DATE_FLEX = Pattern.compile("^(0?[1-9]|1[0-2])/(0?[1-9]|[12]\\d|3[01])/\\d{4}$");

  /** Month lengths without leap years: still accepts Feb 29 in every year. */
  static final Pattern ISO_MONTH_LENGTHS = Pattern.compile(
      "^\\d{4}-(?:(?:0[13578]|1[02])-(?:0[1-9]|[12]\\d|3[01])"
      + "|(?:0[469]|11)-(?:0[1-9]|[12]\\d|30)"
      + "|02-(?:0[1-9]|1\\d|2\\d))$");

  /** Finds yyyy-MM-dd dates inside text. */
  static final Pattern ISO_IN_TEXT = Pattern.compile("\\b\\d{4}-\\d{2}-\\d{2}\\b");

  static final DateTimeFormatter ISO_STRICT =
      DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
  static final DateTimeFormatter US_STRICT =
      DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);
  static final DateTimeFormatter US_FLEX_STRICT =
      DateTimeFormatter.ofPattern("M/d/uuuu").withResolverStyle(ResolverStyle.STRICT);

  public static void main(String[] args) {

    section("Quick reference");
    show("ISO_DATE: 2026-01-15", ISO_DATE.matcher("2026-01-15").matches());
    show("ISO_DATE: 2026-13-01", ISO_DATE.matcher("2026-13-01").matches());
    show("ISO_DATE: 2026-02-30", ISO_DATE.matcher("2026-02-30").matches());
    show("isValidIsoDate(\"2026-02-30\")", isValidIsoDate("2026-02-30"));
    show("isValidIsoDate(\"2028-02-29\")", isValidIsoDate("2028-02-29"));
    show("US_DATE: 01/15/2026", US_DATE.matcher("01/15/2026").matches());
    show("EU_DATE: 15/01/2026", EU_DATE.matcher("15/01/2026").matches());

    section("Shape only");
    for (String s : List.of("2026-01-15", "2026-99-99", "2026-1-15", "26-01-15", "2026/01/15")) {
      show("ISO_SHAPE " + s, ISO_SHAPE.matcher(s).matches());
    }

    section("ISO yyyy-MM-dd with ranges");
    for (String s : List.of("2026-01-15", "2026-12-31", "2026-00-10", "2026-13-01", "2026-01-32",
        "2026-01-00", "2026-02-30", "2026-04-31", "2027-02-29")) {
      show("ISO_DATE " + s, ISO_DATE.matcher(s).matches());
    }
    Matcher m = ISO_DATE.matcher("2026-01-15");
    m.matches();
    show("group(\"year\")", m.group("year"));
    show("group(\"month\")", m.group("month"));
    show("group(\"day\")", m.group("day"));

    section("US and European formats");
    for (String s : List.of("01/15/2026", "15/01/2026", "03/04/2026", "1/5/2026", "02/30/2026")) {
      show(s + "  US_DATE / EU_DATE",
          US_DATE.matcher(s).matches() + " / " + EU_DATE.matcher(s).matches());
    }
    for (String s : List.of("1/5/2026", "01/05/2026", "12/31/2026", "13/1/2026", "001/5/2026")) {
      show("US_DATE_FLEX " + s, US_DATE_FLEX.matcher(s).matches());
    }

    section("Month lengths in a regex");
    for (String s : List.of("2026-04-30", "2026-04-31", "2026-02-28", "2026-02-29", "2026-02-30")) {
      show("ISO_MONTH_LENGTHS " + s, ISO_MONTH_LENGTHS.matcher(s).matches());
    }

    section("LocalDate.parse and resolver styles");
    parse("SMART (ofPattern default): 2026-02-30", "2026-02-30", DateTimeFormatter.ofPattern("uuuu-MM-dd"));
    parse("SMART (ofPattern default): 2026-04-31", "2026-04-31", DateTimeFormatter.ofPattern("uuuu-MM-dd"));
    parse("LENIENT: 2026-02-30", "2026-02-30",
        DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.LENIENT));
    parse("STRICT: 2026-02-30", "2026-02-30", ISO_STRICT);
    parse("STRICT: 2026-04-31", "2026-04-31", ISO_STRICT);
    parse("STRICT: 2028-02-29", "2028-02-29", ISO_STRICT);
    parse("STRICT: 2027-02-29", "2027-02-29", ISO_STRICT);
    parse("STRICT: 2100-02-29", "2100-02-29", ISO_STRICT);
    parse("STRICT: 2000-02-29", "2000-02-29", ISO_STRICT);
    parse("STRICT yyyy (not uuuu): 2026-01-15", "2026-01-15",
        DateTimeFormatter.ofPattern("yyyy-MM-dd").withResolverStyle(ResolverStyle.STRICT));
    parse("ISO_LOCAL_DATE: 2026-02-30", "2026-02-30", DateTimeFormatter.ISO_LOCAL_DATE);
    show("ISO_LOCAL_DATE.getResolverStyle()", DateTimeFormatter.ISO_LOCAL_DATE.getResolverStyle());
    parse("US_STRICT: 02/30/2026", "02/30/2026", US_STRICT);
    parse("US_STRICT: 2/3/2026", "2/3/2026", US_STRICT);
    parse("US_FLEX_STRICT: 2/3/2026", "2/3/2026", US_FLEX_STRICT);
    parse("US_FLEX_STRICT: 02/03/2026", "02/03/2026", US_FLEX_STRICT);

    section("Regex plus LocalDate");
    for (String s : List.of("2026-01-15", "2026-02-30", "2028-02-29", "2026-1-15", " 2026-01-15", "hello")) {
      show("isValidIsoDate(\"" + s + "\")", isValidIsoDate(s));
    }
    show("toLocalDate(\"2026-01-15\")", toLocalDate("2026-01-15"));
    show("toLocalDate(\"2026-02-30\")", toLocalDate("2026-02-30"));

    section("Dates inside text");
    String text = "Shipped 2026-01-15, due 2026-02-30, paid 2026-02-01.";
    show("ISO_IN_TEXT results", ISO_IN_TEXT.matcher(text).results().map(MatchResult::group).toList());
    show("real dates only", ISO_IN_TEXT.matcher(text).results().map(MatchResult::group)
        .filter(DateFormatValidation::isValidIsoDate).toList());
  }

  /** Regex for the exact shape, then LocalDate with STRICT for a real calendar date. */
  static boolean isValidIsoDate(String input) {
    if (input == null || !ISO_DATE.matcher(input).matches()) {
      return false;
    }
    try {
      LocalDate.parse(input, ISO_STRICT);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  static Optional<LocalDate> toLocalDate(String input) {
    try {
      return Optional.of(LocalDate.parse(input, ISO_STRICT));
    } catch (DateTimeParseException e) {
      return Optional.empty();
    }
  }

  static void parse(String label, String input, DateTimeFormatter formatter) {
    try {
      show(label, LocalDate.parse(input, formatter));
    } catch (DateTimeParseException e) {
      show(label, "DateTimeParseException: " + e.getMessage());
    }
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-40s -> %s%n", expression, result);
  }
}

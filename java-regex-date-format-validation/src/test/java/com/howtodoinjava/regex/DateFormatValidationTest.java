package com.howtodoinjava.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DateFormatValidationTest {

  @ParameterizedTest(name = "ISO_SHAPE {0} -> {1}")
  @CsvSource({"2026-01-15, true", "2026-99-99, true", "2026-1-15, false", "26-01-15, false", "2026/01/15, false"})
  void isoShape(String input, boolean expected) {
    assertEquals(expected, DateFormatValidation.ISO_SHAPE.matcher(input).matches());
  }

  @ParameterizedTest(name = "ISO_DATE {0} -> {1}")
  @CsvSource({"2026-01-15, true", "2026-12-31, true", "2026-00-10, false", "2026-13-01, false",
      "2026-01-32, false", "2026-01-00, false", "2026-02-30, true", "2026-04-31, true", "2027-02-29, true"})
  void isoDateRegex(String input, boolean expected) {
    assertEquals(expected, DateFormatValidation.ISO_DATE.matcher(input).matches());
  }

  @Test
  void namedGroups() {
    Matcher m = DateFormatValidation.ISO_DATE.matcher("2026-01-15");
    assertTrue(m.matches());
    assertEquals("2026", m.group("year"));
    assertEquals("01", m.group("month"));
    assertEquals("15", m.group("day"));
  }

  @ParameterizedTest(name = "{0}: US {1}, EU {2}")
  @CsvSource({"01/15/2026, true, false", "15/01/2026, false, true", "03/04/2026, true, true",
      "1/5/2026, false, false", "02/30/2026, true, false"})
  void usAndEuropean(String input, boolean us, boolean eu) {
    assertEquals(us, DateFormatValidation.US_DATE.matcher(input).matches());
    assertEquals(eu, DateFormatValidation.EU_DATE.matcher(input).matches());
  }

  @ParameterizedTest(name = "US_DATE_FLEX {0} -> {1}")
  @CsvSource({"1/5/2026, true", "01/05/2026, true", "12/31/2026, true", "13/1/2026, false", "001/5/2026, false"})
  void usFlexible(String input, boolean expected) {
    assertEquals(expected, DateFormatValidation.US_DATE_FLEX.matcher(input).matches());
  }

  @ParameterizedTest(name = "ISO_MONTH_LENGTHS {0} -> {1}")
  @CsvSource({"2026-04-30, true", "2026-04-31, false", "2026-02-28, true", "2026-02-29, true", "2026-02-30, false"})
  void monthLengths(String input, boolean expected) {
    assertEquals(expected, DateFormatValidation.ISO_MONTH_LENGTHS.matcher(input).matches());
  }

  @Test
  void resolverStyles() {
    DateTimeFormatter smart = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    assertEquals(ResolverStyle.SMART, smart.getResolverStyle());
    assertEquals(LocalDate.of(2026, 2, 28), LocalDate.parse("2026-02-30", smart));
    assertEquals(LocalDate.of(2026, 4, 30), LocalDate.parse("2026-04-31", smart));
    assertEquals(LocalDate.of(2026, 3, 2), LocalDate.parse("2026-02-30", smart.withResolverStyle(ResolverStyle.LENIENT)));

    DateTimeFormatter strict = DateFormatValidation.ISO_STRICT;
    DateTimeParseException e = assertThrows(DateTimeParseException.class, () -> LocalDate.parse("2026-02-30", strict));
    assertEquals("Text '2026-02-30' could not be parsed: Invalid date 'FEBRUARY 30'", e.getMessage());
    assertThrows(DateTimeParseException.class, () -> LocalDate.parse("2026-04-31", strict));
    assertEquals(LocalDate.of(2028, 2, 29), LocalDate.parse("2028-02-29", strict));
    assertEquals(LocalDate.of(2000, 2, 29), LocalDate.parse("2000-02-29", strict));
    e = assertThrows(DateTimeParseException.class, () -> LocalDate.parse("2027-02-29", strict));
    assertEquals("Text '2027-02-29' could not be parsed: Invalid date 'February 29' as '2027' is not a leap year",
        e.getMessage());
    assertThrows(DateTimeParseException.class, () -> LocalDate.parse("2100-02-29", strict));
  }

  @Test
  void yyyyWithStrictFailsEvenForValidDates() {
    DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    DateTimeParseException e = assertThrows(DateTimeParseException.class, () -> LocalDate.parse("2026-01-15", f));
    assertTrue(e.getMessage().contains("Unable to obtain LocalDate from TemporalAccessor"));
  }

  @Test
  void isoLocalDateIsAlreadyStrict() {
    assertEquals(ResolverStyle.STRICT, DateTimeFormatter.ISO_LOCAL_DATE.getResolverStyle());
    assertThrows(DateTimeParseException.class, () -> LocalDate.parse("2026-02-30"));
  }

  @Test
  void usFormatters() {
    assertThrows(DateTimeParseException.class, () -> LocalDate.parse("02/30/2026", DateFormatValidation.US_STRICT));
    DateTimeParseException e = assertThrows(DateTimeParseException.class,
        () -> LocalDate.parse("2/3/2026", DateFormatValidation.US_STRICT));
    assertEquals("Text '2/3/2026' could not be parsed at index 0", e.getMessage());
    assertEquals(LocalDate.of(2026, 2, 3), LocalDate.parse("2/3/2026", DateFormatValidation.US_FLEX_STRICT));
    assertEquals(LocalDate.of(2026, 2, 3), LocalDate.parse("02/03/2026", DateFormatValidation.US_FLEX_STRICT));
  }

  @ParameterizedTest(name = "isValidIsoDate [{0}] -> {1}")
  @CsvSource(ignoreLeadingAndTrailingWhitespace = false, value = {
      "2026-01-15,true", "2026-02-30,false", "2028-02-29,true", "2026-1-15,false", " 2026-01-15,false", "hello,false"})
  void regexPlusLocalDate(String input, boolean expected) {
    assertEquals(expected, DateFormatValidation.isValidIsoDate(input));
  }

  @Test
  void nullIsInvalidAndOptionalHelper() {
    assertFalse(DateFormatValidation.isValidIsoDate(null));
    assertEquals(Optional.of(LocalDate.of(2026, 1, 15)), DateFormatValidation.toLocalDate("2026-01-15"));
    assertEquals(Optional.empty(), DateFormatValidation.toLocalDate("2026-02-30"));
  }

  @Test
  void datesInsideText() {
    String text = "Shipped 2026-01-15, due 2026-02-30, paid 2026-02-01.";
    assertEquals(List.of("2026-01-15", "2026-02-30", "2026-02-01"),
        DateFormatValidation.ISO_IN_TEXT.matcher(text).results().map(MatchResult::group).toList());
    assertEquals(List.of("2026-01-15", "2026-02-01"),
        DateFormatValidation.ISO_IN_TEXT.matcher(text).results().map(MatchResult::group)
            .filter(DateFormatValidation::isValidIsoDate).toList());
  }
}

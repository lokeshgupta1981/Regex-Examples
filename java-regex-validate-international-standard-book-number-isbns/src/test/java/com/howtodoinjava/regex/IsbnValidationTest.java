package com.howtodoinjava.regex;

import static com.howtodoinjava.regex.IsbnValidation.ISBN10;
import static com.howtodoinjava.regex.IsbnValidation.ISBN10_FORMATTED;
import static com.howtodoinjava.regex.IsbnValidation.ISBN13;
import static com.howtodoinjava.regex.IsbnValidation.ISBN13_FORMATTED;
import static com.howtodoinjava.regex.IsbnValidation.ISBN_FORMATTED;
import static com.howtodoinjava.regex.IsbnValidation.compact;
import static com.howtodoinjava.regex.IsbnValidation.isValidIsbn;
import static com.howtodoinjava.regex.IsbnValidation.isValidIsbn10;
import static com.howtodoinjava.regex.IsbnValidation.isValidIsbn13;
import static com.howtodoinjava.regex.IsbnValidation.isbn10CheckDigit;
import static com.howtodoinjava.regex.IsbnValidation.isbn13CheckDigit;
import static com.howtodoinjava.regex.IsbnValidation.toIsbn10;
import static com.howtodoinjava.regex.IsbnValidation.toIsbn13;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class IsbnValidationTest {

  @Test
  void compactRemovesLabelAndSeparators() {
    assertEquals("9789295055124", compact("ISBN 978-92-95055-12-4"));
    assertEquals("9789295055124", compact("ISBN-13: 978 92 95055 12 4"));
    assertEquals("9789295055124", compact("isbn 978-92-95055-12-4"));
    assertEquals("0306406152", compact("ISBN-10 0-306-40615-2"));
    assertEquals("0306406152", compact("0-306-40615-2"));
    assertEquals("123456789X", compact("12345-6789-x"));
  }

  @Test
  void compactFormRegexes() {
    assertTrue(ISBN13.matcher("9789295055124").matches());
    assertTrue(ISBN13.matcher("9799295055124").matches());
    assertFalse(ISBN13.matcher("9779295055124").matches());
    assertTrue(ISBN10.matcher("123456789X").matches());
    assertFalse(ISBN10.matcher("X123456789").matches());
  }

  @Test
  void isbn10CheckDigitRule() {
    assertTrue(isValidIsbn10("0306406152"));
    assertFalse(isValidIsbn10("0306406153"));
    assertTrue(isValidIsbn10("123456789X"));
    assertFalse(isValidIsbn10("1234567890"));
    assertEquals('2', isbn10CheckDigit("030640615"));
    assertEquals('X', isbn10CheckDigit("123456789"));
  }

  @Test
  void isbn13CheckDigitRule() {
    assertEquals(4, isbn13CheckDigit("978929505512"));
    assertTrue(isValidIsbn13("9789295055124"));
    assertFalse(isValidIsbn13("9789295055125"));
    assertTrue(isValidIsbn13("9780306406157"));
    assertFalse(isValidIsbn13("9789295055142"));
    assertTrue(isValidIsbn13("9789290555124"));
    assertFalse(isValidIsbn10("0306406125"));
    assertEquals(6, isbn13CheckDigit("979123456789"));
  }

  @ParameterizedTest
  @CsvSource(delimiter = '|', value = {
      "ISBN 978-92-95055-12-4     | true  | true",
      "ISBN-13: 978-92-95055-12-4 | true  | true",
      "978 92 95055 12 4          | true  | true",
      "9789295055124              | true  | true",
      "ISBN-10 0-306-40615-2      | true  | true",
      "ISBN-10: 0-306-40615-2     | true  | true",
      "0 306 40615 2              | true  | true",
      "0306406152                 | true  | true",
      "978-92-95055-12-5          | true  | false",
      "0-306-40615-3              | true  | false",
      "ISBN-13 0-306-40615-2      | true  | true",
      "ISBN-12: 978-92-95055-12-4 | false | false",
      "0-3061-40615-2             | false | false",
      "978-92-95055124            | false | true",
      "97892-95055-12-4-1         | false | false",
      "ISBN 978--92-95055-12-4    | false | true"})
  void formattedRegexVersusCompactCheck(String input, boolean regex, boolean valid) {
    assertEquals(regex, ISBN_FORMATTED.matcher(input).matches(), input);
    assertEquals(valid, isValidIsbn(input), input);
  }

  @Test
  void separateFormattedRegexes() {
    assertTrue(ISBN10_FORMATTED.matcher("0-306-40615-2").matches());
    assertFalse(ISBN10_FORMATTED.matcher("ISBN-13 0-306-40615-2").matches());
    assertTrue(ISBN13_FORMATTED.matcher("ISBN 978-92-95055-12-4").matches());
    assertFalse(ISBN13_FORMATTED.matcher("978 10 595 05512 4").matches());
  }

  @Test
  void quickReference() {
    assertTrue(isValidIsbn("ISBN 978-92-95055-12-4"));
    assertFalse(isValidIsbn("978-92-95055-12-5"));
    assertTrue(isValidIsbn("ISBN-10: 0-306-40615-2"));
    assertTrue(isValidIsbn("123456789X"));
    assertFalse(isValidIsbn(null));
    assertFalse(isValidIsbn(""));
  }

  @Test
  void conversion() {
    assertEquals(Optional.of("9780306406157"), toIsbn13("0-306-40615-2"));
    assertEquals(Optional.of("9781234567897"), toIsbn13("123456789X"));
    assertEquals(Optional.empty(), toIsbn13("92-95055-12-X"));
    assertEquals(Optional.of("9295055128"), toIsbn10("978-92-95055-12-4"));
    assertEquals(Optional.of("0306406152"), toIsbn10("9780306406157"));
    assertEquals(Optional.empty(), toIsbn10("979-12-34567-89-6"));
    assertTrue(isValidIsbn("979-12-34567-89-6"));
  }

  @Test
  void everySingleDigitErrorIsDetected() {
    String isbn = "9789295055124";
    for (int pos = 0; pos < 13; pos++) {
      for (char d = '0'; d <= '9'; d++) {
        if (d == isbn.charAt(pos) || (pos < 3)) {
          continue;
        }
        String changed = isbn.substring(0, pos) + d + isbn.substring(pos + 1);
        assertFalse(isValidIsbn13(changed), changed);
      }
    }
  }
}

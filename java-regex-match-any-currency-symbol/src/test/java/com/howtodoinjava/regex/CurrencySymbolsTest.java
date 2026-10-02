package com.howtodoinjava.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.Test;

class CurrencySymbolsTest {

  static final String PRICES = "Apple $5, Banana \u20ac3, Cherry \u00a5200";

  @Test
  void quickReference() {
    assertTrue("$".matches("\\p{Sc}"));
    assertTrue("\u20ac".matches("\\p{Sc}"));
    assertFalse("%".matches("\\p{Sc}"));
    assertEquals(List.of("$", "\u20ac", "\u00a5"), CurrencySymbols.symbols(PRICES));
    assertFalse(CurrencySymbols.CURRENCY.matcher("hello").find());
    assertEquals("Apple 5, Banana 3, Cherry 200", PRICES.replaceAll("\\p{Sc}", ""));
    assertTrue(Character.getType('\u20ac') == Character.CURRENCY_SYMBOL);
  }

  @Test
  void positions() {
    assertEquals(List.of(6, 17, 28),
        CurrencySymbols.CURRENCY.matcher(PRICES).results().map(MatchResult::start).toList());
  }

  @Test
  void membership() {
    for (int c : new int[] {'$', 0xA2, 0xA3, 0xA5, 0x20AC, 0x20B9, 0x20BD, 0x20BF, 0x0E3F, 0xFDFC, 0xFF04}) {
      assertTrue(new String(Character.toChars(c)).matches("\\p{Sc}"), Integer.toHexString(c));
    }
    assertFalse("%".matches("\\p{Sc}"));
    assertFalse("#".matches("\\p{Sc}"));
    assertFalse("Rs".matches("\\p{Sc}+"));
    assertFalse("CHF".matches("\\p{Sc}+"));
    assertEquals(63, CurrencySymbols.countAllCurrencySymbols());
    assertEquals(Character.CURRENCY_SYMBOL, Character.getType(0x20C0));
    assertEquals(Character.UNASSIGNED, Character.getType(0x20C1));
  }

  @Test
  void regexAgreesWithCharacterGetType() {
    for (int c = 0; c <= Character.MAX_CODE_POINT; c++) {
      boolean expected = Character.getType(c) == Character.CURRENCY_SYMBOL;
      assertEquals(expected, CurrencySymbols.CURRENCY.matcher(new String(Character.toChars(c))).matches());
    }
  }

  @Test
  void propertySpellings() {
    for (String ok : List.of("\\p{Sc}", "\\p{IsSc}", "\\p{gc=Sc}", "\\p{general_category=Sc}", "\\p{S}",
        "\\p{InCurrencySymbols}")) {
      assertTrue(Pattern.compile(ok).matcher("\u20ac").matches(), ok);
    }
    PatternSyntaxException e = assertThrows(PatternSyntaxException.class, () -> Pattern.compile("\\p{Currency_Symbol}"));
    assertEquals("Unknown character property name {Currency_Symbol}", e.getDescription());
    assertFalse("$".matches("\\p{InCurrencySymbols}"));
    assertTrue("+".matches("\\p{S}"));
  }

  @Test
  void amounts() {
    String bill = "Pay $1,250.00 or 99,90\u20ac or \u00a5 500 today";
    assertEquals(List.of("$ | 1,250.00", "\u00a5 | 500"), CurrencySymbols.SYMBOL_FIRST.matcher(bill).results()
        .map(r -> r.group("symbol") + " | " + r.group("amount")).toList());
    assertEquals(List.of("99,90 | \u20ac"), CurrencySymbols.SYMBOL_LAST.matcher(bill).results()
        .map(r -> r.group("amount") + " | " + r.group("symbol")).toList());
    Matcher m = CurrencySymbols.SYMBOL_FIRST.matcher("Total: $49.99");
    assertTrue(m.find());
    assertEquals("$", m.group("symbol"));
    assertEquals("49.99", m.group("amount"));
  }

  @Test
  void dollarSignInPatternsAndReplacements() {
    assertFalse("cost $5".matches(".*$5"));
    assertTrue("cost $5".matches(".*\\$5"));
    assertTrue("cost $5".matches(".*[$]5"));
    IndexOutOfBoundsException e = assertThrows(IndexOutOfBoundsException.class,
        () -> "cost 5".replaceAll("5", "$5"));
    assertEquals("No group 5", e.getMessage());
    assertEquals("cost $5", "cost 5".replaceAll("5", Matcher.quoteReplacement("$5")));
    assertEquals("cost $5", "cost 5".replaceAll("5", "\\$5"));
  }

  @Test
  void symbolToCurrency() {
    assertEquals("$", Currency.getInstance("USD").getSymbol(Locale.US));
    assertEquals("US$", Currency.getInstance("USD").getSymbol(Locale.CANADA));
    assertEquals("$", Currency.getInstance("CAD").getSymbol(Locale.CANADA));
    assertEquals("\u20ac", Currency.getInstance("EUR").getSymbol(Locale.GERMANY));
    assertEquals("CHF", Currency.getInstance("CHF").getSymbol(Locale.of("de", "CH")));
  }
}

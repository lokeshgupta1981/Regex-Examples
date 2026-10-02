package com.howtodoinjava.regex;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Non-ASCII characters in the output are printed as Java escapes so the console encoding does not matter.
 * Source code for https://howtodoinjava.com/java/regex/java-regex-match-any-currency-symbol/
 */
public class CurrencySymbols {

  static final Pattern CURRENCY = Pattern.compile("\\p{Sc}");

  /** A currency symbol, an optional space, then an amount such as 1,250.00. */
  static final Pattern SYMBOL_FIRST =
      Pattern.compile("(?<symbol>\\p{Sc})\\s?(?<amount>\\d{1,3}(?:,\\d{3})*(?:\\.\\d{2})?)");

  /** An amount such as 99,90, an optional space, then a currency symbol. */
  static final Pattern SYMBOL_LAST =
      Pattern.compile("(?<amount>\\d+(?:,\\d{2})?)\\s?(?<symbol>\\p{Sc})");

  static List<String> symbols(String text) {
    return CURRENCY.matcher(text).results().map(r -> r.group()).toList();
  }

  static long countAllCurrencySymbols() {
    long count = 0;
    for (int c = 0; c <= Character.MAX_CODE_POINT; c++) {
      if (Character.getType(c) == Character.CURRENCY_SYMBOL) {
        count++;
      }
    }
    return count;
  }

  public static void main(String[] args) {

    String prices = "Apple $5, Banana \u20ac3, Cherry \u00a5200";

    section("Quick reference");
    show("\"$\".matches(\"\\\\p{Sc}\")", "$".matches("\\p{Sc}"));
    show("\"\\u20ac\".matches(\"\\\\p{Sc}\")", "\u20ac".matches("\\p{Sc}"));
    show("\"%\".matches(\"\\\\p{Sc}\")", "%".matches("\\p{Sc}"));
    show("symbols(prices)", symbols(prices));
    show("CURRENCY.matcher(\"hello\").find()", CURRENCY.matcher("hello").find());
    show("prices.replaceAll(\"\\\\p{Sc}\", \"\")", prices.replaceAll("\\p{Sc}", ""));
    show("Character.getType('\\u20ac') == CURRENCY_SYMBOL", Character.getType('\u20ac') == Character.CURRENCY_SYMBOL);

    section("1. Find every currency symbol with its position");
    CURRENCY.matcher(prices).results()
        .forEach(r -> show("found " + r.group() + " at", r.start()));

    section("2. Which characters are in \\p{Sc}");
    int[] samples = {'$', 0xA2, 0xA3, 0xA5, 0x20AC, 0x20B9, 0x20BD, 0x20BF, 0x0E3F, 0xFDFC, 0xFF04, '%', '#'};
    for (int c : samples) {
      show(String.format("U+%04X %s", c, Character.getName(c)), new String(Character.toChars(c)).matches("\\p{Sc}"));
    }
    show("\"Rs\".matches(\"\\\\p{Sc}+\")", "Rs".matches("\\p{Sc}+"));
    show("\"CHF\".matches(\"\\\\p{Sc}+\")", "CHF".matches("\\p{Sc}+"));
    show("currency symbols in Java 25", countAllCurrencySymbols());
    show("Character.getType(0x20C0) (som sign, Unicode 14)", Character.getType(0x20C0));
    show("Character.getType(0x20C1) (riyal sign, Unicode 17)", Character.getType(0x20C1));
    show("Character.CURRENCY_SYMBOL", (int) Character.CURRENCY_SYMBOL);

    section("3. Property name spellings");
    for (String name : List.of("\\p{Sc}", "\\p{IsSc}", "\\p{gc=Sc}", "\\p{general_category=Sc}", "\\p{S}",
        "\\p{InCurrencySymbols}", "\\p{Currency_Symbol}")) {
      try {
        show(name + " on \\u20ac", Pattern.compile(name).matcher("\u20ac").matches());
      } catch (PatternSyntaxException e) {
        show(name, "PatternSyntaxException: " + e.getDescription());
      }
    }
    show("\\p{InCurrencySymbols} on $", "$".matches("\\p{InCurrencySymbols}"));
    show("\\p{S} on +", "+".matches("\\p{S}"));

    section("4. Amounts with a currency symbol");
    String bill = "Pay $1,250.00 or 99,90\u20ac or \u00a5 500 today";
    SYMBOL_FIRST.matcher(bill).results()
        .forEach(r -> show("SYMBOL_FIRST", r.group("symbol") + " | " + r.group("amount")));
    SYMBOL_LAST.matcher(bill).results()
        .forEach(r -> show("SYMBOL_LAST", r.group("amount") + " | " + r.group("symbol")));
    Matcher m = SYMBOL_FIRST.matcher("Total: $49.99");
    if (m.find()) {
      show("symbol of \"Total: $49.99\"", m.group("symbol"));
      show("amount of \"Total: $49.99\"", m.group("amount"));
    }

    section("5. The dollar sign in patterns and replacements");
    show("\"cost $5\".matches(\".*$5\")", "cost $5".matches(".*$5"));
    show("\"cost $5\".matches(\".*\\\\$5\")", "cost $5".matches(".*\\$5"));
    show("\"cost $5\".matches(\".*[$]5\")", "cost $5".matches(".*[$]5"));
    try {
      show("\"cost 5\".replaceAll(\"5\", \"$5\")", "cost 5".replaceAll("5", "$5"));
    } catch (IndexOutOfBoundsException e) {
      show("\"cost 5\".replaceAll(\"5\", \"$5\")", e.getClass().getSimpleName() + ": " + e.getMessage());
    }
    show("\"cost 5\".replaceAll(\"5\", Matcher.quoteReplacement(\"$5\"))",
        "cost 5".replaceAll("5", Matcher.quoteReplacement("$5")));
    show("\"cost 5\".replaceAll(\"5\", \"\\\\$5\")", "cost 5".replaceAll("5", "\\$5"));

    section("6. Symbol to currency code");
    show("Currency.getInstance(\"USD\").getSymbol(Locale.US)", Currency.getInstance("USD").getSymbol(Locale.US));
    show("Currency.getInstance(\"USD\").getSymbol(Locale.CANADA)", Currency.getInstance("USD").getSymbol(Locale.CANADA));
    show("Currency.getInstance(\"CAD\").getSymbol(Locale.CANADA)", Currency.getInstance("CAD").getSymbol(Locale.CANADA));
    show("Currency.getInstance(\"EUR\").getSymbol(Locale.GERMANY)", Currency.getInstance("EUR").getSymbol(Locale.GERMANY));
    show("Currency.getInstance(\"CHF\").getSymbol(Locale.of(\"de\", \"CH\"))",
        Currency.getInstance("CHF").getSymbol(Locale.of("de", "CH")));
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-62s -> %s%n", escape(expression), escape(String.valueOf(result)));
  }

  static String escape(String s) {
    StringBuilder sb = new StringBuilder();
    s.codePoints().forEach(c -> sb.append(c > 0x7E ? String.format("\\u%04x", c) : String.valueOf((char) c)));
    return sb.toString();
  }
}

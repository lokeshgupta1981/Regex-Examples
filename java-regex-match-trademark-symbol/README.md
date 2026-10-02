# Regex for the Trademark Symbol in Java

Source code for the article [Regex for the Trademark Symbol in Java](https://howtodoinjava.com/java/regex/java-regex-match-trademark-symbol/).

## Versions

- Java 25 (LTS)
- Maven 3.9 or newer
- JUnit 6.1.3

## Run

```bash
mvn -q compile exec:java   # prints every example with its result
mvn test                   # runs the tests that check each example
```

The trademark sign and other non-ASCII symbols are printed as Java escapes (for example `\u2122`), so the output reads the same on every console.

## Files

| File | What it shows |
|------|---------------|
| `src/main/java/com/howtodoinjava/regex/TrademarkSymbol.java` | Finding U+2122, escape forms, brand names, related legal symbols, "(TM)" and HTML entity conversion, NFKC, printed with results |
| `src/test/java/com/howtodoinjava/regex/TrademarkSymbolTest.java` | Tests for each result, including the exception for `\u{2122}` |

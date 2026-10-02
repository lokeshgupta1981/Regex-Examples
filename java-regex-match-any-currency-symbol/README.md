# Regex for Currency Symbols in Java

Source code for the article [Regex for Currency Symbols in Java](https://howtodoinjava.com/java/regex/java-regex-match-any-currency-symbol/).

## Versions

- Java 25 (LTS, Unicode 16.0)
- Maven 3.9 or newer
- JUnit 6.1.3

## Run

```bash
mvn -q compile exec:java   # prints every example with its result
mvn test                   # runs the tests that check each example
```

Non-ASCII currency symbols are printed as Java escapes (for example the euro sign as `\u20ac`), so the output reads the same on every console.

## Files

| File | What it shows |
|------|---------------|
| `src/main/java/com/howtodoinjava/regex/CurrencySymbols.java` | `\p{Sc}` on sample text, category members, name spellings, amounts with named groups, dollar sign escaping and `Currency` symbols |
| `src/test/java/com/howtodoinjava/regex/CurrencySymbolsTest.java` | Tests for each result, including a check of `\p{Sc}` against `Character.getType()` for every code point |

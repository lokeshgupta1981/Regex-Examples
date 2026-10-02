# Remove Non-ASCII and Non-Printable Characters in Java

Source code for the article [Remove Non-ASCII and Non-Printable Characters in Java](https://howtodoinjava.com/java/regex/java-clean-ascii-text-non-printable-chars/).

## Versions

- Java 25 (LTS)
- Maven 3.9 or newer
- JUnit 6.1.3

## Run

```bash
mvn -q compile exec:java   # prints every example with its result
mvn test                   # runs the tests that check each example
```

The program prints control characters and non-ASCII characters as Java escapes (for example `\t` or `\u00e9`), so the output looks the same on every console.

## Files

| File | What it shows |
|------|---------------|
| `src/main/java/com/howtodoinjava/regex/CleanAsciiText.java` | Every pattern from the article printed with its result, plus the `toAscii()` and `stripAccents()` methods |
| `src/test/java/com/howtodoinjava/regex/CleanAsciiTextTest.java` | Tests for `\p{ASCII}`, `\p{Cntrl}`, `\p{Print}`, `\p{C}`, `Normalizer` and the full cleaning method |

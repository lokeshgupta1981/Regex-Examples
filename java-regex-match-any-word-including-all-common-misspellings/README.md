# Regex for Misspelled Words: Match Every Spelling in Java

Source code for the article [Regex for Misspelled Words: Match Every Spelling in Java](https://howtodoinjava.com/java/regex/java-regex-match-any-word-including-all-common-misspellings/).

## Versions

- Java 25 (LTS)
- Maven 3.9 or newer
- JUnit 6.1.3

## Run

```bash
mvn -q compile exec:java   # prints every example with its result
mvn test                   # runs the tests that check each example
```

## Files

| File | What it shows |
|------|---------------|
| `src/main/java/com/howtodoinjava/regex/Misspellings.java` | Every misspelling pattern from the article, printed with its result |
| `src/test/java/com/howtodoinjava/regex/MisspellingsTest.java` | Tests for accepted and rejected spellings, match positions, word boundaries, case, corrections, comments mode and the limits of the approach |

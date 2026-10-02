# Regex for International Phone Numbers in Java (E.164)

Source code for the article [Regex for International Phone Numbers in Java (E.164)](https://howtodoinjava.com/java/regex/java-regex-validate-international-phone-numbers/).

## Versions

- Java 25 (LTS)
- Maven 3.9 or newer
- JUnit 6.1.3
- libphonenumber 9.0.40

## Run

```bash
mvn -q compile exec:java   # prints every example with its result
mvn test                   # runs the tests that check each example
```

## Files

| File | What it shows |
|------|---------------|
| `src/main/java/com/howtodoinjava/regex/InternationalPhoneNumbers.java` | E.164, E.123 and EPP regexes, normalizing input to E.164, and parsing, validating and formatting with libphonenumber 9.0.40 |
| `src/test/java/com/howtodoinjava/regex/InternationalPhoneNumbersTest.java` | Tests for every regex sample, the normalizer, EPP named groups and the libphonenumber results |

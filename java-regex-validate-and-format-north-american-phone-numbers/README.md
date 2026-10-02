# Java Regex to Validate and Format US Phone Numbers

Source code for the article [Java Regex to Validate and Format US Phone Numbers](https://howtodoinjava.com/java/regex/java-regex-validate-and-format-north-american-phone-numbers/).

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
| `src/main/java/com/howtodoinjava/regex/NorthAmericanPhoneNumbers.java` | The basic 3-3-4 regex, the NANP regex with balanced parentheses and optional +1, formatting with replacement strings, extensions, and reformatting numbers inside text |
| `src/test/java/com/howtodoinjava/regex/NorthAmericanPhoneNumbersTest.java` | Tests for every row of the BASIC/NANP table, group values, output formats, extensions and text replacement |

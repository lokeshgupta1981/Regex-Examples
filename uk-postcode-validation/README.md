# Java Regex for UK Postcode Validation

Source code for the article [Java Regex for UK Postcode Validation](https://howtodoinjava.com/java/regex/uk-postcode-validation/).

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
| `src/main/java/com/howtodoinjava/regex/UkPostcodeValidation.java` | The practical and strict postcode patterns, normalization, named groups and the gov.uk anchor bug |
| `src/test/java/com/howtodoinjava/regex/UkPostcodeValidationTest.java` | Tests for every sample in the article, the inward-code letter rule, normalization and the anchor fix |

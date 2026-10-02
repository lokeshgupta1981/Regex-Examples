# Java Regex for US ZIP Code Validation

Source code for the article [Java Regex for US ZIP Code Validation](https://howtodoinjava.com/java/regex/us-postal-zip-code-validation/).

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
| `src/main/java/com/howtodoinjava/regex/UsZipCodeValidation.java` | The strict and lenient ZIP code patterns, normalization to the USPS form, and finding ZIP codes in text |
| `src/test/java/com/howtodoinjava/regex/UsZipCodeValidationTest.java` | Tests for the valid and invalid samples, anchors, named groups, text search and leading zeros |

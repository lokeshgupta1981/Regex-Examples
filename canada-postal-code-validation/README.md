# Java Regex for Canadian Postal Code Validation

Source code for the article [Java Regex for Canadian Postal Code Validation](https://howtodoinjava.com/java/regex/canada-postal-code-validation/).

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
| `src/main/java/com/howtodoinjava/regex/CanadaPostalCodeValidation.java` | The shape-only, character-class and lookahead patterns, normalization to the Canada Post form, FSA/LDU groups and the region lookup |
| `src/test/java/com/howtodoinjava/regex/CanadaPostalCodeValidationTest.java` | Tests for the valid and invalid samples, agreement of both patterns, normalization and region mapping |

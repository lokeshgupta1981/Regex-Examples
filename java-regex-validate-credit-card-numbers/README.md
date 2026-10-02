# Java Regex to Validate Credit Card Numbers

Source code for the article [Java Regex to Validate Credit Card Numbers](https://howtodoinjava.com/java/regex/java-regex-validate-credit-card-numbers/).

The card numbers are sandbox test numbers published in the Cybersource testing guide; they cannot be charged.

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
| `src/main/java/com/howtodoinjava/regex/CreditCardValidation.java` | The brand regex with named groups, brand detection, input cleanup, the Luhn check and the combined validator |
| `src/test/java/com/howtodoinjava/regex/CreditCardValidationTest.java` | Tests for the published test numbers, prefix boundaries, the old pattern's problems, input cleanup and Luhn |

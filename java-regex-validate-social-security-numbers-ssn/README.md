# Java Regex to Validate SSN (Social Security Number)

Source code for the article [Java Regex to Validate SSN (Social Security Number)](https://howtodoinjava.com/java/regex/java-regex-validate-social-security-numbers-ssn/).

All SSNs in this project are made-up placeholders.

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
| `src/main/java/com/howtodoinjava/regex/SsnValidation.java` | The SSN regex with the SSA rules, a plain-Java version, separator normalization with a backreference, and masking SSNs in text |
| `src/test/java/com/howtodoinjava/regex/SsnValidationTest.java` | Tests for every sample in the article, agreement of both validators on all area numbers, normalization and masking |

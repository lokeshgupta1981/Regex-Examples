# Java Email Validation Using Regex: Simple to Strict

Source code for the article [Java Email Validation Using Regex: Simple to Strict](https://howtodoinjava.com/java/regex/java-regex-validate-email-address/).

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
| `src/main/java/com/howtodoinjava/regex/EmailValidation.java` | Four email regexes, from a plain @ check to a strict pattern with length limits, run against valid and invalid samples; normalization and IDN domains |
| `src/test/java/com/howtodoinjava/regex/EmailValidationTest.java` | Tests for every sample in the accept/reject table, the length limits, normalization and IDN conversion |

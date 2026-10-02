# Java Date Format Validation: Regex and LocalDate STRICT

Source code for the article [Java Date Format Validation: Regex and LocalDate STRICT](https://howtodoinjava.com/java/regex/java-regex-date-format-validation/).

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
| `src/main/java/com/howtodoinjava/regex/DateFormatValidation.java` | Regexes for yyyy-MM-dd, MM/dd/yyyy and dd/MM/yyyy, the dates they wrongly accept, resolver styles, the yyyy versus uuuu trap, and regex plus LocalDate validation |
| `src/test/java/com/howtodoinjava/regex/DateFormatValidationTest.java` | Tests for every regex sample, each resolver style, the exception messages, leap years and date extraction from text |

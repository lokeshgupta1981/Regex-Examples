# Java Regex to Validate ISBN-10 and ISBN-13

Source code for the article [Java Regex to Validate ISBN-10 and ISBN-13](https://howtodoinjava.com/java/regex/java-regex-validate-international-standard-book-number-isbns/).

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
| `src/main/java/com/howtodoinjava/regex/IsbnValidation.java` | Compact and formatted ISBN regexes, ISBN-10 and ISBN-13 check digits, and conversion between the two forms |
| `src/test/java/com/howtodoinjava/regex/IsbnValidationTest.java` | Tests for every sample in the article, the check digit rules, the regex comparison table and the conversions |

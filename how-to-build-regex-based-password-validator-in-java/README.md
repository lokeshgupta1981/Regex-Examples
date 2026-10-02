# Java Password Validation Regex: Lookaheads Explained

Source code for the article [Java Password Validation Regex: Lookaheads Explained](https://howtodoinjava.com/java/regex/how-to-build-regex-based-password-validator-in-java/).

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
| `src/main/java/com/howtodoinjava/regex/PasswordValidation.java` | Every regex from the article: the four lookaheads step by step, zero-width behavior, negative lookaheads, the old (?=.*d) bug, the negated-class form and a NIST SP 800-63B-4 style check |
| `src/main/java/com/howtodoinjava/regex/PasswordPolicy.java` | A configurable policy: one lookahead per rule, compiled once, with an error message per failed rule |
| `src/test/java/com/howtodoinjava/regex/PasswordValidationTest.java` | Tests for each step of the regex, negative lookaheads, PasswordPolicy messages, code point length and the NIST-style check |

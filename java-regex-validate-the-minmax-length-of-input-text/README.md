# Regex Min and Max Length in Java: {min,max} Explained

Source code for the article [Regex Min and Max Length in Java: {min,max} Explained](https://howtodoinjava.com/java/regex/java-regex-validate-the-minmax-length-of-input-text/).

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
| `src/main/java/com/howtodoinjava/regex/MinMaxLength.java` | Quantifier forms, length plus character classes, line breaks and \z, a length lookahead, non-whitespace counts, chars vs code points vs graphemes, and patterns built from settings |
| `src/test/java/com/howtodoinjava/regex/MinMaxLengthTest.java` | Tests for every quantifier row, each sample input, the Unicode counts and the PatternSyntaxException details |

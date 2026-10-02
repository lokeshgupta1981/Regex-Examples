# Regex to Limit the Number of Lines in Text

Source code for the article [Regex to Limit the Number of Lines in Text](https://howtodoinjava.com/java/regex/java-regex-validate-limit-the-number-of-lines-in-text/).

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
| `src/main/java/com/howtodoinjava/regex/LimitLines.java` | Patterns for a maximum, a range and a line length limit, line counting, printed with their results |
| `src/test/java/com/howtodoinjava/regex/LimitLinesTest.java` | Tests for `\R` and `\V`, text blocks, the trailing line break and the `\r\n` backtracking case |

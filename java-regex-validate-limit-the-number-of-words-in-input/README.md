# Regex to Limit the Number of Words in Input

Source code for the article [Regex to Limit the Number of Words in Input](https://howtodoinjava.com/java/regex/java-regex-validate-limit-the-number-of-words-in-input/).

## Versions

- Java 25 (LTS)
- Maven 3.9 or newer
- JUnit 6.1.3

## Run

```bash
mvn -q compile exec:java   # prints every example with its result
mvn test                   # runs the tests that check each example
```

The program also times the word limit pattern with and without `\b` on a long input. The times differ between machines; the results (true or false) do not.

## Files

| File | What it shows |
|------|---------------|
| `src/main/java/com/howtodoinjava/regex/LimitWords.java` | Word limit patterns, Unicode and whitespace variants, and four ways to count words, printed with their results |
| `src/test/java/com/howtodoinjava/regex/LimitWordsTest.java` | Tests for each result, the `\b` and possessive fixes, and a timeout test on a long input |

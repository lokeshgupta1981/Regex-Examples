# Regex to Match Greek Characters in Java

Source code for the article [Regex to Match Greek Characters in Java](https://howtodoinjava.com/java/regex/java-regex-match-any-character-in-greek-extended-or-greek-script/).

## Versions

- Java 25 (LTS, Unicode 16.0)
- Maven 3.9 or newer
- JUnit 6.1.3

## Run

```bash
mvn -q compile exec:java   # prints every example with its result
mvn test                   # runs the tests that check each example
```

Greek characters are printed as Java escapes (for example `\u03b1` for alpha), so the output reads the same on every console.

## Files

| File | What it shows |
|------|---------------|
| `src/main/java/com/howtodoinjava/regex/GreekCharacters.java` | `\p{IsGreek}`, `\p{InGreek}` and `\p{InGreekExtended}` on sample text, property name spellings, accents and case, printed with results |
| `src/test/java/com/howtodoinjava/regex/GreekCharactersTest.java` | Tests for each result, including a walk over all code points that checks the Greek script counts |

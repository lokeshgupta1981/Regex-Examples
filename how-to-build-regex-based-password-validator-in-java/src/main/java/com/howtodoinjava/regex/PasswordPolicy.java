package com.howtodoinjava.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A configurable password policy. Each rule is one lookahead; the builder joins them into one regex
 * that is compiled once. The same rules, compiled one by one, tell the user which rules failed.
 */
public final class PasswordPolicy {

  record Rule(String message, String lookahead, Pattern pattern) {
    Rule(String message, String lookahead) {
      this(message, lookahead, Pattern.compile("^" + lookahead));
    }
  }

  private final int minLength;
  private final int maxLength;
  private final List<Rule> rules;
  private final Pattern pattern;

  private PasswordPolicy(int minLength, int maxLength, List<Rule> rules) {
    this.minLength = minLength;
    this.maxLength = maxLength;
    this.rules = List.copyOf(rules);
    StringBuilder regex = new StringBuilder("^");
    rules.forEach(r -> regex.append(r.lookahead()));
    regex.append(".{").append(minLength).append(',').append(maxLength).append("}$");
    this.pattern = Pattern.compile(regex.toString());
  }

  public static Builder builder() {
    return new Builder();
  }

  public String regex() {
    return pattern.pattern();
  }

  public boolean isValid(String password) {
    return password != null && pattern.matcher(password).matches();
  }

  /** Returns one message per failed rule; an empty list means the password is valid. */
  public List<String> violations(String password) {
    List<String> failed = new ArrayList<>();
    int length = password.codePointCount(0, password.length());
    if (length < minLength || length > maxLength) {
      failed.add("must be " + minLength + " to " + maxLength + " characters long");
    }
    for (Rule rule : rules) {
      if (!rule.pattern().matcher(password).find()) {
        failed.add(rule.message());
      }
    }
    return failed;
  }

  public static final class Builder {

    private int minLength = 8;
    private int maxLength = 64;
    private final List<Rule> rules = new ArrayList<>();

    public Builder length(int min, int max) {
      this.minLength = min;
      this.maxLength = max;
      return this;
    }

    public Builder requireLowercase() {
      rules.add(new Rule("needs a lowercase letter", "(?=.*[a-z])"));
      return this;
    }

    public Builder requireUppercase() {
      rules.add(new Rule("needs an uppercase letter", "(?=.*[A-Z])"));
      return this;
    }

    public Builder requireDigit() {
      rules.add(new Rule("needs a digit", "(?=.*\\d)"));
      return this;
    }

    public Builder requireSpecial() {
      rules.add(new Rule("needs a character that is not a letter or digit", "(?=.*[^a-zA-Z0-9])"));
      return this;
    }

    public Builder forbidWhitespace() {
      rules.add(new Rule("must not contain whitespace", "(?!.*\\s)"));
      return this;
    }

    /** Rejects passwords that contain the word, ignoring case; the word is quoted, so any text is safe. */
    public Builder forbidWord(String word) {
      rules.add(new Rule("must not contain \"" + word + "\"", "(?!.*(?i:" + Pattern.quote(word) + "))"));
      return this;
    }

    public PasswordPolicy build() {
      return new PasswordPolicy(minLength, maxLength, rules);
    }
  }
}

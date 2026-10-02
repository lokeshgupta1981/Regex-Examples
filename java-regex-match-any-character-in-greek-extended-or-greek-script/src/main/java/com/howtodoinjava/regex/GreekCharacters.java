package com.howtodoinjava.regex;

import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Greek characters in the output are printed as Java escapes so the console encoding does not matter.
 * Source code for https://howtodoinjava.com/java/regex/java-regex-match-any-character-in-greek-extended-or-greek-script/
 */
public class GreekCharacters {

  static final Pattern GREEK_SCRIPT = Pattern.compile("\\p{IsGreek}");
  static final Pattern GREEK_BLOCK = Pattern.compile("\\p{InGreek}");
  static final Pattern GREEK_EXTENDED_BLOCK = Pattern.compile("\\p{InGreekExtended}");
  static final Pattern GREEK_WORD = Pattern.compile("[\\p{IsGreek}\\p{M}]+");

  static boolean matches(String regex, String text) {
    return Pattern.compile(regex).matcher(text).matches();
  }

  static String cp(int codePoint) {
    return new String(Character.toChars(codePoint));
  }

  public static void main(String[] args) {

    String alpha = "\u03b1";                // Greek small letter alpha
    String alphaPsili = "\u1f00";           // alpha with psili (Greek Extended block)
    String copticShei = "\u03e2";           // Coptic capital letter shei (Greek and Coptic block)
    String ohm = "\u2126";                  // Ohm sign (Letterlike Symbols block, Greek script)

    section("Quick reference");
    show("\"\\u03b1\".matches(\"\\\\p{IsGreek}\")", alpha.matches("\\p{IsGreek}"));
    show("\"\\u1f00\".matches(\"\\\\p{IsGreek}\")", alphaPsili.matches("\\p{IsGreek}"));
    show("\"\\u03b1\".matches(\"\\\\p{InGreek}\")", alpha.matches("\\p{InGreek}"));
    show("\"\\u1f00\".matches(\"\\\\p{InGreek}\")", alphaPsili.matches("\\p{InGreek}"));
    show("\"\\u1f00\".matches(\"\\\\p{InGreekExtended}\")", alphaPsili.matches("\\p{InGreekExtended}"));
    show("\"\\u03e2\".matches(\"\\\\p{InGreek}\")", copticShei.matches("\\p{InGreek}"));
    show("\"\\u03e2\".matches(\"\\\\p{IsGreek}\")", copticShei.matches("\\p{IsGreek}"));
    show("\"\\u03b1\".matches(\"[\\\\u0370-\\\\u03FF\\\\u1F00-\\\\u1FFF]\")", alpha.matches("[\\u0370-\\u03FF\\u1F00-\\u1FFF]"));

    section("1. Finding Greek letters in text");
    String equation = "a + b = \u03b1 + \u03b2";
    GREEK_SCRIPT.matcher(equation).results()
        .forEach(r -> show("found " + escape(r.group()) + " at", r.start() + "-" + r.end()));
    show("count of Greek letters", GREEK_SCRIPT.matcher(equation).results().count());
    show("\"hello\" contains Greek", GREEK_SCRIPT.matcher("hello").find());
    show("equation contains Greek", GREEK_SCRIPT.matcher(equation).find());

    section("2. Script versus block");
    show("Coptic shei: InGreek / IsGreek", copticShei.matches("\\p{InGreek}") + " / " + copticShei.matches("\\p{IsGreek}"));
    show("Greek question mark U+037E: InGreek / IsGreek",
        "\u037e".matches("\\p{InGreek}") + " / " + "\u037e".matches("\\p{IsGreek}"));
    show("Unassigned U+0378: InGreek / IsGreek",
        "\u0378".matches("\\p{InGreek}") + " / " + "\u0378".matches("\\p{IsGreek}"));
    show("Ohm sign U+2126: InGreek / IsGreek", ohm.matches("\\p{InGreek}") + " / " + ohm.matches("\\p{IsGreek}"));
    show("Ancient Greek number U+10140: IsGreek", cp(0x10140).matches("\\p{IsGreek}"));
    show("Micro sign U+00B5: IsGreek", "\u00b5".matches("\\p{IsGreek}"));
    show("Character.UnicodeBlock.of('\\u03e2')", Character.UnicodeBlock.of(0x03e2));
    show("Character.UnicodeScript.of('\\u03e2')", Character.UnicodeScript.of(0x03e2));
    long greekScript = 0;
    long outsideBlocks = 0;
    for (int c = 0; c <= Character.MAX_CODE_POINT; c++) {
      if (Character.UnicodeScript.of(c) == Character.UnicodeScript.GREEK) {
        greekScript++;
        Character.UnicodeBlock b = Character.UnicodeBlock.of(c);
        if (b != Character.UnicodeBlock.GREEK && b != Character.UnicodeBlock.GREEK_EXTENDED) {
          outsideBlocks++;
        }
      }
    }
    long notGreekInBlock = 0;
    for (int c = 0x0370; c <= 0x03FF; c++) {
      if (Character.UnicodeScript.of(c) != Character.UnicodeScript.GREEK) {
        notGreekInBlock++;
      }
    }
    show("code points in the Greek script (Java 25)", greekScript);
    show("... of them outside the two Greek blocks", outsideBlocks);
    show("code points in Greek and Coptic block not in Greek script", notGreekInBlock);

    section("3. Greek Extended (polytonic Greek)");
    String polytonic = "\u1fb2 \u1fa8";
    GREEK_EXTENDED_BLOCK.matcher(polytonic).results()
        .forEach(r -> show("found " + escape(r.group()) + " at", r.start() + "-" + r.end()));
    show("\"\\u1f00\" InGreek", alphaPsili.matches("\\p{InGreek}"));
    show("\"\\u1f00\" InGreekExtended", alphaPsili.matches("\\p{InGreekExtended}"));
    show("\"\\u03b1\" InGreekExtended", alpha.matches("\\p{InGreekExtended}"));
    show("\"\\u1f00\" [\\p{InGreek}\\p{InGreekExtended}]", alphaPsili.matches("[\\p{InGreek}\\p{InGreekExtended}]"));

    section("4. Property name spellings");
    String[] names = {"\\p{IsGreek}", "\\p{script=Greek}", "\\p{sc=Greek}", "\\p{IsGrek}", "\\p{InGreek}",
        "\\p{InGreekandCoptic}", "\\p{block=Greek}", "\\p{blk=GreekExtended}", "\\p{InGreek_Extended}",
        "\\p{Greek}", "\\p{InGreek_and_Coptic}"};
    for (String name : names) {
      try {
        show(name + " on \"\\u03b1\"", matches(name, alpha));
      } catch (PatternSyntaxException e) {
        show(name, "PatternSyntaxException: " + e.getDescription());
      }
    }

    section("5. Whole Greek words, accents and case");
    String kalimera = "\u039a\u03b1\u03bb\u03b7\u03bc\u03ad\u03c1\u03b1";
    String kalimeraNfd = Normalizer.normalize(kalimera, Normalizer.Form.NFD);
    show("kalimera.length() / NFD length()", kalimera.length() + " / " + kalimeraNfd.length());
    show("kalimera matches \\p{IsGreek}+", kalimera.matches("\\p{IsGreek}+"));
    show("NFD kalimera matches \\p{IsGreek}+", kalimeraNfd.matches("\\p{IsGreek}+"));
    show("NFD kalimera matches [\\p{IsGreek}\\p{M}]+", GREEK_WORD.matcher(kalimeraNfd).matches());
    String mixed = "Hello \u039a\u03b1\u03bb\u03b7\u03bc\u03ad\u03c1\u03b1 and \u03b1\u03b2\u03b3!";
    show("Greek words in mixed text", GREEK_WORD.matcher(mixed).results().map(r -> escape(r.group())).toList());
    show("\"\\u0391\" (?i)\\u03b1", matches("(?i)\\u03b1", "\u0391"));
    show("\"\\u0391\" (?iu)\\u03b1", matches("(?iu)\\u03b1", "\u0391"));
    show("\"\\u0391\" with CASE_INSENSITIVE | UNICODE_CASE",
        Pattern.compile("\u03b1", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher("\u0391").matches());
    show("final sigma \"\\u03c2\" (?iu)\\u03a3", matches("(?iu)\\u03a3", "\u03c2"));
    show("NFC of Ohm sign", escape(Normalizer.normalize(ohm, Normalizer.Form.NFC)));
    show("NFKC of micro sign", escape(Normalizer.normalize("\u00b5", Normalizer.Form.NFKC)));
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-62s -> %s%n", expression, escape(String.valueOf(result)));
  }

  static String escape(String s) {
    StringBuilder sb = new StringBuilder();
    s.codePoints().forEach(c -> sb.append(c > 0x7E
        ? (c > 0xFFFF ? String.format("\\u{%X}", c) : String.format("\\u%04x", c))
        : String.valueOf((char) c)));
    return sb.toString();
  }
}

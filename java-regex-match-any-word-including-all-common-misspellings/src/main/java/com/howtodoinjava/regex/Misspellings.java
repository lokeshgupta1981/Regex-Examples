package com.howtodoinjava.regex;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs every example from the article and prints the result next to the expression.
 * Source code for https://howtodoinjava.com/java/regex/java-regex-match-any-word-including-all-common-misspellings/
 */
public class Misspellings {

  static final Pattern CALENDAR = Pattern.compile("(?i)\\bc[ae]l[ae]nd[ae]r\\b");

  static final String NOTES = "This is may calandar. This is june calander. This is may calendar.";

  public static void main(String[] args) {

    section("Quick reference");
    for (String w : List.of("calendar", "calandar", "calender", "calander", "kalendar")) {
      show("\"" + w + "\".matches(\"c[ae]l[ae]nd[ae]r\")", w.matches("c[ae]l[ae]nd[ae]r"));
    }
    show("\"colour\".matches(\"colou?r\")", "colour".matches("colou?r"));
    show("\"accomodate\".matches(\"ac{1,2}om{1,2}odate\")", "accomodate".matches("ac{1,2}om{1,2}odate"));
    show("\"recieve\".matches(\"rec(?:ei|ie)ve\")", "recieve".matches("rec(?:ei|ie)ve"));
    show("CALENDAR replaceAll in \"Check the calender.\"",
        CALENDAR.matcher("Check the calender.").replaceAll("calendar"));

    section("1. Techniques");
    show("all \\bgr[ae]y\\b in \"gray grey gry\"", all("\\bgr[ae]y\\b", "gray grey gry"));
    show("\"color\".matches(\"colou?r\")", "color".matches("colou?r"));
    show("\"colour\".matches(\"colou?r\")", "colour".matches("colou?r"));
    for (String w : List.of("accommodate", "acommodate", "accomodate", "acomodate", "accommmodate")) {
      show("\"" + w + "\".matches(\"ac{1,2}om{1,2}odate\")", w.matches("ac{1,2}om{1,2}odate"));
    }
    for (String w : List.of("receive", "recieve", "receeve")) {
      show("\"" + w + "\".matches(\"rec(?:ei|ie)ve\")", w.matches("rec(?:ei|ie)ve"));
    }
    for (String w : List.of("definitely", "definately", "definitly", "definatly", "defiantly")) {
      show("\"" + w + "\".matches(\"defin[ia]te?ly\")", w.matches("defin[ia]te?ly"));
    }
    show("all \\bSte(?:ven?|phen)\\b in \"Steve, Steven and Stephen met Stefan\"",
        all("\\bSte(?:ven?|phen)\\b", "Steve, Steven and Stephen met Stefan"));

    section("2. Finding every spelling in a text");
    Matcher m = Pattern.compile("c[ae]l[ae]nd[ae]r", Pattern.CASE_INSENSITIVE).matcher(NOTES);
    while (m.find()) {
      System.out.println("Start index: " + m.start() + " End index: " + m.end() + " " + m.group());
    }

    section("3. Whole words and case");
    show("all c[ae]l[ae]nd[ae]r in \"calendars and a calender\"",
        all("c[ae]l[ae]nd[ae]r", "calendars and a calender"));
    show("all \\bc[ae]l[ae]nd[ae]r\\b in \"calendars and a calender\"",
        all("\\bc[ae]l[ae]nd[ae]r\\b", "calendars and a calender"));
    show("all \\bc[ae]l[ae]nd[ae]rs?\\b in \"calendars and a calender\"",
        all("\\bc[ae]l[ae]nd[ae]rs?\\b", "calendars and a calender"));
    show("\"Calendar\".matches(\"c[ae]l[ae]nd[ae]r\")", "Calendar".matches("c[ae]l[ae]nd[ae]r"));
    show("\"Calendar\".matches(\"(?i)c[ae]l[ae]nd[ae]r\")", "Calendar".matches("(?i)c[ae]l[ae]nd[ae]r"));

    section("4. Listing only the misspelled words");
    show("misspelled in \"calendar, calender, Calendar\"",
        CALENDAR.matcher("calendar, calender, Calendar").results()
            .map(MatchResult::group)
            .filter(w -> !w.equalsIgnoreCase("calendar"))
            .toList());

    section("5. Correcting misspellings");
    String text = "Check the calender. Your Calandars are full.";
    show("CALENDAR.matcher(text).replaceAll(\"calendar\")", CALENDAR.matcher(text).replaceAll("calendar"));
    show("replaceAll keeping case and plural",
        text.replaceAll("\\b([cC])[ae]l[ae]nd[ae]r(s?)\\b", "$1alendar$2"));

    section("6. Readable patterns with (?x)");
    Pattern commented = Pattern.compile("""
        (?x)            # comments mode: spaces and # comments are ignored
        \\b c [ae]      # c, then a or e
        l [ae]          # l, then a or e
        nd [ae] r \\b   # nd, then a or e, then r
        """);
    show("commented.matcher(\"calender\").matches()", commented.matcher("calender").matches());
    show("commented.matcher(\"calendar\").matches()", commented.matcher("calendar").matches());

    section("7. Limits");
    show("\"celendar\".matches(\"c[ae]l[ae]nd[ae]r\")", "celendar".matches("c[ae]l[ae]nd[ae]r"));
    show("\"calendr\".matches(\"c[ae]l[ae]nd[ae]r\")", "calendr".matches("c[ae]l[ae]nd[ae]r"));
    show("\"kalendar\".matches(\"[ck][ae]l[ae]nd[ae]?r\")", "kalendar".matches("[ck][ae]l[ae]nd[ae]?r"));
    show("\"calendr\".matches(\"[ck][ae]l[ae]nd[ae]?r\")", "calendr".matches("[ck][ae]l[ae]nd[ae]?r"));
  }

  static List<String> all(String regex, String input) {
    return Pattern.compile(regex).matcher(input).results().map(MatchResult::group).toList();
  }

  static void section(String title) {
    System.out.println();
    System.out.println("=== " + title + " ===");
  }

  static void show(String expression, Object result) {
    System.out.printf("%-58s -> %s%n", expression, result);
  }
}

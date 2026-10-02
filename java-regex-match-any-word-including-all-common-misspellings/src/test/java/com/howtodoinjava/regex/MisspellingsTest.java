package com.howtodoinjava.regex;

import static com.howtodoinjava.regex.Misspellings.CALENDAR;
import static com.howtodoinjava.regex.Misspellings.NOTES;
import static com.howtodoinjava.regex.Misspellings.all;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MisspellingsTest {

  @ParameterizedTest
  @ValueSource(strings = {"calendar", "calandar", "calender", "calander"})
  void calendarSpellingsMatch(String word) {
    assertTrue(word.matches("c[ae]l[ae]nd[ae]r"));
  }

  @Test
  void techniques() {
    assertFalse("kalendar".matches("c[ae]l[ae]nd[ae]r"));
    assertEquals(List.of("gray", "grey"), all("\\bgr[ae]y\\b", "gray grey gry"));
    assertTrue("color".matches("colou?r"));
    assertTrue("colour".matches("colou?r"));
    for (String w : List.of("accommodate", "acommodate", "accomodate", "acomodate")) {
      assertTrue(w.matches("ac{1,2}om{1,2}odate"), w);
    }
    assertFalse("accommmodate".matches("ac{1,2}om{1,2}odate"));
    assertTrue("receive".matches("rec(?:ei|ie)ve"));
    assertTrue("recieve".matches("rec(?:ei|ie)ve"));
    assertFalse("receeve".matches("rec(?:ei|ie)ve"));
    for (String w : List.of("definitely", "definately", "definitly", "definatly")) {
      assertTrue(w.matches("defin[ia]te?ly"), w);
    }
    assertFalse("defiantly".matches("defin[ia]te?ly"));
    assertEquals(List.of("Steve", "Steven", "Stephen"),
        all("\\bSte(?:ven?|phen)\\b", "Steve, Steven and Stephen met Stefan"));
  }

  @Test
  void findEverySpellingWithIndexes() {
    List<String> found = Pattern.compile("c[ae]l[ae]nd[ae]r", Pattern.CASE_INSENSITIVE).matcher(NOTES)
        .results().map(r -> r.start() + "-" + r.end() + " " + r.group()).toList();
    assertEquals(List.of("12-20 calandar", "35-43 calander", "57-65 calendar"), found);
  }

  @Test
  void wholeWordsAndCase() {
    assertEquals(List.of("calendar", "calender"), all("c[ae]l[ae]nd[ae]r", "calendars and a calender"));
    assertEquals(List.of("calender"), all("\\bc[ae]l[ae]nd[ae]r\\b", "calendars and a calender"));
    assertEquals(List.of("calendars", "calender"), all("\\bc[ae]l[ae]nd[ae]rs?\\b", "calendars and a calender"));
    assertFalse("Calendar".matches("c[ae]l[ae]nd[ae]r"));
    assertTrue("Calendar".matches("(?i)c[ae]l[ae]nd[ae]r"));
  }

  @Test
  void listOnlyMisspelledWords() {
    assertEquals(List.of("calender"), CALENDAR.matcher("calendar, calender, Calendar").results()
        .map(MatchResult::group)
        .filter(w -> !w.equalsIgnoreCase("calendar"))
        .toList());
  }

  @Test
  void correctMisspellings() {
    assertEquals("Check the calendar.", CALENDAR.matcher("Check the calender.").replaceAll("calendar"));
    String text = "Check the calender. Your Calandars are full.";
    assertEquals("Check the calendar. Your Calandars are full.", CALENDAR.matcher(text).replaceAll("calendar"));
    assertEquals("Check the calendar. Your Calendars are full.",
        text.replaceAll("\\b([cC])[ae]l[ae]nd[ae]r(s?)\\b", "$1alendar$2"));
  }

  @Test
  void commentsMode() {
    Pattern commented = Pattern.compile("""
        (?x)            # comments mode: spaces and # comments are ignored
        \\b c [ae]      # c, then a or e
        l [ae]          # l, then a or e
        nd [ae] r \\b   # nd, then a or e, then r
        """);
    assertTrue(commented.matcher("calender").matches());
    assertTrue(commented.matcher("calendar").matches());
  }

  @Test
  void limits() {
    assertTrue("celendar".matches("c[ae]l[ae]nd[ae]r"));
    assertFalse("calendr".matches("c[ae]l[ae]nd[ae]r"));
    assertTrue("kalendar".matches("[ck][ae]l[ae]nd[ae]?r"));
    assertTrue("calendr".matches("[ck][ae]l[ae]nd[ae]?r"));
  }
}

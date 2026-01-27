package com.neptunedreams.refBuilder;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 1/25/26
 * <br>Time: 10:53?AM
 * <br>@author Miguel Muñoz</p>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReferenceParserTest {

  @Test
  void parse() {
    String rawText = "<ref>{{ cite news | access-date = 25 January 2026 | title = The Stand " +
        "| first = James Earl | last = Carter " +
        "| editor-first = Miguel | editor-last = Munoz " +
        "| contributor-first = Stephen | contributor-last = King " +
        "| interviewer-first1 = Jesse | interviewer-last1 = James " +
        "| interviewer-first2 = Tabitha | interviewer-last2 = King " +
        "| translator-first1 = Clive | translator-last1 = Cussler " +
        "| translator-first2 = Gabriel | translator-last2 = Garcia Marquez }}</ref>";
    ReferenceParser parser = new ReferenceParser(rawText);
    List<WikiReference> refList = parser.parse();
    WikiReference wikiReference = refList.get(0);
    List<String> dataKeys = wikiReference.getDataKeys();
    List<String> expectedDataKeys = Arrays.asList("access-date", "title", "first", "last", "editor-first", "editor-last",
        "contributor-first", "contributor-last", "interviewer-first1", "interviewer-last1", "interviewer-first2",
        "interviewer-last2", "translator-first1", "translator-last1", "translator-first2", "translator-last2");
    assertThat(dataKeys, containsInAnyOrder(expectedDataKeys.toArray()));
    assertEquals(expectedDataKeys.size(), dataKeys.size());
    assertEquals(rawText, wikiReference.toString());
    System.out.println(wikiReference);
  }
}

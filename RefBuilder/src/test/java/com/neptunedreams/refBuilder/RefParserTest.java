package com.neptunedreams.refBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/17/25
 * <br>Time: 2:32?AM
 * <br>@author Miguel Mu–oz</p>
 */
@SuppressWarnings("StringConcatenation")
class RefParserTest {
  public static final String sourceTextWebNoSpace = "<ref name=\"BOB\">{{cite web|url" +
      "=https://www.npr.org/2025/02/11/nx-s1-5293246/hegseth-fort-bragg-liberty-name" +
      "|access-date=Jun 11, 2025|publisher=National Public Radio" +
      "|title=Fort Bragg 2.0: Army base reverts to its old name, but with a new namesake" +
      "|date=Feb 2, 2025}}</ref>";
  public static final String sourceTextWebWithSpaces = "< ref name = \"BOB\" > { { cite web | url = " +
      " https://www.npr.org/2025/02/11/nx-s1-5293246/hegseth-fort-bragg-liberty-name " +
      " | access-date = Jun 11, 2025 | publisher = National Public Radio\n" +
      " | title = Fort Bragg 2.0: Army base reverts to its old name, but with a new namesake" +
      " | date = Feb 2, 2025 }  } <  /  ref  >";
  public static final String sourceTextNews = "<ref name=\"NYT\">{{cite news | work = (work)" +
      " | access-date = June 17, 2025 | pages = b5-b7" +
      " | title = Fort Bragg 2.0: Army base reverts to its old name, but with a new namesake" +
      " | url = www.nytimes.com/2025/02/11/nx-s1-5293246/hegseth-fort-bragg-liberty-name | date = June 17, 2025" +
      " | language = en | newspaper = New York Times}}</ref>";
  public static final String sourceTextJournal = "<ref name=\"NYKR\">{{cite journal" +
      " | title = Fort Bragg 2.0: Army base reverts to its old name, but with a new namesake | issn = issn-data" +
      " | url-status = live | url = www.NewYorker.com/2025/06/16/nx-s1-5293246/hegseth-fort-bragg-liberty-name" +
      " | publisher = Conde Nast | issue = June 16, 2025 | date = 06-16 | journal = The New Yorker" +
      " | access-date = 2025-06-17 | year = 2025 | bibcode = bib-code-data | pages = 49-53}}</ref>";
  public static final String sourceTextBook = "<ref>{{cite book |year=1911 |" +
      "title=Lloyd's Register of British and Foreign Shipping |volume=II.ÐSteamers |location=London |" +
      "publisher=[[Lloyd's Register]] of Shipping |at=TIRÐTIT |" +
      "url= https://archive.org/details/HECROS1912ST/page/n1006/mode/1up |via=[[Internet Archive]]}}</ref>";

  @Test
  public void testParseWebWithSpaces() throws IOException {
    testWebTokens(sourceTextWebWithSpaces);
  }

  @Test
  public void testParseWebNoSpaces() throws IOException {
    testWebTokens(sourceTextWebNoSpace);
  }

  private void testWebTokens(String sourceText) throws IOException {
    RefParser.Token[] expected = getExpectedWebTokens();
    String[] strings = {sourceText }; //, sourceTextNews, sourceTextJournal, sourceTextBook};

    List<RefParser.Token> foundTokens = new LinkedList<>();
    for (String string : strings) {
      RefParser parser = new RefParser(string);
      RefParser.Token token = parser.getLowToken();
//    System.out.println(token.text());
      while (token.marker() != RefParser.Marker.end) {
        System.out.printf("%-12s - %s%n", token.marker(), token.text()); // NON-NLS
//        System.out.printf("new RefParser.Token(RefParser.Marker.%s, \"%s\"),%n", token.marker(), token.text()); // NON-NLS
        foundTokens.add(token);
        if ((token.marker() == RefParser.Marker.equals) || (token.marker() == RefParser.Marker.quote)) {
          token = parser.getMixedToken();
        } else {
          token = parser.getLowToken();
        }
      }
      assertArrayEquals(expected, foundTokens.toArray());
    }
  }
  
  private static RefParser.Token @NotNull [] getExpectedWebTokens() {
    return new RefParser.Token[]{
        new RefParser.Token(RefParser.Marker.tagOpen, "<"),
        new RefParser.Token(RefParser.Marker.ref, "ref"),
        new RefParser.Token(RefParser.Marker.name, "name"),
        new RefParser.Token(RefParser.Marker.equals, "="),
        new RefParser.Token(RefParser.Marker.quote, "\""),
        new RefParser.Token(RefParser.Marker.rawText, "BOB"),
        new RefParser.Token(RefParser.Marker.quote, "\""),
        new RefParser.Token(RefParser.Marker.tagClose, ">"),
        new RefParser.Token(RefParser.Marker.braceOpen, "{"),
        new RefParser.Token(RefParser.Marker.braceOpen, "{"),
        new RefParser.Token(RefParser.Marker.word, "cite"),
        new RefParser.Token(RefParser.Marker.webKey, "web"),
        new RefParser.Token(RefParser.Marker.bar, "|"),
        new RefParser.Token(RefParser.Marker.word, "url"),
        new RefParser.Token(RefParser.Marker.equals, "="),
        new RefParser.Token(RefParser.Marker.rawText, "https://www.npr.org/2025/02/11/nx-s1-5293246/hegseth-fort-bragg-liberty-name"),
        new RefParser.Token(RefParser.Marker.bar, "|"),
        new RefParser.Token(RefParser.Marker.word, "access-date"),
        new RefParser.Token(RefParser.Marker.equals, "="),
        new RefParser.Token(RefParser.Marker.rawText, "Jun 11, 2025"),
        new RefParser.Token(RefParser.Marker.bar, "|"),
        new RefParser.Token(RefParser.Marker.word, "publisher"),
        new RefParser.Token(RefParser.Marker.equals, "="),
        new RefParser.Token(RefParser.Marker.rawText, "National Public Radio"),
        new RefParser.Token(RefParser.Marker.bar, "|"),
        new RefParser.Token(RefParser.Marker.word, "title"),
        new RefParser.Token(RefParser.Marker.equals, "="),
        new RefParser.Token(RefParser.Marker.rawText, "Fort Bragg 2.0: Army base reverts to its old name, but with a new namesake"),
        new RefParser.Token(RefParser.Marker.bar, "|"),
        new RefParser.Token(RefParser.Marker.word, "date"),
        new RefParser.Token(RefParser.Marker.equals, "="),
        new RefParser.Token(RefParser.Marker.rawText, "Feb 2, 2025"),
        new RefParser.Token(RefParser.Marker.braceClose, "}"),
        new RefParser.Token(RefParser.Marker.braceClose, "}"),
        new RefParser.Token(RefParser.Marker.tagOpen, "<"),
        new RefParser.Token(RefParser.Marker.slash, "/"),
        new RefParser.Token(RefParser.Marker.ref, "ref"),
        new RefParser.Token(RefParser.Marker.tagClose, ">"),
    };
  }
}

package com.neptunedreams.refBuilder;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The strange rules to determine when to call KeywordParser vs RawTextParser are to handle quotes and the 
 * word "journal," which may be a cite type or a keyword in a key/value pair. In the real application, these
 * rules won't be necessary because the methods will be called in different contexts.
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/17/25
 * <br>Time: 2:32 AM
 * <br>@author Miguel Muñoz</p>
 */
@SuppressWarnings("StringConcatenation")
class AbstractParserTest {
  public static final String sourceTextWebNoSpace = "<ref name=\"Orlando D'Free\">{{cite web|url" +
      "=https://www.npr.org/2025/02/11/nx-s1-5293246/hegseth-fort-bragg-liberty-name" +
      "|access-date=Jun 11, 2025|publisher=National Public Radio" +
      "|title=Fort Bragg 2.0: Army base reverts to its old name, but with a new namesake" +
      "|date=Feb 2, 2025}}</ref>";
  public static final String sourceTextWebWithSpaces = "< ref name = \"Orlando D'Free\" > { { cite web | url = " +
      " https://www.npr.org/2025/02/11/nx-s1-5293246/hegseth-fort-bragg-liberty-name " +
      " | access-date = Jun 11, 2025 | publisher = National Public Radio\n" +
      " | title = Fort Bragg 2.0: Army base reverts to its old name, but with a new namesake" +
      " | date = Feb 2, 2025 }  } <  /  ref  >";
  public static final String sourceTextNews = "<ref name=\"NYT\">{{ cite news" +
      " | access-date = June 17, 2025 | pages = b5-b7" +
      " | work = (work)" +
      " | title = Fort Bragg 2.0: Army base reverts to its old name, but with a new namesake" +
      " | url = www.nytimes.com/2025/02/11/nx-s1-5293246/hegseth-fort-bragg-liberty-name | date = June 17, 2025" +
      " | language = en | newspaper = New York Times}}</ref>";
  public static final String sourceTextJournal = "<ref name=\"NYKR\">{{ cite journal" +
      " | title = Fort Bragg 2.0: Army base reverts to its old name, but with a new namesake | issn = issn-data" +
      " | url-status = live | url = www.NewYorker.com/2025/06/16/nx-s1-5293246/hegseth-fort-bragg-liberty-name" +
      " | publisher = Conde Nast | issue = June 16, 2025 | date = 06-16 | journal = The New Yorker" +
      " | access-date = 2025-06-17 | year = 2025 | bibcode = bib-code-data | pages = 49-53}}</ref>";
  public static final String sourceTextBook = "<ref>{{cite book |year=1911 |" +
      "title=Lloyd's Register of British and Foreign Shipping |volume=II.–Steamers |location=London |" +
      "publisher=[[Lloyd's Register]] of Shipping |at=TIR–TIT |" +
      "url= https://archive.org/details/HECROS1912ST/page/n1006/mode/1up |via=[[Internet Archive]]}}</ref>";

  @Test
  public void testParseWebWithSpaces() {
    testTokens(sourceTextWebWithSpaces, getExpectedWebTokens());
  }

  @Test
  public void testParseWebNoSpaces() {
    testTokens(sourceTextWebNoSpace, getExpectedWebTokens());
  }

  @Test
  void testParseNews() {
    testTokens(sourceTextNews, getExpectedNewsTokens());
  }

  @Test
  void testParseJournal() {
    testTokens(sourceTextJournal, getExpectedJournalTokens());
  }

  @Test
  void testParseBook() {
    testTokens(sourceTextBook, getExpectedBookTokens());
  }

  @Test
  void testParseList() {
    AbstractParser.Token[] webTokens = getExpectedWebTokens();
    AbstractParser.Token[] bookTokens = getExpectedBookTokens();
    AbstractParser.Token[] allTokens = new AbstractParser.Token[webTokens.length + bookTokens.length];
    System.arraycopy(webTokens, 0, allTokens, 0, webTokens.length);
    System.arraycopy(bookTokens, 0, allTokens, webTokens.length, bookTokens.length);
    testTokens(sourceTextWebWithSpaces + sourceTextBook, allTokens);
  }
  
  @Test
  void testParseListFull() {
    ReferenceParser parser = new ReferenceParser(sourceTextNews+sourceTextJournal);
    List<WikiReference> results = parser.parse();
    assertEquals(2, results.size());
    assertEquals(sourceTextNews, results.get(0).toString());
    assertEquals(sourceTextJournal, results.get(1).toString());
  }

  private void testTokens(String sourceText, AbstractParser.Token[] expected) {
    String rawText = "rawText";
    Object keyword = "keyword";
    Object nextWord = keyword;
    String[] strings = { sourceText }; //, sourceTextNews, sourceTextJournal, sourceTextBook};

    List<AbstractParser.Token> foundTokens = new LinkedList<>();
    for (String string : strings) {
      FreePushbackReader reader = new FreePushbackReader(new StringReader(string));
      RefKeywordProcessor keywordParser = new RefKeywordProcessor(reader);
      RawTextParser rawTextParser = new RawTextParser(reader);
      AbstractParser.Token token = keywordParser.getToken();
//    System.out.println(token.text());
      while (token.marker() != AbstractParser.Marker.end) {
        System.out.println(token); // NON-NLS
        // This line helps me create unit tests, so I leave it commented out for now, but ready for service again.
        // The tests getExpectXxxTokens() were generated using this line, then debugged.
//        System.out.printf("new RefParser.Token(RefParser.Marker.%s, \"%s\"),%n", token.marker(), polish(token.text())); // NON-NLS
        foundTokens.add(token);
        final AbstractParser.Marker marker = token.marker();
        if (
            (marker == AbstractParser.Marker.word) ||
            (marker == AbstractParser.Marker.quote) ||
            (marker == AbstractParser.Marker.journalKey) // ||
        ) {
          nextWord = rawText;
        } else if (marker != AbstractParser.Marker.equals) {
          nextWord = keyword;
        }
//        System.out.printf("For token %s: using %s%n", token, nextWord); // NON-NLS
        if ((nextWord == rawText)) {
          token = rawTextParser.getToken();
        } else {
          token = keywordParser.getToken();
        }
//        System.out.printf("%n"); // NON-NLS
      }
      int i=0;
      // These four lines may be uncommented to debug parsing errors. They show both the actual and expected tokens,
      // so you can see exactly where mismatches occur.
//      for (AbstractParser.Token refToken: expected) {
//        AbstractParser.Token actual  = foundTokens.get(i++);
//        System.out.printf("%n%s%n%s%n", refToken, actual); // NON-NLS
//      }
      assertArrayEquals(expected, foundTokens.toArray());
    }
    System.out.println();
  }

  // This is used by a line that I sometimes uncomment, because it helps me build unit tests,
  // so I'm leaving it in for now. It escapes the quote character.
  @SuppressWarnings({"MagicCharacter", "unused"})
  private static String polish(String s) {
    if (s.contains("\"")) {
      StringBuilder sb = new StringBuilder(s);
      for (char c : s.toCharArray()) {
        if (c == '\"') {
          sb.append("\\\"");
        } else {
          sb.append(c);
        }
      }
      return sb.toString();
    }
    return s;
  }
  
  private static AbstractParser.Token @NotNull [] getExpectedWebTokens() {
    return new AbstractParser.Token[]{
        new AbstractParser.Token(AbstractParser.Marker.tagOpen, "<"),
        new AbstractParser.Token(AbstractParser.Marker.ref, "ref"),
        new AbstractParser.Token(AbstractParser.Marker.name, "name"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.quote, "\""),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "Orlando D'Free"),
        new AbstractParser.Token(AbstractParser.Marker.quote, "\""),
        new AbstractParser.Token(AbstractParser.Marker.tagClose, ">"),
        new AbstractParser.Token(AbstractParser.Marker.braceOpen, "{"),
        new AbstractParser.Token(AbstractParser.Marker.braceOpen, "{"),
        new AbstractParser.Token(AbstractParser.Marker.cite, "cite"),
        new AbstractParser.Token(AbstractParser.Marker.webKey, "web"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "url"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "https://www.npr.org/2025/02/11/nx-s1-5293246/hegseth-fort-bragg-liberty-name"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "access-date"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "Jun 11, 2025"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "publisher"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "National Public Radio"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "title"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "Fort Bragg 2.0: Army base reverts to its old name, but with a new namesake"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "date"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "Feb 2, 2025"),
        new AbstractParser.Token(AbstractParser.Marker.braceClose, "}"),
        new AbstractParser.Token(AbstractParser.Marker.braceClose, "}"),
        new AbstractParser.Token(AbstractParser.Marker.tagOpen, "<"),
        new AbstractParser.Token(AbstractParser.Marker.slash, "/"),
        new AbstractParser.Token(AbstractParser.Marker.ref, "ref"),
        new AbstractParser.Token(AbstractParser.Marker.tagClose, ">"),
    };
  }

  private static AbstractParser.Token @NotNull [] getExpectedNewsTokens() {
    return new AbstractParser.Token[]{
        new AbstractParser.Token(AbstractParser.Marker.tagOpen, "<"),
        new AbstractParser.Token(AbstractParser.Marker.ref, "ref"),
        new AbstractParser.Token(AbstractParser.Marker.name, "name"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.quote, "\""),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "NYT"),
        new AbstractParser.Token(AbstractParser.Marker.quote, "\""),
        new AbstractParser.Token(AbstractParser.Marker.tagClose, ">"),
        new AbstractParser.Token(AbstractParser.Marker.braceOpen, "{"),
        new AbstractParser.Token(AbstractParser.Marker.braceOpen, "{"),
        new AbstractParser.Token(AbstractParser.Marker.cite, "cite"),
        new AbstractParser.Token(AbstractParser.Marker.news_key, "news"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "access-date"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "June 17, 2025"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "pages"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "b5-b7"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "work"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "(work)"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "title"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "Fort Bragg 2.0: Army base reverts to its old name, but with a new namesake"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "url"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "www.nytimes.com/2025/02/11/nx-s1-5293246/hegseth-fort-bragg-liberty-name"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "date"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "June 17, 2025"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "language"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "en"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "newspaper"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "New York Times"),
        new AbstractParser.Token(AbstractParser.Marker.braceClose, "}"),
        new AbstractParser.Token(AbstractParser.Marker.braceClose, "}"),
        new AbstractParser.Token(AbstractParser.Marker.tagOpen, "<"),
        new AbstractParser.Token(AbstractParser.Marker.slash, "/"),
        new AbstractParser.Token(AbstractParser.Marker.ref, "ref"),
        new AbstractParser.Token(AbstractParser.Marker.tagClose, ">")
    };
  }

  private static AbstractParser.Token @NotNull [] getExpectedJournalTokens() {
    return new AbstractParser.Token[]{
        new AbstractParser.Token(AbstractParser.Marker.tagOpen, "<"),
        new AbstractParser.Token(AbstractParser.Marker.ref, "ref"),
        new AbstractParser.Token(AbstractParser.Marker.name, "name"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.quote, "\""),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "NYKR"),
        new AbstractParser.Token(AbstractParser.Marker.quote, "\""),
        new AbstractParser.Token(AbstractParser.Marker.tagClose, ">"),
        new AbstractParser.Token(AbstractParser.Marker.braceOpen, "{"),
        new AbstractParser.Token(AbstractParser.Marker.braceOpen, "{"),
        new AbstractParser.Token(AbstractParser.Marker.cite, "cite"),
        new AbstractParser.Token(AbstractParser.Marker.journalKey, "journal"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "title"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "Fort Bragg 2.0: Army base reverts to its old name, but with a new namesake"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "issn"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "issn-data"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "url-status"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "live"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "url"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "www.NewYorker.com/2025/06/16/nx-s1-5293246/hegseth-fort-bragg-liberty-name"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "publisher"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "Conde Nast"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "issue"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "June 16, 2025"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "date"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "06-16"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.journalKey, "journal"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "The New Yorker"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "access-date"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "2025-06-17"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "year"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "2025"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "bibcode"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "bib-code-data"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "pages"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "49-53"),
        new AbstractParser.Token(AbstractParser.Marker.braceClose, "}"),
        new AbstractParser.Token(AbstractParser.Marker.braceClose, "}"),
        new AbstractParser.Token(AbstractParser.Marker.tagOpen, "<"),
        new AbstractParser.Token(AbstractParser.Marker.slash, "/"),
        new AbstractParser.Token(AbstractParser.Marker.ref, "ref"),
        new AbstractParser.Token(AbstractParser.Marker.tagClose, ">"),
    };
  }

  private static AbstractParser.Token @NotNull [] getExpectedBookTokens() {
    return new AbstractParser.Token[]{
        new AbstractParser.Token(AbstractParser.Marker.tagOpen, "<"),
        new AbstractParser.Token(AbstractParser.Marker.ref, "ref"),
        new AbstractParser.Token(AbstractParser.Marker.tagClose, ">"),
        new AbstractParser.Token(AbstractParser.Marker.braceOpen, "{"),
        new AbstractParser.Token(AbstractParser.Marker.braceOpen, "{"),
        new AbstractParser.Token(AbstractParser.Marker.cite, "cite"),
        new AbstractParser.Token(AbstractParser.Marker.bookKey, "book"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "year"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "1911"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "title"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "Lloyd's Register of British and Foreign Shipping"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "volume"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "II.–Steamers"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "location"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "London"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "publisher"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "[[Lloyd's Register]] of Shipping"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "at"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "TIR–TIT"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "url"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "https://archive.org/details/HECROS1912ST/page/n1006/mode/1up"),
        new AbstractParser.Token(AbstractParser.Marker.bar, "|"),
        new AbstractParser.Token(AbstractParser.Marker.word, "via"),
        new AbstractParser.Token(AbstractParser.Marker.equals, "="),
        new AbstractParser.Token(AbstractParser.Marker.rawText, "[[Internet Archive]]"),
        new AbstractParser.Token(AbstractParser.Marker.braceClose, "}"),
        new AbstractParser.Token(AbstractParser.Marker.braceClose, "}"),
        new AbstractParser.Token(AbstractParser.Marker.tagOpen, "<"),
        new AbstractParser.Token(AbstractParser.Marker.slash, "/"),
        new AbstractParser.Token(AbstractParser.Marker.ref, "ref"),
        new AbstractParser.Token(AbstractParser.Marker.tagClose, ">"),
    };
  }
}

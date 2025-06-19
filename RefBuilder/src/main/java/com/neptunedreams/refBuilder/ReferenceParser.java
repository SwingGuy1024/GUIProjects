/*
 * <p>Language:</p>
 * <pre>
 *   referenceList:
 *      reference
 *      reference referenceList
 *
 **   reference:
 *      refOpen ref_data refClose
 *
 *
 **   refOpen:
 *      TAG_OPEN REF TAG_CLOSE
 *      TAG_OPEN REF NAME = quoted_name TAG_CLOSE
 *
 *   refType:
 *      lowText
 *
 **   refClose:
 *      TAG_OPEN SLASH REF TAG_CLOSE
 *
 **   quoted_name:
 *      QUOTE raw_text QUOTE
 *
 *   ref_data:
 *      BRACE_OPEN BRACE_OPEN CITE ref_type ref_field_data BRACE_CLOSE BRACE_CLOSE
 *
 *   ref_field_data:
 *      ref_field
 *      ref_field BAR ref_field_data
 *
 *   ref_field:
 *      BAR word EQUALS rawText
 *
 *   ref_type:
 *      NEWS
 *      JOURNAL
 *      WEB
 *      BOOK
 *
 *   ref_close:
 *      TAG_OPEN SLASH REF TAG_CLOSE
 *
 *   word:
 *      alpha_numeric_char
 *      alpha_numeric_char word
 *
 *   raw_text:
 *      char
 *      char raw_text
 *
 *   common_key:
 *      "title", "year", "date", "url", "page", "pages", "volume", "language", "publisher", "location", "access-date", "url-access", "url-status", "archive-url", "archive-date", "ref"
 *
 *    news_key:, "news.newspaper", "news.agency",
 *       "news.work"
 *
 *    journal_key:
 *       "journal.journal", "journal.issue", "journal.doi", "journal.doi-access", "journal.issn", "journal.bibcode"
 *
 *    web_key:
 *
 *    book_key:
 *        "book.isbn", "book.location", "book.orig-year", "book.edition", "book.oclc", "book.chapter", "book.chapter-url", "book.author-link"
 *
 *
 *    REF*: ref
 *    CITE*: cite
 *    NAME*: name
 *    NEWS: news
 *    JOURNAL: journal
 *    WEB: web
 *    BOOK: book
 *    TAG_OPEN: <
 *    TAG_CLOSE: >
 *    SLASH: /
 *    BAR: |
 *    BRACE_OPEN: {
 *    BRACE_CLOSE: }
 *    EQUALS: =
 *
 * * Ignore case
 * </pre>
 */

package com.neptunedreams.refBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import com.neptunedreams.refBuilder.AbstractParser.Marker;
import com.neptunedreams.refBuilder.AbstractParser.Token;

/**
 * <p>Language:</p>
 * <pre>
 *   referenceList:
 *      reference
 *      reference referenceList
 *
 **   reference:
 *      refOpen ref_data refClose
 *
 *
 **   refOpen:
 *      TAG_OPEN REF TAG_CLOSE
 *      TAG_OPEN REF NAME = quoted_name TAG_CLOSE
 *
 *   refType:
 *      lowText
 *
 **   refClose:
 *      TAG_OPEN SLASH REF TAG_CLOSE
 *
 **   quoted_name:
 *      QUOTE raw_text QUOTE
 *
 *   ref_data:
 *      BRACE_OPEN BRACE_OPEN CITE ref_type ref_field_data BRACE_CLOSE BRACE_CLOSE
 *
 *   ref_field_data:
 *      ref_field
 *      ref_field BAR ref_field_data
 *
 *   ref_field:
 *      BAR word EQUALS rawText
 *
 *   ref_type:
 *      NEWS
 *      JOURNAL
 *      WEB
 *      BOOK
 *
 *   ref_close:
 *      TAG_OPEN SLASH REF TAG_CLOSE
 *
 *   word:
 *      alpha_numeric_char
 *      alpha_numeric_char word
 *
 *   raw_text:
 *      char
 *      char raw_text
 *
 *   common_key:
 *      "title", "year", "date", "url", "page", "pages", "volume", "language", "publisher", "location",
 *      "access-date", "url-access", "url-status", "archive-url", "archive-date", "ref"
 *
 *   news_key:, "news.newspaper", "news.agency",
 *      "news.work"
 *
 *   journal_key:
 *      "journal.journal", "journal.issue", "journal.doi", "journal.doi-access", "journal.issn",
 *      "journal.bibcode"
 *
 *   web_key:
 *
 *   book_key:
 *       "book.isbn", "book.location", "book.orig-year", "book.edition", "book.oclc", "book.chapter",
 *       "book.chapter-url", "book.author-link"
 *
 *
 *   REF*: ref
 *   CITE*: cite
 *   NAME*: name
 *   NEWS: news
 *   JOURNAL: journal
 *   WEB: web
 *   BOOK: book
 *   TAG_OPEN: <
 *   TAG_CLOSE: >
 *   SLASH: /
 *   BAR: |
 *   BRACE_OPEN: {
 *   BRACE_CLOSE: }
 *   EQUALS: =
 *
 * * Ignore case
 * </pre>
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/19/25
 * <br>Time: 12:29 AM
 * <br>@author Miguel Muñoz</p>
 */
public class ReferenceParser {
  private final RefKeywordProcessor keywordProcessor;
  private final RawTextParser rawTextParser;
  private final PushbackReader reader;
  
  private WikiReference wikiReference = new WikiReference();

  public ReferenceParser(String text) {
    reader = new PushbackReader(new BufferedReader(new StringReader(text)));
    keywordProcessor = new RefKeywordProcessor(reader);
    rawTextParser = new RawTextParser(reader);
  }

  private List<WikiReference> parse() throws IOException {
    int ch = reader.read();
    List<WikiReference> references = new LinkedList<>();
    if (ch == -1) {
      return references;
    }
    Token nextToken = new Token(Marker.noMarker, String.valueOf((char)ch)); 
    do {
      reader.unread(nextToken.text().toCharArray());
      nextToken = parseReference(references);
    } while (nextToken.marker() != Marker.end);
    return references;
  }
  
  private Token parseReference(List<WikiReference> rList) throws IOException {
    Token nextToken = null;
    do {
      if (nextToken != null) {
        reader.unread(nextToken.text().toCharArray());
      }
      parseRefOpen();
      WikiReference reference = parseRefData();
      rList.add(reference);
      nextToken = parseRefClose();
      wikiReference = new WikiReference();
    } while (nextToken.marker() != Marker.end);
    return nextToken;
  }
  
  private void parseRefOpen() {
    expect(Marker.tagOpen, Marker.ref);
    Token token = keywordProcessor.getToken();
    if (token.marker() == Marker.name) {
      expect(Marker.equals);
      String name = parseQuotedName();
      wikiReference.setName(name);
    }
    expect(Marker.tagClose);
  }
  
  private Token parseRefClose() {
    expect(Marker.tagOpen, Marker.slash, Marker.ref, Marker.tagClose);
    return keywordProcessor.getToken();
  }
  
  private String parseQuotedName() {
    expect(Marker.quote);
    String name = expect(Marker.rawText);
    expect(Marker.quote);
    return name;
  }

  private WikiReference parseRefData() {
    String refType = expect(Marker.braceOpen, Marker.braceOpen, Marker.cite, Marker.citeValue);
    // Four possibilities here
    RefKey refKey = RefKey.valueOf(refType);
    wikiReference.setRefKey(refKey);
    Token nextToken = parseRefFieldData();
    verify(nextToken, Marker.braceClose);
    expect(Marker.braceClose);
    return wikiReference;
  }
  
  @SuppressWarnings("LoopStatementThatDoesntLoop")
  private Token parseRefFieldData() {
    while (true) {
      Token nextToken = keywordProcessor.getToken();
      do {
        nextToken = parseRefField(nextToken);
      } while (nextToken.marker() == Marker.bar);
      return nextToken;
    }
  }
  
  private Token parseRefField(Token nextToken) {
    verify(nextToken, Marker.bar);
    String key = expect(Marker.word);
    expect(Marker.equals);
    String value = expect(Marker.rawText);
    wikiReference.setKeyValuePair(key, value);
    return keywordProcessor.getToken();
  }

  private String expect(Marker marker) {
    Token token;
    if (marker == Marker.rawText) {
      token = rawTextParser.getToken();
    } else {
      token = keywordProcessor.getToken();
    }
    verify(token, marker);
    return token.text();
  }
  
  private String expect(Marker first, Marker... markers) {
    Token token = keywordProcessor.getToken();
    verify(token, first);
    for (Marker marker: markers) {
      token = keywordProcessor.getToken();
      verify(token, marker);
    }
    return token.text(); // returns last token
  }
  
  private void verify(Token token, Marker marker) {
    if(marker.matchText(token.text())) {
      return;
    }
    if (token.marker() != marker) {
      throw new MismatchException(token, marker);
    }
  }

  @SuppressWarnings("StringConcatenation")
  public static void main(String[] args) throws IOException {
    String text = "<ref name=\"Orlando D'Free\">{{cite web|url" +
        "=https://www.npr.org/2025/02/11/nx-s1-5293246/hegseth-fort-bragg-liberty-name" +
        "|access-date=Jun 11, 2025|publisher=National Public Radio" +
        "|title=Fort Bragg 2.0: Army base reverts to its old name, but with a new namesake" +
        "|date=Feb 2, 2025}}</ref>" +

        "<ref name=\"Beveridge 09Chap1\">{{cite book |last1=Beveridge |" +
        "first1=Bruce |last2=Andrews |first2=Scott |first3=Steve |last3=Hall |last4=Klistorner |" +
        "first4=Daniel |editor1-first=Art |editor1-last=Braunschweiger |" +
        "title=Titanic: The Ship Magnificent |" +
        "chapter-url=http://www.titanic-theshipmagnificent.com/synopsis/chapter01/ |access-date=25 May 2011 |" +
        "volume=I |year=2009 |chapter=Chapter 1: Inception & Construction Plans |" +
        "publisher=History Press |location=Gloucestershire, United Kingdom |isbn=9780752446066 |" +
        "archive-date=24 April 2012 |" +
        "archive-url=https://web.archive.org/web/20120424214844/http://www.titanic-theshipmagnificent.com/synopsis/chapter01/ |url-status=dead }}</ref>";
    ReferenceParser parser = new ReferenceParser(text);
    List<WikiReference> wikiRefs = parser.parse();
    for (WikiReference ref: wikiRefs) {
      System.out.println(ref);
    }
  }
}



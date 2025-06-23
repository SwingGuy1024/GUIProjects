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
  private final FreePushbackReader reader;
  
  private WikiReference wikiReference = new WikiReference();

  public ReferenceParser(String text) {
    reader = new FreePushbackReader(new BufferedReader(new StringReader(text)));
    keywordProcessor = new RefKeywordProcessor(reader);
    rawTextParser = new RawTextParser(reader);
  }

  public List<WikiReference> parse() {
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
  
  private Token parseReference(List<WikiReference> rList) {
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
    String key = expectWord();
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

  private String expectWord() {
    Token token = keywordProcessor.getToken(false);
    
    // Sometimes, when looking for a word, it will match some other keyword. In that case, we still accept the word.
    if ((token.marker() == Marker.word) || token.marker().isText()) {
      return token.text();
    }
    return token.text();
  }
  
  private String expectOneOf(Marker... markers) {
    assert markers.length > 0;
    Token token = keywordProcessor.getToken();
    final String text = token.text();
    for (Marker marker: markers) {
      if (marker.matchText(text)) {
        return text;
      }
    }
    throw new MismatchException(token, markers[0]);
  }
  
  private void verify(Token token, Marker marker) {
    if(marker.matchText(token.text())) {
      return;
    }
    // If the marker is Marker.word, we don't look at the text, because it could be anything.
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

/* 
 Exceptions to the parsing rules:
 
 <ref>''The Titanic: The Memorabilia Collection'', by Michael Swift, Igloo Publishing 2011, {{ISBN|978-0-85780-251-4}}</ref>
 
 <ref>[https://chroniclingamerica.loc.gov/lccn/sn88064469/1911-06-06/ed-1/seq-4/#date1=1911&sort=relevance&rows=20&words=Titanic&searchType=basic&sequence=0&index=19&state=&date2=1911&protext=Titanic&y=0&x=0&dateFilterType=yearRange&page=4 The Caucasian] {{webarchive |url=https://web.archive.org/web/20210106102950/https://chroniclingamerica.loc.gov/lccn/sn88064469/1911-06-06/ed-1/seq-4/#date1=1911&sort=relevance&rows=20&words=Titanic&searchType=basic&sequence=0&index=19&state=&date2=1911&protext=Titanic&y=0&x=0&dateFilterType=yearRange&page=4 |date=6 January 2021 }}
 
 <ref>[https://chroniclingamerica.loc.gov/lccn/sn88064469/1911-06-06/ed-1/seq-4/#date1=1911&sort=relevance&rows=20&words=Titanic&searchType=basic&sequence=0&index=19&state=&date2=1911&protext=Titanic&y=0&x=0&dateFilterType=yearRange&page=4 The Caucasian] {{webarchive |url=https://web.archive.org/web/20210106102950/https://chroniclingamerica.loc.gov/lccn/sn88064469/1911-06-06/ed-1/seq-4/#date1=1911&sort=relevance&rows=20&words=Titanic&searchType=basic&sequence=0&index=19&state=&date2=1911&protext=Titanic&y=0&x=0&dateFilterType=yearRange&page=4 |date=6 January 2021 }}, (newspaper of Shreveport, Louisiana) 6 June 1911...Retrieved 4 October 2018</ref>
 
 <ref>Eaton and Haas; ''The Misadventures of the White Star Line'', c. 1990</ref>
 
 <ref>De Kerbrech, Richard, ''Ships of the White Star Line'', pp. 50, 53, 112</ref>
 
 <ref>[http://maritimequest.com/liners/olympic_page_3.htm portrait is ''Olympic''] {{webarchive |url=https://web.archive.org/web/20210106103038/http://maritimequest.com/liners/olympic_page_3.htm |date=6 January 2021}} on MaritimeQuest.com webpage, Olympic picture page #3, which states the ship.</ref>
 
 <ref name="New York Times 1913, p. 28">''New York Times'', Thursday 16 January 1913, ''Titanic Survivors Asking $6,000,000'', p.28.</ref>
 
 <ref name=wsj1>{{cite news|title=The Real Reason for the Tragedy of the Titanic|last=Berg|first=Chris|newspaper=The Wall Street Journal|date=13 April 2012|url=https://www.wsj.com/articles/SB10001424052702304444604577337923643095442|access-date=8 August 2017|archive-date=14 June 2018|archive-url=https://web.archive.org/web/20180614194758/https://www.wsj.com/articles/SB10001424052702304444604577337923643095442|url-status=live}}</ref>
 
 <ref>[{{GBurl|id=GRbbn6mquSwC|q=the+maiden+voyage+george+bowyer|p=81}} ''A Cold Night in the Atlantic''] pp. 81–82 by Kevin Wright Carney, 2008 {{ISBN|978-1-9350-2802-4}} (hard cover)</ref>
 
 <ref>[http://www.titanicology.com/Titanica/FireDownBelow.pdf Fire Down Below] {{webarchive |url=https://web.archive.org/web/20191209234718/http://www.titanicology.com/Titanica/FireDownBelow.pdf |date=9 December 2019}} – by Samuel Halpern. Retrieved 7 January 2017.</ref>
 
<ref>Titanic Research & Modeling Association: [http://titanic-model.com/db/db-03/CoalBunkerFire.htm ''Coal Bunker Fire''] {{webarchive |url=https://web.archive.org/web/20120512220653/http://titanic-model.com/db/db-03/CoalBunkerFire.htm |date=12 May 2012}}</ref>

<ref name=Fire&Ice>[https://web.archive.org/web/20180520190112/http://wormstedt.com/Titanic/TITANIC-FIRE-AND-ICE-Article.pdf Titanic: Fire & Ice (Or What You Will)] Various Authors. Retrieved 23 January 2017.</ref>
 */


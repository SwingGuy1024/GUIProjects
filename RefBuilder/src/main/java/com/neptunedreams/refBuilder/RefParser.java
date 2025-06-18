package com.neptunedreams.refBuilder;

/*
 * <p>Language:</p>
 * <pre>
 *   reference:
 *      refOpen
 *      ref_data
 *      refClose
 * 
 *   
 *   refOpen:
 *      refTag
 * 
 *   refTag:
 *      TAG_OPEN REF TAG_CLOSE
 *      TAG_OPEN REF NAME = quoted_name TAG_CLOSE
 * 
 *   refClose:
 *      TAG_OPEN SLASH REF TAG_CLOSE
 * 
 *   quoted_name:
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
 *      word EQUALS rawText
 *   
 *   ref_type:
 *      NEWS
 *      JOURNAL
 *      WEB
 *      BOOK
 * 
 *   ref_close:
 *      TAG_OPEN SLASH word TAG_CLOSE
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/11/25
 * <br>Time: 10:39 PM
 * <br>@author Miguel Muñoz</p>
 */
public class RefParser {
  public enum Marker {
    tagOpen("<"),
    ref("ref"),
    tagClose(">"),
    name("name"),
    equals("="),
    braceOpen("{"),
    braceClose("}"),
    slash("/"),
    quote("\""),
    bar("|"),
    news_key("news"),
    journalKey("journal"),
    webKey("web"),
    bookKey("book"),
    word(), // no spaces allowed
    rawText(), // spaces allowed
    end(),
    ;
    private final String contents;
    private final boolean hasContents;
    Marker(String contents) { this.contents = contents; hasContents = true; }
    Marker() { contents = ""; hasContents = false; }
    
    public boolean hasContents() { return hasContents; }
    public String getContents() { return contents; }
  }
  
  private static final Map<String, Marker> markerMap = makeMarkerMap();
  private static final Set<Character> delimChars = makeDelimChars();
  private static final Set<Character> rawDelimChars = makeRawDelimChars();
  
  private static Map<String, Marker> makeMarkerMap() {
    Map<String, Marker> map = new HashMap<>();
    for (Marker marker : Marker.values()) {
      if (marker.hasContents()) {
        map.put(marker.getContents(), marker);
      }
    }
    return map;
  }
  
  private static Set<Character> makeDelimChars() {
    Set<Character> set = new HashSet<>();
    for (Marker marker : Marker.values()) {
      if (marker.hasContents()) {
        String contents = marker.getContents();
        final char firstChar = contents.charAt(0);
        if (contents.length() == 1) {
          set.add(firstChar);
        }
      }
    }
    return set;
  }
  
  @SuppressWarnings("MagicCharacter")
  private static Set<Character> makeRawDelimChars() {
    // This removed characters that are allowed in raw text but not on other text.
    Set<Character> set = new HashSet<>(delimChars);
    set.remove('/');
    return set;
  }
  
  public record Token (Marker marker, String text) { }
  
  private final PushbackReader reader;
  
  RefParser(String text) {
    reader = new PushbackReader(new BufferedReader(new BufferedReader(new StringReader(text))));
  }
  
  private enum WhiteSpace { WHITESPACE_ALLOWED, NO_WHITESPACE }
  private enum UpperCase { UPPER_ALLOWED, LOWER_REQUIRED }
  
  public Token getLowToken() throws IOException {
    return getToken(WhiteSpace.NO_WHITESPACE, UpperCase.LOWER_REQUIRED);
  }

  public Token getMixedToken() throws IOException {
    return getToken(WhiteSpace.WHITESPACE_ALLOWED, UpperCase.UPPER_ALLOWED);
  }


//  public Token getMixedToken() throws IOException {
//    StringBuilder builder = new StringBuilder();
//    int ch = reader.read();
//    while (ch != -1) {
//      
//    }
//    return 
//  }
  
  private Token getToken(WhiteSpace whiteSpace, UpperCase allowUpperCase) throws IOException {
    StringBuilder builder = new StringBuilder();
    int ch = reader.read();
    Set<Character> activeDelimChars = (whiteSpace == WhiteSpace.WHITESPACE_ALLOWED) ? rawDelimChars : delimChars;
    while (ch != -1) {
      while ((whiteSpace == WhiteSpace.NO_WHITESPACE) && Character.isWhitespace(ch)) {
        if (!builder.isEmpty()) {
          return new Token(Marker.word, builder.toString());
        }
        ch = reader.read();
      }
      if ((whiteSpace == WhiteSpace.WHITESPACE_ALLOWED) && (builder.isEmpty())) {
      // Skip leading white space
        while (Character.isWhitespace(ch)) {
          ch = reader.read();
        }
      }
      if (allowUpperCase == UpperCase.LOWER_REQUIRED) {
        ch = Character.toLowerCase(ch);
      }
      builder.append((char) ch);
      final String text = builder.toString();
      Marker marker = markerMap.get(text);
      if (marker != null) {
        return new Token(marker, text);
      }
      if ((!builder.isEmpty()) && activeDelimChars.contains((char) ch)) {
        reader.unread(ch);
        builder.deleteCharAt(builder.length() - 1);
        return processToken(whiteSpace, builder.toString()); // text is out of date!
      }
      ch = reader.read();
    }
    return new Token(Marker.end, "");
  }

  private static @NotNull Token processToken(WhiteSpace whiteSpace, String text) {
    if (whiteSpace == WhiteSpace.WHITESPACE_ALLOWED) {
      return new Token(Marker.rawText, text.trim());
    } else {
      return new Token(Marker.word, text);
    }
  }
}

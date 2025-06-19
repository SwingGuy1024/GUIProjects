package com.neptunedreams.refBuilder;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>The parsing rules are in the comment above, which is not a Javadocs comment.</p>
 * <p>This currently doesn't handle quotes in the values of key-value pairs. I don't know if they ever
 * get used, but if they do, and I need to handle them, I may have to add a third subclass of RefParser.</p>
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/11/25
 * <br>Time: 10:39 PM
 * <br>@author Miguel Muñoz</p>
 */
public abstract class AbstractParser {
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
    cite("cite"),
    bar("|"),
    news_key("news"),
    journalKey("journal"),
    webKey("web"),
    bookKey("book"),
    word(), // no spaces allowed
    rawText(), // spaces allowed
    refType(),
    end(),
    noMarker("\0"), // never matches anything
    ;
    private final String contents;
    private final boolean hasContents;
    Marker(String contents) { this.contents = contents; hasContents = true; }
    Marker() { contents = ""; hasContents = false; }
    
    public boolean hasContents() { return hasContents; }
    public String getContents() { return contents; }

  }

  public record Token (Marker marker, String text) {

    public boolean isGood() { return this.marker != Marker.noMarker; }
    @Override
    public String toString() {
      return String.format("%-12s - %s", marker, text);
    }

  }

  enum WhiteSpace { WHITESPACE_ALLOWED, NO_WHITESPACE }

//  private static final Map<String, Marker> markerMap = makeMarkerMap();
//  private static final Set<Character> delimChars = makeDelimChars();
//  private static final Set<Character> rawDelimChars = makeRawDelimChars();

  private static final Token NO_TOKEN = new Token(Marker.noMarker, "\0");

  private final List<WikiReference> referenceList = new LinkedList<>();
  protected static Map<String, Marker> makeMarkerMap(Marker... excludedMarkers) {
    Set<Marker> excluded = new HashSet<>(List.of(excludedMarkers));
    Map<String, Marker> map = new HashMap<>();
    for (Marker marker : Marker.values()) {
      if (marker.hasContents() && !excluded.contains(marker)) {
        map.put(marker.getContents(), marker);
      }
    }
    return map;
  }

  protected abstract Map<String, Marker> getMarkerMap();

  protected abstract Token getToken() throws IOException;

  protected abstract Token createTextToken(String text);

  protected static Set<Character> makeDelimChars(String excluded) {
    Set<Character> set = new HashSet<>();
    for (Marker marker : Marker.values()) {
      if (marker.hasContents()) {
        String contents = marker.getContents();
        final char firstChar = contents.charAt(0);
        if ((contents.length() == 1) && (excluded.indexOf(firstChar) == -1)) {
          set.add(firstChar);
        }
      }
    }
    return set;
  }

  private final PushbackReader reader;
  
//  private RefKeywordProcessor keywordProcessor;
//  private RawTextParser rawTextParser;
//  
//  public static RefParser create(String text) {
//    RefParser parser = new RefParser();
//  }

  protected AbstractParser(PushbackReader reader) {
    this.reader = reader;
  }

  protected Token variableTextToToken(WhiteSpace whiteSpace, StringBuilder builder, int ch, Set<Character> activeDelimChars) throws IOException {
    builder.append((char) ch);
    final String text = builder.toString();
    Marker marker = getMarkerMap().get(text);
    assert marker != Marker.noMarker;
    if (marker != null) {
      return new Token(marker, text);
    }
    if ((!builder.isEmpty()) && activeDelimChars.contains((char) ch)) {
      reader.unread(ch);
      builder.deleteCharAt(builder.length() - 1);
      return createTextToken(builder.toString());
    }
    return NO_TOKEN;
  }

//  private static @NotNull Token createToken(WhiteSpace whiteSpace, String text) {
//    if (whiteSpace == WhiteSpace.WHITESPACE_ALLOWED) {
//      return new Token(Marker.rawText, text.trim());
//    } else {
//      return new Token(Marker.word, text);
//    }
//  }
  
//  private List<WikiReference> parse() {
//    List<WikiReference> references = new LinkedList<>();
//    while ()
//    WikiReference reference = parseReference();
//    references.add(reference);
//  }
}

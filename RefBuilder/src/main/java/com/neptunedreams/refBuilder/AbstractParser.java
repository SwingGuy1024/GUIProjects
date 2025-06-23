package com.neptunedreams.refBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
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
    citeValue() {
      @Override
      public boolean matchText(String text) { return CITE_VALUES.contains(text.toLowerCase()); }
    },
    ;
    private final String contents;
    private final boolean hasContents;
    private final boolean isText;
    Marker(String contents) { this.contents = contents; hasContents = true; isText = contents.length() > 1; }
    Marker() { contents = ""; hasContents = false; isText = false; }
    
    public boolean hasContents() { return hasContents; }
    public String getContents() { return contents; }
    public boolean isText() { return isText; }
    
    public boolean matchText(String text) {
      return contents.equalsIgnoreCase(text);
    }
    private static final Set<String> CITE_VALUES = Set.of("web", "book", "news", "journal");
  }

  public record Token (Marker marker, String text) {

    public boolean isGood() { return this.marker != Marker.noMarker; }
    @Override
    public String toString() {
      return String.format("%-12s - %s", marker, text);
    }

  }

//  private static final Map<String, Marker> markerMap = makeMarkerMap();
//  private static final Set<Character> delimChars = makeDelimChars();
//  private static final Set<Character> rawDelimChars = makeRawDelimChars();

  protected static final Token NO_TOKEN = new Token(Marker.noMarker, "\0");

  protected static Map<String, Marker> makeMarkerMap(Marker... excludedMarkers) {
    Set<Marker> excluded = (excludedMarkers.length == 0) ?
        EnumSet.noneOf(Marker.class) :
        EnumSet.copyOf(Arrays.asList(excludedMarkers));
    Map<String, Marker> map = new HashMap<>();
    for (Marker marker : Marker.values()) {
      if (marker.hasContents() && !excluded.contains(marker)) {
        map.put(marker.getContents(), marker);
      }
    }
    return map;
  }

  protected abstract Map<String, Marker> getMarkerMap();

//  protected abstract Token getToken() throws IOException;

  protected abstract Token createTextToken(String text);

  protected static Set<Character> makeDelimChars(String excluded) {
    Set<Character> set = new HashSet<>();
    for (Marker marker : Marker.values()) {
      if (marker.hasContents()) {
        String contents = marker.getContents();
        final char firstChar = contents.charAt(0);

        // If it's a one-character content, and it's not excluded...
        if ((contents.length() == 1) && (excluded.indexOf(firstChar) == -1)) {
          set.add(firstChar);
        }
      }
    }
    return set;
  }

  private final FreePushbackReader reader;
  
//  private RefKeywordProcessor keywordProcessor;
//  private RawTextParser rawTextParser;
//  
//  public static RefParser create(String text) {
//    RefParser parser = new RefParser();
//  }

  protected AbstractParser(FreePushbackReader reader) {
    this.reader = reader;
  }

  /**
   * <p>Returns a text Token. A text is either a keyword Token, the contents of a quoted string, or a rawText Token,
   * depending on the implementing class. It may return NO_TOKEN.</p>
   * <p>Returns a keyword if it finds one, and if {@code keywordsOnly} is true. If {@code keywordsOnly} is false,
   * returns word or rawText, depending on the implementing class</p>
   * <p>Called by getXxxToken() methods of both subclasses.</p>
   * <p>This should only be called when whitespace has been found or a delimiter has been read and unread. It should
   * only be called when the builder is not empty.</p>
   * @param builder The builder, which should only contain characters that should appear in the appropriate text token.
   * @param keywordsOnly true if it needs to find a keyword, false for quoted text or raw text.
   * @return An appropriate token.
   */
  protected Token variableTextToToken(StringBuilder builder, boolean keywordsOnly) {
    assert !builder.isEmpty();
    final String text = builder.toString();
    if (keywordsOnly) {
      Marker marker = getMarkerMap().get(text);
      assert marker != Marker.noMarker;
      if (marker != null) {
        return new Token(marker, text);
      }
      return new Token(Marker.word, text);
    }
    return createTextToken(text);
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

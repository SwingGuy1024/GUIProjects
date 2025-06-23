package com.neptunedreams.refBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/18/25
 * <br>Time: 9:29 PM
 * <br>@author Miguel Muñoz</p>
 */
public class RefKeywordProcessor extends AbstractParser {
  
  private final Set<Character> delimChars;
  private final Map<String, Marker> markerMap;
  private final Map<String, Marker> markerKeywordMap = makeMarkerKeywordMap();
  private final FreePushbackReader reader;

  public RefKeywordProcessor(FreePushbackReader reader) {
    super(reader);
    delimChars = makeDelimChars("");
    markerMap = makeMarkerMap();
    this.reader = reader;
  }

  @Override
  protected Map<String, Marker> getMarkerMap() {
    return Collections.unmodifiableMap(markerMap);
  }
  
  private static Map<String, Marker> makeMarkerKeywordMap() {
    final Map<String, Marker> map = Arrays.stream(Marker.values())
        .filter(Marker::isText)
        .collect(Collectors.toMap(Marker::getContents, marker -> marker));
    System.out.printf("MarkerKeywordMap = %s%n", map); // NON-NLS
    return map;
  }

//  @Override
  public Token getToken() {
    return getToken(true);
  }

//  @Override
  public Token getToken(boolean keywordsAllowed) {
    StringBuilder builder = new StringBuilder();
    int chInt = reader.read();
    while (chInt != -1) {
      char ch = (char) chInt;
      if (Character.isWhitespace(ch)) {
        if (!builder.isEmpty()) {
//          final Token token = variableTextToToken(builder, !keywordsAllowed);
//          if (token.isGood()) {
//            return token;
//          }
//          assert builder.chars().allMatch(Character::isLetter) : builder; // chars() returns IntStream
          return variableTextToToken(builder, keywordsAllowed);
        }
      } else  if (delimChars.contains(ch)) {
        if (builder.isEmpty()) {
          Marker marker = markerMap.get(String.valueOf(ch));
          if (marker == null) {
            throw new UnknownSymbolException(ch);
          }
          return new Token(marker, String.valueOf(ch));
        }
        
        // builder is not empty...
        reader.unread(ch);
        return variableTextToToken(builder, keywordsAllowed);
      } else {
        // ch is not whitespace or a delimiter
        ch = Character.toLowerCase(ch);
        builder.append(ch);
      }
      chInt = reader.read();
    }
    return new Token(Marker.end, "");
  }
  
  @Override
  protected Token createTextToken(String text) {
    return new Token(Marker.word, text);
  }
}

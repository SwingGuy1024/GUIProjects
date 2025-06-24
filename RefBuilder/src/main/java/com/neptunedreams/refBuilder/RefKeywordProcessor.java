package com.neptunedreams.refBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.neptunedreams.refBuilder.exception.UnknownSymbolException;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/18/25
 * <br>Time: 9:29 PM
 * <br>@author Miguel Muñoz</p>
 */
public class RefKeywordProcessor extends AbstractParser {
  
  private final Set<Character> delimChars;
  private final Map<String, Marker> markerMap;
  private final FreePushbackReader reader;
  String priorTextToken = "";


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
          priorTextToken = builder.toString();
          return variableTextToToken(builder, keywordsAllowed);
        }
      } else  if (delimChars.contains(ch)) {
        if (builder.isEmpty()) {
          Marker marker = markerMap.get(String.valueOf(ch));
          if (marker == null) {
            throw new UnknownSymbolException(ch, priorTextToken);
          }
          return new Token(marker, String.valueOf(ch));
        }
        
        // builder is not empty...
        reader.unread(ch);
        priorTextToken = builder.toString();
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

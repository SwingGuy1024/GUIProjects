package com.neptunedreams.refBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

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

  @Override
  public Token getToken()  {
    StringBuilder builder = new StringBuilder();
    int ch = reader.read();
//    Set<Character> activeDelimChars = delimChars;
    while (ch != -1) {
      while (Character.isWhitespace(ch)) {
        if (!builder.isEmpty()) {
          return new Token(Marker.word, builder.toString());
        }
        ch = reader.read();
      }
      ch = Character.toLowerCase(ch);
      final Token token = variableTextToToken(builder, ch, delimChars);
      if (token.isGood()) {
        return token;
      }
      ch = reader.read();
    }
    return new Token(Marker.end, "");
  }

  @Override
  protected Token createTextToken(String text) {
    return new Token(Marker.word, text);
  }
}

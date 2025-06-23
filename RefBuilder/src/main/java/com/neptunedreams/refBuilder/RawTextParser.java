package com.neptunedreams.refBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.neptunedreams.refBuilder.exception.UnknownSymbolException;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/18/25
 * <br>Time: 10:13 PM
 * <br>@author Miguel Muñoz</p>
 */
public class RawTextParser extends AbstractParser {
  private final Set<Character> delimChars;
  private final Map<String, Marker> markerMap;
  private final FreePushbackReader reader;

  public RawTextParser(FreePushbackReader reader) {
    super(reader);
    delimChars = makeDelimChars("/");
    markerMap = makeMarkerMap(Marker.quote);
    this.reader = reader;
  }

  @Override
  protected Map<String, Marker> getMarkerMap() {
    return Collections.unmodifiableMap(markerMap);
  }

  protected Token getToken() {
    StringBuilder builder = new StringBuilder();
    int chInt = reader.read();
    while (chInt != -1) {
      char ch = (char) chInt;
      if (builder.isEmpty()) {
        if (delimChars.contains(ch)) {
          final String delimString = String.valueOf(ch);
          Marker marker = getMarkerMap().get(delimString);
          if (marker == null) {
            throw new UnknownSymbolException(ch);
          }
          return new Token(marker, delimString);
        }
        // skip leading white space
        if (!Character.isWhitespace(ch)) {
          builder.append(ch);
        }
      } else {
        // builder is not empty...
        if (delimChars.contains(ch)) {
          reader.unread(ch);
          return variableTextToToken(builder, false); // should be false.
        } else {
          builder.append(ch);
        }
      }
      chInt = reader.read();
    }
    return new Token(Marker.end, "");
  }

  @Override
  protected Token createTextToken(String text) {
    return new Token(Marker.rawText, text.trim());
  }
}

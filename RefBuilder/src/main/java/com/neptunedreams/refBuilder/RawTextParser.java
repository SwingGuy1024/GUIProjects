package com.neptunedreams.refBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

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

  @Override
  protected Token getToken() {
    StringBuilder builder = new StringBuilder();
    int ch = reader.read();
    while (ch != -1) {
      if (builder.isEmpty()) {
        // skip leading white space
        while (Character.isWhitespace(ch)) {
          ch = reader.read();
        }
      }
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
    return new Token(Marker.rawText, text.trim());
  }
}

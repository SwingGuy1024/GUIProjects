package com.neptunedreams.refBuilder;

import java.io.IOException;
import java.io.PushbackReader;
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
  private final PushbackReader reader;

  public RawTextParser(PushbackReader reader) {
    super(reader);
    delimChars = makeDelimChars("/");
    markerMap = makeMarkerMap(Marker.quote);
    this.reader = reader;
  }

  @Override
  protected Map<String, Marker> getMarkerMap() {
    return markerMap;
  }

  @Override
  protected Token getToken() {
    StringBuilder builder = new StringBuilder();
    try {
      int ch = reader.read();
      while (ch != -1) {
        if (builder.isEmpty()) {
          // skip leading white space
          while (Character.isWhitespace(ch)) {
            ch = reader.read();
          }
        }
        final Token token = variableTextToToken(WhiteSpace.WHITESPACE_ALLOWED, builder, ch, delimChars);
        if (token.isGood()) {
          return token;
        }
        ch = reader.read();
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return new Token(Marker.end, "");
  }

  @Override
  protected Token createTextToken(String text) {
    return new Token(Marker.rawText, text.trim());
  }
}

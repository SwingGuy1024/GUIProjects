package com.neptunedreams.refBuilder.exception;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/23/25
 * <br>Time: 2:14 AM
 * <br>@author Miguel Muñoz</p>
 */
public class UnknownSymbolException extends IllegalStateException {
  public UnknownSymbolException(char symbolCharacter) {
    super(formatString(symbolCharacter, ""));
  }

  public UnknownSymbolException(char symbolCharacter, String precedingText) {
    super(formatString(symbolCharacter, precedingText));
  }
  
  private static String formatString(char symbolCharacter, String precedingText) {
    if (precedingText.isEmpty()) {
      return String.format("Unknown symbol: %c", symbolCharacter);
    }
    return String.format("Unknown symbol %c after \"%s\"", symbolCharacter, precedingText);
  }
}

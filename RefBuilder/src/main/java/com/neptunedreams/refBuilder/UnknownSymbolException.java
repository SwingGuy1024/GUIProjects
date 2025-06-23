package com.neptunedreams.refBuilder;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/23/25
 * <br>Time: 2:14 AM
 * <br>@author Miguel Muñoz</p>
 */
public class UnknownSymbolException extends IllegalStateException {
  public UnknownSymbolException(char symbolCharacter) {
    super("Unknown symbol: " + symbolCharacter);
  }

  public UnknownSymbolException(char symbolCharacter, String precedingText) {
    super(String.format("Unknown symbol %c after \"%s\"", symbolCharacter, precedingText));
  }
}

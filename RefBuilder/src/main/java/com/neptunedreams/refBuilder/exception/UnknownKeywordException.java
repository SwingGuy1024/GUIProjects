package com.neptunedreams.refBuilder.exception;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/23/25
 * <br>Time: 1:57 AM
 * <br>@author Miguel Muñoz</p>
 */
public class UnknownKeywordException extends IllegalStateException {
  public UnknownKeywordException(String unknownKeyword) {
    super(String.format("Unknown keyword '%s'", unknownKeyword));
  }
}

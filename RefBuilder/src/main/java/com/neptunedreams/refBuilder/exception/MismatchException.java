package com.neptunedreams.refBuilder.exception;

import com.neptunedreams.refBuilder.AbstractParser.Marker;
import com.neptunedreams.refBuilder.AbstractParser.Token;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/19/25
 * <br>Time: 1:08 AM
 * <br>@author Miguel Muñoz</p>
 */
public class MismatchException extends IllegalStateException {
  public MismatchException(Token token, Marker marker) {
    super(String.format
        ("Expected token of type Marker.%s but found type Marker.%s holding %s", marker, token.marker(), token.text())
    );
  }
}

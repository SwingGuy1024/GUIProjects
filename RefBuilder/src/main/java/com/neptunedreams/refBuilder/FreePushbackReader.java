package com.neptunedreams.refBuilder;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

/**
 * <p>A "Free" PushbackReader which is free of IOExceptions on the methods needed by this project.</p>
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/19/25
 * <br>Time: 4:14 PM
 * <br>@author Miguel Muñoz</p>
 */
public class FreePushbackReader extends PushbackReader {
  
  private int count = 0;
  private final StringBuilder soFar = new StringBuilder();
  
  public FreePushbackReader(Reader in) {
    super(in);
  }

  @Override
  public void unread(int c){
    try {
      super.unread(c);
      // bookkeeping
      count --;
      unCountFromBuilder(1);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void unread(char[] cbuf, int off, int len) {
    try {
      super.unread(cbuf, off, len);
      // bookkeeping
      count -= len;
      unCountFromBuilder(len);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public void unRead(String s) {
    char[] cBuf = s.toCharArray();
    unread(cBuf, 0, cBuf.length); // This calls overridden unread(char[], int, int), which does the bookkeeping.
  }

  @Override
  public int read() {
    try {
      count++;
      final int charAsInt = super.read();
      if (charAsInt != -1) {
        soFar.append((char) charAsInt);
      }
      return charAsInt;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
  
  public int getCount() { return count; }
  
  private void unCountFromBuilder(int charCount) {
    int length = soFar.length();
    soFar.setLength(length - charCount);
  }
  
  public String getStringSoFar() { return soFar.toString(); }
}

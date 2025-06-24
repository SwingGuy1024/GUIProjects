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
  
  public FreePushbackReader(Reader in) {
    super(in);
  }

  @Override
  public void unread(int c){
    // TODO: Write FreePushbackReader.unread()
    try {
      super.unread(c);
      count --;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void unread(char[] cbuf, int off, int len) {
    // TODO: Write FreePushbackReader.unread()
    try {
      super.unread(cbuf, off, len);
      count -= len;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void unread(char[] cbuf) {
    // TODO: Write FreePushbackReader.unread()
    try {
      super.unread(cbuf);
      count -= cbuf.length;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public int read() {
    // TODO: Write FreePushbackReader.read()
    try {
      count++;
      return super.read();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
  
  public int getCount() { return count; }
}

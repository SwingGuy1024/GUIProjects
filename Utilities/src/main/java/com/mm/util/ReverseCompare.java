package com.mm.util;

import java.util.Comparator;

/**
 * <p>Comparator used to execute a "Tail sort," where words are sorted from the back of the word rather 
 * than the front. For example, these words are tail sorted: drama, arena, raise, arose. The words 
 * ending in e come after those ending in a, and so on.</p>
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 2/3/25
 * <br>Time: 1:23 PM
 * <br>@author Miguel Muñoz</p>
 */
public class ReverseCompare implements Comparator<String> {
  public static final ReverseCompare INSTANCE = new ReverseCompare();
  @Override
  public int compare(String s1, String s2) {
    int n1 = s1.length();
    int n2 = s2.length();
    for (int i1 = n1 - 1, i2 = n2 - 1; (i1 >= 0) && (i2 >= 0); i1--, i2--) {
      char c1 = s1.charAt(i1);
      char c2 = s2.charAt(i2);
      if (c1 != c2) {
        return c1 - c2;
      }
    }
    return n1 - n2;
  }
}

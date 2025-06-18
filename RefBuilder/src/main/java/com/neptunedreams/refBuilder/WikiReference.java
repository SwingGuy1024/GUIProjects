package com.neptunedreams.refBuilder;

import java.util.Map;
import java.util.TreeMap;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 6/16/25
 * <br>Time: 9:39 AM
 * <br>@author Miguel Muñoz</p>
 */
public class WikiReference {
  private final Map<String, String> dataMap = new TreeMap<>();
  private RefKey refKey;
  private String name = "";
  
  WikiReference() {}

  public RefKey getRefKey() {
    return refKey;
  }

  public void setRefKey(RefKey refKey) {
    this.refKey = refKey;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

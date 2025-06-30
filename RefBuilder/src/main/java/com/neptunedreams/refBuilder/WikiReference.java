package com.neptunedreams.refBuilder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
  private final List<String> dataKeys = new LinkedList<>(); // to preserve key order
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

  public void setKeyValuePair(String key, String value) {
    dataMap.put(key, value);
    dataKeys.add(key);
  }
  
  public Map<String, String> getDataMap() {
    return Collections.unmodifiableMap(dataMap);
  }
  
  public List<String> getDataKeys() { return Collections.unmodifiableList(dataKeys); }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (name.isEmpty()) {
      builder.append("<ref>");
    } else {
      builder.append(String.format("<ref name=\"%s\">", name));
    }
    builder
        .append("{{ cite ")
        .append(refKey);
    
    for (String key: dataKeys) {
      builder
          .append(" | ")
          .append(key)
          .append(" = ")
          .append(dataMap.get(key));
    }

    builder.append(" }}</ref>");
    
    return builder.toString();
  }
}

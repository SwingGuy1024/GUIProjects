package com.neptunedreams.refBuilder;

import java.util.Collections;
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
  
  public void setKeyValuePair(String key, String value) {
    dataMap.put(key, value);
  }
  
  public Map<String, String> getDataMap() {
    return Collections.unmodifiableMap(dataMap);
  }

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
    
    for (Map.Entry<String, String> entry : dataMap.entrySet()) {
      builder
          .append(" | ")
          .append(entry.getKey())
          .append(" = ")
          .append(entry.getValue());
    }
    
    builder.append("}}</ref>");
    
    return builder.toString();
  }
}

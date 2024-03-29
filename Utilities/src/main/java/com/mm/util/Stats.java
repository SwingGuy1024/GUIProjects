package com.mm.util;

/**
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 3/27/24</p>
 * <p>Time: 2:54 AM</p>
 * <p>@author Miguel Muñoz</p>
 */
@SuppressWarnings("unused")
public class Stats {
  private double sumX = 0.0;
  private double sumXSquared = 0.0;
  private long count;
  
  public void add(double value) {
    sumX += value;
    sumXSquared += value * value;
    count++;
  }
  
  public double mean() {
    return sumX/count;
  }
  
  public double stDev() {
    return StrictMath.sqrt((sumXSquared - ((sumX * sumX) / count)) / (count - 1L));
  }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.general;

import csk.taprats.geometry.Point;

@SuppressWarnings({"MagicNumber", "UnclearExpression", "unused", "ReassignedVariable", "RedundantThrows", "NonFinalUtilityClass", "FinalStaticMethod", "UtilityClassWithPublicConstructor", "UtilityClassWithoutPrivateConstructor"})
public class Loose {
  public static final double TOL = 1.0E-7D;
  public static final double TOL2 = 1.0E-10D;

  public Loose() {
  }

  public static final boolean equals(double var0, double var2) {
    return Math.abs(var0 - var2) < 1.0E-7D;
  }

  public static final boolean zero(double var0) {
    return Math.abs(var0) < 1.0E-7D;
  }

  public static final boolean lessThan(double var0, double var2) {
    return var0 < var2 + 1.0E-7D;
  }

  public static final boolean greaterThan(double var0, double var2) {
    return var0 > var2 - 1.0E-7D;
  }

  public static final boolean equals(Point var0, Point var1) {
    return var0.dist2(var1) < 1.0E-10D;
  }

  public static final boolean zero(Point var0) {
    return var0.mag2() < 1.0E-10D;
  }

  public static final boolean isInteger(double var0) {
    return equals(var0, Math.rint(var0));
  }

  public static final double toDegrees(double var0) {
    while (var0 < -3.141592653589793D) {
      var0 += 6.283185307179586D;
    }

    while (var0 > 3.141592653589793D) {
      var0 -= 6.283185307179586D;
    }

    return Math.toDegrees(var0);
  }

  public static final int mod(int var0, int var1) {
    int var2 = var0 % var1;
    return var2 < 0 ? var1 + var2 : var2;
  }
}

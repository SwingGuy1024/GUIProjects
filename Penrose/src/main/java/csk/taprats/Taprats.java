//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats;

import java.util.StringTokenizer;

@SuppressWarnings({"unused", "UnnecessaryConstantArrayCreationExpression", "ReassignedVariable", "RedundantThrows", "StringConcatenation", "MagicCharacter", "OverlyBroadCatchBlock", "NonFinalUtilityClass", "FinalStaticMethod", "UtilityClassWithPublicConstructor", "UtilityClassWithoutPrivateConstructor", "ThrowInsideCatchBlockWhichIgnoresCaughtException", "UnnecessaryLocalVariable", "StringBufferMayBeStringBuilder", "UnusedReturnValue"})
public class Taprats {
  public static final String NAME = "Taprats";
  public static final int MAJOR_VERSION = 0;
  public static final int MINOR_VERSION = 5;
  public static final int MICRO_VERSION = 0;
  public static final String AUTHOR = "Craig S. Kaplan";
//  public static boolean IS_APPLET = false;
//  public static boolean DEBUG = false;

  public Taprats() {
  }

  public static final String getVersionString() {
    StringBuffer var0 = new StringBuffer();
    var0.append(0);
    var0.append('.');
    var0.append(5);
    return var0.toString();
  }

  public static final int[] parseVersionString(String var0) {
    try {
      StringTokenizer var1 = new StringTokenizer(var0, ".");
      String var2 = var1.nextToken();
      String var3 = "0";
      String var4 = "0";
      if (var1.hasMoreTokens()) {
        var3 = var1.nextToken();
      }

      if (var1.hasMoreTokens()) {
        var4 = var1.nextToken();
      }

      int[] var5 = new int[]{Integer.parseInt(var2), Integer.parseInt(var3), Integer.parseInt(var4)};
      return var5;
    } catch (Exception var6) {
      throw new IllegalArgumentException(var0 + " is not a version string.");
    }
  }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.toolkit;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.util.Hashtable;

public class LoadSave {
  private static Hashtable last_dir = new Hashtable();

  public LoadSave() {
  }

  private static final String doLoadSave(Frame var0, String var1, String var2, int var3) {
    FileDialog var4 = new FileDialog(var0, var1, var3);
    String var5;
    if (last_dir.containsKey(var2)) {
      var5 = (String) last_dir.get(var2);
      var4.setDirectory(var5);
    }

    var4.show();
    var5 = var4.getFile();
    String var6 = var4.getDirectory();
    if (var6 != null) {
      last_dir.put(var2, var6);
    }

    return var5 != null ? var6 + var5 : null;
  }

  public static final String doLoad(Frame var0, String var1, String var2) {
    return doLoadSave(var0, var1, var2, 0);
  }

  public static final String doSave(Frame var0, String var1, String var2) {
    return doLoadSave(var0, var1, var2, 1);
  }

  public static final String doLoadVerify(Frame var0, String var1, String var2) {
    while (true) {
      String var3 = doLoad(var0, var1, var2);
      if (var3 == null) {
        return null;
      }

      File var4 = new File(var3);
      if (var4.canRead()) {
        return var3;
      }

      Warn var5 = new Warn(var0, "File not found: " + var3);
      var5.pack();
      var5.show();
    }
  }

  public static final String doSaveVerify(Frame var0, String var1, String var2) {
    while (true) {
      String var3 = doSave(var0, var1, var2);
      if (var3 != null) {
        File var4 = new File(var3);
        if (var4.canRead()) {
          Warn var5 = new Warn(var0, "Overwrite file " + var3 + "?");
          var5.addCancel();
          var5.pack();
          var5.show();
          if (!var5.okPressed()) {
            continue;
          }

          return var3;
        }

        return var3;
      }

      return null;
    }
  }
}

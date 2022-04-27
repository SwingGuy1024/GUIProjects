//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.penrose;

import csk.taprats.Taprats;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;

public class PenroseApplet extends Applet {
  public PenroseApplet() {
  }

  public void init() {
    Taprats.IS_APPLET = true;
    KiteDart var1 = new KiteDart();
    PenroseApp var2 = new PenroseApp(var1, (Frame) null, true);
    this.setLayout(new BorderLayout());
    this.add("Center", var2);
  }
}

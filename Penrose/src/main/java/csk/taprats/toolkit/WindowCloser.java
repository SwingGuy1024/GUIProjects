//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.toolkit;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowCloser extends WindowAdapter implements ActionListener {
  private Window window;
  private boolean quit;

  public WindowCloser(Window var1, boolean var2) {
    this.window = var1;
    this.quit = var2;
  }

  public WindowCloser(Window var1) {
    this(var1, false);
  }

  public void windowClosing(WindowEvent var1) {
    this.window.setVisible(false);
    this.window.dispose();
    if (this.quit) {
      System.exit(0);
    }

  }

  public void actionPerformed(ActionEvent var1) {
    this.window.setVisible(false);
    this.window.dispose();
    if (this.quit) {
      System.exit(0);
    }

  }
}

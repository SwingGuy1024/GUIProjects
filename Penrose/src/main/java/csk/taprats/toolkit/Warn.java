//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.toolkit;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("PackageVisibleField")
class Warn extends Dialog {
  JPanel sub;
  boolean ok_pressed;

  Warn(Frame var1, String var2) {
    super(var1, var2, true);
    this.setLayout(new BorderLayout());
    this.ok_pressed = false;
    JLabel var3 = new JLabel(var2);
    this.add("Center", var3);
    JButton var4 = new JButton("OK");
    this.sub = new JPanel();
    this.sub.setLayout(new FlowLayout());
    this.sub.add(var4);
    this.add("South", this.sub);
    var4.addActionListener(var11 -> {
      Warn.this.ok_pressed = true;
      Warn.this.dispose();
    });
  }

  void addCancel() {
    JButton var1 = new JButton("Cancel");
    this.sub.add(var1);
    var1.addActionListener(var11 -> Warn.this.dispose());
  }

  boolean okPressed() {
    return this.ok_pressed;
  }
}

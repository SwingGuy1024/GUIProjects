//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.toolkit;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Warn extends Dialog {
  Panel sub;
  boolean ok_pressed;

  Warn(Frame var1, String var2) {
    super(var1, var2, true);
    this.setLayout(new BorderLayout());
    this.ok_pressed = false;
    Label var3 = new Label(var2);
    this.add("Center", var3);
    Button var4 = new Button("OK");
    this.sub = new Panel();
    this.sub.setLayout(new FlowLayout());
    this.sub.add(var4);
    this.add("South", this.sub);
    var4.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent var1) {
        Warn.this.ok_pressed = true;
        Warn.this.dispose();
      }
    });
  }

  void addCancel() {
    Button var1 = new Button("Cancel");
    this.sub.add(var1);
    var1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent var1) {
        Warn.this.dispose();
      }
    });
  }

  boolean okPressed() {
    return this.ok_pressed;
  }
}

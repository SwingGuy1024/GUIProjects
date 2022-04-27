//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.toolkit;

import csk.taprats.general.Signal;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class Slider {
  private Label name;
  private Scrollbar scroll;
  private TextField field;
  private double min;
  private double max;
  private double val;
  private boolean integral;
  public Signal value_changed;

  public Slider(String var1, double var2, double var4, double var6) {
    this.val = var2;
    this.min = var4;
    this.max = var6;
    this.integral = false;
    this.name = new Label(var1);
    this.field = new TextField(this.ftoa(var2), 9);
    this.scroll = new Scrollbar(0, this.getSlideLoc(var2), 1, 0, 257);
    this.value_changed = new Signal();
    this.scroll.addAdjustmentListener(new AdjustmentListener() {
      public void adjustmentValueChanged(AdjustmentEvent var1) {
        Slider.this.updateFromSlider();
      }
    });
    this.field.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent var1) {
        Slider.this.updateFromField();
      }
    });
  }

  private void updateFromSlider() {
    double var1 = this.getValue(this.scroll.getValue());
    if (var1 != this.val) {
      this.field.setText(this.ftoa(var1));
      this.val = var1;
      this.value_changed.signotify(new Double(this.val));
    }

  }

  private void updateFromField() {
    try {
      double var1 = 0.0D;
      if (this.integral) {
        var1 = (double) Integer.parseInt(this.field.getText());
      } else {
        var1 = new Double(this.field.getText());
      }

      if (var1 < this.min) {
        var1 = this.min;
      } else if (var1 > this.max) {
        var1 = this.max;
      }

      if (var1 != this.val) {
        this.field.setText(this.ftoa(var1));
        this.scroll.setValue(this.getSlideLoc(var1));
        this.val = var1;
        this.value_changed.signotify(new Double(var1));
      }
    } catch (NumberFormatException var3) {
      this.field.setText(this.ftoa(this.val));
    }

  }

  private String ftoa(double var1) {
    if (this.integral) {
      return String.valueOf((int) var1);
    } else {
      String var3 = String.valueOf(var1);
      int var4 = var3.lastIndexOf(46);
      if (var4 != -1) {
        int var5 = var3.length() - (var4 + 1);
        if (var5 > 5) {
          var3 = var3.substring(0, var4 + 6);
        }
      }

      return var3;
    }
  }

  private int getSlideLoc(double var1) {
    return (int) (256.0D * (var1 - this.min) / (this.max - this.min));
  }

  private double getValue(int var1) {
    return this.min + (double) var1 / 256.0D * (this.max - this.min);
  }

  public double getValue() {
    return this.val;
  }

  public void setValue(double var1, boolean var3) {
    if (var1 < this.min) {
      var1 = this.min;
    } else if (var1 > this.max) {
      var1 = this.max;
    }

    this.scroll.setValue(this.getSlideLoc(var1));
    this.field.setText(this.ftoa(var1));
    this.val = var1;
    if (var3) {
      this.value_changed.signotify(new Double(var1));
    }

  }

  public void setValue(double var1) {
    this.setValue(var1, true);
  }

  public void setValues(double var1, double var3, double var5, boolean var7) {
    this.min = var3;
    this.max = var5;
    this.setValue(var1, var7);
  }

  public void setValues(double var1, double var3, double var5) {
    this.setValues(var1, var3, var5, true);
  }

  public void setIntegral(boolean var1) {
    this.integral = var1;
  }

  public Panel getAsPanel() {
    Panel var1 = new Panel();
    GridBagLayout var2 = new GridBagLayout();
    var1.setLayout(var2);
    this.insert(var1, var2, 0, 0);
    return var1;
  }

  public void insert(Container var1, GridBagLayout var2, int var3, int var4) {
    GridBagConstraints var5 = new GridBagConstraints();
    var5.gridx = var3;
    var5.gridy = var4;
    var5.gridwidth = 1;
    var5.gridheight = 1;
    var5.weightx = 1.0D;
    var5.weighty = 1.0D;
    var5.anchor = 13;
    var5.fill = 0;
    var2.setConstraints(this.name, var5);
    var1.add(this.name);
    var5.gridx = var3 + 1;
    var5.weightx = 10.0D;
    var5.anchor = 10;
    var5.fill = 2;
    var2.setConstraints(this.scroll, var5);
    var1.add(this.scroll);
    var5.gridx = var3 + 2;
    var5.weightx = 1.0D;
    var5.anchor = 17;
    var5.fill = 0;
    var2.setConstraints(this.field, var5);
    var1.add(this.field);
  }
}

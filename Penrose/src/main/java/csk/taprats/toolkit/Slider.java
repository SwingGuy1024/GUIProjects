//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.toolkit;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;

import csk.taprats.general.Signal;

@SuppressWarnings({"MagicNumber", "NumericCastThatLosesPrecision", "UnclearExpression", "UnusedAssignment", "unused", "RedundantCast", "ReassignedVariable", "RedundantThrows", "Convert2Lambda", "override", "FieldMayBeFinal", "FloatingPointEquality", "UnusedReturnValue", "UnnecessaryExplicitNumericCast", "PublicField"})
public class Slider {
  public static final char DOT = '.';
  private JLabel name;
  private JScrollBar scroll;
  private JTextField field;
  private double min;
  private double max;
  private double val;
  private boolean integral;
  public Signal value_changed;

  public Slider(String text, double var2, double var4, double var6) {
    this.val = var2;
    this.min = var4;
    this.max = var6;
    this.integral = false;
    this.name = new JLabel(text);
    this.field = new JTextField(this.fToa(var2), 9);
    this.scroll = new JScrollBar(JScrollBar.HORIZONTAL, this.getSlideLoc(var2), 1, 0, 257);
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
      this.field.setText(this.fToa(var1));
      this.val = var1;
      this.value_changed.signotify(this.val);
    }
  }

  private void updateFromField() {
    try {
      double var1 = 0.0D;
      if (this.integral) {
        var1 = (double) Integer.parseInt(this.field.getText());
      } else {
        var1 = Double.parseDouble(this.field.getText());
      }

      if (var1 < this.min) {
        var1 = this.min;
      } else if (var1 > this.max) {
        var1 = this.max;
      }

      if (var1 != this.val) {
        this.field.setText(this.fToa(var1));
        this.scroll.setValue(this.getSlideLoc(var1));
        this.val = var1;
        this.value_changed.signotify(var1);
      }
    } catch (NumberFormatException var3) {
      this.field.setText(this.fToa(this.val));
    }

  }

  /**
   * Returns the decimal value as a string, limiting the number of fractional digits to 5.
   * @param dbl The double value
   * @return The double value as a String.
   */
  private String fToa(double dbl) {
    if (this.integral) {
      return String.valueOf((int) dbl);
    } else {
      String text = String.valueOf(dbl);
      int dotSpot = text.lastIndexOf(DOT);
      if (dotSpot != -1) {
        int fracDigits = text.length() - (dotSpot + 1);
        if (fracDigits > 5) {
          text = text.substring(0, dotSpot + 6);
        }
      }

      return text;
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
    this.field.setText(this.fToa(var1));
    this.val = var1;
    if (var3) {
      this.value_changed.signotify(var1);
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

  public JPanel getAsPanel() {
    JPanel var1 = new JPanel();
    GridBagLayout var2 = new GridBagLayout();
    var1.setLayout(var2);
    this.insert(var1, var2, 0, 0);
    return var1;
  }

  public void insert(JComponent var1, GridBagLayout gbLayout, int var3, int var4) {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = var3;
    constraints.gridy = var4;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weightx = 1.0D;
    constraints.weighty = 1.0D;
    constraints.anchor = 13;
    constraints.fill = 0;
    gbLayout.setConstraints(this.name, constraints);
    var1.add(this.name);
    constraints.gridx = var3 + 1;
    constraints.weightx = 10.0D;
    constraints.anchor = 10;
    constraints.fill = 2;
    gbLayout.setConstraints(this.scroll, constraints);
    var1.add(this.scroll);
    constraints.gridx = var3 + 2;
    constraints.weightx = 1.0D;
    constraints.anchor = 17;
    constraints.fill = 0;
    gbLayout.setConstraints(this.field, constraints);
    var1.add(this.field);
  }
}

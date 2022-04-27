//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats;

import csk.taprats.toolkit.WindowCloser;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.image.ImageProducer;
import java.net.URL;

public class About extends Frame {
  private ImageProducer producer;
  private Image image;
  private About.AboutCanvas canvas;
  private GridBagLayout layout;
  private Label big1;
  private Label big2;

  public About() {
    super("About Taprats");
    this.setBackground(Color.white);
    this.canvas = new About.AboutCanvas();
    this.layout = new GridBagLayout();
    this.setLayout(this.layout);
    WindowCloser var1 = new WindowCloser(this, false);
    this.addWindowListener(var1);
    GridBagConstraints var2 = new GridBagConstraints();
    var2.gridx = 0;
    var2.gridy = 0;
    var2.gridwidth = 1;
    var2.gridheight = 1;
    var2.anchor = 10;
    var2.fill = 1;
    var2.weighty = 3.0D;
    this.layout.setConstraints(this.canvas, var2);
    this.add(this.canvas);
    var2.fill = 0;
    var2.weighty = 1.0D;
    Label var3 = new Label("Taprats version " + Taprats.getVersionString(), 1);
    this.big1 = var3;
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new Label("By Craig S. Kaplan", 1);
    this.big2 = var3;
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new Label(" ");
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new Label("See the README file in the distribution", 1);
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new Label("for more information.", 1);
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new Label(" ");
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new Label("Taprats is free for non-commercial purposes.", 1);
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new Label("See the file LICENSE for more information.", 1);
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new Label(" ");
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    String var4 = "http://www.cs.washington.edu/homes/csk/taprats/";
    var3 = new Label(var4, 1);
    var3.setForeground(Color.blue);
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new Label(" ");
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    Button var5 = new Button("OK");
    var5.addActionListener(var1);
    ++var2.gridy;
    this.layout.setConstraints(var5, var2);
    this.add(var5);
  }

  public void addNotify() {
    super.addNotify();

    try {
      URL var1 = this.getClass().getResource("aboutbox.jpg");
      this.producer = (ImageProducer) var1.getContent();
    } catch (Exception var4) {
      System.err.println(var4);
    }

    this.image = this.canvas.createImage(this.producer);
    MediaTracker var5 = new MediaTracker(this.canvas);
    var5.addImage(this.image, 0);

    try {
      var5.waitForAll();
      this.canvas.setSize(this.image.getWidth(this.canvas), this.image.getHeight(this.canvas));
    } catch (InterruptedException var3) {
      this.canvas.setSize(256, 256);
    }

    this.big1.setFont(new Font(this.getFont().getFontName(), 1, this.getFont().getSize() + 2));
    this.big2.setFont(new Font(this.getFont().getFontName(), 1, this.getFont().getSize() + 2));
  }

  class AboutCanvas extends Canvas {
    AboutCanvas() {
    }

    public void update(Graphics var1) {
      this.paint(var1);
    }

    public void paint(Graphics var1) {
      Dimension var2 = this.getSize();
      if (About.this.image != null) {
        int var3 = About.this.image.getWidth(this);
        var1.drawImage(About.this.image, (var2.width - var3) / 2, 0, this);
      }

    }

    public void addNotify() {
      super.addNotify();
    }
  }
}

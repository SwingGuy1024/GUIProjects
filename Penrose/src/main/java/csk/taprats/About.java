//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.ImageProducer;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import csk.taprats.toolkit.WindowCloser;

/**
 * <p>This class crashes. I haven't bothered to fix it because it isn't used. It crashes in its (strange?) use of MediaTracker, which I've never used, and I don't understand its purpose here. I don't think it's being used correctly, given that its use of the image member seems circular. They may have never figured out how to do this.</p>
 * 
 */
@SuppressWarnings({"MagicNumber", "unused", "ImplicitCallToSuper", "ReassignedVariable", "RedundantThrows", "StringConcatenation", "OverlyBroadCatchBlock", "override", "FieldCanBeLocal", "DataFlowIssue", "FieldMayBeFinal", "UnusedReturnValue"})
public class About extends JFrame {
  private ImageProducer producer;
  private Image image;
  private AboutCanvas canvas;
  private GridBagLayout layout;
  private JLabel big1;
  private JLabel big2;

  public About() {
    super("About Taprats");
    this.setBackground(Color.white);
    this.canvas = new AboutCanvas();
    this.layout = new GridBagLayout();
    this.setLayout(this.layout);
    WindowCloser var1 = new WindowCloser(this, false);
    this.addWindowListener(var1);
    GridBagConstraints var2 = new GridBagConstraints();
    var2.gridx = 0;
    var2.gridy = 0;
    var2.gridwidth = 1;
    var2.gridheight = 1;
    var2.anchor = GridBagConstraints.CENTER;
    var2.fill = GridBagConstraints.BOTH;
    var2.weighty = 3.0D;
    this.layout.setConstraints(this.canvas, var2);
    this.add(this.canvas);
    var2.fill = 0;
    var2.weighty = 1.0D;
    JLabel var3 = new JLabel("Taprats version " + Taprats.getVersionString());
    this.big1 = var3;
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new JLabel("By Craig S. Kaplan");
    this.big2 = var3;
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new JLabel(" ");
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new JLabel("See the README file in the distribution");
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new JLabel("for more information.");
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new JLabel(" ");
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new JLabel("Taprats is free for non-commercial purposes.");
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new JLabel("See the file LICENSE for more information.");
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new JLabel(" ");
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    String var4 = "http://www.cs.washington.edu/homes/csk/taprats/";
    var3 = new JLabel(var4);
    var3.setForeground(Color.blue);
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    var3 = new JLabel(" ");
    ++var2.gridy;
    this.layout.setConstraints(var3, var2);
    this.add(var3);
    JButton var5 = new JButton("OK");
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
      var4.printStackTrace();
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

    this.big1.setFont(new Font(this.getFont().getFontName(), Font.BOLD, this.getFont().getSize() + 2));
    this.big2.setFont(new Font(this.getFont().getFontName(), Font.BOLD, this.getFont().getSize() + 2));
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

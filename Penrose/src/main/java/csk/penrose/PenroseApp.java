//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.penrose;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import csk.taprats.general.ParseXML;
import csk.taprats.general.XMLParseError;
import csk.taprats.toolkit.LoadSave;
import csk.taprats.toolkit.Slider;
import csk.taprats.toolkit.WindowCloser;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

@SuppressWarnings({"MagicNumber", "ProtectedField", "unused", "ImplicitCallToSuper", "ReassignedVariable", 
    "RedundantThrows", "MagicCharacter", "Convert2Lambda", "deprecation", "OverlyBroadCatchBlock", "override"})
public class PenroseApp extends JPanel implements App {
  public static final Color STANDARD_COLOR = new Color(80, 144, 255);   // was  80,  95, 255
  public static final Color BRIGHTER_COLOR = new Color(114, 166, 255); // was 114, 126, 255
  protected PenroseTile tile;
  protected Viewer viewer;
  protected Editor edit_first;
  protected Editor edit_second;
  protected Slider s1;
  protected Slider s2;
  protected Slider s3;
  protected Slider s4;
  protected JPopupMenu menu;
  protected JFrame parent;

  public PenroseApp(PenroseTile var1, JFrame var2) {
    this(var1, var2, false);
  }

  public PenroseApp(PenroseTile penroseTile, JFrame frame, boolean var3) {
    this.tile = penroseTile;
    this.parent = frame;
    this.viewer = new Viewer(penroseTile);
    this.viewer.setSize(400, 400);
    this.viewer.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyChar() == 's') {
          System.err.println("Writing to output.txt");

          try {
            FileWriter fileWriter = new FileWriter("output.txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            PenroseApp.this.viewer.emitShapes(printWriter);
            printWriter.flush();
            fileWriter.close();
          } catch (IOException ignore) {
          }
        }

      }
    });
    this.edit_first = new Editor(penroseTile, true);
    this.edit_first.setSize(200, 200);
    this.edit_first.setApp(this);
    this.edit_second = new Editor(penroseTile, false);
    this.edit_second.setSize(200, 200);
    this.edit_second.setApp(this);
    this.s1 = new Slider("P1", 0.0D, 0.0D, 1.0D);
    this.s2 = new Slider("P2", 0.0D, 0.0D, 1.0D);
    this.s3 = new Slider("P3", 0.0D, 0.0D, 1.0D);
    this.s4 = new Slider("P4", 0.0D, 0.0D, 1.0D);
    JPanel sliderPanel = new JPanel();
    GridBagLayout sLayout = new GridBagLayout();
    sliderPanel.setLayout(sLayout);
    this.s1.insert(sliderPanel, sLayout, 0, 0);
    this.s2.insert(sliderPanel, sLayout, 0, 1);
    this.s3.insert(sliderPanel, sLayout, 0, 2);
    this.s4.insert(sliderPanel, sLayout, 0, 3);
    this.s1.value_changed.addObserver(new Observer() {
      public void update(Observable var1, Object var2) {
        PenroseApp.this.tile.setParameter(0, (Double) var2);
        PenroseApp.this.doUpdate();
      }
    });
    this.s2.value_changed.addObserver(new Observer() {
      public void update(Observable var1, Object var2) {
        PenroseApp.this.tile.setParameter(1, (Double) var2);
        PenroseApp.this.doUpdate();
      }
    });
    this.s3.value_changed.addObserver(new Observer() {
      public void update(Observable var1, Object var2) {
        PenroseApp.this.tile.setParameter(2, (Double) var2);
        PenroseApp.this.doUpdate();
      }
    });
    this.s4.value_changed.addObserver(new Observer() {
      public void update(Observable var1, Object var2) {
        PenroseApp.this.tile.setParameter(3, (Double) var2);
        PenroseApp.this.doUpdate();
      }
    });
    GridBagLayout mainLayout = new GridBagLayout();
    this.setLayout(mainLayout);
    GridBagConstraints gbConstraints = new GridBagConstraints();
    JButton newP2Button = new JButton("New P2");
    newP2Button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actonEvent) {
        PenroseApp.this.doNewP2();
      }
    });
    JButton newP3Button = new JButton("New P3");
    newP3Button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actonEvent) {
        PenroseApp.this.doNewP3();
      }
    });
    JButton saveAsButton = new JButton("Save As...");
    saveAsButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actonEvent) {
        PenroseApp.this.doSaveAs();
      }
    });
    JButton loadButton = new JButton("Load...");
    loadButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actonEvent) {
        PenroseApp.this.doLoad();
      }
    });
    JButton quitButton = new JButton("Quit");
    quitButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actonEvent) {
        System.exit(0);
      }
    });
    gbConstraints.gridx = 0;
    gbConstraints.gridy = 0;
    gbConstraints.gridwidth = 2;
    gbConstraints.gridheight = 1;
    gbConstraints.weightx = 1.0D;
    gbConstraints.weighty = 0.0D;
    gbConstraints.anchor = 10;
    gbConstraints.fill = 2;
    JPanel buttonPanel = new JPanel();
    if (var3) {
      buttonPanel.setLayout(new GridLayout(1, 2));
      buttonPanel.add(newP2Button);
      buttonPanel.add(newP3Button);
    } else {
      buttonPanel.setLayout(new GridLayout(1, 5));
      buttonPanel.add(newP2Button);
      buttonPanel.add(newP3Button);
      buttonPanel.add(saveAsButton);
      buttonPanel.add(loadButton);
      buttonPanel.add(quitButton);
    }

    mainLayout.setConstraints(buttonPanel, gbConstraints);
    this.add(buttonPanel);
    gbConstraints.gridx = 0;
    gbConstraints.gridy = 1;
    gbConstraints.gridwidth = 1;
    gbConstraints.gridheight = 1;
    gbConstraints.weightx = 2.0D;
    gbConstraints.weighty = 2.0D;
    gbConstraints.anchor = 10;
    gbConstraints.fill = 1;
    mainLayout.setConstraints(this.edit_first, gbConstraints);
    this.add(this.edit_first);
    gbConstraints.gridy = 2;
    mainLayout.setConstraints(this.edit_second, gbConstraints);
    this.add(this.edit_second);
    gbConstraints.gridx = 1;
    gbConstraints.gridy = 1;
    gbConstraints.gridheight = 2;
    gbConstraints.weightx = 4.0D;
    mainLayout.setConstraints(this.viewer, gbConstraints);
    this.add(this.viewer);
    gbConstraints.gridx = 0;
    gbConstraints.gridy = 3;
    gbConstraints.gridwidth = 2;
    gbConstraints.fill = 2;
    gbConstraints.weighty = 0.0D;
    mainLayout.setConstraints(sliderPanel, gbConstraints);
    this.add(sliderPanel);
    this.menu = new JPopupMenu("Penrose");
    JMenuItem saveAsItem = new JMenuItem("Save As...");
    saveAsItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent var1) {
        PenroseApp.this.doSaveAs();
      }
    });
    this.menu.add(saveAsItem);
    saveAsItem = new JMenuItem("Quit");
    saveAsItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent var1) {
        System.exit(0);
      }
    });
    this.menu.add(saveAsItem);
    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        doPop(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        doPop(e);
      }

      private void doPop(MouseEvent e) {
        if (e.isPopupTrigger()) {
          menu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });
//    this.viewer.add(this.menu);
    if (penroseTile instanceof KiteDart) {
      this.viewer.loadConfig(Places.p2_first, Places.p2_second);
    } else {
      this.viewer.loadConfig(Places.p3_first, Places.p3_second);
    }

  }

  public Viewer getViewer() {
    return this.viewer;
  }

  protected void doSaveAs() {
    String var1 = LoadSave.doSaveVerify(this.parent, "Save Penrose Tile", "save_penrose");
    if (var1 != null) {
      try {
        FileWriter var2 = new FileWriter(var1);
        BufferedWriter var3 = new BufferedWriter(var2);
        PrintWriter var4 = new PrintWriter(var3);
        var4.println("<?xml version=\"1.0\"?>\n<?penrose 0.1?>\n");
        this.tile.write(var4, "");
        var4.flush();
        var2.close();
      } catch (IOException var5) {
        System.err.println("Could not save Penrose tile");
      }
    }

  }

  protected void doLoad() {
    String path = LoadSave.doLoadVerify(this.parent, "Load Penrose Tile", "load_penrose");
    if (path != null) {
      try {
        FileInputStream var2 = new FileInputStream(path);
        BufferedInputStream var3 = new BufferedInputStream(var2);
        PenroseTile var4 = readTile(var3);
        var2.close();
        this.setTiling(var4);
      } catch (IOException ex) {
        System.err.println("Unable to read " + path);
      } catch (XMLParseError var6) {
        System.err.println("Malformed penrose file: " + var6);
      }
    }

  }

  protected void doNewP2() {
    this.setTiling(new KiteDart());
  }

  protected void doNewP3() {
    this.setTiling(new Rhombs());
  }

  protected void setTiling(PenroseTile var1) {
    this.tile = var1;
    this.viewer.setTile(var1);
    this.edit_first.setTile(var1);
    this.edit_second.setTile(var1);
    this.s1.setValue(var1.getParameter(0));
    this.s2.setValue(var1.getParameter(1));
    this.s3.setValue(var1.getParameter(2));
    this.s4.setValue(var1.getParameter(3));
  }

  @Override
  public void doUpdate() {
    this.viewer.forceRedraw();
    this.edit_first.forceRedraw();
    this.edit_second.forceRedraw();
  }

  public static PenroseTile readTile(InputStream var0) throws XMLParseError {
    return readTile(new ParseXML(var0));
  }

  public static PenroseTile readTile(ParseXML var0) throws XMLParseError {
    ProcessingInstruction node = var0.getPINode();
    if (node == null) {
      throw new XMLParseError("Document has no PI node.");
    } else if (!"penrose".equals(node.getNodeName())) {
      throw new XMLParseError("Document is not a Penrose file.");
    } else {
      Element var2 = var0.beginDocument();
      ParseXML.verifyElementName(var2, "penrose");
      String variation = ParseXML.getProp(var2, "type");
      if ("P2".equals(variation)) {
        return new KiteDart(var2, var0);
      } else if ("P3".equals(variation)) {
        return new Rhombs(var2, var0);
      } else {
        throw new XMLParseError("Unrecognized type of Penrose tile.");
      }
    }
  }

  public static void main(String[] var0) {
    KiteDart var1 = new KiteDart();
    JFrame var2 = new JFrame("Penrose Tile Editor");
    PenroseApp var3 = new PenroseApp(var1, var2, false);
    var2.setLayout(new BorderLayout());
    var2.add("Center", var3);
    var2.addWindowListener(new WindowCloser(var2, true));
    var2.pack();
    var2.setVisible(true);
  }
}

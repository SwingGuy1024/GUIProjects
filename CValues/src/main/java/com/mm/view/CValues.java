package com.mm.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;

/*
Wordle 210 3/6

?????
?????
?????
 */

/**
 * GUI utility to show the numeric values of any text character that get pasted in.
 * <p> Requires:
 * <br>com.go2.ui.platform.Platform
 * <br>com.go2.ui.platform.unix.UnixPlatform
 * <br>com.go2.ui.platform.windows.WindowsPlatform
 */
public class CValues //extends JPanel
  implements
    DocumentListener
{
	private static final Font sMonoFnt = new Font("Monospaced", Font.PLAIN, 10);
	private JLabel mDisplay;

	public static void main(String[] args)
  {
    System.out.println("Java version " + System.getProperty("java.version"));
//    try { UIManager.setLookAndFeel(new MetalLookAndFeel()); }
//    catch (Exception err) { }
    try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
    catch (Exception err) { /* Do Nothing */ }
    JFrame mf = new JFrame("Character Values");
    mf.addWindowListener(new WindowAdapter()
      { public void windowClosing(WindowEvent evt) { System.exit(0); } } );
    mf.setBounds(20, 20, 500, 400);
    Container cp = mf.getContentPane();
    CValues cval = new CValues(mf);
//    cp.add(cval, BorderLayout.CENTER);
    mf.setVisible(true);
  }
  
  protected JTextArea myTextView = new JTextArea();
  protected JTextArea myCharView = new JTextArea();
  
  public CValues(RootPaneContainer rpc)
  {
    Container cp = rpc.getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(makeViewPanel(), BorderLayout.CENTER);
    Action cut = new DefaultEditorKit.CutAction();
    Action copy = new DefaultEditorKit.CopyAction();
    Action paste = new DefaultEditorKit.PasteAction()
      {
        public void actionPerformed(ActionEvent evt)
        {
          super.actionPerformed(evt);
          myTextView.requestFocus();
        }
      };

    Action exit = new AbstractAction("Exit")
      {
        public void actionPerformed(ActionEvent evt)
        {
          System.exit(0);
        }
      };
    Action courierFont = new AbstractAction("Courier")
    {
      public void actionPerformed(ActionEvent evt) { setCourier(); }
    };
    Action defFont = new AbstractAction("Default Font")
    {
      public void actionPerformed(ActionEvent evt) { setDefaultFont(); }
    };
    Action arielFont = new AbstractAction("Ariel")
    {
      public void actionPerformed(ActionEvent evt) { setAriel(); }
    };
    exit.putValue(Action.MNEMONIC_KEY, new Integer('X'));
    cut.putValue(Action.MNEMONIC_KEY, new Integer('T'));
    copy.putValue(Action.MNEMONIC_KEY, new Integer('C'));
    paste.putValue(Action.MNEMONIC_KEY, new Integer('P'));
    cut.putValue(Action.NAME, "Cut");
    copy.putValue(Action.NAME, "Copy");
    paste.putValue(Action.NAME, "Paste");
    JToolBar tb = new JToolBar();
    tb.add(cut);
    tb.add(copy);
    tb.add(paste);
    tb.addSeparator();
    tb.add(courierFont);
    tb.add(defFont);
    tb.add(arielFont);
    cp.add(tb, BorderLayout.NORTH);
    JMenuBar mb = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    Action openAction = new Opener();
    fileMenu.add(openAction);
    fileMenu.add(exit);
    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic('E');
	  int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    editMenu.add(cut).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, shortcutMask));
    editMenu.add(copy).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, shortcutMask));
    editMenu.add(paste).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, shortcutMask));
    JMenu fontMenu = new JMenu("Font");
    fontMenu.setMnemonic('o');
    fontMenu.add(courierFont);
    fontMenu.add(defFont);
    fontMenu.add(arielFont);
    addSizeActions(fontMenu);
    editMenu.addSeparator();
    mb.add(fileMenu);
    mb.add(editMenu);
    mb.add(fontMenu);
    rpc.getRootPane().setJMenuBar(mb);
	  cp.add(makeDisplayPanel(), BorderLayout.PAGE_END);
  }

	private JComponent makeDisplayPanel() {
		mDisplay = new JLabel(" ");
		return mDisplay;
	}

	private JComponent makeViewPanel()
  {
    JSplitPane vPanel = new JSplitPane();
//    vPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
    JScrollPane topView = new JScrollPane(myTextView);
    JScrollPane botView = new JScrollPane(myCharView);
    myCharView.setFont(sMonoFnt);
    myCharView.setEditable(false);
    vPanel.setBottomComponent(topView);
    vPanel.setTopComponent(botView);
    vPanel.setDividerLocation(160);
    myTextView.getDocument().addDocumentListener(this);
    myTextView.requestFocus();
    myTextView.setLineWrap(true);
    myTextView.setWrapStyleWord(true);
    return vPanel;
  }

  public void insertUpdate(DocumentEvent e) { changed(e); }
  public void removeUpdate(DocumentEvent e) { changed(e); }
  public void changedUpdate(DocumentEvent e) { changed(e); }
  
  private void changed(DocumentEvent evt)
  {
	  final int length = evt.getDocument().getLength();
    try
    {
	    String doc = evt.getDocument().getText(0, length);
      StringBuffer sb = new StringBuffer();
      for (int ii=0; ii<doc.length(); ++ii)
      {
        char theChar = doc.charAt(ii);
        if (theChar < 32)
        {
          switch (theChar)
          {
            case '\r':
              sb.append("\\r");
              break;
            case '\n':
              sb.append("\\n");
              break;
            case '\t':
              sb.append("\\t");
              break;
            default:
              sb.append(' ');
              sb.append((char)1); // will show up as a box.
//              sb.append(" ");
              break;
          }
        }
        else
        {
          sb.append(' ');
          sb.append(theChar);
        }
        sb.append(" = 0x");
        sb.append(pad(Integer.toHexString(theChar), 4));
        sb.append(" = ");
        sb.append((int)theChar);
        sb.append('\n');
      }
      myCharView.setText(sb.toString());
    }
    catch (BadLocationException ble)
    {
      ble.printStackTrace();
    }
	  mDisplay.setText(String.format("Length: %s characters", length));
  }
  
  private static String pad(String input, int maxLen)
  {
    StringBuffer bf = new StringBuffer(input);
    while (bf.length() < maxLen)
      bf.insert(0, '0');
    return bf.toString();
  }
  
  private void setCourier()
  {
    float oldSize = myTextView.getFont().getSize2D();
    Font newFont = sMonoFnt.deriveFont(oldSize);
    myTextView.setFont(newFont);
  }

  private void setDefaultFont()
  {
    int oldSize = myTextView.getFont().getSize();
    myTextView.setFont( new Font("monospaced", Font.PLAIN, oldSize));
  }

  private void setAriel()
  {
    int oldSize = myTextView.getFont().getSize();
    Font newFont = new Font("Ariel", Font.PLAIN, oldSize);
    AttributedCharacterIterator.Attribute attribute;
    myTextView.setFont(newFont);
  }

  private void addSizeActions(JMenu sMenu)
  {
    sMenu.addSeparator();
    addSize(8, sMenu);
    addSize(10, sMenu);
    addSize(12, sMenu);
    addSize(14, sMenu);
    addSize(18, sMenu);
    addSize(24, sMenu);
	  sMenu.addSeparator();
	  for (int ii=9; ii<=18; ++ii)
	    addSize(ii, sMenu);
  }
  
  private void addSize(int size, JMenu sMenu)
  {
    Action szAct = new AbstractAction("" + size)
    {
      public void actionPerformed(ActionEvent evt)
      {
        Font tFont = myTextView.getFont();
        myTextView.setFont(tFont.deriveFont(Float.valueOf((String)getValue(Action.NAME)).floatValue()));
      }
    };
    sMenu.add(szAct);
  }
  
  private class Opener extends AbstractAction
  {
    Opener()
    {
      super("Open");
      putValue(MNEMONIC_KEY, new Integer('O'));
    }

    public void actionPerformed(ActionEvent e)
    {
      doOpen();
    }
    
    void doOpen()
    {
      JFileChooser chsr = new JFileChooser();
      int ok = -1;
      while (ok != JFileChooser.APPROVE_OPTION)
      {
        ok = chsr.showOpenDialog(mDisplay);
        if (ok == JFileChooser.CANCEL_OPTION)
          return;
        File iFile = chsr.getSelectedFile();
        try
        {
          FileReader rdr = new FileReader(iFile);
          StringBuffer buf = new StringBuffer();
          try
          {
            int rCh = rdr.read();
            while (rCh >= 0)
            {
              char ch = (char)rCh;
              buf.append(ch);
              rCh = rdr.read();
            }
            myTextView.setText(buf.toString());
          }
          catch (IOException e)
          {
            try { rdr.close(); }
            catch (IOException e1) { e1.printStackTrace(); }
          }
        }
        catch (FileNotFoundException e)
        {
          JOptionPane.showMessageDialog(mDisplay, e.getMessage(), "File Not Found", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }
}

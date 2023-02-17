package com.mm.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
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
import javax.swing.text.Caret;
import javax.swing.text.DefaultEditorKit;
import org.jetbrains.annotations.NotNull;

/**
 * GUI utility to show the numeric values of any text character that get pasted in.
 * <p> Requires:
 * <br>com.go2.ui.platform.Platform
 * <br>com.go2.ui.platform.unix.UnixPlatform
 * <br>com.go2.ui.platform.windows.WindowsPlatform
 */
@SuppressWarnings({"CloneableClassWithoutClone", "HardCodedStringLiteral", "MagicNumber", "MagicCharacter"})
public class CValues //extends JPanel
  implements
    DocumentListener
{
	private static final Font sMonoFnt = new Font("Monospaced", Font.PLAIN, 10);
	private JLabel mDisplay;

	public static void main(String[] args)
  {
    //noinspection OverlyBroadCatchBlock
    try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
    catch (Exception ignore) { /* Do Nothing */ }
    JFrame mf = new JFrame("Character Values");
    mf.addWindowListener(new WindowAdapter()
      { @Override
      public void windowClosing(WindowEvent evt) { System.exit(0); } } );
    mf.setBounds(20, 20, 500, 400);
    //noinspection ResultOfObjectAllocationIgnored
    new CValues(mf);
    mf.setVisible(true);
  }
  
  private final JTextArea myTextView = new JTextArea();
  private final JTextArea myCharView = new JTextArea();
  
  public CValues(RootPaneContainer rpc)
  {
    Container cp = rpc.getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(makeViewPanel(), BorderLayout.CENTER);
    Action cut = new DefaultEditorKit.CutAction();
    Action copy = new DefaultEditorKit.CopyAction();
    Action paste = new DefaultEditorKit.PasteAction()
      {
        @Override
        public void actionPerformed(ActionEvent evt)
        {
          super.actionPerformed(evt);
          myTextView.requestFocus();
        }
      };

    Action exit = new AbstractAction("Exit")
      {
        @Override
        public void actionPerformed(ActionEvent evt)
        {
          System.exit(0);
        }
      };
    Action courierFont = new AbstractAction("Courier")
    {
      @Override
      public void actionPerformed(ActionEvent evt) { setCourier(); }
    };
    Action defFont = new AbstractAction("Default Font")
    {
      @Override
      public void actionPerformed(ActionEvent evt) { setDefaultFont(); }
    };
    Action arielFont = new AbstractAction("Ariel")
    {
      @Override
      public void actionPerformed(ActionEvent evt) { setAriel(); }
    };
    exit.putValue(Action.MNEMONIC_KEY, (int) 'X');
    cut.putValue(Action.MNEMONIC_KEY, (int) 'T');
    copy.putValue(Action.MNEMONIC_KEY, (int) 'C');
    paste.putValue(Action.MNEMONIC_KEY, (int) 'P');
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
    tb.add(getJavaVersion());
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
    myTextView.getCaret().addChangeListener(c -> showTextLength());
  }

  @NotNull
  private JLabel getJavaVersion() {
    final JLabel jLabel = new JLabel("  Java v" + System.getProperty("java.version"));
    jLabel.setFont(jLabel.getFont().deriveFont(10.0f));
    return jLabel;
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

  @Override
  public void insertUpdate(DocumentEvent e) { changed(e); }
  @Override
  public void removeUpdate(DocumentEvent e) { changed(e); }
  @Override
  public void changedUpdate(DocumentEvent e) { changed(e); }
  
  private void changed(DocumentEvent evt)
  {
	  final int length = evt.getDocument().getLength();
    try
    {
	    String doc = evt.getDocument().getText(0, length);
      StringBuilder sb = new StringBuilder();
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
        sb.append(pad4(Integer.toHexString(theChar)));
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
    showTextLength();
  }

  private void showTextLength() {
    int length = myTextView.getDocument().getLength();
    final Caret caret = myTextView.getCaret();
    int selectionLength = Math.abs(caret.getDot() - caret.getMark());
    if (selectionLength == 0) {
      mDisplay.setText(String.format("Length: %s", length));
    } else {
      mDisplay.setText(String.format("Length: %d/%d", selectionLength, length));
    }
  }

  private static String pad4(String input)
  {
    StringBuilder bf = new StringBuilder(input);
    while (bf.length() < 4) {
      bf.insert(0, '0');
    }
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
//    AttributedCharacterIterator.Attribute attribute;
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
	  for (int ii=9; ii<=18; ++ii) {
      addSize(ii, sMenu);
    }
  }
  
  private void addSize(int size, JMenu sMenu)
  {
    Action szAct = new AbstractAction(String.valueOf(size))
    {
      @Override
      public void actionPerformed(ActionEvent evt)
      {
        Font tFont = myTextView.getFont();
        myTextView.setFont(tFont.deriveFont(Float.parseFloat((String) getValue(Action.NAME))));
      }
    };
    sMenu.add(szAct);
  }
  
  private class Opener extends AbstractAction
  {
    Opener()
    {
      super("Open");
      putValue(MNEMONIC_KEY, (int) 'O');
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      doOpen();
    }
    
    void doOpen()
    {
      JFileChooser chooser = new JFileChooser();
      int ok = -1;
      while (ok != JFileChooser.APPROVE_OPTION)
      {
        ok = chooser.showOpenDialog(mDisplay);
        if (ok == JFileChooser.CANCEL_OPTION) {
          return;
        }
        File iFile = chooser.getSelectedFile();
        try(FileReader rdr = new FileReader(iFile))
        {
          StringBuilder buf = new StringBuilder();
          int rCh = rdr.read();
          while (rCh >= 0)
          {
            char ch = (char)rCh;
            buf.append(ch);
            rCh = rdr.read();
          }
          myTextView.setText(buf.toString());
        }
        catch (FileNotFoundException e)
        {
          JOptionPane.showMessageDialog(mDisplay, e.getMessage(), "File Not Found", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    }
  }
}

package com.mm.paragraph;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 11/29/22
 * <p>Time: 4:52 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public final class Paragraph extends JPanel {

  private final JTextArea editor = new JTextArea(60, 80);

  public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    @SuppressWarnings("HardCodedStringLiteral")
    JFrame jFrame = new JFrame("Paragraphs");
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setLocationByPlatform(true);
    jFrame.add(new Paragraph());
    jFrame.pack();
    jFrame.setVisible(true);
  }
  
  private Paragraph() {
    super(new BorderLayout());
    add(makeToolBar(), BorderLayout.PAGE_START);
    add(makeMainPanel(), BorderLayout.CENTER);
    JLabel version = new JLabel(System.getProperty("java.version"));
    final float smallFontSize = 10.0f;
    version.setFont(version.getFont().deriveFont(Font.PLAIN, smallFontSize));
    add(version, BorderLayout.PAGE_END);
  }
  
  private JComponent makeToolBar() {
    JToolBar toolBar = new JToolBar();
    //noinspection HardCodedStringLiteral
    Action paragraphAction = new AbstractAction("Paragraph") {
      @Override
      public void actionPerformed(final ActionEvent e) {
        String text = editor.getSelectedText();
        editor.replaceSelection(makeParagraph(text));
      }
    };
    int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    paragraphAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('p', mask));
    toolBar.add(paragraphAction);

    //noinspection HardCodedStringLiteral
    Action copyAll = new AbstractAction("Copy All") {
      @Override
      public void actionPerformed(final ActionEvent e) {
        String text = editor.getText().trim();
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);
      }
    };
    int shiftMask = mask | KeyEvent.SHIFT_MASK;
    copyAll.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('c', shiftMask));
    toolBar.add(copyAll);

    return toolBar;
  }
  
  private JComponent makeMainPanel() {
    editor.setWrapStyleWord(true);
    editor.setLineWrap(true);
    return new JScrollPane(editor,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
  }
  
  private static String makeParagraph(String rawText) {
    while (rawText.contains(" \n")) {
      rawText = swapAll(rawText, " \n", "\n");
    }
    while (rawText.contains("\n ")) {
      rawText = swapAll(rawText, "\n ", "\n");
    }
    while (rawText.contains("\n")) {
      rawText = swapAll(rawText, "\n", " ");
    }
    return rawText;
  }

  private static String swapAll(String src, String bad, String good) {
    int badLen = bad.length();
    StringBuilder builder = new StringBuilder(src);
    while (builder.indexOf(bad) >= 0) {
      int index = builder.lastIndexOf(bad);
      builder.replace(index, index + badLen, good);
    }
    return builder.toString();
  }
}

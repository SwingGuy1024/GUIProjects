package com.neptunedreams;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import com.formdev.flatlaf.FlatDarkLaf;
import com.mm.gui.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.mm.gui.Utils.iter;

/**
 * <p>Tool to assist in solving cryptograms</p>
 * <p>Handling of typed characters:</p>
 * <pre style="font-family: Cambria, 'Times New Roman', Times, serif; font-size: 12px">
 *   1. Character gets typed.
 *        Calls insertString()
 *             calls encode()
 *             calls decode()
 *                  calls clearText.setText()
 *                       calls insertString()
*  </pre>
 * <p>Test cases:</p>
 * <p>ABCDE FGHIJKL MNOP, QGRCST UVWX YCZ</p>
 * <p>WHOMP GIJUZQX VERY, DIFOLS BANK COT.</p>
 * <p>FLICK, QVXOZ, NEW TRAMP BASH JUG DRY.</p>
 * <p>QSO NELTZ JUPRF WPM VECKG PYOU QSO AIHD BPX.</p>
 * <p>DZUZBC FZWO LZ HZDWC TOMSDB SDCPZQW, TL'O LZZ HQZKBWB! — CZFT UWQQS</p>
 * <P>SGD MTLADQR NM SGD OHDBDR NE OZODQ ZQD Z MHGHKHRS BHOGDQ, VGHBG MDDCR SVN CDBQXOSHNM JDXR: NMD SN LZJD SGD FQHC ZMC ZMNSGDQ ENQ SGD ZCCHSHUD RSDO.</P>
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 12/14/23</p>
 * <p>Time: 12:54 AM</p>
 * <p>@author Miguel Muñoz</p>
 */
@SuppressWarnings("MagicCharacter")
public final class Crypto extends JPanel {

  public static final int COLUMNS = 80;
  public static final int ROWS = 4;
  public static final char DOT = '•';
  public static final int ALPHABET = 26;
  private final JTextArea clearText = new JTextArea(ROWS, COLUMNS);
  private final JTextArea cipherText = new JTextArea(ROWS, COLUMNS);
  private final JTextField initialText = new JTextField(COLUMNS);
  private final JTextField cipherKey = new JTextField(ALPHABET);
  private final JTextField clearKey = new JTextField(ALPHABET);

  private final Map<Character, Character> cipherMap = makeCipherMap(this);
  private final JButton clearButton = new JButton("Clear");
  private final JButton encryptButton = new JButton("Encrypt");

  public static void main(String[] args) {
    FlatDarkLaf.setup();
    UIManager.getDefaults().put("TextField.background", Color.BLACK);
    JFrame frame = new JFrame("Crypto");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setLocationByPlatform(true);
    frame.add(new Crypto());
    frame.pack();
    frame.setVisible(true);
  }
  private boolean allowDecode = true;
//  private boolean allowEncode = true;

  private static Map<Character, Character> makeCipherMap(Crypto crypto) {
    Map<Character, Character> cipherMap = new HashMap<>() {
      @Override
      public Character put(Character key, Character value) {
        final Character rVal = super.put(key, value);
        crypto.updateKeys();
        return rVal;
      }
    };
    return initializeMap(cipherMap);
  }

  private void updateKeys() {
    // cipherMap will be null when Crypto is under construction.
    if (cipherMap != null) {
      final String validChars = initialText.getText();
      Set<Character> validKeys = new HashSet<>();
      for (char ch: iter(validChars)) {
        validKeys.add(ch);
      }
      StringBuilder cipherKeys = new StringBuilder(ALPHABET);
      StringBuilder clearKeys = new StringBuilder(ALPHABET);
      for (char key = 'A'; key <= 'Z'; ++key) {
        cipherKeys.append(key).append(' ');
        if (validKeys.contains(key)) {
          clearKeys.append(cipherMap.get(key));
        } else {
          clearKeys.append(' ');
        }
        clearKeys.append(' ');
      }
      cipherKey.setText(cipherKeys.toString());
      clearKey.setText(clearKeys.toString());
    }
  }

  @NotNull
  private static Map<Character, Character> initializeMap(Map<Character, Character> cipherMap) {
    cipherMap.clear();
    for (char c = 'A'; c <= 'Z'; ++c) {
      cipherMap.put(c, DOT);
    }
    cipherMap.put(' ', ' ');
    return cipherMap;
  }

  private Crypto() {
    super(new BorderLayout());
    add(BorderLayout.CENTER, makeDoublePane());
    add(BorderLayout.PAGE_START, makeTopPane());
    add(BorderLayout.PAGE_END, makeBottomPane());
    final Font monoFont = cipherText.getFont();
    initialText.setFont(monoFont);
    cipherKey.setFont(monoFont);
    clearKey.setFont(monoFont);
    FocusListener initialFl = new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        initialText.selectAll();
      }

      @Override
      public void focusLost(FocusEvent e) { }
    };
    initialText.addFocusListener(initialFl);

    FocusListener clearFl = new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        if (!e.isTemporary()) {
          cipherText.setText(initialText.getText());
          clearText.setText("");
          decode();
          clearText.requestFocusInWindow();
          updateKeys();
          encryptButton.setEnabled(false);
        }
      }

      @Override
      public void focusLost(FocusEvent e) { }
    };
    clearText.addFocusListener(clearFl);

    DocumentFilter toUpperFilter = new DocumentFilter() {

      @Override
      public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        fb.insertString(offset, string.toUpperCase(), attr);
      }

      @Override
      public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        fb.replace(offset, length, text.toUpperCase(), attrs);
      }
    };
    getPlainDoc(initialText).setDocumentFilter(toUpperFilter);
    
    DocumentFilter decipherFilter = new DocumentFilter() {
      @Override
      public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
//        allowDecode = false;
        Document doc = clearText.getDocument();
        char dChar = doc.getText(offset, 1).charAt(0);
        if (Character.isLetter(dChar)) {
          Character cipherChar = mappedFrom(cipherMap, dChar);
          if (cipherChar != null) {
            cipherMap.put(cipherChar, DOT);
          }
        }
//        allowDecode = true;
        fb.remove(offset, length);
        decode();
      }

      @Override
      public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
        final String newText = text.toUpperCase();
        encode(newText, offset);

        boolean previousState = allowDecode;
        allowDecode = false;
        fb.insertString(offset, newText, attr);
        allowDecode = previousState;
        decode();
      }

      @Override
      public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        final String newText = text.toUpperCase();
        encode(newText, offset);

        boolean previousState = allowDecode;
        allowDecode = false;
        fb.replace(offset, length, newText, attrs);
        allowDecode = previousState;
        decode();
      }
    };
    getPlainDoc(clearText).setDocumentFilter(decipherFilter);
  }
  
  private void decode() {
    if (allowDecode) {
      allowDecode = false;
      String text = cipherText.getText();
      StringBuilder buff = new StringBuilder();
      for (char c: iter(text)) {
        buff.append(decodeChar(c));
      }
      final Caret caret = clearText.getCaret();
//    int mark = caret.getMark();
      int dot = caret.getDot();
      clearText.setText(buff.toString());
      caret.setDot(dot);
      allowDecode = true;
    }
  }

  private void encode(String text, int offset) {
    if (allowDecode) {
      Document cipherDoc = cipherText.getDocument();
      int i=0;
      for (char clearChar: iter(text)) {
        char cipherChar = charFrom(cipherDoc, offset + i++);
        if (Character.isLetter(cipherChar) && Character.isLetter(clearChar)) {
          if (isMappable(clearChar, cipherChar)) { // remaps.
            cipherMap.put(cipherChar, clearChar);
          }
        }
      }
    }
  }
  
  private char charFrom(Document doc, int offset) {
    try {
      return doc.getText(offset, 1).charAt(0);
    } catch (BadLocationException e) {
      throw new IllegalStateException(String.format("Location %d", offset), e);
    }
  }
  
  private char decodeChar(char ch) {
    if (cipherMap.containsKey(ch)) {
      return cipherMap.get(ch);
    }
    return ch;
  }

  private PlainDocument getPlainDoc(JTextComponent cmp) {
    return (PlainDocument) cmp.getDocument();
  }

  private void doClear() {
    initializeMap(cipherMap);
    initialText.setText("");
    cipherText.setText("");
    clearText.setText("");
    encryptButton.setEnabled(true);
    initialText.requestFocus();
  }
  
  private void doEncrypt() {
    StringBuilder builder = new StringBuilder("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    initializeMap(cipherMap);
    Random random = new Random(System.currentTimeMillis());
    char cipherChar = 'A';
    while (builder.length() > 2) {
      boolean misMatch;
      char clearChar;
      int index;
      do  {
        // never match a character to itself!
        index = random.nextInt(builder.length());
        clearChar = builder.charAt(index);
        misMatch = clearChar == cipherChar;
      } while (misMatch);
      builder.delete(index, index+1);
      cipherMap.put(cipherChar++, clearChar);
    }
    
    // Two letters remain to be mapped to Y and Z.
    if (mapLastOnes(builder, 'Y', 'Z')) {
      if (mapLastOnes(builder, 'Z', 'Y')) {
        // Neither remaining character is Y nor Z, so there's no chance for a mismatch
        int index = random.nextInt(2);
        char clearChar = builder.charAt(index);
        cipherMap.put('Y', clearChar);
        clearChar = builder.charAt(0);
        cipherMap.put('Z', clearChar);
      }
    }
    
    StringBuilder cipherBuilder = new StringBuilder();
    for (char c: initialText.getText().toCharArray()) {
      if (Character.isLetter(c)) {
        c = cipherMap.get(c);
      }
      cipherBuilder.append(c);
    }
    doClear();
    initialText.setText(cipherBuilder.toString());
    updateKeys();
  }

  private boolean mapLastOnes(StringBuilder builder, char p1, char p2) {
    int index = builder.indexOf(String.valueOf(p1));
    if (index >= 0) {
      int clearIndex = 1-index; // index is either 0 or 1. So clearIndex does NOT point to Y.
      char clearChar = builder.charAt(clearIndex);
      cipherMap.put(p1, clearChar);
      builder.delete(clearIndex, clearIndex+1);
      cipherMap.put(p2, builder.charAt(0));
      return false;
    }
    return true; // True means it needs to keep looking.
  }

  private JComponent makeTopPane() {
    var panel = new  Box(BoxLayout.X_AXIS);
    panel.add(BorderLayout.CENTER, initialText);
    panel.add(BorderLayout.LINE_END, clearButton);
    clearButton.addActionListener(e-> doClear());
    encryptButton.addActionListener(e -> doEncrypt());
    panel.add(encryptButton);
    return panel;
  }

  private JPanel makeDoublePane() {
    JPanel doublePane = new JPanel(new GridLayout(0, 1));
    cipherText.setEditable(false);
    doublePane.add(Utils.wrap(cipherText, true));
    doublePane.add(Utils.wrap(clearText, true));
    return doublePane;
  }
  
  private JPanel makeBottomPane() {
    JPanel bottomPane = new JPanel(new GridLayout(0, 1));
    cipherKey.setEditable(false);
    clearKey.setEditable(false);
    bottomPane.add(cipherKey);
    bottomPane.add(clearKey);
    return bottomPane;
  }

  private boolean isMappable(char clearChar, char cipherChar) {
//    final Collection<Character> values = cipherMap.values();
    final Character key = mappedFrom(cipherMap, clearChar);
    if (key != null) {
      cipherMap.put(key, DOT);
      cipherMap.put(cipherChar, clearChar);
      return false;
    }
    return true;
  }
  
  private <K, V> @Nullable K mappedFrom(Map<K, V> map, V value) {
    if (map.containsValue(value)) {
      for (K key : map.keySet()) {
        if (map.get(key).equals(value)) {
          return key;
        }
      }
    }
    return null;
  }
}

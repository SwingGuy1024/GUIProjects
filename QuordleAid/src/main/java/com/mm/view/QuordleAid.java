package com.mm.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import com.formdev.flatlaf.FlatDarkLaf;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/14/22
 * <p>Time: 6:34 PM
 *
 * @author Miguel Mu–oz
 */
@SuppressWarnings({"HardCodedStringLiteral", "UseOfSystemOutOrSystemErr"})
public final class QuordleAid extends JPanel {

  // The width of all the components is determined by the length of this String.
  private static final String FREQ = "EARIOTNSLCUDPMHGBFYWKVXZJQ          ";
  private static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 20);
  private static final Font MONO_BOLD = MONO.deriveFont(Font.BOLD);
  private static final Deque<Quart> quarts = new LinkedList<>();
  private static final FocusListener FOCUS_LISTENER = new FocusAdapter() {
    @Override
    public void focusGained(final FocusEvent e) {
      final JTextComponent textComponent = (JTextComponent) e.getComponent();
      int length = textComponent.getText().length();
      textComponent.select(length, length); // put cursor at the end.
    }
  };
  public static final float FONT_SIZE = 10.0f;
  private final JLabel frequencyLabel = new JLabel(FREQ);
  private static final char SPACE = ' ';
  private final JTextField played = new JTextField(5);

  public static void main(String[] args) {
//    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    FlatDarkLaf.setup();
    UIManager.getDefaults().put("TextField.background", Color.BLACK);
    //noinspection HardCodedStringLiteral
    JFrame jFrame = new JFrame("Quordle Aid");
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setLocationByPlatform(true);
    jFrame.add(new QuordleAid());
    jFrame.pack();
    jFrame.setVisible(true);
  }
  
  private QuordleAid() {
    super(new BorderLayout());
    add(makeTopPanel(), BorderLayout.PAGE_START);
    add(makeQuartiles(), BorderLayout.CENTER);
    add(makeBottomPanel(), BorderLayout.PAGE_END);
  }
  
  private JPanel makeTopPanel() {
    JPanel topPanel = new JPanel(new GridLayout(0, 1));
    final JLabel version = new JLabel(System.getProperty("java.version"));
    version.setFont(version.getFont().deriveFont(Font.PLAIN, FONT_SIZE));
    topPanel.add(version);
    
    topPanel.add(frequencyLabel);
    played.addFocusListener(FOCUS_LISTENER);
    frequencyLabel.setBorder(played.getBorder());
    frequencyLabel.setFont(MONO_BOLD);
    played.setFont(MONO_BOLD);
    final PlainDocument document = (PlainDocument) played.getDocument();
    document.addDocumentListener(makeDocumentListener(frequencyLabel));
    document.setDocumentFilter(DOC_FILTER);
    topPanel.add(played);
    return topPanel;
  }
  
  private JPanel makeQuartiles() {
    JPanel quartiles = new JPanel(new GridLayout(2, 0));
    Border matteBorder = new MatteBorder(10, 5, 10, 5, getBackground());
    quartiles.add(makeQuart(matteBorder));
    quartiles.add(makeQuart(matteBorder));
    quartiles.add(makeQuart(matteBorder));
    quartiles.add(makeQuart(matteBorder));
    return quartiles;
  }
  
  private Quart makeQuart(Border border) {
    quarts.add(new Quart(border));
    return quarts.getLast();
  }
  
  private JPanel makeBottomPanel() {
    JPanel bottomPanel = new JPanel(new FlowLayout());
    JButton clearButton = new JButton("Clear");
    clearButton.addActionListener(e -> doClear());
    bottomPanel.add(clearButton);
    return bottomPanel;
  }
  
  private void doClear() {
    for (Quart q: quarts) {
      q.clear();
    }
    played.setText("");
    played.requestFocus();
  }
  
  private class Quart extends JPanel {

    private final JTextField yellowAndGreen = new JTextField(5);

    Quart(Border border) {
      super(new BorderLayout());
      setBorder(border);
      final JLabel fLabel = new JLabel(FREQ);
      fLabel.setFont(MONO);
      yellowAndGreen.setFont(MONO_BOLD);
      add(fLabel, BorderLayout.PAGE_START);
      add(yellowAndGreen, BorderLayout.CENTER);
      final PlainDocument document = (PlainDocument) yellowAndGreen.getDocument();
      document.addDocumentListener(makeQListener(fLabel));
      document.setDocumentFilter(DOC_FILTER);
      fLabel.setBorder(yellowAndGreen.getBorder());

      PropertyChangeListener pcl = evt -> {
        fLabel.setText(evt.getNewValue().toString());
        doProcess(document, fLabel);
      };
      frequencyLabel.addPropertyChangeListener("text", pcl);

      yellowAndGreen.addFocusListener(FOCUS_LISTENER);
    }
    
    private void clear() {
      yellowAndGreen.setText("");
    }

    private void doProcess(Document document, JLabel fLabel) {
      final Set<Character> prefix = new HashSet<>();
      try {
        String found = document.getText(0, document.getLength()).toUpperCase();
        found.chars()
            .filter(c -> Character.isLetter((char)c))
            .forEach(c -> prefix.add((char)c));
      } catch (BadLocationException e) {
        throw new IllegalStateException("Can't happen", e);
      }
      String text = frequencyLabel.getText();
      List<Integer> indexList = new LinkedList<>();
      for (int i=0; i<text.length(); ++i) {
        char ch = FREQ.charAt(i);
        if (prefix.contains(ch)) {
          indexList.add(i); // put in the front, to get a reverse order // stone coral
        }
      }
      indexList.sort(Comparator.reverseOrder());
      
      List<String> fragmentList = new ArrayList<>();
      fragmentList.add(text);
      for (int index: indexList) {
//        String fragment = fragmentList.get(fragmentList.size()-1);
        String fragment = fragmentList.get(0);
        fragmentList.add(1, fragment.substring(index+1));
        fragmentList.set(0, fragment.substring(0, index));
      }
      StringBuilder fullText = new StringBuilder("<html>");
      int i=0;
      Collections.sort(indexList);
      for (int index: indexList) {
        fullText.append(fragmentList.get(i++))
            .append("<strong>")
            .append(FREQ.charAt(index))
            .append("</strong>");
      }
      fullText
          .append(fragmentList.get(indexList.size()))
          .append("</html>");
      
      // replace spaces
      fLabel.setText(fullText.toString().replaceAll(" ", "&nbsp;"));
    }

    private DocumentListener makeQListener(JLabel fLabel) {
      return new DocumentListener() {
        @Override
        public void insertUpdate(final DocumentEvent e) {
          doProcess(e.getDocument(), fLabel);
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
          doProcess(e.getDocument(), fLabel);
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
          doProcess(e.getDocument(), fLabel);
        }
      };
    }
  }
  
  private DocumentListener makeDocumentListener(JLabel fLabel) {
    return new DocumentListener() {
      @Override
      public void insertUpdate(final DocumentEvent e) {
        doProcess(e.getDocument(), fLabel);
      }

      @Override
      public void removeUpdate(final DocumentEvent e) {
        doProcess(e.getDocument(), fLabel);
      }

      @Override
      public void changedUpdate(final DocumentEvent e) {
        doProcess(e.getDocument(), fLabel);
      }

      private void doProcess(Document doc, JLabel label) {
        String master = FREQ.trim();
        String exclude;
        try {
          exclude = doc.getText(0, doc.getLength()).toUpperCase();
        } catch (BadLocationException e) {
          throw new IllegalStateException("Should not happen", e);
        }
        for (int i = 0; i < exclude.length(); ++i) {
          char c = exclude.charAt(i);
          if (Character.isLetter(c)) {
            master = master.replace(c, SPACE);
          }
        }
        label.setText(master);
      }
    };
  }
  
  private static final DocumentFilter DOC_FILTER = new DocumentFilter() {
    @Override
    public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr)
        throws BadLocationException {
      fb.insertString(offset, string.toUpperCase(), attr);
    }

    @Override
    public void replace(final FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs)
        throws BadLocationException {
      fb.replace(offset, length, text.toUpperCase(), attrs);
    }
  };
}

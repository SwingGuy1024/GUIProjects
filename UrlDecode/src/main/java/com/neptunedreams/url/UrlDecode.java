package com.neptunedreams.url;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 4/11/24</p>
 * <p>Time: 6:26 PM</p>
 * <p>@author Miguel Muñoz</p>
 */
@SuppressWarnings("MagicNumber")
public class UrlDecode extends JPanel {

  public static final char PER_CENT = '%';
  public static final Clipboard SYSTEM_CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();

  // https%3A%2F%2Fi.pinimg.com%2Foriginals%2F86%2F43%2F43%2F8643432b0a4553febb80fd5b3f0f73a9.jpg
  public static void main(String[] args) {
    JFrame frame = new JFrame("Url Decode");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setLocationByPlatform(true);
    frame.add(new UrlDecode());
    frame.pack();
    frame.setVisible(true);
  }
  
  private final JTextField inputField = new JTextField(80);
  private final JTextField outputField = new JTextField(80);
  
  public UrlDecode() {
    super(new BorderLayout());
    add(makeMainPanel(), BorderLayout.CENTER);
    DocumentListener docListener = new DocumentListener() {
      @Override public void insertUpdate (DocumentEvent e) { process(e); }
      @Override public void removeUpdate (DocumentEvent e) { process(e); }
      @Override public void changedUpdate(DocumentEvent e) { process(e); }

      private void process(DocumentEvent event) {
        Document doc = event.getDocument();
        try {
          String txt = doc.getText(0, doc.getLength());
          outputField.setText(processText(txt));
        } catch (BadLocationException e) {
          //noinspection ProhibitedExceptionThrown
          throw new RuntimeException("Should not happen", e);
        }
      }

      private String processText(String txt) {
        StringBuilder builder = new StringBuilder();
        int maxCode = txt.length() - 2;
        int offset = txt.indexOf(PER_CENT);
        int priorOffset = 0;
        while ((offset < maxCode) && (offset >= 0)) {
          builder.append(txt, priorOffset, offset);
          final String code = txt.substring(offset + 1, offset + 3);
          if (isCode(code)) {
            builder.append(decode(code));
            offset += 3;
          } else {
            builder.append(PER_CENT);
            offset++;
          }
          priorOffset = offset;
          offset = txt.indexOf(PER_CENT, offset);
        }
        builder.append(txt.substring(priorOffset));

        final String result = builder.toString();
        StringSelection stringSelection = new StringSelection(result);
        SYSTEM_CLIPBOARD.setContents(stringSelection, stringSelection);
        return result;
      }
      
      private char decode(String hexString) {
        return (char) Integer.parseUnsignedInt(hexString, 16);
      }

      @SuppressWarnings("MagicCharacter")
      private boolean isCode(String substring) {
        for (char c: substring.toCharArray()) {
          if (!Character.isDigit(c)) {
            char cUp = Character.toUpperCase(c);
            if ((cUp < 'A') || (cUp > 'F')) {
              return false;
            }
          }
        }
        return true;
      }
    };
    inputField.getDocument().addDocumentListener(docListener);
  }

  private JPanel makeMainPanel() {
    JPanel mainPanel = new JPanel(new GridLayout(0, 1));
    mainPanel.add(inputField);
    mainPanel.add(outputField);
    return mainPanel;
  }
}

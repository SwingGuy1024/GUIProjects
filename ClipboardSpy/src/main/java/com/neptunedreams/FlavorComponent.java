package com.neptunedreams;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * <p>Components to support</p>
 * <ul>
 *   <li>java.io.InputStream</li>
 *   <li>[C</li>
 *   <li>[B</li>
 *   <li>java.nio.CharBuffer</li>
 *   <li>java.nio.ByteBuffer</li>
 *   <li></li>
 *   <li>java.lang.String</li>
 *   <li>java.io.Reader</li>
 * </ul>
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 4/15/26
 * <br>Time: 6:57 PM
 * <br>@author Miguel Muñoz (<a href="https://github.com/SwingGuy1024">https://github.com/SwingGuy1024</a>)</p>
 */
public class FlavorComponent extends JPanel {

  private static final Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
  private final JTextArea textArea = new JTextArea();

  FlavorComponent(DataFlavor flavor) {
    super(new BorderLayout());
    Class<?> rClass = flavor.getRepresentationClass();
    String labelText;
    if (rClass.isArray()) {
      Class<?> arrayType = rClass.getComponentType();
      //noinspection MagicCharacter
      labelText = String.format(
          "%s -- Array of %s (%c)",
          flavor,
          arrayType.getSimpleName(),
          arrayType.isPrimitive()? 'P' : ' ');
    } else {
      labelText = flavor.toString();
    }
    System.out.printf("%s -- %s%n", flavor.getHumanPresentableName(), labelText);
    JLabel label = new JLabel(labelText);
    add(label, BorderLayout.PAGE_START);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane(
        textArea,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    add(scrollPane, BorderLayout.CENTER);
    add(getBottomComponent(), BorderLayout.PAGE_END);
    try {
      Object contents = systemClipboard.getData(flavor);
      if (flavor.getMimeType().contains("rtf") && (contents instanceof InputStream inputStream)) {
        textArea.setText(decodeRtf(inputStream));
      } else {
        textArea.setText(toString(contents, flavor.getParameter("charset")));
      }
    } catch (UnsupportedFlavorException | IOException e) {
      throw new IllegalStateException("Should not happen", e);
    }
  }
  
  private static String decodeRtf(InputStream inputStream) throws UnsupportedFlavorException, IOException {
    System.out.println("*** Decoding RTF ***");
    RTFEditorKit editorKit = new RTFEditorKit();
    DefaultStyledDocument styledDocument = new DefaultStyledDocument();
    try {
      editorKit.read(inputStream, styledDocument, 0);
      return styledDocument.getText(0, styledDocument.getLength());
    } catch (BadLocationException e) {
      throw new IllegalStateException("Should not happen", e);
    }
  }

  private String toString(Object contents, String charSetName) {
    if (contents instanceof String) {
      return contents.toString();
    }
    Charset charset = (charSetName == null) ? Charset.defaultCharset() : Charset.forName(charSetName);
    if (contents instanceof ByteBuffer buffer) {
      return new String(buffer.array(), charset);
    }
    if (contents instanceof CharBuffer cBuffer) {
      return cBuffer.toString();
    }
    if (contents instanceof InputStream inputStream) {
      try (Reader reader = new InputStreamReader(inputStream, charset)) {
        return readerToString(reader);
      } catch (IOException e) {
        throw new IllegalStateException("Should not happen", e);
      }
    }
    if (contents instanceof Reader reader) {
      try {
        return readerToString(reader);
      } catch (IOException e) {
        throw new IllegalStateException("Should not happen", e);
      }
    }
    final Class<?> cClass = contents.getClass();
    if (cClass.isArray()) {
      if (cClass.getComponentType() == Byte.TYPE) {
        byte[] bytes = (byte[]) contents;
        return new String(bytes, charset);
      }
      if (cClass.getComponentType() == Character.TYPE) {
        char[] chars = (char[]) contents;
        return new String(chars);
      }
    }
    // Unsupported type:
    System.out.printf("Unsupported type: %s%n", contents.getClass()); // NON-NLS
    return contents.toString();
  }

  private JPanel getBottomComponent() {
    JPanel bottomPanel = new JPanel(new BorderLayout());
    JButton copyButton = new JButton("Copy Contents");
    bottomPanel.add(copyButton, BorderLayout.LINE_END);
    copyButton.addActionListener(e -> copyText());
    return bottomPanel;
  }

  private void copyText() {
    String contents = textArea.getText();
    StringSelection stringSelection = new StringSelection(contents);
    systemClipboard.setContents(stringSelection, stringSelection);
  }
  
  private static String readerToString(Reader reader) throws IOException {
    StringWriter writer = new StringWriter();
    reader.transferTo(writer);
    return writer.toString();
  }
}

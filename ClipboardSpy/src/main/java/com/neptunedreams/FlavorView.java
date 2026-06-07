package com.neptunedreams;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>This class is used by the {@link com.neptunedreams.ClipboardSpy} class.</p> 
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 4/18/26
 * <br>Time: 12:18 AM
 * <br>@author Miguel Muñoz (<a href="https://github.com/SwingGuy1024">https://github.com/SwingGuy1024</a>)</p>
 */
@SuppressWarnings("MagicNumber")
public class FlavorView extends JPanel {
  private final Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
  private final JTree tree = new JTree();
  private final JTextArea dataView = new JTextArea(40, 80);
  private final JLabel dataLabel = new JLabel();
  private final Map<FlavorData, String> contentMap = new TreeMap<>();

  FlavorView() {
    super(new BorderLayout());
  }
  
  static void processClipboardData() {
    FlavorView flavorView = new FlavorView();
    JComponent view = flavorView.clipboardDataToTree();
    flavorView.makeFrame(view);
  }

  private JComponent clipboardDataToTree() {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    DataFlavor[] flavors = clipboard.getAvailableDataFlavors();

    Map<String, SortedSet<FlavorData>> mimeTypeMap = new TreeMap<>();
    for (DataFlavor flavor : flavors) {
      String mimeType = extractRawMimeType(flavor);
      Object content;
      try {
//        content = systemClipboard.getContents(flavor).getTransferData(flavor);
        content = systemClipboard.getData(flavor);
      } catch (UnsupportedFlavorException | IOException e) {
        throw new IllegalStateException("Should not happen", e);
      }
      @Nullable String charset = flavor.getParameter("charset");
      @Nullable String document = flavor.getParameter("document");
      FlavorData flavorData = new FlavorData(flavor, mimeType, content, charset, document);
      mimeTypeMap.merge(mimeType, new TreeSet<>(List.of(flavorData)), (prior, current) -> {
        prior.addAll(current);
        return prior;
      });
//      try {
      try {
        String data = decodeContents(flavorData.flavor(), flavorData.content());
        if ("html".equals(flavorData.flavor().getSubType())) {
          String htmldata = decodeHtml2(data);
          data = "%s\n\n\n%s".formatted(data, htmldata);
        }
        contentMap.put(flavorData, data);
      } catch (IOException e) {
        e.printStackTrace();
      }
//        SwingUtilities.invokeAndWait(() -> contentMap.put(flavorData, data));
//      } catch (IOException | InterruptedException | InvocationTargetException e) {
//        throw new IllegalStateException("Shouldn't happen", e);
//      }
    }

    MutableTreeNode top = new DefaultMutableTreeNode("Top");

    int count = 0;
    for (Map.Entry<String, SortedSet<FlavorData>> entry : mimeTypeMap.entrySet()) {
      MimeTypeNode mimeTypeNode = new MimeTypeNode(entry.getKey());
      Set<FlavorData> dataForMimeType = entry.getValue();
      mimeTypeNode.addChildren(dataForMimeType);
      top.insert(mimeTypeNode, count++);
    }

    ((DefaultTreeModel) tree.getModel()).setRoot(top);
    tree.setRootVisible(false);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.expandRow(0);

    tree.addTreeSelectionListener(this::valueChanged);
    JScrollPane treeScrollPane = new JScrollPane(tree);
    JScrollPane dataScrollPane = new JScrollPane(
        dataView,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    dataView.setEditable(false);
    dataView.setLineWrap(true);
    dataView.setWrapStyleWord(true);
    JPanel wrapperPanel = new JPanel(new BorderLayout());
    wrapperPanel.add(dataScrollPane, BorderLayout.CENTER);
    wrapperPanel.add(dataLabel, BorderLayout.PAGE_START);
    final JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, wrapperPanel);
    jSplitPane.setDividerLocation(0.2);
    add(jSplitPane, BorderLayout.CENTER);
    return this;
  }

  private static String extractRawMimeType(DataFlavor flavor) {
    return String.format("%s/%s", flavor.getPrimaryType(), flavor.getSubType()); 
  }

  private void makeFrame(JComponent contents) {

    JFrame frame = new JFrame("Clipboard Spy");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setContentPane(contents);
    frame.setLocationRelativeTo(null);
    frame.pack();
    frame.setVisible(true);
  }
  
  public void valueChanged(TreeSelectionEvent e) {
    TreePath path = e.getPath();
    tree.scrollPathToVisible(path);
    Object someNode = tree.getLastSelectedPathComponent();
    if (someNode instanceof FlavorNode flavorNode) {
      FlavorData flavorData = flavorNode.getUserObject();
      String data;
//      if (contentMap.containsKey(flavorData)) {
      data = contentMap.get(flavorData);
//      } else {
//        try {
//      data = decodeContents(flavorData.flavor(), flavorData.content());
//      if ("html".equals(flavorData.flavor().getSubType())) {
//        data = decodeHtml(data);
//      }
//        } catch (IOException ex) {
//          data = "Unable to read contents: %s\n".formatted(ex.getMessage());
//          ex.printStackTrace();
//        }
//        contentMap.put(flavorData, data);
//      }
      dataView.setText(data);
      dataLabel.setText(String.format(
          "%s Using %s (%s) Char Set %s",
          flavorData.mimeType(),
          flavorData.flavor().getRepresentationClass(),
          flavorData.flavor().getParameter("document"),
          flavorData.flavor().getParameter("charSet")
      ));
    }
  }
  
//  private String decodeHtml(String content) {
//    String fullBody = String.format("<html><body>%s</body></html>", content);
//    return Jsoup.parse(fullBody).wholeText();
//  }
//
  private String decodeHtml2(String content) {
    content = String.format("<html><body>%s</body></html>", content);
    StringBuilder builder = new StringBuilder();
    HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {
      @Override
      public void handleText(char[] data, int pos) {
        builder.append(data);
      }
    };
    try {
      new ParserDelegator().parse(new StringReader(content), callback, false);
    } catch (IOException e) {
      e.printStackTrace();
      return content;
    }
    return builder.toString();
  }

  private static String decodeContents(DataFlavor flavor, Object contents) throws IOException {
    String flavorCharset = flavor.getParameter("charset");
    Charset charset = (flavorCharset == null) ? Charset.defaultCharset() : Charset.forName(flavorCharset);
    boolean isRtf = "rtf".equals(flavor.getSubType());
    if (contents instanceof ByteBuffer buffer) {
      return decodeIfRtf(isRtf, new String(buffer.array(), charset));
    }
    if (contents instanceof CharBuffer cBuffer) {
      return decodeIfRtf(isRtf, cBuffer.toString());
    }
    if (contents instanceof InputStream inputStream) {
      if (isRtf) {
        return decodeRtf(inputStream, charset);
      }
      try (Reader reader = new InputStreamReader(inputStream, charset)) {
        return readerToString(reader);
      }
    }
    if (contents instanceof Reader reader) {
      if (isRtf) {
        return getRtfString(reader);
      }
      return readerToString(reader);
    }
    if (contents instanceof String s) {
      return s;
    }
    final Class<?> cClass = contents.getClass();
    if (cClass.isArray()) {
      if (cClass.getComponentType() == Byte.TYPE) {
        byte[] bytes = (byte[]) contents;
        return decodeIfRtf(isRtf, new String(bytes, charset));
      }
      if (cClass.getComponentType() == Character.TYPE) {
        char[] chars = (char[]) contents;
        return decodeIfRtf(isRtf, new String(chars));
      }
    }
    // Unsupported type:
    System.out.printf("Unsupported type: %s%n", contents.getClass()); // NON-NLS
    return contents.toString();
  }

  private static String decodeIfRtf(boolean isRtf, String content) throws IOException {
    if (!isRtf) {
      return content;
    }
    try (Reader reader = new StringReader(content)) {
      return getRtfString(reader);
    }
  }

  private static String decodeRtf(InputStream inputStream, Charset charset) throws IOException {
    try (Reader reader = new InputStreamReader(inputStream, charset)) {
      return getRtfString(reader);
    }
  }

  private static String getRtfString(Reader reader) throws IOException {
    RTFEditorKit editorKit = new RTFEditorKit();
    DefaultStyledDocument styledDocument = new DefaultStyledDocument();
    try {
      editorKit.read(reader, styledDocument, 0);
      return styledDocument.getText(0, styledDocument.getLength());
    } catch (BadLocationException e) {
      throw new IllegalStateException("Shouldn't happen", e);
    }
  }

  private static String readerToString(Reader reader) throws IOException {
    StringWriter writer = new StringWriter();
    reader.transferTo(writer);
    return writer.toString();
  }


  record FlavorData(DataFlavor flavor, String mimeType, Object content, @Nullable String charSet, @Nullable String document) implements Comparable<FlavorData> {
    @Override
    public int compareTo(@NotNull FlavorData o) {
      int mimeTypeCompare = mimeType().compareTo(o.mimeType());
      if (mimeTypeCompare != 0) {
        return mimeTypeCompare;
      }
      final Class<?> thisClass = flavor.getRepresentationClass();
      final Class<?> thatClass = o.flavor().getRepresentationClass();
      int typeCompare = thisClass.getName().compareTo(thatClass.getName());
      if (typeCompare != 0) {
        return typeCompare;
      }
      // If one is an array, they're both arrays
      if (thisClass.isArray()) {
        int elementCompare = (thisClass.getComponentType().getName().compareTo(thatClass.getComponentType().getName()));
        if (elementCompare != 0) {
          return elementCompare;
        }
      }

      int compare = compareObjects(this.charSet(), o.charSet());
      if (compare != 0) {
        return compare;
      }

      return compareObjects(document(), o.document());
    }

    /**
     * Comparable of two Comparable, Nullable objects that puts null objects after all the non-null objects. If the
     * class Comparable method has its own way of handling null values, this method overrides that behavior.
     * @param o1 The first Object
     * @param o2 The second Object
     * @return 0 if the objects are equal, 1 if o1 is null or greater than o2, and -1 if o2 is null or
     * greater than o1. If both are null, returns 0.
     * @param <T> Comparable Type
     */
    private static <T extends Comparable<T>> int compareObjects(@Nullable T o1, @Nullable T o2) {
      //noinspection ObjectEquality
      if (o1 == o2) {
        return 0;
      }
      if (o1 == null) {
        return 1;
      }
      if (o2 == null) {
        return -1;
      }
      return o1.compareTo(o2);
    }

    @Override
    public @NotNull String toString() {
      return String.format(
          "%s Using %s %s charset %s",
          flavor.getHumanPresentableName(),
          flavor.getRepresentationClass(),
          flavor.getParameter("document"),
          flavor.getParameter("charset")
      );
    }
  }

  static final class MimeTypeNode extends DefaultMutableTreeNode {
    MimeTypeNode(String mimeType) {
      super(mimeType);
    }

    @Override
    public String getUserObject() {
      return super.getUserObject().toString();
    }

    final void addChildren(Collection<FlavorData> flavorData) {
      for (FlavorData flavor : flavorData) {
        add(new FlavorNode(flavor));
      }
    }
  }

  static final class FlavorNode extends DefaultMutableTreeNode {
    FlavorNode(FlavorData flavordata) {
      super(flavordata);
    }

    @Override
    public FlavorData getUserObject() {
      return (FlavorData) super.getUserObject();
    }
  }
}

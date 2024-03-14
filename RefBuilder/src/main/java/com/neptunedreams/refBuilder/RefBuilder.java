package com.neptunedreams.refBuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.Box;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Utility to build Wikipedia Links using {@code cite}</p>
 * <p>Cite Values:</p>
 * <p>
 *   web<br>
 *   news<br>
 *   book<br>
 *   journal<br>
 * </p>
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 3/13/24</p>
 * <p>Time: 4:01 AM</p>
 * <p>@author Miguel Muñoz</p>
 */
@SuppressWarnings("StringConcatenation")
public class RefBuilder extends JPanel {
  
  // Cite subects: book, news, journal, web
  public static final String numeric = "first | last | editor-first | editor-last";
  public static final String common = "title | year | date | url | page | pages | volume | language | publisher | " +
      "access-date | url-access | url-status | archive-url | archive-date | ref";
  public static final String sources = "book.isbn | book.location | book.orig-year | book.edition | book.oclc | " +
      "book.chapter | book.chapter-url | book.author-link | " +
      "journal.issue | journal.doi | journal.doi-access | journal.issn | journal.bibcode | " +
      "news.newspaper | news.agency | news.work | web";
  public static final String DELIMITER = "\\|";
  public static final char DOT = '.';
  public static final int TEXT_FIELD_LENGTH = 500;

  public static void main(String[] args) {
    JFrame frame = new JFrame("Reference Buildr");
    frame.add(new RefBuilder());
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setLocationByPlatform(true);
    frame.pack();
    
    // By setting the minimum size, we keep the left-side labels from vanishing when the window shrinks.
    // We need to wait until the window first appears, because we don't know the window size until after we show it.
    HierarchyListener hierarchyListener = new HierarchyListener() {
      @Override
      public void hierarchyChanged(HierarchyEvent e) {
        // This executes after the window first appears.
        frame.setMinimumSize(frame.getSize());
        frame.removeHierarchyListener(this);
      }
    };
    frame.addHierarchyListener(hierarchyListener);
    frame.setVisible(true);
  }
  
  private final JTabbedPane tabPane = new JTabbedPane();
  private final Map<String, Set<String>> tagMap;
  private final @NonNls Map<String, Document> valueMap = new HashMap<>();
  
  private final Map<String, Integer> multipleMap = new HashMap<>();
  
  RefBuilder() {
    super(new BorderLayout());
    tagMap = new HashMap<>();
    populate(tagMap);
    populateCommon(tagMap);
    printMap();
    for (String source: tagMap.keySet()) {
      tabPane.add(source, makeTabContent(source));
    }
    add(tabPane, BorderLayout.CENTER);
    add(makeControlPane(), BorderLayout.PAGE_END);
  }

  private JComponent makeTabContent(String source) {
    JPanel content = new JPanel(new GridBagLayout());
    content.add(hStrut(1), constrain(0));
    content.add(hStrut(TEXT_FIELD_LENGTH), constrain(1));
    Set<String> keys = tagMap.get(source);
    for (String key: keys) {
      makeFixedField(content, source, key);
    }
    
    // We create one last invisible row and give it a weighty of 1.0 to push everything above it to the top of the
    // tab pane.
    GridBagConstraints constraints = constrain(1);
    constraints.weighty = 1.0f;
    content.add(hStrut(1), constraints);
    return content;
  }

  private Component makeControlPane() {
    JPanel controlPane = new JPanel(new BorderLayout());
    JTextField nameField = new JTextField();
    controlPane.add(makeControlNorthPane(nameField), BorderLayout.PAGE_START);
    
    JTextArea result = new JTextArea(6, 0);
    JComponent scrollPane = scrollWrapTextArea(result);
    controlPane.add(scrollPane, BorderLayout.CENTER);
    ActionListener actionListener = e -> buildReference(result, nameField.getText().trim());

    JPanel goPane = makeGoPane(actionListener);
    controlPane.add(goPane, BorderLayout.PAGE_END);
    return controlPane;
  }

  private void buildReference(JTextArea result, String name) {
    String tab = tabPane.getTitleAt(tabPane.getSelectedIndex());
    StringBuilder builder = new StringBuilder();
    for (String key: valueMap.keySet()) {
      if (key.startsWith(tab)) {
        Document doc = valueMap.get(key);
        final String fieldText = getText(doc);
        if (!fieldText.isEmpty()) {
          String fieldName = key.substring(key.indexOf(DOT) + 1);
          builder.append(" | ")
              .append(fieldName)
              .append(" = ")
              .append(fieldText);
        }
      }
    }
    builder.insert(0, tab);
    builder.insert(0, "{{cite ");
    builder.append("}}");
    String openRef = name.isEmpty()? "<ref>" : String.format("<ref name=\"%s\">", name);
    builder.insert(0, openRef);
    builder.append("</ref>");
    final String referenceText = builder.toString();
    result.setText(referenceText);
    StringSelection stringSelection = new StringSelection(referenceText);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, stringSelection);
  }
  
  private String getText(Document doc) {
    try {
      return doc.getText(0, doc.getLength()).trim();
    } catch (BadLocationException e) {
      throw new IllegalStateException("Should not happen", e);
    }
  }

  private JComponent makeControlNorthPane(JTextField textField) {
    JLabel nameLabel = new JLabel("Name: ");
    JPanel northPane = new JPanel(new BorderLayout());
    northPane.add(nameLabel, BorderLayout.LINE_START);
    northPane.add(textField, BorderLayout.CENTER);
    return northPane;
  }

  /**
   * The GoPane sits at the bottom of the control pane. It just has the "Go" button, on the right. 
   * @return The Go pane
   */
  @NotNull
  private static JPanel makeGoPane(ActionListener actionListener) {
    JButton goButton = new JButton("go");
    goButton.addActionListener(actionListener);
    JPanel goPane = new JPanel(new BorderLayout());
    goPane.add(goButton, BorderLayout.LINE_END);
    return goPane;
  }

  private static Component hStrut(int length) {
    return Box.createHorizontalStrut(length);
    
  }

  private void makeFixedField(JPanel content, String tab, String source) {
    JComponent label = makeFakeLabel(source);
    content.add(label, constrain(0));
    DisplayComponent valueField = new DisplayComponent(source);
    content.add(valueField, constrain(1));
    valueMap.put(tab + DOT + source, valueField.getDocument());
//    return valueField;
  }

  private GridBagConstraints constrain(int column) {
//    return constrain(column, GridBagConstraints.RELATIVE);
//  }
//  
//  private GridBagConstraints constrain(int column, int row) {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = column;
    constraints.ipadx = 4;
        
    if (column == 0) {
      constraints.anchor = GridBagConstraints.NORTHWEST;
      constraints.ipady = 4;
    } else {
      constraints.fill = GridBagConstraints.BOTH;
      constraints.weightx = 1.0;
    }
    return constraints;
  }

  private void populateCommon(Map<String, Set<String>> tagMap) {
    String[] commonTags = common.split(DELIMITER);
    for (String key: tagMap.keySet()) {
      for (String tag : commonTags) {
        Set<String> set = tagMap.get(key);
        set.add(tag.trim());
      }
    }
  }

  private void populate(Map<String, Set<String>> tagMap) {
    String[] citeTags = sources.split(DELIMITER);
    for (String tag: citeTags) {
      final int dotSpot = tag.indexOf(DOT);
      if (dotSpot < 0) {
        tagMap.put(tag.trim(), new LinkedHashSet<>());
      } else {
        String tagName = tag.substring(0, dotSpot).trim();
        String value = tag.substring(dotSpot+1).trim();
        Set<String> map = tagMap.containsKey(tagName) ? tagMap.get(tagName) : mapNewValue(tagMap, tagName);
        map.add(value);
      }
    }
  }
  
  private Set<String> mapNewValue(Map<String, Set<String>> tagMap, String key) {
    Set<String> set = new LinkedHashSet<>();
    tagMap.put(key, set);
    return set;
  }
  
  public void printMap() {
    for (String key: tagMap.keySet()) {
      Set<String> set = tagMap.get(key);
      for (String tag: set) {
        System.out.printf("%s.%s%n", key, tag); // NON-NLS
      }
    }
  }
  
  private static class DisplayComponent extends JPanel {
    private final ButtonModel buttonModel;
    private final Document document;

    DisplayComponent(String name) {
      super(new BorderLayout());
      JTextField textField = new JTextField(1);
      document = textField.getDocument();

//      DocumentListener dListener = new DocumentListener() {
//        @Override public void insertUpdate(DocumentEvent e) {System.out.println(textField.getBounds());}
//        @Override public void removeUpdate(DocumentEvent e) {System.out.println(textField.getBounds());}
//        @Override public void changedUpdate(DocumentEvent e) {System.out.println(textField.getBounds());}
//      };
//      document.addDocumentListener(dListener);
      
      add(textField, BorderLayout.CENTER);
      JCheckBox big = new JCheckBox("Big");
      big.setSelected(false);
      buttonModel = big.getModel();
      big.addItemListener(e -> toggleBig(e, buttonModel));
      add(big, BorderLayout.LINE_END);
    }

    private void toggleBig(ItemEvent e, ButtonModel buttonModel) {
      boolean isBig = buttonModel.isSelected();
      assert isBig == (e.getStateChange() == ItemEvent.SELECTED);
      if (isBig) {
        final int rows = 5;
        final int columns = 1;
        JTextArea textArea = new JTextArea(document, null, rows, columns);
        JScrollPane scrollPane = scrollWrapTextArea(textArea);
        remove(0); // replace the existing JTextField
        addImpl(scrollPane, BorderLayout.CENTER, 0);
      } else {
        JTextField textField = new JTextField(document, null, 1);
        remove(0); // replace the existing JTextArea
        addImpl(textField, BorderLayout.CENTER, 0);
      }
      getParent().invalidate();
      getParent().revalidate();
    }

    public Document getDocument() {
      return document;
    }

  }
  @NotNull
  private static JScrollPane scrollWrapTextArea(JTextArea textArea) {
    textArea.setWrapStyleWord(true);
    textArea.setLineWrap(true);
    return new JScrollPane(
        textArea,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
  }

  private static JComponent makeFakeLabel(String text) {
    UIDefaults uiDefaults = UIManager.getDefaults();
    final Color textFieldForeground = uiDefaults.getColor("TextField.foreground");
    uiDefaults.put("TextField.inactiveForeground", textFieldForeground);

    JTextField textField = new JTextField(text);
    textField.setEnabled(false);
    final Color bgColor = uiDefaults.getColor("Label.background");
    textField.setBackground(bgColor);
    textField.setForeground(textFieldForeground);
    Border textFieldBorder = textField.getBorder();
    Insets insets = textFieldBorder.getBorderInsets(textField);
    System.out.printf("Border Insets: %s%n", insets); // NON-NLS
    if (textFieldBorder.getClass().toString().contains("Aqua")) {
      textFieldBorder = new MatteBorder(5, 5, 5, 5, bgColor);
      textField.setBorder(textFieldBorder);
      System.out.println("Replacing Aqua Border.");
    }

//    System.out.printf("Border: %s%n", textField.getBorder()); // NON-NLS
//    System.out.printf("    of  %s%n", textField.getBorder()); // NON-NLS
//    System.out.println("Borders:");
//    uiDefaults.keySet()
//        .stream()
////        .filter(s -> s.toString().contains("border") || s.toString().contains("font"))
//        .filter(s -> s.toString().contains("TextField"))
//        .forEach(s -> System.out.printf("%-40s: %s%n", s, uiDefaults.get(s)));
    return textField;
  }
}

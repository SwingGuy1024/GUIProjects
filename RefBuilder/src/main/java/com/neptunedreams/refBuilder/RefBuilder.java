package com.neptunedreams.refBuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonModel;
import javax.swing.DefaultCellEditor;
import javax.swing.FocusManager;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.CaretListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;

import com.mm.gui.Borders;
import com.mm.gui.LandF;
import com.mm.gui.Utils;
import com.mm.util.Constrainer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.mm.gui.Utils.emptyIfNull;
import static javax.swing.ScrollPaneConstants.*;

/**
 * <p>Utility to build Wikipedia Links using {@code cite}</p>
 * <p>Cite Subjects:</p>
 * <p>
 *   web<br>
 *   news<br>
 *   book<br>
 *   journal<br>
 * </p>
 * <p>For the purposes of documentation, this class defines the following terms:<br>
 * <strong>subject:</strong> One of the four types of "cite" values: web, news, book, and journal.<br>
 * <strong>key:</strong> The name of a value in a citation, such as title in {{cite news | title = Ross Ulbricht Convicted}}<br>
 * <strong>value:</strong> The value for a key in a citation, such as "Ross Ulbricht Convicted" in the previous example.<br>
 * <strong>tag:</strong> A combination of a subject and a key, such as news.title<br>
 * <strong>name:</strong> The optional name of a reference, found in the opening ref tag, like this:<br>
 * {@literal <ref name="Titanic Wreckage">}</p>
 * <p>A fill tag will have an optional name, a subject after the word "cite," and a series of key-value pairs, along with html featres like the tags. For example:</p>
 * <p><code>{@literal <ref name="Harry Potter">{{ cite book | title = Harry Potter and the Sorcerer's Stone | first = J.K. | last = Rowling | publisher = Scholastic }}</ref>}</code> or<br>
 * <code>{@literal <ref>{{ cite book | title = Harry Potter and the Sorcerer's Stone | first = J.K. | last = Rowling | publisher = Scholastic }}</ref>}</code></p>
 * <p>This can be used to create new references or modifying existing ones. To modify an existing reference, use the import button and paste in the entire reference, starting with the
 * {@literal <ref>} tag and ending with the {@literal </ref>} closing tag.</p>
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 3/13/24</p>
 * <p>Time: 4:01&nbsp;AM</p>
 * <p>@author Miguel Muñoz</p>
 */
@SuppressWarnings("MagicNumber")
public class RefBuilder extends JSplitPane {

  // Done: Add buttons for lower case and title case. 
  // DONE: Add Multiples
  // Todo: Add a Date Utility
  // Done: Add URL Encoder. From 32 to 47  and 58 to 64 and 123 to 126 Need to experiment with > 128
  //       Encode <>&()\#%[\]^`{|} 0xa0, 0xad (nbs and soft hyphen) & anything above 0xff
  // TODO: Move subject-specific tags to the end. Maybe split off access and archive names into a second,
  //       minor branch so they may be put at the end.
  // Done: Move page and pages & volume to news, journal, and book.
  // TODO: Add a custom field?
  // TODO: Try another L&F
  // Done: Put tab pane in a ScrollPane!
  // TODO: Add a parser to paste in existing references. This may require adding the ability to add 
  //       custom attributes.
  
  // Cite subjects: book, news, journal, web
  
  /*
    Filter Specs:
    1. Filtering is done in two places: When text is entered into a field, and when a reference is being built.
    2. There are two types of filters: url fields, and non-url fields, which I'll call normal fields.
    3. When typing, we want to replace new lines with spaces.
    4. When building, we want to do the following conversions:
      a. nbsp -> &nbsp;
      b. Url fields: url encode the following: < > and space and soft hyphen.
      c. normal fields: url encode the following: < > and soft hyphen
   */

  public static final String ACCESS_DATE = "access-date";
  public static final String URL_STATUS = "url-status";
  private static final Set<String> common = new LinkedHashSet<>(
      List.of("title", "year", "date", "url", "page", "pages", "volume", "language", "publisher",
          "location", ACCESS_DATE, "url-access", URL_STATUS, "archive-url", "archive-date", "ref")
  );
  private static final Set<String> sources
      = new LinkedHashSet<>(List.of("book.isbn", "book.location", "book.orig-year", "book.edition",
      "book.oclc", "book.chapter", "book.chapter-url", "book.author-link", "journal.journal", "journal.issue",
      "journal.doi", "journal.doi-access", "journal.issn", "journal.bibcode", "news.newspaper", "news.agency",
      "news.work", "web") //"web.website")
  );
  public static final String DELIMITER = "\\.";
  public static final char DOT = '.';
  public static final int TEXT_FIELD_LENGTH = 500;
  public static final int FIRST_COLUMN = 0;
  public static final int LAST_COLUMN = 1;

  // http://www.strangeChars.com/!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~ ¬­­/中文

  private static final Set<Integer> forbidden = "\n\r"
      .chars()
      .boxed()
      .collect(Collectors.toUnmodifiableSet());
  private static final Set<Integer> urlEncode = " <>\u00ad" // space, angle brackets, soft hyphen.
      .chars()
      .boxed()
      .collect(Collectors.toUnmodifiableSet());

  private static final Set<Integer> normalEncode = "<>\u00ad" // angle brackets and soft hyphen
      .chars()
      .boxed()
      .collect(Collectors.toUnmodifiableSet());
  public static final String REFERENCE_BUILDER = "Reference Builder";
  public static final char NBSP = '\u00a0'; // Non-breaking space
  public static final char SPACE = ' ';
  
  private static final Map<Character, String> urlEncodings = createEncodings();
  public static final char NEW_LINE = '\n';

  private final Color textFieldBgColor;
  private final Color fakeLabelColor = new Color(0, 0, 0, 0);
  private final ReferenceTabbedPane tabPane = new ReferenceTabbedPane();
  private final Map<String, AuthorTableModel> tableModelMap = new HashMap<>();

  private final Map<String, Set<String>> subjectMap;
  private final @NonNls Map<String, Document> keyMap = new HashMap<>();
  
  private final List<Runnable> editorTerminatorOperations = new LinkedList<>();
  private static final AtomicInteger windowCounter = new AtomicInteger();
  private final JTextArea resultField = new JTextArea(6, 0);

  public static void main(String[] args) {
    LandF.Platform.quickSetLF();
    if (UIManager.getLookAndFeel().getClass().toString().contains("Aqua")) {
      // Aqua JTables use a selection color that's way too dark. So I reduce the saturation by 80% here. 
      UIDefaults defaults = UIManager.getDefaults();
      Color selBG = (Color) defaults.get("Table.selectionBackground");

      float[] hsb = Color.RGBtoHSB(selBG.getRed(), selBG.getGreen(), selBG.getBlue(), null);
      float sat = 0.2f*hsb[1]; // drop saturation a lot.
      int newRGB = Color.HSBtoRGB(hsb[0], sat, hsb[2]);
      Color revisedColor = new ColorUIResource(newRGB);
      defaults.put("Table.selectionBackground", revisedColor);
    }

    final RefBuilder refBuilder = new RefBuilder();
    makeNewFrame(refBuilder);
  }
  
  private static void makeNewFrame(RefBuilder refBuilder) {
    int count = windowCounter.incrementAndGet();
    String frameTitle = (count == 1) ? REFERENCE_BUILDER : String.format("%s %d", REFERENCE_BUILDER, count);
    JFrame frame = new JFrame(frameTitle);
    frame.add(refBuilder);
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
        refBuilder.adjustTabPaneInitialSize();
        frame.removeHierarchyListener(this);
//        examineInputMaps();
      }
    };
    frame.addHierarchyListener(hierarchyListener);
    frame.setVisible(true);
  }

  RefBuilder() {
    super(JSplitPane.VERTICAL_SPLIT);
    UIDefaults uiDefaults = UIManager.getDefaults();
    Color textFieldForeground = new ColorUIResource(Color.black);
    textFieldBgColor = uiDefaults.getColor("Label.background");
    uiDefaults.put("TextField.inactiveForeground", textFieldForeground);
    subjectMap = new HashMap<>();
    populate(subjectMap);
    populateCommon(subjectMap);
    for (String subject: subjectMap.keySet()) {
      tabPane.add(subject, new RefTabPane(subject));
    }
    setTopComponent(makeTopSplitPane());
    setBottomComponent(makeControlPane());
  }
  
  private JPanel makeTopSplitPane() {
    JPanel topSplitPane = new JPanel(new BorderLayout());
    topSplitPane.add(makeImportPane(), BorderLayout.PAGE_START);
    topSplitPane.add(tabPane, BorderLayout.CENTER);
    return topSplitPane;
  }
  
  private JPanel makeImportPane() {
    JButton importButton = new JButton("Import");
    JPanel importPanel = new JPanel(new BorderLayout());
    importPanel.add(importButton, BorderLayout.LINE_END);
    importButton.addActionListener(e -> showImportDialog());
    Borders.addEmptyBorder(importPanel, 12);
    
    JButton newWindow = new JButton("New Window");
    importPanel.add(newWindow, BorderLayout.LINE_START);
    newWindow.addActionListener(e -> makeNewFrame(new RefBuilder()));
    return importPanel;
  }

  private Component makeControlPane() {
    JPanel controlPane = new JPanel(new BorderLayout());

    tabPane.addChangeListener(e -> resultField.setText("")); // Clear the bottom JTextArea
    JComponent scrollPane = scrollWrapTextArea(resultField);
    controlPane.add(scrollPane, BorderLayout.CENTER);
    resultField.setEditable(false);

    JPanel createPane = makeCreatePane();
    controlPane.add(createPane, BorderLayout.PAGE_END);
    return controlPane;
  }
  
  private static void examineInputMaps() {
    examineInputMap(new JTextField());
    examineInputMap(new JFormattedTextField());
    examineInputMap(new JList<>());
    examineInputMap(new JTable());
    examineInputMap(new JTree());
    
  }
  
  private static void examineInputMap(JComponent component) {
    System.out.println("\n\n" + component.getClass().getSimpleName());
    ActionMap actionMap = component.getActionMap();
    Object[] aKeys = emptyIfNull(actionMap.keys());
    Object[] allAKeys = actionMap.allKeys();
    System.out.printf("Action Map sizes: %d and %d%n", aKeys.length, allAKeys.length); // NON-NLS
    for (Object key : allAKeys) {
      System.out.printf("%32s: %20s - %s%n", key, key.getClass().getSimpleName(), actionMap.get(key)); // NON-NLS
    }
    System.out.println("---");
    for (Object key : aKeys) {
      System.out.printf("%32s: %20s - %s%n", key, key.getClass().getSimpleName(), actionMap.get(key)); // NON-NLS
    }
    System.out.println("---");
    InputMap inputMap = component.getInputMap();
    KeyStroke[] iKeys = emptyIfNull(inputMap.keys());
    KeyStroke[] allIKeys = emptyIfNull(inputMap.allKeys());
    System.out.printf("%n%nInput Map Sizes: %d and %d%n", iKeys.length, allIKeys.length); // NON-NLS
    for (KeyStroke key : allIKeys) {
      final Object o = inputMap.get(key);
      System.out.printf("%32s: %20s - %s%n", key, o.getClass().getSimpleName(), o); // NON-NLS
    }
  }
  
  private AuthorTableModel getCurrentTableModel() {
    int index = tabPane.getSelectedIndex();
    String currentTab = tabPane.getTitleAt(index);
    return tableModelMap.get(currentTab);
  }
  
  private void buildReferenceText(JTextArea resultView, String name) {
    // Close all active editors first.
    for (Runnable runner: editorTerminatorOperations) {
      runner.run();
    }
    
    WikiReference wikiReference = new WikiReference();
    wikiReference.setName(name);

    String selectedTab = tabPane.getTitleAt(tabPane.getSelectedIndex());
    for (Map.Entry<String, Document> entry : keyMap.entrySet()) {
      String tag = entry.getKey();
      if (tag.startsWith(selectedTab)) {
        Document doc = entry.getValue();
        final String fieldText = clean(getText(doc), isForUrl(tag));
        if (!fieldText.isEmpty()) {
          String fieldName = tag.substring(tag.indexOf(DOT) + 1);
          wikiReference.setKeyValuePair(fieldName, fieldText);
        }
      }
    }

    // Count Writers and Editors
    List<Integer> writerIndices = new LinkedList<>();
    List<Integer> editorIndices = new LinkedList<>();
    AuthorTableModel tableModel = getCurrentTableModel();
    for (int row = 0; row < (tableModel.getRowCount() - 1); ++row) {
      final Object valueAt = tableModel.getValueAt(row, 2);
      Role role = (Role) valueAt;
      if (role == Role.WRITER) {
        writerIndices.add(row);
      } else if (role == Role.EDITOR) {
        editorIndices.add(row);
      }
    }
    addRoleData(wikiReference, Role.WRITER, writerIndices);
    addRoleData(wikiReference, Role.EDITOR, editorIndices);

    wikiReference.setRefKey(RefKey.valueOf(selectedTab));

    final String referenceText = wikiReference.toString();
    resultView.setText(referenceText);
    StringSelection stringSelection = new StringSelection(referenceText);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, stringSelection);
  }
  
  boolean isForUrl(String tag) {
    return tag.endsWith("url"); // .url and .archive-url
  }

  /**
   * <p>Clean text before putting it in the reference output.</p>
   * <p>Cleaning consists of this: <br>
   *       a. nbsp -> {@literal &nbsp;} <br>
   *       b. Url fields: url encode the following: {@literal <>} and space. <br>
   *       c. normal fields: url encode the following: {@literal <>}</p>
   * @param s The text to clean
   * @param isUrl Pass true if the string s is a URL, false otherwise. If in doubt, use false.
   * @return The cleaned text
   */
  public static String clean(String s, boolean isUrl) {
    StringBuilder builder = new StringBuilder();
    Set<Integer> banned = isUrl ? urlEncode : normalEncode;
    for (char ch : s.toCharArray()) {
      if (banned.contains((int) ch)) {
        builder.append(urlEncodings.get(ch));
      } else if (ch == NBSP) { // Non-breaking space
        builder.append("&nbsp;");
      } else if (ch == NEW_LINE) {
        builder.append("<br>");
      } else {
        builder.append(ch);
      }
    }
    return builder.toString();
  }

  private void addRoleData(WikiReference wikiReference, Role role, List<Integer> indices) {
    List<String> firstNameList = new LinkedList<>();
    List<String> lastNameList = new LinkedList<>();
    // First, eliminate rows with no first or last name
    AuthorTableModel tableModel = getCurrentTableModel();
    for (int row : indices) {
      final String first = clean(tableModel.getValueAt(row, FIRST_COLUMN).toString(), false).trim();
      final String last = clean(tableModel.getValueAt(row, LAST_COLUMN).toString(), false).trim();
      if (!first.isEmpty() || !last.isEmpty()) {
        firstNameList.add(first);
        lastNameList.add(last);
      }
    }
    
    @NonNls String firstName = role.namePrefix + "first";
    @NonNls String lastName = role.namePrefix + "last";
    if (firstNameList.size() == 1) {
      String firstText = firstNameList.get(0);
      String lastText = lastNameList.get(0);
      if (!firstText.isEmpty()) {
        wikiReference.setKeyValuePair(firstName, firstText);
      }
      if (!lastText.isEmpty()) {
        wikiReference.setKeyValuePair(lastName, lastText);
      }
    } else {
      for (int row=0; row<firstNameList.size(); ++row) {
        int userIndex = row+1;
        String firstText = firstNameList.get(row);
        String lastText = lastNameList.get(row);
        if (!firstText.isEmpty()) {
          wikiReference.setKeyValuePair(firstName+userIndex, firstText);
        }
        if (!lastText.isEmpty()) {
          wikiReference.setKeyValuePair(lastName+userIndex, lastText);
        }
      }
    }
  }


  // Is this necessary?
  public void adjustTabPaneInitialSize() {
    tabPane.setMinimumSize(tabPane.getSize());
    tabPane.setPreferredSize(tabPane.getSize());
  }
  
  private String getText(Document doc) {
    try {
      return doc.getText(0, doc.getLength()).trim();
    } catch (BadLocationException e) {
      throw new IllegalStateException("Should not happen", e);
    }
  }

  private JComponent makeNamePane(JTextField textField) {
    JLabel nameLabel = new JLabel("Name: ");
    JPanel northPane = new JPanel(new BorderLayout());
    northPane.add(nameLabel, BorderLayout.LINE_START);
    northPane.add(textField, BorderLayout.CENTER);
    Borders.addEmptyBorder(northPane, 4);
    return northPane;
  }

  /**
   * <p>The CreatePane sits at the bottom-rite of the control pane, which itself sits at the bottom of the window.
   * It just has the "Create" button, on the right.</p> 
   * @return The CreatePane
   */
  @NotNull
  private JPanel makeCreatePane() {
    ActionListener actionListener = e -> buildReferenceText(resultField, getKeyField().getText().trim());
    JButton createButton = new JButton("Create and Copy");
    createButton.addActionListener(actionListener);
    JPanel createPane = new JPanel(new BorderLayout());
    JPanel trailingPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
    trailingPanel.add(createButton);
    createPane.add(trailingPanel, BorderLayout.LINE_END);

    createPane.add(makeControlUtilityPane(), BorderLayout.LINE_START);
    return createPane;
  }

  private @Nullable JTextComponent focusedTextComponent = null;
  private JComponent makeControlUtilityPane() {
    JPanel utilityPane = new JPanel(new FlowLayout());
    JButton toLowerCase = new JButton("lower case");
    JButton toUpperCase = new JButton("UPPER Case");
    JButton toTitleCase = new JButton("Title Case");
    toLowerCase.setRequestFocusEnabled(false);
    toTitleCase.setRequestFocusEnabled(false);
    toUpperCase.setRequestFocusEnabled(false);
    toLowerCase.setToolTipText("Convert selection to Lower Case");
    toUpperCase.setToolTipText("Convert selection to Upper Case");
    toTitleCase.setToolTipText("Convert selection to Title Case");
    toLowerCase.addActionListener(e -> toLowerCase());
    toUpperCase.addActionListener(e -> toUpperCase());
    toTitleCase.addActionListener(e -> toTitleCase());
    utilityPane.add(toLowerCase);
    utilityPane.add(toUpperCase);
    utilityPane.add(toTitleCase);

    final CaretListener caretListener = e -> processCaretEvent(e.getDot() != e.getMark(), toLowerCase, toUpperCase, toTitleCase);

    PropertyChangeListener propertyChangeListener = evt -> {
      if (focusedTextComponent != null) {
        focusedTextComponent.removeCaretListener(caretListener);
      }
      if (evt.getNewValue() instanceof JTextComponent textComponent) {
        focusedTextComponent = textComponent;
        focusedTextComponent.addCaretListener(caretListener);
        final Caret caret = focusedTextComponent.getCaret();
        processCaretEvent(caret.getDot() != caret.getMark(), toLowerCase, toUpperCase, toTitleCase);
      } else {
        focusedTextComponent = null;
        processCaretEvent(false, toLowerCase, toUpperCase, toTitleCase);
      }
    };

    final FocusManager currentManager = FocusManager.getCurrentManager();
    currentManager.addPropertyChangeListener("permanentFocusOwner", propertyChangeListener);
    return utilityPane;
  }
  
  private void processCaretEvent(boolean isSelection, JButton... buttons) {
    for (JButton button : buttons) {
      button.setEnabled(isSelection);
    }
  }

  private static void toLowerCase() {
    modifyText(String::toLowerCase);
  }

  private static void toUpperCase() {
    modifyText(String::toUpperCase);
  }

  private static void toTitleCase() {
    modifyText(RefBuilder::toTitleCase);
  }

  private static void modifyText(Function<String, String> mutator) {
    Component focusOwner = FocusManager.getCurrentManager().getPermanentFocusOwner();
    if (focusOwner instanceof JTextComponent textComponent) {
      final String selectedText = textComponent.getSelectedText();
      if (!selectedText.isEmpty()) {
        int selectionStart = textComponent.getSelectionStart();
        final String revisedText = mutator.apply(selectedText);
        textComponent.replaceSelection(revisedText);
        textComponent.select(selectionStart, selectionStart + revisedText.length());
      }
    }
  }
  
  private static String toTitleCase(String input) {
    String[] words = input.split(" ");
    StringBuilder builder = new StringBuilder();
    for (String word: words) {
      String lower = word.toLowerCase();
      builder.append(Character.toTitleCase(lower.charAt(0)));
      builder.append(lower.substring(1));
      builder.append(SPACE);
    }
    return builder.toString().trim();
  }

  private static Component hStrut(int length) {
    return Box.createHorizontalStrut(length);
  }
  
  private Document makeFixedField(JPanel content, String subject, String fieldName, boolean unexpected) {
//    System.out.printf("%s, %s%n", subject, fieldName); // NON-NLS
    JComponent label = makeFakeLabel(fieldName, unexpected);
    final GridBagConstraints constrain = constrain(0);
    constrain.fill = GridBagConstraints.HORIZONTAL; // Moves label to the top.
    content.add(label, constrain);
    SingleFieldDisplay valueField = new SingleFieldDisplay();
    content.add(valueField, constrain(1));
    keyMap.put(subject + DOT + fieldName, valueField.getDocument());
    String defaultValue = "";
    switch (fieldName) {
      case URL_STATUS -> defaultValue = "live";
      case ACCESS_DATE -> {
        Date today = new Date();
        DateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
        defaultValue = dateFormat.format(today);
      }
      default -> { }
    }
    if (!defaultValue.isEmpty()) {
      try {
        valueField.getDocument().insertString(0, defaultValue, new SimpleAttributeSet());
      } catch (BadLocationException e) {
        throw new InternalError("Should not happen", e);
      }
    }
    return valueField.getDocument();
  }

  private GridBagConstraints constrain(int column) {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = column;
    constraints.ipadx = 4;
        
    if (column == 0) {
      constraints.anchor = GridBagConstraints.NORTHWEST;
      constraints.ipady = 4;
    } else {
      constraints.weightx = 1.0;
    }
    constraints.fill = GridBagConstraints.BOTH;
    return constraints;
  }

  private void populateCommon(Map<String, Set<String>> theSubjectMap) {
    for (Set<String> set : theSubjectMap.values()) {
      for (String name : common) {
        set.add(name.trim());
      }
    }
  }

  private void populate(Map<String, Set<String>> theSubjectMap) {
    // sources each have a subject and a name, connected by a dot, like this: book.chapter, journal.issue
    for (String rawTag: sources) {
      Tag tag = new Tag(rawTag);
//      final int dotSpot = tag.indexOf(DOT);
      if (tag.getKey() == null) {
        theSubjectMap.put(tag.getSubject(), new LinkedHashSet<>());
      } else {
        String subject = tag.getSubject();
        Set<String> nameSet = theSubjectMap.containsKey(subject) ? theSubjectMap.get(subject) : mapNewSubject(theSubjectMap, subject);
        nameSet.add(tag.getKey());
      }
    }
  }

  private Set<String> mapNewSubject(Map<String, Set<String>> theSubjectMap, String subject) {
    Set<String> set = new LinkedHashSet<>();
    theSubjectMap.put(subject, set);
    return set;
  }
  
  private void showImportDialog() {
    final int MATTE_SIZE = 24;
    JTextArea textArea = new JTextArea(6, 80);
    JScrollPane scrollPane = scrollWrapTextArea(textArea);
    JPanel inputPanel = new JPanel(new GridBagLayout());
    Constrainer constraint = new Constrainer();
    inputPanel.add(new JLabel("Please Enter the Reference Text, from the <ref> to the </ref>, inclusive."), constraint.at(0, 0).gridSize(5, 1));
    inputPanel.add(scrollPane, new Constrainer(constraint).at(0, 1).weight(1.0, 1.0));
    inputPanel.add(Box.createVerticalStrut(6), constraint.at(1, 2));
    inputPanel.add(Box.createHorizontalGlue(), constraint.at(0, 3).gridSize(1, 1).weightX(10.0));
    Borders.insertEmptyBorder(inputPanel, MATTE_SIZE);
    final JButton cancel = new JButton("Cancel");
    inputPanel.add(cancel, constraint.at(3, 3).weightX(0.0));
    final JButton ok = new JButton("OK");
    inputPanel.add(ok, constraint.at(4, 3));
    inputPanel.add(Box.createHorizontalStrut(12), constraint.at(2, 3));

    JDialog dialog = new JDialog((JFrame)getRootPane().getParent());
    dialog.add(inputPanel, BorderLayout.CENTER);
    dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);

    ok.addActionListener(e -> doImport(dialog, textArea));
    cancel.addActionListener(e -> dialog.dispose());
    dialog.getRootPane().setDefaultButton(ok);

    dialog.pack();
    dialog.setVisible(true);
  }
  
  private void doImport(JDialog dialog, JTextArea textArea) {
    dialog.dispose();
    ReferenceParser parser = new ReferenceParser(textArea.getText());
    final List<WikiReference> refs = parseReferences(parser);
    if (!refs.isEmpty()) {
      Iterator<WikiReference> iterator = refs.iterator();
      WikiReference firstWikiReference = iterator.next();
      unpack(firstWikiReference);
      while (iterator.hasNext()) {
        WikiReference wikiReference = iterator.next();
        RefBuilder refBuilder = new RefBuilder();
        makeNewFrame(refBuilder);
        refBuilder.unpack(wikiReference);
      }
    }
  }

  private static List<WikiReference> parseReferences(ReferenceParser parser) {
    try {
      return parser.parse();
    } catch (IllegalStateException e) {
      Throwable exception = e;
      int count = parser.getCount();
      StringBuilder error = new StringBuilder(String.format("Unexpected error after %d characters.", count));
      do {
        error
            .append("<br>")
            .append(exception.getMessage());
        exception = exception.getCause();
      } while (exception != null);
      String stringSoFar = RefBuilder.clean(parser.getStringSoFar(), false);
      final int lengthSoFar = stringSoFar.length();
      if (lengthSoFar > 40) {
        //noinspection MagicCharacter
        stringSoFar = '…' + stringSoFar.substring(lengthSoFar - 37);
      }
      error
          .append("<br>Text so far:<br>")
          .append(stringSoFar);
      error
          .insert(0, "<html><p>")
          .append("</p></html>");
      JOptionPane.showMessageDialog(null, new JLabel(error.toString()), "Error", JOptionPane.ERROR_MESSAGE);
      return parser.getReferenceList(); // return every Reference that completed.
    }
  }

  void unpack(WikiReference reference) {
    RefKey citeType = reference.getRefKey();
    String citeName = citeType.name();
    RefTabPane selectedTab = null; 
    
    findTab:
    for (int i=0; i< tabPane.getTabCount(); ++i) {
      if (tabPane.getTitleAt(i).equals(citeName)) {
        citeName = citeType.toString();
        tabPane.setSelectedIndex(i);
        selectedTab = tabPane.getSelectedComponent();
        break findTab;
      }
    }
    
    if (selectedTab == null) {
      throw new IllegalStateException(String.format("No Selected Tab named %s", citeName));
    }
    if (citeName.isEmpty()) {
      throw new IllegalStateException("No Cite type Found");
    }
    getKeyField().setText(reference.getName());

    Set<String> tableKeys = new TreeSet<>(List.of("first", "last", "editor-first", "editor-last"));
    Map<String, Author> suffixToAuthorMap = new TreeMap<>();
    final List<String> dataKeys = reference.getDataKeys();
    Map<String, String> valueMap = reference.getDataMap();
    for (String key : dataKeys) {
      String value = valueMap.get(key);
      // First, see if it belongs in the table. "Normal" here means it doesn't belong in the table.
      boolean isNormal = true;
      for (String tableKey : tableKeys) {
        if (key.toLowerCase().startsWith(tableKey)) {
          isNormal = false;
          String suffix = key.substring(tableKey.length());
          @SuppressWarnings("MagicCharacter")
          Role role = (Character.toLowerCase(tableKey.charAt(0)) == 'e') ? Role.EDITOR : Role.WRITER;
          Author newAuthor = tableKey.contains("f") ? Author.ofFirstName(value, role) : Author.ofLastName(value, role);
          suffixToAuthorMap.merge(suffix, newAuthor, Author::remap);
        }
      }
      if (isNormal) {
        String nameKey = String.format("%s.%s", citeName, key);
        try {
          PlainDocument document = (PlainDocument) keyMap.get(nameKey);
          if (document == null) {
            document = (PlainDocument) makeFixedField(selectedTab.getTabContent(), citeName, key, true); // unexpected
          }
          if (value != null) {
            // access-date and url-status fields may have default text.
            // replace all existing text from any field. (Most will be blank.)
            int length = document.getLength();
            document.replace(0, length, value, null);
          }
        } catch (BadLocationException e) {
          throw new IllegalStateException(String.format("Could not insert %s", nameKey), e);
        }
      }
    }
    List<Author> authorList = new LinkedList<>();

    for (Author author : suffixToAuthorMap.values()) {
      if (author.getRole() == Role.WRITER) {
        authorList.add(author);
      }
    }
    for (Author author : suffixToAuthorMap.values()) {
      if (author.getRole() == Role.EDITOR) {
        authorList.add(author);
      }
    }
    AuthorTableModel tableModel = getCurrentTableModel();
    tableModel.populateAuthors(authorList);
  }
  
  @SuppressWarnings("MagicCharacter")
  private static Map<Character, String> createEncodings() {
    Map<Character, String> encodings = new HashMap<>();
    encodings.put('<', "&lt;");
    encodings.put('>', "&gt;");
    encodings.put('\u00a0', "&nbsp;");
    encodings.put('\u00ad', "&shy;");
    return encodings;
  }
  
  private static class ReferenceTabbedPane extends JTabbedPane {
    @Override
    public RefTabPane getSelectedComponent() {
      return (RefTabPane) super.getSelectedComponent();
    }
  }
  
  /**
   * <p>A DisplayComponent consists of a text editor and a checkbox called "Big." The check box toggles the 
   * text editor between a JTextField and a JTextArea, both of which share the same data model, the Document.</p>
   */
  private static class SingleFieldDisplay extends JPanel {
    private final ButtonModel buttonModel;
    private final Document document;
    
    private static final DocumentFilter mainDocumentFilter = getMainDocumentFilter();

    SingleFieldDisplay() {
      super(new BorderLayout());
      JTextField textField = new JTextField(1);
//      System.out.printf("InputMap size = %d%n", textField.getInputMap(JComponent.WH).size()); // NON-NLS
//      System.out.printf("InputMap revised size = %d%n", textField.getInputMap().size()); // NON-NLS
//      showInputMapSizes(textField);
//      textField.addHierarchyListener(new HierarchyListener() {
//        @Override
//        public void hierarchyChanged(HierarchyEvent e) {
////          System.out.printf("Final InputMapSize = %d%n%n", textField.getInputMap().size()); // NON-NLS
//          System.out.println("Final Sizes:");
//          showInputMapSizes(textField);
//          Component parent = textField.getParent();
//          while (parent instanceof JComponent pc) {
//            showInputMapSizes(pc);
//            parent = parent.getParent();
//          }
//        }
//      });
      document = textField.getDocument();
      ((PlainDocument)document).setDocumentFilter(mainDocumentFilter);

      add(textField, BorderLayout.CENTER);
      JCheckBox big = new JCheckBox("Big");
      big.setToolTipText("Enlarges this Text field for more text");
      big.setSelected(false);
      buttonModel = big.getModel();
      big.addItemListener(e -> toggleBig(e, buttonModel));
      Utils.addTopRight(this, big);
//      add(big, BorderLayout.LINE_END);
    }
    
    private void showInputMapSizes(JComponent cmp) {
      final int[] conditions = {
          JComponent.WHEN_FOCUSED,
          JComponent.WHEN_IN_FOCUSED_WINDOW,
          JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT};
      for (int i: conditions) {
        //noinspection MagicConstant
        System.out.printf("For %s inputMap %d, InputMap size = %d%n", cmp.getClass().getSimpleName(), i, cmp.getInputMap(i).size()); // NON-NLS
      }
      System.out.println();
    }

    private static DocumentFilter getMainDocumentFilter() {
      return new DocumentFilter() {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
          fb.insertString(offset, filter(string), attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
          fb.replace(offset, length, filter(text), attrs);
        }

        private String filter(String string) {
          StringBuilder builder = new StringBuilder();
          for (char c : string.toCharArray()) {
            if (forbidden.contains((int) c)) {
              builder.append(SPACE);
            } else {
              builder.append(c);
            }
          }
          return builder.toString();
        }
      };
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

  /**
   * <p>Create a scrollable version of a JPanel with sensible scrolling defaults. This needs to be added to
   * a JScrollPane to be useful. The JScrollPane is not included in this class. It scrolls vertically only.
   * Clicking the up or down arrow moves the field by a single line of text. The block scrolling size adjusts to
   * changes in the panel's preferred size.</p>
   */
  private class ScrollingPane extends JPanel implements Scrollable {
    private final int unitIncrement = 13;
    ScrollingPane(LayoutManager layoutManager) {
      super(layoutManager);
      setBackground(textFieldBgColor);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
      return unitIncrement;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
      return visibleRect.height - unitIncrement;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
      return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
      return false;
    }
  }

  @NotNull
  private static JScrollPane scrollWrapTextArea(JTextArea textArea) {
    textArea.setWrapStyleWord(true);
    textArea.setLineWrap(true);
    return new JScrollPane(
        textArea,
        VERTICAL_SCROLLBAR_ALWAYS,
        HORIZONTAL_SCROLLBAR_NEVER);
  }

  private JComponent makeFakeLabel(String text, boolean unexpected) {
    // I don't remember why I decided to use a JTextField that looks and acts like a JLabel, but it
    // may have had something to do with the Aqua look and feel.
    JTextField textField = new JTextField(text);
    textField.setEnabled(false);
    textField.setBackground(fakeLabelColor);
    if (unexpected) {
      textField.setForeground(Color.RED.darker()); // Figure out why this doesn't work.
    }
    textField.setOpaque(false);
    textField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
  
    return textField;
  }
  
  private JTextField getKeyField() {
    RefTabPane selectedPane = tabPane.getSelectedComponent();
    return selectedPane.getNameField();
  }

  /**
   * The AuthorNameEditorPane contains a table with all the keys used to specify the names of authors and
   * editors. I handle these separately because they take a different form than most tags, because there
   * may be multiple authors and multiple editors. If there is only on author or editor, the first and
   * last name keys will be "first" and "last" for an author, or "editor-first" and "editor-last" for an
   * editor. But if there are multiple people for either category, then these field keys get a number after
   * the key. So they could be "first1", "last1", "first2", "last2" and so on. The same goes for editors.
   */
  private static final class AuthorNameEditorPane extends JPanel {

    private final JTable table;

    private AuthorNameEditorPane(List<Runnable> terminatorOperationList, AuthorTableModel tableModel) {
      super(new BorderLayout());
      JLabel authorLabel = new JLabel("Writers and Editors:");
      add(authorLabel, BorderLayout.NORTH);
      table = new JTable(tableModel);
      table.setFillsViewportHeight(true);
      table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
      table.setCellSelectionEnabled(true);
      table.setDefaultRenderer(Object.class, new EmptyCellRenderer());
      FocusListener tableFocusListener = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
          table.getSelectionModel().clearSelection();
        }
      };
      table.addFocusListener(tableFocusListener);

      JScrollPane scrollPane
          = new JScrollPane(table, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
      table.setPreferredScrollableViewportSize(table.getPreferredSize());
      add(scrollPane, BorderLayout.CENTER);
      tableModel.addRowCountListener(()-> {
        // This readjusts the size of the enclosing JScrollPane to match the JTable. It's what allows us
        // to expand the table's screen size when the user adds a row or when importing data.
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.revalidate();
        revalidate();
        paintImmediately(getBounds());
      });

      final TableColumnModel columnModel = table.getColumnModel();
      final TableColumn roleColumn = columnModel.getColumn(2);
      JComboBox<Role> roleEditor = new JComboBox<>(new Role[]{Role.WRITER, Role.EDITOR});
      roleColumn.setCellEditor(new DefaultCellEditor(roleEditor));
      
      // Make the first two columns wide enough to make the full blank-text string visible.
      // The default width is 1/3 of 580, or about 193.
      columnModel.getColumn(0).setPreferredWidth(250);
      columnModel.getColumn(1).setPreferredWidth(250);
      final JTableHeader tableHeader = table.getTableHeader();
      tableHeader.setReorderingAllowed(false);

      Runnable terminationOperation = () -> {
        TableCellEditor editor = table.getCellEditor();
        if (editor != null) {
          editor.stopCellEditing();
        }
      };
      terminatorOperationList.add(terminationOperation);
    }
  }

  /**
   * The EmptyCellRenderer puts in a line of gray text on the last line of the table that
   * invites the user to add new entries.
   */
  private static class EmptyCellRenderer extends DefaultTableCellRenderer {
    EmptyCellRenderer() {
      super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      String cellString = value.toString();
      boolean isEmpty = cellString.isEmpty() && ((column == 0) && (row == (table.getRowCount() - 1)));
      if (isEmpty) {
        cellString = "Add new writer/editor names here…";
      } else {
        // When I set the last line's foreground to gray, it fails to change it the next time the renderer gets used.
        // So I change it before calling the super method.
        if (isSelected) {
          setForeground(table.getSelectionForeground());
        } else {
          setForeground(table.getForeground());
        }
      }
//      Color fg = this.getForeground();
//      if (fg == null) { fg = Color.BLACK; }
      final Component tableCellRendererComponent = super.getTableCellRendererComponent(table, cellString, isSelected, hasFocus, row, column);
      if (isEmpty) {
        JLabel label = (JLabel) tableCellRendererComponent;
        label.setForeground(new ColorUIResource(Color.gray));
      }
      return tableCellRendererComponent;
    }
  }

  /**
   * <p>The Author class handles first and last names for authors and editors. The role of author or editor
   * is handled by the Role field, which has values of "Writer" and "Editor"</p>
   */
  private static class Author {
    private String first="";
    private String last="";
    private Role role = Role.WRITER;

    public String getFirst() {
      return first;
    }

    public void setFirst(String first) {
      this.first = avoidNull(first);
    }

    public String getLast() {
      return last;
    }

    public void setLast(String last) {
      this.last = avoidNull(last);
    }

    public Role getRole() {
      return role;
    }

    public void setRole(Role role) {
      this.role = role;
    }
    
    public static Author ofFirstName(String firstName, Role role) {
      Author author = new Author();
      author.setFirst(firstName);
      author.setRole(role);
      return author;
    }
    
    public static Author ofLastName(String lastName, Role role) {
      Author author = new Author();
      author.setLast(lastName);
      author.setRole(role);
      return author;
    }
    
    public static Author remap(Author existing, Author newAuthor) {
      return existing.copyFrom(newAuthor);
    }
    
    public Author copyFrom(Author newAuthor) {
      if (!newAuthor.getFirst().isEmpty()) {
        setFirst(newAuthor.getFirst());
      }
      if (!newAuthor.getLast().isEmpty()) {
        setLast(newAuthor.getLast());
      }
      return this;
    }
  }
  
  private static String avoidNull(String s) {
    if (s == null) { return ""; }
    return s;
  }

  /**
   * <p>The AuthorTableModel is the TableModel for the JTable that holds the first and last names of all the
   * authors and editors. For ease of maintenance, this model divides the data into separate row models and 
   * column models. The RowModel is a list of {@code Author} instances, and the columns are instances of
   * {@code TableColumn}.</p>
   */
  private static class AuthorTableModel extends AbstractTableModel {
    private final List<AuthorTableColumn<Author, ?>> columnList = new ArrayList<>();
    private final List<Author> rowModel = new ArrayList<>();
    
    private final List<Runnable> rowCountListenerList = new LinkedList<>();
    
    AuthorTableModel() {
      super();

      columnList.add(new AuthorTableColumn<>(String.class, "First", Author::getFirst, Author::setFirst));
      columnList.add(new AuthorTableColumn<>(String.class, "Last", Author::getLast, Author::setLast));
      columnList.add(new AuthorTableColumn<>(Role.class, "Role", Author::getRole, Author::setRole));
    }

    @Override
    public int getColumnCount() {
      return columnList.size();
    }

    @Override
    public int getRowCount() {
      return rowModel.size() + 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (rowIndex == rowModel.size()) {
        return "";
      }
      final Author author = rowModel.get(rowIndex);
      return columnList.get(columnIndex).getter.apply(author);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      if (rowIndex == rowModel.size()) {
        if (aValue.toString().isBlank()) {
          // Don't put blank cells in the table in the last row. It defeats the purpos of the last row.
          return;
        }
        final Author blankAuthor = new Author();
        
        // Role defaults to the value from the previous row, or WRITER for row zero.
//        Role defaultRole = (rowIndex == 0) ? Role.WRITER : (Role) getValueAt(rowIndex - 1, ROLE_COLUMN);
//        blankAuthor.setRole(defaultRole);
        rowModel.add(blankAuthor);
      }
      final Author author = rowModel.get(rowIndex);
      setValueImpl(aValue, columnIndex, author);
      fireTableRowsInserted(rowIndex+1, rowIndex+1);
      fireLocalRowCountUpdated();
    }

    private void fireLocalRowCountUpdated() {
      for (Runnable listener: rowCountListenerList) {
        listener.run();
      }
    }

    private <T> void setValueImpl(T aValue, int columnIndex, Author author) {
      @SuppressWarnings("unchecked")
      final AuthorTableColumn<Author, T> column = (AuthorTableColumn<Author, T>) columnList.get(columnIndex);

      // JTable won't call this method if column.setter is null, but this won't compile without the test.
      if (column.setter != null) {
        column.setter.accept(author, column.valueClass.cast(aValue));
      }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      // Role column is not editable in the last row.
      return ((rowIndex < rowModel.size()) || (columnIndex < 2)) && columnList.get(columnIndex).isEditable();
    }

    @Override
    public String getColumnName(int column) {
      return columnList.get(column).getTitle();
    }
    
    public void addRowCountListener(Runnable listener) {
      rowCountListenerList.add(listener);
    }
    
    public void populateAuthors(List<Author> authors) {
//      int rowCount = authors.size();
      rowModel.clear();
      rowModel.addAll(authors);
//      fireTableRowsUpdated(0, rowCount - 1);
//      fireTableRowsInserted(rowCount, rowModel.size() - rowCount);
      fireTableRowsUpdated(0, rowModel.size() - 1);
      fireLocalRowCountUpdated();
    }
  }

  /**
   * <p>This class implements the ColumnModel of the AuthorTableModel.</p>
   * @param <R> The RowModel Type
   * @param <C>The class of the items held in the column.
   */
  private static class AuthorTableColumn<R, C> {
    private final String title;
    final Class<C> valueClass;
    final Function<R, C> getter;
    @Nullable
    final BiConsumer<R, C> setter;
    
    AuthorTableColumn(Class<C> vClass, String title, Function<R, C> getter, @Nullable BiConsumer<R, C> setter) {
      this.title = title;
      this.valueClass = vClass;
      this.getter = getter;
      this.setter = setter;
    }
    
    public boolean isEditable() {
      return setter != null;
    }
    
    public String getTitle() {
      return title;
    }
  }

  /**
   * <p>The Role of each Author or Editor. </p>
   */
  private enum Role {
    WRITER(""),
    EDITOR("editor-"),
    NONE("");
    
    final @NonNls String namePrefix;
    Role(String namePrefix) {
      this.namePrefix = namePrefix;
    }


    @Override
    public String toString() {
      if (this == NONE) {
        return "";
      }
      return super.toString();
    }
  }

  /**
   * <p>Class to hold a key/value pair.</p>
   */
  private static class Tag {
    private final String subject;
    @Nullable
    private final String key;
    
    private final int hash;
    
    Tag(String tag) {
      String[] fields = tag.split(DELIMITER);
      subject = fields[0].trim();
      if (fields.length == 1) {
        key = null;
      } else {
        key = fields[1].trim();
      }
      hash = Objects.hash(subject, key);
    }

    public String getSubject() {
      return subject;
    }

    @Nullable
    public String getKey() {
      return key;
    }

    @Override
    public boolean equals(Object o) {

      if (this == o) {
        return true;
      }
      if (!(o instanceof Tag that)) {
        return false;
      } // implicitly checks for null

      if (!subject.equals(that.subject)) {
        return false;
      }
      return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
      return hash;
    }
  }

  /**
   * The class added to each tab in the JTabbedPane. Calling {@code getSelectedPane()} returns an instance of
   * this class.
   */
  private class RefTabPane extends JPanel {
    private final JTextField nameField = new JTextField();
    private final JPanel tabContent;

    RefTabPane(String subject) {
      super(new BorderLayout());
      Borders.addEmptyBorder(this, 12);

      add(makeNamePane(nameField), BorderLayout.PAGE_END);

      tabContent = new ScrollingPane(new GridBagLayout());
      Borders.addEmptyBorder(tabContent, 0, 4, 0, 4);

      GridBagConstraints tableConstraint = (constrain(0));
      tableConstraint.gridwidth = 3;
      tableConstraint.ipadx = 0;
      tableConstraint.ipady = 0;
      AuthorTableModel tableModel = new AuthorTableModel();
      tableModelMap.put(subject, tableModel);
      AuthorNameEditorPane editorPane = new AuthorNameEditorPane(editorTerminatorOperations, tableModel);
      tabContent.add(editorPane, tableConstraint);
      tabContent.add(hStrut(1), constrain(0));
      tabContent.add(hStrut(TEXT_FIELD_LENGTH), constrain(1));
      Set<String> nameSet = subjectMap.get(subject);
      for (String name : nameSet) {
        makeFixedField(tabContent, subject, name, false);
      }

      // We create one last invisible row and give the components a weighty of 1.0 to push everything 
      // above it to the top of the tab pane.
      GridBagConstraints strutConstraint = constrain(0);
      strutConstraint.weighty = 1.0f;
      tabContent.add(new JLabel(" "), strutConstraint);
      strutConstraint.gridx = 1;
      tabContent.add(new JLabel(" "), strutConstraint);
      // We give it a second column, so we can add custom fields after it.
//      final GridBagConstraints vConstraint = constrain(0);
//      vConstraint.weighty = 1.0f;
      
      final JScrollPane scrollPane = new JScrollPane(tabContent, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
      scrollPane.getViewport().setBackground(textFieldBgColor);
      add(scrollPane, BorderLayout.CENTER);
    }
    
    JTextField getNameField() {
      return nameField;
    }
    
    JPanel getTabContent() { return tabContent; }
  }
}


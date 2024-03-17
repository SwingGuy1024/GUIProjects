package com.neptunedreams.refBuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.swing.Box;
import javax.swing.ButtonModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Utility to build Wikipedia Links using {@code cite}</p>
 * <p>Cite Subjects:</p>
 * <p>
 *   web<br>
 *   news<br>
 *   book<br>
 *   journal<br>
 * </p>
 * <p>This class defines the following terms:<br>
 * <strong>subject:</strong> One of the four types of "cite" values: web, news, book, and journal.<br>
 * <strong>name:</strong> The name of a value in a citation, such as title in {{cite news | title = Trump Convicted}}<br>
 * <strong>value:</strong> The value for a name in a citation, such as "Trump Convicted" in the previous example.<br>
 * <strong>tag:</strong> A combination of a subject and a name, such as news.title<br>
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 3/13/24</p>
 * <p>Time: 4:01&nbsp;AM</p>
 * <p>@author Miguel Muñoz</p>
 */
@SuppressWarnings("StringConcatenation")
public class RefBuilder extends JPanel {

  // Todo: Add buttons for lower case and title case. 
  // DONE: Add Multiples
  // Todo: Add a Date Utility
  // TODO: Add URL Encoder. From 32 to 47  and 58 to 64 and 123 to 126 Need to experiment with > 128
  //       Encode <>&()\#%[\]^`{|} 0xa0, 0xad (nbs and soft hyphen) & anything above 0xff
  // TODO: Move subject-specific tags to the end. Maybe split off access and archive names into a second,
  //       minor branch so they may be put at the end.
  // TODO: Move page and pages & volume to news, journal, and book.
  // TODO: Add a custom field?
  // TODO: Try another L&F
  // Done: Put tab pane in a ScrollPane!
  
  // Cite subjects: book, news, journal, web
  
//  private static final Set<String> authorPair = new HashSet<>(List.of("first", "last"));
//  private static final Set<String> editorPair = new HashSet<>(List.of("editor-first", "editor-last"));
//  private static final Set<Set<String>> numeric = new HashSet<>(List.of(authorPair, editorPair));
  
  private static final Set<String> common = new LinkedHashSet<>(
      List.of("title", "year", "date", "url", "page", "pages", "volume", "language", "publisher", 
          "access-date", "url-access", "url-status", "archive-url", "archive-date", "ref")
  );
  private static final Set<String> sources
      = new LinkedHashSet<>(List.of("book.isbn", "book.location", "book.orig-year", "book.edition",
      "book.oclc", "book.chapter", "book.chapter-url", "book.author-link", "journal.issue", "journal.doi",
      "journal.doi-access", "journal.issn", "journal.bibcode", "news.newspaper", "news.agency", "news.work", "web")
  );
  public static final String DELIMITER = "\\.";
  public static final char DOT = '.';
  public static final int TEXT_FIELD_LENGTH = 500;
  private static final AuthorTableModel tableModel = new AuthorTableModel();
  public static final int FIRST_COLUMN = 0;
  public static final int LAST_COLUMN = 1;
  public static final int ROLE_COLUMN = 2;
  
  private final Color textFieldForeground;

  private final Color textFieldBgColor;
  private final JTabbedPane tabPane = new JTabbedPane();

  private final Map<String, Set<String>> subjectMap;
  private final @NonNls Map<String, Document> nameMap = new HashMap<>();
  
  private final List<Runnable> editorTerminatorOperations = new LinkedList<>();

//  private final Map<String, Integer> multipleMap = new HashMap<>();
  public static void main(String[] args) {
    JFrame frame = new JFrame("Reference Builder");
    final RefBuilder refBuilder = new RefBuilder();
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
      }
    };
    frame.addHierarchyListener(hierarchyListener);
    frame.setVisible(true);
  }

  RefBuilder() {
    super(new BorderLayout());
    UIDefaults uiDefaults = UIManager.getDefaults();
    textFieldForeground = uiDefaults.getColor("TextField.foreground");
    textFieldBgColor = uiDefaults.getColor("Label.background");
    uiDefaults.put("TextField.inactiveForeground", textFieldForeground);
    subjectMap = new HashMap<>();
    populate(subjectMap);
    populateCommon(subjectMap);
    for (String subject: subjectMap.keySet()) {
      tabPane.add(subject, makeTabContent(subject));
    }
    add(tabPane, BorderLayout.CENTER);
    add(makeControlPane(), BorderLayout.PAGE_END);
  }

  private JComponent makeTabContent(String subject) {
    JPanel tabContent = new ScrollingPane(new GridBagLayout());

    GridBagConstraints tableConstraint = (constrain(0));
    tableConstraint.gridwidth = 3;
    tableConstraint.ipadx = 0;
    tableConstraint.ipady = 0;
    tabContent.add(new AuthorNameEditorPane(editorTerminatorOperations), tableConstraint);
    tabContent.add(hStrut(1), constrain(0));
    tabContent.add(hStrut(TEXT_FIELD_LENGTH), constrain(1));
    Set<String> nameSet = subjectMap.get(subject);
    for (String name: nameSet) {
      makeFixedField(tabContent, subject, name);
    }
    
    // We create one last invisible row and give it a weighty of 1.0 to push everything above it to the top of the
    // tab pane.
    GridBagConstraints constraints = constrain(1);
    constraints.weighty = 1.0f;
    tabContent.add(hStrut(1), constraints);
    final JScrollPane scrollPane = new JScrollPane(tabContent, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.getViewport().setBackground(textFieldBgColor);
    return scrollPane;
  }

  private Component makeControlPane() {
    JPanel controlPane = new JPanel(new BorderLayout());
    JTextField nameField = new JTextField();
    controlPane.add(makeControlNorthPane(nameField), BorderLayout.PAGE_START);
    
    JTextArea result = new JTextArea(6, 0);
    JComponent scrollPane = scrollWrapTextArea(result);
    controlPane.add(scrollPane, BorderLayout.CENTER);
    ActionListener actionListener = e -> buildReferenceText(result, nameField.getText().trim());

    JPanel goPane = makeGoPane(actionListener);
    controlPane.add(goPane, BorderLayout.PAGE_END);
    return controlPane;
  }

  private void buildReferenceText(JTextArea resultView, String name) {
    for (Runnable runner: editorTerminatorOperations) {
      runner.run();
    }

    StringBuilder builder = new StringBuilder();

    // Count Writers and Editors
    List<Integer> writerIndices = new LinkedList<>();
    List<Integer> editorIndices = new LinkedList<>();
    for (int row = 0; row < (tableModel.getRowCount() - 1); ++row) {
      final Object valueAt = tableModel.getValueAt(row, 2);
      Role role = (Role) valueAt;
      if (role == Role.WRITER) {
        writerIndices.add(row);
      } else if (role == Role.EDITOR) {
        editorIndices.add(row);
      }
    }
    addRoleData(builder, Role.WRITER, writerIndices);
    addRoleData(builder, Role.EDITOR, editorIndices);

    String tab = tabPane.getTitleAt(tabPane.getSelectedIndex());
    for (String key: nameMap.keySet()) {
//      System.out.printf("ValueMap: %s%n", key); // NON-NLS
      if (key.startsWith(tab)) {
        Document doc = nameMap.get(key);
        final String fieldText = getText(doc);
        if (!fieldText.isEmpty()) {
          String fieldName = key.substring(key.indexOf(DOT) + 1);
          appendPair(builder, fieldName, fieldText);
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
    resultView.setText(referenceText);
    StringSelection stringSelection = new StringSelection(referenceText);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, stringSelection);
  }

  private static void appendPair(StringBuilder builder, String fieldName, String fieldText) {
    builder.append(" | ")
        .append(fieldName)
        .append(" = ")
        .append(fieldText);
  }

  private void addRoleData(StringBuilder builder, Role role, List<Integer> indices) {
    // First, eliminate rows with no first or last name
    for (Iterator<Integer> itr = indices.iterator(); itr.hasNext();) {
      int row = itr.next();
      final String first = tableModel.getValueAt(row, FIRST_COLUMN).toString().trim();
      final String last = tableModel.getValueAt(row, LAST_COLUMN).toString().trim();
      if (first.isEmpty() && last.isEmpty()) {
        itr.remove();
      }
    }
    
    String firstName = role.namePrefix + "first";
    String lastName = role.namePrefix + "last";
    if (indices.size() == 1) {
      int row = indices.get(0);
      String firstText = tableModel.getValueAt(row, FIRST_COLUMN).toString().trim();
      String lastText = tableModel.getValueAt(row, LAST_COLUMN).toString().trim();
      if (!firstText.isEmpty()) {
        appendPair(builder, firstName, firstText);
      }
      if (!lastText.isEmpty()) {
        appendPair(builder, lastName, lastText);
      }
    } else {
      for (int i=0; i<indices.size(); ++i) {
        int userIndex = i+1;
        int row = indices.get(i);
        String firstText = tableModel.getValueAt(row, FIRST_COLUMN).toString().trim();
        String lastText = tableModel.getValueAt(row, LAST_COLUMN).toString().trim();
        if (!firstText.isEmpty()) {
          appendPair(builder, firstName+userIndex, firstText);
        }
        if (!lastText.isEmpty()) {
          appendPair(builder, lastName+userIndex, lastText);
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

  private void makeFixedField(JPanel content, String subject, String name) {
    JComponent label = makeFakeLabel(name);
    content.add(label, constrain(0));
    DisplayComponent valueField = new DisplayComponent();
    content.add(valueField, constrain(1));
    nameMap.put(subject + DOT + name, valueField.getDocument());
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
    for (String subject: theSubjectMap.keySet()) {
      Set<String> set = theSubjectMap.get(subject);
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
      if (tag.getName() == null) {
        theSubjectMap.put(tag.getSubject(), new LinkedHashSet<>());
      } else {
        String subject = tag.getSubject();
        Set<String> nameSet = theSubjectMap.containsKey(subject) ? theSubjectMap.get(subject) : mapNewSubject(theSubjectMap, subject);
        nameSet.add(tag.getName());
      }
    }
  }
  
  private Set<String> mapNewSubject(Map<String, Set<String>> theSubjectMap, String subject) {
    Set<String> set = new LinkedHashSet<>();
    theSubjectMap.put(subject, set);
    return set;
  }

//  public void printMap() {
//    for (String subject: subjectMap.keySet()) {
//      Set<String> nameSet = subjectMap.get(subject);
//      for (String name: nameSet) {
//        System.out.printf("%s.%s%n", subject, name); // NON-NLS
//      }
//    }
//  }

  /**
   * <p>A DisplayComponent consists of a text editor and a checkbox called "Big." The check box toggles the 
   * text editor between a JTextField and a JTextArea, both of which share the same data model, the Document.</p>
   */
  private static class DisplayComponent extends JPanel {
    private final ButtonModel buttonModel;
    private final Document document;

    DisplayComponent() {
      super(new BorderLayout());
      JTextField textField = new JTextField(1);
      document = textField.getDocument();

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
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
  }

  private JComponent makeFakeLabel(String text) {

    JTextField textField = new JTextField(text);
    textField.setEnabled(false);
    textField.setBackground(textFieldBgColor);
    textField.setForeground(textFieldForeground);
    Border textFieldBorder = textField.getBorder();
//    Insets insets = textFieldBorder.getBorderInsets(textField);
    if (textFieldBorder.getClass().toString().contains("Aqua")) {
      textFieldBorder = new MatteBorder(5, 5, 5, 5, textFieldBgColor);
      textField.setBorder(textFieldBorder);
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
  
  private static final class AuthorNameEditorPane extends JPanel {
    private AuthorNameEditorPane(List<Runnable> terminatorOperationList) {
      super(new BorderLayout());
      JTable table = new JTable(tableModel);
      table.setFillsViewportHeight(true);
      table.setPreferredScrollableViewportSize(getPreferredSize());
      table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      JScrollPane scrollPane
          = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      add(scrollPane, BorderLayout.CENTER);
      tableModel.addRowCountListener(()-> {
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        revalidate();
        paintImmediately(getBounds());
      });
      
      table.addHierarchyListener(new HierarchyListener() {
        @Override
        public void hierarchyChanged(HierarchyEvent e) {
          table.setPreferredScrollableViewportSize(table.getPreferredSize());
          table.removeHierarchyListener(this); // I can't do "this" in a lambda!
        }
      });
//      JTableHeader tableHeader = table.getTableHeader();

      JComboBox<Role> roleEditor = new JComboBox<>(new Role[]{Role.WRITER, Role.EDITOR});
      table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(roleEditor));
      
      Runnable terminationOperation = () -> {
        TableCellEditor editor = table.getCellEditor();
        if (editor != null) {
          editor.stopCellEditing();
        }
      };
      terminatorOperationList.add(terminationOperation);
    }
  }
  
  private static class Author {
    private String first="";
    private String last="";
    private Role role = Role.NONE;

    public String getFirst() {
      return first;
    }

    public void setFirst(String first) {
      this.first = first;
    }

    public String getLast() {
      return last;
    }

    public void setLast(String last) {
      this.last = last;
    }

    public Role getRole() {
      return role;
    }

    public void setRole(Role role) {
      this.role = role;
    }
  }
  
  private static class AuthorTableModel extends AbstractTableModel {
    private final List<TableColumn<Author, ?>> columnList = new ArrayList<>();
    private final List<Author> rowModel = new ArrayList<>();
    
    private final List<Runnable> rowCountListenerList = new LinkedList<>();
    
    AuthorTableModel() {
      super();

      columnList.add(new TableColumn<>(String.class, "First", Author::getFirst, Author::setFirst));
      columnList.add(new TableColumn<>(String.class, "Last", Author::getLast, Author::setLast));
      columnList.add(new TableColumn<>(Role.class, "Role", Author::getRole, Author::setRole));
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
        return (columnIndex == ROLE_COLUMN) ? Role.NONE : "";
      }
      final Author author = rowModel.get(rowIndex);
      return columnList.get(columnIndex).getter.apply(author);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      if (rowIndex == rowModel.size()) {
        final Author blankAuthor = new Author();
        
        // Role defaults to the value from the previous row, or WRITER for row zero.
        Role defaultRole = (rowIndex == 0) ? Role.WRITER : (Role) getValueAt(rowIndex - 1, ROLE_COLUMN);
        blankAuthor.setRole(defaultRole);
        rowModel.add(blankAuthor);
      }
      final Author author = rowModel.get(rowIndex);
      setValueImpl(aValue, columnIndex, author);
      fireTableRowsInserted(rowIndex+1, rowIndex+1);
      for (Runnable listener: rowCountListenerList) {
        listener.run();
      }
    }

    private <T> void setValueImpl(T aValue, int columnIndex, Author author) {
      @SuppressWarnings("unchecked")
      final TableColumn<Author, T> column = (TableColumn<Author, T>) columnList.get(columnIndex);

      // JTable won't call this method if column.setter is null, but this won't compile without the test.
      if (column.setter != null) {
        column.setter.accept(author, column.valueClass.cast(aValue));
      }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return columnList.get(columnIndex).isEditable();
    }

    @Override
    public String getColumnName(int column) {
      return columnList.get(column).getTitle();
    }
    
    public void addRowCountListener(Runnable listener) {
      rowCountListenerList.add(listener);
    }
  }
  
  private static class TableColumn<R, C> {
    private final String title;
    final Class<C> valueClass;
    final Function<R, C> getter;
    @Nullable
    final BiConsumer<R, C> setter;
    
    TableColumn(Class<C> vClass, String title, Function<R, C> getter, @Nullable BiConsumer<R, C> setter) {
      this.title = title;
      this.valueClass = vClass;
      this.getter = getter;
      this.setter = setter;
    }
    
//    TableColumn(Class<C> vClass, String title, Function<R, C> getter){
//      this(vClass, title, getter, null);
//    }
    
    public boolean isEditable() {
      return setter != null;
    }
    
    public String getTitle() {
      return title;
    }
  }
  
  private enum Role {
    WRITER(""),
    EDITOR("editor-"),
    NONE("");
    
    final String namePrefix;
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
  
  private static class Tag {
    private final String subject;
    @Nullable
    private final String name;
    
    private final int hash;
    
    Tag(String tag) {
      String[] fields = tag.split(DELIMITER);
      subject = fields[0].trim();
      if (fields.length == 1) {
        name = null;
      } else {
        name = fields[1].trim();
      }
      hash = Objects.hash(subject, name);
    }

    public String getSubject() {
      return subject;
    }

    @Nullable
    public String getName() {
      return name;
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
      return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
      return hash;
    }
  }
}


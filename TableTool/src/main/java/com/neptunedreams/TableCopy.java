package com.neptunedreams;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import com.mm.gui.Borders;
import com.mm.gui.Utils;
import com.mm.gui.table.AbstractFiringTableModel;
import com.mm.gui.table.FiringTable;

/**
 * <p>Tool to make it easier to copy tabular data from a web page to a spreadsheet or other application that supports 
 * tab-delimited text.</p>
 * <p>This is not a problem with web tables made using the html table tag. But many tables appear using other
 * tags, and when these are copied, they usually put each cell on its own line. This class makes it easy to 
 * convert table cell data back into tab-delimited form for pasting in other applications. While it doesn't
 * figure out how many columns are in the table, it lets the user specify, and shows them how the table would
 * look with that many columns, making it easier to avoid mistakes.</p>
 * <p>Possible ways to detect column width:</p>
 * <p><b>For flex tables,</b> count how many flex-cell tag attributes there are between successive flex-row tag
 * attributes.<br>
 * <b>Note: </b>The necessary data to deduce the number of columns can be found in the html fragment that gets
 * put on the clipboard, so this looks viable.</p>
 * <b>Note 2: </b>This worked well.
 * <p><b>For CSS Grid Tables,</b> find an element like this: {@code "grid-template-columns: 2fr 1fr 1fr;"} and count the
 * number of words between grid-template-columns and the semicolon.<br>
 * <b>Note: </b>This doesn't work, because the grid-template-columns attribute appears before any of the table
 * contents. This means it doesn't get copied to the clipboard. The data on the clipboard doesn't have any
 * clues showing where the lines break.</p>
 * <p>However, If the user includes the entire table header line, this can be used to deduce the number of columns.
 * There will be one {@code grid-header} for each table column.</p>
 * <p>I have not yet implemented this, although it should be pretty easy.</p>
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 4/20/26
 * <br>Time: 3:15 AM
 * <br>@author Miguel Muñoz (<a href="https://github.com/SwingGuy1024">https://github.com/SwingGuy1024</a>)</p>
 */
@SuppressWarnings("MagicNumber")
public class TableCopy extends JPanel {

  private static final String TAB = "\t";
  private static final String LINE_BREAK = "\n";
  private static final String flexRowDelimiter = "flex-row";
  private static final String flexCellDelimiter = "flex-cell";
  private static final String GRID_HEADER = "grid-header";
  private static final Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

  private final List<String> tableData = new ArrayList<>();
  private final JTable table = makeTable();
  private final JPanel topPanel = new JPanel(new BorderLayout());
  private static final TableCopy tableCopy = new TableCopy();

  private String rawClipboard = "";

  private JFrame frame;

  public static void main(String[] args) {

    loadClipboard();
    tableCopy.showInFrame();
  }
  
  private static String filterText(String text) {
    text = text.replace(LINE_BREAK, "\\n");
    text = text.replace(TAB, "\\t");
    return text;
  }
  
  private static JTable makeTable() {
    JTable table = new FiringTable();
    shadeTableHeader(table);
    return table;
  }

  /**
   * Tints the table header a light shade of gray, to distinguish its column headers from the table data to
   * be copied.
   * @param table The table
   */
  private static void shadeTableHeader(final JTable table) {
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
      @Override
      public DefaultTableCellRenderer getTableCellRendererComponent(
          final JTable table,
          final Object value,
          final boolean isSelected,
          final boolean hasFocus,
          final int row,
          final int column
      ) {
        final var rendererBase = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) rendererBase;
        renderer.setBackground(UIManager.getColor("Panel.background"));
        Borders.addMatte(renderer, 0, 0, 1, 0, Color.darkGray);
        return renderer;
      }
    };
    table.getTableHeader().setDefaultRenderer(renderer);
  }

  // This needs to go after the main method sets the look and feel, if it does so.
  private final Color bgColor = UIManager.getColor("panel.background");

  private static void loadClipboard() {
    try {
      String rawDataObject = systemClipboard.getData(DataFlavor.stringFlavor).toString();
      System.out.printf("*** Comparing: %d chars to %d chars%nnew: %s%nold: %s%n",
          rawDataObject.length(),
          tableCopy.rawClipboard.length(),
          filterText(rawDataObject),
          filterText(tableCopy.rawClipboard)
      ); // NON-NLS

      TableMaker tableMaker = tableCopy.processRawClipboardData(rawDataObject);
      if (tableCopy.rawClipboard.isEmpty()) {
        tableCopy.rawClipboard = rawDataObject;
        tableCopy.displayData(tableMaker);
      } else if (!rawDataObject.equals(tableCopy.rawClipboard)) {
        if (tableCopy.confirmReload(tableCopy.frame, tableMaker)) {
          tableCopy.rawClipboard = rawDataObject;
          tableCopy.displayData(tableMaker);
        }
      }
    } catch (UnsupportedFlavorException e) {
      showError(e, "No Text Data Found");
    } catch (IOException e) {
      showError(e, "Data is Unavailable");
    } catch (IllegalStateException e) {
      showError(e, "Clipboard is Unavailable");
    }
  }

  private static void showError(Exception e, String message) {
    String fullMessage = String.format("<html><p>TableCopy was unable to read the clipboard data.</p><p>Reason: %s.<br><br></p><p>Error Message: %s</p>", message, e.getMessage());
    JOptionPane.showMessageDialog(null, fullMessage, "TableCopy Error", JOptionPane.ERROR_MESSAGE);
    throw new IllegalStateException(e.getMessage(), e);
  }
  
  private static boolean confirmReload(JFrame owner, TableMaker tableMaker) {
    JPanel messagePanel = new JPanel(new BorderLayout());
    Borders.addMatte(messagePanel, 20);
    final var messageLabel = new JLabel(String.format(
        "<html><p>Clipboard data has changed. Would you like to reload?</p><br><br><p>%s</p><br></html>",
        tableMaker.getTableDescription()
    ));

    messagePanel.add(messageLabel, BorderLayout.PAGE_START);
    CopyTableModel tableModel = tableMaker.create();
    final var messageTable = new JTable(tableModel);
    shadeTableHeader(messageTable);
    final var comp = Utils.prepareTable(messageTable);
    comp.setPreferredSize(new Dimension(300, 100));
    messagePanel.add(BorderLayout.CENTER, comp);
    int result = JOptionPane.showConfirmDialog(owner, messagePanel,
        "Reload", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    return result == JOptionPane.YES_OPTION;
  }

  TableCopy() {
    super(new BorderLayout());

    JScrollPane scrollPane = new JScrollPane(table);
    //noinspection MagicNumber
    scrollPane.setPreferredSize(new Dimension(650, 500));
    add(topPanel, BorderLayout.PAGE_START);
    add(scrollPane, BorderLayout.CENTER);
    add(makeCopyPanel(), BorderLayout.PAGE_END);
  }

  private TableMaker processRawClipboardData(String rawData) throws IOException, UnsupportedFlavorException {
    tableData.clear();
    TableMaker tableMaker;
    if (rawData.contains("\t")) {
      tableMaker = new FixedTableMaker(rawData);
//      tableCopy.processTabDelimitedTable(rawData);
    } else {
      final var data = systemClipboard.getData(DataFlavor.allHtmlFlavor);
//      System.out.printf("ClipboardData of %s%n -> %s%n", data.getClass().getSimpleName(), data); // NON-NLS
      String htmlData = data.toString();
      if (htmlData.contains(flexRowDelimiter)) {
        tableMaker = new FlexTableMaker(rawData, htmlData);
      } else {
        int columnsCountFromHeaders = countSubStrings(GRID_HEADER, htmlData);
        tableMaker = new AmbiguousTableMaker(rawData, columnsCountFromHeaders);
      }
    }
    return tableMaker;
  }
  
  private void displayData(TableMaker tableMaker) {
    CopyTableModel tableModel = tableMaker.create();
    table.setModel(tableModel);
    System.out.println("TableData Size = " + tableData.size());
    tableData.addAll(tableMaker.getTableCellData());
    topPanel.removeAll();
    if (!tableMaker.isColumnCountKnown()) {
      topPanel.add(makeChoicePanel(tableMaker.getColumnChoices(), tableMaker.getColumnCount()));
    }
    revalidate();
  }

////    printComponent(this, "");
//  }
//  private void printComponent(Component component, String indent) {
//    final String simpleName = component.getClass().getSimpleName();
//    System.out.printf("%s -* %s%n", indent, simpleName);
//    if (component instanceof Container c) {
//      for (Component child: c.getComponents()) {
//        printComponent(child, indent + String.format(" -> %s", simpleName));
//      }
//    }
//  }
//

  private JPanel makeCopyPanel() {
    JPanel flowPanel = new JPanel(new FlowLayout());
    JButton copyButton = new JButton("Copy and Close");
    flowPanel.add(copyButton);
    copyButton.addActionListener(e -> doCopyAndExit());
    return flowPanel;
  }

  private JPanel makeChoicePanel(Set<Integer> columnOptions, int columnCount) {
    JPanel choicePanel = new JPanel(new BorderLayout());

    final JSpinner spinner = new JSpinner();
    choicePanel.add(makeSpinnerPanel(spinner, columnCount), BorderLayout.PAGE_END);
    ActionListener numberButtonListener = e -> {
      JButton button = (JButton) e.getSource();
      int value = Integer.parseInt(button.getText());
      spinner.setValue(value);
    };

    JPanel buttonPanel = new JPanel(new FlowLayout());
    for (Integer i: columnOptions) {
      @SuppressWarnings("CallToNumericToString")
      JButton button = new JButton(i.toString());
      button.addActionListener(numberButtonListener);
      buttonPanel.add(button);
    }
    choicePanel.add(buttonPanel, BorderLayout.CENTER);
    return choicePanel;
  }

  private JPanel makeSpinnerPanel(JSpinner spinner, int startingValue) {
    int max = Math.max(2, tableData.size());
    final SpinnerNumberModel model = new SpinnerNumberModel(startingValue, 1, max, 1);
    spinner.setModel(model);
    spinner.addChangeListener(e -> ((CopyTableModel) table.getModel()).setColumnCount(model.getNumber().intValue()));
    JPanel longSpinner = Utils.stretchHorizontal(spinner, 100);
    JPanel spinnerPanel = new JPanel(new FlowLayout());
    spinnerPanel.add(Utils.wrapWithLabel(longSpinner, "Column Count: ", null));

    @SuppressWarnings("MagicNumber")
    int matteSize = 40;
    Border matteBorder = BorderFactory.createMatteBorder(matteSize, matteSize, matteSize, matteSize, bgColor);
    spinnerPanel.setBorder(matteBorder);
    return spinnerPanel;
  }

  private void showInFrame() {
    frame = new JFrame("Table Copy");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.getContentPane().add(this, BorderLayout.CENTER);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.getContentPane().add(BorderLayout.PAGE_END, makeFrameFooter());
    frame.setVisible(true);
    WindowAdapter cl = new WindowAdapter() {
      @Override
      public void windowActivated(WindowEvent e) {
        frame.removeWindowListener(this);
        addPermanentWindowListener(frame);
      }
    };
    frame.addWindowListener(cl);
  }
  
  private static JPanel makeFrameFooter() {
    String javaVersion = System.getProperty("java.version");
    JLabel version = new JLabel(String.format("  Java Version: %s", javaVersion));
    JLabel author = new JLabel("Written by Miguel Muñoz ");
    return Utils.loadBorderPanel()
        .lineStart(version)
        .lineEnd(author);
  }

  private void addPermanentWindowListener(final JFrame frame) {
    WindowListener windowListener = new WindowAdapter() {
      private boolean lastDeactivationWasExternal = false;
      @Override
      public void windowActivated(WindowEvent e) {
        System.out.printf("Activated, event = %s%n", e);
        if ((e.getOppositeWindow() == null) && lastDeactivationWasExternal) {
          System.out.printf("activated!%n"); // NON-NLS
          loadClipboard();
        }
      }

      @Override
      public void windowDeactivated(WindowEvent e) {
        // Due to a bug in the JDK, a WindowEvent instance for the windowActivated event shows the opposite window
        // as null, regardless of whether it was deactivated by opening a dialog box, or to switch to another
        // application. However, the Deactivated event doesn't have this problem. In this event, the opposite window
        // is the dialog or null. It's null when the user switched to another application.
        lastDeactivationWasExternal = e.getOppositeWindow() == null;
        System.out.println("Deactivated: " + e);
      }
    };
    frame.addWindowListener(windowListener);
  }

  private void doCopyAndExit() {
    String finalData = readyForClipboard(tableData, table.getColumnCount());
    StringSelection selection = new StringSelection(finalData.trim());
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(selection, selection);
    frame.dispose();
  }
  
  String readyForClipboard(List<String> elementList, int columns) {
    StringBuilder builder = new StringBuilder();
    Iterator<String> iterator = elementList.iterator();
    while (iterator.hasNext()) {
      for (int i = 1; i < columns; i++) {
        if (iterator.hasNext()) {
          builder
              .append(iterator.next())
              .append(TAB);
        }
      }
      if (iterator.hasNext()) {
        builder
            .append(iterator.next())
            .append(LINE_BREAK);
      }
    }
    return builder.toString();
  }

  private static int countSubStrings(String searchFor, String line) {
    int count = 0;
    int index = line.lastIndexOf(searchFor);
    while (index >= 0) {
      count++;
      index = line.lastIndexOf(searchFor, index - 1);
    }
    return count;
  }

  static class CopyTableModel extends AbstractFiringTableModel {
    
    private final ArrayList<String> elements;
    private int columnCount;
    private int rowCount;

    CopyTableModel(int columnCount, List<String> elements) {
      super();
      this.elements = new ArrayList<>(elements);
      setColumnCount(columnCount); // Sets rowCount too.
    }
    
    @Override
    public int getRowCount() {
      return rowCount;
    }

    @Override
    public int getColumnCount() {
      return columnCount;
    }
    
    @Override
    public Object getValueAt(int row, int column) {
      final int index = (row * columnCount) + column;
      if (index < elements.size()) {
        return elements.get(index);
      }
      // This will happen if the selected number of rows is not a divisor of the number of elements.
      return "";
    }
    
    void setColumnCount(int columnCount) {
      this.columnCount = columnCount;
      int rows = elements.size() / columnCount;
      rowCount = rows + (((elements.size() % columnCount) == 0) ? 0 : 1);
      fireTableStructureChanged();
    }
  }
  
  
  private abstract static class TableMaker {
    private final List<String> tableCellData = new ArrayList<>();
    private int columnCount;

    CopyTableModel create() {
      return new CopyTableModel(columnCount, tableCellData);
    }

    int getCellCount() {
      return tableCellData.size();
    }

    abstract String getTableDescription();
    abstract boolean isColumnCountKnown();

    protected int getColumnCount() {
      return columnCount;
    }

    protected void setColumnCount(int columnCount) {
      this.columnCount = columnCount;
    }

    /**
     * This only needs to return a value if isColumnCountKnown returns false;
     * @return The most likely options for the number of columns in the table, which is a list containing all
     * the divisors of the number of cells.
     */
    Set<Integer> getColumnChoices() { throw new UnsupportedOperationException("Should not happen"); }

    protected List<String> getTableCellData() {
      //noinspection AssignmentOrReturnOfFieldWithMutableType
      return tableCellData;
    }
  }
  
  private static class FlexTableMaker extends TableMaker {
    private final int rowCount;

    FlexTableMaker(String rawData, String htmlData) {
      super();
      String[] cells = rawData.split(LINE_BREAK);
      final var tableCellData = getTableCellData();
      tableCellData.addAll(List.of(cells));
      int rowIndex = htmlData.indexOf(flexRowDelimiter);
      int cellIndex = htmlData.indexOf(flexCellDelimiter);
      int rowCellCount = 0;
      var rowCellCountList = new LinkedList<Integer>();

      // Skip the first rowDelimiter.
      if (rowIndex < cellIndex) {
        rowIndex = htmlData.indexOf(flexRowDelimiter, rowIndex+1);
      }
      while (cellIndex >= 0) {
        if (rowIndex < cellIndex) {
          rowCellCountList.add(rowCellCount);
          rowCellCount = 0;
          rowIndex = htmlData.indexOf(flexRowDelimiter, rowIndex + 1);
          if (rowIndex < 0) {
            rowIndex = Integer.MAX_VALUE; // Imaginary final rowDelimiter to keep going until we run out of cells.
          }
        } else {
          rowCellCount++;
          cellIndex = htmlData.indexOf(flexCellDelimiter, cellIndex + 1);
        }
      }

      // Add the last row found.
      rowCellCountList.add(rowCellCount);
      // This probably won't happen.
      while (rowCellCountList.get(0) == 0) {
        rowCellCountList.remove(0);
      }
      int columnCount = rowCellCountList.stream().max(Integer::compareTo).get(); // throws exception if empty
      setColumnCount(columnCount);
      int rows = tableCellData.size()/ columnCount;

      // Pack empty cells if the first row is shorter than the others.
      int firstCellCount = rowCellCountList.getFirst();
      for (int i = firstCellCount; i< columnCount; ++i) {
        tableCellData.add(0, "");
      }
      int lastCellCount = rowCellCountList.getLast();
      for (int i = lastCellCount; i< columnCount; ++i) {
        tableCellData.add("");
      }
      rowCount = rows + (((tableCellData.size() % columnCount) == 0) ? 0 : 1);
    }

    @Override
    public String getTableDescription() {
      return "Flex Table with %d cells in %d rows and %d columns".formatted(getCellCount(), rowCount, getColumnCount());
    }

    @Override
    public boolean isColumnCountKnown() {
      return true;
    }
  }

  private static class AmbiguousTableMaker extends TableMaker {
    private final Set<Integer> columnOptions = new TreeSet<>();

    /**
     * <p>Make an AmbiguousTable, which is a table with an unknown number of columns, from the raw data and possibly
     * the number of columns. This number will be available if the user included the column headers in the selection,
     * and will be accurate if the user included all of them. Since we have no way of knowing, the user will still
     * be able to adjust the number of columns.</p>
     * @param rawData The raw data.
     * @param columnCount The number of columns if known, zero otherwise.
     */
    AmbiguousTableMaker(String rawData, int columnCount) {
      super();
      StringTokenizer lineTokenizer = new StringTokenizer(rawData, "\n");
      int lineCount = lineTokenizer.countTokens();
      List<String> elements = new LinkedList<>();
      while (lineTokenizer.hasMoreTokens()) {
        elements.add(lineTokenizer.nextToken());
      }
      final var tableCellData = getTableCellData();
      tableCellData.addAll(elements);
      int limit = tableCellData.size() / 2;
      for (int i = 2; i <= limit; ++i) {
        if ((lineCount % i) == 0) {
          columnOptions.add(i);
        }
      }
      if (columnOptions.isEmpty()) {
        columnOptions.addAll(List.of(2, 3, 4, 5));
      }
      if (columnCount > 0) {
        columnOptions.add(columnCount);
      }
      columnOptions.add(tableCellData.size());
      setColumnCount((columnCount == 0) ? 2 : columnCount);
    }
    
    @Override
    public CopyTableModel create() {
      return new CopyTableModel(getColumnCount(), getTableCellData());
    }

    @Override
    public String getTableDescription() {
      return String.format("Table with %d cells and an unknown number of columns", getTableCellData().size());
    }

    @Override
    public boolean isColumnCountKnown() {
      return false;
    }

    @Override
    Set<Integer> getColumnChoices() {
      return new TreeSet<>(columnOptions);
    }
  }

  /**
   * This makes a table that originated with the html table tag.
   */
  private static class FixedTableMaker extends TableMaker {
    final int rowCount;

    FixedTableMaker(String rawData) {
      super();
      String[] lines = rawData.split(LINE_BREAK);
      List<String[]> tableRows = new LinkedList<>();
      int columnTally = 0;
      int firstRowCount = countSubStrings(TAB, lines[0]) + 1;
      int lastRowCount = 0;
      for (String line : lines) {
        String[] cells = line.split(TAB);
        columnTally = Integer.max(columnTally, cells.length);
        tableRows.add(cells);
        lastRowCount = cells.length;
      }

      final var tableCellData = getTableCellData();

      // If the first row is missing cells, pack those empty cells with blank strings.
      for (int i=0; i< (columnTally - firstRowCount); i++) {
        tableCellData.add("");
      }
      rowCount = tableRows.size();
      setColumnCount(columnTally);
      for (String[] cells : tableRows) {
        Collections.addAll(tableCellData, cells);
      }
      if (tableRows.size() > 1) {
        for (int i = lastRowCount; i < columnTally; i++) {
          tableCellData.add("");
        }
      }
    }

    @Override
    public boolean isColumnCountKnown() {
      return true;
    }

    @Override
    public String getTableDescription() {
      return String.format("Table with %d cells in %d rows and %d columns", getCellCount(), rowCount, getColumnCount());
    }
  }
  
//  record tableStructure(String[] elements, String encoded, TableType tableType, int rows, int columns) {}
  
//  private enum TableType {
//    HTML,
//    FLEX,
//    GRID
//  }
}

/*
<meta charset='utf-8'><div class="

flex-row
 flex-header" style="display: flex; background-color: rgb(33, 150, 243); color: white; font-weight: bold; font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">Product</div><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">Price</div><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right-width: medium; border-right-style: none; border-right-color: currentcolor; border-bottom: 1px solid rgb(221, 221, 221);">Stock</div></div><div class="

flex-row
" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">Laptop</div><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">$999</div><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right-width: medium; border-right-style: none; border-right-color: currentcolor; border-bottom: 1px solid rgb(221, 221, 221);">15</div></div><div class="

flex-row
" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">Smartphone</div><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">$699</div><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right-width: medium; border-right-style: none; border-right-color: currentcolor; border-bottom: 1px solid rgb(221, 221, 221);">28</div></div><div class="

flex-row
" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">Tablet</div><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">$399</div><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right-width: medium; border-right-style: none; border-right-color: currentcolor; border-bottom: 1px solid rgb(221, 221, 221);">12</div></div><div class="

flex-row
" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom-width: medium; border-bottom-style: none; border-bottom-color: currentcolor;">Chromebook</div><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom-width: medium; border-bottom-style: none; border-bottom-color: currentcolor;">$299</div><div class="
---- flex-cell" style="flex: 1 1 0%; padding: 15px; border-right-width: medium; border-right-style: none; border-right-color: currentcolor; border-bottom-width: medium; border-bottom-style: none; border-bottom-color: currentcolor;">19</div></div>
 */

package com.neptunedreams;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
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
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
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
 * <p><b>For CSS Grid Tables,</b> find an element like this: "grid-template-columns: 2fr 1fr 1fr;" and count the
 * number of words between grid-template-columns and the semicolon.<br>
 * <b>Note: </b>This doesn't work, because the grid-template-columns attribute appears before any of the table
 * contents. This means it doesn't get copied to the clipboard. The data on the clipboard doesn't have any
 * clues showing where the lines break.</p>
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 4/20/26
 * <br>Time: 3:15 AM
 * <br>@author Miguel Muñoz (<a href="https://github.com/SwingGuy1024">https://github.com/SwingGuy1024</a>)</p>
 */
@SuppressWarnings("MagicNumber")
public class TableCopy extends JPanel {

  public static final String TAB = "\t";
  public static final String LINE_BREAK = "\n";

  private final List<String> tableData = new ArrayList<>();
  private final JTable table = makeTable();
  private final JPanel topPanel = new JPanel(new BorderLayout());
  private static final TableCopy tableCopy = new TableCopy();

  private String rawClipboard = "";
//  private String lastClipboardData = "";

  private JDialog dialog;
  private JFrame frame;

  public static void main(String[] args) {

    loadClipboard();
    tableCopy.showInFrame();
  }
  
  private static String filterText(String text) {
    text = text.replace("\n", "\\n");
    text = text.replace("\t", "\\t");
    return text;
  }
  
  private static JTable makeTable() {
    JTable table = new FiringTable();
    table.setOpaque(false);
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
    return table;
  }

  // This needs to go after the main method sets the look and feel, if it does so.
  private final Color bgColor = UIManager.getColor("panel.background");

  private static void loadClipboard() {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    
    try {
      String rawDataObject = clipboard.getData(DataFlavor.stringFlavor).toString();
      System.out.printf("*** Comparing: %d chars to %d chars%nnew: %s%nold: %s%n",
          rawDataObject.length(),
          tableCopy.rawClipboard.length(),
          filterText(rawDataObject),
          filterText(tableCopy.rawClipboard)
      ); // NON-NLS

      if (tableCopy.rawClipboard.isEmpty()) {
        tableCopy.processRawClipboardData(rawDataObject);
      } else if (!rawDataObject.equals(tableCopy.rawClipboard)) {
        if (tableCopy.confirmReload(tableCopy.frame)) {
          tableCopy.processRawClipboardData(rawDataObject);
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
    String fullMessage = String.format("%s%n%nError Message: %s", message, e.getMessage());
    JOptionPane.showMessageDialog(null, fullMessage, "Error", JOptionPane.ERROR_MESSAGE);
  }
  
  private static boolean confirmReload(JFrame owner) {
    int result = JOptionPane.showConfirmDialog(owner, "Clipboard contents have changed. Reload Table Data?",
        "Reload", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    return result == JOptionPane.YES_OPTION;
  }

  /**
   * <p>I'm not currently using this, but I may change the UI to show the user the text on the clipboard when I ask
   * if they want to replace the data. If so, this dialog may come in handy, although it may be possible to do this
   * with a JOptionPane, too.</p>
   * @param owner The owning component
   * @return True if the user chooses to replace the table data, false otherwise.
   */
  private boolean confirmReloadDlg(JFrame owner) {
    if (dialog == null) {
      // reusable dialog
      dialog = new JDialog(owner, "Reload?", true);
      dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
      dialog.setResizable(false);
    }
    JLabel message = new JLabel("Clipboard contents have changed. Reload Table Data?");
    JPanel questionPanel = new JPanel(new BorderLayout());
    questionPanel.add(message, BorderLayout.NORTH);
    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton yesButton = new JButton("Yes");
    JButton noButton = new JButton("No");
    buttonPanel.add(yesButton);
    buttonPanel.add(noButton);
    questionPanel.add(buttonPanel, BorderLayout.SOUTH);
    AtomicBoolean confirmed = new AtomicBoolean(false);
    ActionListener buttonListener = e -> {
        //noinspection ObjectEquality
      confirmed.set(e.getSource() == yesButton);
      dialog.setVisible(false);
    };
    yesButton.addActionListener(buttonListener);
    noButton.addActionListener(buttonListener);
    int matteSize = 40;
    Borders.addMatte(questionPanel, matteSize);
    
    dialog.setContentPane(questionPanel);
    dialog.pack();
    dialog.setLocationRelativeTo(frame);
    dialog.setVisible(true);
    return confirmed.get();
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

  private void processRawClipboardData(String rawData) throws IOException {
    this.rawClipboard = rawData;
    StringTokenizer tabTokenizer = new StringTokenizer(rawData, "\t");
    int tokenCount = tabTokenizer.countTokens();
    tableData.clear();
    if(tokenCount == 1) {
      tableCopy.processUnformattedTable(rawData);
    } else {
      tableCopy.processReadyTable(rawData);
    }
  }

  private void processReadyTable(String rawData) {
    String[] lines = rawData.split(LINE_BREAK);
    int columnCount = 0;
    for (String line: lines) {
      String[] cells = line.split(TAB);
      columnCount = Integer.max(columnCount, cells.length);
    }
    for (String line: lines) {
      String[] cells = line.split(TAB);
      Collections.addAll(tableData, cells);
      for (int i = cells.length; i < columnCount; i++) {
        tableData.add("");
      }
    }
    createTable(tableData, columnCount);
  }

  private void processUnformattedTable(String rawData) {
    StringTokenizer lineTokenizer = new StringTokenizer(rawData, "\n");
    int lineCount = lineTokenizer.countTokens();
    while(lineTokenizer.hasMoreTokens()) {
      tableData.add(lineTokenizer.nextToken());
    }
    Set<Integer> columnOptions = new TreeSet<>();
    int limit = tableData.size()/2;
    for (int i=2; i<=limit; ++i) {
      if ((lineCount % i) == 0) {
        columnOptions.add(i);
      }
    }

    if (columnOptions.isEmpty()) {
      createTable(tableData, 2, columnOptions);
    } else {
      int bestGuessColumnCount = columnOptions.iterator().next();
      createTable(tableData, bestGuessColumnCount, columnOptions);
    }
    System.out.printf("Elements count: %s%n", getComponentCount()); // NON-NLS
//    printComponent(this, "");
  }
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

  private void createTable(List<String> tableData, int bestGuessColumnCount, Set<Integer> columnOptions) {
    CopyTableModel tableModel = new CopyTableModel(bestGuessColumnCount, tableData);
    table.setModel(tableModel);
    topPanel.removeAll();
    topPanel.add(makeChoicePanel(bestGuessColumnCount, columnOptions), BorderLayout.CENTER);
    revalidate();
  }

  private void createTable(List<String> tableData, int columnCount) {
    CopyTableModel tableModel = new CopyTableModel(columnCount, tableData);
    table.setModel(tableModel);
    
    topPanel.removeAll();
    revalidate();
  }

  private JPanel makeCopyPanel() {
    JPanel panel = new JPanel(new FlowLayout());
    JButton copyButton = new JButton("Copy and Close");
    panel.add(copyButton);
    copyButton.addActionListener(e -> doCopyAndExit());
    return panel;
  }

  private JPanel makeChoicePanel(int bestGuess, Set<Integer> columnOptions) {
    JPanel choicePanel = new JPanel(new BorderLayout());

    final JSpinner spinner = new JSpinner();
    choicePanel.add(makeSpinnerPanel(spinner, bestGuess), BorderLayout.PAGE_END);
    ActionListener buttonListener = e -> {
      JButton button = (JButton) e.getSource();
      int value = Integer.parseInt(button.getText());
      spinner.setValue(value);
    };

    JPanel buttonPanel = new JPanel(new FlowLayout());
    for (Integer i: columnOptions) {
      @SuppressWarnings("CallToNumericToString")
      JButton button = new JButton(i.toString());
      button.addActionListener(buttonListener);
      buttonPanel.add(button);
    }
    choicePanel.add(buttonPanel, BorderLayout.CENTER);
    return choicePanel;
  }

  private JPanel makeSpinnerPanel(JSpinner spinner, int bestGuess) {
    int max = Math.max(bestGuess, tableData.size()/2);
    final SpinnerNumberModel model = new SpinnerNumberModel(bestGuess, 0, max, 1);
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
    String javaVersion = System.getProperty("java.version");
    JLabel label = new JLabel(String.format("  Java Version: %s", javaVersion));
    frame = new JFrame("Table Copy");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.getContentPane().add(this, BorderLayout.CENTER);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.getContentPane().add(BorderLayout.PAGE_END, label);
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
  
  record tableStructure(String[] elements, String encoded, TableType tableType, int rows, int columns) {}
  
  private enum TableType {
    HTML,
    FLEX,
    GRID
  }
}

/*
<meta charset='utf-8'><div class="flex-row flex-header" style="display: flex; background-color: rgb(33, 150, 243); color: white; font-weight: bold; font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">Product</div><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">Price</div><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right-width: medium; border-right-style: none; border-right-color: currentcolor; border-bottom: 1px solid rgb(221, 221, 221);">Stock</div></div><div class="flex-row" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">Laptop</div><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">$999</div><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right-width: medium; border-right-style: none; border-right-color: currentcolor; border-bottom: 1px solid rgb(221, 221, 221);">15</div></div><div class="flex-row" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">Smartphone</div><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">$699</div><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right-width: medium; border-right-style: none; border-right-color: currentcolor; border-bottom: 1px solid rgb(221, 221, 221);">28</div></div><div class="flex-row" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">Tablet</div><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom: 1px solid rgb(221, 221, 221);">$399</div><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right-width: medium; border-right-style: none; border-right-color: currentcolor; border-bottom: 1px solid rgb(221, 221, 221);">12</div></div><div class="flex-row" style="display: flex; color: rgb(0, 0, 0); font-family: Arial, sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; white-space: normal; text-decoration-thickness: initial; text-decoration-style: initial; text-decoration-color: initial;"><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom-width: medium; border-bottom-style: none; border-bottom-color: currentcolor;">Chromebook</div><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right: 1px solid rgb(221, 221, 221); border-bottom-width: medium; border-bottom-style: none; border-bottom-color: currentcolor;">$299</div><div class="flex-cell" style="flex: 1 1 0%; padding: 15px; border-right-width: medium; border-right-style: none; border-right-color: currentcolor; border-bottom-width: medium; border-bottom-style: none; border-bottom-color: currentcolor;">19</div></div>
 */

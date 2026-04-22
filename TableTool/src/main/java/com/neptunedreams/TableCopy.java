package com.neptunedreams;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
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
import javax.swing.table.AbstractTableModel;

import com.mm.gui.Utils;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 4/20/26
 * <br>Time: 3:15 AM
 * <br>@author Miguel Muñoz (<a href="https://github.com/SwingGuy1024">https://github.com/SwingGuy1024</a>)</p>
 */
@SuppressWarnings("CallToNumericToString")
public class TableCopy extends JPanel {

  public static final String TAB = "\t";
  public static final String LINE_BREAK = "\n";

  private final List<String> tableData = new ArrayList<>();
  private final JTable table = new  JTable();

  public static void main(String[] args) {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    try {
      Object rawDataObject = clipboard.getData(DataFlavor.stringFlavor);
      processRawClipboardData(rawDataObject.toString());
    } catch (UnsupportedFlavorException e) {
      showError(e, "No Text Data Found");
    } catch (IOException e) {
      showError(e, "Data is Unavailable");
    } catch (IllegalStateException e) {
      showError(e, "Clipboard is Unavailable");
    }
  }

  // This needs to go after the main method sets the look and feel, if it does so.
  private final Color bgColor = UIManager.getColor("panel.background");

  private static void processRawClipboardData(String rawData) throws IOException {
    StringTokenizer tabTokenizer = new StringTokenizer(rawData, "\t");
    int tokenCount = tabTokenizer.countTokens();
    TableCopy tableCopy = new TableCopy();
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
    
    if (!columnOptions.isEmpty()) {
      int bestGuessColumnCount = columnOptions.iterator().next();
      createTable(tableData, bestGuessColumnCount, columnOptions);
      return;
    }
    createTable(tableData, 5, columnOptions); 
  }
  
  private void createTable(List<String> tableData, int bestGuessColumnCount, Set<Integer> columnOptions) {
    CopyTableModel tableModel = new CopyTableModel(bestGuessColumnCount, tableData);
    table.setModel(tableModel);
    JScrollPane scrollPane = new JScrollPane(table);
    
    Box box = Box.createVerticalBox();
    
    box.add(makeChoicePanel(bestGuessColumnCount, columnOptions));
    box.add(scrollPane);
    box.add(makeCopyPanel());
    add(box, BorderLayout.CENTER);
    
    showInFrame();
  }
  
  private void createTable(List<String> tableData, int columnCount) {
    CopyTableModel tableModel = new CopyTableModel(columnCount, tableData);
    table.setModel(tableModel);
    JScrollPane scrollPane = new JScrollPane(table);
    
    Box box = Box.createVerticalBox();
    box.add(scrollPane);
    box.add(makeCopyPanel());
    add(box, BorderLayout.CENTER);

    showInFrame();
  }
  
  private JPanel makeCopyPanel() {
    JPanel panel = new JPanel(new FlowLayout());
    JButton copyButton = new JButton("Copy");
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
      JButton button = new JButton(i.toString());
      button.addActionListener(buttonListener);
      buttonPanel.add(button);
    }
    choicePanel.add(buttonPanel, BorderLayout.CENTER);
    return choicePanel;
  }
  
  private JPanel makeSpinnerPanel(JSpinner spinner, int bestGuess) {
    final SpinnerNumberModel model = new SpinnerNumberModel(bestGuess, 0, tableData.size() / 2, 1);
    spinner.setModel(model);
    spinner.addChangeListener(e -> ((CopyTableModel) table.getModel()).setColumnCount(model.getNumber().intValue()));
    JPanel longSpinner = Utils.stretchHorizontal(spinner, 100);
    JPanel spinnerPanel = new JPanel(new FlowLayout());
    spinnerPanel.add(Utils.wrapWithLabel(longSpinner, "Column Count: ", null));

    @SuppressWarnings("MagicNumber")
    int matteSize = 20;
    Border matteBorder = BorderFactory.createMatteBorder(matteSize, matteSize, matteSize, matteSize, bgColor);
    spinnerPanel.setBorder(matteBorder);
    return spinnerPanel;
  }
  
  private void showInFrame() {
    String javaVersion = System.getProperty("java.version");
    JLabel label = new JLabel(String.format("Java Version: %s", javaVersion));
    add(BorderLayout.PAGE_END, label);
    JFrame frame = new JFrame("Table Copy");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.add(this, BorderLayout.CENTER);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
  
  private void doCopyAndExit() {
    String finalData = readyForClipboard(tableData, table.getColumnCount());
    StringSelection selection = new StringSelection(finalData.trim());
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(selection, selection);
    
    Window frame = (Window) getRootPane().getParent();
    frame.dispose();
  }
  
  private static void showError(Exception e, String message) {
    String fullMessage = String.format("%s%n%nError Message: %s", message, e.getMessage());
    JOptionPane.showMessageDialog(null, fullMessage, "Error", JOptionPane.ERROR_MESSAGE);
    System.exit(1);
  }

  TableCopy() {
    super(new BorderLayout());
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
  
  static class CopyTableModel extends AbstractTableModel {
    
    private final ArrayList<String> elements;
    private int columnCount;
    private int rowCount;

    CopyTableModel(int columnCount, List<String> elements) {
      super();
      this.elements = new ArrayList<>(elements);
      setColumnCount(columnCount);
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
      // This can happen if the selected number of rows is not a divisor of the number of elements.
      return "";
    }
    
    void setColumnCount(int columnCount) {
      this.columnCount = columnCount;
      int rows = elements.size() / columnCount;
      rowCount = rows + (((elements.size() % columnCount) == 0) ? 0 : 1);
      fireTableStructureChanged();
    }
  }
}

package com.mm.gui.table;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.mm.gui.table.HighlightRenderer.HighlightMode;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 3/24/24</p>
 * <p>Time: 9:52&nbsp;PM</p>
 * <p>@author Miguel Muñoz</p>
 */
@SuppressWarnings("MagicNumber")
public final class TableHighlightingRendererDemo extends JPanel {
  public static void main(String[] args) {
//    Properties sysProps = System.getProperties();
//    for (Map.Entry<Object, Object> entry : sysProps.entrySet()) {
//      System.out.printf("%30s: %s%n", entry.getKey(), entry.getValue()); // NON-NLS
//    }
    System.out.println(System.getProperty("java.version"));
    JFrame frame = new JFrame("Table RendererRFE");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setLocationByPlatform(true);
    frame.add(new TableHighlightingRendererDemo());
    frame.pack();
    frame.setVisible(true);
  }
  
  private final String[] targetStrings = {"mispelled", "shoud"};

  private TableHighlightingRendererDemo() {
    super(new BorderLayout());
//    final HilightRenderer hilightRenderer = new HilightRenderer(Color.red, Color.white);
//    hilightRenderer.setAllowHTML(true);
//    hilightRenderer.setHilightText(bad);
    final HighlightRenderer highlightRendererStar = new HighlightRenderer();
    highlightRendererStar.setIcon(StarIcon.makeStarIcon());
    final HighlightRenderer underliningRenderer = new HighlightRenderer(this::spellCheck,
        "text-decoration: underline; text-decoration-color: red;");
    TableCellRenderer[] renderers = {
        new DefaultTableCellRenderer(),
        new HighlightRenderer(this::spellCheck, HighlightMode.RedText),
        highlightRendererStar,
        underliningRenderer,
        new HighlightRenderer(this::spellCheck, HighlightMode.RedUnderlinedText),
        new HighlightRenderer(this::spellCheck, HighlightMode.UnderlinedText),
        new HighlightRenderer(this::spellCheck, HighlightMode.PinkHighlight),
        new HighlightRenderer(this::spellCheck, HighlightMode.BlueHighlight),
        new HighlightRenderer(this::spellCheck, HighlightMode.YellowHighlight),
    };
    
    highlightRendererStar.setHighlightRangeSource(this::spellCheck, "color:red;");
    AbstractTableModel tableModel = new AbstractTableModel() {
      @Override
      public int getRowCount() {
        return renderers.length;
      }

      @Override
      public int getColumnCount() {
        return 3;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex) {
          case 0: return renderers[rowIndex].getClass().getSimpleName();
          case 1: return "The mispelled word shoud appear here.";
          case 2: return "Clipping still works when nothing is misspelled.";
          default:
            return "";
        }
      }
    };

    JTable table = new JTable(tableModel) {
      @Override
      public TableCellRenderer getCellRenderer(int row, int column) {
        return renderers[row];
      }
    };
    
    table.setCellSelectionEnabled(true);
    final TableColumnModel columnModel = table.getTableHeader().getColumnModel();
    final TableColumn column = columnModel.getColumn(0);
    column.setMaxWidth(250);
    column.setPreferredWidth(175);

    JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    add(scrollPane, BorderLayout.CENTER);
  } // The mispelled word shoud appear here.

  @NotNull
  private List<HighlightRenderer.TextRange> spellCheck(String text) {
    List<HighlightRenderer.TextRange> rangeList = new LinkedList<>();
    for (String s: targetStrings) {
      int tSpot = text.indexOf(s);
      if (tSpot >= 0) {
        rangeList.add(HighlightRenderer.TextRangeImpl.of(tSpot, s.length()));
      }
    }
    return rangeList;
  }
}

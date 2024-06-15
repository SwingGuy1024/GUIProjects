package com.mm.gui.table;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;

import com.mm.gui.table.HighlightRenderer.HighlightMode;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 3/24/24</p>
 * <p>Time: 9:52&nbsp;PM</p>
 * <p>@author Miguel Muñoz</p>
 */
@SuppressWarnings({"MagicNumber", "MagicCharacter"})
public final class TableHighlightingRendererDemo extends JPanel {

  private final JTable table;

  public static void main(String[] args) {
//    Properties sysProps = System.getProperties();
//    for (Map.Entry<Object, Object> entry : sysProps.entrySet()) {
//      System.out.printf("%30s: %s%n", entry.getKey(), entry.getValue()); // NON-NLS
//    }
    System.out.println(System.getProperty("java.version"));
    JFrame frame = new JFrame("Table Highlight Renderer Demo");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setLocationByPlatform(true);
    frame.add(new TableHighlightingRendererDemo());
    frame.pack();
    frame.setVisible(true);
  }
  
  private List<String> targetStrings = Arrays.asList("mispelled", "shoud", "works", "iis");

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
        new HighlightRenderer(this::spellCheck, HighlightMode.BlackHighlight),
        new HighlightRenderer(this::spellCheck, HighlightMode.LavenderHighlight),
        new HighlightRenderer(this::spellCheck, HighlightMode.DarkBlueHighlight),
        new HighlightRenderer(this::spellCheck, HighlightMode.RedBgYellowTextHighlight),
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
          case 2: return "Clipping still works when nothing iis misspelled.";
          default:
            return "";
        }
      }
    };

    table = new JTable(tableModel) {
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
    
    add(makeSpellErrorList(), BorderLayout.LINE_END);
  } // The mispelled word shoud appear here.

  private JPanel makeSpellErrorList() {
    JLabel label = new JLabel("Misspellings");
    StringBuilder initialTextBldr = new StringBuilder(targetStrings.get(0));
    for (int i=1; i<targetStrings.size(); ++i) {
      initialTextBldr
          .append('\n')
          .append(targetStrings.get(i));
    }
    JTextArea wordList = new JTextArea(initialTextBldr.toString(), 20, 15);
    wordList.setFont(Font.getFont(Font.MONOSPACED));
    JPanel spellErrorList = new JPanel(new BorderLayout());
    spellErrorList.add(label, BorderLayout.PAGE_START);
    DocumentListener processSpellChanges = getDocListener();
    wordList.getDocument().addDocumentListener(processSpellChanges);
    JScrollPane scrollPane = new JScrollPane(wordList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    spellErrorList.add(scrollPane, BorderLayout.CENTER);
    return spellErrorList;
  }
  
  private DocumentListener getDocListener() {
    return new DocumentListener() {
      @Override public void insertUpdate(DocumentEvent e) { process(e); }
      @Override public void removeUpdate(DocumentEvent e) { process(e); }
      @Override public void changedUpdate(DocumentEvent e) { process(e); }
      
      private void process(DocumentEvent e) {
        int length = e.getDocument().getLength();
        try {
          String text = e.getDocument().getText(0, length);
          String[] lines = text.split("\\n");
          Arrays.stream(lines)
              .forEach(System.out::println);
          System.out.println('-');
          targetStrings = Arrays.asList(lines);
          table.repaint();
        } catch (BadLocationException ex) {
          throw new IllegalStateException("Should not happen", ex);
        }
      }
    }; 
  }

  @NotNull
  private Set<HighlightRenderer.TextRange> spellCheck(String text) {
    SortedSet<HighlightRenderer.TextRange> rangeList = new HighlightRenderer.RangeSet();
    for (String s: targetStrings) {
      if (!s.isEmpty()) {
        int startSearch = 0;
        int tSpot = 0;
        while (tSpot >= 0) {
          tSpot = text.indexOf(s, startSearch);
          if (tSpot >= 0) {
            rangeList.add(HighlightRenderer.TextRangeImpl.of(tSpot, s.length()));
          }
          startSearch = tSpot+1;
        }
      }
    }
    return rangeList;
  }
}

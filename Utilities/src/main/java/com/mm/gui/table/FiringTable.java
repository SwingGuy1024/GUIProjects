package com.mm.gui.table;

import java.util.Vector;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jetbrains.annotations.NotNull;

/**
 * <p>JTable meant to ensure its TableModel implements FiringTableModel. This will ensure subclasses will have access
 * to the fireXxx methods found in AbstractTableModel and DefaultTableModel without needing to cast the model to
 * a concrete class.</p>
 * <p>Ideally, if a developer tried to use the wrong table model, it should be a compile time error. But Java
 * doesn't let me override a method parameter with a subclass, so the setModel(TableModel) method has to resort
 * to throwing an exception at runtime.</p>
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 4/21/26
 * <br>Time: 11:51 PM
 * <br>@author Miguel Muñoz (<a href="https://github.com/SwingGuy1024">https://github.com/SwingGuy1024</a>)</p>
 */
@SuppressWarnings("unused")
public class FiringTable extends JTable {
  public FiringTable() {
    super();
  }

  public FiringTable(FiringTableModel dm) {
    super(dm);
  }

  public FiringTable(FiringTableModel dm, TableColumnModel cm) {
    super(dm, cm);
  }

  public FiringTable(FiringTableModel dm, TableColumnModel cm, ListSelectionModel sm) {
    super(dm, cm, sm);
  }

  public FiringTable(int numRows, int numColumns) {
    this(new DefaultFiringTableModel(numRows, numColumns));
  }

  @SuppressWarnings({"UseOfObsoleteCollectionType", "rawtypes"})
  public FiringTable(Vector<? extends Vector> rowData, Vector<?> columnNames) {
    this(new DefaultFiringTableModel(rowData, columnNames));
  }

  public FiringTable(@NotNull Object[][] rowData, @NotNull Object[] columnNames) {
    this(new AbstractFiringTableModel() {
      @Override public String getColumnName(int column) { return columnNames[column].toString(); }
      @Override public int getRowCount() { return rowData.length; }
      @Override public int getColumnCount() { return columnNames.length; }
      @Override public Object getValueAt(int row, int col) { return rowData[row][col]; }
      @Override public boolean isCellEditable(int row, int column) { return true; }
      @Override public void setValueAt(Object value, int row, int col) {
        rowData[row][col] = value;
        fireTableCellUpdated(row, col);
      }
    });
  }

  @Override
  public FiringTableModel getModel() {
    return (FiringTableModel) super.getModel();
  }

  @Override
  public void setModel(@NotNull TableModel dataModel) {
    if (!(dataModel instanceof FiringTableModel)) {
      throw new IllegalArgumentException(
          "TableModel Does not implement FiringTableModel: %s".format(dataModel.getClass().getName())
      );
    }
    super.setModel(dataModel);
  }
}

package com.mm.gui.table;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/**
 * <p>These methods are all implemented by AbstractTableModel and DefaultTableModel. This interface allows the
 * developer to take any subclass of AbstractTableModel, such as DefaultTableModel, and cast it to a FiringTableModel,
 * then to call the appropriate fireXxx() method to update the view.</p>
 * 
 * <p>These methods often need to be called by the view, but since they are not in the TableModel interface, code
 * in the view layer doesn't have access to them without first casting them to the appropriate concrete table
 * model, This, of course, is not a good practice. I created this interface to get around that problem. These
 * will work with any TableModel that extends either DefaultTableModel or AbstractTableModel.</p>
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 3/30/26
 * <br>Time: 2:16 AM
 * <br>@author Miguel Muñoz (<a href="https://github.com/SwingGuy1024">https://github.com/SwingGuy1024</a>)</p>
 */
public interface FiringTableModel extends TableModel {
  void fireTableDataChanged();
  void fireTableStructureChanged();
  void fireTableRowsInserted(int firstRow, int lastRow);
  void fireTableRowsUpdated(int firstRow, int lastRow);
  void fireTableRowsDeleted(int firstRow, int lastRow);
  void fireTableCellUpdated(int row, int column);

  /**
   * <p>In AbstractTableModel and DefaultTableModel, all the other fireXxx() methods call this method to do the
   * work. In general, subclasses shouldn't need to call this directly.</p>
   * @param e The TableModelEvent
   */
  void fireTableChanged(TableModelEvent e);
}

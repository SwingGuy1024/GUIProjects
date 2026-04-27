package com.mm.gui.table;

import javax.swing.table.AbstractTableModel;

/**
 * <p>AbstractTableModel that incudes the firing methods used by both AbstractTableModel and
 * DefaultTableModel, but are not include in the TableModel interface. This class lets you call those methods
 * without needing to cast your TableModel to a concrete class. You can use this class the same way you
 * would use AbstractTableModel.</p>
 * @see FiringTableModel
 * @see DefaultFiringTableModel
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 3/30/26
 * <br>Time: 2:20 AM
 * <br>@author Miguel Muñoz (<a href="https://github.com/SwingGuy1024">https://github.com/SwingGuy1024</a>)</p>
 */
public abstract class AbstractFiringTableModel extends AbstractTableModel implements FiringTableModel {
  public AbstractFiringTableModel() {
    super();
  }

  @Override
  public abstract int getRowCount();

  @Override
  public abstract int getColumnCount();

  @Override
  public abstract Object getValueAt(int rowIndex, int columnIndex);
}

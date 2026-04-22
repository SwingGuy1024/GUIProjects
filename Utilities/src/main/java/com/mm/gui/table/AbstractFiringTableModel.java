package com.mm.gui.table;

import javax.swing.table.AbstractTableModel;

/**
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

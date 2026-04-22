package com.mm.gui.table;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 3/30/26
 * <br>Time: 2:35 AM
 * <br>@author Miguel Muñoz (<a href="https://github.com/SwingGuy1024">https://github.com/SwingGuy1024</a>)</p>
 */
@SuppressWarnings("UseOfObsoleteCollectionType")
public class DefaultFiringTableModel extends DefaultTableModel implements FiringTableModel {
  public DefaultFiringTableModel() {
    super();
  }

  public DefaultFiringTableModel(int rowCount, int columnCount) {
    super(rowCount, columnCount);
  }

  public DefaultFiringTableModel(Vector<?> columnNames, int rowCount) {
    super(columnNames, rowCount);
  }

  public DefaultFiringTableModel(Object[] columnNames, int rowCount) {
    super(columnNames, rowCount);
  }

  public DefaultFiringTableModel(Vector<? extends Vector> data, Vector<?> columnNames) {
    super(data, columnNames);
  }

  public DefaultFiringTableModel(Object[][] data, Object[] columnNames) {
    super(data, columnNames);
  }
}

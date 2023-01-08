package com.mm.gui.table;

import javax.swing.table.TableModel;

/*
 * Created using IntelliJ IDEA. Date: Apr 27, 2005 Time: 1:59:31 PM
 *
 * @author Miguel Mu\u00f1oz
 *         <p/>
 *         Copyright (c) 2004 Miguel Munoz
 */

/**
 * A model to support the use of a TableRowHeader.
 * @see TableRowHeader
 */
public interface BasicRowHeaderModel {
	/**
	 * Returns the number of rows. This is identical to a method in TableModel.
	 * @return The number of rows.
	 */
	int getRowCount();

	/**
	 * Gets the name of the row from the row model index. This name appears in the TableRowHeader.
	 * If you need to name the row based on the view index, be sure to convert the model index 
	 * to the view index.
	 * @param rowModelIndex The model index of the row
	 * @return The name of the row to appear in the TableRowHeader.
	 */
	Object getRowName(int rowModelIndex);

	/**
	 * A convenience model that supports both the basic TableModel and the BasicRowHeaderModel.
	 * Your table model may implement this interface to support both the table 
	 * and its header.
	 */
	interface RowHeaderTableModel extends BasicRowHeaderModel, TableModel { }
}

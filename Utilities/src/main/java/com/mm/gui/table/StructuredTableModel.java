/*
 * Created using IntelliJ IDEA. Date: Apr 25, 2005 Time: 2:35:47 AM
 *
 * @author Miguel Mu\u00f1oz
 *         <p/>
 *         Copyright (c) 2004 Miguel Munoz
 * 
 * Note to Java Development Team 
 */

package com.mm.gui.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * This class implements a TableModel with a very common structure. It makes
 * common assumptions about the structure in order to separate the model into
 * row models and column models. This class then implements the model's methods
 * by extracting the row (if used) and passing it to the specified column.
 * <p/>
 * A Column is implemented by extending the AbstractColumn class and overriding
 * its <code>getValue()</code> method, and any other methods that are needed.
 * This makes the TableModel much easier to maintain, since any changes to a
 * specific column are made in that column's class, and won't interact with the
 * code for any other column.
 * <p/>
 * Since this class extends AbstractTableModel, all its familiar event handling
 * may be used.
 *
 * @see AbstractColumn
 */
public class StructuredTableModel<_RowType>
				extends AbstractTableModel
				implements
				BasicRowHeaderModel {
	private RowModel<_RowType> mRows;                   // Here is the row model...
	private List<AbstractColumn<_RowType, ?>> mColumns; // and column model.

	/**
	 * Construct a StructuredTableModel from a List of objects of _RowType.
	 * Columns will be added later.
	 * @param pRows The List
	 */
	public StructuredTableModel(List<_RowType> pRows) {
		this(new RowModel.ListRowModel<_RowType>(pRows));
	}

	/**
	 * Construct a StructuredTableModel from an array of objects of _RowType.
	 * Columns will be added later.
	 * @param pRows The array.
	 */
	public StructuredTableModel(_RowType[] pRows) {
		this(new RowModel.ArrayRowModel<_RowType>(pRows));
	}

	/**
	 * Construct a StructuredTableModel from a List of objects of _RowType and a List of columns.
	 * @param pRows The List of rows.
	 * @param pColumns The Columns.
	 */
	public StructuredTableModel(List<_RowType> pRows, List<AbstractColumn<_RowType, ?>> pColumns) {
		this(pRows);
		for (AbstractColumn<_RowType, ?> column: pColumns)
			addColumn(column);
	}
	
	/**
	 * Construct a StructuredTableModel from an array of objects of _RowType and an array of columns.
	 * @param pRows The array of rows.
	 * @param pColumns The Columns.
	 */
	public StructuredTableModel(_RowType[] pRows, AbstractColumn<_RowType, ?>[] pColumns) {
		this(pRows);
		for (AbstractColumn<_RowType, ?> column : pColumns) {
			addColumn(column);
		}
	}

	/**
	 * Construct a StructuredTableModel from a List of objects of _RowType and an array of columns.
	 * @param pRows The List of rows.
	 * @param pColumns The Columns.
	 */
	public StructuredTableModel(List<_RowType> pRows, AbstractColumn<_RowType, ?>[] pColumns) {
		this(pRows);
		for (AbstractColumn<_RowType, ?> column: pColumns)
			addColumn(column);
	}
	
	/**
	 * Construct a StructuredTableModel from an array of objects of _RowType and a List of columns.
	 * @param pRows The array of rows.
	 * @param pColumns The Columns.
	 */
	public StructuredTableModel(_RowType[] pRows, List<AbstractColumn<_RowType, ?>> pColumns) {
		this(pRows);
		for (AbstractColumn<_RowType, ?> column : pColumns) {
			addColumn(column);
		}
	}

	/**
	 * Construct a StructuredTableModel from a RowModel. The other constructors 
	 * call this constructor. Columns will be added later.
	 * @param pRows The row model.
	 */
	public StructuredTableModel(RowModel<_RowType> pRows) {
		mColumns = makeColumnList();
		mRows = pRows;
	}

	final protected List<AbstractColumn<_RowType, ?>> makeColumnList() {
		return new ArrayList<AbstractColumn<_RowType, ?>>();
	}

	protected List<? extends AbstractColumn<_RowType, ?>> getColumnList() {
		return mColumns;
	}

	/**
	 * Appends the column to the current list of columns. <p><em>Note:</em> Be
	 * advised that you shouldn't do this after the model has been set on a
	 * table, unless you follow it by firing an event indicating the table
	 * structure has changed.
	 *
	 * @param pCol The column to add.
	 * @see #fireTableStructureChanged()
	 */
	public void addColumn(AbstractColumn<_RowType, ?> pCol) {
		mColumns.add(mColumns.size(), pCol);
	}

	/**
	 * Inserts the column into the specified place in the list of columns.
	 * <p><em>Note:</em> Be advised that you shouldn't do this after the model
	 * has been set on a table, unless you follow it by firing an event
	 * indicating the table structure has changed.
	 *
	 * @param pWhere
	 * @param pCol
	 */
	public void insertColumn(int pWhere, AbstractColumn<_RowType, ?> pCol) {
		mColumns.add(pWhere, pCol);
	}

	/**
	 * Gets the column at the specified index.
	 *
	 * @param colIndex The index of the column to retrieve.
	 * @return The Column instance.
	 */
	public AbstractColumn<_RowType, ?> getColumn(int colIndex) {
		return mColumns.get(colIndex);
	}

	/**
	 * Returns the number of rows in the model.
	 *
	 * @return the number of rows in the model
	 * @see #getColumnCount
	 */
	public int getRowCount() {
		return mRows.getRowCount();
	}

	public Object getRowName(int ii) {
		return String.valueOf(ii);
	}

	/**
	 * Returns the number of columns in the model.
	 *
	 * @return the number of columns in the model
	 * @see #getRowCount
	 */
	public int getColumnCount() {
		return mColumns.size();
	}

	public RowModel.Expandable<_RowType> getExpandableRows() { return (RowModel.Expandable<_RowType>) mRows; }
	public RowModel.Editable<_RowType> getEditableRows() { return (RowModel.Editable<_RowType>) mRows; }
	public RowModel<_RowType> getRows() {
		return mRows;
	}

	/**
	 * Returns the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>. Delegates this work to the column instance.
	 *
	 * @param	rowIndex	the row whose value is to be queried
	 * @param	columnIndex the column whose value is to be queried
	 * @return the value Object at the specified cell
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return getColumn(columnIndex).getValue(mRows.getRow(rowIndex));
	}

	/**
	 * Sets the value in the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code> to <code>aValue</code>. Delegates this work to the
	 * column instance.
	 * <p/>
	 * This also queries the column's updateTable and (if needed) updateRow
	 * properties. If either of those returns true, this calls
	 * fireTableRowsUpdated() with either the whole table (if updateTable is
	 * true) or the current row (if updateRow is true). If updatTable is true,
	 * this doesn't bother to query the updateRow property.
	 *
	 * @param aValue      value to assign to cell
	 * @param rowIndex    row of cell
	 * @param columnIndex column of cell
	 * @see AbstractColumn#getUpdateRow
	 * @see AbstractColumn#getUpdateTable
	 * @see AbstractTableModel#fireTableRowsUpdated(int,int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (!isCellEditable(rowIndex, columnIndex)) {
			return;
		}
		AbstractColumn<_RowType, ?> column = getColumn(columnIndex);
		setTheValue(rowIndex, column, aValue);
	}

	/**
	 * I do the work of setValue in this private method because it lets me specify a generic type. I can't
	 * do that in setValue because it needs to match the signature of the method it's overriding.
	 * @param rowIndex
	 * @param pColumn
	 * @param unknownValue
	 */
	private <V> void setTheValue(int rowIndex, AbstractColumn<_RowType, V> pColumn, Object unknownValue) {
		//noinspection unchecked
		V aValue = (V) unknownValue;
		_RowType theRow = mRows.getRow(rowIndex);
		assert theRow != null;
		pColumn.setValue(aValue, theRow);
		if (pColumn.getUpdateTable(theRow)) {
			fireTableRowsUpdated(0, mRows.getRowCount() - 1);
		} else if (pColumn.getUpdateSubsequentRows(theRow)) {
			fireTableRowsUpdated(rowIndex, mRows.getRowCount() - 1);
		} else if (pColumn.getUpdateRow(theRow)) {
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}

	/**
	 * Returns the name of the specified column
	 *
	 * @param column the column being queried
	 * @return the name of <code>column</code>, from the column model
	 */
	public String getColumnName(int column) {
		return getColumn(column).getColumnName();
	}

	/**
	 * Returns the most specific superclass for all the cell values in the
	 * column.  This is used by the <code>JTable</code> to set up a default
	 * renderer and editor for the column. Delegates this to the column
	 * instance.
	 *
	 * @param columnIndex the column being queried
	 * @return the Object.class
	 */
	public Class<?> getColumnClass(int columnIndex) {
		return getColumn(columnIndex).getColumnClass();
	}

	/**
	 * Returns true if the cell at <code>rowIndex</code> and
	 * <code>columnIndex</code> is editable.  Otherwise, <code>setValueAt</code>
	 * on the cell will not change the value of that cell. Delegates this work
	 * to the column instance.
	 *
	 * @param rowIndex    the row being queried
	 * @param columnIndex the column being queried
	 * @return false
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return getColumn(columnIndex).isEditable(mRows.getRow(rowIndex));
	}

	/**
	 * Returns the preferred width of the column. <p> <b>Note:</b> For this to
	 * work, you must call <code>setUpHeader(JTable tbl)</code> on the table
	 * after setting this as the table model.
	 *
	 * @param column The column index
	 * @return the preferredWidth from the AbsractColumn.
	 * @see #setUpHeader(JTable)
	 */
	public int getPreferredWidth(int column) {
		return getColumn(column).getPreferredWidth();
	}

	/**
	 * Returns the custom renderer for the specified column. If this renderer is
	 * not null, it overrides the default renderer for the column class.<p>
	 * <b>Note:</b> For this to work, you must call <code>setUpHeader(JTable
	 * tbl)</code> on the table after setting this as the table model.
	 *
	 * @param column The column index
	 * @return The custom renderer for the column.
	 * @see #setUpHeader(JTable)
	 */
	public TableCellRenderer getRenderer(int column) {
		return getColumn(column).getRenderer();
	}

	/**
	 * Returns the custom cell editor for specified column. <p> If this editor
	 * is not null, it overrides the default cell editor for the column
	 * class.<p> <b>Note:</b> For this to work, you must call
	 * <code>setUpHeader(JTable tbl)</code> on the table after setting this as
	 * the table model.
	 *
	 * @param column The column index
	 * @return The custom editor for the column
	 * @see #setUpHeader(JTable)
	 */
	public TableCellEditor getEditor(int column) {
		return getColumn(column).getEditor();
	}

//	public <_ValueType, _SortKey extends Comparable<? super _SortKey>>void sort(int column) {
//		 Comparator<? super _RowType> cmp = makeComparator(column);
//		mRows.sort(cmp);
//		fireTableRowsUpdated(0, mRows.getRowCount() - 1);
//	}
	//
	/**
	 * Since this table model includes information handled by the Table Header
	 * as well as the JTable, we need to send the information to the
	 * JTableHeader. The user should call this method after setting the model on
	 * the JTable. <p>Without this call, this TableModel won't support column
	 * widths, column tool tips, or custom renderers and editors.
	 *
	 * @param tbl
	 * @see #setUpHeader(JTable)
	 */
	public static void setUpHeader(final JTable tbl) {
		TableModel tMdl = tbl.getModel();
		if (tMdl instanceof StructuredTableModel) {
			final StructuredTableModel sMdl = (StructuredTableModel) tMdl;
			applyHeader(tbl, sMdl);
		}
	}

	private static void applyHeader(JTable pTable, StructuredTableModel pModel) {
		JTableHeader hdr = pTable.getTableHeader();
		for (int col = 0; col < pModel.getColumnCount(); ++col) {
			TableColumn colMdl = hdr.getColumnModel().getColumn(col);
			applyColumnToHeader(pModel.getColumn(col), colMdl);
		}
	}

	private static void applyColumnToHeader(AbstractColumn col,
	                                        TableColumn colMdl) {
		colMdl.setPreferredWidth(col.getPreferredWidth());
		TableCellRenderer rndr = col.getRenderer();
		if (rndr != null) {
			colMdl.setCellRenderer(rndr);
		}
		TableCellEditor edt = col.getEditor();
		if (edt != null) {
			colMdl.setCellEditor(edt);
		}
	}

//	private <_ValueType/*, _SortKey extends Comparable<? super _SortKey>*/>
//	Comparator<? super _RowType> makeComparator(int columnIndex) {
////		AbstractColumn<_RowType, ?, ?> column = getColumn(columnIndex);
//		//noinspection unchecked
//		final AbstractColumn<_RowType, _ValueType> chosenColumn =
//						(AbstractColumn<_RowType, _ValueType>)getColumn(columnIndex);
//		assert chosenColumn instanceof AbstractSortableColumn;
//		//noinspection unchecked
//		final AbstractSortableColumn sortColumn = (AbstractSortableColumn<_RowType, _ValueType, ?>) chosenColumn;
//						
//		return new Comparator<_RowType>() {
//			/**
//			 * Compares its two arguments for order.  Returns a negative integer, zero,
//			 * or a positive integer as the first argument is less than, equal to, or
//			 * greater than the second.<p>
//			 */
//			public int compare(_RowType r0, _RowType r1){
//				//noinspection unchecked
//				return sortColumn.getSortKey(r0).compareTo(sortColumn.getSortKey(r1));
//			}
//		};
//	}
}

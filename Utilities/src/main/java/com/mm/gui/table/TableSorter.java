package com.mm.gui.table;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

/**
 * Created by IntelliJ IDEA.
 * User: Miguel
 * Date: Apr 9, 2006
 * Time: 11:38:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class TableSorter<R> {
	private int mKeyCountToSort=1;
	private int mSortedColumn = -1;
	private boolean mAscending;
	private JTable mTable;
	private StructuredTableModel<R> mModel;
	
	public TableSorter(JTable pTable, StructuredTableModel<R> sortableModel) {
		mTable = pTable;
		mModel = sortableModel;
		init();
	}

	public int getKeyCountToSort() { return mKeyCountToSort; }
	public void setKeyCountToSort(int pKeyCountToSort) { mKeyCountToSort = pKeyCountToSort; }

	private void init() {
		MouseListener ml = new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				sortClickedColumn(evt);
			}
		};
		mTable.getTableHeader().addMouseListener(ml);
	}

	private void sortClickedColumn(MouseEvent evt) {
		if (evt.getClickCount() == mKeyCountToSort) {
			TableColumnModel cModel = mTable.getTableHeader().getColumnModel();
			final int columnIndex = cModel.getColumnIndexAtX(evt.getX());
			
			if (columnIndex == mSortedColumn)
				mAscending = !mAscending;
			else
				mAscending = true;
			mSortedColumn = columnIndex;
			// In case they clicked to the right of the last column...
			if (columnIndex < mModel.getColumnCount() && columnIndex >= 0) {
				AbstractColumn<R, ?> column = mModel.getColumn(columnIndex);
				if (column instanceof AbstractSortableColumn) {
					AbstractSortableColumn<R, ?, ?> col = (AbstractSortableColumn<R, ?, ?>)column;
					RowModel<R> rows = mModel.getRows();
					rows.sort(col.getComparator(mAscending));
					mModel.fireTableRowsUpdated(0, mModel.getRowCount()-1);
				}
			}
		}
	}
}

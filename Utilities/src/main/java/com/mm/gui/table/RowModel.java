//////////////////////////////////////////////////////////////////////////////////////
//
//
// COPYRIGHT:
//    Copyright (c) 2001-2004, iRise, Inc. ALL RIGHTS RESERVED
//
// TRADE SECRET NOTICE
//    This software contains trade secret information belonging to iRise, Inc.
//    Permission to view, copy, or disclose is prohibited without the express
//    written consent of iRise, Inc.
//
// WARRANTY DISCLAIMER
//    THE IRISE CORPORATION MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
//    ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
//    EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
//    PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT INFRINGE
//    ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS. THE SOFTWARE
//    AND DATA ARE PROVIDED "AS IS".
//
// SPECIFICATIONS ARE SUBJECT TO CHANGE WITHOUT NOTICE
//
//////////////////////////////////////////////////////////////////////////////////////

package com.mm.gui.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mmunoz
 * Date: Sep 15, 2005
 * Time: 3:47:56 PM
 */
public interface RowModel<_RowType> {
	public _RowType getRow(int rowIndex);

	public int getRowCount();
	public void sort(Comparator<? super _RowType> cmp);

	public interface Editable<_RowType> extends RowModel<_RowType>
	{
		public _RowType setRow(_RowType theRow, int where);
		public void swapRows(int ii, int jj);
	}

	public interface Expandable<_RowType> extends Editable<_RowType> {
		public boolean addRow(_RowType addedRow);
		public List<_RowType> getBackingList();
	}

	public class ListRowModel<_RowType> implements Expandable<_RowType> {
		private List<_RowType> mList;

		/**
		 * Wraps the specified List as the RowModel. The List must be mutable
		 * if the user will be making changes.
		 * @param rows
		 */
		public ListRowModel(Collection<_RowType> rows) {
			this(rows, false);
		}

		/**
		 * Create a ListRowModel of type _RowType from a collection. The
		 * duplicate parameter determines if it duplilcates the List parameter,
		 * or if it generates a separate ArrayList
		 * @param rows      The Collection of rows.
		 * @param duplicate If true, and rows is a List, sets the row list to
		 *                  {@code rows}. Otherwise generates a new ArrayList from the
		 *                  collection, with the order determined by the Collection's iterator().
		 */
		public ListRowModel(Collection<_RowType> rows, boolean duplicate) {
			if (rows instanceof List && !duplicate) {
				mList = (List<_RowType>) rows;
			} else {
				mList = new ArrayList<_RowType>(rows);
			}
		}

		public _RowType getRow(int rowIndex) {
			return mList.get(rowIndex);
		}

		public int getRowCount() {
			return mList.size();
		}

		public void sort(Comparator<? super _RowType> comparator) {
			Collections.sort(mList, comparator);
		}

		public _RowType setRow(_RowType rowType, int where) {
			_RowType oldOne = mList.get(where);
			mList.set(where, rowType);
			return oldOne;
		}

		public void swapRows(int ii, int jj) {
			_RowType first = mList.get(ii);
			mList.set(ii, mList.get(jj));
			mList.set(jj, first);
		}

		public boolean addRow(_RowType theRow) {
			return mList.add(theRow);
		}

		public List<_RowType> getBackingList() { return mList; }
	}

	public class ArrayRowModel<_RowType> implements Editable<_RowType> {
		private _RowType[] mRows;

		/**
		 * All five methods call this method. So if your row model needs to
		 * represent a volatile array, you can override this method and all
		 * three implementations in this class will still work.
		 *
		 * @return an array of the rows.
		 */
		protected _RowType[] getRows() {
			return mRows;
		}

		public ArrayRowModel(_RowType[] array) {
			mRows = array;
		}

		public _RowType getRow(int rowIndex) {
			return getRows()[rowIndex];
		}

		public int getRowCount() {
			return getRows().length;
		}

		public void sort(Comparator<? super _RowType> cmp) {
			Arrays.sort(getRows(), cmp);
		}

		public _RowType setRow(_RowType rowType, int where) {
			_RowType[] data = getRows();
			_RowType previous = data[where];
			getRows()[where] = rowType;
			return previous;
		}

		public void swapRows(int ii, int jj) {
			_RowType[] rows = getRows();
			_RowType first = rows[ii];
			rows[ii] = rows[jj];
			rows[jj] = first;
		}
	}
}

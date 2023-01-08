package com.mm.gui.table;

import java.util.Comparator;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Created using IntelliJ IDEA. Date: Apr 1, 2005 Time: 2:38:09 PM
 * I may want to change this name to AbstractComparableColumn or AbstractComparatorColumn to make it clear
 * that an AbstractColumn may still be sortable. I may not need this class, because the TableSorter takes
 * a Comparator, which is independent of the row.
 * 
 * If I want to make this compatible with JDK 1.6, I might want to eliminate this class and instead write a 
 * KeyBasedTableSorter, which is smart enough to use collation keys to do the sorting. In this case, I would 
 * write a new ModelWrapper which would extract the sort key using the getSortKey() method. I might also 
 * write a StringColumn class which would set a collation key every time the value changed. I'm not sure if 
 * this would work.
 * 
 * Maybe this class should be renamed AbstractKeyBasedColumn or AbstractSortableKeyColumn, or 
 * AbstractKeySortColumn to signify that it uses keys to sort values. Maybe it should be 
 * AbstractStringSortingColumn. 
 *
 * @author Miguel Mu\u00f1oz
 *         <p/>
 *         Copyright (c) 2004 Miguel Munoz
 */

public abstract class AbstractSortableColumn<_RowType, _CompareType, _SortKey extends Comparable<? super _SortKey>>
    extends AbstractColumn<_RowType, _CompareType>
    implements SortableColumn<_RowType, _SortKey>
{
	protected AbstractSortableColumn(String pName, Class pClass, int pPrefWidth) {
		super(pName, pClass, pPrefWidth);
		setSortable(true);
	}

	protected AbstractSortableColumn(String pName, Class pClass, int pPrefWidth, boolean pEditable) {
		super(pName, pClass, pPrefWidth, pEditable);
		setSortable(true);
	}

	protected AbstractSortableColumn(String pName, Class pClass, int pPrefWidth, TableCellRenderer pRend) {
		super(pName, pClass, pPrefWidth, pRend);
		setSortable(true);
	}

	protected AbstractSortableColumn(String pName, Class pClass, int pPrefWidth, TableCellRenderer pRend, boolean pEditable) {
		super(pName, pClass, pPrefWidth, pRend, pEditable);
		setSortable(true);
	}

	public AbstractSortableColumn(String pName, Class pClass, int pPrefWidth, TableCellRenderer pRnd, TableCellEditor pEdt)
	{
		super(pName, pClass, pPrefWidth, pRnd, pEdt);
		setSortable(true);
	}

	/**
	 * Returns a Comparable for sorting the table's rows by this field. Since
	 * each column returns a different Comparable for the same type of row, this
	 * Comparable will necessarily be inconsistent with the equals method for
	 * the row. Since the Comparable isn't used to sort a SortedSet or
	 * SortedMap, this isn't a problem.
	 * <P> The default implementation just calls getValue. This is valid for 
	 * AbstractSortableColumns for which _CompareType is the same as _SortKey.
	 * If a subclass uses a different type for _SortKey, it needs to override
	 * this method.
	 * @param pRow The row
	 * @return a _SortKey, which implements Comparable, used to sort the rows.
	 */
	public _SortKey getSortKey(_RowType pRow)
	{
		//noinspection unchecked
		return (_SortKey)getValue(pRow);
	}

	public Comparator<? super _RowType> getComparator(boolean ascending) {
		if (ascending) {
			return new Comparator<_RowType>() {
				public int compare(_RowType o1, _RowType o2) {
					return getSortKey(o1).compareTo(getSortKey(o2));
				}
			};
		} else {
			return new Comparator<_RowType>() {
				public int compare(_RowType o1, _RowType o2) {
					return getSortKey(o2).compareTo(getSortKey(o1));
				}
			};
		}
	}
}

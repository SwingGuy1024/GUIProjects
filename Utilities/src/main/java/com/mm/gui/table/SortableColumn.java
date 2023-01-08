package com.mm.gui.table;

/**
 * Created using IntelliJ IDEA. Date: Apr 1, 2005 Time: 2:30:23 PM
 *
 * @author Miguel Mu\u00f1oz
 *         <p>
 *         Copyright (c) 2004 Miguel Mu\u00f1oz</p>
 */

public interface SortableColumn<_RowType, _CompareType extends Comparable<? super _CompareType>>
{
	public _CompareType getSortKey(_RowType pRow);
}

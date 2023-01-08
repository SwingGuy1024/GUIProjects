package com.mm.gui.table;

import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

/**
 * Separates TablesModels into separate row models and column models for
 * ease-of-maintanance.
 *
 * @author Miguel Mu\u00f1oz Copyright (c) 2004 Miguel Munoz
 */

public class StructuredCombinedTableModel <_RowModel>

        extends StructuredTableModel<_RowModel>
        implements
//	     BasicRowHeaderModel.RowHeaderTableModel,
        TableColumnModelListener {

	public StructuredCombinedTableModel(RowModel<_RowModel> pRows) {
		super(pRows);
	}

    public StructuredCombinedTableModel(List<_RowModel> pRowList) {
        super(new RowModel.ListRowModel(pRowList));
    }

	// these four methods aren't usually handled by the TableModel. They're
	// handled by the table header, and users need to call setUpHeader() for
	// these to work.

	public Object getRowName(int row) {
		return row;
	}

	private void storeAllWidths() {

	}

	/**
	 * Implements TableColumnModelListener Tells listeners that a column was
	 * added to the model.
	 */

	public void columnAdded(TableColumnModelEvent e) {
		storeAllWidths();// Needed?
//		int where = e.getToIndex();
//		Object source=e.getSource();
//		System.out.println("Column added at " + where + ": Source of " + source.getClass());
//		TableColumnModel colMdl=(TableColumnModel)source;
//		applyColumnToHeader(getColumn(where), colMdl.getColumn(where));
	}

	/**
	 * Implements TableColumnModelListener Tells listeners that a column was
	 * removed from the model.
	 */
	public void columnRemoved(TableColumnModelEvent e) {
		System.out.println("C  gone: " + e.getFromIndex() + " - " + e.getToIndex() + e.getSource()
		        .getClass());
	}

	/**
	 * Implements TableColumnModelListener Tells listeners that a column was
	 * repositioned.
	 */
	public void columnMoved(TableColumnModelEvent e) {
		System.out.println("C moved: " + e.getFromIndex() + " - " + e.getToIndex() + e.getSource()
		        .getClass());
	}

	/**
	 * Implements TableColumnModelListener Tells listeners that a column was
	 * moved due to a margin change.
	 */
	public void columnMarginChanged(ChangeEvent e) {
		System.out.println("Column Margin changed: src=" + e.getSource().getClass());
	}

	/**
	 * Implements TableColumnModelListener Tells listeners that the selection
	 * model of the TableColumnModel changed.
	 */
	public void columnSelectionChanged(ListSelectionEvent e) {
	}
//	/**
//	 * This does the same thing as the default header renderer, but it also
//	 * sets the tool tip. Since the default class is a private inner class, I 
//	 * couldn't extend it, so I had to copy the code. This may break if the 
//	 * original code changes.
//   * <p>
//   * <b>Update: </b>In JDK 1.5, the code did change. Damn!
//	 */
//	static class RevisedHeaderRenderer extends DefaultTableCellRenderer //implements UIResource {
//	{
//		public Component getTableCellRendererComponent(JTable table,
//		                                               Object value,
//		                                               boolean isSelected,
//		                                               boolean hasFocus,
//		                                               int row,
//		                                               int column)
//		{
//			if (table!=null)
//			{
//				JTableHeader header=table.getTableHeader();
//				if (header!=null)
//				{
//					setForeground(header.getForeground());
//					setBackground(header.getBackground());
//					setFont(header.getFont());
//				}
//			}
//
//			String title = value==null ? "" : value.toString();
//			setText(title);
//			setToolTipText(title);
//			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
//			return this;
//		}
//	}
//	
//	private void link(AbstractColumn aCol, TableColumn tCol)
//	{
//		tCol.addPropertyChangeListener(aCol);
//	}
}

/*
  The following was taken from setUpHeader, when it also added the 
  ColumnModelListener. I put it here In case I revive this feature.
//	    tbl.getTableHeader().getColumnModel().addColumnModelListener(sMdl);
//			
//			TableModelListener tml = new TableModelListener()
//			{
//				private final JTable mLsnrTable = tbl;
//				public void tableChanged(final TableModelEvent e)
//				{
//					// This assumes the table header knows about the added column. 
//					// It doesn't yet, so I delay the event until later.
//					if (e.getType() == e.UPDATE && e.getColumn() == e.ALL_COLUMNS)
//					{
//						Runnable rn = new Runnable()
//						{ public void run()
//							{
//								applyHeader(mLsnrTable, (StructuredTableModel)e.getSource());
//							}
//						};
//						SwingUtilities.invokeLater(rn);
//						System.out.println("TableModelEvent: \n");
//						System.out.println("  Col = " + e.getColumn());
//						System.out.println("  Row from " + e.getFirstRow() + " to " + e.getLastRow());
//						int type=e.getType();
//						System.out.print("  Type: ");
//						System.out.println(type==e.INSERT? "Insert" : (type==e.DELETE? "Delete" : "Update"));
//					}
//				}
//			};
//			sMdl.addTableModelListener(tml);

*/

package com.mm.gui.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Abstract Column 
 * 
 * <br>R is the class for each row in the table. 
 * 
 * <p> This class makes no assumptions about the form of the data in R
 * Typically, this class is subclassed to implement a getValue() method, and
 * potentially a setValue() method, but the rest of the properties are set
 * in the constructor. This makes subclasses very easy to write. For example,
 * a column to display the price property of an instance of class Product would
 * look like this:
 * <pre>
 *  private static class PriceColumn extends AbstractColumn<Product, Double> {
 *      PriceColumn() {
 *          super("Price",             // Column Name
 *                Double.class,        // Column class
 *                65,                  // Column Width
 *                mMoneyRenderer       // TableCellRenderer
 *          );
 *      }
 *      public Double     getValue(Product row) { return row.getPrice(); }
 *      public void       setValue(Double pr, Product row) { row.setPrice(pr); }
 *      public boolean    isEditable(Product pNum) { return true; }
 *  }
 * </pre>
 * The constructors here can't possibly give us every needed combination of
 * parameters, but developers can easily write a subclass with any other
 * constructors they need. In the example above, a constructor that includes a
 * value for the editable property would be useful, particularly if several
 * columns needed it.
 * <p>
 * <em>Caution:</em> When subclassing, be sure to say {@code extends
 * AbstractColumn<R, V>} instead of {@code extends AbstractColumn}, or you
 * will get confusing error messages with any method you override.
 *
 * @author Miguel Mu\u00f1oz Copyright (c) 2004 by Miguel Munoz
 * @param <R> The type of each row in the table
 * @param <V> The value type for this column. The getColumnClass() method returns the class
 * object for this type, V.class.
 */
public abstract class AbstractColumn<R, V>
				implements PropertyChangeListener {
	private int mPrefWidth;
	private String mName;
	private TableCellRenderer mRenderer;
	private TableCellEditor mEditor;
	private Class<V> mClass;
	private int mSavedWidth;
	private boolean mIsEditable = false;
	private boolean mSortable = false;
	private boolean mComparatorUsedForSorting = false;

	/**
	 * Instantiate a Read-Only column with the specified parameters.
	 * @param pName       The name of the column in the column header
	 * @param pClass      The class of the column's data
	 * @param pPrefWidth  The preferred width of the column
	 */
	protected AbstractColumn(String pName, Class<V> pClass, int pPrefWidth) {
		this(pName, pClass, pPrefWidth, null, false);
	}

	/**
	 * Instantiate a column with the specified parameters.
	 * @param pName       The name of the column in the column header
	 * @param pClass      The class of the column's data
	 * @param pPrefWidth  The preferred width of the column
	 * @param pEditable   The isEditable property.
	 */
	protected AbstractColumn(String pName, Class<V> pClass, int pPrefWidth, boolean pEditable) {
		this(pName, pClass, pPrefWidth, null, pEditable);
	}

	/**
	 * Instantiate a Read-Only column with the specified parameters.
	 * @param pName       The name of the column in the column header
	 * @param pClass      The class of the column's data
	 * @param pPrefWidth  The preferred width of the column
	 * @param pRend       The Default TableCellRenderer. (May be null)
	 * 
	 */
	protected AbstractColumn(String pName, Class<V> pClass, int pPrefWidth, TableCellRenderer pRend) {
		this(pName, pClass, pPrefWidth, pRend, false);
	}

	/**
	 * Instantiate a column with the specified parameters.
	 * @param pName       The name of the column in the column header
	 * @param pClass      The class of the column's data
	 * @param pPrefWidth  The preferred width of the column
	 * @param pRend       The Default TableCellRenderer. (May be null)
	 * @param pEditable   The isEditable property
	 * 
	 */
	protected AbstractColumn(String pName, Class<V> pClass, int pPrefWidth, TableCellRenderer pRend, boolean pEditable) {
		mName = pName;
		mClass = pClass;
		mPrefWidth = pPrefWidth;
		mRenderer = pRend;
		mIsEditable = pEditable;
	}

	/**
	 * Instantiate an editable column with the specified parameters.
	 * @param pName       The name of the column in the column header
	 * @param pClass      The class of the column's data
	 * @param pPrefWidth  The preferred width of the column
	 * @param pRend       The Default TableCellRenderer. (May be null)
	 * @param pEd         The Default TableCellEditor. (May be null)
	 */
	protected AbstractColumn(String pName,
	                         Class<V> pClass,
	                         int pPrefWidth,
	                         TableCellRenderer pRend,
	                         TableCellEditor pEd) {
		this(pName, pClass, pPrefWidth, pRend);
		mEditor = pEd;
		mIsEditable = true;
	}

	public abstract V getValue(R pRow);

	public void setValue(V value, R pRow) {
		//noinspection StringConcatenation,MagicCharacter
		assert false : "SetValue called for non-editable column: " + getClass()
						+ " <vtype=" + value.getClass() + ", rtype=" + pRow.getClass() + '>';
	}

	public Class getColumnClass() { return mClass; }

	/**
	 * Columns are not editable by default.
	 *
	 * @param pRow The row
	 * @return true if the row is editable, false otherwise.
	 */
	public boolean isEditable(R pRow) {
		return mIsEditable;
	}

	public String getColumnName() { return mName; }

	public void setName(String pName) { mName = pName; }

	public int getPreferredWidth() { return mPrefWidth; }

	public void setPreferredWidth(int pWidth) { mPrefWidth = pWidth; }

	public TableCellRenderer getRenderer() { return mRenderer; }

	public void setRenderer(TableCellRenderer pRndr) { mRenderer = pRndr; }

	public TableCellEditor getEditor() { return mEditor; }

	public void setEditor(TableCellEditor pEdtr) { mEditor = pEdtr; }

	public int getSavedWidth() { return mSavedWidth; }

	public void setSavedWidth(int pSavedWidth) { mSavedWidth = pSavedWidth; }

	public boolean getAllEditable() { return mIsEditable; }

	public void setAllEditable(boolean pEd) { mIsEditable = pEd; }

	/**
	 * Specifies if changes to the current cell will cause changes elsewhere in
	 * the same row. By default this returns false. If changes to this cell can
	 * force changes to other cells <i>in the same row</i> then this method
	 * should be overridden to return true.
	 *
	 * @param pRow The row that is changing.
	 * @return true if changes to this cell force changes to other cells in the
	 *         same row, false otherwise.
	 * @see StructuredCombinedTableModel#setValueAt(Object, int, int)
	 */
	public boolean getUpdateRow(R pRow) { return false; }

	/**
	 * Specifies if changes to the current cell will cause changes elsewhere in
	 * the table, in other rows. By default this returns false. If changes to
	 * this cell can force changes to cells <i>in other rows</i> then this
	 * method should be overridden return true.
	 *
	 * @param pRow The row that is changing.
	 * @return true if changes to this cell force changes to other rows in the
	 *         table, false otherwise.
	 * @see StructuredCombinedTableModel#setValueAt(Object, int, int)
	 */
	public boolean getUpdateTable(R pRow) { return false; }

	/**
	 * Specifies if changes to the current cell will cause changes elsewhere in
	 * the table, in subsequent rows, but not in preceeding rows. By default this
	 * returns false. If changes to this cell can force changes to cells <i>in
	 * other rows below this one</i> then this method should be overridden to 
	 * return true.
	 *
	 * @param pRow The row that is changing.
	 * @return true if changes to this cell force changes to rows below this cell,
	 *         false otherwise.
	 */
	public boolean getUpdateSubsequentRows(R pRow) { return false; }

	/**
	 * This method gets called when a bound property is changed.
	 * <p/>
	 * <em>Note:</em> This is only useful when the model is used by only one
	 * JTable. If more than one JTable use this model, this would prevent the
	 * user from setting a different column width in each table.
	 * Note2: This method probably doesn't work anyway, because the user
	 * probably doesn't change the <i>preferred</i> width when adjusting
	 * column separators.
	 * todo: Figure out if this works.
	 * @param evt A PropertyChangeEvent object describing the event source and
	 *            the property that has changed.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		//noinspection HardCodedStringLiteral,CallToStringEquals
		if ("preferredWidth".equals(evt.getPropertyName())) {
			setPreferredWidth((Integer) evt.getNewValue());
		}
	}
	
	public boolean isSortable() { return mSortable; }
	public void setSortable(boolean pSortable) { mSortable = pSortable; }

	public boolean isComparatorUsedForSorting() { return mComparatorUsedForSorting; }
	public void setComparatorUsedForSorting(boolean pComparatorUsedForSorting) { mComparatorUsedForSorting = pComparatorUsedForSorting; }
}

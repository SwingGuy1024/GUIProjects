package com.mm.gui;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Adds tool tips to the table headers.
 */
public class ToolTipHeaderRenderer extends DefaultTableCellRenderer
{
//	private static final String uiClassID = "TableHeaderUI";
	
//	public String getUIClassID()
//	{
//		return uiClassID;
//	}

//	public void updateUI()
//	{
//		setUI((TableHeaderUI)UIManager.getUI(this));
//		revalidate();
//		repaint();
//		invalidate();//PENDING
//	}
//	 public ToolTipHeaderRenderer(String text)
//	 {
//			setToolTipText(text);
//			setText(text);
//	 }
//
//	 public ToolTipHeaderRenderer(Icon icon)
//	 {
//			setIcon(icon);
//	 }
//
//	 public ToolTipHeaderRenderer(String text, Icon icon)
//	 {
//			this(text);
//			setIcon(icon);
//	 }
//
	 public Component getTableCellRendererComponent
	     (JTable tbl, Object val, boolean sel, boolean focus, int row, int col)
	 {
			JLabel comp = (JLabel) 
			    super.getTableCellRendererComponent (tbl, val, sel, focus, row, col);

			setHorizontalAlignment(JLabel.LEFT);
			JTableHeader hdr = tbl.getTableHeader();
			comp.setFont(hdr.getFont());
			comp.setForeground(hdr.getForeground());
			comp.setBackground(hdr.getBackground());
			comp.setBorder(UIManager.getBorder("TableHeader.cellBorder"));

			return comp;
	 }

	public void setText(String text)
	{
		super.setText(text);
		setToolTipText(text);
	}
}

package com.mm.gui.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * Created by IntelliJ IDEA.
 * User: mmunoz
 * Date: Jul 17, 2005
 * Time: 1:47:28 AM
 */
public class AutoColumnTable extends JTable {
	public static final int DM = 500;

	private MouseListener columnModeListener=null;

	public AutoColumnTable(int defaultMode, int shiftMode, int controlMode, int altMode) {
		super();
		init(defaultMode, shiftMode, controlMode, altMode);
	}

	public AutoColumnTable(TableModel dm, int defaultMode, int shiftMode, int controlMode, int altMode) {
		super(dm);
		init(defaultMode, shiftMode, controlMode, altMode);
	}

	private void init(int defaultMode, int shiftMode, int controlMode, int altMode)
	{
		if (columnModeListener != null)
			getTableHeader().removeMouseListener(columnModeListener);
		columnModeListener = makeModeListener(defaultMode, shiftMode, controlMode, altMode);
		getTableHeader().addMouseListener(columnModeListener);
	}

	private static MouseListener makeModeListener(int defaultMode, int shiftMode, int controlMode, int altMode) {
		MouseListener resizeListener = new FlexibleColumnResizeAdapter(defaultMode, shiftMode, controlMode, altMode);
		return resizeListener;
	}

	public static void main(String[] args) {
		JFrame mf = new JFrame("Test");
		mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TableModel mdl = new TestModel();
		AutoColumnTable view = new AutoColumnTable(mdl, AUTO_RESIZE_NEXT_COLUMN,
		        AUTO_RESIZE_ALL_COLUMNS,
		        AUTO_RESIZE_SUBSEQUENT_COLUMNS,
		        AUTO_RESIZE_LAST_COLUMN);
		mf.add(new JScrollPane(view), BorderLayout.CENTER);
		JPanel btnPanel = new JPanel(new GridLayout());
		view.addButton(btnPanel, JTable.AUTO_RESIZE_ALL_COLUMNS, "All");
		view.addButton(btnPanel, JTable.AUTO_RESIZE_LAST_COLUMN, "Last");
		view.addButton(btnPanel, JTable.AUTO_RESIZE_NEXT_COLUMN, "Next");
		view.addButton(btnPanel, JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS, "Subsq");
		view.addButton(btnPanel, JTable.AUTO_RESIZE_OFF, "Off");
		mf.add(btnPanel, BorderLayout.SOUTH);
		mf.setBounds(10, 10, DM, DM);
		mf.setVisible(true);
	}

	private void addButton(JComponent cmp, int resizeMode, String name)
	{
		JButton btn = new JButton(name);
		cmp.add(btn);
		btn.addActionListener(new ModeListener(resizeMode));
	}

	private class ModeListener implements ActionListener
	{
		int mode;
		ModeListener(int rMode) { mode = rMode; }

		public void actionPerformed(ActionEvent e) {
			setAutoResizeMode(mode);
		}
	}

	private static class TestModel extends AbstractTableModel{
		public int getRowCount() { return 10; }
		public int getColumnCount() { return 10; }
		public Object getValueAt(int rowIndex, int columnIndex) {
			return new Integer(new Dimension(rowIndex, columnIndex).hashCode());
		}
	}

}

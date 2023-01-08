package com.mm.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Enhanced JTable, which works around some bugs in the JTable, and adds some
 * missing features.
 * <p>
 * <B><i>Bugs Fixed:</i></b>
 * <p>
 * <b>Bug 4478121</b> Descenders in Cell Editors get cut off at the bottom
 * <p>
 * <b>Bug XXX</b> JTable doesn't stop editing on focus loss.
 * <p>
 * <b><i>Features:</i></b>
 * <p>
 * Darkens read-only cells just enough to tell them apart from editable cells. 
 *
 * @author Miguel Mu\u00f1oz
 *         <p/>
 *         Copyright (c) 2004 Miguel Munoz
 */
public class JJTable extends JTable
{
  private static final Border sFixBorder = new EditorFixBorder();
  public static final double ash = 0.95;
  
  private boolean mUseRowHeader;
  private BasicRowHeaderModel mRowHeader; 

  /**
   * Create a JJTable.
   */ 
  public JJTable() { this(false); }
  
  /**
   * Create a JJTable with the specified TableModel, and no Row Header.
   * @param pMdl  The TableModel
   */ 
  public JJTable(javax.swing.table.TableModel pMdl) { this(pMdl, false); }
  
  /**
   * Construct a JJTable that uses the default RowHeaderModel, if so specified.
   * @param pUseRowHeader
   */ 
  public JJTable(boolean pUseRowHeader)
  {
    super();
    mUseRowHeader=pUseRowHeader;
    init();
  }
  
  /**
   * Construct a JJTable that uses the specified TableModel, and optionally
   * uses the default RowHeaderModel.
   * @param pMdl           The TableModel
   * @param pUseRowHeader  Specifies use of Default Row Header
   */ 
  public JJTable(javax.swing.table.TableModel pMdl, boolean pUseRowHeader)
  {
    super(pMdl);
    mUseRowHeader=pUseRowHeader;
    init();
  }

  /**
   * Construct a JJTable that uses the specified TableModel and the specified
   * BasicRowHeaderModel.
   * @param pMdl
   * @param pHeadMdl
   */ 
  public JJTable(javax.swing.table.TableModel pMdl, BasicRowHeaderModel pHeadMdl)
  {
    this(pMdl, false);
    mUseRowHeader = true;
    mRowHeader = pHeadMdl;
  }
  
  public JJTable(BasicRowHeaderModel.RowHeaderTableModel pMdl)
  {
    this(pMdl, pMdl);
  }

  private void init()
  {
    // This FocusListener makes sure the editing stops whenever the Table
    // loses the focus, if it's a permanent focus loss. (Pull-down menus
    // generate temporary focus losses.) This doesn't handle every 
    // conceivable case. For example, buttons that act on the selected 
    // text in the editor shouldn't stop the editing. This code doesn't
    // handle that case, but it can be added by registering these buttons with
    // the table. I'm not yet sure if they should be statically registered, so 
    // they act on all tables, or if they should be registered on a 
    // table-by-table basis, or if the developer should have a choice.
    FocusListener editStopper = new FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        if (!e.isTemporary())
          if (isEditing())
            getCellEditor().stopCellEditing();
      }
    };
    addFocusListener(editStopper);
    if (mUseRowHeader)
      installDefaultRowHeaderModel();
  }
  
  private void installDefaultRowHeaderModel()
  {
    throw new RuntimeException("Not yet written");
  }

  /**
   * If this <code>JTable</code> is the <code>viewportView</code> of an
   * enclosing <code>JScrollPane</code> (the usual situation), configure this
   * <code>ScrollPane</code> by, amongst other things, installing the table's
   * <code>tableHeader</code> as the <code>columnHeaderView</code> of the scroll
   * pane. When a <code>JTable</code> is added to a <code>JScrollPane</code> in
   * the usual way, using <code>new JScrollPane(myTable)</code>,
   * <code>addNotify</code> is called in the <code>JTable</code> (when the table
   * is added to the viewport). <code>JTable</code>'s <code>addNotify</code>
   * method in turn calls this method, which is protected so that this default
   * installation procedure can be overridden by a subclass.
   *
   * @see #addNotify
   */
  protected void configureEnclosingScrollPane()
  {
    super.configureEnclosingScrollPane();
    // ToDo I need to create a rowHeader member component, and write some 
    // configuring code to handle the case where the header is specified
    // directly.
    if (mRowHeader != null)
    {
      Container p = getParent();
      if (p instanceof JViewport)
      {
          Container gp = p.getParent();
          if (gp instanceof JScrollPane)
          {
              JScrollPane scr = (JScrollPane)gp;
              TableRowHeader header = new TableRowHeader(mRowHeader);
              scr.setRowHeaderView(header);
              // they share a selection model.
              header.setSelectionModel(getSelectionModel());
              // put in a blank corner component if there isn't one.
              if (scr.getCorner(scr.UPPER_LEFT_CORNER) == null)
              {
                JLabel corner = new JLabel("");
                corner.setBorder(new MatteBorder(0, 0, 1, 1, getGridColor()));
                scr.setCorner(scr.UPPER_LEFT_CORNER, corner);
              }
              if (scr.getCorner(scr.UPPER_RIGHT_CORNER) == null)
              {
                JLabel corner = new JLabel("");
                corner.setBorder(new MatteBorder(0, 1, 1, 0, getGridColor()));
                scr.setCorner(scr.UPPER_RIGHT_CORNER, corner);
              }
              // The change listener keeps the two viewports in sync. The JScrollPane 
              // already makes sure the header will scroll to match the main viewport.
              // This listener makes sure the main viewport will scroll to match the 
              // row header.
              // There's always the possibility for an infinite loop with this design
              // pattern. But this listener only scrolls the view if they don't match.
              // So once they match, the process will end.
              ChangeListener
              cl = new ChangeListener()
              {
                public void stateChanged(ChangeEvent e)
                {
                  JViewport master = (JViewport) e.getSource();
                  JViewport slave = ((JScrollPane)master.getParent()).getViewport();
                  final Point mstrVp=master.getViewPosition();
                  final Point slvVp=slave.getViewPosition();
                  int msV = mstrVp.y;
                  int slV = slvVp.y;
                  if (msV != slV)
                    slave.setViewPosition(new Point(slvVp.x, msV));
                }
              };
              scr.getRowHeader().addChangeListener(cl);
          }
      }
    }
  }

  public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
  {
    Component rndr = super.prepareRenderer(renderer, row, column);
    if (!isCellEditable(row, column))
    {
      // Since the DefaultTableCellRenderer uses its own strange getBackground()
      // method, we need to handle that case separately.
      if (rndr instanceof DefaultTableCellRenderer)
      {
        if (isCellSelected(row, column))
          rndr.setBackground(ashen(getSelectionBackground()));
        else
          rndr.setBackground(ashen(JJTable.this.getBackground()));
      }
      else
      {
        rndr.setBackground(ashen(rndr.getBackground()));
      }
    }
    return rndr;
  }

  public static final Color ashen(Color inp)
  {
    int red = inp.getRed();
    int green = inp.getGreen();
    int blue = inp.getBlue();
    int alpha=inp.getAlpha();
    return new Color((int)(red*ash), (int)(green*ash), (int)(blue*ash), alpha);
  }

  /**
   * Prepares the editor by querying the data model for the value and selection
   * state of the cell at <code>row</code>, <code>column</code>.
   * <p/>
   * This class works around bug 4478121, where descenders get cut off at the
   * bottom of the cell editor. For some reason, the editor paints the text
   * one cell farther down than the renderer. This doesn't leave enough room
   * for the last pixel row of the descenders. This fix is to use a custom
   *  border.
   *
   * @param editor the <code>TableCellEditor</code> to set up
   * @param row    the row of the cell to edit, where 0 is the first row
   * @param column the column of the cell to edit, where 0 is the first column
   *
   * @return the <code>Component</code> being edited
   */
  public Component prepareEditor(TableCellEditor editor, int row, int column)
  {
    final JComponent cmp=(JComponent)super.prepareEditor(editor, row, column);
    cmp.setBorder(sFixBorder);
    return cmp;
  }
  /**
   * Class to work around bug 4478121, where descenders get cut off at the
   * bottom of the cell editor. For some reason, the editor paints the text
   * one cell farther down than the renderer. This doesn't leave enough room
   * for the last pixel row of the descenders. The workaround is this custom 
   * border. Normally, the editor component has a line border, with insets of
   * one pixel all around. While the top inset isn't what pushes the text 
   * down I can bring it back up by reducing the inset's top to zero pixels.
   * However, the editor then won't draw the top line of the border. So 
   * This custom border has a top inset of zero, but still draws a line at 
   * the top.  
   */ 
  private static class EditorFixBorder extends MatteBorder
  {
    EditorFixBorder()
    {
      super(0,1,1,1, Color.black);
    }
    
    /**
     * This paint method is where it redraws the line at the top, in spite of
     * it being outside the inset space.
     */ 
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
      super.paintBorder(c, g, x, y, width, height);
      Insets insets = getBorderInsets(c);
      Color oldColor = g.getColor();
      g.translate(x,y);
      g.setColor(color);
      // I'm hoping the line width is one here. For efficiency, I don't want to
      // save and restore the previous value. If this doesn't work, Remove this
      // line and use the fillRect call below it. That's guaranteed to work, 
      // but I'm not sure if it's as fast as drawLine. (It may be.)
      g.drawLine(0, 0, width-insets.right, 0 ); 
//      g.fillRect(0, 0, width - insets.right, 1);
      g.translate(-x,-y);
      g.setColor(oldColor);
    }
  }
}

package com.mm.gui.table;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

/**
 * Created by IntelliJ IDEA.
 * User: Miguel Mu&ntilde;oz (SwingGuy1024@yahoo.com)
 * Date: Jul 17, 2005
 * Time: 2:50:40 AM
 * <p/>
 * It drives me crazy that LimeWire's JTables use AUTO_RESIZE_NEXT_COLUMN. But
 * rather than trying to persuade you to choose a different mode, I wrote
 * this class to throw that decision into the hands of the end user.
 * <p/>
 * Add this MouseListener to any JTableHeader, and the user will be able to
 * choose the resize mode by holding down the appropriate modifier keys.Guil
 * With no modifiers, this class leaves the behavior of LimeWire's resize mode
 * unchanged. The shift, control, and alt keys each give the user a different
 * resize mode. Multiple modifiers have no effect -- shift overrides control,
 * which overrides Alt.
 * <p/>
 * The default setting is as follows: <br>
 * &nbsp;&nbsp; No modifiers:   AUTO_RESIZE_NEXT_COLUMN <br>
 * &nbsp;&nbsp; Shift key: AUTO_RESIZE_LAST_COLUMN <br>
 * &nbsp;&nbsp; Control key: AUTO_RESIZE_SUBSEQUENT_COLUMNS <br>
 * &nbsp;&nbsp; Alt key: AUTO_RESIZE_ALL_COLUMNS <br>
 * You may choose a different order by using the other constructor.
 */
public class FlexibleColumnResizeAdapter extends MouseAdapter {
    int dMode;
    int sMode;
    int cMode;
    int aMode;

    /**
     * Create a FlexibleColumnResizeAdapter with the default settings, as
     * described above
     */
    public FlexibleColumnResizeAdapter() {
        this(JTable.AUTO_RESIZE_NEXT_COLUMN,
                JTable.AUTO_RESIZE_LAST_COLUMN,
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS,
                JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    /**
     * Create a FlexibleColumnResizeAdapter with the specified settings.
     * When multiple modifiers are pressed, only one is processed. The
     * priority matches the order of these parameters.
     * @param defaultMode   The resize mode when no modifier keys are pressed
     * @param shiftMode     The resize mode when the shift key is pressed
     * @param controlMode   The resize mode when the control key is pressed
     * @param altMode       The resize mode when the alt key is pressed
     */
    public FlexibleColumnResizeAdapter(int defaultMode, int shiftMode,
                                       int controlMode, int altMode) {
        dMode = defaultMode;
        sMode = shiftMode;
        cMode = controlMode;
        aMode = altMode;
    }

    /**
     * Sets the JTable's auto-resize mode depending on which modifier key is
     * pressed.
     * @param e
     */
    public void mousePressed(MouseEvent e) {
        JTableHeader hdr = (JTableHeader) e.getSource();
        int mask = e.getModifiers();
        JTable tbl = hdr.getTable();
        if ((mask & MouseEvent.SHIFT_MASK) != 0)
            tbl.setAutoResizeMode(sMode);
        else if ((mask & MouseEvent.CTRL_MASK) != 0)
            tbl.setAutoResizeMode(cMode);
        else if ((mask & MouseEvent.ALT_MASK) != 0)
            tbl.setAutoResizeMode(aMode);
        else
            tbl.setAutoResizeMode(dMode);
    }
}

/*
  Copyright (c) 2000 Goto.com

  $RCSfile: SelectCaret.java,v $
  $Author: Miguel $
  $Date: 2005/05/31 07:25:31 $
  $Revision: 1.1 $
  @Author Miguel Munoz <miguel@goto.com>

  (Change Log is at the end of the file.)
*/

package com.mm.gui;

import java.awt.event.FocusEvent;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

/**
 * Under JDK 1.3, a JTextComponent will hide the selection when
 * it loses the focus. This prevents us from showing the selected
 * word when a dialog (like a spelling checker) is visible. This
 * class overrides that behavior and keeps the selection visible.
 * This class also solves the problem of the missing caret when
 * editing cells in a JTable. Turn on the isInTable property to 
 * use this in a JTable.
 */
@SuppressWarnings("unused")
public class SelectCaret extends DefaultCaret
{
  private boolean myIsInTable = false;
  SelectCaret()
  {
    super();
  }

  SelectCaret(boolean isInTable)
  {
    super();
    myIsInTable = isInTable;
  }

  @Override
  public void focusLost(FocusEvent e) {
	  // The default implementation calls setVisible(false) and setSelectionVisible(false)
	  // This one just sets visible false. But this doesn't work anymore. It worked fine 
	  // in 1.3 and (I think) 1.4. I don't know if it worked in 1.5 but it fails in 1.6 and
	  // I don't know why.
    if (!myIsInTable) {
      setVisible(false);
    }
  }
  
  public boolean isInTable() { return myIsInTable; }
  public void setInTable(boolean inTable) { myIsInTable = inTable; }

  /**
   * Since the caret and selection aren't visible by default,
   * we need to turn them on when the caret is installed.
   */
  @Override
  public void install(JTextComponent tc)
  {
    int blinkRate = tc.getCaret().getBlinkRate();
    super.install(tc);

    setBlinkRate(blinkRate);// set the blinkRate before setVisible...
    if (tc.hasFocus())
    {
      setVisible(true);         // ...or it won't blink the first time.
    }
    setSelectionVisible(true);
  }
	
	public static void installCaret(JTextComponent tc) {
		SelectCaret newCaret = new SelectCaret();
		newCaret.install(tc);
	}
}

/*
  $Log: SelectCaret.java,v $
  Revision 1.1  2005/05/31 07:25:31  Miguel
  First attempt to create a CVS Module

  Revision 1.3  2001/07/19 09:32:06  miguel
  Fixed always-showing caret.

  Revision 1.2  2001/07/17 23:09:37  miguel
  Merged with BLOB-1, again.

  Revision 1.1.2.1  2001/07/17 05:00:35  miguel
  Initial Release.

*/

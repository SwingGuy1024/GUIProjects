package com.mm.gui.undo;

import javax.swing.undo.UndoableEdit;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 3/22/26
 * <br>Time: 11:15 AM
 * <br>@author Miguel Muñoz</p>
 */
public interface CombinableEdit extends UndoableEdit {
  default boolean isCombinable() { return true; }
  
  default boolean isCombinableWith(UndoableEdit otherEdit) {
    return (getClass() == otherEdit.getClass());
  }

  /**
   * Combines this CombinableEdit in place with laterEdit. At the end of this method, this edit should now combine the
   * details of both edits. This edit will remain the most recent edit, and the new edit will go away.
   * @param laterEdit The new edit
   */
  void combineWith(CombinableEdit laterEdit);

  static boolean isCombinable(UndoableEdit edit) { return (edit instanceof CombinableEdit ce) && ce.isCombinable(); }
  
  static boolean isPairCombinable(CombinableEdit priorEdit, UndoableEdit laterEdit) {
    if (isCombinable(priorEdit)) {
      return priorEdit.isCombinableWith(laterEdit);
    }
    return false;
  }
}

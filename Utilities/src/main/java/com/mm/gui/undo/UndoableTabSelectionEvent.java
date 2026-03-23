package com.mm.gui.undo;

import javax.swing.event.UndoableEditEvent;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 3/19/26
 * <br>Time: 12:57 AM
 * <br>@author Miguel Muñoz</p>
 */
public class UndoableTabSelectionEvent extends UndoableEditEvent {
  
  public UndoableTabSelectionEvent(Object source, UndoableTabChangeEdit edit) {
    super(source, edit);
  }
  
  public int getPriorTabIndex() { return ((UndoableTabChangeEdit) getEdit()).getPriorTab(); }
  public int getNewTabIndex() { return ((UndoableTabChangeEdit) getEdit()).getNewTab(); }
}

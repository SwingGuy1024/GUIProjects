package com.mm.gui.undo;

import java.util.LinkedList;
import java.util.List;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditListener;

import org.intellij.lang.annotations.MagicConstant;

/**
 * <p>A JTabbedPane that treats changes to the selection as undoable edits. It does not treat adding new tabs as
 * undoable. It's intended for JTabbedPanes that don't change as the user works. Subclasses may override this
 * behavior.</p>
 * <p>When setting up a tabbed pane, it's advisable to add the UndoableEditListener after adding all the tabs.</p>
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 3/19/26
 * <br>Time: 4:25 PM
 * <br>@author Miguel Muñoz</p>
 */
public class UndoableTabbedPane extends JTabbedPane {
  
  private final List<UndoableEditListener> undoableEditListenerList = new LinkedList<>();
  
  private int priorIndex = -1;
  
  private boolean undoOrRedoInNotProgress = true;

  public UndoableTabbedPane() {
    super();
    build();
  }
  
  public UndoableTabbedPane(
      @MagicConstant(intValues = { SwingConstants.TOP, SwingConstants.BOTTOM, SwingConstants.LEFT, SwingConstants.RIGHT})
      int tabPlacement) {
    super(tabPlacement);
    build();
  }

  public UndoableTabbedPane(
      @MagicConstant(intValues = {SwingConstants.TOP, SwingConstants.BOTTOM, SwingConstants.LEFT, SwingConstants.RIGHT})
      int tabPlacement,
      @MagicConstant(intValues = {JTabbedPane.WRAP_TAB_LAYOUT, JTabbedPane.SCROLL_TAB_LAYOUT})
      int tabLayoutPolicy) {
    super(tabPlacement, tabLayoutPolicy);
    build();
  }
  
  public void addUndoableEditListener(UndoableEditListener listener) {
    undoableEditListenerList.add(listener);
  }
  
  void setUndoOrRedoInProgress(boolean undoOrRedoInProgress) {
    this.undoOrRedoInNotProgress = !undoOrRedoInProgress;
  }
  
  private void fireUndoableEditEvent(UndoableTabSelectionEvent event) {
    for (UndoableEditListener undoableEditListener : undoableEditListenerList) {
      undoableEditListener.undoableEditHappened(event);
    }
  }
  
  private void build() {
    // Model is the SingleSelectionModel
    model.addChangeListener(makeUndoableChangeListener());
  }

  private ChangeListener makeUndoableChangeListener() {
    return (ignored) -> {
      final int newTabIndex = this.getSelectedIndex();
      if (undoOrRedoInNotProgress) {
        UndoableTabChangeEdit edit = new UndoableTabChangeEdit(this, priorIndex, newTabIndex);
        UndoableTabSelectionEvent event = new UndoableTabSelectionEvent(this, edit);
        fireUndoableEditEvent(event);
      }
      priorIndex = newTabIndex;
    };
  }
}

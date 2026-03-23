package com.mm.gui.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 3/19/26
 * <br>Time: 5:00 PM
 * <br>@author Miguel Muñoz</p>
 */
public class UndoableTabChangeEdit extends AbstractUndoableEdit implements CombinableEdit {
//  protected static final String UndoName = "Undo Tab Change";
//  protected static final String RedoName = "Redo Tab Change";

  private final UndoableTabbedPane source;
  private final int priorTab;
  private int newTab;

//  private boolean undoOrRedoInProgress = false;

  UndoableTabChangeEdit(UndoableTabbedPane source, int priorTab, int newTab) {
    super();
    this.source = source;
    this.priorTab = priorTab;
    this.newTab = newTab;
  }
  
  public int getPriorTab() { return priorTab; }
  public int getNewTab() { return newTab; }
  private void setNewTab(int newTab) {
    this.newTab = newTab;
  }

  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    source.setUndoOrRedoInProgress(true);
    source.setSelectedIndex(priorTab);
    source.setUndoOrRedoInProgress(false);
  }

  @Override
  public void redo() throws CannotRedoException {
    super.redo();
    source.setUndoOrRedoInProgress(true);
    source.setSelectedIndex(newTab);
    source.setUndoOrRedoInProgress(false);
  }

  @Override
  public void combineWith(CombinableEdit laterEdit) {
    UndoableTabChangeEdit newEdit = (UndoableTabChangeEdit) laterEdit;
    setNewTab(newEdit.getNewTab());
  }

  //  public boolean isInProgress() { return undoOrRedoInProgress; }

  @Override
  public String getPresentationName() {
    return String.format("Tab Change from %s to %s", source.getTitleAt(priorTab), source.getTitleAt(newTab));
  }
}

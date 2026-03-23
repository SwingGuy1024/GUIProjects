package com.mm.gui.undo;

import java.awt.Component;
import javax.swing.undo.AbstractUndoableEdit;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 3/20/26
 * <br>Time: 10:59 AM
 * <br>@author Miguel Muñoz</p>
 */
public class UndoableFocusChangeEdit extends AbstractUndoableEdit {
//  public static final String  UndoName = "Undo Focus Change";
//  public static final String RedoName = "Redo Focus Change";

  private final UndoFramework source;
  private final Component focussedComponent;
  public UndoableFocusChangeEdit(UndoFramework source, Component focusComponent) {
    super();
    this.source = source;
    this.focussedComponent = focusComponent;
  }

  public Component getFocussedComponent() {
    return focussedComponent;
  }

  public UndoFramework getSource() {
    return source;
  }

  @Override
  public boolean isSignificant() {
    return false;
  }

  @Override
  public void undo() {
    super.undo();
    if (focussedComponent != null) {
      focussedComponent.requestFocus();
    }
  }
  
  @Override
  public void redo() {
    super.redo();
    if (focussedComponent != null) {
      focussedComponent.requestFocus();
    }
  }

  @Override
  public String getPresentationName() {
    return "Focus Restore";
  }
}

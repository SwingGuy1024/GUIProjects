package com.mm.gui.undo;

import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeListener;
import javax.swing.text.JTextComponent;

/**
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 3/22/26
 * <br>Time: 11:11 PM
 * <br>@author Miguel Muñoz</p>
 */
class FocusWatch {
  public static final String PERMANENT_FOCUS_OWNER = "permanentFocusOwner";

  private final UndoFramework undoFramework;
  private JTextComponent lastTextComponent;
  private JTextComponent currentTextComponent;

  FocusWatch(UndoFramework undoFramework) {
    this.undoFramework = undoFramework;
    installPermanentFocusListener();
  }

  private void installPermanentFocusListener() {
    PropertyChangeListener pcl = evt -> {
      Component value = (Component) evt.getNewValue();
      if (value instanceof JTextComponent textComponent) {
        currentTextComponent = textComponent;
      }
    };

    final KeyboardFocusManager focusManager = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager();
    focusManager.addPropertyChangeListener(PERMANENT_FOCUS_OWNER, pcl);
  }
  
  void fireOnFocusChange() {
    if (currentTextComponent != null) {
      //noinspection ObjectEquality
      if (lastTextComponent != currentTextComponent) {
        UndoableFocusChangeEdit focusEdit = new UndoableFocusChangeEdit(undoFramework, currentTextComponent);
        undoFramework.packageEdits();
        undoFramework.addEditInternal(focusEdit); // Prevents an infinite recursion! 
        lastTextComponent = currentTextComponent;
      }
    }
  }
  
  void fireAfterPackaging() {
    //noinspection ObjectEquality
    if (currentTextComponent == lastTextComponent) {
      System.out.printf("Closing focus event%n"); // NON-NLS
      UndoableFocusChangeEdit focusEdit = new UndoableFocusChangeEdit(undoFramework, currentTextComponent);
      undoFramework.addEditInternal(focusEdit);
    }
  }
  
  public static String className(Object object) {
    String name = (object == null) ? "null" : object.getClass().getSimpleName();
    if (object == null) {
      return name;
    }
    return String.format("%s@%d", name, object.hashCode());
  }
}

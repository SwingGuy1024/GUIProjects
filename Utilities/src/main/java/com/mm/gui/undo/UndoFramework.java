package com.mm.gui.undo;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import static com.mm.gui.undo.CombinableEdit.isCombinable;

/**
 * Todo: Fix Focussed Field Recording. When multiple changes occur in one field, the focus gets restored on redo but 
 * todo    not on undo. Save the focus event at both ends.
 * Todo: Add Undo to the table editing. This may require an UndoableTable class.
 * recording the current focussed component.
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 3/16/26
 * <br>Time: 2:14 AM
 * <br>@author Miguel Muñoz</p>
 */
public final class UndoFramework {
  // This constant should be defined in class DefaultKeyboardFocusManager, but it's not.
  private final UndoManager undoManager = new UndoManager();

  private CompoundEdit tempUndoManager = new CompoundEdit();
  private UndoableEdit lastEdit = null;
  private final FocusWatch focusWatch = new FocusWatch(this);

  // We watch changes to the Caret to detect when the user changes the location of the Caret or selects
  // a new field. This is when we coalesce edits into a single edit, and switch to a new CompoundEdit. But
  // we need turn this watching off on two occasions: When the user is typing, and when the user is using 
  // undo or redo. Those operations will also change the caret, but we don't want to respond to them. So we
  // use the watchCaretChanges variable to temporarily disable watching for caret changes.
  private boolean watchCaretChanges = true;

  private CompoundEdit getActiveEdit() {
    if (tempUndoManager.isInProgress()) {
      return tempUndoManager;
    } else {
      return undoManager;
    }
  }
  
//  private final List<UndoableEditListener> undoableFocusChangeEventListeners = new LinkedList<>();

  private final DocumentFilter documentFilter = new DocumentFilter() {
    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
      watchCaretChanges = false;
      super.remove(fb, offset, length);
      watchCaretChanges = true;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
      watchCaretChanges = false;
      super.insertString(fb, offset, string, attr);
      watchCaretChanges = true;
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
      watchCaretChanges = false;
      super.replace(fb, offset, length, text, attrs);
      watchCaretChanges = true;
    }
  };

  private final Action undoAction = new AbstractAction("Undo") {
    @Override
    public void actionPerformed(ActionEvent e) {
      watchCaretChanges = false;
      packageEdits();
      undoManager.undo();
      watchCaretChanges = true;
      processActions();
    }
  };
  private final Action redoAction = new AbstractAction("Redo") {
    @Override
    public void actionPerformed(ActionEvent e) {
      watchCaretChanges = false;
      if (undoManager.canRedo()) {
        undoManager.redo();
      }
      watchCaretChanges = true;
      processActions();
    }
  };

  public UndoFramework() {
    undoAction.setEnabled(false);
    redoAction.setEnabled(false);
  }

  private void processActions() {
    undoAction.setEnabled(undoManager.canUndo());
    redoAction.setEnabled(undoManager.canRedo());
  }

  public void addEdit(UndoableEdit edit) {
    final boolean combinable = isCombinable(edit);
    if (combinable && isCombinable(lastEdit)) {
      CombinableEdit priorEdit = (CombinableEdit) lastEdit;
      if (priorEdit.isCombinableWith(edit)) {
        priorEdit.combineWith((CombinableEdit) edit);
        // don't add the edit.
        processActions();
        return;
      }
    } else {
      if (combinable) {
        // Combinable has not yet been added.
        packageEdits(); // This avoids packaging a Combinable edit with other types, when it's created.
      }
    }
    focusWatch.fireOnFocusChange(); // Saves the focus into the CompoundEdit if it has changed
    
    // I don't understand why I don't need to package a second time, when lastEdit is combinable but this edit isn't.
    // The packageEdits() method will packed if the last edit is combinable, but I'm not sure how that method gets
    // called at the right time.
    getActiveEdit().addEdit(edit);
    lastEdit = edit;
    processActions();
  }

  /**
   * Package-level method used by FocusWatch to prevent an infinite recursion.
   * @param edit The UndoableFocusChangeEdit
   */
  void addEditInternal(UndoableFocusChangeEdit edit) {
    getActiveEdit().addEdit(edit);
  }

  /**
   * Installs an UndoableEditListener in all descendents of the specified parent component that extend JTextComponent,
   * excluding any specified JTextComponents. This also adds caret listeners and a DocumentFilter that enables
   * coalescing multiple edits into a single edit when the user moves the caret or selects text.
   * @param parent The parent component, with descendents that need an undoableEditListener installed
   * @param exclude The JTextComponents to exclude from this operation.
   */
  public void installInChildren(JComponent parent, JTextComponent... exclude) {
//    Object lock = parent.getTreeLock();
    
    // Turn the array into a set, and call the recursive method that does the work.
    Set<Component> excludeSet = Arrays
        .stream(exclude)
        .collect(Collectors.toUnmodifiableSet());
    installInChildrenInternal(parent, excludeSet); // Recursive
  }
  
  public Action getUndoAction() { return undoAction; }
  public Action getRedoAction() { return redoAction; }
  
  private void installInChildrenInternal(Container parent, Set<Component> excludeSet) {
    Component[] children = parent.getComponents();
    for (Component child : children) {
      if (!excludeSet.contains(child)) {
        if (child instanceof JTextComponent tc) {
          setUpTextComponent(tc);  // Does not recurse
        } else if (child instanceof Container cntr) {
          process(cntr, excludeSet); // recurses back to this method
        }
      }
    }
  }
  
  private void setUpTextComponent(JTextComponent textComponent) {
    if (textComponent.isEditable()) {
      final PlainDocument document = (PlainDocument) textComponent.getDocument();
      document.addUndoableEditListener(e -> addEdit(e.getEdit()));

      document.setDocumentFilter(documentFilter);
      textComponent.addCaretListener(caretEvent -> {
            if (watchCaretChanges) {
              packageEdits();
            }
          }
      );
    }
  }

  /**
   *  <p>Package the edits in a tempUndoManager into a single edit. We do this under these circumstances:</p>
   *  <ol>
   *    <li>When the caret changes position (excluding when typing or undo/redoing). In other words, When the 
   *    caret moves due to a user interaction, such as clicking or typing an arrow key.</li>
   *    <li>When the focus moves to a new field</li>
   *    <li>When the user chooses a new tab in an UndoableTabbedPane. This could also be described as when
   *    a series of CombinableEdits occurs, since that's the kind of edits fired by the UndoableTabbedPane. </li>
   *    <li>When the user fires an undo event.</li>
   *  </ol>
   */
  void packageEdits() {
    boolean isCombinable = isCombinable(lastEdit);
    if (isCombinable || (tempUndoManager.isInProgress() && tempUndoManager.isSignificant())) {
      lastEdit = tempUndoManager;
      tempUndoManager.end();
      undoManager.addEdit(tempUndoManager);
      tempUndoManager = new CompoundEdit();
      focusWatch.fireAfterPackaging();
    }
  }
  

  private void process(Container component, Set<Component> excludeSet) {
    installInChildrenInternal(component, excludeSet);
  }
  
}

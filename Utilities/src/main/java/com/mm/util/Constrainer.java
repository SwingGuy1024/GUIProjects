package com.mm.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Range;

/**
 * <p>This is a much-improved version of GridBagConstraints, which is its base class.</p>
 * <p>This class adds no new fields, but adds several new methods which may be chained. It also rewrites the
 * {@code clone()} method to make that chainable too. And it changes one default value. The fill member now 
 * defaults to BOTH instead of NONE, which is much more useful.</p>
 * <p>Here is an example to show the method chaining in action. With GridBagConstraints, I would write code like this:</p>
 * <pre>
 *   import GridBagConstraints;
 *   import static GridBagConstraints.*
 *   ...
 *   JPanel panel = new JPanel(new GridBagLayout());
 *   GridBagConstraints c = new GridBagConstraints();
 *   c.anchor = LINE_START;
 *   c.fill = BOTH
 *   panel.add(versionLabel, c);
 *
 *   c.gridy = 1;
 *   c.anchor = CENTER;
 *   c.weightx = 1.0;
 *   panel.add(new JLabel("Find:"), c);
 *
 *   cGrid = (GridBagConstraints) c.clone()
 *   cGrid.gridx = 1;
 *   cGrid.gridwidth = 3;
 *   panel.add(new JTextField(15), cGrid);
 *   // ...
 * </pre>
 *
 * <p>With The Constrainer Class, it would look like this:</p>
 * <pre>
 *   import Constrainer;
 *   import static GridBagConstraints.*
 *   ...
 *   JPanel panel = new JPanel(new GridBagLayout());
 *   Constrainer c = new Constrainer();
 *   panel.add(versionLabel, c.anchor(LINE_START));
 *   panel.add(new JLabel("Find:"), c.gridy(1).anchor(CENTER).weightx(1.0));
 *   panel.add(new JTextField(15), c.clone().gridx(1).gridWidth(3));
 *   // ...
 * </pre>
 * <p>It also provides four optional two-dimensional methods, to set both dimensions at once. So these…</p>
 * <p>{@code gridx(5).gridy(6)} can be written as {@code at(5, 6)}</p>
 * <p>{@code ipadx(5).ipady(6)} can be written as {@code pad(5, 6)}</p>
 * <p>{@code weightX(5.0).weightY(6.0)} can be written as {@code weight(5.0, 6.0)}</p>
 * <p>{@code gridWidth(5).gridHeight(6)} can be written as {@code gridSize(5, 6)}</p>
 *
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 2/3/25
 * <br>Time: 4:59 PM
 * <br>@author Miguel Muñoz</p>
 */
@SuppressWarnings("unused")
public class Constrainer extends GridBagConstraints {

  /**
   * Construct a constrainer with a fill value of BOTH.
   */
  public Constrainer() {
    super();
    //noinspection AssignmentToSuperclassField
    super.fill = GridBagConstraints.BOTH;
  }

  /**
   * <p>Copy Constructor. Use this to make a copy of another instance of Constrainer or GridBagConstraints.</p>
   *
   * @param model The original Constrainer to copy.
   */
  public Constrainer(final GridBagConstraints model) {
    this();
    this.at(model.gridx, model.gridy)
        .anchor(model.anchor)
        .fill(model.fill)
        .gridSize(model.gridwidth, model.gridheight)
        .weight(model.weightx, model.weighty)
        .insets(model.insets)
        .pad(model.ipadx, model.ipady)
    ;
  }

  @SuppressWarnings("UseOfClone")
  @Override
  public Constrainer clone() {
    return (Constrainer) super.clone();
  }

  /**
   * Sets the {@code gridx} and {@code gridy} properties to x and y, respectively.
   *
   * @param x the new {@code gridx} value
   * @param y the new {@code gridy} value
   * @return this, for method chaining
   * @see #gridx(int)
   * @see #gridy(int)
   */
  public Constrainer at(int x, int y) {
    gridx = x;
    gridy = y;
    return this;
  }

  /**
   * Sets {@code gridx} to the parameter
   *
   * @param x the new {@code gridx}
   * @return this, for method chaining
   * @see #at(int, int)
   */
  public Constrainer gridx(int x) {
    gridx = x;
    return this;
  }

  /**
   * Sets {@code gridy} to the parameter
   *
   * @param y the new {@code gridy}
   * @return this, for method chaining
   * @see #at(int, int)
   */
  public Constrainer gridy(int y) {
    gridy = y;
    return this;
  }

  /**
   * Sets gridwidth and gridheight to x and y
   *
   * @param x The new gridwidth
   * @param y The new gridheight
   * @return this, for method chaining 
   * @see #gridWidth(int)
   * @see #gridHeight(int) 
   */
  public Constrainer gridSize(int x, int y) {
    gridwidth = x;
    gridheight = y;
    return this;
  }

  /**
   * Sets gridwidth to w
   *
   * @param w the new gridwidth
   * @return this, for method chaining
   * @see #gridSize(int, int) 
   */
  public Constrainer gridWidth(int w) { gridwidth = w; return this; }

  /**
   * Sets gridheight to h
   *
   * @param h the new gridheight
   * @return this, for method chaining
   * @see #gridSize(int, int) 
   */
  public Constrainer gridHeight(int h) { gridheight = h; return this; }

  /**
   * Sets the weightx and weighty values
   *
   * @param weightX the new weightx
   * @param weightY the new weighty
   * @return this, for method chaining
   * @see #weightX(double) 
   * @see #weightY(double) 
   */
  public Constrainer weight(double weightX, double weightY) {
    this.weightx = weightX;
    this.weighty = weightY;
    return this;
  }

  /**
   * Sets the weightx value
   * 
   * @param weightX the new weightx value
   * @return this, for method chaining
   * @see #weight(double, double) 
   */
  public Constrainer weightX(double weightX) { weightx = weightX; return this; }

  /**
   * Sets the new weighty value
   *
   * @param weightY the new weighty value
   * @return this, for method chaining
   * @see #weight(double, double) 
   */
  public Constrainer weightY(double weightY) { weighty = weightY; return this; }

  /**
   * Sets the new {@code anchor} value
   *
   * @param anchor The new {@code anchor} value
   * @return this, for method chaining
   */
  @Range(from = GridBagConstraints.CENTER, to = GridBagConstraints.BELOW_BASELINE_TRAILING)
  @MagicConstant(valuesFromClass = GridBagConstraints.class)
  public Constrainer anchor(int anchor) {
    this.anchor = anchor;
    return this;
  }

  /**
   * Sets the new fill value
   *
   * @param fill The new fill value
   * @return this, for method chaining
   */
  @Range(from = GridBagConstraints.NONE, to = GridBagConstraints.VERTICAL)
  @MagicConstant(valuesFromClass = GridBagConstraints.class)
  public Constrainer fill(int fill) {
    this.fill = fill;
    return this;
  }

  /**
   * Sets the new insets
   *
   * @param top    The new top value
   * @param left   the new left value
   * @param bottom the new bottom value
   * @param right  the new right value
   * @return this, for method chaining
   */
  public Constrainer insets(int top, int left, int bottom, int right) {
    this.insets = new Insets(top, left, bottom, right);
    return this;
  }

  /**
   * Sets the new insets to a copy of the specified insets
   *
   * @param insets The insets to set
   * @return this, for method chaining
   */
  @SuppressWarnings("UseOfClone")
  public Constrainer insets(Insets insets) {
    this.insets = (Insets) insets.clone();
    return this;
  }

  /**
   * Sets the insets to a new Insets instance with all four sides set to the same value.
   *
   * @param allSides The insets for all four sides
   * @return this, for method chaining
   */
  public Constrainer insets(int allSides) {
    this.insets = new Insets(allSides, allSides, allSides, allSides);
    return this;
  }

  /**
   * Sets the ipadx and ipady values
   *
   * @param padX the new ipadx value
   * @param padY the new ipady value
   * @return this, for method chaining
   * @see #iPadX(int) 
   * @see #iPadY(int) 
   */
  public Constrainer pad(int padX, int padY) {
    ipadx = padX;
    ipady = padY;
    return this;
  }

  /**
   * Sets the ipadx value
   *
   * @param padX the new ipadx value
   * @return this, for method chaining
   * @see #pad(int, int) 
   */
  public Constrainer iPadX(int padX) { ipadx = padX; return this; }

  /**
   * Sets the ipady value
   *
   * @param padY the new ipady value
   * @return this, for method chaining
   * @see #pad(int, int) 
   */
  public Constrainer iPadY(int padY) { ipady = padY; return this; }

  /**
   * Sets the {@code gridx} value to {@code RELATIVE}, indicating that the component should be placed next to the
   * previously added component
   *
   * @return this, for method chaining
   * @see GridBagConstraints#RELATIVE
   * @see GridBagConstraints*gridx
   */
  public Constrainer relativeX() {
    gridx = RELATIVE;
    return this;
  }

  /**
   * Sets the {@code gridy} value to {@code RELATIVE}, indicating that the component should be placed next to the
   * previously added component
   *
   * @return this, for method chaining
   * @see GridBagConstraints#RELATIVE
   * @see GridBagConstraints*gridy
   */
  public Constrainer relativeY() {
    gridy = RELATIVE;
    return this;
  }

  /**
   * Sets the gridwidth value to {@code RELATIVE}, indicating that the component is the next-to-last component in its
   * column.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#RELATIVE
   * @see GridBagConstraints*gridWidth
   */
  public Constrainer relativeWidth() {
    gridwidth = RELATIVE;
    return this;
  }

  /**
   * Sets the gridheight value to {@code RELATIVE}, indicating that the component is the next-to-last component in its
   * row.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#RELATIVE
   * @see GridBagConstraints*gridheight
   */
  public Constrainer relativeHeight() {
    gridheight = RELATIVE;
    return this;
  }

  /**
   * Sets the gridwidth value to {@code REMAINDER}, indicating that the component's display area will be from
   * {@code gridx} to the last cell in the row.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#REMAINDER
   * @see GridBagConstraints*gridWidth
   */
  public Constrainer remainderWidth() {
    gridwidth = REMAINDER;
    return this;
  }

  /**
   * Sets the gridHeight value to {@code REMAINDER}, indicating that the component's display area will be from
   * {@code gridy} to the last cell in the column.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#REMAINDER
   * @see GridBagConstraints*gridHeight
   */
  public Constrainer remainderHeight() {
    gridheight = REMAINDER;
    return this;
  }

  /**
   * Sets the fill value to {@code NONE}: Do not resize the component
   *
   * @return this, for method chaining
   * @see GridBagConstraints#NONE
   * @see GridBagConstraints#fill
   */
  public Constrainer fillNone() {
    fill = GridBagConstraints.NONE;
    return this;
  }

  /**
   * Sets the fill value to {@code HORIZONTAL}: Make the component wide enough to fill its display area horizontally,
   * but do not change its height.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#HORIZONTAL
   * @see GridBagConstraints#fill
   */
  public Constrainer fillHorizontal() {
    fill = GridBagConstraints.HORIZONTAL;
    return this;
  }

  /**
   * Sets the fill value to {@code VERTICAL}: Make the component tall enough to fill its display area vertically,
   * but do not change its width.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#VERTICAL
   * @see GridBagConstraints#fill
   */
  public Constrainer fillVertical() {
    fill = GridBagConstraints.VERTICAL;
    return this;
  }

  /**
   * Sets the fill value to {@code BOTH}: Make the component fill its display area entirely.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#BOTH
   * @see GridBagConstraints#fill
   */
  public Constrainer fillBoth() {
    fill = GridBagConstraints.BOTH;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code CENTER}, which is one of the absolute values for {@code anchor}.
   * @return this, for method chaining
   * @see GridBagConstraints#anchor
   * @see GridBagConstraints#CENTER
   */
  public Constrainer anchorCenter() {
    anchor = CENTER;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code NORTH}, which is one of the absolute values for {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#anchor
   * @see GridBagConstraints#NORTH
   */
  public Constrainer anchorNorth() {
    anchor = NORTH;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code NORTHEAST}, which is one of the absolute values for {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#anchor
   * @see GridBagConstraints#NORTHEAST
   */
  public Constrainer anchorNorthEast() {
    anchor = NORTHEAST;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code EAST}, which is one of the absolute values for {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#anchor
   * @see GridBagConstraints#EAST
   */
  public Constrainer anchorEast() {
    anchor = EAST;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code SOUTHEAST}, which is one of the absolute values for {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#anchor
   * @see GridBagConstraints#SOUTHEAST
   */
  public Constrainer anchorSouthEast() {
    anchor = SOUTHEAST;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code SOUTH}, which is one of the absolute values for {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#anchor
   * @see GridBagConstraints#SOUTH
   */
  public Constrainer anchorSouth() {
    anchor = SOUTH;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code SOUTHWEST}, which is one of the absolute values for {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#anchor
   * @see GridBagConstraints#SOUTHWEST
   */
  public Constrainer anchorSouthWest() {
    anchor = SOUTHWEST;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code WEST}, which is one of the absolute values for {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#anchor
   * @see GridBagConstraints#WEST
   */
  public Constrainer anchorWest() {
    anchor = WEST;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code NORTHWEST}, which is one of the absolute values for {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#anchor
   * @see GridBagConstraints#NORTHWEST
   */
  public Constrainer anchorNorthWest() {
    anchor = NORTHWEST;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code PAGE_START}, which is one of the orientation relative values for
   * {@code anchor}
   * @return this, for method chaining
   * @see GridBagConstraints#PAGE_START
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorPageStart() {
    anchor = PAGE_START;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code PAGE_END}, which is one of the orientation relative values for
   * {@code anchor}
   *
   * @return this, for method chaining
   * @see GridBagConstraints#PAGE_END
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorPageEnd() {
    anchor = PAGE_END;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code LINE_START}, which is one of the orientation relative values for
   * {@code anchor}
   *
   * @return this, for method chaining
   * @see GridBagConstraints#LINE_START
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorLineStart() {
    anchor = LINE_START;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code LINE_END}, which is one of the orientation relative values for
   * {@code anchor}
   *
   * @return this, for method chaining
   * @see GridBagConstraints#LINE_END
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorLineEnd() {
    anchor = LINE_END;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code FIRST_LINE_START}, which is one of the orientation relative values for
   * {@code anchor}
   *
   * @return this, for method chaining
   * @see GridBagConstraints#FIRST_LINE_START
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorFirstLineStart() {
    anchor = FIRST_LINE_START;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code FIRST_LINE_END}, which is one of the orientation relative values for
   * {@code anchor}
   *
   * @return this, for method chaining
   * @see GridBagConstraints#FIRST_LINE_END
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorFirstLineEnd() {
    anchor = FIRST_LINE_END;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code LAST_LINE_START}, which is one of the orientation relative values for
   * {@code anchor}
   *
   * @return this, for method chaining
   * @see GridBagConstraints#LAST_LINE_START
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorLastLineStart() {
    anchor = LAST_LINE_START;
    return this;
  }

  /**
   * Sets the {@code anchor} value to {@code LAST_LINE_END}, which is one of the orientation relative values for
   * {@code anchor}
   *
   * @return this, for method chaining
   * @see GridBagConstraints#LAST_LINE_END
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorLastLineEnd() {
    anchor = LAST_LINE_END;
    return this;
  }

  /**
   * Sets the {@code anchor} field to BASELINE, which is one of the baseline relative values for {@code anchor}.
   * @return this, for method chaining
   * @see GridBagConstraints#BASELINE
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorBaseline() {
    anchor = BASELINE;
    return this;
  }

  /**
   * Sets the {@code anchor} field to BASELINE_LEADING, which is one of the baseline relative values for
   * {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#BASELINE_LEADING
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorBaselineLeading() {
    anchor = BASELINE_LEADING;
    return this;
  }

  /**
   * Sets the {@code anchor} field to BASELINE_TRAILING, which is one of the baseline relative values for
   * {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#BASELINE_TRAILING
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorBaselineTrailing() {
    anchor = BASELINE_TRAILING;
    return this;
  }

  /**
   * Sets the {@code anchor} field to ABOVE_BASELINE, which is one of the baseline relative values for
   * {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#ABOVE_BASELINE
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorAboveBaseline() {
    anchor = ABOVE_BASELINE;
    return this;
  }

  /**
   * Sets the {@code anchor} field to ABOVE_BASELINE_LEADING, which is one of the baseline relative values for
   * {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#ABOVE_BASELINE_LEADING
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorAboveBaselineLeading() {
    anchor = ABOVE_BASELINE_LEADING;
    return this;
  }

  /**
   * Sets the {@code anchor} field to ABOVE_BASELINE_TRAILING, which is one of the baseline relative values for
   * {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#ABOVE_BASELINE_TRAILING
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorAboveBaselineTrailing() {
    anchor = ABOVE_BASELINE_TRAILING;
    return this;
  }

  /**
   * Sets the {@code anchor} field to BELOW_BASELINE, which is one of the baseline relative values for {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#BELOW_BASELINE
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorBelowBaseline() {
    anchor = BELOW_BASELINE;
    return this;
  }

  /**
   * Sets the {@code anchor} field to BELOW_BASELINE_LEADING, which is one of the baseline relative values for
   * {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#BELOW_BASELINE_LEADING
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorBelowBaselineLeading() {
    anchor = BELOW_BASELINE_LEADING;
    return this;
  }

  /**
   * Sets the {@code anchor} field to BELOW_BASELINE_TRAILING, which is one of the baseline relative values for
   * {@code anchor}.
   *
   * @return this, for method chaining
   * @see GridBagConstraints#BELOW_BASELINE_TRAILING
   * @see GridBagConstraints#anchor
   */
  public Constrainer anchorBelowBaselineTrailing() {
    anchor = BELOW_BASELINE_TRAILING;
    return this;
  }
}

package com.mm.gui.table;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Renderer to highlight selected text. This works by wrapping the text inside html tags, and decorating spans that 
 * contain text to be decorated. The choice of text to decorate is determined by an external class that implements
 * the HighlightRangeSource interface. So, for example, to connect to a spell checker, you may write a method that
 * takes examines the text in the field for spelling checkers and returns a List of TextRange instances, each of
 * which has a start index (inclusive) and an end index (exclusive). The renderer calls this method as it renders
 * the cell, and highlights the text ranges that it gets back.</p>
 * <p>The highlightStyle property is a String that would appear in an html style attribute. For example, to
 * set color the highlighted text in magenta, you could call the setter like this:</p>
 * <pre>
 *   HighlightRenderer myRenderer = new HighlightRenderer();
 *   myRenderer.setHighlightStyle("color:red;");
 * </pre>
 * <p>This would produce a html String that would render a misspelled word like this:</p>
 * <pre>{@code   <html>This has a <span style="color:red">mispelled</span> word.</html> }</pre>
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 3/28/24</p>
 * <p>Time: 9:24 AM</p>
 * <p>@author Miguel Muñoz</p>
 */
@SuppressWarnings("unused")
public class HighlightRenderer extends DefaultTableCellRenderer {

  /**
   * <p>Interface to specify a range of text to get highlighted.</p>
   * <p>As with the {@code String.substring(...)} method, start is inclusive,
   * and end is exclusive.</p>
   */
  public interface TextRange extends Comparable<TextRange> {
    int getStart();
    int getEnd();

    /**
     *  Returns true if {@code this} range intersects {@code that} range.
     *  Ranges that touch but don't overlap are not considered to be 
     *  intersecting, so a range from 10 to 20 does not intersect a range from 20 to 30, but does intersect a range
     *  from 19 to 30.
     * @param that The other TextRange 
     * @return true if this intersects that, false otherwise.
     */
    default boolean intersects(TextRange that) {
      // This method assumes that start < end for both ranges.
      if (this.getStart() >= that.getEnd()) {
        return false;
      }
      //noinspection RedundantIfStatement
      if (that.getStart() >= this.getEnd()) {
        return false;
      }
      return true;
    }
  }

  /**
   * <p>Default implementation of TextRange. This class is immutable and sortable.
   * In addition to its constructor that takes start and end values, it
   * also has a static {@code of()} method, that takes a start index and a
   * length.</p>
   */
  public static class TextRangeImpl implements TextRange, Comparable<TextRange> {
    private final int startInclusive;
    private final int endExclusive;
    
    public TextRangeImpl(int startInclusive, int endExclusive) {
      if (startInclusive <0) {
        throw new IllegalArgumentException(String.format("Negative Start: %d", startInclusive));
      }
      if (endExclusive < 0) {
        throw new IllegalArgumentException(String.format("Negative end: %d", endExclusive));
      }
      if (startInclusive == endExclusive) {
        throw new IllegalArgumentException(String.format("Start equals End: %d", startInclusive));
      }
      if (startInclusive > endExclusive) {
        this.startInclusive = endExclusive;
        this.endExclusive = startInclusive;
      } else {
        this.startInclusive = startInclusive;
        this.endExclusive = endExclusive;
      }
    }

    @Override
    public int getStart() {
      return startInclusive;
    }

    @Override
    public int getEnd() {
      return endExclusive;
    }

    @Override
    public int compareTo(@NotNull HighlightRenderer.TextRange that) {
      return Integer.compare(this.getStart(), that.getStart());
    }

    @Override
    public int hashCode() {
      // Adapted from java.awt.Dimension
      int sum = startInclusive + endExclusive;
      return ((sum * (sum + 1)) / 2) + startInclusive;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof TextRange) {
        TextRange that = (TextRange) obj;
        return (this.startInclusive == that.getStart()) && (this.endExclusive == that.getEnd());
      }
      return false;
    }

    @Override
    public String toString() {
      return String.format("(%s <-> %s)", startInclusive, endExclusive);
    }

    public static TextRange of(int start, int width) {
      if (width <= 0) {
        throw new IllegalArgumentException(String.format("Negative or zero width: %d", width));
      }
      return new TextRangeImpl(start, start + width);
    }
  }

  /**
   * <p>This interfaces to whatever external systems will determine what text gets highlighted.</p>
   * <p>Implementing classes must meet these conditions.</p>
   * <p>1. Ranges must not overlap.</p>
   * <p>2. Ranges must be sorted in ascending order</p>
   */
  @FunctionalInterface
  public interface HighlightRangeSource {
    /**
7     * <p>Return an ordered Collection of non-overlapping {@code TextRange} instances, in
     * increasing order, specifying the text that should get highlighted.</p>
     * @param text The text to inspect for possible highlights
     * @return An ordered array, in increasing order, of non-overlapping text
     * ranges to highlight.
     */
    Collection<TextRange> getOrderedTextRanges(String text);
  }
  
  
  private @Nullable HighlightRangeSource highlightRangeSource;
  private String highlightStyle; // example: color:red;

  /**
   * <p>Instantiate a HighlightRenderer with no highlighting mechanism
   * specified. The {@code HighlightRangeSource} object may be set later.
   * The highlight style defaults to {@code color:red;}</p>
   */
  public HighlightRenderer() {
    this(null, "color:red;");
  }
  
  public HighlightRenderer(String highlightStyle) {
    super();
    this.highlightStyle = highlightStyle;
  }
  
  public HighlightRenderer(HighlightMode highlightMode) {
    super();
    this.highlightStyle = highlightMode.getStyle();
  }
  
  public HighlightRenderer(@Nullable HighlightRangeSource highlightRangeSource, String highlightStyle) {
    super();
    this.highlightRangeSource = highlightRangeSource;
    this.highlightStyle = highlightStyle;
  }

  public HighlightRenderer(@Nullable HighlightRangeSource highlightRangeSource, HighlightMode highlightMode) {
    super();
    this.highlightRangeSource = highlightRangeSource;
    this.highlightStyle = highlightMode.getStyle();
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

    final JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    
    // Most of these values are obvious, but some were determined by stepping through the method when a 
    // paint() method was calling it. 
    String displayText = SwingUtilities.layoutCompoundLabel(renderer,
        renderer.getFontMetrics(renderer.getFont()),
        renderer.getText(),
        renderer.getIcon(),
        renderer.getVerticalAlignment(),
        renderer.getHorizontalAlignment(),
        renderer.getVerticalTextPosition(),
        renderer.getHorizontalTextPosition(),
        prepareRect(table.getCellRect(row, column, false), renderer.getInsets()),
        new Rectangle(),
        new Rectangle(),
        renderer.getIconTextGap()
        );
    String fullText = value.toString();

    // 3 is for the three dots at the end. (The renderer does not use the ellipsis character.)
    int maxDisplayLength = fullText.equals(displayText) ? fullText.length() : (displayText.length() - 3);

    if (highlightRangeSource != null) {
      Collection<TextRange> highlightRanges = highlightRangeSource.getOrderedTextRanges(fullText);
      highlightRanges.forEach(System.out::println);
      System.out.println();
      if (!highlightRanges.isEmpty()) {
        StringBuilder builder = new StringBuilder();
        // Text below this number has been completely highlighted.
        int indexCompleted = 0;
        int previousHigh = 0;
        for (TextRange textRange: highlightRanges) {
          assert textRange.getStart() < textRange.getEnd() : String.format("%d < %d ?", textRange.getStart(), textRange.getEnd());
          assert textRange.getStart() >= 0 : String.format("%d >= 0 ?", textRange.getStart());
          assert textRange.getStart() >= previousHigh : String.format("%d >= %d ?", textRange.getStart(), previousHigh);
          previousHigh = textRange.getEnd();

          final int start = textRange.getStart();
          assert start >= 0 : String.format("Start = %d < 0", start);
          if (start < maxDisplayLength) {
            assert start >= indexCompleted : String.format("%d > %d ?", start, indexCompleted);
            builder
                .append(fullText, indexCompleted, start)
                .append("<span style=\"")
                .append(highlightStyle)
                .append("\">");
            int end = textRange.getEnd();
            if (end > maxDisplayLength) {
              builder
                  .append(displayText.substring(start))
                  .append("</span>");
              indexCompleted = displayText.length();
              break;
            } else {
              builder
                  .append(displayText, start, end)
                  .append("</span>");
              indexCompleted = end;
            }
          } else {
            assert maxDisplayLength >= indexCompleted: 
                String.format("maxDisplayLength < indexCompleted: %d < %d", maxDisplayLength, indexCompleted);
            builder
                .append(fullText, indexCompleted, maxDisplayLength)
                .append("<span style=\"")
                .append(highlightStyle)
                .append("\">")
                .append(displayText.substring(maxDisplayLength))
                .append("</span>");
            indexCompleted = displayText.length();
            break;
          }
        } // end for loop
        if (indexCompleted <= maxDisplayLength) {
          builder.append(displayText.substring(indexCompleted));
        } 
        builder
            .insert(0, "<html>")
            .append("</html>");
        renderer.setText(builder.toString());
//        System.out.printf("%nUsing      %s%nto display %s%nfrom       %s%n", builder, displayText, fullText); // NON-NLS
      }
    }
    return renderer;
  }

  private static Rectangle prepareRect(Rectangle rectangle, Insets insets) {
    return new Rectangle(
        insets.left,
        insets.top,
        rectangle.width - (insets.left + insets.right),
        rectangle.height - (insets.top + insets.bottom)
    );
  }
  
  public @Nullable HighlightRangeSource getHighlightRangeSource() {
    return highlightRangeSource;
  }
  
  @SuppressWarnings("NullableProblems")
  public void setHighlightRangeSource(HighlightRangeSource highlightRangeSource) {
    this.highlightRangeSource = highlightRangeSource;
  }

  /**
   * <p>Sets the highlightStyle. This is a String that would appear in an html style attribute</p>
   * @param highlightStyle The highlight style.
   */
  public void setHighlightStyle(String highlightStyle) {
    this.highlightStyle = highlightStyle;
  }

  public void setHighlightRangeSource(@Nullable HighlightRangeSource highlightRangeSource, String highlightStyle) {
    this.highlightRangeSource = highlightRangeSource;
    this.highlightStyle = highlightStyle;
  }
  
  public enum HighlightMode {
    RedText("color:red;"),
    RedUnderlinedText("text-decoration:underline; color:#ff0000;"),
    UnderlinedText("text-decoration: underline;"),
    PinkHighlight("background-color: #ffaaaa;"),
    YellowHighlight("background-color: #f5d51b;"),
    BlueHighlight("background-color: #aaddff;"),
    BlackHighlight("background-color: #44444ff; color: #ffffff;"),
    LavenderHighlight("background-color: #8888ff; color: #ffffff"),
    DarkBlueHighlight("background-color: #2244ff; color: #ffffff;"),
    RedBgYellowTextHighlight("background-color: #ff0000; color: #ffff00;")
    ;
    private final String style;
    HighlightMode(String style) {
      this.style = style;
    }
    
    public String getStyle() { return style; }
  }
                                  
  public static class RangeSet extends TreeSet<TextRange> {
    /**
     * Adds a new TextRange to the set, rejecting any that overlap an existing TextRange.
     * @param textRange element to add
     * @return true if it added the range, false otherwise.
     */
    @Override
    public boolean add(TextRange textRange) {
      SortedSet<TextRange> headSet = headSet(textRange);
      if (!headSet.isEmpty()) {
        final TextRange last = headSet.last();
        if (last.intersects(textRange)) {
          return doubleCheck(false);
        }
      }
      SortedSet<TextRange> tailSet = tailSet(textRange);
      if (tailSet.isEmpty()) {
        return doubleCheck(super.add(textRange));
      }
      final TextRange first = tailSet.first();
      if (first.intersects(textRange)) {
        return doubleCheck(false);
      }
      return doubleCheck(super.add(textRange));
    }

    /**
     * This double-checks that there are no overlapping TextRanges in the RangeSet. It does this inside
     * an assert, so it can be turned off in production by leaving assertions disabled.
     * @param rValue the return value
     * @return rValue
     */
    private boolean doubleCheck(boolean rValue) {
      // yeah, this calls noOverlaps twice, but only if it detects an overlap. 
      assert noOverlaps().isEmpty() : noOverlaps();
      return rValue;
    }
    
    private String noOverlaps() {
      Iterator<TextRange> iterator = iterator();
      TextRange previous = iterator.next();
      while (iterator.hasNext()) {
        TextRange current = iterator.next();
        if (current.intersects(previous)) {
          return String.format("Overlap: (%s) & (%s)", previous, current);
        }
        previous = current;
      }
      return "";
    }

    /**
     * If textRange starts earlier than last, replaces last in the TreeSet with textRange, and returns true;
     * otherwise, leaves the TreeSet unchanged and returns false.
     * @param textRange The TextRange to possibly insert
     * @param headSet The set of values before TextRange.
     * @param last last element of headSet
     * @return True if this method changed the Set.
     */
    private static boolean insertedValue(TextRange textRange, SortedSet<TextRange> headSet, TextRange last) {
      final int lastStart = last.getStart();
      final int nextStart = textRange.getStart();
      if ((lastStart > nextStart) || ((lastStart == nextStart) && (textRange.getEnd() > last.getEnd()))) {
        headSet.remove(last);
        headSet.add(textRange);
        return true;
      }
      return false; // The mispelled word shoud appear here.
    }

    @Override
    public boolean addAll(Collection<? extends TextRange> c) {
      boolean modified = false;
      for (TextRange e : c) {
        if (add(e)) {
          modified = true;
        }
      }
      return modified;
    }
  }
}

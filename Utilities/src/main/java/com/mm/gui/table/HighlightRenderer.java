package com.mm.gui.table;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 3/28/24</p>
 * <p>Time: 9:24 AM</p>
 * <p>@author Miguel Muñoz</p>
 */
public class HighlightRenderer extends DefaultTableCellRenderer {
  
  public interface TextRange {
    int getLow();
    int getHigh();
  }

  public static class TextRangeImpl implements TextRange, Comparable<TextRange> {
    private final int lowInclusive;
    private final int highExclusive;
    
    public TextRangeImpl(int lowInclusive, int highExclusive) {
      if (lowInclusive > highExclusive) {
        this.lowInclusive = highExclusive;
        this.highExclusive = lowInclusive;
      } else {
        this.lowInclusive = lowInclusive;
        this.highExclusive = highExclusive;
      }
    }

    @Override
    public int getLow() {
      return lowInclusive;
    }

    @Override
    public int getHigh() {
      return highExclusive;
    }

    @Override
    public int compareTo(@NotNull HighlightRenderer.TextRange that) {
      return Integer.compare(this.getLow(), that.getLow());
    }

    @Override
    public int hashCode() {
      // Adapted from java.awt.Dimension
      int sum = lowInclusive + highExclusive;
      return ((sum * (sum + 1)) / 2) + lowInclusive;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof TextRange) {
        TextRange that = (TextRange) obj;
        return (this.lowInclusive == that.getLow()) && (this.highExclusive == that.getHigh());
      }
      return false;
    }

    @Override
    public String toString() {
      return String.format("(%s -> %s)", lowInclusive, highExclusive);
    }

    public static TextRange of(int lowInclusive, int width) {
      return new TextRangeImpl(lowInclusive, lowInclusive + width);
    }
  }
  
  @FunctionalInterface
  public interface HighlightRangeSource {
    /**
     * Return non-overlapping text ranges, in increasing order, describing text that should get highlighted.
     * @param text The text to inspect for possible highlights
     * @return an ordered array, in increasing order, of text ranges to highlight
     */
    List<TextRange> getOrderedTextRanges(String text);
  }
  
  
  private @Nullable HighlightRangeSource highlightRangeSource;
  private String highlightStyle; // example: color:red;
  
  public HighlightRenderer() {
    this(null, "");
  }

  public HighlightRenderer(@Nullable HighlightRangeSource highlightRangeSource, String highlightStyle) {
    super();
    this.highlightRangeSource = highlightRangeSource;
    this.highlightStyle = highlightStyle;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

    final JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    String displayText = SwingUtilities.layoutCompoundLabel(renderer,
        renderer.getFontMetrics(renderer.getFont()),
        renderer.getText(),
        renderer.getIcon(),
        renderer.getVerticalAlignment(),
        renderer.getHorizontalAlignment(),
        renderer.getVerticalTextPosition(),
        renderer.getHorizontalTextPosition(),
        prepareRect(table.getCellRect(row, column, false), renderer.getInsets()),
        new Rectangle(), // TODO: Figure out how to calculate the iconRectangle.
        new Rectangle(), // TODO: Figure out how to calculate the textRectangle. (both are empty with no icon
        renderer.getIconTextGap()
        );
    if (column == 1) {
      System.out.printf("displayText ends with %s at row %2d%n", getLastThree(displayText), row); // NON-NLS
    }
    String fullText = value.toString();
    boolean textClipped = !fullText.equals(displayText);
    int maxDisplayLength = textClipped ? (displayText.length() - 3) : fullText.length();
    if (highlightRangeSource != null) {
      System.out.printf("Border: %s%n", renderer.getBorder());
      System.out.printf("Insets: %s%n", renderer.getInsets()); // NON-NLS
      List<TextRange> highlightRanges = highlightRangeSource.getOrderedTextRanges(fullText);
      int previousHigh = 0;
      for (TextRange range: highlightRanges) {
        assert range.getLow() < range.getHigh();
        assert range.getLow() >= 0;
        assert range.getHigh() >= 0;
        assert range.getLow() >= previousHigh;
        previousHigh = range.getHigh();
      }
      if (!highlightRanges.isEmpty()) {
        StringBuilder builder = new StringBuilder();
        // Text below this number has been completely highlighted.
        int indexCompleted = 0;
        for (TextRange textRange: highlightRanges) {
          final int start = textRange.getLow();
          assert start >= 0 : String.format("Start = %d < 0", start);
          if (start < maxDisplayLength) {
            builder
                .append(fullText, indexCompleted, start)
                .append("<span style=\"")
                .append(highlightStyle)
                .append("\">");
            int end = textRange.getHigh();
            if (end > maxDisplayLength) {
              System.out.printf("range to find %d: %d - %d in <%s>%n", displayText.length(), start, end, displayText); // NON-NLS
              builder
                  .append(displayText.substring(start))
                  .append("</span>");
              indexCompleted = maxDisplayLength;
            } else {
              builder
                  .append(displayText, start, end)
                  .append("</span>");
              indexCompleted = end;
            }
          } else {
            if (maxDisplayLength < indexCompleted) {
              System.out.printf("Error: %d < %d%n", maxDisplayLength, indexCompleted); // NON-NLS
            }
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
        }
        if (indexCompleted < maxDisplayLength) {
          builder.append(displayText.substring(indexCompleted));
        }
        builder
            .insert(0, "<html>")
            .append("</html>");
        renderer.setText(builder.toString());
        System.out.printf("Using      %s%nto display %s%nfrom       %s%n", builder, displayText, fullText); // NON-NLS
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
  
  private String getLastThree(String text) {
    if (text.length() > 2) {
      return text.substring((text.length()-3));
    }
    return text;
  }

  public @Nullable HighlightRangeSource getHighlightRangeSource() {
    return highlightRangeSource;
  }

  public void setHighlightRangeSource(@Nullable HighlightRangeSource highlightRangeSource, String highlightStyle) {
    this.highlightRangeSource = highlightRangeSource;
    this.highlightStyle = highlightStyle;
  }
}

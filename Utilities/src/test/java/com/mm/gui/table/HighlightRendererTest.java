package com.mm.gui.table;

import java.util.Iterator;

import com.mm.gui.table.HighlightRenderer.RangeSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>For TextRange testing, this class uses terms "overlap" and "swallow" to describe two different
 * kinds of intersection over Text Ranges. A swallow is when one range starts before and ends after
 * the other range. An overlap is when one starts before and ends before, or starts after and ends
 * after.</p>
 * <p>It also uses words Low and High to determine the placement of the second overlap. Low means
 * the second overlap has a lower starting index, and High means a higher starting index.</p>
 * <p>There are ten possible intersection scenarios that should be tested. In this diagram, X shows 
 * an existing range, and . shows an inserted range that intersects. Here are the ten scenarios that 
 * need to be tested.</p>
 * <pre>
 *          XXXXXX      XXXXXX      XXXXXX
 *  A         ...
 *  B         .......
 *  C         .............
 *  D         ...................
 *  E         .........................
 *  F         ...............................
 *  G                ......
 *  H                ............
 *  I                ..................
 *  J                ........................
 * </pre>
 * 
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 5/8/24</p>
 * <p>Time: 10:19?AM</p>
 * <p>@author Miguel Mu–oz</p>
 */
@SuppressWarnings("MagicNumber")
class HighlightRendererTest {

  @Test
  void rangeSet_A_AddSwallowHigh() {
    // Setup
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(10, 20));
    rangeSet.add(range(30, 40));

    rangeSet.add(range(12, 18));

    assertEquals(2, rangeSet.size());
    Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();
    assertEquals(range(10, 20), itr.next());
    assertEquals(range(30, 40), itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  void range_B_SetAddOverlapHigh() {
    // Setup
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(10, 20));
    rangeSet.add(range(30, 40));
    rangeSet.add(range(50, 60));

    // Test
    rangeSet.add(range(15, 25));

    // Verify
    assertEquals(3, rangeSet.size());
    Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();
    assertEquals(range(10, 20), itr.next());
    assertEquals(range(30, 40), itr.next());
    assertEquals(range(50, 60), itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  void rangeSet_C() {
    // Setup
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(30, 40));
    rangeSet.add(range(50, 60));
    rangeSet.add(range(10, 20));

    rangeSet.add(range(15, 35));

    assertEquals(3, rangeSet.size());
    Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();
    assertEquals(range(10, 20), itr.next());
    assertEquals(range(30, 40), itr.next());
    assertEquals(range(50, 60), itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  void rangeSet_D() {
    // Setup
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(30, 40));
    rangeSet.add(range(50, 60));
    rangeSet.add(range(10, 20));

    // Test
    rangeSet.add(range(15, 45));

    // Verify
    assertEquals(3, rangeSet.size());
    Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();
    assertEquals(range(10, 20), itr.next());
    assertEquals(range(30, 40), itr.next());
    assertEquals(range(50, 60), itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  void rangeSet_E() {
    // Setup
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(30, 40));
    rangeSet.add(range(50, 60));
    rangeSet.add(range(10, 20));

    // Test
    rangeSet.add(range(15, 55));

    // Verify
    assertEquals(3, rangeSet.size());
    Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();
    assertEquals(range(10, 20), itr.next());
    assertEquals(range(30, 40), itr.next());
    assertEquals(range(50, 60), itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  void rangeSet_F() {
    // Setup
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(30, 40));
    rangeSet.add(range(50, 60));
    rangeSet.add(range(10, 20));

    // Test
    rangeSet.add(range(15, 65));

    // Verify
    assertEquals(3, rangeSet.size());
    Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();
    assertEquals(range(10, 20), itr.next());
    assertEquals(range(30, 40), itr.next());
    assertEquals(range(50, 60), itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  void rangeSet_G() {
    // Setup
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(30, 40));
    rangeSet.add(range(10, 20));
    rangeSet.add(range(50, 60));

    // Test
    rangeSet.add(range(25, 35));

    // Verify
    assertEquals(3, rangeSet.size());
    Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();
    assertEquals(range(10, 20), itr.next());
    assertEquals(range(30, 40), itr.next());
    assertEquals(range(50, 60), itr.next());
    assertFalse(itr.hasNext());
  }

  @org.junit.jupiter.api.Test
  void rangeSet_G_AddOverlapLow() {
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(2, 3));
    rangeSet.add(range(10, 20));

    rangeSet.add(range(5, 15));

    assertEquals(2, rangeSet.size());
    Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();
    assertEquals(range(2, 3), itr.next());
    assertEquals(range(10, 20), itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  void rangeSet_H_AddSwallowLow() {
    // Setup
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(10, 20));
    rangeSet.add(range(30, 40));
    rangeSet.add(range(50, 60));

    // Test
    rangeSet.add(range(25, 45));

    // Verify
    assertEquals(3, rangeSet.size());
    Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();
    assertEquals(range(10, 20), itr.next());
    assertEquals(range(30, 40), itr.next());
    assertEquals(range(50, 60), itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  void rangeSet_I() {
    // Setup
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(10, 20));
    rangeSet.add(range(30, 40));
    rangeSet.add(range(50, 60));

    // Test
    rangeSet.add(range(25, 45));

    // Verify
    assertEquals(3, rangeSet.size());
    Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();
    assertEquals(range(10, 20), itr.next());
    assertEquals(range(30, 40), itr.next());
    assertEquals(range(50, 60), itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  void rangeSet_J() {
    // Setup
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(10, 20));
    rangeSet.add(range(30, 40));
    rangeSet.add(range(50, 60));

    // Test
    rangeSet.add(range(25, 45));

    // Verify
    assertEquals(3, rangeSet.size());
    Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();
    assertEquals(range(10, 20), itr.next());
    assertEquals(range(30, 40), itr.next());
    assertEquals(range(50, 60), itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  void rangeSetAddKissLow() {
    // Setup
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(10, 20));
    rangeSet.add(range(5, 10));

    assertEquals(2, rangeSet.size());
    Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();

    assertEquals(range(5, 10), itr.next());
    assertEquals(range(10, 20), itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  void rangeSetAddKissHigh() {
    // Setup
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(5, 10));
    rangeSet.add(range(10, 20));

    assertEquals(2, rangeSet.size());
    Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();

    assertEquals(range(5, 10), itr.next());
    assertEquals(range(10, 20), itr.next());
    assertFalse(itr.hasNext());
  }
  
  @Test
  void rangeSetAddOverlapOfTwo() {
    // Setup
    RangeSet rangeSet = new RangeSet();
    rangeSet.add(range(10, 20));
    rangeSet.add(range(30, 40));
    rangeSet.add(range(50, 60));
    rangeSet.add(range(70, 80));

    rangeSet.add(range(25, 65));
    
    assertEquals(4, rangeSet.size());
    final Iterator<HighlightRenderer.TextRange> itr = rangeSet.iterator();
    assertEquals(range(10, 20), itr.next());
    assertEquals(range(30, 40), itr.next());
    assertEquals(range(50, 60), itr.next());
    assertEquals(range(70, 80), itr.next());
    assertFalse(itr.hasNext());
  }
  
  

  private static HighlightRenderer.TextRange range(int low, int high) {
    return new HighlightRenderer.TextRangeImpl(low, high);
  }
}

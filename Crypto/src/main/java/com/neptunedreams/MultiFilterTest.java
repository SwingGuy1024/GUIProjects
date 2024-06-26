package com.neptunedreams;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.jetbrains.annotations.NonNls;

/**
 * <p>Program to test a certain cryptogram with MultiFilter:</p>
 * <p>Here's the cryptogram:</p>
 * <pre>
 *   n rp krwqj wl xlgtaqb oraj na oraj inwo pb uwtrazq oqtlqu raj wl ugtfqb woq
 *   ugtznaz nppqaunwb lk mnkq wl ugtfqb nw wotlgzo woq mrgzowqt worw rmm dra uqq
 *   raj wotlgzo woq wqrtu gauqqa raj gayalia cb rablaq
 * </pre>
 * <p>This decodes to this:</p>
 * <pre>
 *   I AM FATED TO JOURNEY HAND IN HAND WITH MY STRANGE HEROES AND TO SURVEY THE
 *   SURGING IMMENSITY OF LIFE TO SURVEY IT THROUGH THE LAUGHTER THAT ALL CAN SEE
 *   AND THROUGH THE TEARS UNSEEN AND UNKNOWN BY ANYONE
 * </pre>
 * <p>When I sent the entire string to decrypt, it takes a long time, as expected, 
 * but it doesn't find the solution. When I put shorter segments in, it works. It 
 * should still work when I put the whole thing in. So this is an experiment to try
 * to find a reproducible test case.</p>
 * <p>I discovered that the problem was a single misspelled word, which I suspected
 * but failed to find visually. The word was qaugga, which I had as quagga, for "unseen."</p>
 * 
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 5/2/24</p>
 * <p>Time: 5:49 PM</p>
 * <p>@author Miguel Muñoz</p>
 */
public enum MultiFilterTest {
  ;

  public static void main(String[] args) throws IOException {
    @NonNls String cryptogram = "n rp krwqj wl xlgtaqb oraj na oraj inwo pb uwtrazq oqtlqu raj wl ugtfqb woq" +
        " ugtznaz nppqaunwb lk mnkq wl ugtfqb nw wotlgzo woq mrgzowqt worw rmm dra uqq" +
        " raj wotlgzo woq wqrtu gauqqa raj gayalia cb rablaq";
    
//    long start = System.currentTimeMillis();
    System.out.println( cryptogram);
    String[] words = cryptogram.split(" ");
    MultiFilter multiFilter = new MultiFilter();
//    multiFilter.matchList(words);
//    long end = System.currentTimeMillis();
//    long duration = end - start;
//    long millis = duration % 1000;
//    long dSeconds = duration / 1000;
//    long seconds = dSeconds % 60;
//    long minutes = dSeconds / 60;
//    System.out.printf("%d seconds = %d:%02d.%03d%n", dSeconds, minutes, seconds, millis); // NON-NLS

    Arrays.sort(words, Comparator.comparingInt(String::length).reversed());
//    Arrays.stream(words)
//        .sequential()
//        .forEach(System.out::println);
    multiFilter.matchList(words);
    for (int i=0; i<words.length; ++i) {
      System.out.printf("%d:%n", i);
      multiFilter.matchList(Arrays.copyOfRange(words, 0, words.length-i));
    }
  }
}

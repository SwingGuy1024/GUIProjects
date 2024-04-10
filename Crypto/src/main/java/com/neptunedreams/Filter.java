package com.neptunedreams;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

/**
 * Class for cracking Cryptograms.
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 3/23/24</p>
 * <p>Time: 11:36ÊAM</p>
 * <p>@author Miguel Mu–oz</p>
 */
public class Filter {
  public static final String WORD_LIST_FILE = "enable1.txt";
  private static final Set<Character> doublable = "bcdefgklmnoprst"
      .chars()
      .boxed()
      .map(i -> (char) i.intValue())
      .collect(Collectors.toUnmodifiableSet());
  private static final Set<String> ignoreList = new HashSet<>(Arrays.asList("abasias", "abaxial", "acaudal", "adagial", "adaxial",
      "ataghan", "ataxias", "egested", "emended", "emender", "emeries", "emersed", "emeutes", "momisms", "nandina",
      "nandins", "nankins", "titbits", "yakitori"));

  private static void test(String a, String b, BiPredicate<String, String> predicate) {
    System.out.printf("%-5b -- %s, %s: %n", predicate.test(a, b), a, b); // NON-NLS
  }

  public static void main(String[] args) throws URISyntaxException, IOException {
    // pjrbz cggem jm fqosg. pjrbz flu tbbu. ubl'a tbzhga tbbu. dbj kfl hb f iggc ioapbja qfjhpolh.
    
    // Humor keeps us alive. Humor and food. Don't forget food. You can go a week without laughing.
    
    //htujxxtp wxckk zektxa
//    testSd();
    final Path path = getPath();
    Counter counter = new Counter();
//    Counter innerCounter = new Counter();
//    try (Stream<String> lines = Files.lines(path)) {
//      lines
//          .filter(s -> isAorI(s, 1))
//          .filter(matchFilter("QFJHPOLH"))
//          .peek(counter)
//          .forEach(System.out::println);
//    }
    BiPredicate<String, String> biPredicate = filterMatch("ioapbja", "qfjhpolh");
    test("without", "laughing", biPredicate);
    test("without", "laughint", biPredicate);
    test("without", "laughinj", biPredicate);
    test("without", "laughxng", biPredicate);
    test("without", "laugxing", biPredicate);
    test("wxthout", "laughing", biPredicate);
    test("witxout", "laughing", biPredicate);
    test("jikhxyz", "laughing", biPredicate);
    test("without", "jkqxhiyz", biPredicate);
    
//    List<String> matchList = match("hmoywle", s -> isAorI(s,1));
//    for(String s:matchList) {
//      System.out.println(s);
//    }

//    matchFilter("xwlpk", "hmoywle", null, s -> isAorI(s, 1));
//    matchFilter("kcwfat", "hmoywle", null, s -> isAorI(s, 1));
    matchFilter("kcwfat", "qwca", null, s -> isAorI(s, 1));
//    matchFilter("qdha", "qwca", null, null);
    System.out.printf("%n---------------%n%n"); // NON-NLS
//    matchFilter("htujxxtp", "wxckk", null, s -> isAorI(s, 2));
//    matchFilter("htujxxtp", "wxckk", null, s -> isAorI(s, 1));
//    System.out.println(counter);

//    try (Stream<String> lines = Files.lines(path)) {
//      lines
//          .filter(s1 -> s1.length() == 9)
//          .filter(s1 -> distinctLetters(s1) == 9)
//          .forEach(counter);
//    }
//    System.out.printf("Matches: %d%n", counter.get()); // NON-NLS
//    try (Stream<String> lines = Files.lines(path)) {
//      lines
//          .filter(s -> s.length() == 9)
//          .filter(s -> distinctLetters(s) == 7)
//          .filter(s -> lettersMatch(s, 1, 5))
//          .filter(s -> lettersMatch(s, 3, 6))
//          .forEach(innerCounter);
//    }
//    System.out.printf("Matches: %d%n", innerCounter.get()); // NON-NLS

//    long start = System.currentTimeMillis();
//    try (Stream<String> lines = Files.lines(path)) {
//      lines
//          // YMXGVSTHO SHKUZHUEX -- eghkmostuvxyz -- XHS
////          .parallel()
//          .filter(s -> s.length() == 9)
////          .filter(s -> isAorI(s.charAt(5)))
//          .filter(s -> distinctLetters(s) == 7)
//          .filter(s -> lettersMatch(s, 1, 5))
//          .filter(s -> lettersMatch(s, 3, 6))
//          .forEach(s -> {
////              char c0 = s.charAt(0);
////              char c1 = s.charAt(1);
////              char c8 = s.charAt(8);
////            System.out.println(s);
//            try (Stream<String> lines2 = Files.lines(path)) {
//              lines2
//                  .filter(s1 -> s1.length() == 9)
//                  .filter(s1 -> distinctLetters(s1) == 9)
//                  .filter(s1 -> distinctLetters(s + s1) == 13)
//                  .filter(s1 -> lettersCrossMatch(s1, s, 5, 0))
//                  .filter(s1 -> lettersCrossMatch(s1, s, 7, 1, 5))
//                  .filter(s1 -> lettersCrossMatch(s1, s, 2, 8))
////                  .filter(s1 -> s1.charAt(5) == c0)
////                  .filter(s1 -> s1.charAt(7) == c1)
////                  .filter(s1 -> s1.charAt(2) == c8)
//                  .forEach(s1 -> System.out.printf("%s %s%n", s, s1));
//            } catch (IOException e) {
//              throw new RuntimeException(e);
//            }
//          });
////          .filter(s -> doesNotHave(s, "eradfghm"))
////          .filter(s -> lettersMatch(s, 0, 2))
////          .filter(s -> lettersMatch(s, 4, 5))
//////          .filter(s -> s.charAt(2) != 'a')
////          .peek(counter)
////          .forEach(System.out::println);
//      
////          .filter(s -> s.startsWith("a") || s.startsWith("i"))
////          .filter(s -> lettersMatch(s, 0, 4, 6))
////          .filter(s -> lettersMatch(s, 2, 5))
////          .filter(s -> doublable.contains(s.charAt(2)))
////          .peek(counter)
////          .forEach(System.out::println);
//      long duration = System.currentTimeMillis() - start;
//      System.out.println(counter.get());
//      System.out.printf("%4d ms%n", duration); // NON-NLS
//    }
  }
  
  private static boolean isReal(String s) { return !ignoreList.contains(s); }

  private static Path getPath() throws URISyntaxException {
    return Path.of(getResource());
  }

  private static URI getResource() throws URISyntaxException {
    final URL resource = Filter.class.getResource(WORD_LIST_FILE);
    if (resource == null) {
      //noinspection ProhibitedExceptionThrown
      throw new NullPointerException(String.format("From %s", WORD_LIST_FILE));
    }
    return resource.toURI();
  }

  private static void findZwusz(String s0, Path path, Counter innerCounter) {
    // FSAGVUSZ ZWUSZ, U IN (A, I)
    try (Stream<String> lines = Files.lines(path)) {
      final char c7 = s0.charAt(7);
      final char c1 = s0.charAt(1);
      final char c5 = s0.charAt(5);
      lines
          .filter(s -> s.length() == 5)
          .filter(s -> lettersMatch(s, 0, 4))
          .filter(s -> distinctLetters(s) == 4)
          .filter(s -> c7 == s.charAt(0))
          .filter(s -> c1 == s.charAt(3))
          .filter(s -> c5 == s.charAt(2))
          .peek(innerCounter)
          .forEach(s -> System.out.printf("%s %s%n", s0, s));
    } catch (IOException e) {
      //noinspection ProhibitedExceptionThrown
      throw new RuntimeException(e);
    }
    System.out.printf("  %d%n", innerCounter.get()); // NON-NLS
  }

  private static boolean isAorI(char c) {
    //noinspection MagicCharacter
    return (c == 'a') || (c == 'i');
  }

  private static boolean isAorI(String s, int index) {
    return isAorI(s.charAt(index));
  }

  private static boolean doesNotHave(String word, String letters) {
    for (char c : letters.toCharArray()) {
      if (word.indexOf(c) >= 0) {
        return false;
      }
    }
    return true;
  }

  private static void measurePerformance(Path path) throws IOException {
    int loops = 1000;
    long sum = 0;
    long sumSq = 0;
    for (int i = 0; i < loops; ++i) {
      long time = executeParallel(path);
      sum += time;
      sumSq += time * time;
      System.out.println(time);
    }

    System.out.println("Parallel");
    System.out.printf("Mean:  %8f ms%n", mean(sum, loops)); // NON-NLS
    System.out.printf("StDev: %8f ms%n", stDev(loops, sum, sumSq)); // NON-NLS


    sum = 0;
    sumSq = 0;
    for (int i = 0; i < loops; ++i) {
      long time = executeSerial(path);
      sum += time;
      sumSq += time * time;
    }

    System.out.println("Serial");
    System.out.printf("Mean:  %8f ms%n", mean(sum, loops)); // NON-NLS
    System.out.printf("StDev: %8f ms%n", stDev(loops, sum, sumSq)); // NON-NLS
//    Counter counter = new Counter();
//    try (Stream<String> lines = Files.lines(path)) {
//      lines
////          .parallel()
//          .filter(s -> s.length() == 10)
//          .filter(s -> distinctLetters(s) == 8)
//          .filter(s -> lettersMatch(s, 0, 3, 6))
//          .peek(counter)  // non-terminating counter.
//          .forEach(System.out::println);
//    }
//    System.out.println(counter.get());
//    System.out.printf("%d ms%n", System.currentTimeMillis() - start); // NON-NLS

//    searchForXAXxCXE_and_xxCXAxE(path); // enemies friends

//    searchForXAXB_XBAxXx(path); // envied even
  }

  private static void testSd() {
    int[] values = {100, 120, 113, 132, 112, 134, 106, 123, 126};
//    double std1 = calcStDev(values);
    int sumX = 0;
    int sumXSqr = 0;
    for (int i : values) {
      sumX += i;
      sumXSqr += i * i;
    }
    double std2 = stDev(values.length, sumX, sumXSqr);
//    System.out.println(std1); // NON-NLS
    System.out.println(std2);
  }

  private static double mean(long sum, int loops) {
    return ((double) sum) / loops;
  }

  private static double stDev(long n, long sum, long sumSq) {
    double nd = (double) n;
    return StrictMath.sqrt((sumSq - ((sum * sum) / nd)) / (nd - 1.0));
  }

  // This was an early method to match a single word. It works fine, but it's not very flexible. 
  // For example, I can't specify which characters are only a or i. It's not flexible because
  // it does everything. It creates the stream and runs the loop, returning a list of matching
  // words.
  private static List<String> match(String cipherText) throws URISyntaxException, IOException {
    cipherText = cipherText.toLowerCase();
    final Path path = getPath();
    int length = cipherText.length();
    int distinct = distinctLetters(cipherText);
    try (Stream<String> lines = Files.lines(path)) {
      Stream<String> stream = lines
          .filter(s -> s.length() == length)
          .filter(s -> distinctLetters(s) == distinct);
      if (distinct < length) {
        Map<Character, List<Integer>> matchSets = matchSets(cipherText);
        for (Character c : matchSets.keySet()) {
          List<Integer> list = matchSets.get(c);
          stream = stream.filter(s -> lettersMatch(s, list));
        }
      }
      return stream.collect(Collectors.toList());
    }
  }
  
  private static List<String> match(String cipherText, @Nullable Predicate<String> prefilter) throws URISyntaxException, IOException {
    cipherText = cipherText.toLowerCase();
    final Path path = getPath();
    try (Stream<String> lines = Files.lines(path)) {
      return lines
          .filter(Filter::isReal)
          .filter(matchFilter(cipherText, prefilter))
          .collect(Collectors.toList());
    }
  }

  // This matches a single word. Unlike my first attempt, this just returns a filter that will
  // may be used in a stream. The filter it returns is a compound filter. It runs a series of
  // tests, based on repeated characters, but it can be treated a single filter in a Stream.
  private static Predicate<String> matchFilter(String cipherText, @Nullable Predicate<String> preFilter) {
    cipherText = cipherText.toLowerCase();
    int length = cipherText.length();
    int distinct = distinctLetters(cipherText);
    Predicate<String> compoundFilter = s -> s.length() == length;
    if (preFilter != null) {
      compoundFilter = compoundFilter.and(preFilter);
    }
    compoundFilter = compoundFilter.and((s -> distinctLetters(s) == distinct));
    if (distinct < length) {
      Map<Character, List<Integer>> matchSets = matchSets(cipherText);
      for (Character c : matchSets.keySet()) {
        List<Integer> list = matchSets.get(c);
        compoundFilter = compoundFilter.and(s -> lettersMatch(s, list));
      }
    }
    return compoundFilter;
  }
  
  

  // After giving it some thought about how to match two words, Here's what I think I need: First,
  // I need a method that just matches two words, but does everything. It should include pre-filters,
  // so I can do AorI filtering ahead of time, and it it should create the stream. Second I could 
  // create a method that takes two words, and figures out which word to start with, by measuring 
  // how many results each filter produces on the first pass. (Maybe that should be a separate method.)
  // And I think I need one method that takes a Stream and two words, matches the first word, and 
  // starts a second stream to match the second word. And I could use a method that takes a Stream
  // that has already reduced to viable first words, and starts a second stream. This should include
  // a parameter to do pre-filtering for things like AorI detection.
  public static void matchFilter(
      String cipherTextOne,
      String cipherTextTwo,
      @Nullable Predicate<String> preFilterOne,
      @Nullable Predicate<String> preFilterTwo
  ) throws URISyntaxException, IOException {
    cipherTextOne = cipherTextOne.toLowerCase();
    cipherTextTwo = cipherTextTwo.toLowerCase();
    BiPredicate<String, String> biPredicate = filterMatch(cipherTextOne, cipherTextTwo);
    Predicate<String> filter;
    List<String> listOne = match(cipherTextOne, preFilterOne);
    List<String> listTwo = match(cipherTextTwo, preFilterTwo);
    if (listOne.size() > listTwo.size()) {
      dualMatch(biPredicate, listTwo, listOne, true);
    } else {
      dualMatch(biPredicate, listOne, listTwo, false);
    }
  }
  
  private static void dualMatch(
      BiPredicate<String, String> biPredicate,
      List<String> shortList,
      List<String> longList,
      boolean reverse
  ) {
    Counter counter = new Counter();
    Stream<String> shortStream = shortList.stream();
    shortStream.forEach(s -> {
      matchPair(biPredicate, s, longList, reverse, counter);
    });
    System.out.println(counter);
  }
  
  private static void matchPair(
      BiPredicate<String, String> biPredicate,
      String candidateOne,
      List<String> longList,
      boolean reverse,
      Counter counter
  ) {
    final Predicate<String> stringPredicate = reverse?
        s -> biPredicate.test(s, candidateOne) :
        s -> biPredicate.test(candidateOne, s);
    try (Stream<String> stream = longList.stream()) {
      stream
          .filter(stringPredicate)
          .peek(counter)
          .forEach((String s) -> System.out.printf("%s %s%n", candidateOne, s)); // NON-NLS);
    }
  }

  private static BiPredicate<String, String> filterMatch(
      String cipherTextOne,
      String cipherTextTwo
  ) {
      // In this map, the keys are indices of characters in String one that are also found in String two.
      // The List has the indices into String two for that same letter.
      // the lettersCrossMatch() method uses these values to filter out words that don't match both cipher words
      Map<Integer, List<Integer>> mutualLetters = new HashMap<>();
      Set<Character> pastCharacters = new HashSet<>();
      for (int i=0; i<cipherTextOne.length(); ++i) {
        char c = cipherTextOne.charAt(i);
        if (!pastCharacters.contains(c)) {
          pastCharacters.add(c);
          for (int j = 0; j<cipherTextTwo.length(); j++) {
            if (c == cipherTextTwo.charAt(j)) {
              mutualLetters.merge(i, new LinkedList<>(List.of(j)), (la, lb) -> {
                la.add(lb.get(0));
                return la;
              });
            }
          }
        }
      }
      List<BiPredicate<String, String>> predicateList = new LinkedList<>();
      for (Integer index: mutualLetters.keySet()) {
        List<Integer> iList = mutualLetters.get(index);
        int[] indices = new int[iList.size()];
        int i=0;
        for (int j: iList) {
          indices[i++] = j;
        }
        predicateList.add((s, t) -> lettersCrossMatch(s, t, index, indices));
      }
      int distinctLetters = distinctLetters(cipherTextOne + cipherTextTwo);
      
      // The distinctLetters() test isn't foolproof. "without laughing" and "without laughint" have the 
      // same number of distinct letters. This case is rare enough that we can ignore it.
      //noinspection StringConcatenation
      BiPredicate<String, String> biPredicate = (s, t) -> distinctLetters(s + t) == distinctLetters;
      for (BiPredicate<String, String> next : predicateList) {
        biPredicate = biPredicate.and(next);
      }
      return biPredicate;
  }

  /**
   * <p>Searches for characters that are repeated in the cipherText, and returns a map of those 
   * repeated characters in the cipher text. The keys to each entry are the repeated characters,
   * and the values are a lists of the indices where that key letter appears.</p>
   * <p>So, for the word abcbdebcf, the repeated characters are b and c. Those will be the keys.
   * The b maps to a list of the positions of b, which are 1, 3, and 6. The c will map to a list of
   * 2 and 7.</p>
   * 
   * @param cipherText The text to map.
   * @return A map of the repeated characters with each mapping to a list of its indices. 
   */
  private static Map<Character, List<Integer>> matchSets(String cipherText) {
    Map<Character, List<Integer>> characterSetMap = new HashMap<>();
    int index = 0;
    for (char c : cipherText.toCharArray()) {
      if (characterSetMap.containsKey(c)) {
        characterSetMap.get(c).add(index);
      } else {
        List<Integer> list = new LinkedList<>();
        list.add(index);
        characterSetMap.put(c, list);
      }
      index++;
    }

    // Remove all maps with only one entry:
    for (Iterator<Character> itr = characterSetMap.keySet().iterator(); itr.hasNext(); ) {
      final Character next = itr.next();
      List<Integer> list = characterSetMap.get(next);
      if (list.size() == 1) {
        itr.remove();
      }
    }
    return characterSetMap;
  }

  private static long executeParallel(Path path) throws IOException {
    long start = System.currentTimeMillis();
    try (Stream<String> lines = Files.lines(path)) {
      lines
          .parallel()
          .filter(s -> s.length() == 10)
          .filter(s -> distinctLetters(s) == 8)
          .filter(s -> lettersMatch(s, 0, 3, 6))
          .forEach((s) -> {
          });
    }
    return System.currentTimeMillis() - start;
  }

  private static long executeSerial(Path path) throws IOException {
    long start = System.currentTimeMillis();
    try (Stream<String> lines = Files.lines(path)) {
      lines
//          .parallel()
          .filter(s -> s.length() == 10)
          .filter(s -> distinctLetters(s) == 8)
          .filter(s -> lettersMatch(s, 0, 3, 6))
          .forEach((s) -> {
          });
    }
    return System.currentTimeMillis() - start;
  }

  private static boolean lettersMatch(String s, int i, int... indices) {
    char a = s.charAt(i);
    for (int j : indices) {
      if (s.charAt(j) != a)
        return false;
    }
    return true;
  }

  private static boolean lettersMatch(String s, List<Integer> indices) {

    Iterator<Integer> itr = indices.iterator();
    char first = s.charAt(itr.next());
    while (itr.hasNext()) {
      if (first != s.charAt(itr.next())) {
        return false;
      }
    }
    return true;
  }

  private static boolean lettersCrossMatch(String s1, String s2, int i1, int... i2s) {
    char c1 = s1.charAt(i1);
    for (int i2 : i2s) {
      if (c1 != s2.charAt(i2)) {
        return false;
      }
    }
    return true;
  }

  private static void findXYY(Path path) throws IOException {
    try (final Stream<String> lines = Files.lines(path)) {
      lines
          .filter(s -> s.length() == 3)
          .filter(s -> s.charAt(1) == s.charAt(2))
          .forEach(System.out::println); // add all ass bee boo brr egg fee inn moo nee off pee see shh tee too wee woo zoo
    }
  }

  private static int distinctLetters(String s) {
    Set<Character> iSet = new HashSet<>();
    for (char c : s.toCharArray()) {
      iSet.add(c);
    }
    return iSet.size();
  }

  private static void searchForXAXxCXE_and_xxCXAxE(Path path) throws IOException {
    try (final Stream<String> lines = Files.lines(path)) {
      lines
          .filter(s -> s.length() == 7)
          .filter(s -> (s.charAt(0) == s.charAt(2)) && (s.charAt(0) == s.charAt(5)))
          .filter(s -> (s.charAt(3) != s.charAt(0)) && (s.charAt(3) != s.charAt(1)) && (s.charAt(3) != s.charAt(4)) && (s.charAt(3) != s.charAt(6)))
          .forEach(s -> searchForxxCXAxE(path, s));
    }
  }

  private static void searchForxxCXAxE(Path path, String s1) {
    if (ignoreList.contains(s1)) {
      return;
    }
    try (Stream<String> lines = Files.lines(path)) {
      List<String> resultList = lines
          .filter(s -> s.length() == 7)
          .filter(s -> s.charAt(3) == s1.charAt(0))
          .filter(s -> s.charAt(4) == s1.charAt(1))
          .filter(s -> s.charAt(6) == s1.charAt(6))
          .collect(Collectors.toList());
      if (!resultList.isEmpty()) {
        System.out.printf("%s:%n", s1); // NON-NLS
        for (String s : resultList) {
          System.out.printf("  %s -- %s%n", s, s1);
        }
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  private static void searchForXAXB_XBAxXx(Path path) {
    try (final Stream<String> lines = Files.lines(path)) {
      lines
          .filter(s -> s.length() == 4)
          .filter(s -> s.charAt(0) == s.charAt(2))
          .filter(s -> s.charAt(1) != s.charAt(3))
          .forEach(s -> searchx(s, path));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void searchx(String s4, Path path) {
    try (Stream<String> lines = Files.lines(path)) {
      lines
          .filter(s -> s.length() == 6)
          .filter(s -> s.charAt(0) == s4.charAt(0))
          .filter(s -> s.charAt(1) == s4.charAt(3))
          .filter(s -> s.charAt(2) == s4.charAt(1))
          .filter(s -> s.charAt(4) == s4.charAt(0))
          .forEach(s -> System.out.printf("%s %s%n", s, s4));
    } catch (IOException e) {
      //noinspection ProhibitedExceptionThrown
      throw new RuntimeException(e);
    }
  }



  private static class Counter implements Consumer<Object> {
    int count = 0;

    @Override
    public void accept(Object ignored) {
      count++;
    }

    int get() {
      return count;
    }

    @Override
    public String toString() {
      return String.valueOf(count);
    }
  }
}

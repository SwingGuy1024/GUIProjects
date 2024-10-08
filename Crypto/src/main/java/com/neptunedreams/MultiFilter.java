//#! /usr/bin/java --source 17
package com.neptunedreams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 4/20/24</p>
 * <p>Time: 5:01 PM</p>
 * <p>@author Miguel Muñoz</p>
 */
public class MultiFilter {
  private static final Map<Integer, Set<String>> lengthMap = new HashMap<>();
  
  // The longest word in the original dictionary is 28 letters.
  public static final int MAX_WORD_LENGTH = 28;

  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      System.out.printf("Usage: MultiFilter <word> [<word> ...]%n"); // NON-NLS
      return;
    }
    MultiFilter multiFilter = new MultiFilter();
    multiFilter.matchList(args);
  }
  
  // Q WSDVML NTQN KMSV LMN YQOFS UMFD NEPS CEOO LMN YQOFS UMFD QKYEXS
  // "QKYEXS", "WSDVML", "YQOFS"
  public MultiFilter() throws IOException {
    for (int i = 1; i<= MAX_WORD_LENGTH; ++i) {
      Path path = getPath(String.format("words/words_%d.txt", i));
      try (Stream<String> stream = Files.lines(path)) {
        Set<String> set = stream.collect(Collectors.toCollection(TreeSet::new));
        lengthMap.put(i, set);
      }
    }
  }

  public void matchList(String... allWords) {
    matchList(System.out::println, allWords);
  }

  public void matchList(Consumer<List<String>> consumer, String... cipherWords) {
    String cipherWord = cipherWords[0];
    String[] remainingCiphers = Arrays.copyOfRange(cipherWords, 1, cipherWords.length);
    System.out.printf("%nSearching for %s, %s%n", cipherWord, Arrays.toString(remainingCiphers)); // NON-NLS
    String[] lowRemainers = new String[remainingCiphers.length];
    int i=0;
    for (String remainer: remainingCiphers) {
      lowRemainers[i++] = remainer.toLowerCase();
    }
    Map<Character, Character> cipher = new HashMap<>();
    List<String> pastWords = new ArrayList<>();
    match(consumer, cipher, pastWords, cipherWord.toLowerCase(), lowRemainers);
  }

  private static Set<String> wordsForSize(String cipherWord) {
    int length = cipherWord.length();
    return lengthMap.get(length);
  }

  private void match(
      Consumer<List<String>> consumer,
      Map<Character, Character> cipherKey,
      List<String> pastWords,
      String cipherWord,
      String... futureWords
  ) {
    Set<String> clearWords = wordsForSize(cipherWord);

    Set<Character> newKeyChars = new HashSet<>();
    for (String clearWord: clearWords) {
      char[] clearLetters = clearWord.toCharArray();
      int index = 0;
      boolean match = true;
      for (char c: cipherWord.toCharArray()) {
        final char clearChar = clearLetters[index++];
        if (clearChar == c) {
          match = false;
          break;
        } else if (cipherKey.containsKey(clearChar) || cipherKey.containsValue(c)) {
          final Character ch = cipherKey.get(clearChar);
          if ((ch == null) || (ch != c)) {
            match = false;
            break;
          }
        } else {
          cipherKey.put(clearChar, c);
          newKeyChars.add(clearChar);
        }
      }
      if (match) {
        List<String> allWords = new ArrayList<>(pastWords.size()+1);
        allWords.addAll(pastWords);
        if (futureWords.length == 0) {
          allWords.add(clearWord);
          consumer.accept(allWords); // NON-NLS
        } else {
          allWords.add(clearWord);
          final String[] nextFutureWords = Arrays.copyOfRange(futureWords, 1, futureWords.length);
          match(consumer, cipherKey, allWords, futureWords[0], nextFutureWords); // recurse
        }
      }
      // Remove all new characters from cipherKey that were added by this clearWord.
      newKeyChars.forEach(cipherKey::remove);
      newKeyChars.clear();
    }
  }
  
//  private void match2(
//      Consumer<List<String>> consumer,
//      Map<Character, Character> cipher,
//      List<String> pastWords,
//      String cipherWord,
//      String... futureWords
//  ) {
//    
//    private final Stream<List<String>> stream = Stream.of()
//  }
  
//  private void match2(Map<Character, Character> cipher, List<String> pastWords, String cipherWord, String... futureWords) {
//    wordsForSize(cipherWord)
//        .stream()
//        .flatMap()
//  }

  private static Path getPath(String relativeFilePath) {
    // This is the only way to get the source file's path in a shebang class.
    String basePath = System.getProperty("jdk.launcher.sourcefile"); // Only works as a shebang file
    if (basePath == null) {
      basePath = System.getProperty("user.dir"); // for when you're testing before you make it a shebang file
    }
    Path parent = Path.of(basePath).getParent();
    return parent.resolve(relativeFilePath);
  }
}

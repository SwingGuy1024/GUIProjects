//#! /usr/bin/java/ --source 17
package com.neptunedreams;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
      Path path = getPath(String.format("/words/words_%d.txt", i));
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
      Map<Character, Character> cipher,
      List<String> pastWords,
      String cipherWord,
      String... futureWords
  ) {
    Set<String> clearWords = wordsForSize(cipherWord);

    for (String clearWord: clearWords) {
      Map<Character, Character> privateCipher = new HashMap<>(cipher);
      char[] clearLetters = clearWord.toCharArray();
      int index = 0;
      boolean match = true;
      for (char c: cipherWord.toCharArray()) {
        final char clearChar = clearLetters[index++];
        if (privateCipher.containsKey(clearChar) || privateCipher.containsValue(c)) {
          final Character ch = privateCipher.get(clearChar);
          if ((ch == null) || (ch != c)) {
            match = false;
            break;
          }
        } else {
          privateCipher.put(clearChar, c);
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
          match(consumer, privateCipher, allWords, futureWords[0], Arrays.copyOfRange(futureWords, 1, futureWords.length));
        }
      }
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

  public Path getPath(String path) {
    try {
      return Path.of(getResource(path));
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Should not happen", e);
    }
  }

  private URI getResource(String path) throws URISyntaxException {
    final URL resource = getClass().getResource(path);
    if (resource == null) {
      //noinspection ProhibitedExceptionThrown
      throw new NullPointerException(String.format("From %s", path));
    }
    return resource.toURI();
  }
}

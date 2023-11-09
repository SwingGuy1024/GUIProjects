package com.neptunedreams;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Class to compile a list of English Language words.</p>
 * <p>This class loads the existing word list, adds whatever words it finds on the clipboard, then writes out a new revised list.</p>
 * <p>The list can be found at {dir}/wordList/englishWords.txt, where {dir} is the System property {@code user.home}.</p>
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/21/23
 * <p>Time: 10:59 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public enum WordList {
  ;

  public static void main(String[] args) throws IOException, UnsupportedFlavorException {
    String home = System.getProperty("user.home");
    File dir = new File(home, "wordList");
    //noinspection ResultOfMethodCallIgnored
    dir.mkdir();
    File outFile = new File(dir, "englishWords.txt");
    //noinspection ResultOfMethodCallIgnored
    outFile.createNewFile();
    
    Path path = outFile.toPath();
    Set<String> existingWords;
    try (final Stream<String> lines = Files.lines(path)) {
      existingWords = lines.collect(Collectors.toCollection(TreeSet::new));
    }
    
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    String data = clipboard.getData(DataFlavor.selectionHtmlFlavor).toString();
    HtmlTokenizer tokenizer = new HtmlTokenizer(data);
    int count = 0;
    while (tokenizer.hasMoreTokens()) {
      String word = tokenizer.nextToken();
      if ((!word.isEmpty()) && allLetters(word)) {
        System.out.println(word);
        existingWords.add(word);
        count++;
      }
    }
    System.out.printf("Found %d words%n", count); // NON-NLS

    File tempFile = File.createTempFile("tmp", ".txt", dir);
    System.out.printf("Temp File: <%s>%n", tempFile.getName()); // NON-NLS
    final FileWriter out = new FileWriter(tempFile, true);
    try (BufferedWriter writer = new BufferedWriter(out)) {
      for (String word: existingWords) {
        writer.write(word);
        writer.newLine();
      }
    }
    Files.move(tempFile.toPath(), outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
  }

  private static boolean allLetters(String word) {
    if (word.length() < 2) {
      return false;
    }
    for (int i=0; i<word.length(); ++i) {
      char c = word.charAt(i);
      if (!Character.isLetter(c) || !Character.isLowerCase(c)) {
        return false;
      }
    }
    return true;
  }
  
  @SuppressWarnings("MagicCharacter")
  private static class HtmlTokenizer {
    private final String source;
    private int index = 0;
    private boolean hasNext = true;
    HtmlTokenizer(String source) {
      this.source = source;
    }
    
    boolean hasMoreTokens() { return hasNext; }
    
    String nextToken() {
      boolean inWord = false;
      StringBuilder builder = new StringBuilder();
      while (index < source.length()) {
        char c = source.charAt(index++);
        while (c == '<') {
          if (inWord) {
            index--;
            return builder.toString();
          }
          c = advanceToEndOfTag();
        }
        inWord = true;
        builder.append(c);
      }
      hasNext = false;
      return "";
    }
    
    private char advanceToEndOfTag() {
      while (index < source.length()) {
        char c = source.charAt(index++);
        if (c == '>') {
          if (index < source.length()) {
            return source.charAt(index++);
          }
        }
      }
      hasNext = false;
      return '\0';
    }
  }
}

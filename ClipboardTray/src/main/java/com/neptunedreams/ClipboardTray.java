package com.neptunedreams;

import java.awt.AWTException;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Text utilities for the clipboard tray.</p>
 * <p>The parameters in some of these methods code follow a naming convention. Some of the menu items 
 * perform a line-by-line operation, while others don't. For methods that perform
 * line-by-line operation, there are two sets of methods that transform a String to another String. Some of
 * these methods operate on many lines, while others only operate on a single line of tet at a time. And
 * for each operation, there may be two methods with similar names. So for the methods that operate on many
 * lines, the String input parameter will be called {@code lines}, while the method that operates on a single
 * line at a time will have a String input parameter called {@code lineIn}.</p>
 * 
 * <p><b>To Install:</b> Drag this into the Utilities folder.
 * On a Macintosh, the first time, you should then open <b>System Settings:General:Login Items & Extensions</b> and add it to the list of <b>Open at Login</b>items.</p>
 * <p>I don't remember how to add it to the System Tray on Windows, but it's not hard.</p>
 * 
 * <p>This was originally written to go on a System Tray, which is supported on Windows and Macintosh. I don't know
 * if it's supported on Linux, but it's not supported on Chromebook. So on that platform (or any that doesn't 
 * support the SystemTray) it puts the controls into a Frame with "Always on Top" turned on.</p>
 *
 * <p>Icon: <a href="https://clipartix.com/hummingbird-clipart/">Hummingbird Clip Art</a></p>
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 9/9/24</p>
 * <p>Time: 12:42 AM</p>
 * <p>@author Miguel Muñoz</p>
 */
public enum ClipboardTray {
  ;

  public static final char NEW_LINE = '\n';
  public static final char SPACE = ' ';
  public static final char TAB = '\t';

  @SuppressWarnings("StringConcatenationMissingWhitespace")
  private static final @NonNls String substitutionSource = 
      // Generated using Artificial Stupidity. I had fix many mistakes.
      "U+1D4BAU+212FℯSCRIPT SMALL E" +
      "U+1D455U+210EℎMATHEMATICAL ITALIC SMALL H" +
      "U+1D49DU+212CℬSCRIPT CAPITAL B" +
      "U+1D4A0U+2130ℰSCRIPT CAPITAL E" +
      "U+1D4A1U+2131ℱSCRIPT CAPITAL F" +
      "U+1D4A3U+210BℋSCRIPT CAPITAL H" +
      "U+1D4A4U+2110ℐSCRIPT CAPITAL I" +
      "U+1D4A7U+2112ℒSCRIPT CAPITAL L" +
      "U+1D4A8U+2133ℳSCRIPT CAPITAL M" +
      "U+1D4ADU+211BℛSCRIPT CAPITAL R" +
      "U+1D4BAU+212F\u212fSCRIPT SMALL E" +
      "U+1D4BCU+210AℊSCRIPT SMALL G" +
      "U+1D4C4U+2134ℴSCRIPT SMALL O" +
      "U+1D506U+212DℭBLACK-LETTER CAPITAL C" +
      "U+1D50BU+210CℌBLACK-LETTER CAPITAL H" +
      "U+1D50CU+2111ℑBLACK-LETTER CAPITAL I" +
      "U+1D515U+211CℜBLACK-LETTER CAPITAL R" +
      "U+1D51DU+2128ℨBLACK-LETTER CAPITAL Z" +
      "U+1D53AU+2102ℂDOUBLE-STRUCK CAPITAL C (Complex Numbers)" +
      "U+1D53FU+210DℍDOUBLE-STRUCK CAPITAL H (Quaternions)" +
      "U+1D545U+2115ℕDOUBLE-STRUCK CAPITAL N (Natural Numbers)" +
      "U+1D547U+2119ℙDOUBLE-STRUCK CAPITAL P" +
      "U+1D548U+211AℚDOUBLE-STRUCK CAPITAL Q (Rational Numbers)" +
      "U+1D549U+211DℝDOUBLE-STRUCK CAPITAL R (Real Numbers)" +
      "U+1D551U+2124ℤDOUBLE-STRUCK CAPITAL Z (Integers)"
      ;
  
  /*
    To Add:
    1D7CE Bold Digits
    1D7D8 Double Struck Digits 
    1D7E2 San Serif Digits
    1D7EC Sans Serif Bold Digits
    1D7F6 Monospace Digits
   */
  private static final Map<Integer, Character> substitutions = makeSubstitutions();
  public static final int HEX_RADIX = 16;

  private static Map<Integer, Character> makeSubstitutions() {
    Map<Integer, Character> subs = new HashMap<>();
    int where = 0;
    while (where >= 0) {
      where += 2;
      String codePointSource = substitutionSource.substring(where, where + 5);
      long codePoint = Long.parseLong(codePointSource, HEX_RADIX);
      int lowSurrogate = toSurrogates(codePoint)[1]; // We don't need the first value, which will always be the same.
      where += 7;
      String charSource = substitutionSource.substring(where, where + 4);
      int character = Integer.parseInt(charSource, HEX_RADIX);
      char replacement = substitutionSource.charAt(where+4);

      // AS-Generated substitutionSource was wrong at Black-letter capital R, among others. 
      // (Its character was Script Capital R, but the 4-digit code was correct.)
      // this assertion triggers whenever the 4-digit describing the replacement character (following the second "U+"
      // on each line) doesn't match the numeric value of the character that follows.
      assert character == replacement : 
          String.format(
              "Mismatch: (%c) 0x%04x != 0x.%04x (%c) %s",
              character,
              character,
              (int) replacement,
              replacement, describe(where)
          );
      subs.put(lowSurrogate, (char)character);
      where = substitutionSource.indexOf("U+", where);
    }
    return subs;
  }
  
  private static String describe(int where) {
    int endOfDescription = substitutionSource.indexOf("U+", where);
    if (endOfDescription == -1) {endOfDescription = substitutionSource.length();}
    return substitutionSource.substring(where+5, endOfDescription);
    
  }

  public static void main(String[] args) throws AWTException {
    if (SystemTray.isSupported()) {
      SystemTray systemTray = SystemTray.getSystemTray();

      systemTray.add(getTextTray());
    } else {
      JFrame frame = new JFrame();
      frame.setAlwaysOnTop(true);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JPanel mainPanel = new JPanel(new GridLayout(0, 1));
      FilterOwner filterOwner = new FilterOwner(mainPanel);
      addFilters(filterOwner);
      frame.setContentPane(mainPanel);
      frame.pack();
      frame.setVisible(true);
    }
  }

  private static TrayIcon getTextTray() {
    ImageIcon imageIcon = getImageIcon();
//    ImageIcon imageIcon = getImageIcon("/hummingbirdIcon8000.png");
    TrayIcon trayIcon = new TrayIcon(imageIcon.getImage(), "Clipboard Tools");
    trayIcon.setImageAutoSize(true);
    PopupMenu wrappedPopupMenu = new PopupMenu();
    FilterOwner filterOwner = new FilterOwner(wrappedPopupMenu);
    
    trayIcon.setPopupMenu(wrappedPopupMenu);
    addFilters(filterOwner);
    wrappedPopupMenu.addSeparator();
    wrappedPopupMenu.add(exitItem());
    return trayIcon;
  }

  private static void addFilters(FilterOwner filterOwner) {
    addCharFilter(filterOwner, "To Plain Text", c -> c);
    addCharFilter(filterOwner, "To Upper Case", Character::toUpperCase);
    addCharFilter(filterOwner, "To Lower Case", Character::toLowerCase);
    addStringFilter(filterOwner, "To Title Case", ClipboardTray::toTitleCase);
    addStringFilter(filterOwner, "To Sentence Case", ClipboardTray::toSentenceCase);
    addStringFilter(filterOwner, "Combine Lines", ClipboardTray::combineLines);
    addLineFilter(filterOwner, "Indent 2 spaces", t -> prePadLine(t, "  "));
    addLineFilter(filterOwner, "Indent 4 spaces", t -> prePadLine(t, "    "));
    addLineFilter(filterOwner, "Unordered List <li>", t -> wrapLine(t, "li"));
    addLineFilter(filterOwner, "Paragraph <p>", t -> wrapLine(t, "p"));
    addStringFilter(filterOwner, "Counts", ClipboardTray::stats);
    addStringFilter(filterOwner, "To Table…", ClipboardTray::toTable);
    addLineFilter(filterOwner, "Email Indent >", ClipboardTray::toEmailLine);
    filterOwner.addSeparator(); // 𝘚𝘢𝘯𝘴 𝘐𝘵𝘢𝘭𝘪𝘤
    addStringFilter(filterOwner, "To 𝗦𝗮𝗻𝘀 𝗕𝗼𝗹𝗱 Surrogates", ClipboardTray::toSansBoldSurrogates);
    addStringFilter(filterOwner, "To 𝘚𝘢𝘯𝘴 𝘐𝘵𝘢𝘭𝘪𝘤 Surrogates", ClipboardTray::toSansItalicSurrogates);
    addStringFilter(filterOwner, "To 𝙎𝙖𝙣𝙨 𝘽𝙤𝙡𝙙 𝙄𝙩𝙖𝙡𝙞𝙘 Surrogates", ClipboardTray::toSansBoldItalicSurrogates);
    addStringFilter(filterOwner, "To 𝐒𝐞𝐫𝐢𝐟 𝐁𝐨𝐥𝐝 Surrogates",  ClipboardTray::toSerifBoldSurrogates);
    addStringFilter(filterOwner, "To 𝑆𝑒𝑟𝑖𝑓 𝐼𝑡𝑎𝑙𝑖𝑐 Surrogates", ClipboardTray::toSerifItalicSurrogates);
    addStringFilter(filterOwner, "To 𝑺𝒆𝒓𝒊𝒇 𝑩𝒐𝒍𝒅 𝑰𝒕𝒂𝒍𝒊𝒄 Surrogates", ClipboardTray::toSerifBoldItalicSurrogates);
    addStringFilter(filterOwner, "To 𝒮𝒸𝓇𝒾𝓅𝓉 Surrogates", ClipboardTray::toScriptSurrogates);
    addStringFilter(filterOwner, "To 𝓑𝓸𝓵𝓭 𝓢𝓬𝓻𝓲𝓹𝓽 Surrogates", ClipboardTray::toScriptBoldSurrogates);
    addStringFilter(filterOwner, "To 𝔻𝕠𝕦𝕓𝕝𝕖-𝕊𝕥𝕣𝕦𝕔𝕜 Surrogates", ClipboardTray::toDoubleStruckSurrogates);
    addStringFilter(filterOwner, "To 𝔅𝔩𝔞𝔠𝔨 𝔏𝔢𝔱𝔱𝔢𝔯 Surrogates", ClipboardTray::toBlackLetterSurrogates);
    addStringFilter(filterOwner, "To 𝙼𝚘𝚗𝚘𝚜𝚙𝚊𝚌𝚎𝚍 Surrogates", ClipboardTray::toMonospaceSurrogates);
  }

  @NotNull
  private static ImageIcon getImageIcon() {
    String iconName = "/hummingbird.png";
    final URL resource = ClipboardTray.class.getResource(iconName);
    return new ImageIcon(Objects.requireNonNull(resource));
  }


  /**
   * Adds a MenuItem that filters text by character.
   * @param popupMenu The popupMenu
   * @param name The text of the Menu Item
   * @param charFunction A function that transforms each individual character in the input String
   */
  private static void addCharFilter(FilterOwner popupMenu, String name, IntUnaryOperator charFunction) {
    
    ActionControl menuItem = popupMenu.newActionControl(name);
    menuItem.addActionListener(e -> processClipboardData(charFunction));
    popupMenu.add(menuItem);
  }

  /**
   * Adds a MenuItem that filters the input text as a whole
   * @param popupMenu The popupMenu
   * @param name The text of the Menu Item
   * @param stringFunction A function that transforms the entire input text from the clipboard
   */
  private static void addStringFilter(FilterOwner popupMenu, String name, UnaryOperator<String> stringFunction) {
    ActionControl menuItem = popupMenu.newActionControl(name);
    menuItem.addActionListener(e -> processClipboardData(stringFunction));
    popupMenu.add(menuItem);
  }

  /**
   * Adds a MenuItem that filters each line of text, but leaves the line breaks alone.
   * @param popupMenu The popupMenu
   * @param name The text of the Menu Item
   * @param lineFunction A function that transforms a single line of the input text
   */
  private static void addLineFilter(FilterOwner popupMenu, String name, UnaryOperator<String> lineFunction) {
    ActionControl menuItem = popupMenu.newActionControl(name);

    Function<String, String> allLinesProcessor = t -> transformLines(t, lineFunction);
    menuItem.addActionListener(e -> processClipboardData(allLinesProcessor));
    popupMenu.add(menuItem);
  }
  
  private static void processClipboardData(IntUnaryOperator function) {
    processClipboardData(toStringFunction(function));
  }
  
  private static Function<String, String> toStringFunction(IntUnaryOperator operator) {
    return s -> s
        .chars()
        .map(operator)
        .boxed()
        .collect(StringCollector.instance);
  }

  private static MenuItem exitItem() {
    MenuItem exitItem = new MenuItem("Exit");
    exitItem.addActionListener((e) -> System.exit(0));
    return exitItem;
  }

  private static void processClipboardData(Function<String, String> stringFunction) { 
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    try {
      String contents = clipboard.getData(DataFlavor.stringFlavor).toString();
      final String result = stringFunction.apply(contents);
      // Allow some MenuItems to avoid replacing clipboard data by returning null. 
      if (result != null) {
        StringSelection stringSelection = new StringSelection(result);
        clipboard.setContents(stringSelection, stringSelection);
      }
    } catch (UnsupportedFlavorException | IOException shouldNotHappen) {
      throw new IllegalStateException(shouldNotHappen);
    } catch (CancelledOperationException ignore) { } // Do Nothing
  }

  /**
   * Transforms the entire text by applying the lineTransformer to each line of text.
   * @param lines All the lines of text
   * @param lineTransformer The line function to transform each line
   * @return The entire transformed text
   */
  private static String transformLines(String lines, Function<String, String> lineTransformer) {
    StringBuilder builder = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new StringReader(lines))) {
      String theLine = reader.readLine();
      while (theLine != null) {
        builder
            .append(lineTransformer.apply(theLine))
            .append(NEW_LINE);
        theLine = reader.readLine();
      }
    } catch(IOException ioe) {
      throw new IllegalStateException(ioe);
    }
    return builder.toString();
  }
  
  private static String toEmailLine(String lineIn) {
    return String.format("> %s", lineIn);
  }
  
  private static String toTitleCase(String lines) {
    // Prepositions, articles, and conjunctions 
    String[] prepArray = PREPS.split("\n");
    Set<String> prepSet = new HashSet<>(Arrays.asList(prepArray));
    String[] acronymArray = ACRONYMS.split("\n");
    Set<String> acronymSet = new HashSet<>(Arrays.asList(acronymArray));
    StringBuilder builder = new StringBuilder();
    List<String> tokens = new LinkedList<>();
    StringTokenizer tokenizer = new StringTokenizer(lines, " .,?!()@-+=<>/\n\r\t", true);
    while (tokenizer.hasMoreTokens()) {
      tokens.add(tokenizer.nextToken());
    }
    Iterator<String> itr = tokens.iterator();
    String firstToken = itr.next();
    if (acronymSet.contains(firstToken.toLowerCase())) {
      builder.append(firstToken.toUpperCase());
    } else {
      appendTitleCaseWord(firstToken, builder); // First word is always capitalized
    }
    while (itr.hasNext()) {
      String word = itr.next();
      final String lowWord = word.toLowerCase();
      if (prepSet.contains(lowWord)) {
        builder.append(lowWord);
      } else if  (acronymSet.contains(lowWord)) {
        builder.append(lowWord.toUpperCase());
      } else {
        appendTitleCaseWord(word, builder);
      }
    }
    return builder.toString();
  }

  private static String toSentenceCase(String lines) {
    StringBuilder builder = new StringBuilder();
    StringTokenizer tokenizer = new StringTokenizer(lines, ".?!", true);
    while (tokenizer.hasMoreTokens()) {
      String sentence = tokenizer.nextToken();
      boolean sentenceStart = true;
      for (char c : sentence.toCharArray()) {
        if (Character.isAlphabetic(c)) {
          if (sentenceStart) {
            builder.append(Character.toUpperCase(c));
            sentenceStart = false;
          } else {
            builder.append(Character.toLowerCase(c));
          }
        } else {
          builder.append(c);
        }
      }
    }
    return builder.toString();
  }

  // Basic theory of Unicode surrogate: https://www.johndcook.com/blog/2025/03/09/unicode-surrogates/
  // Slightly out-of-date code chart: https://www.unicode.org/charts/PDF/U1D400.pdf
  private static final char HIGH_SURROGATE = 0xd835;
  public static final char A = 'A';
  private static final int  BOLD_UPPER_DELTA = toDelta(0x1D5D4);
  public static final char a = 'a';
  private static final int  ITALIC_UPPER_DELTA = toDelta(0x1D608);
  private static final int  BOLD_ITALIC_UPPER_DELTA = toDelta(0x1D63C);
  private static final int  ITALIC_SERIF_UPPER_DELTA = toDelta(0x1D434);
  private static final int  BOLD_ITALIC_SERIF_UPPER_DELTA = toDelta(0x1D468);
  private static final int  BOLD_SERIF_UPPER_DELTA = toDelta(0x1D400);
  private static final int  BOLD_SCRIPT_UPPER_DELTA = toDelta(0x1D4D0);
  private static final int  SCRIPT_UPPER_DELTA = toDelta(0x1D49C); 
  private static final int  BLACK_LETTER_UPPER_DELTA = toDelta(0x1D504);
  private static final int  MONOSPACE_UPPER_DELTA = toDelta(0x1D670); 
  private static final int  DOUBLE_STRUCK_UPPER_DELTA = toDelta(0x1D538);
  private static final int  SERIF_BOLD_NUMERIC_DELTA = 0xDFCE - '0';
  private static final int  DOUBLE_STRUCK_NUMERIC_DELTA = SERIF_BOLD_NUMERIC_DELTA + 10; // 0xDFD8
  private static final int  SANS_NUMERIC_DELTA = DOUBLE_STRUCK_NUMERIC_DELTA + 10; // 0xDFE2
  private static final int  SANS_BOLD_NUMERIC_DELTA = SANS_NUMERIC_DELTA + 10; // 0XDFEC
  private static final int  MONOSPACED_NUMERIC_DELTA = SANS_BOLD_NUMERIC_DELTA + 10;

  private static String toSansBoldSurrogates(String lines) {
    return toSurrogates(lines, BOLD_UPPER_DELTA, SANS_BOLD_NUMERIC_DELTA);
  }

  private static String toSansItalicSurrogates(String lines) {
    return toSurrogates(lines, ITALIC_UPPER_DELTA, SANS_NUMERIC_DELTA); // There are no italic digits
  }
  
  private static String toSansBoldItalicSurrogates(String lines) {
    return toSurrogates(lines, BOLD_ITALIC_UPPER_DELTA, SANS_BOLD_NUMERIC_DELTA);
  }
  
  private static String toSerifBoldSurrogates(String lines) {
    return toSurrogates(lines, BOLD_SERIF_UPPER_DELTA, SERIF_BOLD_NUMERIC_DELTA);
  }

  private static String toSerifItalicSurrogates(String lines) {
    return toSurrogates(lines, ITALIC_SERIF_UPPER_DELTA, SERIF_BOLD_NUMERIC_DELTA);
  }

  private static String toSerifBoldItalicSurrogates(String lines) {
    return toSurrogates(lines, BOLD_ITALIC_SERIF_UPPER_DELTA, SERIF_BOLD_NUMERIC_DELTA);
  }
  
  private static String toScriptSurrogates(String lines) {
    return toSurrogates(lines, SCRIPT_UPPER_DELTA, SANS_NUMERIC_DELTA);
  }

  private static String toScriptBoldSurrogates(String lines) {
    return toSurrogates(lines, BOLD_SCRIPT_UPPER_DELTA, SANS_BOLD_NUMERIC_DELTA);
  }

  private static String toDoubleStruckSurrogates(String lines) {
    return toSurrogates(lines, DOUBLE_STRUCK_UPPER_DELTA, DOUBLE_STRUCK_NUMERIC_DELTA);
  }

  private static String toBlackLetterSurrogates(String lines) {
    return toSurrogates(lines, BLACK_LETTER_UPPER_DELTA, SANS_NUMERIC_DELTA);
  }
  
  private static String toMonospaceSurrogates(String lines) {
    return toSurrogates(lines, MONOSPACE_UPPER_DELTA, MONOSPACED_NUMERIC_DELTA);
  }

  private static String toSurrogates(String lines, int upperDelta, int numericDelta) {
    @SuppressWarnings("MagicNumber")
    int lowerDelta = (upperDelta + 26) + (A - a); // 26 skips past the 26 upper case letters.
    StringBuilder builder = new StringBuilder();
    for (char c : lines.toCharArray()) {
      if (isAtoZUpperCase(c)) {
        appendSurrogate(c, upperDelta, builder);
      } else if (isAtoZLowerCase(c)) {
        appendSurrogate(c, lowerDelta, builder);
      } else if (isDigit(c)) {
        // There are no italic or bold italic digits.
        appendSurrogate(c, numericDelta, builder);
      } else {
        builder.append(c);
      }
    }
    return builder.toString();
  }
  
  private static void appendSurrogate(char c, int delta, StringBuilder builder) {
    int secondCharAsInt = (c + delta);
    char secondChar = (char) secondCharAsInt;
    if (substitutions.containsKey(secondCharAsInt)) {
      /*
        This assertion used to work. Then it stopped working. I have no idea why. The Character.isSurrogatePair()
        method now returns true for all pairs, even when the code points are undefined. I don't know why because
        I didn't change the version of Java in the meantime.
        // This tests if the character we're about it get a substitute for is actually a bad character.
        // Sometimes the code chart is wrong.
        assert !Character.isSurrogatePair(HIGH_SURROGATE, secondChar) 
            : String.format("Bad second character: \\u%04x for %c", secondCharAsInt, c);
      */
      builder.append(substitutions.get(secondCharAsInt));
    } else {
      builder.append(HIGH_SURROGATE);
      builder.append(secondChar);
    }
  }
  
  @SuppressWarnings("MagicNumber")
  private static int toDelta(int codePoint) {
    return ((codePoint - 0x10000) + 0x800) - A;
  }
  
  private static boolean isAtoZUpperCase(char c) {
    //noinspection MagicCharacter
    return (c >= A) && (c <= 'Z');
  }
  
  private static boolean isAtoZLowerCase(char c) {
    //noinspection MagicCharacter
    return (c >= a) && (c <= 'z');
  }
  
  private static boolean isDigit(char c) {
    //noinspection MagicCharacter
    return (c >= '0') && (c <= '9');
  }
  
  private static void appendTitleCaseWord(String word, StringBuilder builder) {
    final char firstLetter = word.charAt(0);
    if (Character.isLetter(firstLetter)) {
      builder.append(Character.toTitleCase(firstLetter));
      builder.append(word.substring(1).toLowerCase());
    } else {
      builder.append(word);
    }
  }

  private static String combineLines(String lines) {
    StringBuilder builder = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new StringReader(lines))) {
      String theLine = reader.readLine();
      while (theLine != null) {
        theLine = theLine.trim();

        // test for empty to handle multiple blank lines correctly.
        if (!theLine.isEmpty()) {
          builder
              .append(theLine)
              .append(SPACE);
        }
        theLine = reader.readLine();
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return builder.toString().trim();
  }

  private static @Nullable String stats(String lines) {
    int lineCount = 0;
    final String trim = lines.trim();
    try (BufferedReader reader = new BufferedReader(new StringReader(trim))) {
      String theLine = reader.readLine();
      while (theLine != null) {
        lineCount++;
        theLine = reader.readLine();
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    // We don't include $¢%,.() because they're always joined to a word or number. By not including period,
    // we treat each of these as a single word: $500.00 and google.com.
    StringTokenizer tokenizer = new StringTokenizer(trim, "\n \t?!");
    int wordCount = tokenizer.countTokens();
    String message = String.format(
        "Characters: %d\n\nTrimmed:\nCharacters: %d\nWords: %d\nLines: %d",
        lines.length(), trim.length(), wordCount, lineCount);
    JOptionPane.showMessageDialog(null, message);
    return null;
  }

  private static @Nullable String toTable(String lines) {
    if (lines.isEmpty()) {
      return null;
    }
    final int columnCount = getColumnCount();

    StringBuilder builder = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new StringReader(lines.trim()))) {
      String theLine = reader.readLine();
      outerLoop: while (theLine != null) {
        for (int i = 1; i < columnCount; i++) {
          builder.append(theLine);
          builder.append(TAB);
          theLine = reader.readLine();
          if (theLine == null) {
            break outerLoop;
          }
        }
        builder.append(theLine);
        builder.append(NEW_LINE);
        theLine = reader.readLine();
      }
    } catch (IOException ioe) { throw new IllegalStateException(ioe); }
    return builder.toString();
  }

  private static int getColumnCount() {
    boolean invalid = true;
    int columnCount = -1;
    while (invalid) {
      String tValue = JOptionPane.showInputDialog(null, "How Many Columns?", 5);
      if(tValue == null) {
        throw new CancelledOperationException(); 
      }
      try {
        columnCount = Integer.parseInt(tValue);
        if (columnCount > 0) {
          invalid = false;
        } else {
          JOptionPane.showMessageDialog(null, "Column Count: %d must be greater than zero".formatted(columnCount), "Notice", JOptionPane.INFORMATION_MESSAGE);
        }
      } catch (NumberFormatException ignored) {
        JOptionPane.showMessageDialog(null, "Columns must be an integer");
      }
    }
    return columnCount;
  }

  private static String wrapLine(String lineIn, String tag) {
    return wrapLine(lineIn, String.format("<%s>", tag), String.format("</%s>", tag));
  }
  
  private static @NonNls String wrapLine(String lineIn, String openTag, String closeTag) {
    return openTag + lineIn + closeTag;
  }
  
  @SuppressWarnings("StringConcatenation")
  private static String prePadLine(String lineIn, String pad) { return pad + lineIn; }

  public static final int LOW_MASK = 0x03FF;
  public static final int LOW_HEAD = 0xDC00;

  /**
   * <p>Convert a UTF-16 code point to a pair of surrogate characters, with the high surrogate first. For the
   * characters currently handled by this tool, the high surrogate will always evaluate to 0xd835, but I may deal
   * with other characters later as I learn more about them.</p>
   * @param codePoint The UTF-16 digit to convert
   * @return A pair of characters, with the high surrogate at [0] and the low surrogate at [1].
   */
  @SuppressWarnings({"LocalCanBeFinal", "MagicNumber"})
  private static int[] toSurrogates(final long codePoint) {
    int lowTen = (int) (codePoint & LOW_MASK); // Low ten digits
    int fullLowTen = lowTen | LOW_HEAD;

    // Here, we only capture six bits of the high-ten bits. But the seventh bit is the leading digit in the
    // five-hexDigit code point 0x1_Dxxx which we need to strip out.
    int highTen = (int) ((codePoint & 0xFC00) >>> 10);
    int fullHighTen = highTen | 0xD800;
    return new int[]{fullHighTen, fullLowTen};
  }

  /**
   * <p>A FilterControl is either a PopupMenu or a JButton, depending on whether a SystemTray is supported
   * on this platform.</p>
   */
  private static class FilterOwner {
    private final boolean isPopupMenu;
    private final @Nullable PopupMenu popupMenu;
    private final @Nullable JPanel panel;
    FilterOwner(@NotNull PopupMenu popupMenu) {
      isPopupMenu = true;
      this.popupMenu = popupMenu;
      this.panel = null;
    }
    
    FilterOwner(@NotNull JPanel panel) {
      isPopupMenu = false;
      this.panel = panel;
      this.popupMenu = null;
    }
    
    @SuppressWarnings("DataFlowIssue")
    private void add(ActionControl actionControl) {
      if (isPopupMenu) {
        assert popupMenu != null;
        popupMenu.add((MenuItem) actionControl);
      } else {
        assert panel != null;
        panel.add((JButton) actionControl);
      }
    }
    
    ActionControl newActionControl(String name) {
      if (isPopupMenu) {
        return new CTMenuItem(name);
      } else {
        return new CTButton(name);
      }
    }
    
    void addSeparator() {
      if (popupMenu != null) {
        popupMenu.addSeparator();
      } else if (panel != null) {
        panel.add(Box.createVerticalStrut(8));
      }
    }
  }
  
  @SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
  private interface ActionControl {
    void addActionListener(ActionListener listener);
  }
  
  private static class CTMenuItem extends MenuItem implements ActionControl {
    CTMenuItem(String name) { super(name); }
  }

  private static class CTButton extends JButton implements ActionControl {
    CTButton(String name) { super(name); }
  }

  /**
   * <p>Immutable Collector to use when using an IntStream or{@literal Stream<Integer>} to filter text. I wrote this
   * because the {@code String.chars()} method returns an {@code IntStream}, which doesn't have a method that takes 
   * a Collector, which would look something like this:</p>
   * <pre>
   *   {@literal <R, A> R collect(Collector<? super T, A, R> collector);}
   * </pre>
   * <p>Instead, it has this clumsier method:</p>
   * <pre>
   * {@literal <R> R collect(
   *     Supplier<R> supplier,
   *     ObjIntConsumer<R> accumulator,
   *     BiConsumer<R, R> combiner);}
   * </pre>
   * <p>(This is because there are no Collectors that work with primitive types like {@code int}.)</p>
   * <p>Here's an example. To remove all characters from a String that match the specified {@code IntPredicate},
   * we can use this StringCollector to write this:</p>
   * <pre>
   *   private String customFilterOut(String input, IntPredicate cFilter) {
   *     return input.chars()  // returns an IntStream
   *       .filter(cFilter)
   *       .boxed()            // converts IntStream to a{@literal Stream<Integer>}
   *       .collect(StringCollector.instance());
   *   }
   * </pre>
   *
   * <p>Without this StringCollector, it's still doable, but it's more verbose, clumsier, and harder to remember:</p>
   *
   * <pre>
   *   public static String customFilterOut(String input, IntPredicate cFilter) {
   *     return input.chars()
   *       .filter(cFilter)
   *       .collect(
   *           StringBuilder::new,
   *           (sb, i) -> sb.append((char) i),
   *           (sb1, sb2) -> sb1.append(sb2.toString()) )
   *       .toString();
   *   }
   * </pre>
   *
   * <p>Created by IntelliJ IDEA.</p>
   * <p>Date: 6/22/22</p>
   * <p>Time: 5:38 PM</p>
   *
   * @author Miguel Muñoz
   * @see #filterString(String, IntPredicate)
   */
  @SuppressWarnings({"unused", "UnnecessaryUnicodeEscape"})
  private static final class StringCollector implements Collector<Integer, StringBuilder, String> {
    private final Supplier<StringBuilder> supplier = StringBuilder::new;
    private final BiConsumer<StringBuilder, Integer> accumulator = (sb, i) -> sb.append((char) i.intValue());
    private final BinaryOperator<StringBuilder> combiner = (t, u) -> t.append(u.toString());
    private final Function<StringBuilder, String> finisher = StringBuilder::toString;
    private final Set<Characteristics> characteristics = Collections.emptySet();

    private static final StringCollector instance = new StringCollector();

    @Override
    public Supplier<StringBuilder> supplier() {
      return supplier;
    }

    @Override
    public BiConsumer<StringBuilder, Integer> accumulator() {
      return accumulator;
    }

    @Override
    public BinaryOperator<StringBuilder> combiner() {
      return combiner;
    }

    @Override
    public Function<StringBuilder, String> finisher() {
      return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return characteristics;
    }

    public static StringCollector instance() {
      return instance;
    }

    /**
     * <p>Filter out specified characters in a String based on the specified IntPredicate,
     * using a sequential stream. For example, to remove all white space from a String, you would write this:</p>
     * <pre>
     *   String input = ...
     *   String cleanedInput = StringCollector.filterString(input,{@literal ((Predicate<Character>) Character::isWhitespace).negate())}
     * </pre>
     * <p>or</p>
     * <pre>
     *   String input = ...
     *   String cleanedInput = StringCollector.filterString(input, negate(Character::isWhitespace))
     * </pre>
     *
     * @param input  The input String
     * @param filter Determines which characters to keep. This should return false for characters to reject.
     * @return A new String with only the characters that matched the provided {@code filter}
     */
    public static String filterString(String input, IntPredicate filter) {
      return input
          .chars()
          .sequential()
          .boxed()
          .collect(instance);
    }

    /**
     * <p>Convenience method to negate a predicate that's less verbose. This way, we can write this</p>
     * <pre>
     *   stream.filter(negate(Character::isWhitespace)) ...
     * </pre>
     * <p>instead of this</p>
     * <pre>
     *  {@literal stream.filter((Predicate<Character>) Character::isWhitespace)}.negate() ...
     * </pre>
     *
     * @param p A predicate
     * @return The negated predicate
     */
    public static <T> Predicate<T> negate(Predicate<T> p) {
      return p.negate();
    }

    /**
     * <p>Convenience method to negate an {@code IntPredicate} that's less verbose. This way, we can write this</p>
     * <pre>
     *   stream.filter(negateI(Character::isWhitespace)) ...
     * </pre>
     * <p>instead of this</p>
     * <pre>
     *   stream.filter((IntPredicate) Character::isWhitespace).negate() ...
     * </pre>
     *
     * @param p A predicate
     * @return The negated predicate
     */
    public static IntPredicate negateI(IntPredicate p) {
      return p.negate();
    }

    /**
     * <p>Remaps all characters matching the predicate to the specified replacement character</p>
     * <p>For example, to replace all newline characters with spaces, you can do this:</p>
     * <pre>
     *   public static String replaceNewLineWithSpace(String input) {
     *     return reMapString(input, c-> (c=='\n') || (c=='\r'), ' ');
     *   }
     * </pre>
     *
     * @param input       The String to remap
     * @param filter      The predicate filter. Characters for which this returns true get remapped.
     * @param replacement The replacement character
     * @return The remapped String
     */
    public static String reMapString(String input, IntPredicate filter, char replacement) {
      return input
          .chars()
          .sequential()
          .map(i -> filter.test(i) ? replacement : i)
          .boxed()
          .collect(instance);
    }
  }
  
  private static class CancelledOperationException extends RuntimeException { }

  // Articles, prepositions and conjunctions:
  private static final String PREPS = """
a
aboard
about
above
absent
across
after
against
aloft
along
alongside
amid
amidst
and
mid
midst
among
amongst
anti
apropos
around
round
as
astride
at
atop
bar
barring
before
behind
below
beneath
beside
besides
between
beyond
but
by
chez
circa
come
concerning
contra
counting
cum
despite
down
during
effective
ere
except
excepting
excluding
failing
following
for
from
in
including
inside
into
less
like
minus
modulo
near
nearer
nearest
next
notwithstanding
of
off
offshore
on
onto
opposite
out
if
outside
over
o'er
pace
past
pending
per
plus
post
pre
pro
qua
re
regarding
respecting
sans
save
saving
since
sub
than
the
through
thru
throughout
thruout
till
times
to
toward
towards
under
underneath
unlike
until
unto
up
upon
versus
vs
via
with
w
within
without
either
or
neither
nor
both
whether
so
not
rather
once
when
whenever
while
yet
though
""";

  private static final String ACRONYMS = """
la
dj
""";
}


/*
Missing Code Points:
U+1D4BAU+212FℯSCRIPT SMALL EU+1D4BCU+210AℊSCRIPT SMALL GU+1D4C4U+2134ℴSCRIPT SMALL O
U+1D4A0U+212CℬSCRIPT CAPITAL BU+1D4A1U+2130ℰSCRIPT CAPITAL EU+1D4A3U+2131ℱSCRIPT CAPITAL FU+1D4A4U+210BℋSCRIPT CAPITAL HU+1D4A7U+2110ℐSCRIPT CAPITAL IU+1D4A8U+2112ℒSCRIPT CAPITAL LU+1D4ADU+211BℛSCRIPT CAPITAL R
U+1D506U+212DℭBLACK-LETTER CAPITAL CU+1D50BU+210CℌBLACK-LETTER CAPITAL HU+1D50CU+2111ℑBLACK-LETTER CAPITAL IU+1D515U+211CℛBLACK-LETTER CAPITAL RU+1D51DU+2128ℨBLACK-LETTER CAPITAL Z
U+1D53AU+2102ℂDOUBLE-STRUCK CAPITAL C (Complex Numbers)U+1D53EU+210DℍDOUBLE-STRUCK CAPITAL H (Quaternions)U+1D545U+2115ℕDOUBLE-STRUCK CAPITAL N (Natural Numbers)U+1D547U+2119ℙDOUBLE-STRUCK CAPITAL PU+1D548U+211AℚDOUBLE-STRUCK CAPITAL Q (Rational Numbers)U+1D549U+211DℝDOUBLE-STRUCK CAPITAL R (Real Numbers)U+1D551U+2124ℤDOUBLE-STRUCK CAPITAL Z (Integers)
U+1D556U+2145ⅅDOUBLE-STRUCK ITALIC CAPITAL D (Differential)U+1D558U+2146ⅆDOUBLE-STRUCK ITALIC SMALL D (Differential)U+1D559U+2147ⅇDOUBLE-STRUCK ITALIC SMALL E (Exponential)U+1D55AU+2148ⅈDOUBLE-STRUCK ITALIC SMALL I (Imaginary) */

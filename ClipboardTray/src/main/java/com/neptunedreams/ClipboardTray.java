package com.neptunedreams;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import javax.swing.ImageIcon;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

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

  public static void main(String[] args) throws AWTException {
    SystemTray systemTray = SystemTray.getSystemTray();

    systemTray.add(getIndentTray());
    systemTray.add(getTextTray());
  }

  private static TrayIcon getTextTray() {
    ImageIcon imageIcon = getImageIcon("/hummingbird.png");
//    ImageIcon imageIcon = getImageIcon("/hummingbirdIcon8000.png");
    TrayIcon trayIcon = new TrayIcon(imageIcon.getImage(), "Clipboard Tools");
    trayIcon.setImageAutoSize(true);
    PopupMenu popupMenu = new PopupMenu();
    trayIcon.setPopupMenu(popupMenu);
    addCharFilter(popupMenu, "To Plain Text", c -> c);
    addCharFilter(popupMenu, "To Upper Case", Character::toUpperCase);
    addCharFilter(popupMenu, "To Lower Case", Character::toLowerCase);
    addStringFilter(popupMenu, "To Title Case", ClipboardTray::toTitleCase);
    addStringFilter(popupMenu, "Combine Lines", ClipboardTray::combineLines);
    addLineFilter(popupMenu, "Indent 2 spaces", t -> prePadLine(t, "  "));   
    addLineFilter(popupMenu, "Indent 4 spaces", t -> prePadLine(t, "    "));
    addLineFilter(popupMenu, "Unordered List <li>", t -> wrapLine(t, "li"));
    addLineFilter(popupMenu, "Paragraph <p>", t -> wrapLine(t, "p"));
    popupMenu.addSeparator();
    popupMenu.add(exitItem());
    return trayIcon;
  }
  
  private static TrayIcon getIndentTray() {
    ImageIcon indentIcon = getImageIcon("/IndentIcon.png");
    PopupMenu popupMenu = new PopupMenu();
    TrayIcon trayIcon = new TrayIcon(indentIcon.getImage(), "Indent", popupMenu);
//    addStringFilter(popupMenu, "Indent", ClipboardTray::indentForEmail);
    addLineFilter(popupMenu, "Indent", ClipboardTray::toEmailLine);
    return trayIcon;
  }

  @NotNull
  private static ImageIcon getImageIcon(String iconName) {
    final URL resource = ClipboardTray.class.getResource(iconName);
    return new ImageIcon(Objects.requireNonNull(resource));
  }


  /**
   * Adds a MenuItem that filters text by character.
   * @param popupMenu The popupMenu
   * @param name The text of the Menu Item
   * @param charFunction A function that transforms each individual character in the input String
   */
  private static void addCharFilter(PopupMenu popupMenu, String name, IntUnaryOperator charFunction) {
    MenuItem menuItem = new MenuItem(name);
    menuItem.addActionListener(e -> processClipboardData(charFunction));
    popupMenu.add(menuItem);
  }

  /**
   * Adds a MenuItem that filters the input text as a whole
   * @param popupMenu The popupMenu
   * @param name The text of the Menu Item
   * @param stringFunction A function that transforms the entire input text from the clipboard
   */
  private static void addStringFilter(PopupMenu popupMenu, String name, UnaryOperator<String> stringFunction) {
    MenuItem menuItem = new MenuItem(name);
    menuItem.addActionListener(e -> processClipboardData(stringFunction));
    popupMenu.add(menuItem);
  }

  /**
   * Adds a MenuItem that filters each line of text, but leaves the line breaks alone.
   * @param popupMenu The popupMenu
   * @param name The text of the Menu Item
   * @param lineFunction A function that transforms a single line of the input text
   */
  private static void addLineFilter(PopupMenu popupMenu, String name, UnaryOperator<String> lineFunction) {
    MenuItem menuItem = new MenuItem(name);
    
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
      StringSelection stringSelection = new StringSelection(result);
      clipboard.setContents(stringSelection, stringSelection);
    } catch (UnsupportedFlavorException | IOException ignored) { }
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
    String[] prepArray = preps.split("\n");
    Set<String> prepSet = new HashSet<>(Arrays.asList(prepArray));
    StringBuilder builder = new StringBuilder();
    List<String> tokens = new LinkedList<>();
    StringTokenizer tokenizer = new StringTokenizer(lines, " .,?!()@-+=<>/", true);
    while (tokenizer.hasMoreTokens()) {
      tokens.add(tokenizer.nextToken());
    }
    for (String word: tokens) {
      final String lowWord = word.toLowerCase();
      if (prepSet.contains(lowWord)) {
        builder.append(lowWord);
      } else {
        final char firstLetter = word.charAt(0);
        if (Character.isLetter(firstLetter)) {
          builder.append(Character.toTitleCase(firstLetter));
          builder.append(word.substring(1).toLowerCase());
        } else {
          builder.append(word);
        }
      }
    }
    return builder.toString();
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
  
  private static String wrapLine(String lineIn, String tag) {
    return wrapLine(lineIn, String.format("<%s>", tag), String.format("</%s>", tag));
  }
  
  private static @NonNls String wrapLine(String lineIn, String openTag, String closeTag) {
    return openTag + lineIn + closeTag;
  }
  
  @SuppressWarnings("StringConcatenation")
  private static String prePadLine(String lineIn, String pad) { return pad + lineIn; }

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
     *   String cleanedInput = StringCollector.filterString(input,{@literal ((Predicate<Character>)} Character::isWhitespace).negate())
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
     *  {@literal stream.filter((Predicate<Character>)} Character::isWhitespace).negate() ...
     * </pre>
     *
     * @param p A predicate
     * @return The negated predicate
     */
    public static <T> Predicate<T> negate(Predicate<T> p) {
//      System.out.println("Predicate");
      return p.negate();
    }

    /**
     * <p>Convenience method to negate an {@code IntPredicate} that's less verbose. This way, we can write this</p>
     * <pre>
     *   stream.filter(negate(Character::isWhitespace)) ...
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
//      System.out.println("IntPredicate");
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

  // Articles, prepositions and conjunctions:
  private static final String preps = """
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
}

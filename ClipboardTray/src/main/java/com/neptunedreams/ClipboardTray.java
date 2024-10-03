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
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
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

import org.jetbrains.annotations.NotNull;

/**
 * <p>Icon: <a href="https://clipartix.com/hummingbird-clipart/">Hummingbird Clip Art</a></p>
 * <p>Created by IntelliJ IDEA.</p>
 * <p>Date: 9/9/24</p>
 * <p>Time: 12:42 AM</p>
 * <p>@author Miguel Muñoz</p>
 */
public enum ClipboardTray {
  ;

  public static final char NEW_LINE = '\n';

  public static void main(String[] args) throws AWTException {
    SystemTray systemTray = SystemTray.getSystemTray();
    
    TrayIcon trayIcon = getTextTray();

    systemTray.add(trayIcon);
    systemTray.add(getIndentTray());
  }

  private static TrayIcon getTextTray() {
//    ImageIcon imageIcon = getImageIcon("/hummingbird.png");
    ImageIcon imageIcon = getImageIcon("/hummingbirdIcon8000.png");
    TrayIcon trayIcon = new TrayIcon(imageIcon.getImage(), "Clipboard Tools");
    trayIcon.setImageAutoSize(true);
    PopupMenu popupMenu = new PopupMenu();
    trayIcon.setPopupMenu(popupMenu);
    System.out.printf("Popup Menu: %s%n", popupMenu); // NON-NLS
    addFilter(popupMenu, "To Upper", Character::toUpperCase);
    addFilter(popupMenu, "To Lower", Character::toLowerCase);
    addFilter(popupMenu, "To Title", Character::toTitleCase);
    popupMenu.add(exitItem());
    return trayIcon;
  }
  
  private static TrayIcon getIndentTray() {
    ImageIcon indentIcon = getImageIcon("/IndentIcon.png");
    PopupMenu popupMenu = new PopupMenu();
    TrayIcon trayIcon = new TrayIcon(indentIcon.getImage(), "Indent", popupMenu);
    addStringFilter(popupMenu, "Indent", ClipboardTray::indent);
    return trayIcon;
  }

  @NotNull
  private static ImageIcon getImageIcon(String iconName) {
    final URL resource = ClipboardTray.class.getResource(iconName);
    return new ImageIcon(Objects.requireNonNull(resource));
  }


  private static void addFilter(PopupMenu popupMenu, String name, IntUnaryOperator charFunction) {
    MenuItem menuItem = new MenuItem(name);
    menuItem.addActionListener(e -> process(charFunction));
    popupMenu.add(menuItem);
  }
  
  private static void addStringFilter(PopupMenu popupMenu, String name, UnaryOperator<String> stringFunction) {
    MenuItem menuItem = new MenuItem(name);
    menuItem.addActionListener(e -> process(stringFunction));
    popupMenu.add(menuItem);
  }
  
  private static void process(IntUnaryOperator function) {
    process(toStringFunction(function));
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
  
  private static void process(Function<String, String> stringFunction) { 
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    try {
      String contents = clipboard.getData(DataFlavor.stringFlavor).toString();
      final String result = stringFunction.apply(contents);
      StringSelection stringSelection = new StringSelection(result);
      clipboard.setContents(stringSelection, stringSelection);
    } catch (UnsupportedFlavorException | IOException ignored) { }
  }

  public static String indent(String text) {
    StringBuilder builder = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
      String line = reader.readLine();
      while (line != null) {
        builder
            .append("> ")
            .append(line)
            .append(NEW_LINE);
        line = reader.readLine();
      }
    } catch(IOException ioe) {
      throw new IllegalStateException(ioe);
    }
    return builder.toString();
  }

  @SuppressWarnings({"unused", "UnnecessaryUnicodeEscape"})
  private static class StringCollector implements Collector<Integer, StringBuilder, String> {
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
     *   stream.filter((({@literal Predicate<Character>)} Character::isWhitespace).negate() ...
     * </pre>
     *
     * @param p A predicate
     * @return The negated predicate
     */
    public static <T> Predicate<T> negate(Predicate<T> p) {
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
}

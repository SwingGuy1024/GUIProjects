package com.mm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.mm.gui.Utils;
import com.mm.util.Constrainer;
import com.mm.util.ReverseCompare;
import com.mm.util.StringCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.awt.GridBagConstraints.BELOW_BASELINE_TRAILING;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.LINE_START;
import static java.awt.GridBagConstraints.VERTICAL;

/**
 * <p>This uses these two data files:
 * <br><a href="https://gist.github.com/cfreshman/a03ef2cba789d8cf00c08f767e0fad7b">wordle-answers.txt</a>
 * <br><a href="https://gist.github.com/cfreshman/cdcdf777450c5b5301e439061d29694c">wordle-allowed-guesses.txt</a></p>
 * <p><strong>Bug:</strong>
 * <br>
 * Find: ie
 * Req:  nie
 * Forb: stor pla
 * Rev: on
 * With Show All checked, it should find GENIE GYNIE NEWIE NIXIE NUDIE. INDIE should be in red, and Genie in white.
 * Turning off Show All should Show Genie and Indie. But it only shows Genie. (Later, Genie will show in red, because
 * it's today's word. </p>
 * <p>When I change Find to just e, I see six Red words: Binge, HINGE, MINCE, NICHE WHINE WINCE. When I turn on
 * Show All, I get those same six red words, but I aslo get INDIE in red.</p>
 * <p>Why doesn't INDIE show up in red when Show All is off?</p>
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 2/3/25
 * <br>Time: 3:26 AM
 * <br>@author Miguel Muñoz</p>
 */
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@SuppressWarnings("CallToPrintStackTrace")
public class WordleAid extends JPanel {
  public static final String[] EMPTY = new String[0];
  public static final String ANSWER_WORDS_FILE = "/wordle-answers.txt";
  public static final String GUESS_WORDS_FILE = "/wordle-allowed-guesses.txt";
  public static final String ROCK_PAPER_SHOTGUN = "www.rockpapershotgun.com";
  public static final String FILE = "wordle-past-answers";
  private static final int LIMIT = 8;
  //  private static final String CHECK_MARK = "\u2714 ";
  private static final Comparator<? super String> FORWARD_COMPARE = null; // defaults to natural order
  private static final Comparator<String> REVERSE_COMPARE = ReverseCompare.INSTANCE;
  private static final Font MONOSPACED = Utils.getFont("Consolas,Courier New, Lucida Console,Menlo,Monospaced", Font.PLAIN, 14);
  private static JFrame sFrame = null;
  private final JTextField input = new JTextField(LIMIT);
  private final JTextField required = new JTextField(LIMIT);
  private final JTextField forbidden = new JTextField(LIMIT);
  private final JList<String> wordList = new JList<>();
//  private static final Font MONOSPACED_BOLD = MONOSPACED.deriveFont(Font.BOLD);
  private final WordleRenderer wordleRenderer = new WordleRenderer();
  // Not sure if there's a better way to mark the accelerators. I can do this by 
  // setting a property in an Action if this were an ordinary Button, but I don't
  // know if there's a way with a toggle button.
  private final JCheckBox reverse = new JCheckBox("<html><u>R</u>ev</html>");
  private final JCheckBox tailSort = new JCheckBox("<html><u>T</u>ail Sort</html>");
  private final JCheckBox showAll = new JCheckBox("<html><u>S</u>how All</html>");
  private final JMenu hiddenMenu = new JMenu("");
  private final Color textFgColor = UIManager.getDefaults().getColor("Label.foreground");

  private LocalDate refreshDate = getToday();
  private Set<String> pastWords = null;
  private SortedSet<String> answerWords = null;
  private SortedSet<String> allWords = null;
  private SortedSet<String> reverseWords = null;

  WordleAid() {
    super(new BorderLayout());
    SortedSet<String> guessWords;
    try {
      answerWords = load(ANSWER_WORDS_FILE);
      guessWords = load(GUESS_WORDS_FILE);
    } catch (IOException ioe) {
      throw new IllegalStateException(ioe);
    }
    allWords = new TreeSet<>(answerWords);
    allWords.addAll(guessWords);

    reverseWords = new TreeSet<>(ReverseCompare.INSTANCE);
    reverseWords.addAll(allWords);

    sFrame.getJMenuBar().add(hiddenMenu);
    add(makeInputPanel(), BorderLayout.PAGE_START);
    add(makeBottomPanel(), BorderLayout.PAGE_END);

    JScrollPane scrollPane = new JScrollPane(wordList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    add(scrollPane, BorderLayout.CENTER);
    wordList.setRequestFocusEnabled(false);
    wordList.setVisibleRowCount(32);
    wordList.setFont(Font.getFont(Font.MONOSPACED));

    try {
      pastWords = makePastWords();
    } catch (IOException e) {
      e.printStackTrace();
    }

    addRenderer();
    addDataListener();
  }
  
  private LocalDate  getToday() {
    return LocalDate.now(ZoneId.of("America/Los_Angeles"));
  }

  public static void main(String[] args) {
    FlatMacDarkLaf.setup();
    sFrame = new JFrame("Wordle Aid");
    sFrame.setJMenuBar(new JMenuBar());
    try {
      final WordleAid wordleAid = new WordleAid();

      sFrame.add(wordleAid, BorderLayout.CENTER);
      sFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      sFrame.pack();
      sFrame.setLocationByPlatform(true);
//		wwf.handlePlatform();
      sFrame.setVisible(true);
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
  }

  static InputStream getWordStream(Class<?> theClass, String fileName) {
//		return Objects.requireNonNull(theClass.getResourceAsStream("englishWordsClean.txt"));
    // returning to enable1.txt after adding gypsy, china, young, and needing to add copse to englishWordsClean.txt.
    return Objects.requireNonNull(theClass.getResourceAsStream(fileName));
  }

  /**
   * Converts a String to a charArray, after filtering out non-letters, and converting to upper case.
   *
   * @param text The text to convert
   * @return a character array with just the letters, converted to upper case.
   */
  public static char[] toFilteredCharArray(String text) {
    return text
        .chars()
        .filter(Character::isLetter)
        .map(Character::toUpperCase)
        .boxed()
        .collect(StringCollector.instance())
        .toCharArray();
  }
  
  private void refreshPastWords() {
    LocalDate today = getToday();
    if (today.isAfter(refreshDate)) {
      try {
        pastWords = makePastWords();
        refreshDate = today;
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Could not refresh Past Words: %s".formatted(e.getMessage()));
      }
    }
  }

  private Set<String> makePastWords() throws IOException {
    URL url = new URL("https", ROCK_PAPER_SHOTGUN, FILE);
    URLConnection conn = url.openConnection();
    conn.connect();
    Set<String> archive = new HashSet<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
      boolean searching = true;
      while (searching) {
        String line = reader.readLine();
        if ("<ul class=\"inline\">".equals(line)) {
          searching = false;
        }
        if (line == null) {
          System.out.printf("Starting line not found.%n"); // NON-NLS
          throw new IllegalStateException("Starting line not found.");
        }
      }
      boolean processing = true;
      while (processing) {
        String word = reader.readLine().trim();
        if (word.startsWith("<li>")) {
          archive.add(clean(word));
        } else {
          processing = false;
        }
      }
    }
    return archive;
  }

  private String clean(String word) {
    int start = word.indexOf('>') + 1;
    int end = word.lastIndexOf('<');
    return word.substring(start, end);
  }

  private SortedSet<String> load(String fileName) throws IOException {
    InputStream fileStream = getWordStream(getClass(), fileName);
    assert fileStream != null;
    SortedSet<String> wordSet = new TreeSet<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8))) {
      String line = reader.readLine();
      while (line != null) {
        wordSet.add(line.toUpperCase());
        line = reader.readLine();
      }
    }
    return wordSet;
  }

  private void toggleItem(JToggleButton button) {
    ButtonModel model = button.getModel();
    model.setSelected(!model.isSelected());
  }

  private void setAccelerator(JToggleButton toggleButton, int keyEventKeyCode) {
    final JMenuItem menuItem = createMenuItem(toggleButton, keyEventKeyCode);
    menuItem.addActionListener(e -> toggleItem(toggleButton));
  }

  /**
   * Gives the button an accelerator. This must be called after adding all the ActionListeners to theButton.
   * @param theButton The button
   * @param keyEventCode The keystroke code for the accelerator.
   */
  private void setAccelerator(final JButton theButton, @SuppressWarnings("SameParameterValue") int keyEventCode) {
    final JMenuItem menuItem = createMenuItem(theButton, keyEventCode);
    ActionListener actionListener = e -> {
      System.out.println("Action: " + theButton.getName());
      for (ActionListener al : theButton.getActionListeners()) {
        al.actionPerformed(e);
      }
    };
    menuItem.addActionListener(actionListener);
  }

  private @NotNull JMenuItem createMenuItem(AbstractButton toggleButton, int keyEventKeyCode) {
    JMenuItem menuItem = new JMenuItem(toggleButton.getText());
    int mods = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
    menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEventKeyCode, mods));
    hiddenMenu.add(menuItem);
    menuItem.addActionListener(e -> System.out.println("Menu Action"));
    return menuItem;
  }

  private JComponent makeInputPanel() {
//		JPanel inputPanel = new JPanel(new GridBagLayout());
    int y = 0;
//    GridHelper helper = new GridHelper();
    Constrainer constrainer = new Constrainer();

    final JLabel version = new JLabel(System.getProperty("java.version"));
    version.setFont(version.getFont().deriveFont(Font.PLAIN, 10.0f));

    JPanel panel = Constrainer.createPanel();
    panel.add(version, constrainer.at(0, y).anchor(LINE_START));
    panel.add(new JLabel("Find:"), constrainer.at(0, ++y).anchor(CENTER).weight(1.0, 0.0));
    panel.add(input, constrainer.at(1, y).gridSize(3, 1));
    panel.add(new JLabel("Req:"), constrainer.at(0, ++y).gridSize(1, 1));
    panel.add(required, constrainer.at(1, y).gridSize(3, 1));
    panel.add(new JLabel("Forb:"), constrainer.at(0, ++y).gridSize(1, 1));
    panel.add(forbidden, constrainer.at(1, y).gridSize(3, 1));

    setAccelerator(reverse, KeyEvent.VK_R);

    reverse.setRequestFocusEnabled(false);
    int x = 0;
    panel.add(reverse, new Constrainer().at(x, ++y).anchor(LINE_START)); // Not sure I need the clear() here.

    setAccelerator(tailSort, KeyEvent.VK_T);

    panel.add(tailSort, constrainer.at(++x, y).anchor(LINE_START).weight(1.0, 0.0).gridSize(1, 1));

    setAccelerator(showAll, KeyEvent.VK_S);
    panel.add(showAll, constrainer.at(++x, y).weight(1.0, 0.0));

    // Just to test:
    constrainer.fill(VERTICAL);
    constrainer.anchor(BELOW_BASELINE_TRAILING);
    
    watchForNewDay(input);

    return panel;
  }
  
  private void watchForNewDay(JTextField input) {
    input.getDocument().addDocumentListener(new DocumentListener() {
      @Override public void insertUpdate(DocumentEvent e) { refreshPastWords(); }
      @Override public void removeUpdate(DocumentEvent e) {}
      @Override public void changedUpdate(DocumentEvent e) {}
    });
  }

  private JPanel makeBottomPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    JButton copy = new JButton("<html>c<u>O</u>py</html>");
    panel.add(copy, BorderLayout.CENTER);
    copy.addActionListener(e -> {
      copyVisibleWords();
    });
    setAccelerator(copy, KeyEvent.VK_O); // must be called after adding all ActionListeners
    return panel;
  }

  private void copyVisibleWords() {
    StringBuilder builder = new StringBuilder();
    ListModel<String> model = wordList.getModel();
    String firstWord = model.getElementAt(0);
    int start = Character.isLetter(firstWord.charAt(0)) ? 0 : 1;
    for (int i = start; i < model.getSize(); i++) {
      final String element = model.getElementAt(i);
      if (!pastWords.contains(element)) {
        builder.append(element)
            .append(' ');
      }
    }
    StringSelection selection = new StringSelection(builder.toString());
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(selection, selection);
  }

  private void addDataListener() {
//		Document wordDocument = input.getDocument();
//		Document requiredDoc = required.getDocument();
    JTextField[] textFields = {input, required, forbidden};
    for (JTextField textField : textFields) {
      Document doc = textField.getDocument();
      doc.addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
          process(input.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          process(input.getText());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          process(input.getText());
        }

      });
    }

    reverse.addItemListener(pItemEvent -> process(input.getText()));
    tailSort.addItemListener(pItemEvent -> process(input.getText()));
    showAll.addItemListener(pItemEvent -> process(input.getText()));
  }

  private void process(String mixedText) {
    String text = mixedText.toUpperCase(Locale.US);
    if (text.isEmpty()) {
      wordList.setListData(EMPTY);
    } else {
      SortedSet<String> wordSource;
      if (showAll.isSelected()) {
        wordSource = allWords;
      } else {
        wordSource = answerWords;
      }
      Set<String> wordSet;
      if (reverse.isSelected()) {
        wordSet = getReverseList(text, wordSource);
      } else {
        wordSet = getForwardList(text, wordSource);
      }
      int preFilterSize = wordSet.size();
      // rangedModel is a new set, not backed by the original.
      wordSet = getRangedSubset(wordSet);

      filterForRequiredCharacters(wordSet);

      // Add in hidden word count...
      int hiddenCount = preFilterSize - wordSet.size();
      if (hiddenCount == 1) {
        wordSet.add("(1 word hidden)");
      } else if (hiddenCount > 1) {
        wordSet.add(String.format("(%d words hidden)", hiddenCount));
      }
      wordList.setListData(wordSet.toArray(EMPTY));
    }
  }

  private void filterForRequiredCharacters(Set<String> wordSet) {
    char[] requiredLetters = toFilteredCharArray(required.getText());
    char[] forbiddenLetters = toFilteredCharArray(forbidden.getText());
    if ((requiredLetters.length > 0) || (forbiddenLetters.length > 0)) {
      int goal = requiredLetters.length;
      Iterator<String> itr = wordSet.iterator();
      outer:
      while (itr.hasNext()) {
        String word = itr.next();
        for (char c : forbiddenLetters) {
          if (word.indexOf(c) >= 0) {
            itr.remove();
            continue outer;
          }
        }
        int rCount = 0;
        for (char c : requiredLetters) {
          if (word.indexOf(c) >= 0) {
            rCount++;
          }
        }
        if (rCount < goal) {
          itr.remove();
        }
      }
    }
  }

  private Set<String> getForwardList(String pText, SortedSet<String> wordSource) {
    String lastWord = makeLastWord(pText);
    Set<String> wordSet;
    if (lastWord == null) {
      wordSet = wordSource.tailSet(pText);
    } else {
      wordSet = wordSource.subSet(pText, lastWord);
    }
    return new TreeSet<>(wordSet); // return a set that's NOT backed by the original, so we can delete elements.
  }

  private Set<String> getReverseList(String text, Set<String> wordSource) {
    reverseWords.clear();
    reverseWords.addAll(wordSource);
    String lastWord = makeLastReverseWord(text);
    Set<String> wordSet;
    if (lastWord == null) {
      wordSet = reverseWords.tailSet(text);
    } else {
      wordSet = reverseWords.subSet(text, lastWord);
    }
    // new TreeSet to alphabetize in forward order.
    return newTreeSet(wordSet);
  }

  @Nullable
  private String makeLastWord(String text) {
    // strip off every trailing Z
    int last = text.length() - 1;
    while ((last >= 0) && (text.charAt(last) == 'Z')) {
      last--;
    }
    if (last == -1) {
      return null;
    }
    final char lastChar = text.charAt(last);
    return text.substring(0, last) + (char) (lastChar + 1);
  }

  @Nullable
  private String makeLastReverseWord(String text) {
    int start = 0;
    while ((text.length() > start) && (text.charAt(start) == 'Z')) {
      start++;
    }
    String sub = text.substring(start);
    if (sub.isEmpty()) {
      return null;
    }
    return ((char) (sub.charAt(0) + 1)) + sub.substring(1);
  }

  private TreeSet<String> newTreeSet(Collection<String> words) {
    TreeSet<String> treeSet = newTreeSet();
    treeSet.addAll(words);
    return treeSet;
  }

  @NotNull
  private TreeSet<String> newTreeSet() {
    Comparator<? super String> comparator = tailSort.isSelected() ? REVERSE_COMPARE : FORWARD_COMPARE;
    return new TreeSet<>(comparator);
  }

  private SortedSet<String> getRangedSubset(Set<String> source) {
    return newTreeSet(source);
  }

  private void addRenderer() {
    wordList.setCellRenderer(wordleRenderer);
  }

  private String formatEntry(String text) {
    if (tailSort.isSelected()) {
      return String.format("%15s", text); // right-justified
    }
    return String.format("%s", text);
  }

  private class WordleRenderer extends DefaultListCellRenderer {
    private boolean strikethrough = false;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      String text = value.toString();
      String newValue;

      // set the foreground color
      Color fg;
      if (!Character.isLetter(text.charAt(0))) {
        fg = Color.gray;
        newValue = text;
      } else {
        newValue = formatEntry(text);
        if (answerWords.contains(text)) {
          // Normal entry, formatted and colored black
          fg = textFgColor;
        } else {
          // hidden word count. Don't format it
          fg = Color.GRAY; // color it gray
        }
      }
      Component renderer = super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
      renderer.setFont(MONOSPACED);
      strikethrough = pastWords.contains(text);
      if (strikethrough) {
        fg = new Color(0x880000);
      }
      renderer.setForeground(fg);
      return renderer;
    }

  }
}

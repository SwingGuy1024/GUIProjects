package com.mm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.Preferences;
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
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import com.mm.util.GridHelper;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * WordsForWwf (Words for "Words with Friends")
 * <p>This used to use the enable1.txt file for its list of English words. This is a well known list
 * that was compiled for use by games. I made a few updates for modern words and created the file 
 * {@code englishWords.txt}, but both lists have a lot of garbage in them. Then I used AppleScript to
 * remove all words that are not in the Microsoft Words spelling dictionary. This gave me the current
 * list, called {@code englishWordsClean.txt}.</p>
 * <br>Created by IntelliJ IDEA.
 * <br>Date: 1/13/12
 * <br>Time: 4:09 PM
 *
 * @author Miguel Muñoz
 */
@SuppressWarnings({"HardCodedStringLiteral", "MagicCharacter", "MagicNumber", "StringConcatenation", "TryWithIdenticalCatches", "Convert2Diamond", "TryFinallyCanBeTryWithResources", "UseOfSystemOutOrSystemErr"})
public final class WordsForWwf extends JPanel {

	public static final String[] EMPTY = new String[0];
	private static final int LIMIT = 15;
	private static final String CHECK_MARK = "\u2714 ";
	private static final Comparator<? super String> FORWARD_COMPARE = null; // defaults to natural order
	private static final Comparator<String> REVERSE_COMPARE = (s1, s2) -> {
		int n1=s1.length();
		int n2=s2.length();
		for (int i1=n1-1, i2=n2-1; (i1 >= 0) && (i2 >= 0); i1--, i2--) {
			char c1 = s1.charAt(i1);
			char c2 = s2.charAt(i2);
			if (c1 != c2) {
				return c1 - c2;
			}
		}
		return n1 - n2;
	};

	private final JTextField input = new JTextField(LIMIT);
//	@SuppressWarnings("rawtypes")
	private final JList<String> wordList = new JList<>();
	private SortedSet<String> words=null;
	private final SortedSet<String> reverseWords;
	private static final Font MONOSPACED = getFont("Menlo,Lucida Console,Monospaced", Font.PLAIN, 14);
	private final Preferences prefs = Preferences.userNodeForPackage(WordsForWwf.class);
	private static JFrame sFrame=null;
	private final JCheckBox reverse = new JCheckBox("Rev");
	private final JCheckBox tailSort = new JCheckBox("Tail Sort");
	private final JMenu hiddenMenu = new JMenu("");
	private SpinnerIntModel minModel;
	private SpinnerIntModel maxModel;
	private final TileCount mTileCount;

	public static void main(String[] args) {
		sFrame = new JFrame("Words For WWF");
		sFrame.setJMenuBar(new JMenuBar());
		final WordsForWwf wwf = new WordsForWwf();

		sFrame.add(wwf, BorderLayout.CENTER);
		sFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		sFrame.pack();
		sFrame.setLocationByPlatform(true);
//		wwf.handlePlatform();
		sFrame.setVisible(true);
	}

//	private void handlePlatform() {
//		String os = System.getProperty("os.name");
//		if (os.startsWith("Mac")) {
//			Application app = Application.getApplication();
//			app.setQuitHandler(new QuitHandler() {
//				@Override
//				public void handleQuitRequestWith(AppEvent.QuitEvent pQuitEvent, QuitResponse pQuitResponse) {
//					mTileCount.saveData();
//					pQuitResponse.performQuit();
//				}
//			});
//		}
//	}

	private WordsForWwf() {
		super(new BorderLayout());

		try {
//			FileReader reader = new FileReader(file);
			words = load();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		reverseWords = new TreeSet<String>(REVERSE_COMPARE);
		reverseWords.addAll(words);

		sFrame.getJMenuBar().add(hiddenMenu);
		add(makeInputPanel(), BorderLayout.PAGE_START);

		JScrollPane scrollPane = new JScrollPane(wordList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
		wordList.setRequestFocusEnabled(false);
		wordList.setVisibleRowCount(32);
		wordList.setFont(Font.getFont(Font.MONOSPACED));

		addDataListener();
		addRenderer();

		mTileCount = new TileCount();
		add(mTileCount, BorderLayout.PAGE_END);
	}
	
	static InputStream getWordStream(Class<?> theClass) {
		return Objects.requireNonNull(theClass.getResourceAsStream("englishWordsClean.txt"));
	}

	private SortedSet<String> load() throws IOException {
//		BufferedInputStream reader = new BufferedInputStream(fileReader);
		// 172820 lines total, 168548 lines < 15 characters
//		long start = System.currentTimeMillis();
		//		int counter = 0;
//        File file = new File(System.getProperty("user.home") + "/My Documents/Downloads", "enable1.txt");
		InputStream fileReader = getWordStream(getClass());
		assert fileReader != null;
		SortedSet<String> wordSet = new TreeSet<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileReader, StandardCharsets.UTF_8));
		try {
			String line = reader.readLine();
			while (line != null) {
				if (line.length() < 16) {
					wordSet.add(line.toUpperCase(Locale.US));
//					counter++;
				}
				line = reader.readLine();
			}
		} finally {
			reader.close();
		}

//		System.out.printf("Line count: %s\n", counter);
		return wordSet;
	}
	
	private void toggleItem(JToggleButton button) {
		ButtonModel model = button.getModel();
		model.setSelected(!model.isSelected());
	}
	
	private void setAccelerator(JToggleButton toggleButton, int keyEventKeyCode) {
		JMenuItem menuItem = new JMenuItem(toggleButton.getText());
		int mods = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		menuItem.setAccelerator(KeyStroke.getKeyStroke(keyEventKeyCode, mods));
		hiddenMenu.add(menuItem);
		menuItem.addActionListener(e -> toggleItem(toggleButton));
	}
	
	private JComponent makeInputPanel() {
//		JPanel inputPanel = new JPanel(new GridBagLayout());
		int y=0;
		GridHelper helper = new GridHelper();

		final JLabel version = new JLabel(System.getProperty("java.version"));
		version.setFont(version.getFont().deriveFont(Font.PLAIN, 10.0f));
		helper.add(version, 0, y++, GridBagConstraints.LINE_START);
		helper.add(input, 0, y++, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 1.0, 0.0, 3, 1);

		setAccelerator(reverse, KeyEvent.VK_R);

		reverse.setRequestFocusEnabled(false);
		int x=0;
		helper.add(reverse, x++, y, GridBagConstraints.LINE_START, GridBagConstraints.BOTH);

		setAccelerator(tailSort, KeyEvent.VK_S);
		
		helper.add(tailSort, x++, y, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, 1.0, 0.0);

		minModel = new SpinnerIntModel(1, 1, LIMIT);
		JSpinner minSize = new JSpinner(minModel);
		minSize.setRequestFocusEnabled(false);
		maxModel = new SpinnerIntModel(LIMIT, 1, LIMIT);
		JSpinner maxSize = new JSpinner(maxModel);
		maxSize.setRequestFocusEnabled(false);
		minModel.addChangeListener(pChangeEvent -> {
			SpinnerNumberModel model = (SpinnerNumberModel) pChangeEvent.getSource();
			Integer min = (Integer) model.getValue();
			maxModel.setMinimum(min);
		});
		maxModel.addChangeListener(pChangeEvent -> {
			SpinnerNumberModel model = (SpinnerNumberModel) pChangeEvent.getSource();
			Integer max = (Integer) model.getValue();
			minModel.setMaximum(max);
		});
		helper.add(minSize, x++, y);
		helper.add(maxSize, x, y);
		return helper.getPanel();
	}

	private void addDataListener() {
		Document document = input.getDocument();
		document.addDocumentListener(new DocumentListener() {
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
		
		reverse.addItemListener(pItemEvent -> process(input.getText()));
		tailSort.addItemListener(pItemEvent -> process(input.getText()));
		
		minModel.addChangeListener(pChangeEvent -> process(input.getText()));
		
		maxModel.addChangeListener(pChangeEvent -> process(input.getText()));
	}

	private void process(String mixedText) {
		String text = mixedText.toUpperCase(Locale.US);
		if (text.isEmpty()) {
			wordList.setListData(EMPTY);
		} else {
			Set<String> wordSet;
			if (reverse.isSelected()) {
				wordSet = getReverseList(text);
			} else {
				wordSet = getForwardList(text);
			}
			int max = maxModel.getInt();
			int min = minModel.getInt();
			if ((min > 1) || (max < LIMIT)) {
				// rangedModel is a new set, not backed by the original.
				Set<String> rangedModel = getRangedSubset(wordSet, min, max);
				
				// Add in hidden word count...
				int hiddenCount = wordSet.size() - rangedModel.size();
				if (hiddenCount == 1) {
					rangedModel.add("(1 word hidden)");
				} else if (hiddenCount > 1) {
					rangedModel.add(String.format("(%d words hidden)", hiddenCount));
				}
				
				wordList.setListData(rangedModel.toArray(EMPTY));
			} else {
				wordList.setListData(wordSet.toArray(EMPTY));
			}
		}
	}

	private Set<String> getForwardList(String pText) {
		String lastWord = makeLastWord(pText);
		Set<String> wordSet;
		if (lastWord == null) {
			wordSet = words.tailSet(pText);
		} else {
			wordSet = words.subSet(pText, lastWord);
		}
		return wordSet;
	}

	private Set<String> getReverseList(String text) {
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
	
	private TreeSet<String> newTreeSet(Collection<String> words) {
		TreeSet<String> treeSet = newTreeSet();
		treeSet.addAll(words);
		return treeSet;
	}

	@NotNull
	private TreeSet<String> newTreeSet() {
		Comparator<? super String> comparator = tailSort.isSelected()? REVERSE_COMPARE : FORWARD_COMPARE;
		return new TreeSet<>(comparator);
	}

	@Nullable
	private String makeLastWord(String text) {
		// strip off every trailing Z
		int last = text.length()-1;
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
		return ((char)(sub.charAt(0) +1)) + sub.substring(1);
	}
	private SortedSet<String> getRangedSubset(Set<String> source, int min, int max) {
		SortedSet<String> range = newTreeSet();
		for (String word: source) {
			int length = word.length();
			if ((length >= min) && (length <= max)) {
				range.add(word);
			}
		}
		return range;
	}
	
	private void addRenderer() {
		ListCellRenderer<Object> renderer = new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				String text = value.toString();
				String newValue;
				
				// set the foreground color
				Color fg;
				if (Character.isLetter(text.charAt(0))) {
					// Normal entry, formatted and colored black
					newValue = formatEntry(text);
					fg = Color.BLACK;
				} else {
					// hidden word count. Don't format it
					newValue = text;
					fg = Color.GRAY; // color it gray
				}
				Component renderer = super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
				renderer.setForeground(fg);
				renderer.setFont(MONOSPACED);
				return renderer;
			}
		};
		wordList.setCellRenderer(renderer);
	}
	
	private String formatEntry(String text) {
		if (tailSort.isSelected()) {
			return String.format("%02d::  %15s", text.length(), text); // right-justified
		}
		return String.format("%02d:   %s", text.length(), text);
	}
	
	@SuppressWarnings({"CharUsedInArithmeticContext", "MagicCharacter"})
	private final class TileCount extends JPanel {
		private static final char DOT = '.';
		private static final String TILE_DATA_KEY = "mappedData";
		Tile[] tiles = {
				new Tile('a', 1, 9),
				new Tile('b', 4, 2),
				new Tile('c', 4, 2),
				new Tile('d', 2, 5),
				new Tile('e', 1, 13),
				new Tile('f', 4, 2),
				new Tile('g', 3, 3),
				new Tile('h', 3, 4),
				new Tile('i', 1, 8),
				new Tile('j', 10, 1),
				new Tile('k', 5, 1),
				new Tile('l', 2, 4),
				new Tile('m', 4, 2),
				new Tile('n', 2, 5),
				new Tile('o', 1, 8),
				new Tile('p', 4, 2),
				new Tile('q', 10, 1),
				new Tile('r', 1, 6),
				new Tile('s', 1, 5),
				new Tile('t', 1, 7),
				new Tile('u', 2, 4),
				new Tile('v', 5, 2),
				new Tile('w', 4, 2),
				new Tile('x', 8, 1),
				new Tile('y', 3, 2),
				new Tile('z', 10, 1),
				new Tile('.', 0, 2)
		};
		private final JTextArea playedTiles;
		private WwfDataBean wwfDataBean=null;
		private Map<String, String> opponentMap = new HashMap<String, String>();
		private JPanel allNames;
		private @Nullable String currentName=null;
		private @Nullable JToggleButton currentButton=null;

		private TileCount() {
			super(new BorderLayout());
			add(makeNamePanel(), BorderLayout.PAGE_START);
			playedTiles = new JTextArea(5, 4);
			playedTiles.setEditable(false);
			playedTiles.setWrapStyleWord(true);
			playedTiles.setLineWrap(true);
			installCapsDetector(playedTiles);
			JScrollPane scrollPane = new JScrollPane(playedTiles, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			add(scrollPane, BorderLayout.CENTER);
			add(makeTileCountPane(), BorderLayout.PAGE_END);
			
			// load the preferences
			byte[] data = prefs.getByteArray(TILE_DATA_KEY, null);
			if (data != null) {
				try {
					loadMap(data);
					for (String name: opponentMap.keySet()) {
						// We can't just call addName here, because it assumes the key isn't in the hashMap yet.
						addNameButton(name);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			sFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent pWindowEvent) {
					saveData();
				}

				@Override
				public void windowActivated(WindowEvent pWindowEvent) {
					adjustForCapsLock(mTileCount.playedTiles);
				}
			});
		}

		private void saveData() {
			if (currentName != null) {
				opponentMap.put(currentName, playedTiles.getText());
			}
			saveMap();
		}

		private void installCapsDetector(@NotNull final JTextComponent textComponent) {
			final Toolkit mToolkit = Toolkit.getDefaultToolkit();
			try {
				mToolkit.getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
				AWTEventListener listener = pAWTEvent -> {
					int id = pAWTEvent.getID();
//					System.out.printf("id: 0x%08x\n", id);
					if ((id == KeyEvent.KEY_PRESSED) || (id == KeyEvent.KEY_RELEASED)) {
						adjustForCapsLock(textComponent);
					}
				};
				mToolkit.addAWTEventListener(listener, KeyEvent.KEY_EVENT_MASK);

			} catch (UnsupportedOperationException ignored) {
			}
		}

		private JPanel makeTileCountPane() {
			JPanel tilePane = new JPanel(new GridLayout(0, 3));
			final JLabel[] columnLabels = new JLabel[3];
			for (int ii=0; ii<3; ++ii) {
				columnLabels[ii] = new JLabel();
				columnLabels[ii].setFont(MONOSPACED);
				tilePane.add(columnLabels[ii]);
			}
			final JLabel remainingTilePane = new JLabel();
			drawTiles(columnLabels, remainingTilePane);
			playedTiles.getDocument().addDocumentListener(new DocumentListener() {
				@Override public void insertUpdate(DocumentEvent pDocumentEvent) { drawTiles(columnLabels, remainingTilePane); }
				@Override public void removeUpdate(DocumentEvent pDocumentEvent) { drawTiles(columnLabels, remainingTilePane); }
				@Override public void changedUpdate(DocumentEvent pDocumentEvent) { drawTiles(columnLabels, remainingTilePane); }
			});
			JPanel tileCountPane = new JPanel(new BorderLayout());
			tileCountPane.add(tilePane, BorderLayout.CENTER);
			tileCountPane.add(remainingTilePane, BorderLayout.PAGE_END);
			return tileCountPane;
		}

		private void loadMap(byte[] pData) throws IOException, ClassNotFoundException {
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(pData));
			try {
				Object map = inputStream.readObject();
				if (map instanceof WwfDataBean) {
					wwfDataBean = (WwfDataBean) map;
					opponentMap = wwfDataBean.getOpMap();
				} else {
					//noinspection unchecked
					opponentMap = (Map<String, String>) map;
					wwfDataBean = new WwfDataBean();
					wwfDataBean.setOpMap(opponentMap);
				}
				if (opponentMap == null) {
					opponentMap = new HashMap<String, String>();
					wwfDataBean = new WwfDataBean();
					wwfDataBean.setOpMap(opponentMap);
				}
				// Remove any null keys, which are legacies of removed code
				opponentMap.remove(null);
			} finally {
				inputStream.close();
			}
		}
		
		private void saveMap() {
			try {
				writeMap();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void writeMap() throws IOException {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
			try {
				outputStream.writeObject(wwfDataBean);
				prefs.putByteArray(TILE_DATA_KEY, byteArrayOutputStream.toByteArray());
			} finally {
				outputStream.close();
			}
		}

		private void drawTiles(JLabel[] columnLabels, JLabel remainingTilePane) {
			int[] counts = new int[27];
			String text = playedTiles.getText();
			int count = 0;
			for (int ii=0; ii<text.length(); ++ii) {
				char c = Character.toUpperCase(text.charAt(ii));
				if (Character.isLetter(c) || (c == '.')) {
					int offset = (c == DOT) ? 26 : (c - 'A');
					counts[offset]++;
					count++;
				}
			}
			int rows = tiles.length / 3;
			for (int col=0; col<3; col++) {
				StringBuilder columnBuilder = new StringBuilder();
				for (int row=0; row< rows; ++row) {
					int offset = row + (col * 9);
					Tile tile = tiles[offset];
					String display = tile.getString(counts[offset]);
					columnBuilder.append(display);
				}
				columnLabels[col].setText(String.format("<html>%s</html>", columnBuilder));
			}
			int rackedTiles = 14;
			int remainingTiles = 104 - count - rackedTiles;
			if (remainingTiles < 0) {
				remainingTiles = 0;
				rackedTiles = 104 - count;
			}
			String tileText = (rackedTiles > 0) ? String.format("Remaining Tiles: %d   Racked: %d", remainingTiles, rackedTiles) : String.format("Remaining Tiles: %d", remainingTiles);
			remainingTilePane.setText(tileText);
		}
		
		private JPanel makeNamePanel() {
			JPanel namePanel = new JPanel(new BorderLayout());
			namePanel.add(makeAddRemovePanel(), BorderLayout.PAGE_START);
			namePanel.add(makeAllNamesPanel(), BorderLayout.CENTER);
			return namePanel;
		}
		
		private JPanel makeAddRemovePanel() {
			JButton addButton = new JButton("+");
			addButton.setRequestFocusEnabled(false);
			addButton.addActionListener(pActionEvent -> {
				String name;
				boolean exists;
				do {
					name = JOptionPane.showInputDialog("Please enter an opponent's name", "");
					if (name == null) {
						return;
					}
					exists = opponentMap.containsKey(name);
					if (exists) {
						JOptionPane.showMessageDialog(TileCount.this, "A user with that name already exists");
					}
				} while (exists);
				addName(name);
			});
			
			// remove button
			JButton removeButton = new JButton("-");
			removeButton.setRequestFocusEnabled(false);
			removeButton.addActionListener(pActionEvent -> {
				int confirm = JOptionPane.showConfirmDialog(TileCount.this, String.format("Are you sure you want to remove player %s", currentName), "Remove Player", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					for (int ii=0; ii<allNames.getComponentCount(); ++ii) {
						JToggleButton cmp = (JToggleButton) allNames.getComponent(ii);
						
						// cleanName() removes the check mark from the name
						if (cleanName(cmp.getText()).equals(currentName)) {
							allNames.remove(cmp);
							playedTiles.setText("");
							if (!opponentMap.containsKey(currentName) && currentName.isEmpty()) {
								opponentMap.remove(null);
							}
							opponentMap.remove(currentName);
							
							// This cleans up a data bug introduced by a coding bug. It's no longer needed, for now
//								if (opponentMap.containsKey(CHECK_MARK+currentName)) {
//									opponentMap.remove(CHECK_MARK+currentName);
//								}
							currentName = null;
							currentButton = null;
							allNames.validate();
							allNames.repaint();
							playedTiles.setEditable(false);
							return;
						}
					}
				}
			});
			JPanel topPanel = new JPanel(new FlowLayout());
			topPanel.add(addButton);
			topPanel.add(removeButton);
			return topPanel;
		}
		
		private void addName(@NotNull String name) {
			opponentMap.put(name, "");
			JToggleButton button = addNameButton(name);
			button.doClick();
			revalidate();
			allNames.revalidate();
			allNames.repaint();
			final Container parent = allNames.getParent();
			parent.revalidate();
			parent.repaint();
		}

		private JToggleButton addNameButton(String name) {
			JToggleButton button = new JToggleButton(name);
			button.setRequestFocusEnabled(false);

			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent pActionEvent) {
					if (currentButton != null) {
						markAsActive(currentButton, false);
						currentButton.setSelected(false);
					}
					if (currentName != null) {
						String playedText = playedTiles.getText();
						opponentMap.put(currentName, playedText);
					}
					
					// cleanName removes the check mark.
					currentName = cleanName(pActionEvent.getActionCommand());
					if (currentName.isEmpty()) {
						currentName = null;
					}
					playedTiles.setText(opponentMap.get(currentName));
					currentButton = (JToggleButton) pActionEvent.getSource();
					currentButton.setSelected(true);
					markAsActive(currentButton, true);
					playedTiles.setEditable(true);
					saveMap();
				}
				
				private void markAsActive(@NotNull JToggleButton button, boolean active) {
					String name = button.getText();
					if (active) {
						if (!name.startsWith(CHECK_MARK)) {
							button.setText(CHECK_MARK + name);
						}
					} else {
						if (name.startsWith(CHECK_MARK)) {
							button.setText(cleanName(name));
						}
					}
				}
			});
			allNames.add(button);
			return button;
		}

		private JPanel makeAllNamesPanel() {
			allNames = new JPanel(new GridLayout(0, 3));
			return allNames;
		}
	}

	private void adjustForCapsLock(JTextComponent textComponent) {
		boolean capsLock = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
		textComponent.setCaretColor(capsLock ? Color.RED : Color.BLACK);
	}

	private String cleanName(String name) {
		if (name.startsWith(CHECK_MARK)) {
			return name.substring(CHECK_MARK.length());
		}
		return name;
	}
	
	private static class Tile {
		private static final String NBSP = "&nbsp;"; // non-breaking space
		final char letter;
		final int value;
		final int quantity;
		Tile(char l, int v, int q) {
			letter = Character.toUpperCase(l);
			value = v;
			quantity = q;
		}

		public String getString(int played) {
			int remaining = quantity - played;
			String scoreSpacer = (value > 9) ? "" : NBSP;
			String countSpacer = (remaining > 9) ? "" : NBSP;
			String display = String.format("<b>%c</b><sup><small>%d%s</small></sup>%s%d<font color=gray>/%d</font><br>",
//			String display = String.format("<b>%c</b><sup>%d</sup> %d/%d<br>",
					letter, value, scoreSpacer, countSpacer, remaining, quantity);
			if (remaining == 0) {
				display = String.format("<font color=gray>%s</font>", display);
			} else if (remaining < 0) {
				display = String.format("<font color=red>%s</font>", display);
			}
			return display;
		}
	}

	/**
	 * Get a font from a list of font names. If none of the named fonts are found, 
	 * returns Dialog
	 * @param fontNameList a comma-delimited list of names for the font, in order 
	 *                     of preference.
	 * @param style The font style
	 * @param size The font size
	 * @return The first font found from the list of font names. If none of the 
	 * named fonts are found, returns Dialog
	 */
	private static Font getFont(String fontNameList, @MagicConstant(valuesFromClass = Font.class) int style, int size) {
		String[] fonts = fontNameList.split(",");
		for (String name: fonts) {
			Font f = new Font(name, style, size);
			if (!"Dialog".equals(f.getFontName())) {
				return f;
			}
		}
		return new Font("Dialog", style, size);
	}
	
	private static class SpinnerIntModel extends SpinnerNumberModel {
		SpinnerIntModel(int value, int min, int max) {
			super(value, min, max, 1);
		}

		public int getMin() { return (Integer) getMinimum(); }
		public int getMax() { return (Integer) getMaximum(); }
		public int getInt() { return (Integer) getNumber(); }
	}
	
	private static class WwfDataBean implements Serializable {
		private Map<String, String> opMap=null;

		public Map<String, String> getOpMap() {
			return opMap;
		}

		public void setOpMap(Map<String, String> pOpMap) {
			opMap = pOpMap;
		}
	}
}

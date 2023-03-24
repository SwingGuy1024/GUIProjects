package com.mm;


import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.jetbrains.annotations.Nullable;

/**
 * ReverseLookup
 * <br>Created by IntelliJ IDEA.
 * <br>Date: 1/13/12
 * <br>Time: 4:09 PM
 *
 * @author Miguel Mu�oz
 */
@SuppressWarnings({"HardCodedStringLiteral", "MagicNumber", "MagicCharacter"})
public class ReverseLookup extends JPanel{

	public static final String[] EMPTY = new String[0];
	private final JTextField input = new JTextField(21);
	private final JList<Object> wordList = new JList<>();
	private SortedSet<String> words;
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("ReverseLookup");
		frame.add(new ReverseLookup(), BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

	private ReverseLookup() {
		super(new BorderLayout());
//		File file = new File("enable1.txt");

		try {
			words = load();			
		} catch (IOException e) {
			e.printStackTrace();
		}

		add(input, BorderLayout.PAGE_START);
		wordList.setRequestFocusEnabled(false);
		
		JScrollPane scrollPane = new JScrollPane(wordList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);

		addDataListener();
		addRenderer();
	}

	private SortedSet<String> load() throws IOException {
		final SortedSet<String> sortedSet = new TreeSet<>(REVERSE_COMPARE);
		InputStream stream = getClass().getResourceAsStream("com/mm/englishWords.txt"); 
		assert stream != null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		try {
			String line = reader.readLine();
			while (line != null) {
				if (line.length() < 16) {
					sortedSet.add(line.toUpperCase(Locale.US));
//					counter++;
				}
				line = reader.readLine();
			}
		} finally {
			reader.close();
		}
//		System.out.printf("Line count: %s\n", counter);
//		sortedSet.addAll(prelim);
		return sortedSet;
	}
	
	private static final Comparator<String> REVERSE_COMPARE = new Comparator<String>() {
		@Override
		public int compare(String s1, String s2) {
			int n1=s1.length();
			int n2=s2.length();
			for (int i1=n1-1, i2=n2-1; (i1 >= 0) && (i2 >= 0); i1--, i2--) {
				char c1 = s1.charAt(i1);
				char c2 = s2.charAt(i2);
				if (c1 != c2) {
					return (int) c1 - (int) c2;
				}
			}
			return n1 - n2;
		}
	};
	
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
			
			private void process(String mixedText) {
				String text = mixedText.toUpperCase(Locale.US);
				if (text.isEmpty()) {
					wordList.setListData(EMPTY);
				} else {
					String lastWord = makeLastReverseWord(text);
					Set<String> wordSet;
					if (lastWord == null) {
						wordSet = words.tailSet(text);
					} else {
						wordSet = words.subSet(text, lastWord);
					}
					SortedSet<String> alphabetizedSet = new TreeSet<>(wordSet);
					wordList.setListData(alphabetizedSet.toArray(new String[wordSet.size()]));
				}
			}

			@Nullable
			private String makeLastReverseWord(String input) {
				int start = 0;
				while (input.length() > start && input.charAt(start) == 'Z') {
					start++;
				}
				String sub = input.substring(start);
				if (sub.isEmpty()) {
					return null;
				}
				return ((char)((int) sub.charAt(0) +1)) + sub.substring(1);
			}
		});
	}
	
	private void addRenderer() {
		ListCellRenderer<Object> renderer = new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				String text = value.toString();
				String newValue = String.format("%02d :   %s", text.length(), text);
				return super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
			}
		};
		wordList.setCellRenderer(renderer);
	}
}


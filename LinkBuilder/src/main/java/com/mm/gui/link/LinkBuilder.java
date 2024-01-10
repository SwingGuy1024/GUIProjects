package com.mm.gui.link;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 3/27/17
 * <p>Time: 1:29 AM
 * <br>
 * Done: 1) Show weight of headline field
 * Done: 2) Change button name on selection
 * Done: 3) Copy text on click
 * Done: 4) Add support for changing headline size
 * skip: 5) Preserve text for next use (Why did I think this would be useful?)
 * Done: 6) Add an accelerator for the "Create Link" button
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"HardCodedStringLiteral", "MagicNumber", "MagicCharacter"})
public final class LinkBuilder extends JPanel {

	private static final String CREATE_LINK_AND_COPY = "Create Link and Copy";
	private static final String SELECT_TEXT_TO_LINK = "Select Text to Link";
	private final int r = 10;
	private final int c = 45;
	@NotNull private final JTextArea textView = new JTextArea(r, c);
	@NotNull private final JTextArea linkView = new JTextArea(r, c);
	@NotNull private final JTextArea htmlView = new JTextArea(r, c);
	@NotNull private final JLabel instructions = new JLabel(SELECT_TEXT_TO_LINK);
			
	private JButton button;
	@NotNull
	private final SpinnerNumberModel model = new SpinnerNumberModel(1, 0, 4, 1);
	@NotNull
	private final JSpinner spinner = new JSpinner(model);
	@NotNull
	private final JTextField headlineField = new JTextField();
	private Action linkAction;

	public static void main(String[] args) {
		FlatMacDarkLaf.setup();
		JFrame frame = new JFrame("Link Builder");  
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(new LinkBuilder(frame));
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}
	
	private LinkBuilder(@NotNull JFrame frame) {
		super(new BorderLayout());
		add(makeHeadlineView(), BorderLayout.PAGE_START);
		add(makeCenterView(), BorderLayout.CENTER);
		add(makeButtonPanel(), BorderLayout.PAGE_END);
		Font plainFont = this.getFont(Font.PLAIN, 12, "Verdana", "Arial", "Helvetica", "sans-serif");
		textView.setFont(plainFont);
		linkView.setFont(plainFont);
		htmlView.setFont(plainFont);
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Edit");
		menu.setMnemonic('e');
		JMenuItem item = new JMenuItem(linkAction);
		menu.add(item);
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);
	}
	
	@NotNull
	private JComponent makeHeadlineView() {
		JPanel view = new JPanel(new BorderLayout());
		view.add(headlineField, BorderLayout.CENTER);
		view.add(spinner, BorderLayout.LINE_END);
		model.addChangeListener(e -> {
			final Number value = (Number) model.getValue();
			assert value != null;
			adjustHeadlineFont(value.intValue());
		});
		adjustHeadlineFont(1);
		return view;
	}

	private void adjustHeadlineFont(final int i) {
		int size = new int[] { 12, 15, 17, 22, 26 }[i];
		Font font = getFont(Font.BOLD, size, "Verdana", "Arial", "Helvetica", "sans-serif");
		headlineField.setFont(font);
	}
	
	private Font getFont(int style, int size, @NotNull String... names) {
		for (String name: names) {
			Font f = new Font(name, style, size);
//			System.out.printf("Comparing %s with %s...%n", f.getFontName(), name);
			final String fontName = f.getFontName();
			assert fontName != null;
			if (fontName.contains(name)) {
				return f;
			}
		}
		return new Font(null, style, size);
	}

	@NotNull
	private JComponent makeCenterView() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
//		textView.setText("This is a noteworthy article, from a respectable journal.");
		panel.add(wrap(scroll(textView), "Body Text:"));
//		linkView.setText("http://www.newyorker.com/magazine/noteworthy-article");
		panel.add(wrap(scroll(linkView), "Link to:"));
		panel.add(wrap(scroll(htmlView), "Result"));
		
		textView.getCaret().addChangeListener(e -> {
			String selection = textView.getSelectedText();
			assert button != null;
			final boolean enabled = selection != null;
			assert linkAction != null;
			linkAction.setEnabled(enabled);
			instructions.setVisible(!enabled);
//			linkAction.putValue(Action.NAME, enabled? CREATE_LINK_AND_COPY : SELECT_TEXT_TO_LINK);
//			button.setDisplayedMnemonicIndex(enabled? 'L' : -1);
			
		});
		return panel;
	}
	
	private JComponent wrap(@NotNull JComponent component, String title) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(title), BorderLayout.PAGE_START);
		panel.add(component, BorderLayout.CENTER);
		return panel;
	}

	@NotNull
	private JPanel makeButtonPanel() {
		button = new JButton();
		//noinspection CloneableClassWithoutClone
		linkAction = new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				createLink();
			}
		};
		int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
		linkAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('L', shortcutMask));
		linkAction.putValue(Action.NAME, CREATE_LINK_AND_COPY);
		linkAction.setEnabled(false);
//		button.setDisplayedMnemonicIndex(7);
//		button.addActionListener(e -> createLink());
		button.setAction(linkAction);
//		button.setEnabled(false);
		JPanel buttonPanel = new JPanel(new BorderLayout());
		JPanel innerPanel = makeInnerButtonPanel(button);
		buttonPanel.add(innerPanel, BorderLayout.LINE_END);
		
		JButton clearButton = new JButton("Clear");
		buttonPanel.add(clearButton, BorderLayout.LINE_START);
		clearButton.addActionListener(e -> doClear());
		return buttonPanel;
	}
	
	@NotNull
	private JPanel makeInnerButtonPanel(@NotNull JButton copyLink) {
		JPanel innerPanel = new JPanel(new BorderLayout());
		innerPanel.add(instructions, BorderLayout.CENTER);
		innerPanel.add(copyLink, BorderLayout.LINE_END);
		return innerPanel;
	}
	
	private void doClear() {
		textView.setText("");
		linkView.setText("");
		htmlView.setText("");
		headlineField.setText("");
	}
	
	private void createLink() {
		int start = textView.getSelectionStart();
		int end = textView.getSelectionEnd();
		String cText = textView.getText();
		String text = String.format(
				"%s%s<a href=\"%s\">%s</a>%s",
				getHeadlineString(),
				cText.substring(0, start),
				linkView.getText(),
				cText.substring(start, end), 
				cText.substring(end)
		);
		htmlView.setText(text);

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection(text);
		clipboard.setContents(stringSelection, stringSelection);
	}
	
	private String getHeadlineString() {
		final String text = headlineField.getText().trim();
		if (text.isEmpty()) {
			return "";
		}
		return String.format(
				"%s<b>%s</b>%s<br>",
				bigStart(),
				text,
				bigEnd()
			);
	}
	
	private String bigStart() {
		//noinspection ConstantConditions
		return repeat("<big>", ((Integer)model.getValue()));
	}
	
	private String bigEnd() {
		//noinspection ConstantConditions
		return repeat("</big>", ((Integer)model.getValue()));
	}

	@NotNull
	private String repeat(String txt, int multiple) {
		return String.valueOf(txt).repeat(Math.max(0, multiple));
	}
	
	@NotNull
	private JComponent scroll(@NotNull JTextArea wrapped) {
		wrapped.setLineWrap(true);
		wrapped.setWrapStyleWord(true);
		return new JScrollPane(wrapped, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	}
}

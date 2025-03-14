package com.mm.view;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.mm.util.ExtensionFileFilter;

import static java.awt.event.KeyEvent.*;

/**
 * GUI utility to show the numeric values of any text character that get pasted in.
 * todo: Look at flicker for large properties.
 * todo: Add a check box to control wrapping in the master.
 */
@SuppressWarnings({"HardCodedStringLiteral", "MagicNumber", "HardcodedLineSeparator", "DuplicateStringLiteralInspection", "HardcodedFileSeparator", "StringConcatenation", "MagicCharacter", "MethodOnlyUsedFromInnerClass", "unchecked", "TryFinallyCanBeTryWithResources", "NestedAssignment", "CloneableClassWithoutClone", "UseOfSystemOutOrSystemErr", "UnnecessaryUnicodeEscape"})
public class Escape
{
	public static final Font SANS_SERIF = new Font("SansSerif", Font.PLAIN, 12);
	private static final String specialSaveChars = ":\t\r\n\f#!";
	private static final Font sMonoFnt = new Font("Monospaced", Font.PLAIN, 12);
	private static final int sInitLoc = 20;
	private final JTextArea myMasterView = new JTextArea();
	private final JTextArea mySlaveView = new JTextArea();
	private final PropView  myPropView;
	private boolean mFileChanged = false;
	private File    mOpenFile = null;

	private final JDialog  myParagraphView;
	private static JFrame   sFrame;
	private static JFrame sPeek;
	private static JTextField sPeekText;
	public final String sPropExtension="properties";
	private float  mFontSize=12;
	private final JComboBox<String> mFontBox;
	// todo: Two choices for the escape threshold. I should add a toggle 
	// todo  button to the UI to let the user change this interactively.
	// Use ASCII_ESCAPE_THRESHOLD for 7-bit characters
	private static final int ASCII_ESCAPE_THRESHOLD = 0x007e;
	// Use LATIN_1_ESCAPE_THRESHOLD for 8-bit characters
//	private static final int LATIN_1_ESCAPE_THRESHOLD = 0x00ff;

	public static void main(String[] args)
	{
    System.out.println("Java version " + System.getProperty("java.version"));
		FlatMacDarkLaf.setup();
		//noinspection CatchGenericClass
		sFrame=new JFrame("Escape Code Editor");
		sFrame.setBounds(sInitLoc, sInitLoc, 600, 900);
		sFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		final Escape cVal = new Escape(sFrame);
		sFrame.addWindowListener(new WindowAdapter()
			{ @Override
			  public void windowClosing(WindowEvent evt) { cVal.doExit(); } } );

//		Container cp = sFrame.getContentPane();
//		cp.add(cVal, BorderLayout.CENTER);
		sFrame.setVisible(true);
	}
	
	public Escape(RootPaneContainer rpc)
	{
		myParagraphView = new JDialog(sFrame, false);
		myParagraphView.setBounds(500, sInitLoc, 400, 400);
		myPropView=new PropView();
		myParagraphView.getContentPane().add(myPropView);
		myMasterView.setFont(SANS_SERIF);
		
		Container cp = rpc.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(makeViewPanel(), BorderLayout.CENTER);
		Action cut = new DefaultEditorKit.CutAction();
		Action copy = new DefaultEditorKit.CopyAction();
		Action paste = new DefaultEditorKit.PasteAction()
			{
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					super.actionPerformed(evt);
					myMasterView.requestFocus();
				}
			};
		Action exit = new AbstractAction("Exit")
			{
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					doExit();
				}
			};
		Action sansSerifFont = new FontAction("SansSerif");
		Action serifFont = new FontAction("Serif");
		Action timesFont = new FontAction("Times New Roman");
		Action monospacedFont = new FontAction("Monospaced");
		Action unicodeFont = new FontAction("Arial Unicode MS");
		Action viewAction = new AbstractAction("View Property")
		{
			@Override
			public void actionPerformed(ActionEvent e) { doView(); }
		};
		Action openAction = new AbstractAction("Open File")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				doOpen();
			}
		};
		Action saveAction = new AbstractAction("Save")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				doSave();
			}
		};
		Action saveAsAction = new AbstractAction("Save As")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (askSaveAs()) {
					doSave();
				}
//				JFileChooser ch = makeChooser();
//				if (askForFile(ch) == JFileChooser.APPROVE_OPTION)
//					doSave();
			}
		};

		
		openAction.putValue(Action.MNEMONIC_KEY, (int) 'o');
		saveAction.putValue(Action.MNEMONIC_KEY, (int) 's');
		saveAsAction.putValue(Action.MNEMONIC_KEY, (int) 'a');
		exit.putValue(Action.MNEMONIC_KEY, (int) 'x');
		cut.putValue(Action.MNEMONIC_KEY, (int) 't');
		copy.putValue(Action.MNEMONIC_KEY, (int) 'c');
		paste.putValue(Action.MNEMONIC_KEY, (int) 'p');
		cut.putValue(Action.NAME, "Cut");
		copy.putValue(Action.NAME, "Copy");
		paste.putValue(Action.NAME, "Paste");
		JToolBar tb = new JToolBar();
//		tb.add(cut);
//		tb.add(copy);
//		tb.add(paste);
		tb.addSeparator();
//		tb.add(courierFont);
		tb.add(monospacedFont);
		tb.add(new InternationalFont());
//		tb.add(defFont);
//		tb.add(sansSerifFont);
//		tb.add(serifFont);
		
		tb.addSeparator();
		tb.add(viewAction);
		tb.addSeparator();
		cp.add(tb, BorderLayout.NORTH);
		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		fileMenu.add(openAction);
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);
		fileMenu.addSeparator();
		fileMenu.add(exit);
		
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		editMenu.add(cut).setAccelerator(KeyStroke.getKeyStroke(VK_X, InputEvent.CTRL_DOWN_MASK));
		editMenu.add(copy).setAccelerator(KeyStroke.getKeyStroke(VK_C, InputEvent.CTRL_DOWN_MASK));
		editMenu.add(paste).setAccelerator(KeyStroke.getKeyStroke(VK_V, InputEvent.CTRL_DOWN_MASK));
		editMenu.addSeparator();
		editMenu.add(viewAction);
		JMenu fontMenu = new JMenu("Font");
		fontMenu.setMnemonic('n');
//		fontMenu.add(courierFont);
//		fontMenu.add(defFont);
		fontMenu.add(new FontAction("Monospaced"));
		fontMenu.add(sansSerifFont);
		fontMenu.add(serifFont);
		fontMenu.add(timesFont);
		fontMenu.add(new FontAction("Cambria"));
		fontMenu.add(new FontAction("Calibri"));
		fontMenu.add(new FontAction("Dialog"));
		fontMenu.add(new FontAction("DialogInput"));
		fontMenu.add(new FontAction("Lucida Bright"));
		fontMenu.add(new FontAction("Lucida Console"));
		fontMenu.add(new FontAction("Lucida Sans"));
//		fontMenu.add(new FontAction("Not Found"));
		fontMenu.add(unicodeFont);
		addSizeActions(fontMenu);
		editMenu.addSeparator();
		mb.add(fileMenu);
		mb.add(editMenu);
		mb.add(fontMenu);
		rpc.getRootPane().setJMenuBar(mb);
		
		// add Font Combo:
		GraphicsEnvironment env=GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fnts = env.getAvailableFontFamilyNames();
		mFontBox=new JComboBox<>(fnts);
		ActionListener al = e -> {
			JComboBox<String> src = (JComboBox<String>)e.getSource();
			final Object selectedItem = src.getSelectedItem();
			if (selectedItem != null) { // Shouldn't happen
				String chosenName = selectedItem.toString();
				@SuppressWarnings("NumericCastThatLosesPrecision")
				Font fnt = new Font(chosenName, Font.PLAIN, (int)(mFontSize+0.5));
				myMasterView.setFont(fnt);
			}
		};

		String defFontName=myMasterView.getFont().getFamily();
		mFontBox.setSelectedItem(defFontName);
		mFontBox.addActionListener(al);
		mFontBox.setMaximumRowCount(30);
		tb.add(new JLabel("Font: "));
		tb.add(mFontBox);
		tb.add(new CharacterCounter());

		KeyListener keyListener = new KeyAdapter() {
			@Override
			public void keyTyped(final KeyEvent e) {
				super.keyTyped(e);
				Escape.this.keyTyped(e);
			}
		};
		myMasterView.addKeyListener(keyListener);
		mySlaveView.addKeyListener(keyListener);
		rpc.getContentPane().addKeyListener(keyListener);
		Toolkit.getDefaultToolkit().addAWTEventListener(e -> keyTyped((KeyEvent) e), AWTEvent.KEY_EVENT_MASK);
		
		myMasterView.addCaretListener(e -> {
			int diff = e.getDot() - e.getMark();
			boolean peekIsVisible = (sPeek != null) && sPeek.isVisible();
			if ((diff == 0) && peekIsVisible) {
				sPeekText.setText("");
				hidePeekWindow();
			} else if ((diff != 0)) {
				int min = Math.min(e.getDot(), e.getMark());
				int max = Math.max(e.getDot(), e.getMark());
				try {
					String text = myMasterView.getText(min, max-min);
					showPeekWindow(saveConvert(text, true));
				} catch (BadLocationException ignored) { }
			}
		});
	}
	
	private static void showFont(Font font, String name) {
		System.out.printf("%s font: name %s of family %s size %d style %d%n", name, font.getFontName(), font.getFamily(), font.getSize(), font.getStyle());
	}
	
	private static void showPeekWindow(String peekText) {
		final int MAX_WIDTH = 10;
		if (sPeek == null) {
			sPeek = new JFrame();
			sPeek.setType(Window.Type.UTILITY);
			sPeek.setAlwaysOnTop(true);
			sPeek.setFocusable(false);
			sPeek.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			sPeek.setResizable(false);
			sPeek.getContentPane().setFocusCycleRoot(false);
			sPeek.setFocusableWindowState(false);
			sPeekText = new JTextField(MAX_WIDTH);
			sPeekText.setFont(sMonoFnt);
			sPeek.add(sPeekText);
			sPeek.setLocationByPlatform(false);
			sPeek.setLocation(sFrame.getX() + sFrame.getWidth(), sFrame.getY());
		}
		sPeekText.setColumns(Math.max(MAX_WIDTH, peekText.length()));
		sPeekText.setText(peekText);
		sPeek.pack();
		sPeek.setExtendedState(JFrame.NORMAL); // In case it was iconified.
		sPeek.setVisible(true);
	}
	
	private static void showPeekWindow() {
		if ((sPeek != null) && !sPeekText.getText().isEmpty()) {
			sPeek.setVisible(true);
		}
	}

	private static void hidePeekWindow() {
		if (sPeek != null) {
			sPeek.setVisible(false);
		}
	}
	
	private void keyTyped(KeyEvent ignored) {
		// Enable these to debug keystrokes.
//		int modsEx = e.getModifiersEx();
//		System.out.printf("%5d (%c) %s%n", e.getKeyCode(), e.getKeyChar(), modString(modsEx)); // NON-NLS
	}
	
	private static String modString(int modEx) {
		java.util.List<String> mods = new LinkedList<>();
		testForModifier(modEx, SHIFT_DOWN_MASK, "Shift", mods);
		testForModifier(modEx, CTRL_DOWN_MASK, "Ctrl", mods);
		testForModifier(modEx, META_DOWN_MASK, "Meta", mods);
		testForModifier(modEx, ALT_DOWN_MASK, "Alt", mods);

		if (mods.isEmpty()) {
			return "";
		}

		StringBuilder builder = new StringBuilder(20);
		Iterator<String> itr = mods.iterator();
		builder.append(itr.next());
		while (itr.hasNext()) {
			builder.append('+');
			builder.append(itr.next());
		}
		return builder.toString();
	}
	
	private static void testForModifier(int modifiers, int mask, String value, java.util.List<String> mods) {
		if ((modifiers & mask) == mask) {
			mods.add(value);
		}
	}

	private JComponent makeViewPanel()
	{
		JSplitPane vPanel = new JSplitPane();
		vPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		JScrollPane topView = new JScrollPane(myMasterView);
		JScrollPane botView = new JScrollPane(mySlaveView);
		topView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		botView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		mySlaveView.setFont(sMonoFnt);
		mySlaveView.setEditable(false);
		vPanel.setBottomComponent(botView);
		vPanel.setTopComponent(topView);
		vPanel.setDividerLocation(350);
		EscapeDocument doc=new EscapeDocument();
//		doc.setUpdateChange(true);
		myMasterView.setDocument(doc);
		myMasterView.getDocument().addDocumentListener(new Transformer(mySlaveView));
		myMasterView.setText(makeInitialText(myMasterView.getFont()));
		mFileChanged = false;
		setForWords(myMasterView);
		setForWords(mySlaveView);
		myMasterView.requestFocus();
		return vPanel;
	}

	private String makeInitialText(Font font) {
		StringBuilder initialTextBldr = new StringBuilder(initialText);

		addRange(font, initialTextBldr, '\u0080', '\u0100', "Extended ASCII:");
		addRange(font, initialTextBldr, '\u20a0', '\u20d0', "Currency:");
		addRange(font, initialTextBldr, '\u2100', '\u2200', "arrows:"); // arrows
		addRange(font, initialTextBldr, '\u27f0', '\u2800', "arrows A:"); // arrows
		addRange(font, initialTextBldr, '\u2900', '\u2980', "arrows B:"); // arrows
		addRange(font, initialTextBldr, '\u2b00', '\u2c00', "Misc. Symbols and Arrows:"); // arrows
		addRange(font, initialTextBldr, '\u0391', '\u03aa', "Greek");
		addRange(font, initialTextBldr, '\u03b1', '\u03ca', "");
		addRange(font, initialTextBldr, '\u2200', '\u2300', "Math:\n\u00d7 \u00f7"); // Math
		addRange(font, initialTextBldr, '\u2300', '\u2400', "Misc. Tech:"); // Misc. Tech
		addRange(font, initialTextBldr, '\u2400', '\u2500', "Enclosed letters:"); // Misc. Tech
		addRange(font, initialTextBldr, '\u2500', '\u2600', "Boxes & Block drawing:"); // box & block
		addRange(font, initialTextBldr, '\u2600', '\u2800', "Dingbats:"); // dingbats
		addDoubleWide(font, initialTextBldr, 0xd83d, '\ud000', '\ue000', "Double-Wide:");
		initialTextBldr.append("\nJava version ");
		initialTextBldr.append(System.getProperty("java.version"));
		return initialTextBldr.toString();
	}

	@SuppressWarnings("CharacterComparison")
	private void addRange(Font pFont, StringBuilder pInitialTextBldr, char start, char end, String label) {
		if (label.isEmpty()) {
			pInitialTextBldr.append('\n');
		} else {
			pInitialTextBldr.append("\n\n").append(label).append('\n');
		}
		for (char cc = start; cc < end; cc++) {
			if (pFont.canDisplay(cc)) {
				pInitialTextBldr.append(cc).append(' ');
			}
		}
	}

	@SuppressWarnings({"CharacterComparison", "SameParameterValue"})
	private void addDoubleWide(Font pFont, StringBuilder pInitialTextBldr, int firstChar, char start, char end, String label) {
		char lead = (char)firstChar;
		if (label.isEmpty()) {
			pInitialTextBldr.append('\n');
		} else {
			pInitialTextBldr.append("\n\n").append(label).append('\n');
		}
		for (char cc = start; cc < end; cc++) {
			// We don't test of the font can display the individual characters because that call 
			// always return false for double characters. Instead, we call Font.canDisplayUpTo()
			// Also, for some reason, on my Mac, these characters only display when the
			// application is bundled into a Mac application.
			String doubleCharacter = String.valueOf(lead) + cc;
			if (pFont.canDisplayUpTo(doubleCharacter) < 0) {
				pInitialTextBldr.append(lead).append(cc).append(' ');
			}
		}
	}

	public static void setForWords(JTextArea pArea)
	{
		pArea.setLineWrap(true);
		pArea.setWrapStyleWord(true);
	}

	private JFileChooser makeChooser()
	{
		JFileChooser fileDlg = new JFileChooser();
//		fileDlg.addChoosableFileFilter(makePropsFilter());
		fileDlg.setFileFilter(makePropsFilter());
		return fileDlg;
	}
	
	private void doExit()
	{
		int saveValue = promptForSave();
		if (saveValue == JOptionPane.CANCEL_OPTION) {
			return;
		}
		System.exit(0);
	}
	
	private void doOpen()
	{
		if (mFileChanged)
		{
			int saveValue = promptForSave();
			if (saveValue == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}
		JFileChooser fileDlg = makeChooser();
		int openVal = fileDlg.showOpenDialog(sFrame);
		if (openVal == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				mOpenFile=fileDlg.getSelectedFile();
				readFromOpenedFile();
			}
			catch (IOException e)
			{
				showError(e);
			}
		}
	}

	@SuppressWarnings("OverlyBroadThrowsClause")
	private void readFromOpenedFile() throws IOException {
		BufferedReader rdr= new BufferedReader(new FileReader(mOpenFile));
		try
		{
			StringBuilder text = new StringBuilder();
			char[] cbuf = new char[1024];
			int count;
			while ((count=rdr.read(cbuf))>=0)
			{
				text.append(cbuf, 0, count);
			}
			myMasterView.setText(loadConvert(text.toString()));
			mFileChanged = false;
		}
		finally
		{
			rdr.close();
		}
	}

	private FileFilter makePropsFilter()
	{
		return new ExtensionFileFilter(sPropExtension);
	}
	
	/**
	 * Ask the user if s/he wants to save changes.
	 * Saves if yes, asking for name if needed.
	 * @return One of the following constants from JOptionPane: 
	 * YES_OPTION, NO_OPTION, or CANCEL_OPTION
	 */ 
	private int promptForSave()
	{
		if (mFileChanged)
		{
			int answer = JOptionPane.showConfirmDialog(
					sFrame, 
					"Do you want to save your changes?", 
					"", 
					JOptionPane.YES_NO_CANCEL_OPTION
 			);
			if ((answer == JOptionPane.YES_OPTION) && !doSave()) {
				return JOptionPane.CANCEL_OPTION;
			}
			return answer;
		}
		return JOptionPane.NO_OPTION;
	}
	
//	private String dbgAnswer(int val)
//	{
//		switch (val)
//		{
//			case JOptionPane.YES_OPTION:
//				return "Yes";
//			case JOptionPane.NO_OPTION:
//				return "No";
//			case JOptionPane.CANCEL_OPTION:
//				return "Cancel";
//			default:
//				return "unknown: " + val;
//		}
//	}
//	
	/**
	 * Saves the file, asking for a name if needed.
	 * @return true if the user saved, false if the user cancelled.
	 */ 
	private boolean doSave()
	{
		if ((mOpenFile == null) && !askSaveAs()) {
			return false;
		}
		saveFile();
		return true;
	}
	
	/**
	 * Asks the user to specify a file to save. If the specified file
	 * already exists, asks if the user wants to replace it. As long
	 * as the user says no, it keeps asking for another file until one of
	 * three things happens:
	 * <br>&nbsp;&nbsp;  The user specifies a file that doesn't exist yet,
	 * <br>&nbsp;&nbsp;  The user allows the existing file to be replaced,
	 * <br>&nbsp;&nbsp;  The user cancels.
	 * @return false if the user cancels, true if the approves the save.
	 * If the user says no when asked if the file should be replaced, this
	 * does not return, it asks again. 
	 */ 
	private boolean askSaveAs()
	{
		boolean nameNotChosen = true;
		JFileChooser fileDlg = makeChooser();
		while (nameNotChosen)
		{
			int saveVal = askForFile(fileDlg);
			if (saveVal == JFileChooser.CANCEL_OPTION) {
				return false;
			}
			File newFile = fileDlg.getSelectedFile();
			if (!newFile.exists())
			{
				//noinspection ObjectAllocationInLoop
				StringBuilder newName = new StringBuilder(fileDlg.getName(newFile));
				if (!(newName.indexOf(".")>0))
				{
					newName.append('.');
					newName.append(sPropExtension);
					//noinspection ObjectAllocationInLoop
					newFile = new File(newFile.getParentFile(), newName.toString());
				}
			}
			if (newFile.exists())
			{
				int replace = JOptionPane.showConfirmDialog(sFrame, "The file " 
				        + mOpenFile.getName() + " already exists. Do you want to replace it?");
				if (replace == JOptionPane.CANCEL_OPTION) {
					return false;
				}
				if (replace == JOptionPane.YES_OPTION) {
					nameNotChosen = false;
				}
			}
			else
			{
				mOpenFile = newFile;
				nameNotChosen = false;
			}
		}
		return true;
	}
	
	/**
	 * Asks the user where they want to save the file. Stores the name of the
	 * file, but doesn't save it.
	 * @param dlg The File Chooser.
	 * @return JFileChooser.APPROVE_OPTION or JFileChooser.CANCEL_OPTION 
	 */ 
	private int askForFile(JFileChooser dlg)
	{
		if (mOpenFile != null) {
			dlg.setSelectedFile(mOpenFile);
		}
		int saveResult=dlg.showSaveDialog(sFrame);
		if (saveResult == JFileChooser.APPROVE_OPTION) {
			mOpenFile = dlg.getSelectedFile();
		}
		return saveResult;
	}
	
	/**
	 * Writes the text to the stored file.
	 */ 
	private void saveFile()
	{
		try {
			writeToSaveFile();
		}
		catch (IOException e) {
			showError(e);
		}
	}

	private void writeToSaveFile() throws IOException {
		Writer pen = new BufferedWriter(new FileWriter(mOpenFile));
		try {
			pen.write(mySlaveView.getText());
		}
		finally {
			pen.close();
		}
	}

	private void showError(Throwable err)
	{
		JOptionPane.showMessageDialog(sFrame, "Error: " + err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);		
	}
	
	private void doView()
	{
		int frameRight = sFrame.getLocation().x + sFrame.getWidth();
		Point dlgLoc = myParagraphView.getLocation();
		myParagraphView.setLocation(frameRight, dlgLoc.y);
		myPropView.updateView(myMasterView.getCaret().getDot());
		myParagraphView.setVisible(true);
	}

	/**
	 * Converts unicodes to encoded &#92;uxxxx
	 * and writes out any of the characters in specialSaveChars
	 * with a preceding slash
	 * @param theString The String to convert.
	 * @return The converted String
	 */
	private static String saveConvert(String theString) {
		return saveConvert(theString, false);
	}

	/**
	 * Converts unicodes to encoded &#92;uxxxx
	 * and writes out any of the characters in specialSaveChars
	 * with a preceding slash
	 * @param theString the String to convert.
	 * @param includeNewLine if true, include literal \n for new line characters
	 * @return The converted String
	 */
	@SuppressWarnings("CharUsedInArithmeticContext")
	private static String saveConvert(String theString, boolean includeNewLine) {
		int len = theString.length();
		StringBuilder outBuffer = new StringBuilder(len << 1); // len << 1 means len*2
	
			for(int x=0; x<len; x++) {
					char aChar = theString.charAt(x);
					switch(aChar) {
//              case ' ':
//                if (x == 0)
//                  outBuffer.append('\\');
//                  outBuffer.append(' ');
//                  break;
							case '\t': outBuffer.append('\\').append('t');
												break;
							case '\f':outBuffer.append('\\').append('f');
												break;
////							case '\\':outBuffer.append('\\'); outBuffer.append('\\');
////	                          break;
							case '\n':
								if (includeNewLine) {
									outBuffer.append('\\').append('n');
								} else {
									outBuffer.append(aChar);
								}
												break;
							case '\r':outBuffer.append('\\').append('r');
												break;
							default:
								if (specialSaveChars.indexOf(aChar) != -1)
								{
									outBuffer.append('\\');
									outBuffer.append(aChar);
								}
								else if ((aChar < 0x0020) || (aChar > ASCII_ESCAPE_THRESHOLD)) {
//								else if ((aChar < 0x0020) || (aChar > LATIN_1_ESCAPE_THRESHOLD)) {
											outBuffer.append('\\');
											outBuffer.append('u');
											outBuffer.append(toHex((aChar >> 12) & 0xF));
											outBuffer.append(toHex((aChar >>  8) & 0xF));
											outBuffer.append(toHex((aChar >>  4) & 0xF));
											outBuffer.append(toHex( aChar        & 0xF));
									} else {
											outBuffer.append(aChar);
									}
					}
			}
			return outBuffer.toString();
	}
	
	/*
	 * Converts encoded &#92;uxxxx to unicode chars
	 * and changes special saved chars to their original forms
	 * (Stolen from class java.util.Properties)
	 */
	@SuppressWarnings("CharUsedInArithmeticContext")
	private static String loadConvert (String theString)
	{
			int len = theString.length();
			StringBuilder outBuffer = new StringBuilder(len);
	
			int x=0;
			while(x<len)
			{
				char aChar = theString.charAt(x++);
				if (aChar == '\\')
				{
					int saveX=x;
					//noinspection ConstantValueVariableUse
					char saveAChar = aChar;
					//noinspection CatchGenericClass,OverlyBroadCatchBlock
					try
					{
						aChar = theString.charAt(x++);
						if(aChar == 'u')
						{
							// Read the xxxx
							int value=0;
							for (int i=0; i<4; i++)
							{
								aChar = theString.charAt(x++);
								switch (aChar) {
									case '0':
									case '1':
									case '2':
									case '3':
									case '4':
									case '5':
									case '6':
									case '7':
									case '8':
									case '9':
										value = ((value << 4) + aChar) - '0';
										break;
									case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
										value = ((value << 4) + 10 + aChar) - 'a';
										break;
									case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
										value = ((value << 4) + 10 + aChar) - 'A';
										break;
									default:
										throw new IllegalArgumentException("Malformed \\uxxxx encoding. Found " + aChar);
								}                     // end switch
							}                       // end for
							outBuffer.append((char)value);
						} else {
//	                if (aChar == 't') aChar = '\t';
//	                else if (aChar == 'r') aChar = '\r';
//	                else if (aChar == 'n') aChar = '\n';
//	                else if (aChar == 'f') aChar = '\f';
////	                else throw new RuntimeException();
////							outBuffer.append('\\');
							outBuffer.append(saveAChar);
							x=saveX;
						}                           // end if char=='u'
					}
					catch (RuntimeException siob)
					{
//						//noinspection StringConcatenation,StringContatenationInLoop,MagicCharacter,HardcodedLineSeparator
//						System.err.println("siob of " + siob.getClass()); // NON-NLS
//						System.err.println(siob.getMessage());
						x=saveX;
//						System.err.println("out = <" + outBuffer + ">");
						outBuffer.append(saveAChar);
					}
				} else {
					outBuffer.append(aChar);
				}
			}
			return outBuffer.toString();
	}
	//  private String pad(String input, int maxLen)
	//  {
	//    StringBuffer bf = new StringBuffer(input);
	//    while (bf.length() < maxLen)
	//      bf.insert(0, '0');
	//    return bf.toString();
	//  }
		/**
		 * Convert a nybble to a hex character
		 * @param	nybble	the nybble to convert.
		 * @return the hex digit for the nybble.
		 */
		private static char toHex(int nybble) {
			return hexDigit[(nybble & 0xF)];
		}

		/** A table of hex digits */
		private static final char[] hexDigit = {
			'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
		};

	private Font makeFont(String fName)
	{
		int oldSize = myMasterView.getFont().getSize();
		return new Font(fName, Font.PLAIN, oldSize);
	}
	
	private void addSizeActions(JMenu sMenu)
	{
		sMenu.addSeparator();
		addSize(8, sMenu);
		addSize(9, sMenu);
		addSize(10, sMenu);
		addSize(12, sMenu);
		addSize(14, sMenu);
		addSize(18, sMenu);
		addSize(24, sMenu);
		addSize(36, sMenu);
	}

	private void addSize(int size, JMenu sMenu)
	{
		Action szAct = new AbstractAction(String.valueOf(size))
		{
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				mFontSize= Integer.parseInt((String) getValue(Action.NAME));
				Font masterFont=myMasterView.getFont().deriveFont(mFontSize);
				myMasterView.setFont(masterFont);
				mySlaveView.setFont(mySlaveView.getFont().deriveFont(mFontSize));
				myPropView.setFont(masterFont);
				myMasterView.select(0, 0);
				mySlaveView.select(0, 0);
			}
		};
		sMenu.add(szAct);
	}

	@SuppressWarnings("BooleanVariableAlwaysNegated")
	private class PropView extends JPanel
	{
		private DocumentListener mSrcEar;
		private DocumentListener mViewEar;
//    private Transformer mTransformer = new Transformer(myMasterView, "Text");
		private final JTextArea mView = new JTextArea(new EscapeDocument());
		private int mBeginRange = -1; // starting index of current property
		private CaretListener mCaretEar;
//    private int mEndRange = 0;
		PropView()
		{
			super(new BorderLayout());
			add(new JScrollPane(mView), BorderLayout.CENTER);
			setUpListeners();
			setForWords(mView);
		}
		
		private boolean mViewActive = false;
		private boolean mSourceActive = false;
		
		private void listenToView()
		{
			myMasterView.getDocument().removeDocumentListener(mSrcEar);
			myMasterView.removeCaretListener(mCaretEar);
			mSourceActive = false;
			if (!mViewActive)
			{
				mViewActive = true;
				mView.getDocument().addDocumentListener(mViewEar);
			}
//      myMasterView.getCaret().setVisible(true);
		}
		
		private void listenToSource()
		{
			mView.getDocument().removeDocumentListener(mViewEar);
			mViewActive = false;
			if (!mSourceActive && myParagraphView.isVisible())
			{
				mSourceActive = true;
				myMasterView.getDocument().addDocumentListener(mSrcEar);
				myMasterView.addCaretListener(mCaretEar);
			}
//      mView.getCaret().setVisible(true);
		}
		
		private void setUpListeners()
		{
			WindowListener viewEar = new WindowAdapter()
			{
				@Override
				public void windowActivated(WindowEvent e) { listenToView(); }
			};
			myParagraphView.addWindowListener(viewEar);

			WindowListener mainEar = new WindowAdapter()
			{
				@Override
				public void windowActivated(WindowEvent e) {
					listenToSource();
					showPeekWindow();
				}

				@Override public void windowDeactivated(WindowEvent e) { hidePeekWindow(); }
			};
			sFrame.addWindowListener(mainEar);
			
			mSrcEar = new DocumentListener()
			{
				@Override
				public void insertUpdate(DocumentEvent e)  { updateView(e); }
				@Override
				public void removeUpdate(DocumentEvent e)  { updateView(e); }
				@Override
				public void changedUpdate(DocumentEvent e) { updateView(e); }
			};
			
			mViewEar = new DocumentListener()
			{
				@Override
				public void insertUpdate(DocumentEvent e)  { updateSrc(); }
				@Override
				public void removeUpdate(DocumentEvent e)  { updateSrc(); }
				@Override
				public void changedUpdate(DocumentEvent e) { updateSrc(); }
			};
			
			mCaretEar=new CaretListener()
			{
				/**
				 * Called when the caret position is updated.
				 *
				 * @param e the caret event
				 */
				@Override
				public void caretUpdate(CaretEvent e)
				{
					int dot = e.getDot();
					Document doc = myMasterView.getDocument();
					try
					{
						String txt = doc.getText(0, doc.getLength());
						int pStart = getPropStart(dot, txt);
						if (pStart != mLastPropStart)
						{
							mLastPropStart = pStart;
							myPropView.updateView(dot);
						}
					}
					catch (BadLocationException e1)
					{
						// We shouldn't ever get here.
						e1.printStackTrace();
						//noinspection ProhibitedExceptionThrown
						throw new RuntimeException(e1);
					}
				}
			};
			myMasterView.addCaretListener(mCaretEar);
		}
		
		/**
		 * index of the start of this property (line) in the master view.
		 */ 
		private int mLastPropStart=-1;
		private void updateView(DocumentEvent evt) { updateView(evt.getOffset(), false); }
		private void updateView(int offset) { updateView(offset, true); }
		private void updateView(int offset, boolean pScroll)
		{
			
			String prop = getProp(offset);
			mView.setText(filterText(prop));
			if (pScroll) {
				mView.select(0, 0);
			} else {
				// Scrolls the view to the changed text:
				Runnable viewCaret = () -> {
					int dot=myMasterView.getCaret().getDot();
					int start = (mLastPropStart < 0) ? 1 : mLastPropStart;
					int sPoint = dot - mLastPropStart - countLines("\\n", myMasterView, start, dot);
					mView.select(sPoint, sPoint);
				};
				SwingUtilities.invokeLater(viewCaret);
			}
		}
		
		private void updateSrc()
		{
			String txt = mView.getText();
			String newText = unFilterText(txt);
			setProp(newText);
			Runnable mstrCaret = () -> {
				int dot = myPropView.mView.getCaret().getDot();
				int sPoint = dot + mLastPropStart + countLines("\n", mView, dot);
				myMasterView.select(sPoint, sPoint);
			};
			SwingUtilities.invokeLater(mstrCaret);
		}
		
		private int countLines(String breaker, JTextArea src, int stop)
		{
			return countLines(breaker, src, 0, stop);
		}
		
		public int countLines(String breaker, JTextArea src, int start, int stop)
		{
			assert start >= 0   : "Start = " + start + ", Stop = " + stop;
			assert stop >= start : "Start = " + start + ", Stop = " + stop;
			int where;
			int count=1;
			int breakLen = breaker.length();
			String theText = src.getText().substring(start, stop);
			while ((where=theText.indexOf(breaker)) >= 0)
			{
				count++;
				theText = theText.substring(where+breakLen);
			}
			return count;
		}
		
		
		private void setProp(String pNewText)
		{
			String srcTxt = myMasterView.getText();
			int endLoc = srcTxt.indexOf('\n', mBeginRange);
			if (endLoc < 0) {
				endLoc = srcTxt.length();
			}

			myMasterView.replaceRange(pNewText, mBeginRange, endLoc);
		}

		private String getProp(int where)
		{
			Document doc = myMasterView.getDocument();
			try
			{
				String txt = doc.getText(0, doc.getLength());
				int beginLoc = getPropStart(where, txt);
				int endLoc = txt.indexOf('\n', where);
				if (endLoc < 0) {
					endLoc = txt.length();
				}
				// Skip the new line character
				beginLoc++; // Even if it didn't find anything, this works
				mBeginRange = beginLoc;
				return txt.substring(beginLoc, endLoc);
			}
			catch (BadLocationException e1)
			{
				// We shouldn't ever see this
				e1.printStackTrace();
				//noinspection ProhibitedExceptionThrown
				throw new RuntimeException(e1);
			}
		}
		
		private int getPropStart(int offset, String txt)
		{
			return txt.substring(0, offset).lastIndexOf('\n');
		}
		
		int lineCount = 1;
		private String filterText(String inputTxt)
		{
			lineCount=1;
			if (!inputTxt.contains("\\n")) {
				return inputTxt;
			}
			StringBuilder buf = new StringBuilder(inputTxt);
			int where;
			while ((where=buf.indexOf("\\n")) >= 0)
			{
				buf.replace(where, where+2, "\n");
				lineCount++;
			}
			return buf.toString();
		}
		
		private String unFilterText(String inputTxt)
		{
			lineCount = 1;
			if (inputTxt.indexOf('\n') < 0) {
				return inputTxt;
			}
			StringBuilder buf = new StringBuilder(inputTxt);
			while (buf.charAt(buf.length()-1) == '\n') {
				buf.deleteCharAt(buf.length()-1);
			}
			int where;
			while ((where=buf.indexOf("\n")) >= 0)
			{
				lineCount++;
				buf.replace(where, where+1, "\\n");
			}
			return buf.toString();
		}

		/**
		 * Sets the font for this component.
		 *
		 * @param font the desired {@code Font} for this component
		 * description: The font for the component.
		 * @see Component#getFont
		 */
		@Override
		public void setFont(Font font)
		{
			super.setFont(font);
			// test is just for initialization.
			if (mView != null) {
				mView.setFont(font);
			}
		}
	}

	@SuppressWarnings("TryWithIdenticalCatches")
	private class EscapeDocument extends PlainDocument
	{
		/**
		 * false means this is a new document, and you don't need to prompt the
		 * user to find out if they want to save. They do. 
		 */ 
//		private boolean mUpdateChange = false;
		@Override
		public void insertString(int offs, final String str, AttributeSet a)
									throws BadLocationException
		{
			//noinspection CatchGenericClass
			try
			{
				mFileChanged = true;
				final String preText=getText(0, offs);
				String preConverted = preText + str;
				int newLen = preConverted.length();
				if (newLen >= 6)
				{
					int searchLength = 5 + str.length();
					int beginIndex=Math.max(0, newLen-searchLength);
					int endIndex=offs+str.length();
					String lookForCode = preConverted.substring(beginIndex, endIndex);
//					String lookForSlash = lookForCode.substring(0, lookForCode.length()-5); 
					int slashLoc = lookForCode.indexOf('\\');
					if (slashLoc >= 0)
					{
						String converted = loadConvert(lookForCode);
						if (converted.length() < lookForCode.length())
						{
							remove(beginIndex, offs-beginIndex);
							super.insertString(beginIndex, converted, a);
							return;
						}
					}
				}
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
			catch (RuntimeException re)
			{
				re.printStackTrace();
			}
			super.insertString(offs, str, a);
		}

		/**
		 * Updates any document structure as a result of text removal. This will happen
		 * within a write lock. Since the structure represents a line map, this just
		 * checks to see if the removal spans lines.  If it does, the two lines outside
		 * of the removal area are joined together.
		 *
		 * @param chng the change event describing the edit
		 */
		@Override
		protected void removeUpdate(DefaultDocumentEvent chng)
		{
			super.removeUpdate(chng);
			mFileChanged = true;
		}
	}
	
	private static final class Transformer implements DocumentListener
	{
		private final Document  mDestination;
//		private Caret     mDestCaret;
		
		private Transformer(JTextArea pDest)
		{
			mDestination = pDest.getDocument();
//			mDestCaret = pDest.getCaret();
		}
		@Override
		public void insertUpdate(DocumentEvent e) { changed(e); }
		@Override
		public void removeUpdate(DocumentEvent e) { changed(e); }
		@Override
		public void changedUpdate(DocumentEvent e) { changed(e); }

		private void changed(DocumentEvent evt)
		{
			Document document=evt.getDocument();
			int dlen=mDestination.getLength();
			try
			{
//				int caretMark = mDestCaret.getMark();
//				int caretDot  = mDestCaret.getDot();
				String origtext=document.getText(0, document.getLength());
				final String newTxt = saveConvert(origtext);
				mDestination.remove(0, dlen);
				mDestination.insertString(0, newTxt, null);
//				mDestCaret.setDot(caretMark);
//				mDestCaret.moveDot(caretDot);
			}
			catch (BadLocationException ble)
			{
				System.err.println(ble.getMessage() + ": " + ble.offsetRequested() + " of " + dlen);
				ble.printStackTrace();
			}
		}
	}
	
	private class FontAction extends AbstractAction
	{
		FontAction(String fName) { super(fName); }

		/**
		 * Invoked when an action occurs.
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			Font newFont=makeFont(getFontName());
			showFont(newFont, getFontName());
			myMasterView.setFont(newFont);
			myPropView.setFont(newFont);
			mFontBox.getModel().setSelectedItem(getFontName());
			myMasterView.select(0, 0);
			mySlaveView.select(0, 0);
		}

		protected String getFontName() { return getValue(Action.NAME).toString(); }
	}

	private class InternationalFont extends FontAction
	{
		InternationalFont() { super("International Font"); }
		@Override
		protected String getFontName() { return "Arial Unicode MS"; }
	}
	
	@SuppressWarnings("FieldCanBeLocal")
	private final String initialText = "This is a utility is to edit localized " +
		"properties files that use unicode characters. Since " +
		"properties files only support 7-bit characters, this " +
		"translates all characters with eight or more bits to " +
		"unicode escape sequences.\n\n" + 
		"To see the utility of this tool, type a \\u followed by " +
		"four Hex digits and see what happens.\n\n" + 
		"Type into the top pane. Feel free to use characters in " +
		"other alphabets, like this: \n\n" +
		"Spanish: Espa\u00F1ol \n" +
		"Arabic: \u0639\u0631\u0628\u064A\n" +
		"Chinese: \u4E2D\u6587\n" +
		"French: Fran\u00E7ais\n" +
		"Russian: \u0420\u0443\u0441\u0441\u043A\u0438\u0439\n\n" +
		"Try typing and pasting the foreign characters, or typing " +
		"the escape codes (To see the other alphabets, you may need " +
		"to choose the International font on the tool bar. For this " +
		"to work on Microsoft Windows, you need the \"Arial Unicode MS\" " +
		"font installed.)\n" +
		"\nS\u2080 \u2081 \u2082 \u2083 \u2084 \u2085 \u2086 \u2087 \u2088 \u2089 \u208A \u208B \u208C \u208D \u208E \n" +
			"S\u2070 \u00B9 \u00B2 \u00B3 \u2074 \u2075 \u2076 \u2077 \u2078 \u2079 \u207A \u207B \u207C \u207D \u207E \u2071 \u207F"
			;
	
	private class CharacterCounter extends JLabel {
		CharacterCounter() {
			super("");
			setFont(new Font("Lucida Console", Font.PLAIN, 12));
			DocumentListener charListener = new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					showCount();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					showCount();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					showCount();
				}
				
			};
			myMasterView.getDocument().addDocumentListener(charListener);
			showCount();
		}
		
		private void showCount() {
			int count = myMasterView.getDocument().getLength();
			String label = String.format("%5d chars", count);
			setText(label);
		}
	}
}

/*
Save Strategy:

Open:
prompt for save
  ask for name
  yes?save
  cancel?return
ask for file
open

Save:
check for name
  ?!exists ask for name
  ?cancel return
save

Save As:
ask for name
  yes? save
  cancel? return;

Exit:
prompt for save
  ask for name
  yes? save
  cancel?return
exit
  
doSave (file set)
check for save
ask for name
save                  Done

*/

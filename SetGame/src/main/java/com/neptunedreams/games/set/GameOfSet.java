package com.neptunedreams.games.set;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicToggleButtonUI;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Each card has four properties: Figure, Fill, Hue, and Number. A set of three cards
 * has three cards where, for each property, all have the same value or all have
 * different values. The point is to find sets.
 * <p>
 * This should show cards in a window with a Table(playing area) and an errorView (where
 * errors are displayed.) I'm thinking that errors should display for five seconds, then fade
 * for five seconds. But choosing another set makes the error vanish.
 * <p>
 * The playing area should have a draggable corner that lets you choose the size of the table.
 * As it expands, the software fills the table with a grid of cards (face down) revealing the
 * size of the grid for the current size.
 * <p>
 * Errors should show which attribute didn't work, by showing the three values. So if hue didn't work, it should show
 * the three hues in a tile, so we would see, say, green green blue. If there were two errors, it should show two
 * error tiles. It should be able to show up to four. Anything except hue should be drawn in a dark fg color against
 * a light bg one. Neither fg nor bg should be close to one of the three hues.
 * <p>
 * During play, users should be able to click three cards. Clicking toggles the selection of the card.
 * As soon as three are selected, it should either flash (okay) or buzz and display the error. Then the three cards
 * should go away and get replaced, with a wipe effect, by three new cards.
 * <p>
 * Todo A:   Write error view
 * Tofu B:   When there's more than 3 rows: before replacing the cards, search for sets among the remaining cards, and 
 * tofu      if there aren't any, just replace the three removed cards.
 * Todo C:   Add a way to save a color scheme. (Preferences?) (See Todo AA)
 * Tofu D:   Fix color discontinuity when swinging around blue in the color chooser
 * Tofu E: * In the credit dialog, Change the cursor when the user is over a name.
 * Todo F:   Animation phase. Instead of sliding the cards, do a "teleport": window shade vanish with simultaneous
 * todo      window shade reveal in the new spot. (one at a time, for clarity.)  (Only do this when the size shrinks.
 * todo      When the size grows, Just do a window-shade reveal, and when the size stays the same, do a window-shade
 * todo      shrink before showing the credit dialog, and a window-shade grow afterwards. Finish to-do B first.)
 * Todo G:   Create empty cards for animation and end-of-game.
 * Todo H:   Add sound to animation.
 * Todo I:   Add options button: sound on/off (pending), choose colors (Done).
 * Todo J: * Put strings into resource file.
 * Todo K:   Use non-language icons for everything. (Only need to remove "Total: " and opening buttons)
 * todo      This also means adding an option for western numbers in locales that don't use them.
 * todo      For the winners, put their names in a box with a victory icon. Put the names of the others,
 * todo      in order of score, below. Maybe this could be a JList, with an icon and a bold name for the winners.
 * todo      "Ready" could be a green traffic light icon.
 * Todo L: * Remember position of credit dialog. Put it to the right of the main window.
 * todo      Or, put the credit dialog at the bottom of the main window, but don't remember its position.
 * Tofu M: * Give users choice of 9 or 12. (Or just start with 12.) (It may be better at 9. It may be set to 12
 * tofu      for actual cards to reduce the number of times you have to decide there are no sets and re-deal.)
 * tofu      In software, this isn't an issue. To change, just change the gridRows constant.
 * Tofu N: * Support arrows in the credit dialog.
 * Tofu O: * Put the score in the credit window.
 * Tofu P: * Make "Ready" a default button.
 * Todo Q: * Disable Solitaire when there is at least one player in the list? (Make it default iff it's enabled?)
 * todo      Currently, playing a solitaire game removes everyone from the list. Is this what we want?
 * Todo R: * Add a Cancel button to the color chooser Dialog. Fix cancel by escape key. (esc = OK right now.)
 * Todo S:   Windows: Transmit click to Frame after dismissing cheat-menu. (Can we detect Windows by looking at who
 * todo      has the focus?)
 * Tofu T:   When all cards are used, draw the main window with just the background.
 * Todo U: * Put final set list into a JScrollPane?
 * Tofu V: * Wrap final list into two columns if there's only one player
 * Todo W:   Show number in local numeric system
 * Todo X:   Make it work as an Applet. (For an applet, would the count window work better as a series of pop-ups, so
 * todo      The users would only see their scores, but could pop-up a view of their sets?)
 * Todo Y:   Separate the UI from the game code, for the android version. (Extract Deck and Card, mainly, I think.)
 * Todo Z:   Give the 27th set to whoever found set 26. (Test this by dealing cards in numerical order.)
 * Todo AA:  Allow black as a color. (See Todo C)
 * Todo AB:  Transition last set.
 * Todo AC:  Fade to remove cards, wipe to reposition them.
 * 
 * * Starred items are easy.
 * 
 * <p>
 * Created by IntelliJ IDEA. <br>
 * * User: miguelmunoz<br>
 * Date: Aug 2, 2009<br>
 * Time: 7:22:11 PM<br>
 */
@SuppressWarnings({"MagicNumber", "MethodOnlyUsedFromInnerClass", "ObjectAllocationInLoop", "HardCodedStringLiteral", "NonThreadSafeLazyInitialization", "AccessingNonPublicFieldOfAnotherObject"})
public final class GameOfSet extends JPanel {
	private static JFrame mainFrame=null;
	private static final Card[] EMPTY_CARDS = new Card[3];
	public  static final int CARD_WIDTH = 166;
	public  static final int CARD_HT = 107;
	public  static final int INSET = 8;
	private static final int OBJ_GAP = 3;
	private static GameOfSet instance=null;
	private static final ColorReference sBlue = new ColorReference(new Color(128, 0, 255));  // midway between blue and magenta
	private static final ColorReference sGreen = new ColorReference(Color.GREEN.darker());
	private static final ColorReference sRed = new ColorReference(Color.RED);

	private static final String PLAYER_PROPERTY = "player";

	static final BasicStroke FIGURE_STROKE = new BasicStroke(3.0f);
	static final BasicStroke BORDER_STROKE = new BasicStroke(0.5f);
	static final BasicStroke SHADED_STROKE = new BasicStroke(3.0f);

	private static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	private static final String STARTING_ROWS = "startingRowCount";
	private static final Color GREEN_FELT = Color.GREEN.darker().darker();
	//	private static final JComponent DECK_PROGRESS = new JProgressBar(JProgressBar.HORIZONTAL, 0, 27);
	private final JComponent deckProgressBar = makeDeckIcon();
	private Deck deck=null;
	private final java.util.Set<CardButton> selectedBunch = new HashSet<>();
	private final Table table;
	private final CountView countView;
	private final List<SetIcon> allSets = new LinkedList<>(); // all sets on the table
	private final Map<String, FoundSetView> foundMap = new HashMap<>();
	private int currentSetCount=0;
	private final JFrame foundSetFrame;
	@SuppressWarnings("UseOfObsoleteCollectionType")
	private final Vector<String> players = new Vector<>();
	private long startTime=0;
	private long gameTimeSeconds=0L;
	private final JLabel timeLabel = new JLabel("0:00  ");
	private static int startingRowCount=0;
	
//	private static JFrame dummyFrame = new JFrame("Dummy");
	
	private static final Animator animator = new Animator();
	
	public static void main(String[] args) {
		Preferences preferences = Preferences.userNodeForPackage(GameOfSet.class);
		startingRowCount = Integer.parseInt(preferences.get(STARTING_ROWS, String.valueOf(3)));
		setLF();
		installSetUI();
//		Locale[] locales = Locale.getAvailableLocales();
//		Comparator<Locale> comparator = new Comparator<Locale>() {
//			@Override
//			public int compare(Locale pLocale, Locale pLocale1) {
//				return String.CASE_INSENSITIVE_ORDER.compare(pLocale.toString(), pLocale1.toString());
//			}
//		};
//		Arrays.sort(locales, comparator);
//		for (Locale l: locales) {
//			String v = l.getVariant();
//			if (v.isEmpty()) {
//				System.out.printf("%2s-%2s    (%s) %s\n", l.getLanguage(), l.getCountry(), l.getDisplayLanguage(Locale.US), l.getDisplayCountry(Locale.US));
//			} else {
//				System.out.printf("%2s-%2s-%2s (%s) %s [%s]\n", l.getLanguage(), l.getCountry(), v, l.getDisplayLanguage(Locale.US), l.getDisplayCountry(Locale.US), l.getDisplayVariant());
//			}
//		}

//		Locale.setDefault(new Locale("th", "TH", "TH"));
//		Locale.setDefault(new Locale("ar", "SA", "SA")); // Doesn't work
		if ((args.length > 0) && "icon".equals(args[0])) {
			AppIcon appIcon = new AppIcon();
			JFrame frame = new JFrame("Set Icon");
			JLabel label = new JLabel(appIcon);
			frame.add(label);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
			//noinspection HardcodedLineSeparator
			System.out.printf("\nLabel size: %d, %d\n frameSize: %d, %d", label.getSize().width, label.getSize().height, frame.getSize().width, frame.getSize().height);

			Image image = new BufferedImage(appIcon.getIconWidth(), appIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D) image.getGraphics();
			setRenderingHints(g2);
			appIcon.paintIcon(null, g2, 0, 0);

			try {
				ImageOutputStream os = ImageIO.createImageOutputStream(new File("Diamonds.png"));
				ImageIO.write((RenderedImage) image, "png", os);
				os.flush();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return;
		}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
					 IllegalAccessException ignored) { }
		//noinspection HardCodedStringLiteral
		mainFrame = new JFrame("Set");
		mainFrame.setLocationByPlatform(true);
		instance = new GameOfSet();
		instance.startGame();
		mainFrame.add(instance, BorderLayout.CENTER);
		mainFrame.pack();
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainFrame.setResizable(false);

		WindowListener closer = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				askForNewGame(null, instance.startTime);
			}
		};
		mainFrame.addWindowListener(closer);
		mainFrame.setVisible(true);
		
//		dummyFrame.add(instance.new DummyView());
//		dummyFrame.pack();
//		dummyFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//		dummyFrame.setVisible(true);
	}

	private static  void setLF() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
						 UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	private GameOfSet() {
		super(new BorderLayout());
//		add(new ErrorView(), BorderLayout.PAGE_START);
		table = new Table(startingRowCount, 3);
		
		// This extra JPanel is solely to fix the problem displaying 6 cards at the end of the game.
		JPanel tableHolder = new JPanel(new BorderLayout());
		tableHolder.add(table, BorderLayout.PAGE_START);
		tableHolder.setBackground(GREEN_FELT);
//		setOpaque(true);
		tableHolder.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		add(tableHolder, BorderLayout.CENTER);
		
		countView = new CountView();
		add(countView, BorderLayout.PAGE_END);
		foundSetFrame = new JFrame();
		foundSetFrame.getContentPane().setLayout(new GridLayout(1, 0, 10, 10));
		addPropertyChangeListener("colors", pPropertyChangeEvent -> {
			repaint();                // Only needed for Windows
			foundSetFrame.repaint();  // Windows and Mac
		});
		launchTimer();
	}

	private void startGame() {
		deck = new Deck();
		deck.shuffle();
		table.removeAll();
		mainFrame.repaint();
		establishPlayers();
		foundSetFrame.getContentPane().removeAll();
		foundMap.clear();
		if (players.isEmpty()) {
			foundSetFrame.add(new FoundSetView(null));
		} else {
			for (String name : players) {
				foundSetFrame.add(new FoundSetView(name));
			}
		}
		
		for (int column=0; column < table.gridColumns; ++column) {
			for (int row=0; row< table.gridRows; ++row) {
				final Card newCard = deck.remove(0);
				System.out.printf("Deal %s at start%n", newCard); // NON-NLS
				CardButton button = new CardButton(newCard);
				table.add(button);
				animator.addComponent(button);
			}
		}
		countView.setCount(null);
		
		ensureSetsExist(null);
		
		deck.resetCount();
		foundSetFrame.pack();
		foundSetFrame.setVisible(false);
		mainFrame.pack();
		animator.begin(); // temp removal
		startTime = System.currentTimeMillis();
	}
	
	@SuppressWarnings("HardCodedStringLiteral")
	private void establishPlayers() {
//		JOptionPane.showMessageDialog(instance, "Message", "Title", JOptionPane.ERROR_MESSAGE, new ColorIcon(sRed, sGreen, sBlue));
//		JOptionPane.showMessageDialog(instance, "", "Title", JOptionPane.ERROR_MESSAGE, new ColorChooser.ColorRing(sRed, sGreen, sBlue));
		final JPanel playerPanel = new JPanel(new BorderLayout());
		playerPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(makeConfigPanel());
		
		JComponent playerLabel = new JLabel("<html>Press <i>Add Players\u2026</i> to add more people, <br>or click <i>Solitaire</i> to play alone.<br><br><b>Players</b></html>");
		playerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		topPanel.add(playerLabel, BorderLayout.PAGE_END);
		playerPanel.add(topPanel, BorderLayout.PAGE_START);
		
		final DefaultComboBoxModel<String> listModel = new DefaultComboBoxModel<>(players);
		final JList<String> playerList = new JList<>();
		playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scroll = new JScrollPane(playerList);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		playerPanel.add(scroll, BorderLayout.CENTER);
		
		playerList.setVisibleRowCount(6);
		JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
		JPanel buttonHolderPanel = new JPanel(new BorderLayout());
		buttonHolderPanel.add(buttonPanel, BorderLayout.PAGE_START);
		buttonHolderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		playerPanel.add(buttonHolderPanel, BorderLayout.LINE_END);
		final JButton addPlayerButton = new JButton("Add Player\u2026");
		buttonPanel.add(addPlayerButton);
		final JButton removePlayerButton = new JButton("Remove Player");
		removePlayerButton.setEnabled(false);
		buttonPanel.add(removePlayerButton);
		ActionListener addLsnr = e -> {
			// temporary set, just to check for duplicates
			java.util.Set<String> nameSet = new HashSet<>();
			for (int ii=0; ii<listModel.getSize(); ++ii) {
				nameSet.add(listModel.getElementAt(ii));
			}
			int playerNumber = playerList.getModel().getSize() + 1;
			String newName;
			boolean duplicate;
			do {
				newName = JOptionPane.showInputDialog(String.format("Name of Player %d", playerNumber));
				duplicate = nameSet.contains(newName);
				if (duplicate) {
					Toolkit.getDefaultToolkit().beep();
				}
			} while (duplicate && (newName != null));
			if (newName!= null) {
				listModel.addElement(newName);
				nameSet.add(newName);
				playerPanel.revalidate();
			}
		};
		addPlayerButton.addActionListener(addLsnr);
		ListSelectionListener listLsnr = e -> {
			int index = playerList.getSelectedIndex();
			removePlayerButton.setEnabled(index >= 0);
		};
		playerList.addListSelectionListener(listLsnr);
		ActionListener removeLsnr = e -> {
			int index = playerList.getSelectedIndex();
			listModel.removeElementAt(index);
			if (players.size() > index) {
				playerList.setSelectedIndex(index);
			} else if (!players.isEmpty()) {
				playerList.setSelectedIndex(index-1);
			}
		};
		removePlayerButton.addActionListener(removeLsnr);
		
		final JDialog dlg = new JDialog(mainFrame, "Game of Set", true);
		dlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dlg.setResizable(false);
		JPanel bottomPanel = new JPanel();
		BoxLayout layout = new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS);
		bottomPanel.setLayout(layout);
		JButton solitaire = new JButton("Solitaire");
		JButton ready = new JButton("Ready");
		JButton chooseColors = new JButton("Choose Colors...");
		bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
		bottomPanel.add(chooseColors);
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(solitaire);
		bottomPanel.add(ready);
		ActionListener solitaireLsnr = e -> {
			players.clear();
			dlg.setVisible(false);
		};
		solitaire.addActionListener(solitaireLsnr);
		ActionListener readyLsnr = e -> dlg.setVisible(false);
		ready.addActionListener(readyLsnr);
		ActionListener colorLsnr = e -> launchColorChooser();
		chooseColors.addActionListener(colorLsnr);
		
		playerPanel.add(bottomPanel, BorderLayout.PAGE_END);
		
		dlg.add(playerPanel, BorderLayout.CENTER);
		
		dlg.getRootPane().setDefaultButton(ready);
		dlg.pack();
		dlg.setLocationRelativeTo(mainFrame.getContentPane());

		// we delay setting the list until the frame has been packed. This makes it use its default size, which is larger.
		playerList.setModel(listModel);

		// Here's what we have to do in Swing to request the default focus on a button:
		final ComponentListener focusGrabber = new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent pComponentEvent) {
				dlg.removeComponentListener(this);
				addPlayerButton.requestFocus();
			}
		};
		dlg.addComponentListener(focusGrabber);

		dlg.setVisible(true);
	}

	private JComponent makeConfigPanel() {
		JPanel configPanel = new JPanel(new BorderLayout());
		JLabel configText = new JLabel("Specify the number of cards to deal each round.");
		configPanel.add(configText, BorderLayout.PAGE_START);
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		ButtonGroup group = new ButtonGroup();
		for (int ii=3; ii<8; ++ii) {
			JRadioButton button = new JRadioButton(Integer.toString(ii*3));
			button.addActionListener(pActionEvent -> {
				String name = pActionEvent.getActionCommand();
				int oldStartingRowCount = startingRowCount;
				//noinspection AssignmentToStaticFieldFromInstanceMethod
				startingRowCount = Integer.parseInt(name) / 3;
				table.setRowCount(startingRowCount);
				if (oldStartingRowCount != startingRowCount) {
					Preferences.userNodeForPackage(GameOfSet.class).put(STARTING_ROWS, String.valueOf(startingRowCount));
				}
			});
			group.add(button);
			buttonPanel.add(button);
			if (startingRowCount == ii) {
				button.setSelected(true);
			}
		}
		configPanel.add(buttonPanel, BorderLayout.CENTER);
		configPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
		return configPanel;
	}

	private void launchTimer() {
		Runnable timer = () -> {
			//noinspection InfiniteLoopStatement
			while (true) {
				if (startTime > 0) {
					long time = (System.currentTimeMillis() - startTime)/1000;
					if (time != gameTimeSeconds) {
						gameTimeSeconds = time;
						showNewTime(gameTimeSeconds);
					}
				}
				try {
					//noinspection BusyWait
					Thread.sleep(100);
				} catch (InterruptedException ignored) { }
			}
		};
		Thread timerThread = new Thread(timer, "Timer Thread"); // temp removal
		timerThread.setDaemon(true);
		timerThread.start();
	}
	
	private void showNewTime(final long gameTime) {
		Runnable timeDisplayAdjust = () -> {
			long hours = gameTime/3600L;
			long minutes = (gameTime%3600L)/60L;
			long seconds = gameTime%60L;
			//				DateFormat format = DateFormat.getTimeInstance();
			NumberFormat minuteFormat = NumberFormat.getNumberInstance();
			minuteFormat.setMinimumIntegerDigits(2);
			NumberFormat hourFormat = NumberFormat.getNumberInstance();
			String display;
			if (hours > 0) {
//					display = MessageFormat.format("{0}:{1,number,00}:{2,number,00}", hours, minutes, seconds);
				display = MessageFormat.format("{0}:{1}:{2}  ", hourFormat.format(hours), minuteFormat.format(minutes), minuteFormat.format(seconds));
			} else {
//					display = MessageFormat.format("{0}:{1,number,00}", minutes, seconds);
				display = MessageFormat.format("{0}:{1}  ", hourFormat.format(minutes), minuteFormat.format(seconds));
			}
			timeLabel.setText(display);
		};
		SwingUtilities.invokeLater(timeDisplayAdjust);
	}
	
	private void launchColorChooser() {
		JOptionPane.showMessageDialog(
				instance, 
				new ColorChooser(sRed, sGreen, sBlue), 
				"Choose your Colors", 
				JOptionPane.ERROR_MESSAGE,
				new AppIcon()
		);
		instance.firePropertyChange("colors", 0, 1); // the 0 and 1 mean nothing.
	}
	
	private class Table extends JPanel {
		private final int gridColumns;
		private int gridRows;
		Table(int rows, int columns) {
			// We specify 0 (unspecified) rows, so it can add more rows if needed.
			super(new GridLayout(0, columns));
			gridRows = rows;
			gridColumns = columns;
			setOpaque(false);
		}

		@Override
		protected void addImpl(Component comp, Object constraints, int index) {
			if (comp instanceof CardButton cardButton) {
				
				System.out.printf("Added %s%n", cardButton.card); // NON-NLS
			}
			super.addImpl(comp, constraints, index);
		}

		private void setRowCount(int rows) {
			gridRows = rows;
		}
		
		private int getCurrentRowCount() { return getComponentCount()/3; }
	}
	
//	private class ErrorView extends Canvas {
//		private ErrorView() {
//			setBackground(Color.CYAN);
//			setSize(CARD_WIDTH, CARD_HT);
//		}
//
//		@Override
//		public void paint(Graphics g) {
//			Graphics2D g2 = (Graphics2D) g;
//			g2.setColor(Color.DARK_GRAY);
//			g2.fill3DRect(0, 0, getWidth(), getHeight(), false);
//		}
//	}


	@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
	public static final class CardIcon implements Icon {
		private final Card card;
		private final Stroke figureStroke = FIGURE_STROKE;
		private final Stroke borderStroke = BORDER_STROKE;
		private final Stroke shadedStroke = SHADED_STROKE;
		private final Area cardFigure;
		private final double size;

		private CardIcon(Card pCard) {
			card = pCard;
			cardFigure = card.figure.getFigure();
			size = 1.0;
		}

//		/**
//		 * This constructor is only used to create Android resource files, when the app is launched with the -res option.
//		 * @param pCard The Card
//		 * @param cardSize The size
//		 */
//		protected CardIcon(Card pCard, double cardSize) {
//			card = pCard;
//			size = cardSize;
//			AffineTransform transform = AffineTransform.getScaleInstance(size, size);
//			cardFigure = card.figure.getFigure().createTransformedArea(transform);
////			cardFigure = figure;
//		}

		@Override
		public int getIconWidth() { return CARD_WIDTH; }
		@Override
		public int getIconHeight() { return CARD_HT; }

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g;
			// Interpolation must NOT be set to "NEAREST_NEIGHBOR" for windows,
			// or the shaded fill won't work at 2/3 scale.
			setRenderingHints(g2);
			g2.setStroke(borderStroke);
			RoundRectangle2D borderShape = new RoundRectangle2D.Double(INSET, INSET, CARD_WIDTH - (2 * INSET), CARD_HT - (2 * INSET), 8.0, 8.0);
			Path2D.Double border = new Path2D.Double(borderShape);
			if (size != 1.0) {
				AffineTransform transform = AffineTransform.getScaleInstance(size, size);
        border.transform(transform);
  		}
			boolean selected = (c instanceof AbstractButton) && ((AbstractButton) c).isSelected();
			Color unselectedColor = (c == null) ? new Color(0, 0, 0, 0) : Color.WHITE; // 0000 is transparent.
			g2.setColor(selected? Color.lightGray : unselectedColor);
			g2.fill(border);
			g2.setColor(Color.BLACK);
			g2.draw(border);
			
			g2.setStroke((card.fill == Fill.shaded) ? shadedStroke : figureStroke);
			g2.setColor(card.hue.color.getColor());
			Paint paint = (card.fill == Fill.shaded) ? card.hue.getStripes() : null;
			// for now, we only draw one.
			AffineTransform savedTransform = g2.getTransform();
			double figureWidth = objectWidth * size;
			double sideSpace = (CARD_WIDTH * size) - figureWidth;

			// I don't know why the -1 is needed, but without it, the smallest icon doesn't fill with stripes properly
			// when creating Icon resources for the Android version of the game. 
			double topSpace = topGap * size; // - 1;

			try {
				switch (card.count) {
					case one:
						g2.translate((sideSpace)/2, topSpace);
						draw(g2, cardFigure, card.fill, paint);
						break;
					case two:
						g2.translate(((sideSpace) / 2) - (figureWidth / 2) - OBJ_GAP, topSpace) ;
						draw(g2, cardFigure, card.fill, paint);
						g2.translate(figureWidth + (2 * OBJ_GAP), 0);
						draw(g2, cardFigure, card.fill, paint);
						break;
					case three:
						g2.translate(((sideSpace) / 2) - figureWidth - (OBJ_GAP * 2), topSpace);
						draw(g2, cardFigure, card.fill, paint);
						g2.translate(figureWidth + (2 * OBJ_GAP), 0);
						draw(g2, cardFigure, card.fill, paint);
						g2.translate(figureWidth + (2 * OBJ_GAP), 0);
						draw(g2, cardFigure, card.fill, paint);
						break;
					default:
						throw new AssertionError(String.format("Drawing %d shapes", card.count.getVal() + 1)); // NON-NLS
				}
			} finally {
				g2.setTransform(savedTransform);
			}
		}

		@Override
		public String toString() {
			return String.format("Card Icon %s", card);
		}
	}

	public static void setRenderingHints(Graphics2D pG2) {
		pG2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		pG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	/*
		 * This could be made more efficient. It reuses three (shaded) paint objects for the normal fill, but
		 * creates a new paint object each time for the tiny fills. I should re-use the same objects.
		 */
	private static void draw(Graphics2D g2, @NotNull Area figure, @NotNull Fill fill, @Nullable Paint paint) {
		// "Tiny Stripes" are actually large stripes that get scaled down for tiny icons.
//		boolean useTinyStripes = g2.getTransform().getDeterminant() < 0.3;
		if (fill == Fill.solid) {
			assert paint == null;
			g2.fill(figure);
		} else {
			Color fillColor = g2.getColor();
			g2.setColor(Color.WHITE);
			g2.fill(figure);          // Give the figure a white background.
			g2.setColor(fillColor);
			if (fill == Fill.shaded) {
				assert paint != null;
//				if (useTinyStripes) {
//					// true means small icon
//					g2.setPaint(getPaint(fillColor, true));
//				} else {
					g2.setPaint(paint);
//				}
				g2.fill(figure);
				g2.setColor(fillColor);
			} else {
				assert g2.getPaint() instanceof Color;
			}
		}
		g2.draw(figure); // draw an outline for both hollow and shaded fill
	}

	/**
	 * The AppIcon class is used to create an Icon for the application. This is used by the ChooseColor dialog, but 
	 * it's also used to create an icon file for the app. When used this way, it isn't done when the game is launched.
	 * It takes a special launch to create the icon file.
	 */
	private static class AppIcon implements Icon {
		private static final float scale = 2; // scale determines how big the stripes are drawn.
		private static final float figureHeight = (2 * 41.0f) / 60;
		private static final float figureScale = figureHeight /scale;
		private static final int yStart = (int)(28/(figureHeight *scale)) + 1;
		private static final int dim = 128;

		@Override public int getIconWidth() { return dim; }
		@Override public int getIconHeight() { return dim; }

		@Override
		public void paintIcon(Component pComponent, Graphics pGraphics, int x, int y) {
			Graphics2D g2 = (Graphics2D) pGraphics;
			setRenderingHints(g2);
			g2.scale(scale, scale);
//			Rectangle rect = new Rectangle(0, 0, dim, dim);
//			g2.setColor(Color.BLACK);
//			g2.draw(rect);

			double deltaX = 22/ figureHeight;
			double deltaY = 10/ figureHeight;

			g2.setStroke(new BasicStroke(2.0f*figureScale));
			g2.translate(16/(figureHeight*scale), (2 * yStart) - 3);
			g2.setColor(Color.white);
			Area figure = getTransformedFigure(Figure.triangle);
			g2.fill(figure); // background fill, before shading
			Color color1 = sRed.color;
			g2.setColor(color1);
			draw(g2, figure, Fill.shaded, getPaint(color1));

			g2.setStroke(new BasicStroke(3.0f*figureScale));
			g2.translate(((2 * deltaX) / scale) - (4 / (figureHeight * scale)), (-2 * deltaY) / scale);
			g2.setColor(Color.white);
			figure = getTransformedFigure(Figure.oval);
			g2.fill(figure); // background fill, before shading
			g2.setColor(sGreen.color);
			draw(g2, figure, Fill.hollow, null);

			g2.translate(4 + ((2 * deltaX) / scale), (-2 * deltaY) / scale);
			Color color3 = sBlue.color;
			g2.setColor(color3);
			draw(g2, getTransformedFigure(Figure.square), Fill.solid, null);
		}
		
		private Area getTransformedFigure(Figure f) {
			return f.getFigure().createTransformedArea(AffineTransform.getScaleInstance(figureScale, figureScale));
		}
	}
	
	private static final double objectWidth = (((CARD_WIDTH - (INSET * 2)) * 60.0) / 300) + 6;
	private static final double objectHeight = (((CARD_HT - (INSET * 2) - 1) * 60.0) / 100) - 6;  // -2 works for a & c , 1,7 works for c, 1,8 works for a & c
	private static final double topGap = (CARD_HT - objectHeight)/2;
	private static Area getoval() {
		Shape oval = new Arc2D.Double(0.0, 0.0, objectWidth, objectHeight, 0.0, 360.0, Arc2D.CHORD);
		return new Area(oval);
//		Shape baseRect = new Rectangle2D.Double(0, objectWidth/2.0, objectWidth, objectHeight-objectWidth);
//		Shape topoval =    new Arc2D.Double(0, 0, objectWidth, objectWidth, 0.0, 360.0, Arc2D.CHORD);
//		Shape bottomoval = new Arc2D.Double(0, objectHeight-objectWidth, objectWidth, objectWidth, 0.0, 360.0, Arc2D.CHORD);
//		Area ovalArea = new Area(baseRect);
//		ovalArea.add(new Area(topoval));
//		ovalArea.add(new Area(bottomoval));
//		return ovalArea;
	}
	
	private static Area getSquare() {
		GeneralPath path = new GeneralPath();
		double serifW = objectWidth /4;
		double serifH = objectHeight /4;
		path.moveTo(objectWidth, 0.0);
		path.lineTo(objectWidth, serifH);
		path.lineTo(objectWidth -serifW, serifH);
		path.lineTo(objectWidth -serifW, objectHeight - serifH);
		path.lineTo(objectWidth, objectHeight - serifH);
		path.lineTo(objectWidth, objectHeight);
		path.lineTo(0.0, objectHeight);
		path.lineTo(0.0, objectHeight - serifH);
		path.lineTo(serifW, objectHeight - serifH);
		path.lineTo(serifW, serifH);
		path.lineTo(0.0, serifH);
		path.lineTo(0.0, 0.0);
		path.lineTo(objectWidth, 0.0);
//		path.moveTo((float) objectWidth/2, 0);
//		path.lineTo((float) objectWidth, (float) objectHeight/2);
//		path.lineTo((float) objectWidth/2, (float) objectHeight);
//		path.lineTo(0, (float) objectHeight/2);
//		path.lineTo((float) objectWidth/2, 0);
		return new Area(path);
	}
	
//	@SuppressWarnings({"HardCodedStringLiteral", "HardcodedLineSeparator"})
	private static Area getTriangle() {
//		Point2D p1  = new Point2D.Float(16, 26);
//		Point2D c12 = new Point2D.Float(20, 16.5F);
//		Point2D p2  = new Point2D.Float(15, 5);
//		Point2D c23 = new Point2D.Float(13, 0);
//		Point2D p3  = new Point2D.Float(5, 0);
//		Point2D c34 = new Point2D.Float(-4, 0);
//		Point2D p4  = new Point2D.Float(1, 10);
//		Point2D c45 = new Point2D.Float(4, 16);
//		Point2D p5  = new Point2D.Float(1, 22);
//		Point2D center = new Point2D.Float((float) (p1.getX()+p5.getX())/2.0F, (float)(p1.getY()+p5.getY())/2.0F);
//		Point2D c56 = reflect(c12, center);
//		Point2D p6  = reflect(p2, center);
//		Point2D c67 = reflect(c23, center);
//		Point2D p7  = reflect(p3, center);
//		Point2D c78 = reflect(c34, center);
//		Point2D p8  = reflect(p4, center);
//		Point2D c89 = reflect(c45, center);

		GeneralPath path = new GeneralPath();
		path.moveTo(objectWidth, objectHeight);

//		path.lineTo((3.0 * objectWidth) / 4.0, objectHeight);
//		double slope = (2 * objectHeight) / objectWidth;
//		path.lineTo(((3.0 * objectWidth) / 4.0) - (objectHeight / 6.0 / slope), (5.0 * objectHeight) / 6.0);
////		path.moveTo(((3.0 * objectWidth) / 4.0) - (objectHeight / 3.0 / slope), (2.0 * objectHeight) / 3.0);
////		path.lineTo(objectWidth / 2.0, objectHeight / 2.0);
////		path.lineTo((objectWidth / 4.0) + (objectHeight / 3.0 / slope), (2.0 * objectHeight) / 3.0);
////		path.lineTo(((3.0 * objectWidth) / 4.0) - (objectHeight / 3.0 / slope), (2.0 * objectHeight) / 3.0);
////		path.moveTo(((3.0 * objectWidth) / 4.0) - (objectHeight / 6.0 / slope), (5.0 * objectHeight) / 6.0);
//		path.lineTo((objectWidth / 4.0) + (objectHeight / 6.0 / slope), (5.0 * objectHeight) / 6.0);
//		path.lineTo(objectWidth/4.0, objectHeight);
		
		path.lineTo(0.0, objectHeight);
		path.lineTo(objectWidth /2, 0.0);
		path.lineTo(objectWidth, objectHeight);
//		path.moveTo((float) p1.getX(), (float) p1.getY());
//		quadto(path, c12, p2);
//		quadto(path, c23, p3);
//		quadto(path, c34, p4);
//		quadto(path, c45, p5);
//		quadto(path, c56, p6);
//		quadto(path, c67, p7);
//		quadto(path, c78, p8);
//		quadto(path, c89, p1);
//		double scale = objectHeight/(p1.getY()+p5.getY());
//		AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
//		path.transform(transform);
		return new Area(path);
	}
	
//	private static void quadto(GeneralPath path, Point2D control, Point2D end) {
//		path.quadTo((float) control.getX(), (float) control.getY(), (float) end.getX(), (float) end.getY());
//	}
	
//	private static Point2D reflect(Point2D pt, Point2D ctr) {
//		return new Point2D.Float((float) ((2 * ctr.getX()) - pt.getX()), (float) ((2 * ctr.getY()) - pt.getY()));
//	}

 /**
  * Card Button.
	* For now we draw these three figures:<br>
	* oval: Ellipse<br>
	* Square: I-beam<br>
	* Triangle: Triangle.
	*/
	private class CardButton extends JToggleButton {
		Card card;
		final AnimatingIcon animatingIcon = new AnimatingIcon(animator, GREEN_FELT);

		private CardButton(Card crd) {
			super();
			this.card = crd;
			setIcon(animatingIcon); // temp removal
//			setIcon(new CardIcon(card));
			animatingIcon.prepare(null, new CardIcon(card));
			setOpaque(false);
			setBorder(null);
			setCursor(HAND_CURSOR);
			ActionListener al = e -> {
				if (isSelected()) {
					selectedBunch.add(CardButton.this);
					System.out.printf("%23s%n", CardButton.this.card);
					if (selectedBunch.size() == 3) {
						processSelection();
					}
				} else {
					selectedBunch.remove(CardButton.this);
				}
			};
			addActionListener(al);
		}
	 
		public void transitionTo(Card card) {
			System.out.printf("Transition from %s to %s%n%n", this.card, card);
			this.card = card;
			animatingIcon.prepare(null, new CardIcon(card));
			animator.addComponent(this);
		}

		public boolean isInTransition() {
			return animatingIcon.hasMore();
		}
	 
		public void sendCardTo(CardButton receiver) {
			System.out.printf("Send card  from %s (%s) to %s\n", animatingIcon.leftSide, this.card, receiver.card);
			receiver.animatingIcon.prepare(null, new CardIcon(this.card));
			receiver.card = this.card;
			// same card at first.
			animatingIcon.prepare(animatingIcon.leftSide, null);
			animator.addComponent(receiver);
			animator.addComponent(this);
		}
	 
		public void removeCard() {
			animatingIcon.prepare(null);
			animator.addComponent(this);
		}
	}

	private void processSelection() {
		Card[] cards = new Card[3];
		int ii=0;
		for (CardButton cd: selectedBunch) {
			cards[ii++] = cd.card;
		}
		SetState state = new SetState(cards);
		if (state.isGood) {
			processGoodState();
		} else {
			processBadState();
		}
	}

	private void processGoodState() {
		List<Card> cards = new LinkedList<>();
		List<CardButton> cardBtns = new LinkedList<>();
		for (CardButton cd: selectedBunch) {
			cardBtns.add(cd); // used to replace the cards
			cards.add(cd.card); // used to display the results
		}
		assert cards.size() == 3;
		Collections.sort(cards);
		SetIcon foundSet = new SetIcon(cards.toArray(EMPTY_CARDS));
		String player = getPlayer(foundSet);
		FoundSetView view = foundMap.get(player);
		view.addSet(foundSet);
//		foundSets.add(new JLabel(foundSet));
		foundSetFrame.pack();
		if (!foundSetFrame.isVisible()) {
			Rectangle mainBounds = mainFrame.getBounds();
			foundSetFrame.setLocation((int)mainBounds.getMaxX(), (int) mainBounds.getMinY());
		}
		foundSetFrame.setVisible(true);
		
		// Now remove the found cards
		int[] removedCards = new int[3];
		int removedIndex = 0;
		boolean tableShrank = true;
		for (CardButton cd : cardBtns) {
			int index = getIndexOf(cd);
//			table.remove(index);
			removedCards[removedIndex++] = index;
			if (!deck.isEmpty() && tableIsTooSmall()) {
//				table.add(new CardButton(deck.remove(0)), index);
				final Card newCard = deck.remove(0);
				System.out.printf("Dealt %s from processGoodState()%n", newCard); // NON-NLS
				cd.transitionTo(newCard);
				CardButton cardButton = (CardButton) table.getComponent(index);
				tableShrank = false; // we replaced the cards so the table has the same number as before.
//			} else if (deck.isEmpty()) {
//				System.out.printf("** Remove %d\n", index);
//				table.remove(index); // TODO: need transition here
			}
		}
		
		java.util.Set<Card> selectedSet = new HashSet<>(cards);
		List<Card> remainingCards = getTableCards();
		System.out.println("Cards replaced");
		remainingCards.removeIf(selectedSet::contains);
		
		countView.setCount(remainingCards); // this sets currentSetCount
		
		// must wait to deselect until after the count!
		for (CardButton button : selectedBunch) {
			button.setSelected(false);
		}
		selectedBunch.clear();
		table.revalidate();
		table.repaint(); // This will visually remove the last set after 27 sets have been found.
		if (tableShrank) {
			System.out.printf("Table Shrank\n");
			ensureSetsExist(removedCards);
		} else {
			System.out.printf("Table is same size\n");
			ensureSetsExist(null);
		}
		
		@SuppressWarnings("NonConstantStringShouldBeStringBuffer")
		@NonNls String winner=null;
		if (players.size() > 1) {
			int bestScore = -1;
			
			// find a winner
			for (Map.Entry<String, FoundSetView> entry : foundMap.entrySet()) {
				if (entry.getValue().total > bestScore) {
					winner = entry.getKey();
					bestScore = entry.getValue().total;
				}
			}
			assert winner != null;
			
			// find any ties
			String firstWinner = winner;
			for (final Map.Entry<String, FoundSetView> entry : foundMap.entrySet()) {
				String name = entry.getKey();
				if (!firstWinner.equals(name) && (entry.getValue().total == bestScore)) {
					//noinspection StringContatenationInLoop
					winner += " and " + name; // NON-NLS
				}
			}
		}

		deck.resetCount();

		if (deck.isEmpty() && (currentSetCount == 0)) {
			askForNewGame(winner, startTime);
		}
		animator.begin();
	}

	@SuppressWarnings({"HardCodedStringLiteral", "HardcodedLineSeparator"})
	private static void askForNewGame(@Nullable String winner, long start) {// Is the game over?
		String question = "Would you like another game?";
		if (winner == null) {
			long endTime = System.currentTimeMillis();
			int time = (int) ((endTime-start)/1000);
			int minutes = time/60;
			int seconds = time % 60;
			question = String.format("Game completed in %d minutes, %d seconds.\n%s", minutes, seconds, question);
		} else {
			question = String.format("Congratulations, %s\n%s", winner, question);
		}
		instance.startTime = 0L;
		int choice = JOptionPane.showConfirmDialog(instance, question, "", JOptionPane.YES_NO_OPTION); // NON-NLS
		if (choice == JOptionPane.YES_OPTION) {
			instance.startGame();
		} else {
			System.exit(0);
		}
	}

	/**
	 * After set has been removed, add new cards where needed.
	 * @param where The locations of the blank spots where the new cards should go. 
	 *              May be null if new cards aren't needed.
	 */
	private void ensureSetsExist(@Nullable int[] where) {
		// Now make sure there are sets.
		if ((currentSetCount > 0) && (where != null)) {
			// There are, so move cards out of the last row
			removeLastRow(where);
		} else {
			while ((currentSetCount == 0) && !deck.isEmpty()) {
				// This makes the table larger.
				if (where == null) {
					for (int ii=0; ii<3; ++ii) {
						final Card newCard = deck.remove(0);
						System.out.printf("Deal-%s from ensureSetExists(a)%n", newCard); // NON-NLS
						CardButton btn = new CardButton(newCard);
						table.add(btn);
						animator.addComponent(btn);
					}
	//				table.add(new CardButton(deck.remove(0)));
	//				table.add(new CardButton(deck.remove(0)));
	//				table.add(new CardButton(deck.remove(0)));
				} else {
					// Insert the cards where we removed the previous cards. Insert them in reverse order.
					reverse(where);
	//				table.add(new CardButton(deck.remove(0)), where[2]);
	//				table.add(new CardButton(deck.remove(0)), where[1]);
	//				table.add(new CardButton(deck.remove(0)), where[0]);
					for (int ii: where) {
						System.out.printf("** Replacing %s\n", ((CardButton)table.getComponent(ii)).animatingIcon);
						final Card newCard = deck.remove(0);
						System.out.printf("Deal.%s from ensureSetExists(b)%n", newCard); // NON-NLS
//						CardButton btn = new CardButton(newCard); // added 24-4-16
//						table.add(btn, ii); // added 24-4-16
						((CardButton)table.getComponent(ii)).transitionTo(newCard);
					}
					where = null;
				}
				countView.setCount(null); // this sets currentSetCount
			}
		}
		deckProgressBar.repaint();
		if (!tableIsTooSmall()) {
			mainFrame.pack();
		}
	}
	
	private static void reverse(int[] array) {
		int tail = array.length;
		int halfSize = tail/2;
		for (int i=0; i<halfSize; ++i) {
			int swap = array[i];
			array[i] = array[--tail];
			array[tail] = swap;
		}
	}
	
	private void removeLastRow(final int[] where) {
		Arrays.sort(where);

		// index of the first card in the last row
		int lastRowIndexStart = (table.getCurrentRowCount() - 1) * 3;

		java.util.Set<Integer> targetIndices = new HashSet<>();
		for (int i: where) {
			targetIndices.add(i);
		}

		java.util.Set<Integer> sourceIndices = new HashSet<>();
		for (int i=0; i<3; ++i) {
			sourceIndices.add(lastRowIndexStart+i);
		}
		
		// The intersection of these two sets is all cards that get deleted. They were part
		// of the last set, but in the last row, so we don't move them, we just delete them.
		
		java.util.Set<Integer> cardsToDelete = new HashSet<>(targetIndices);
		cardsToDelete.retainAll(sourceIndices); // Now, it's the intersection.
		
		// The cards we need to move are the remaining cards. We subtract the intersection to get them.
		targetIndices.removeAll(cardsToDelete);
		sourceIndices.removeAll(cardsToDelete);
		
		// We delete the cards in cardsToDelete:
		for (int index: cardsToDelete) {
			CardButton button = (CardButton) table.getComponent(index);
			button.removeCard();
		}
		
		// Now we move cards that can stay in their columns:
		for (Iterator<Integer> targetItr = targetIndices.iterator(); targetItr.hasNext(); ) {
			int target = targetItr.next();
			for (Iterator<Integer> sourceItr = sourceIndices.iterator(); sourceItr.hasNext(); ) {
				int source = sourceItr.next();
				// If they're in the same column: 
				if ((target % 3) == (source % 3)) {
					targetItr.remove();
					sourceItr.remove();
					moveCard(target, source);
				}
			}
		}
		
		// Now we move the rest of the cards.
		assert targetIndices.size() == sourceIndices.size();
		Iterator<Integer> sourceItr = sourceIndices.iterator();
		Iterator<Integer> targetItr = targetIndices.iterator();
		while ( targetItr.hasNext()) {
			int target = targetItr.next();
			int source = sourceItr.next();
			moveCard(target, source);
		}

		Runnable cleanup = () -> {
			System.out.println("Removing last row");
			for (int ii = 0; ii < 3; ++ii) {
				table.remove(table.getComponentCount() - 1); // remove the last component
			}
		};

		animator.begin(cleanup);
	}
	
	private void moveCard(final int dstIndex, final int sourceIndex) {
//		int rowCount = table.getCurrentRowCount() - 1;
		CardButton destination = (CardButton) table.getComponent(dstIndex);
//		int sourceIndex = (rowCount*3) + srcColumn;
		CardButton source = (CardButton) table.getComponent(sourceIndex);
		if (sourceIndex != dstIndex) {
			source.sendCardTo(destination);
		}
	}
	
	/**
	 * Shows the dialog that determines who gets credit for the chosen set.
	 * @param foundSet The set to show
	 * @return The name of the player to get credit
	 */
	private String getPlayer(SetIcon foundSet) {
		if (players.isEmpty()) {
			return "";
		}
		if (players.size() == 1) {
			return players.get(0);
		}
		final JDialog dlg = new JDialog(mainFrame, true);
		dlg.setResizable(false);
		final JPanel playerPanel = new JPanel(new GridLayout(0, 1));
		
		// handle up, down, left, right, and enter. (Tab is handled automatically.)
		boolean leftToRight = playerPanel.getComponentOrientation().isLeftToRight();
		final int downCode = leftToRight? KeyEvent.VK_RIGHT : KeyEvent.VK_LEFT;
		final int upCode = leftToRight? KeyEvent.VK_LEFT : KeyEvent.VK_RIGHT;
		KeyListener nextPlayer = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent pKeyEvent) {
				if (pKeyEvent.getModifiersEx() == 0) {
					final int code = pKeyEvent.getKeyCode();
					if ((code == KeyEvent.VK_DOWN) || (code == downCode)) {
						pKeyEvent.getComponent().transferFocus();
					} else if((code == KeyEvent.VK_UP) || (code == upCode)) {
						pKeyEvent.getComponent().transferFocusBackward();
					} else if (code == KeyEvent.VK_ENTER) {
						((AbstractButton)pKeyEvent.getComponent()).doClick();
					}
				}
			}
		};
		playerPanel.addKeyListener(nextPlayer);
		for (final String name : players) {
			JButton btn = new JButton(name);
			btn.setCursor(HAND_CURSOR);
			btn.setRolloverEnabled(true);
			btn.setFont(getPlayerFont(btn.getFont()));
			btn.addKeyListener(nextPlayer);
			playerPanel.add(btn);
			ActionListener playerId = e -> {
				((JComponent)dlg.getContentPane()).putClientProperty(PLAYER_PROPERTY, name);
				dlg.setVisible(false);
			};
			btn.addActionListener(playerId);
		}
		dlg.add(playerPanel, BorderLayout.CENTER);
		final JLabel setDisplay = new JLabel(new SetIcon(foundSet), SwingConstants.CENTER);
		JPanel setPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		setPanel.add(setDisplay);
		dlg.add(setPanel, BorderLayout.PAGE_START);
		dlg.setLocationRelativeTo(mainFrame);
		dlg.pack();
		dlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dlg.setVisible(true);
		return ((JComponent)dlg.getContentPane()).getClientProperty(PLAYER_PROPERTY).toString();
	}
	
	private static Font playerFont=null;
	private static Font getPlayerFont(Font starter) {
		if (playerFont == null) {
			playerFont = starter.deriveFont(Font.BOLD, starter.getSize()*2);
		}
		return playerFont;
	}
	// card count compared to initial (minimum) table Size
	private boolean tableIsTooSmall() { return table.getComponentCount() <= (table.gridColumns * table.gridRows); }

	private int getIndexOf(CardButton cd) {
		Component[] all = table.getComponents();
		for (int ii=0; ii<all.length; ++ii) {
			//noinspection ObjectEquality
			if (all[ii] == cd) {
				return ii;
			}
		}
		return -1;
	}

	private void processBadState() {
		Toolkit.getDefaultToolkit().beep();
		for (CardButton cd : selectedBunch) {
			cd.setSelected(false);
		}
		selectedBunch.clear();
	}
	
	// Card Classes and Tools
	
	private enum Hue implements Element {
		red(0, sRed),
		green(1, sGreen),
		blue(2, sBlue),
		;
		private final int val;
		private final ColorReference color;
		Hue(int v, ColorReference clr) {
			val = v; 
			color = clr;
		}

		private Paint getStripes() { return color.getStripes(); }

		@Override
		public int getVal() { return val; }
	}
	private enum Figure implements Element {
		// We call them these but we can draw anything. See CardButton for what we draw.
		oval(0, getoval()),
		square(1, getSquare()),
		triangle(2, getTriangle()),
		;
		private final int val;
		private final Area shape;
		Figure(int v, Area shape) { val = v; this.shape = shape; }
		@Override
		public int getVal() { return val; }
		public Area getFigure() { return shape; }
	}
	private enum Fill implements Element {
		hollow(0),
		shaded(1),
		solid(2),
		;
		private final int val;
		Fill(int v) { val = v; }
		@Override
		public int getVal() { return val; }
	}
	private enum Count implements Element {
		one(0),
		two(1),
		three(2),
		;
		private final int val;
		Count(int v) { val = v; }
		@Override
		public int getVal() { return val; }
	}
	@FunctionalInterface
	private interface Element {
		int getVal();
	}
	
	private static class SetState {
		Hue[] hues = new Hue[3];
		Figure[] figures = new Figure[3];
		Fill[] fills = new Fill[3];
		Count[] counts = new Count[3];
		
		private final boolean isGood;

		SetState(Card... cards) {
			assert cards.length == 3;
			int index = 0;
			for (Card cd:cards) {
//				System.out.println(cd);
				hues[index] = cd.hue;
				figures[index] = cd.figure;
				fills[index] = cd.fill;
				counts[index] = cd.count;
				index++;
			}
			isGood = test(hues) && test(figures) && test(fills) && test(counts);
		}

		/**
		* Determines if this element is valid set.
		*
		* @param values Array of elements
		* @param <T> Element type
		* @return true iff it's a set for this element.
		*/
		@SuppressWarnings("ObjectEquality")
		private <T extends Element> boolean test(T[] values) {
			boolean lowMatch = values[0] == values[1];
			boolean highMatch = values[1] == values[2];
			boolean endMatch = values[0] == values[2];
			
			// It's a set of the match values are all true, or all false.
			return (lowMatch == highMatch) && (lowMatch == endMatch); // test if they're all the same.
		}
	}
	
	private static final int CardMax = 81; // 3^4
	private static class Card implements Comparable<Card>{
		private final Hue hue;
		private final Figure figure;
		private final Fill fill;
		private final Count count;
		private final int mIndex;

		Card(int index) {
			mIndex = index;
			assert index<CardMax;
			int iFigure = index%3;
			figure = getElement(iFigure, Figure.values());
			index /= 3;
			int iFill = index%3;
			fill = getElement(iFill, Fill.values());
			index/=3;
			int iHue = index%3;
			hue = getElement(iHue, Hue.values());
			index /= 3;
			int iCount = index; // don't bother with % on the last one
			count = getElement(iCount, Count.values());
			assert index < 3;
		}

		private static <T extends Element> T getElement(int index, T[] values) {
			for (T el:values) {
				if (el.getVal() == index) {
					return el;
				}
			}
			assert false : String.format("getElement(%d, %s)", index, values[0].getClass());
			// noinspection ProhibitedExceptionThrown
			throw new RuntimeException(); // shouldn't happen
		}

		@Override
		public int compareTo(Card o) {
			// This sorts first by number, then color, then shape, then fill. This is the reverse order from 
			// card creation.
//			System.out.printf("%s =? %s\n", this, o);
			//noinspection SubtractionInCompareTo
			return mIndex - o.mIndex;
		}

		// probably not needed, but here for consistency.
		@Override
		public boolean equals(final Object o) {
			return (o instanceof Card) && (mIndex == ((Card) o).mIndex);
		}

		@Override
		public int hashCode() {
			return mIndex;
		}

		@Override
		public String toString() {
			return String.format("%d %-5s %-6s %-8s (%2d)", count.val+1, hue, fill, figure, mIndex); // NON-NLS
		}
	}
	
	@SuppressWarnings("ClassExtendsConcreteCollection")
	private static class Deck extends LinkedList<Card> {
//		private Random rnd=null;
		private int removeCount = 0;
		
		private void resetCount() {
			removeCount = 0;
		}

		Deck() {
			super();
		}

		@Override
		public Card remove(final int i) {
			removeCount++;
			return super.remove(i);
		}

		public void shuffle() {
			clear();
			long seed = System.currentTimeMillis();
//			long seed = 1374511642569L;
//			long seed = 1374142277847L; // reproduces problem at 14 sets
			
			// Also try 1374142366125L;
//			long seed = 1374441459663L; // reproduces problem in 11 sets.
			System.out.printf("\t\t\tlong seed = %dL;\n", seed);
			Random rnd = new Random(seed);
			for (int ii=0; ii<CardMax; ++ii) {
				Card element = new Card(ii);
				insertRandom(element, rnd);
			}
//			// These give us a 21-card starting view.
//			bump(11);
//			bump(38);
//			bump(71);
//			bump(79);
//			bump(78);
//			bump(19);
//			bump(46);
//			bump(62);
//			bump(53);
//			bump(26);
//			bump(20);
//			bump(47);
//			bump(58);
//			bump(40);
//			bump(13);
//			bump(10);
//			bump(37);
//			bump(68);
//			bump(76);
//
//			bumpRandom(66, rnd);
//			bumpRandom(11, rnd);
//			bumpRandom(38, rnd);
//			bumpRandom(71, rnd);
//			bumpRandom(79, rnd);
//			bumpRandom(78, rnd);
//			bumpRandom(19, rnd);
//			bumpRandom(46, rnd);
//			bumpRandom(62, rnd);
//			bumpRandom(53, rnd);
//			bumpRandom(26, rnd);
//			bumpRandom(20, rnd);
//			bumpRandom(47, rnd);
//			bumpRandom(58, rnd);
//			bumpRandom(40, rnd);
//			bumpRandom(13, rnd);
//			bumpRandom(10, rnd);
//			bumpRandom(37, rnd);
//			bumpRandom(68, rnd);
//			bumpRandom(76, rnd);
//			bumpRandom(66, rnd);
//			int nonMember = get(20).mIndex;
//			bump(nonMember, rnd.nextInt(21));
//		}
//		
//		private void bumpRandom(int cdNum, Random rnd) {
//			bump(cdNum, rnd.nextInt(21));
//		}
//		
//		private void bump(int cdNum) {
//			bump(cdNum, 0);
//		}
//		
//		private void bump(int cdNum, int where) {
//			for (Card cd: this) {
//				if (cd.mIndex == cdNum) {
//					remove(cd);
//					add(where, cd);
//					return;
//				}
//			}
		}

		private void insertRandom(Card pElement, Random rnd) {
			int where = (rnd.nextInt(size()+1));
			add(where, pElement);
		}
	}

	private int countSetsFromTable() {
		List<Card> cds = getTableCards();
		return countSets(cds);
	}

	private List<Card> getTableCards() {
		Component[] allCards = table.getComponents();
		List<Card> cds = new ArrayList<>(allCards.length);
		for (Component cmp : allCards) {
			cds.add(((CardButton) cmp).card);
		}
		return cds;
	}

	private int countSets(final List<Card> cds) {
		allSets.clear();
		int setCount = 0;
		for (int c1=0; c1 < (cds.size() - 2); ++c1) {
			Card card1 = cds.get(c1);
			for (int c2=c1+1; c2 < (cds.size() - 1); ++c2) {
				Card card2 = cds.get(c2);
				for (int c3=c2+1; c3<cds.size(); ++c3) {
					Card card3 = cds.get(c3);
					SetState state = new SetState(card1, card2, card3);
					if (state.isGood) {
						setCount++;
						allSets.add(new SetIcon(card1, card2, card3));
					}
				}
			}
		}
		return setCount;
	}

	private class CountView extends JPanel {
		private final JLabel countLabel = makeCountLabel();

		CountView() {
			super(new BorderLayout(3,3));
			
//			JButton print = new JButton("P");
//			print.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(final ActionEvent actionEvent) {
//					for (int c=0; c<table.getComponentCount(); ++c) {
//						CardButton cardButton = (CardButton) table.getComponent(c);
//						System.out.printf("%2d: %s (%b)\n", c, cardButton.card, cardButton.animatingIcon.leftSide.card.equals(cardButton.card));
//						if ((c % 3) == 2) {
//							System.out.println("\n");
//						}
//					}
//				}
//			});
			
			JComponent combinedComponent = combine(countLabel, deckProgressBar, null);
			add(combinedComponent, BorderLayout.CENTER);
			add(timeLabel, BorderLayout.LINE_END);
//			MouseListener pop = new MouseAdapter() {
//				@Override public void mousePressed(MouseEvent e) { doPopup(e); }
//				@Override public void mouseClicked(MouseEvent e) { doPopup(e); }
//				@Override public void mouseReleased(MouseEvent e) { doPopup(e); }
//				private void doPopup(MouseEvent e) {
//					if (e.isPopupTrigger()) {
//						popUpSets((Component)e.getSource(), e.getX(), e.getY());
//					}
//				}
//			};
//			label.addMouseListener(pop);
//			timeLabel.addMouseListener(pop);
			
			add(makeChooserLabel(), BorderLayout.LINE_START);
		}
		
		private JComponent combine(@Nullable JComponent leading, @Nullable JComponent center, @Nullable JComponent trailing) {
			JPanel combinedPane = new JPanel(new BorderLayout(3,3));
			if (leading != null) {
				combinedPane.add(leading, BorderLayout.LINE_START);
			}
			if (center != null) {
				combinedPane.add(center, BorderLayout.CENTER);
			}
			if (trailing != null) {
				combinedPane.add(trailing, BorderLayout.LINE_END);
			}
			return combinedPane;
		}
		
		private URL getResource(String name) {
			URL resource = getClass().getResource(name);
			if (resource == null) {
				throw new IllegalStateException("Should not happen. Missing resource: " + name);
			}
			return resource;
		}
		
		private JComponent makeChooserLabel() {
			final URL resource = getResource("/colorChooser.png");
			JLabel chooserLabel = new JLabel(new ImageIcon(resource), SwingConstants.LEADING);
			chooserLabel.setToolTipText("Choose Colors");
			chooserLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent pMouseEvent) {
					launchColorChooser();
				}
			});
			chooserLabel.setBorder(new EmptyBorder(0, 8, 0, 8));
			return chooserLabel;
		}
		
		private JLabel makeCountLabel() {
			final ImageIcon popupIcon = new ImageIcon(getResource("/popup.png"));
			JLabel label = new JLabel(popupIcon, SwingConstants.LEADING);
			label.addMouseListener(new MouseAdapter() {
				private long cancelTime = 0L;

				// I have to perform a tricky test to make the button toggle the popup menu. If I just set and clear a flag
				// to indicate when the popup menu is visible, it won't work, because the click itself will hide the popup
				// menu before my mousePressed handler gets called. So by the time I enter this code, any visible popup menu has
				// already been hidden. So I test on the cancellation time. The time of cancellation will always be >= the 
				// mouseEvent time. So if some other event dismissed the popup, cancelTime will be less than the mouseEvent
				// time, and I'll know I should pop-up the menu. 
				// (Without this test, clicking the button when the menu is visible will hide, then reshow the menu.)
				// (This is tested on both Mac and Windows.)
				@Override
				public void mousePressed(MouseEvent pMouseEvent) {
					// This test will not show the menu only when the click event is what dismissed the menu.
					if (cancelTime < pMouseEvent.getWhen()) { // cancelTime is usually equal, sometimes greater than.
						showPopup(pMouseEvent); // only pops up when it's not yet visible.
					}
				}

				private void showPopup(MouseEvent evt) {
					Component component = evt.getComponent();
					final JPopupMenu popupMenu = popUpSets(component, component.getX(), component.getY() + component.getHeight());
					PopupMenuListener popListener = new PopupMenuListener() {
						@Override public void popupMenuWillBecomeVisible(PopupMenuEvent evt) { } 
						@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) { } 
						@Override public void popupMenuCanceled(PopupMenuEvent evt) { cancelTime = System.currentTimeMillis(); }
					};
					popupMenu.addPopupMenuListener(popListener);
				}
			});
			return label;
		}

		private JPopupMenu popUpSets(Component source, int x, int y) {
			JPopupMenu popper = new JPopupMenu();
			for (SetIcon set : allSets) {
				popper.add(new JLabel(set));
			}
			popper.show(source, x, y);
			return popper;
		}

		@SuppressWarnings("HardCodedStringLiteral")
		private void setCount(@Nullable List<Card> cards) {
			if (cards == null) {
				currentSetCount = countSetsFromTable();
			} else {
				currentSetCount = countSets(cards);
			}
			if (currentSetCount == 1) {
				countLabel.setText("1 Set");
			} else {
				countLabel.setText(String.format(Locale.getDefault(), "%d Sets", currentSetCount));
			}
		}
	}
	
	private static class SetIcon implements Icon {
		private final double scale;
		private final CardIcon[] cardIcons = new CardIcon[3];

		SetIcon(Card... cards) {
			scale = 1.0/3.0;
			assert cards.length == 3;
			Arrays.sort(cards);
			cardIcons[0] = new CardIcon(cards[0]);
			cardIcons[1] = new CardIcon(cards[1]);
			cardIcons[2] = new CardIcon(cards[2]);
			
		}
		
		SetIcon(SetIcon original) {
			scale = 2.0/3.0;
			int ii=0;
			for (CardIcon icon : original.cardIcons) {
				cardIcons[ii++] = icon;
			}
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g;
			setRenderingHints(g2);
			AffineTransform saved = g2.getTransform();
			g2.scale(scale, scale);
			try {
				cardIcons[0].paintIcon(c, g, x, y);
				g2.translate(CARD_WIDTH, 0);
				cardIcons[1].paintIcon(c, g, x, y);
				g2.translate(CARD_WIDTH, 0);
				cardIcons[2].paintIcon(c, g, x, y);
			} finally {
				g2.setTransform(saved);
			}
		}

		@Override
		public int getIconWidth() {
			//noinspection NumericCastThatLosesPrecision
			return (int) (cardIcons[0].getIconWidth() * 3 * scale);
		}

		@Override
		public int getIconHeight() {
			//noinspection NumericCastThatLosesPrecision
			return (int) (cardIcons[0].getIconHeight() * scale);
		}
	}
	
	@SuppressWarnings("ThisEscapedInObjectConstruction")
	private class FoundSetView extends JPanel {
		private int total;
		private final JPanel sets;
		private final JLabel totalView;

		FoundSetView(@Nullable String name) {
			super(new BorderLayout());
			total = 0;
			
			if (name == null) {
				sets = new JPanel(new GridLayout(14, 0, 8, 0));
				add(sets, BorderLayout.CENTER);
				foundMap.put("", this);
			} else {
				// topPanel gets both the player and the sets
				JPanel topPanel = new JPanel(new BorderLayout());
				add(topPanel, BorderLayout.PAGE_START);
				topPanel.add(new JLabel(name), BorderLayout.PAGE_START);
				sets = new JPanel(new GridLayout(0, 1, 8, 0));
				foundMap.put(name, this);
				topPanel.add(sets, BorderLayout.CENTER);
			}
			totalView = new JLabel(" ");
			add(totalView, BorderLayout.PAGE_END);
		}
		
		public void addSet(SetIcon set) {
			total ++;
			sets.add(new JLabel(set));
			//noinspection HardCodedStringLiteral
			totalView.setText(String.format(Locale.getDefault(), "Total: %s", total));
		}
	}
	
	static Paint getPaint(Color stripeColor) {
		float[] hsb = Color.RGBtoHSB(stripeColor.getRed(), stripeColor.getGreen(), stripeColor.getBlue(), null);
		float hue = hsb[0];
		float sat = hsb[1];
		float brt = hsb[2];
		float deltaB = ((1.0f - brt) * 2.0f) / 3.0f;
		Color startColor = Color.getHSBColor(hue, 0.2f * sat, 1.0f);
		Color endColor = Color.getHSBColor(hue, 0.6f * sat, 1.0f - deltaB);
		return new GradientPaint(0.0f, 0.0f, startColor, 0.0f, (float)objectHeight, endColor);
//		// Normal stripes will color one out of every three rows, although it draws as 
//		// one out of every two rows at 2/3 scale for the "credit" dialog. [see getPlayer(SetIcon)] 
//		// Small stripes color three out of every six rows, which will get scaled down by a 
//		// factor of three.
//		final int colorLimit = small? 3 : 1;
//		final int sHt = small? 6 : 3;
//		final int wid = 10;
//		byte[] red = new byte[] {(byte)255, (byte)stripeColor.getRed() };
//		byte[] grn = new byte[] {(byte)255, (byte)stripeColor.getGreen() };
//		byte[] blu = new byte[] {(byte)255, (byte)stripeColor.getBlue() };
//		byte[] alpha = new byte[] { 0, (byte) 255 };
//		IndexColorModel cModel = new IndexColorModel(1, 2, red, grn, blu, alpha);
//		BufferedImage stripes = new BufferedImage(wid, sHt, BufferedImage.TYPE_BYTE_BINARY, cModel);
//		WritableRaster raster = stripes.getRaster();
//		byte[] one = new byte[] { (byte) 1 };
//		for (int xx=0; xx<wid; ++xx) {
//			for (int yy=0; yy<colorLimit; ++yy) {
//				raster.setDataElements(xx, yy, one);
//			}
//		}
//		byte[] zero = new byte[] { (byte) 0 };
//		for (int xx=0; xx<wid; ++xx) {
//			for (int yy=colorLimit; yy<sHt; ++yy) {
//				raster.setDataElements(xx, yy, zero);
//			}
//		}
//		Rectangle2D anchor = new Rectangle2D.Double(0, 0, wid, sHt);
//		return new TexturePaint(stripes, anchor);
	}

	public static class ColorReference {
		private Color color=null;
		private Paint stripes;

		public ColorReference(Color color) { setColor(color); }
		public Paint getStripes() { return stripes; }
		public Color getColor() { return color; }
		public final void setColor(Color clr) {
			color = clr;
			stripes = getPaint(clr);
		}
	}
	
	private static class ColorChooser extends JLabel {
		public static final double colorDiameter = 180;
		public static final double hueIconDiameter = 185;
		public static final double hueIconRadius = hueIconDiameter/2;
		public static final int size = 220;

		ColorChooser(ColorReference red, ColorReference green, ColorReference blue) {
			super(new ColorRing(red, green, blue));
			ColorRingControl mControl = new ColorRingControl();
			addMouseListener(mControl);
			addMouseMotionListener(mControl);
			ColorRing ring = (ColorRing) getIcon();
			for (HueIcon hue: ring.controls) {
				mControl.addHueIcon(hue);
			}
		}

		/**
		 * This is the slider icon that marks the hue.
		 */
		private static class HueIcon implements Icon {
			private final Stroke outline = new BasicStroke(1.0f);
			private final ColorReference color;
			private final Shape shape;
			private boolean isSelected=false;
			HueIcon(ColorReference clr) {
				color = clr;
				GeneralPath path = new GeneralPath();
				path.moveTo(0.0f, 0.0f);
				path.lineTo(10.0f, 3.0F);
				path.lineTo(10.0F, -3.0F);
				path.lineTo(0.0f, 0.0f);
				shape = new Area(path);
//				shape = new Arc2D.Double(0, 0, 10, 10, 0, 360, Arc2D.OPEN);
			}
			@Override
			public int getIconWidth() { return size; }
			@Override
			public int getIconHeight() { return size; }

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				double radians = getHueRadians();
				Point2D hueLoc = getHueLocation(radians);
				Graphics2D g2 = (Graphics2D) g;
				setRenderingHints(g2);
				AffineTransform savedTransform = g2.getTransform();
				double halfSize = size/2.0;
				g2.translate(x+halfSize+hueLoc.getX(), y+halfSize+hueLoc.getY());
				g2.rotate(-radians);
				g2.setColor(isSelected? Color.white : Color.lightGray);
				g2.fill(shape);
				g2.setStroke(outline);
				g.setColor(Color.black);
				g2.draw(shape);
				
				g2.setTransform(savedTransform);
			}
			
			private Point2D getHueLocation(double radians) {
				double x = StrictMath.cos(radians)*hueIconRadius;
				double y = -StrictMath.sin(radians)*hueIconRadius;
				return new Point2D.Double(x, y);
			}

			private void setRadians(Point2D pt) {
				double radians = StrictMath.atan2(-pt.getY(), pt.getX());
				double hue = radians/2/Math.PI;
				// hue is in the range of -0.5 to 0.5 here, so I add 1 if it's negative
				if (hue < 0) { hue += 1.0; }
				//noinspection NumericCastThatLosesPrecision
				color.setColor(ColorRing.hueToColor((float)hue));
			}

			private double getHueRadians() {
				float[] hsb = new float[3];
				Color theColor = color.getColor();
				Color.RGBtoHSB(theColor.getRed(), theColor.getGreen(), theColor.getBlue(), hsb);
				return hsb[0]*Math.PI*2;
			}

			private void setSelected(boolean s) {
				isSelected = s;
			}
		}

		private static class ColorRing implements Icon {
			public static final double arcLength = 360.0/255.0;
			public static final float arcStrokeWidth = 20.0f;
			public static final float rimStroke = 1.75f;
			public static final double innerDiameter = colorDiameter-arcStrokeWidth;
			public static final double outerDiameter = colorDiameter+arcStrokeWidth;
			public static final double offset = (size- outerDiameter)/2;
			private final Stroke arcStroke = new BasicStroke(arcStrokeWidth);
			private final List<HueIcon> controls = new LinkedList<>();
		
			private final ColorIcon clrIcon;
			
			ColorRing(ColorReference red, ColorReference green, ColorReference blue) {
				clrIcon = new ColorIcon(red, green, blue);
				controls.add(new HueIcon(red));
				controls.add(new HueIcon(green));
				controls.add(new HueIcon(blue));
			}

			@Override
			public int getIconWidth() { return size; }
			@Override
			public int getIconHeight() { return size; }

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g;
				
				// Anti-aliasing at this point produces a Moire pattern.
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g2.setStroke(arcStroke);
				for (int ii=0; ii<255; ++ii) {
					double start = ii*arcLength;
					Arc2D arc = new Arc2D.Double(offset, offset, outerDiameter, outerDiameter, start, arcLength, Arc2D.PIE);
					float hue = ii / 256.0f;
					g2.setColor(hueToColor(hue));
					g2.fill(arc);
				}
			
				// Now we can turn on anti-aliasing.
				setRenderingHints(g2);
				double innerOffset = (size-innerDiameter)/2;
				Arc2D inneroval = new Arc2D.Double(innerOffset, innerOffset, innerDiameter, innerDiameter, 0, 360.0, Arc2D.PIE);
				g2.setColor(Color.white);
				g2.fill(inneroval);
				g2.setStroke(new BasicStroke(rimStroke));
				g2.setColor(Color.GRAY);
				Arc2D innerArc = new Arc2D.Double(innerOffset, innerOffset, innerDiameter, innerDiameter, 0, 360.0, Arc2D.CHORD);
				g2.draw(innerArc);
				double outerOffset = (size-outerDiameter)/2;
				Arc2D outerArc = new Arc2D.Double(outerOffset, outerOffset, outerDiameter, outerDiameter, 0, 360.0, Arc2D.CHORD);
				g2.draw(outerArc);
			
				int xLoc = (getIconWidth()-clrIcon.getIconWidth())/2;
				int yLoc = (getIconHeight()-clrIcon.getIconHeight())/2;
				clrIcon.paintIcon(c, g2, xLoc, yLoc);
				for (HueIcon icn : controls) {
					icn.paintIcon(c, g2, x, y);
				}
			}

			private static Color hueToColor(float pHue) {return Color.getHSBColor(pHue, 1.0f, darkener(pHue));}

			/**
			 * This is the brightness function that darkens everything on the upper portion of the hue oval.
			 * @param hue The hue, in the range of 0 to 1, where 1 means 360 degrees
			 * @return A brightness in the range of 0 to 1
			 */
			private static float darkener(float hue) {
				final float hueMax = 1.0f;
				final float degMax = 360.0f;
				final float toHue = hueMax/degMax;    // 1/360
				final float darkest = 0.7f;
				final float darkness = 1.0f-darkest;  // darkness = 0.3

				// constants are in degrees
				
				final float headCenter = 30.0f;
				final float gapDelta = 15.0f;
				final float gapDeltaHue = gapDelta*toHue;             // = 15/360
				final float headStart = (headCenter-gapDelta)*toHue;  // = 15/360
				final float headEnd = (headCenter + gapDelta)*toHue;  // = 45/360
				final float tailStart = headStart + (hueMax / 2.0f);      // = 15/360 + 1/2 = 195/360
				final float tailEnd = headEnd + (hueMax / 2.0f);          // = 45/360 + 1/2 = 225/360
				
				assert hue >= -0.0;
				assert hue <= 1.0;
				
				// radians here start at the right edge of the oval, at Red (hue = 0) and increases
				// counterclockwise. It starts to get dark at 15 degrees, is fully dark at 45 degrees, 
				// and stays dark through 195 degrees (just past the left edge), which is most of the top of 
				// the oval. It gets back to normal at 225 degrees (45 short of the bottom), and 
				// continues on at normal through the rest of the arc back up to the right edge.
				if (hue < headStart) {
					return 1.0f;
				}
				if (hue < headEnd) {
					final float fraction = (hue - headStart) / (gapDeltaHue * 2);
					return 1.0f - (fraction * darkness);
				}
				if (hue < tailStart) {
					return darkest;
				}
				if (hue < tailEnd) {
					final float fraction = (hue - tailStart) / (gapDeltaHue * 2);
					return darkest + (fraction * darkness);
				}
				return 1.0f;
			}
		}

		/**
		 * This draws the three pie pieces in the center that show the three colors next to each other.
		 */
		private static class ColorIcon implements Icon {
			public static final double diameter = 90;
			public static final double radius = diameter/2;
			private static final int clSize = 100;
			private final BasicStroke stroke = new BasicStroke(3.0f);
			private final ColorReference one;
			private final ColorReference two;
			private final ColorReference three;
			private static final double offset = (clSize -diameter)/2;
			Arc2D arcOne = new Arc2D.Double(offset, offset, diameter, diameter, 330.0, 120.0, Arc2D.PIE);
			Arc2D arcTwo = new Arc2D.Double(offset, offset, diameter, diameter, 90.0, 120.0, Arc2D.PIE);
			Arc2D arcThree = new Arc2D.Double(offset, offset, diameter, diameter, 210.0, 120.0, Arc2D.PIE);

			ColorIcon (ColorReference one, ColorReference two, ColorReference three) {
				this.one = one;
				this.two = two;
				this.three = three;
			}
			@Override
			public int getIconWidth() { return clSize; }
			@Override
			public int getIconHeight() { return clSize; }

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g;
				setRenderingHints(g2);
				AffineTransform savedTransform = g2.getTransform();
				g2.translate(x, y);
				setRenderingHints(g2);
				g2.setColor(one.getColor());
				g2.fill(arcOne);
				g2.setColor(two.getColor());
				g2.fill(arcTwo);
				g2.setColor(three.getColor());
				g2.fill(arcThree);
				g2.setColor(Color.white);
				g2.setStroke(stroke);
				g2.translate(clSize /2, clSize /2);
				g2.rotate(Math.PI/6.0);
				Line2D line = new Line2D.Double(0.0, 0.0, radius, 0.0);
				g2.draw(line);
				g2.rotate((2.0 * Math.PI) / 3.0);
				g2.draw(line);
				g2.rotate((2.0 * Math.PI) / 3.0);
				g2.draw(line);
				g2.setTransform(savedTransform);
			}
		}

		private static class ColorRingControl extends MouseInputAdapter {
			private final AffineTransform transform = AffineTransform.getScaleInstance(1.0, 1.0);
			private final List<HueIcon> icons = new LinkedList<>();
			private @Nullable HueIcon selectedIcon = null;
			ColorRingControl() {
				super();
				transform.translate(-size/2.0, -size/2.0);
			}
	
			private void addHueIcon(HueIcon icon) {
				icons.add(icon);
			}
	
			@Override
			public void mouseMoved(MouseEvent pMouseEvent) {
				Point2D location = transform.transform(pMouseEvent.getPoint(), null);
				for (HueIcon icon: icons) {
					Point2D hueLocation = icon.getHueLocation(icon.getHueRadians());
					// if hue location is within 12 pixels of the mouse location...
					if (hueLocation.distanceSq(location) < 144) {
						//noinspection ObjectEquality
						if ((selectedIcon != null) && (selectedIcon != icon)) {
							selectedIcon.setSelected(false);
						}
						icon.setSelected(true);
						selectedIcon = icon;
						repaintPie(pMouseEvent);
						return;
					} else {
						//noinspection ObjectEquality
						if (icon == selectedIcon) {
							selectedIcon = null;
							icon.setSelected(false);
							repaintPie(pMouseEvent);
						}
					}
				}
			}

			private void repaintPie(MouseEvent pMouseEvent) {
				// We could say pMouseEvent.getComponent().repaint(), but that would just repaint the pie in 
				// the middle. By calling getParent() twice, we also repaint the AppIcon, owned by the dialog.
				Component component = pMouseEvent.getComponent();
				component.getParent().getParent().repaint();
			}

			@Override
			public void mouseDragged(MouseEvent pMouseEvent) {
				Point2D location = transform.transform(pMouseEvent.getPoint(), null);
				if (selectedIcon != null) {
					selectedIcon.setRadians(location);
					repaintPie(pMouseEvent);
				}
			}
		}
	}
	
	private JLabel makeDeckIcon() {
		Icon deckIcon = new Icon() {
			private static final int cardWidth = 10;
			private static final int spacing = 2;
			private static final int deckWidth = (cardWidth * 27) + (2 * spacing); // 10 pixels by 27 rounds
			private static final int deckHeight = 12;
			@Override
			public void paintIcon(Component pComponent, Graphics pGraphics, int x, int y) {
				int cards = deck.size()/3;
				Graphics2D g2 = (Graphics2D) pGraphics;
				setRenderingHints(g2);
				g2.translate(x+spacing, y);
				g2.setColor(Color.gray);
				for (int ii=0; ii<cards; ++ii) {
					g2.fillRect(ii*cardWidth, 0, cardWidth-1, deckHeight);
				}
				g2.setColor(Color.lightGray);
				for (int ii=cards; ii<27; ++ii) {
					g2.fillRect(ii*cardWidth, 0, cardWidth-1, deckHeight);
				}
			}

			@Override
			public int getIconWidth() {
				return deckWidth;
			}

			@Override
			public int getIconHeight() {
				return deckHeight;
			}
		};
		return new JLabel(deckIcon, SwingConstants.TRAILING);
	}
	
	private static class SetButtonUI extends BasicToggleButtonUI { }
	
	private static void installSetUI() {
		UIDefaults uiDefaults = UIManager.getDefaults();
		uiDefaults.put("ToggleButtonUI", SetButtonUI.class.getName());
	}
	
	private static class AnimatingIcon implements Icon {
		private @Nullable CardIcon leftSide =null;
		private @Nullable CardIcon rightSide =null;
		private final Animator localAnimator;
		private final Color bgColor;
		private final Queue<CardIcon> iconList = new LinkedList<>();
		private boolean isAnimating = false;
		
		AnimatingIcon(Animator animator, Color bgColor) {
			this.localAnimator = animator;
			this.bgColor = bgColor;
		}
		
		public void prepareBoth(@Nullable CardIcon left, @Nullable CardIcon right) {
			this.leftSide = left;
			this.rightSide = right;
		}
		
		public void prepare(@Nullable CardIcon... newItems) {
			// If I pass null, It makes the whole array nullable instead of giving me a null element!
			if (newItems == null) {
				iconList.add(null);
				isAnimating = true;
			} else {
				for (CardIcon item: newItems) {
					iconList.add(item);
					isAnimating = true;
				}
			}
		}

		/**
		 * Load the next icon.
		 * @return the value of hasMore() before the next icon was loaded.
		 */
		private boolean loadNext() {
			if (hasMore()) {
				CardIcon icon = iconList.poll();
				prepareBoth(icon, leftSide);
				return true;
			} else {
				isAnimating = false;
				return false;
			}
		}
		
		private boolean hasMore() {
			return !iconList.isEmpty();
		}

		@Override
		public void paintIcon(final Component component, final Graphics graphics, final int x, final int y) {
//			System.out.printf("paintIcon: %s, %s%n", leftSide, rightSide); // NON-NLS
			if (!isAnimating) {
				// Not animating. Just paint the left side.
				if (leftSide != null) {
					leftSide.paintIcon(component, graphics, x, y);
				}
//				rightSide.paintIcon(component, graphics, x, y); // temp removal (reversed)
			} else {
				int divider = (getIconWidth() * localAnimator.getFrame()) / localAnimator.getAnimationLength();
				graphics.setClip(x, y, divider, getIconHeight());
				if (leftSide == null) {
					graphics.setColor(bgColor);
					graphics.fillRect(x, y, getIconWidth(), getIconHeight());
				} else {
					leftSide.paintIcon(component, graphics, x, y);
				}
				graphics.setClip(x+divider, y, getIconWidth()-divider, getIconHeight());
				if (rightSide == null) {
					graphics.setColor(bgColor);
					graphics.fillRect(x, y, getIconHeight(), getIconHeight());
				} else {
					rightSide.paintIcon(component, graphics, x, y);
				}
				graphics.setClip(null);
			}
		}

		@Override
		public int getIconWidth() {
			return CARD_WIDTH;
		}

		@Override
		public int getIconHeight() {
			return CARD_HT;
		}
		
		@Override
		public String toString() {
			return String.format("Left:  %s%nRight: %s%n%d remaining (a=%b)%n", leftSide, rightSide, iconList.size(), isAnimating);
		}
	}
	
	// assertions:
	private static boolean tableCheck() {
		Table table = instance.table;
		for (int ii=0; ii<table.getComponentCount(); ++ii) {
			CardButton button = (CardButton) table.getComponent(ii);
			Card card = button.card;
			AnimatingIcon icon = (AnimatingIcon) button.getIcon();
			if (icon.leftSide != null) {
				Card iconCard = icon.leftSide.card;
				//noinspection ObjectEquality
				if (card != iconCard) { return false; }
			}
		}
		return true;
	}
	
	private static boolean deckCheck() {

		java.util.Set<Card> deckSet = new HashSet<>(instance.deck);
		
		java.util.Set<Card> tableSet = new HashSet<>();
		
		Table table = instance.table;
		for (int ii=0; ii<table.getComponentCount(); ++ii) {
			CardButton btn = (CardButton) table.getComponent(ii);
			if (tableSet.contains(btn.card)) {
				System.out.println("Duplicate in table: " + btn.card);
				return false;
			}
			tableSet.add(btn.card);
		}
		
		java.util.Set<Card> doneSet = new HashSet<>();

		Map<String, FoundSetView> fMap = instance.foundMap;
		for (FoundSetView foundSetView: fMap.values()) {
			for (int ii=0; ii<foundSetView.sets.getComponentCount(); ++ii) {
				SetIcon setIcon = (SetIcon) ((JLabel)foundSetView.sets.getComponent(ii)).getIcon();
				for (CardIcon cardIcon : setIcon.cardIcons) {
					if (doneSet.contains(cardIcon.card)) {
						System.out.println("Duplicate in foundSet: " + cardIcon);
						return false;
					}
					doneSet.add(cardIcon.card);
				}
			}
		}
		
		int total = deckSet.size() + tableSet.size() + doneSet.size();
		if (total != CardMax) {
			System.out.printf("Total of %d + %d + %d = %d\n", deckSet.size(), tableSet.size(), doneSet.size(), total);
			return false;
		}
		
		// now check for overlap:
		for (Card card: deckSet) {
			if (tableSet.contains(card)) {
				System.out.println("Dup in deck and table: " + card);
				return false;
			}
			if (doneSet.contains(card)) {
				System.out.println("Dup in deck and done: " + card);
				return false;
			}
		}
		
		for (Card card: tableSet) {
			if (doneSet.contains(card)) {
				System.out.println("Dup in table and done: " + card);
				return false;
			}
		}
		
		return true;
	}

	private static class Animator {
		private final int duration = 650; // milliseconds
		private final int fps = 30; // frames per second
		private final int animationLength = duration/fps;
		private int frame=animationLength;
		private @Nullable Runnable cleanupTask = null;
		ActionListener action = actionEvent -> animateFrame();
		private final Timer timer = new Timer(fps, action);

		java.util.Set<AbstractButton> buttonSet = new HashSet<>();

		public void addComponent(AbstractButton button) {
			buttonSet.add(button);
		}
		
		public void begin(Runnable cleanup) {
			cleanupTask = cleanup;
			begin();
		}
		
		public void begin() {
			assert !isRunning;
			timer.start(); // temp removal
		}
		
		private volatile boolean isRunning = false;
		private void animateFrame() {
			assert !isRunning;
			isRunning = true;
			try {
				if (frame >= animationLength) {
					frame = 0;
					for (AbstractButton button: buttonSet) {
						((AnimatingIcon)button.getIcon()).loadNext();
					}
				}
				for (AbstractButton button: buttonSet) {
	
					button.repaint();
				}
				frame++;
				if (frame >= animationLength) {
					for (Iterator<AbstractButton> iterator = buttonSet.iterator(); iterator.hasNext(); ) {
						AbstractButton button = iterator.next();
						AnimatingIcon icon = (AnimatingIcon) button.getIcon();
						if (!icon.loadNext()) {
							iterator.remove();
						}
					}
					if (buttonSet.isEmpty()) {
						timer.stop();
						if (!timer.isRunning() && (cleanupTask != null)) {
							System.out.println("Cleanup!");
							cleanupTask.run();
							cleanupTask = null;
						}
						assert tableCheck();
						assert deckCheck();
					} else {
						frame = 0;
					}
				}

			} finally {
				isRunning = false;
			}
		}
		
		public boolean isLastFrame() {
			return frame >= animationLength;
		}

		public int getFrame() {
			return frame;
		}

		public int getAnimationLength() {
			return animationLength;
		}
	}
	
//	private class DummyView extends JPanel {
//		Random random = new Random(System.currentTimeMillis());
//
//		private DummyView() {
//			setBackground(GREEN_FELT);
//			setLayout(new FlowLayout());
//			
//			final CardButton button = new CardButton(new Card(0));
//			final Animator animator = new Animator();
//			final AnimatingIcon icon = new AnimatingIcon(animator, GREEN_FELT);
//			final AnimatingIcon icon2 = new AnimatingIcon(animator, GREEN_FELT);
//			final AnimatingIcon icon3 = new AnimatingIcon(animator, GREEN_FELT);
//			button.setIcon(icon);
//			final CardButton b2 = new CardButton(new Card(1));
//			b2.setIcon(icon2);
//			final CardButton b3 = new CardButton(new Card(3));
//			b3.setIcon(icon3);
//
//			add(button);
//			add(b2);
//			add(b3);
//			
//			JButton nextButton = new JButton("Next");
//			nextButton.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(final ActionEvent actionEvent) {
//					icon.prepare (null, new CardIcon(new Card(random.nextInt(CardMax))), null, new CardIcon(new Card(random.nextInt(CardMax))));
//					icon2.prepare(null, new CardIcon(new Card(random.nextInt(CardMax))), null, new CardIcon(new Card(random.nextInt(CardMax))));
//					icon3.prepare(null, new CardIcon(new Card(random.nextInt(CardMax))));
//					animator.addComponent(button);
//					animator.addComponent(b2);
//					animator.addComponent(b3);
//					animator.begin();
//				}
//			});
//			add(nextButton);
//			
//			JButton printButton = new JButton("Print");
//			printButton.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(final ActionEvent actionEvent) {
//					print(button);
//					print(b2);
//					print(b3);
//				}
//				
//				private void print(AbstractButton btn) {
//					AnimatingIcon icon = (AnimatingIcon) btn.getIcon();
//					icon.print();
//				}
//			});
//			add(printButton);
//		}
//	}
}




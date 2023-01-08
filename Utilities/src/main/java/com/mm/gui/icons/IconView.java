package com.mm.gui.icons;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import com.mm.gui.Borders;
import com.mm.gui.LandF;
import org.jetbrains.annotations.NonNls;

/**
 * Created by IntelliJ IDEA.
 * User: miguelmunoz
 * Date: Jul 11, 2010
 * Time: 10:54:41 PM
 */
@SuppressWarnings({"HardCodedStringLiteral"})
public class IconView extends JPanel {
	private JPanel centerView = null;
	
	public static void main(String[] args) {
		LandF.Nimbus.set();
		JFrame frame = new JFrame();
		frame.add(new IconView());
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private IconView() {
		super(new BorderLayout());
		add(makeFamilyChooser(), BorderLayout.LINE_START);
	}
	
	private JPanel makeFamilyChooser() {
		JPanel familyChooserPanel = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel(new GridLayout(0, 1));
		familyChooserPanel.add(buttons, BorderLayout.NORTH); // NON-NLS
//		FamilyButton crystal = new FamilyButton("Crystal", new CrystalFamily());
//		buttons.add(crystal);
		
		FamilyButton fatCow = new FamilyButton("FatCow", new FatCowFamily());
		buttons.add(fatCow);
		
		JButton saveButton = new JButton("Save FC");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent pActionEvent) {
				if (centerView != null) {
					save(centerView);
				}
			}
		});
		buttons.add(saveButton);
		
		return familyChooserPanel;
	}
	
	private void save(JPanel panel) {
		try {
			writePane(panel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writePane(JPanel panel) throws IOException {
		File file = new File("Fatcow.png");
		ImageOutputStream oStream = ImageIO.createImageOutputStream(file);
		try {
			BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics gr = image.getGraphics();
			panel.print(gr);
			ImageIO.write(image, "png", oStream);
		} finally {
			oStream.close();
		}
	}
	
	private class FamilyButton extends JButton {
		FamilyButton(String name, final Family family) {
			super(name);
			ActionListener al = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JPanel view = family.makeView();
					centerView = view;
					JScrollPane scroller = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
									
					IconView.this.add(scroller, BorderLayout.CENTER);
					((JFrame)view.getRootPane().getParent()).setExtendedState(JFrame.MAXIMIZED_BOTH);
				}
			};
			addActionListener(al);
		}
	}
	
	private interface Family {
		JPanel makeView();
	}
	
	@SuppressWarnings({"MagicNumber"})
	private abstract class AbstractFamily implements Family {
//		private JPanel view = new JPanel(new GridLayout(100, 0));
		private String parentDir;
		AbstractFamily(String parentDir) {
			this.parentDir = parentDir;
		}
		
		@Override
		public JPanel makeView() {
			JPanel panel = new ViewPanel();
			Collection<String> strings = getIconPaths();
			List<String> stringList = new ArrayList<String>(strings.size());
			stringList.addAll(strings);
			//noinspection UnusedAssignment
			strings = null;
			int cols = (stringList.size() + 99) / 100;
			int total = stringList.size();
//                      int col = 0;
			for (int row = 0; row < 100; ++row) {
				for (int col = 0; col < cols; ++col) {
					int index = col * 100 + row;
					if (index < total) {
						String iconName = stringList.get(index);
						panel.add(makeIconComponent(iconName));
					}
				}			}
//			String[] subDirs = getSubDirectories();
//			File[] dirs = new File[subDirs.length];
//			int ii=0;
//			for (String subdir: subDirs) {
//							dirs[ii++] = new File(parentDir + '/' + subdir);
//			}
//			File firstDir = dirs[0];
//			File[] iconFiles = firstDir.listFiles();
//			for (File iFile : iconFiles) {
//							JComponent icon = makeIconComponent(file);
//			}
			Borders.addMatte(panel, 16, 16, 16, 0);
			return panel;
		}

		private class ViewPanel extends JPanel implements Scrollable {
			private ViewPanel() { super(new GridLayout(100, 0)); }

			@Override
			public Dimension getPreferredScrollableViewportSize() {
				return getPreferredSize();
			}

			@Override
			public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
				if (orientation == SwingConstants.VERTICAL) {
					return getComponent(0).getHeight();
				}
				return getComponent(0).getWidth();
			}

			@Override
			public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
				if (orientation == SwingConstants.VERTICAL) {
					return visibleRect.height - getComponent(0).getHeight();
				}
				return visibleRect.width - getComponent(0).getWidth();
			}

			@Override
			public boolean getScrollableTracksViewportWidth() { return false; }

			@Override
			public boolean getScrollableTracksViewportHeight() { return false; }
		}

		protected URL getParentDir() {
			final Class<? extends AbstractFamily> aClass = getClass();
			final URL resource = aClass.getResource(parentDir);
			//noinspection HardcodedLineSeparator,HardcodedFileSeparator
			System.err.printf("parent dir = %s\n%s\nResource = %s\n", parentDir, aClass, resource); // NON-NLS
			return resource;
		}
		protected String getParentDirName() { return parentDir; }
		
		abstract protected Collection<String> getIconPaths();
		
		abstract JComponent makeIconComponent(String iconName);
		
		protected Icon makeIcon(File file) {
			URL url = getClass().getResource(file.getAbsolutePath());
			Image img = Toolkit.getDefaultToolkit().createImage(url);
			return new ImageIcon(img);
		}
		
		abstract protected String[] getSubDirectories();
	}

	@SuppressWarnings({"HardcodedFileSeparator", "StringConcatenation"})
	private class FatCowFamily extends AbstractFamily {
		private static final char SLASH = '/';

		FatCowFamily() {
			super("fatcow-hosting-icons-2400");
		}

//		@Override
//		public JPanel makeView() {
//			
//		}

		@Override
		protected Collection<String> getIconPaths() {
			@NonNls String sub1 = getSubDirectories()[0];
			URL parentUrl = getParentDir();
			File parentDir;
			try {
				parentDir = new File(parentUrl.toURI());
			} catch (URISyntaxException e) {
				parentDir = new File("sub1");
			}
			File subDir = new File(parentDir, sub1);
			String[] fileNames = subDir.list(new PngFilter());
			Set<String> paths = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			for (@NonNls String name: fileNames) {
				//noinspection StringContatenationInLoop
				paths.add(sub1 + SLASH + name);
			}
			return paths;
		}

		@Override
		JComponent makeIconComponent(String iconName) {
			final String smallDirName = getParentDirName() + SLASH + iconName;
			ImageIcon icon = new ImageIcon(getClass().getResource(smallDirName));
//			System.err.println("SmlDir: " + smallDirName);
			final String bigDir = getSubDirectories()[1];
			String baseName = iconName.substring(iconName.lastIndexOf(SLASH)+1);
			final String bigDirName = getParentDirName() + SLASH + bigDir + SLASH + baseName;
//			System.err.println("BigDir: " + bigDirName);
			final URL resource = getClass().getResource(bigDirName);
			if (resource == null) {
				// no big file exists
				System.out.println("No big file for " + baseName);
				JLabel smallLabel = new JLabel(baseName, icon, JLabel.LEFT);
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(smallLabel, BorderLayout.CENTER);
				return panel;
			} else {
				ImageIcon bigIcon = new ImageIcon(resource);
				JLabel smallLabel = new JLabel(icon);
				Borders.addMatte(smallLabel, 0, 0, 0, 10);
				JLabel bigLabel = new JLabel(baseName, bigIcon, JLabel.LEFT);
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(smallLabel, BorderLayout.LINE_START);
				panel.add(bigLabel, BorderLayout.CENTER);
				Borders.addMatte(panel, 2);
				return panel;
			}
		}

		@Override
		protected String[] getSubDirectories() {
			return new String[] {"16x16", "32x32"};
		}
	}
	
//	private class CrystalFamily implements Family {
//		CrystalFamily() {
//			
//		}
//
//		@Override
//		public JPanel makeView() {
//			throw new RuntimeException(); // todo: write me
//		}
//	}
	
	private static class PngFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".png");
		}
	}
	
//	private class IconDisplay extends 
}

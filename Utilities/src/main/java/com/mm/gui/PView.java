package com.mm.gui;

/**
 * Created using IntelliJ IDEA.
 * Date: Jul 11, 2004
 * Time: 10:57:44 PM
 * @author Miguel Mu\u00f1oz
 *
 * Copyright (c) 2004 Miguel Munoz
 */


import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FilenameFilter;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
//import javax.swing.event.InternalFrameListener;
//import javax.swing.event.InternalFrameAdapter;
//import javax.swing.event.InternalFrameEvent;

public class PView
    extends JPanel
{
	private static JFrame mf;
	private JDesktopPane mDesk;
  private Preferences mPref;
	private static ScaleButton[] mBtnList;

	public static void main(String[] args)
	{
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e) { e.printStackTrace(); }
//		catch (InstantiationException e) { e.printStackTrace(); }
//		catch (IllegalAccessException e) { e.printStackTrace(); }
//		catch (UnsupportedLookAndFeelException e) { e.printStackTrace(); }
		mf=new JFrame("PView");
		mf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		PView view=new PView();
		mf.getContentPane().setLayout(new BorderLayout());
		mf.getContentPane().add(view, BorderLayout.CENTER);
		mf.setBounds(10, 10, 600, 400);
    mf.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mf.setVisible(true);
//    // doesn't work.
//    view.activate();
	}

	private PView()
	{
		super(new BorderLayout());
		File userDir = new File(System.getProperty("user.dir"));
		FilenameFilter picFiles = new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				String lowName = name.toLowerCase();
				if (lowName.endsWith(".jpg"))
					return true;
				if (lowName.endsWith(".jpeg"))
					return true;
				if (lowName.endsWith(".gif"))
					return true;
				if (lowName.endsWith(".png"))
					return true;
				return false;
			}
		};
		File[] pics = userDir.listFiles(picFiles);
    
    mPref=Preferences.userNodeForPackage(PView.class);
		
		mDesk=new JDesktopPane();
		add(mDesk, BorderLayout.CENTER);
		watch();
		
		InternalFrameListener ilr = new InternalFrameAdapter()
		{
//			public void internalFrameOpened(InternalFrameEvent e) { process(e); }
//			public void internalFrameClosing(InternalFrameEvent e) { process(e); }
//			public void internalFrameClosed(InternalFrameEvent e) { process(e); }
//			public void internalFrameIconified(InternalFrameEvent e) { process(e); }
//			public void internalFrameDeiconified(InternalFrameEvent e) { process(e); }
			public void internalFrameActivated(InternalFrameEvent e) { process(e); }
//			public void internalFrameDeactivated(InternalFrameEvent e) { process(e); }
		};
		for (int ii=0; ii<pics.length; ++ii)
		{
      double scale = Double.valueOf(mPref.get(pics[ii].getName(), "1.0")).doubleValue();
			JInternalFrame ifr = new ScrollingFrame(pics[ii], scale);
			ifr.setVisible(true);
			ifr.pack();
			
			ifr.addInternalFrameListener(ilr);
      // Due to a bug, we can't maximize until after we display the window.
//			try { ifr.setMaximum(true); }
//			catch (PropertyVetoException e) { e.printStackTrace(); }
			mDesk.add(ifr);
//			System.out.println(pics[ii].getName());
		}
		
		JPanel topPanel = new JPanel(new GridLayout(1, 0));
		mBtnList=new ScaleButton[]{
				new ScaleButton(0.5),
				new ScaleButton(0.75),
				new ScaleButton(1.0),
				new ScaleButton(1.5),
				new ScaleButton(2.0),
				new ScaleButton(2.5),
				};
		for (ScaleButton btn : mBtnList)
			topPanel.add(btn);
//		topPanel.add(new ScaleButton(0.5));
//		topPanel.add(new ScaleButton(0.75));
//		topPanel.add(new ScaleButton(1.0));
//		topPanel.add(new ScaleButton(1.5));
//		topPanel.add(new ScaleButton(2.0));
//		topPanel.add(new ScaleButton(2.5));
		add(topPanel, BorderLayout.NORTH);
	}
  
//  private void activate()
//  {
//    try { mDesk.getSelectedFrame().setMaximum(true); }
//    catch (PropertyVetoException e) { e.printStackTrace(); }
//  }
//	
	private static int      sTabCount=0;
	private static boolean  sCtrl = false;
	private void watch()
	{
//		KeyListener kl = new KeyListener()
//		{
//			public void keyTyped(KeyEvent e)
//			{
//				// To change body of implemented methods use File | Settings | File Templates.
//			}
//
//			public void keyPressed(KeyEvent e)
//			{
//				doKeyPressed(e);
//			}
//
//			public void keyReleased(KeyEvent e)
//			{
//				doKeyReleased(e);
//			}
//		};
		AWTEventListener awte = new AWTEventListener()
		{
			public void eventDispatched(AWTEvent event)
			{
//        System.out.print("KP: " + event.getID() + ' ' + ((KeyEvent)event).getKeyCode() + ": ");
				if(event.getID() == KeyEvent.KEY_PRESSED)
					doKeyPressed((KeyEvent)event);
				else if (event.getID() == KeyEvent.KEY_RELEASED)
					doKeyReleased((KeyEvent)event);
//				else
//        System.out.println("evt Id = " + event.getID());
			}
		};
//		mDesk.addKeyListener(kl);
		Toolkit.getDefaultToolkit().addAWTEventListener(awte, AWTEvent.KEY_EVENT_MASK);
	}

	private void doKeyReleased(KeyEvent e){
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			sCtrl = false;
			JInternalFrame chosen=mDesk.getAllFrames()[sTabCount];
			mDesk.setSelectedFrame(chosen);
			chosen.moveToFront();
//			System.out.println("ft: " + sTabCount);
      if (!chosen.isMaximum())
        try { chosen.setMaximum(true); }
        catch (PropertyVetoException pve) { pve.printStackTrace(); }
//      System.out.println("one");
      if (!chosen.isMaximum())
        try { chosen.setMaximum(true); }
        catch (PropertyVetoException pve) { pve.printStackTrace(); }
//      System.out.println("Two");
			sTabCount = 0;
		}
	}

	 private void doKeyPressed(KeyEvent e){
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
			sCtrl = true;
		else if (e.getKeyCode() == KeyEvent.VK_TAB)
			if (sCtrl)
				if ((e.getModifiers() & KeyEvent.SHIFT_MASK) != 0)
				{
					sTabCount--;
					if (sTabCount < 0)
						sTabCount = mDesk.getComponentCount()-1;
//					System.out.println("t:" + sTabCount);
				} else {
					sTabCount++;
					if (sTabCount >= mDesk.getComponentCount())
						sTabCount = 0;
//					System.out.println("t:" + sTabCount);
				}
			else
			{
        JInternalFrame[] all=mDesk.getAllFrames();
        JInternalFrame chosen = all[mDesk.getComponentCount()-1];
//        System.out.println("no cntl. cnt=" + mDesk.getComponentCount());
//        for (int ii=0; ii<all.length; ++ii)
//          System.out.println(" [" + ii + "] " + all[ii].getTitle());
//        System.out.println("Chosen = " + chosen.getTitle());
				chosen.moveToFront();
				mDesk.setSelectedFrame(chosen);
        if (chosen.isIcon())
          try { chosen.setIcon(false); }
          catch (PropertyVetoException e1) { e1.printStackTrace(); }
        if (!chosen.isMaximum()) // doesn't work. Everything is already maximized.
          try { chosen.setMaximum(true); }
          catch (PropertyVetoException pve) { pve.printStackTrace(); }
//        System.out.println("Three");
			}
				
	}

	private static void process(InternalFrameEvent evt)
	{
//		System.out.println("e: " + evt.getID() +" i=" + find(evt) + "  w: " + evt.getInternalFrame().getTitle());
    JInternalFrame iFrame=evt.getInternalFrame();
    if (!iFrame.isMaximum())
      try { iFrame.setMaximum(true); }
      catch (PropertyVetoException e) { e.printStackTrace(); }
		ScrollingFrame scr = (ScrollingFrame) iFrame;
		double iScale = scr.mScl;
		showScale(iScale);
//    System.out.println("Four");
	}

	private static void showScale(double pIScale){
		for (ScaleButton btn : mBtnList)
			if (btn.mScale == pIScale)
				btn.check();
			else
				btn.uncheck();
	}
//	
//	private int find(InternalFrameEvent evt)
//	{
//		JInternalFrame fr = evt.getInternalFrame();
//		JInternalFrame[] all = mDesk.getAllFrames();
//		for (int ii=0; ii<all.length; ++ii)
//			if (all[ii] == fr)
//				return ii;
//		return -1;
//	}
	
	private static class ZoomImageIcon extends ImageIcon
	{
		private double mScale;
		ZoomImageIcon(String fName, double scale)
    {
      super(fName);
      setScale(scale);
    }

		public double getScale() { return mScale; }
		public void setScale(double pScale) { mScale=pScale; }

		public int getIconWidth()
		{
			return (int)(super.getIconWidth()*mScale + 0.5);
		}

		public int getIconHeight()
		{
			return (int)(super.getIconHeight()*mScale + 0.5);
		}

		public synchronized void paintIcon(Component c, Graphics g, int x, int y)
		{
//      System.out.println("Painting icon at " + x + ", " + y);
			if(getImageObserver() == null) {
			   g.drawImage(getImage(), x, y, getIconWidth(), getIconHeight(), c);
			} else {
	      g.drawImage(getImage(), x, y, getIconWidth(), getIconHeight(), getImageObserver());
			}
		}
	}
	
	private class ScaleButton extends JButton
	{
		private double mScale;
		ScaleButton(double pScale)
		{
			mScale = pScale;
			ActionListener al = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
//          System.out.println("Scale");
					ScrollingFrame topFrame = (ScrollingFrame) mDesk.getAllFrames()[0];
					JLabel lbl = topFrame.mLbl;
          boolean setMax = topFrame.isMaximum();
					ZoomImageIcon icn = (ZoomImageIcon)lbl.getIcon();
					icn.setScale(mScale);
          mPref.put(topFrame.getTitle(), Double.toString(mScale));
//					System.out.println("scale set to " + icn.getScale());
					topFrame.pack();
					topFrame.repaint();
          if (setMax && !topFrame.isMaximum())
            try { topFrame.setMaximum(true); }
            catch (PropertyVetoException ex) { ex.printStackTrace(); }
					showScale(mScale);
				}
			};
			addActionListener(al);
			setText(getBtnName());
		}
		
		private String getBtnName()
		{
			return "" + (int)(mScale*100+.5) + '%';
		}
		
		private void check() { setText("\u2714 " + getBtnName()); }
		private void uncheck() { setText(getBtnName()); }
	}
  
  private class ScrollingFrame extends JInternalFrame
  {
    private JLabel mLbl;
	  private double mScl;
    ScrollingFrame(File pFile, double scale)
    {
      super(pFile.getName(), true, true, true, true);
      ImageIcon img = new ZoomImageIcon(pFile.getAbsolutePath(), scale);
      mLbl = new JLabel(img);
	    mScl = scale;
      getContentPane().add(new JScrollPane(mLbl));
    }
  }
}

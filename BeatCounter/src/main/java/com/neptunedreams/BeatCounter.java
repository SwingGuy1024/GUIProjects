package com.neptunedreams;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/*
  I need to keep track of the last ten beats and average out the rate.
  I can then filter that average. That will probably work better.
  I can even put different beat counters into the same applet to compare 
  them. They can all use the same beat button, but have different displays.
*/


@SuppressWarnings({"HardCodedStringLiteral", "HardcodedLineSeparator", "StringConcatenation", "MagicNumber"})
public class BeatCounter extends JPanel
  implements
    KeyListener,
    ActionListener
{
	private static final double SQRT_2 = Math.sqrt(2.0);
	private JLabel           myCount2;
  private final  int       sFiltMult=100;
  private JButton start;
  private static final String   sReset = "(Reset space bar)";
//	private static String   reCalc = "Recalculating...\n(Keep tapping)";
	private        boolean  mApplet=true;
	private        WindowListener mWindowListener;
	private final Scaler scaler = new CircularScaler();
  
  private final Beat2 b2 = new Beat2();
  
  private final Counter[] counters = { b2, new Beat1() }; 

  private final Canvas myCanvas = new Canvas()
  {
    @Override
    public void paint(Graphics gr) { scaler.drawScale((Graphics2D) gr, this); }
    @Override
    public void update(Graphics gr) { paint(gr); }
  };
	private static final int CANVAS_SIZE = 300;

	@Override
	public int getWidth() { return getSize().width; }
  @Override
  public int getHeight() { return getSize().height; }
  
  // Application use only:
  private static Color mBgColor;
  
  public static void main(String[] args)
  {
	  //noinspection CatchGenericClass,OverlyBroadCatchBlock
	  try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
    catch (Exception err) { err.printStackTrace(); }
	  JFrame f = new JFrame("Beat Counter");
	  f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  try
      {
	      RIcon windowIcon = new RIcon("Drum-SH-256x256.png", BeatCounter.class);
	      f.setIconImage(windowIcon.getImage());
      }
      catch (MissingResourceException ignored) { }

    BeatCounter view=new BeatCounter(166, 190, 253);
    
    view.init();
    f.getContentPane().setLayout(new BorderLayout());
    f.getContentPane().add(view, BorderLayout.CENTER);
    String sloganText =
          "<html>" +
      "\n  <body style=\"font-family:Serif, Times New Roman; font-weight:bold; font-size:12px\">" +
      "\n    <p style=\"text-align:center;\">" +
      "\n      &nbsp;&nbsp;&nbsp;&nbsp;\u201cBeat me, daddy, eight to the bar!\u201d&nbsp;&nbsp;&nbsp;&nbsp;" +
      "\n    <p style=\"font-style:italic; text-align:right\">" +
      "\n      \u2014 The Andrews Sisters" +
      "\n  </body>" +
      "\n</html>";
    JLabel slogan = new JLabel(sloganText, JLabel.CENTER);
    Borders.addEmptyBorder(slogan, 8,8,8,8);
    slogan.setBackground(mBgColor);
    slogan.setOpaque(true);
    f.getContentPane().add(slogan, BorderLayout.NORTH);
    f.setBounds(10, 10, 400, 400);
    f.pack();
	  
	  f.addWindowListener(view.mWindowListener);
    f.setVisible(true);
  }

  public BeatCounter() { super(); }
  private BeatCounter(int rr, int gg, int bb)
  {
    super();
	  //noinspection AssignmentToStaticFieldFromInstanceMethod
	  mBgColor = new Color(rr, gg, bb);
    setNoApplet();
	  mWindowListener = new WindowAdapter() {

		  @Override
		  public void windowDeactivated(WindowEvent e) {
			  copyBeatToClipboard();
		  }
	  };
  }
	
	private void copyBeatToClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection currentRate = new StringSelection(String.valueOf(b2.myRate));
		clipboard.setContents(currentRate, currentRate);
	}

  public int getBeatCount() { return b2.getRate(); }

  public void setNoApplet() { mApplet = false; }
//  @Override
  public void init()
  {
    setLayout(new BorderLayout());
    Color bgClr;
//    if (mApplet)
//    {
//      String colorParam = getParameter("RGB");
//      bgClr = extractColor(colorParam);
//    }
//    else
    bgClr = mBgColor;
    if (bgClr == null) {
	    bgClr = new Color(192, 192, 192);
    }
    setBackground(bgClr);
	  myCanvas.setBackground(bgClr);
	  
	  for (Counter ctr : counters) {
		  scaler.addCounter(ctr);
	  }
	  
	  ResetListener lsnr = () -> {
		  myCount2.setText(" ");
		  myCount2.repaint();
	  };
	  scaler.addResetListener(lsnr);
    
    myCount2 = new JLabel(" ");
    myCount2.setHorizontalAlignment(JLabel.CENTER);
	  myCount2.setOpaque(true);
	  myCount2.setBackground(getBackground());
    add(border(myCount2), BorderLayout.NORTH);
    
    start = new JButton("Beat me!");
    if (!mApplet)
    {
      String tfTxt = "<br>Use this Beat Counter to find out how fast "
            + "your music is playing.\n\n"
            + "Just tap the space bar for every beat. After a few "
            + "beats, it will show you how many beats per minute "
            + "your song is playing.\n\n"
            + "(Don't use the mouse. It won't work very well.)";
      String htmlFmt = "<html>{0}</html>";
      tfTxt = MessageFormat.format(htmlFmt, tfTxt);
      JLabel lbl = new JLabel(tfTxt);
      lbl.setPreferredSize(new Dimension(120, 120));
      lbl.setVerticalAlignment(SwingConstants.TOP);
      add(border(lbl), BorderLayout.EAST);
	    lbl.setOpaque(true);
	    lbl.setBackground(getBackground());
    }

    start.addActionListener(this);
    JPanel btnPanel = new JPanel(new FlowLayout());
    btnPanel.setBackground(getBackground());
    btnPanel.add(border(start));
    add(border(btnPanel), BorderLayout.SOUTH);

    JButton resetBtn = new JButton(sReset);
    resetBtn.addActionListener(this);
    btnPanel.add(resetBtn);

    myCanvas.setPreferredSize(new Dimension(CANVAS_SIZE, CANVAS_SIZE));
    add(myCanvas, BorderLayout.CENTER);
    
    myCanvas.addKeyListener(this);

    start.requestFocus();
  }

  private Color extractColor(String param)
  {
    try
    {
      if (param != null)
      {
        StringTokenizer tk = new StringTokenizer(param, ", ");
        int red = Integer.parseInt(tk.nextToken());
        int green = Integer.parseInt(tk.nextToken());
        int blue = Integer.parseInt(tk.nextToken());
        return new Color(red, green, blue);
      }
    }
    catch (NumberFormatException | NoSuchElementException ignored) { }
	  return null;
  }

  @Override
  public void actionPerformed(ActionEvent evt)
  {
    long time = System.currentTimeMillis();
    doBeat(time);
    if ((evt == null) || sReset.equals(evt.getActionCommand())) {
	    start.requestFocus();
    }
  }

  public void resetStartButton() { start.requestFocus(); }

  private void doBeat(long time)
  {
    for (Counter cnt : counters)
    {
      cnt.countBeat(time);
    }
  }

  /**
   Implementation of KeyListener
   */
  @Override
  public void keyPressed(KeyEvent evt) {
    long time = System.currentTimeMillis();
    if (evt.getKeyCode()==KeyEvent.VK_SPACE) {
	    doBeat(time);
    }
  }
  @Override
  public void keyTyped(KeyEvent e) { }
  @Override
  public void keyReleased(KeyEvent e) { }
	
	private static class CircularScaler extends AbstractScaler {
		private final int bigRadius = (CANVAS_SIZE * 35) / 80;
		private final int bigOuterGap = (CANVAS_SIZE / 2) - bigRadius;
		private final int radius = (CANVAS_SIZE * 25) / 80;
		private final int outerGap = (CANVAS_SIZE / 2) - radius;
		private final int center= CANVAS_SIZE/2;
		private final Arc2D arc = new Arc2D.Double(outerGap, outerGap, radius*2, radius*2, -135.0, 270.0, Arc2D.OPEN);
		private final Arc2D circle = new Arc2D.Double(bigOuterGap, bigOuterGap, bigRadius*2, bigRadius*2, 0.0, 360.0, Arc2D.OPEN);
		private final Line2D bigTic = new Line2D.Double(0, radius*0.9, 0, radius-0.5);
		private final Line2D smallTic = new Line2D.Double(0, radius*0.95, 0, radius-0.5);
		private final Shape needle = makeNeedle();
		private final Shape thinNeedle = new Line2D.Double(0.0, 0.0, 0.0, radius*0.9);
		private final Stroke outerStroke = new BasicStroke(4.0F);
		private final Stroke thinStroke = new BasicStroke(1.0F);
		private static Point2D[] LABEL_POINTS = null;

		@Override
		public void reset() {
			for (Counter cnt : getCounters())
			{
			  cnt.resetCounter();
			}
			setTop(0);
			for (ResetListener lsnr : getListeners()) {
				lsnr.reset();
			}
		}
		
		@Override
		public void drawScale(Graphics2D gr, Canvas canvas) {
			Counter primary = getCounters().get(0);
		  int beat = primary.getRate();
			gr.setColor(canvas.getBackground());
			gr.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			int top = getTop();
			if (top == 0) {
				resetImage(beat);
			} else {
				if (beat >= top) {
//					if (!
							resetImage(beat);
//					) {
//						return;
//					}
				} else if (beat <= (top - scRange)) {
//					if (!
							resetImage(beat);
//					) {
//						return;
//					}
				}
				
				gr.setColor(Color.black);
				gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				gr.setStroke(outerStroke);
				// Draw the Circle
				gr.setColor(Color.white);
				gr.fill(circle);
				gr.setColor(Color.black);
				gr.draw(circle);

				// Draw the arc
				gr.setStroke(thinStroke);
				top = getTop();
				int topDegrees = ((top / 10) * 90) % 360;
				double topRads = getRadians(topDegrees);
				AffineTransform savedTransform = gr.getTransform();
				gr.translate(center, center);
				AffineTransform centerTransfrom = gr.getTransform();
				gr.rotate(topRads);
				gr.translate(-center, -center);
				gr.draw(arc);

				// Draw the tic marks
				for (int ii = top-25; ii <= (top + 5); ++ii) {
					gr.setTransform(centerTransfrom);
					double ticDegrees = (ii*9)%360;
					gr.rotate((ticDegrees * Math.PI) / 180.0);
					if ((ii % 5) == 0) {
						gr.draw(bigTic);
					} else {
						gr.draw(smallTic);
					}
				}
				
				// Draw the thin needle
				gr.setTransform(centerTransfrom);
				int beat2 = getCounters().get(1).getRate();
				int needleAngleDegrees = (beat2*9)%360;
				double needleAngleRads = (needleAngleDegrees * Math.PI) / 180.0;
				gr.rotate(needleAngleRads);
				gr.draw(thinNeedle);

				// Draw the needle
				gr.setTransform(centerTransfrom);
				needleAngleDegrees = (beat*9)%360;
				needleAngleRads = (needleAngleDegrees * Math.PI) / 180.0;
				gr.rotate(needleAngleRads);
				gr.setColor(Color.RED.darker());
				gr.fill(needle);
				
				
				// Now draw labels:
				gr.setTransform(savedTransform);
				gr.setColor(Color.black );
				Point2D[] labelPoints = getLabelPoints(gr);
				Point2D[] shiftedPoints = new Point2D[7];
				int shiftStart = ((top / 5) + 2) % 8;
				for (int ii=0; ii<7; ++ii) {
					shiftedPoints[ii] = labelPoints[shiftStart%8];
					shiftStart++;
				}
				int bottom = top - 25;
				for (Point2D pt : shiftedPoints) {
					String lbl = String.format("%3d", bottom);
					if (bottom >= 0) {
						//noinspection NumericCastThatLosesPrecision
						gr.drawString(lbl, (float)pt.getX(), (float)pt.getY());
					}
					bottom += 5;
					
					// Draw text rects
//					String dummyLabel = String.format("%3d", 999);
//					Font font = gr.getFont();
//					FontRenderContext frc = gr.getFontRenderContext();
//					Rectangle2D stringRect = font.getStringBounds(dummyLabel, frc);
//					AffineTransform temp = gr.getTransform();
//					gr.translate(pt.getX(), pt.getY());
//					gr.draw(stringRect);
//					gr.setTransform(temp);
				}
			}
		}

		private Point2D[] getLabelPoints(Graphics2D gr) {
			if (LABEL_POINTS == null) {
				//noinspection AssignmentToStaticFieldFromInstanceMethod
				LABEL_POINTS = new Point2D[8];
				// points start at lower left "5" tic mark and proceed clockwise, ending with the bottom label
				String dummyLabel = String.format("%3d", 999);
				Font font = gr.getFont();
				FontRenderContext frc = gr.getFontRenderContext();
				Rectangle2D stringRect = font.getStringBounds(dummyLabel, frc);
				double dimRadius = radius/ SQRT_2; // DIMinished Radius (at 45 degree angle)
				final double halfHt = stringRect.getHeight() / 2;
				final double halfWd = stringRect.getWidth() / 2;
				final double desc = gr.getFontMetrics().getDescent();
				LABEL_POINTS[0] = new Point2D.Double(
								center - dimRadius - halfHt-stringRect.getWidth(),
						(center + dimRadius + stringRect.getHeight()) - desc
				);
				LABEL_POINTS[1] = new Point2D.Double(
								outerGap - halfHt - stringRect.getWidth(),
						(center + halfHt) - desc
				);
				LABEL_POINTS[2] = new Point2D.Double(
								LABEL_POINTS[0].getX(),
								center - dimRadius - desc //+ halfHt
				);
				LABEL_POINTS[3] = new Point2D.Double(
								center - halfWd,
								outerGap - desc - halfHt// - stringRect.getHeight()
				);
				LABEL_POINTS[4] = new Point2D.Double(
								center + dimRadius + halfHt,
								LABEL_POINTS[2].getY()
				);
				LABEL_POINTS[5] = new Point2D.Double(
								center + radius + halfHt,
								LABEL_POINTS[1].getY()
				);
				LABEL_POINTS[6] = new Point2D.Double(
								center + dimRadius + halfHt,
								LABEL_POINTS[0].getY()
				);
				LABEL_POINTS[7] = new Point2D.Double(
								LABEL_POINTS[3].getX(),
						((center + radius) - desc) + halfHt + stringRect.getHeight()
				);
			}
			return LABEL_POINTS;
		}

		private double getRadians(int pTopDegrees) {
			switch(pTopDegrees) {
				case 0:
					return 0.0;
				case 90:
					return Math.PI/2;
				case 180:
					return Math.PI;
				case 270:
					return (3.0 * Math.PI) / 2;
				default:
					throw new AssertionError("Unknown angle: " + pTopDegrees);
			}
		}

		private Shape makeNeedle() {
			GeneralPath path = new GeneralPath();
			float needleBase = 10.0f;
			path.moveTo(0.0F, -needleBase);
			path.lineTo(-needleBase /2.0F, 0.0F);
			path.lineTo(0.0F, radius*0.9F);
			path.lineTo(needleBase /2.0F, 0.0F);
			path.closePath();
			return path;
		}

		/**
		 * returns true if we should draw, false otherwise.
		 * @param beat The beat rate
		 * @return true if we should draw.
		 */
		private boolean resetImage(int beat)
		{
		  boolean rVal = true;
		  if (getTop() != 0)
		  {
		    if (beat >= getTop()) {
			    while (beat >= getTop()) {
				    setTop(getTop() + (scRange / 2));
			    }
		    } else if (beat <= (getTop()-scRange)){
			    while (beat <= (getTop() - scRange)) {
			      setTop(getTop() - (scRange / 2));
			    }
		    }
//		    if (beat > getTop())
//		    {
//		      reset();
//		      rVal = false;
//		    }
//		    else if (beat < getTop()-scRange)
//		    {
//		      reset();
//		      rVal = false;
//		    }
		  }
		  return rVal;
		}
	}

  private class Beat1 implements Counter
  {
    private long            myLastTime;
    private long            myLastBeat;
    private long            myFilteredBeat;
    private long            myFRate;
    @Override
    public void countBeat(long time)
    {
      long beat = time - myLastTime;
      myLastTime = time;
      // Has a long time passed since they last hit the beat?
      if ((beat != 0) && (myLastBeat != 0) && ((beat / myLastBeat) < 5) && ((myLastBeat / beat) < 5))
      {
        // No. It hasn't been too long.
        long brate = bpm(myLastBeat);
        if (myFilteredBeat == 0) {
	        myFilteredBeat = brate*sFiltMult;
        } else {
	        myFilteredBeat = ((3 * myFilteredBeat) + (brate * sFiltMult)) / 4;
        }
	      myFRate = (myFilteredBeat + (sFiltMult / 2)) / sFiltMult;
      }
      else {
	      myFilteredBeat = 0;
      }
      myLastBeat = beat;
    }
  
    private long bpm(long millis)
    {
      return 60000L/millis;
    }

    @Override
    public int getRate()
    {
      return (int)myFRate;
    }

    @Override
    public void resetCounter()
    {
      myLastBeat = 0;
      myLastTime = 0;
      myFilteredBeat = 0;
    }
  }

  private class Beat2 implements Counter
  {
    private final int myReset = 3;
    private final int myBMax = 10;

    private int       myBeat;
    private int       myLastBeat;
    private final long[]    myBLongList = new long[myBMax];
    private int       myBSize = 0;
    private int       myBIndex = 0;
    private long      myBLastTime;
    private long      myBLastBeat;
    private long      myBFilter = 0;
    private long      myRate;
  
    @Override
    public void countBeat(long time)//, Scrollbar scrl)
    {
      long beat = time - myBLastTime;
      myBLastTime = time;
      myBLongList[myBIndex++] = time;
      if (myBIndex >= myBMax) {
	      myBIndex = 0;
      }
      if (myBSize < myBMax) {
	      myBSize++;
      }
      if ((beat != 0) && (myBLastBeat != 0) && ((beat / myBLastBeat) < myReset) && ((myBLastBeat / beat) < myReset))
      {
        if (myBSize > 1)
        {
          int prevTimeIndex;
          if (myBSize < myBMax) {
	          prevTimeIndex = 0;
          } else
          {
            prevTimeIndex = myBIndex;
            if (prevTimeIndex >= myBMax) {
	            prevTimeIndex = 0;
            }
          }
          long rate = (60000l * (myBSize - 1)) / (time - myBLongList[prevTimeIndex]);
          if (myBFilter == 0) {
	          myBFilter = sFiltMult*rate;
          } else {
	          myBFilter = ((3 * myBFilter) + (sFiltMult * rate)) / 4;
          }
          long fRate = (myBFilter + (sFiltMult / 2)) / sFiltMult;
          myBeat = (int) fRate;
	        myRate = fRate;
          myCount2.setText(String.valueOf(fRate) + " Beats per Minute");
          myCount2.repaint();
        }
	      int top = scaler.getTop();
        if (top == 0)
        {
          if (Math.abs(myBeat-myLastBeat)<3)
          {
            int mod = Scaler.scRange/2;
            scaler.setTop(((myBeat + ((3 * mod) / 2)) / mod) * mod);
          }
        }
        if ((myBSize == myBMax) && (Math.abs(myLastBeat - myBeat) > 5))
        {
          int prevIndex = myBIndex-1;
          if (prevIndex<0) {
	          prevIndex+=myBMax;
          }
          myBLongList[0] = myBLongList[prevIndex];
          myBLongList[1] = time;
          myBSize = 2;
          myBIndex = 2;
          myBFilter = beat;
        }
        myLastBeat = myBeat;
      }
      else // reset everything
      {
        scaler.reset();
      }
      myBLastBeat = beat;
      myCanvas.repaint();
    }

    @Override
    public int getRate()
    {
      return myBeat;
    }
    
    @Override
    public void resetCounter()
    {
      myBeat = 0;
      myLastBeat = -100;
      myBSize = 0;
      myBLastBeat = 0;
      myBIndex = 0;
      myBFilter = 0;
    }
  }

  private interface Counter
  {
    void countBeat(long time);
    int  getRate();
    void resetCounter();
  }
    
	// Debug tool to draw borders around the specified component. This 
	// lets you examine the layout. 
	private JComponent border(JComponent cmp)
	{
		return cmp;
	}
	
	private static abstract class AbstractScaler implements Scaler {
		private final List<ResetListener> listeners = new LinkedList<ResetListener>();
		protected Iterable<ResetListener> getListeners() { return listeners; }

		@Override
		public void addResetListener(ResetListener listener) {
			listeners.add(listener);
		}

		private int myTop;
		@Override
		public void setTop(int top) { myTop = top; }
		@Override
		public int getTop() { return myTop; }

		private final List<Counter> counterList = new LinkedList<Counter>();

		@Override
		public void addCounter(Counter counter) { counterList.add(counter); }
		protected List<Counter> getCounters() { return counterList; }
	}
	
	private interface Scaler {
		public final int scRange = 20;
		public void drawScale(Graphics2D graphics, Canvas canvas);
		public void reset();
		public void setTop(int top);
		public int getTop();
		public void addCounter(Counter counter);
		public void addResetListener(ResetListener listener);
	}
	
	private interface ResetListener
	{
		public void reset();
	}
}
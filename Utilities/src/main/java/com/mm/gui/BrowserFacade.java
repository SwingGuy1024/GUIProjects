package com.mm.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.event.HyperlinkListener;
//import com.webrenderer.IBrowserCanvas;
//import com.webrenderer.event.MouseAdapter;
//import com.webrenderer.event.MouseEvent;
//import com.webrenderer.event.MouseListener;
//import com.webrenderer.windows.BrowserFactory;

/**
 * Preliminary version of a browser component. As of this writing, this
 * exists solely to try different browser components for StockChart, 
 * and so only implements what StockChart needs.
 * Created using IntelliJ IDEA. Date: Mar 24, 2005 Time: 11:20:57 AM
 *
 * @author Miguel Mu\u00f1oz
 *         <p/>
 *         Copyright (c) 2004 Miguel Munoz
 */

public interface BrowserFacade extends Scrollable
{
//	public void addHyperlinkListener(HyperlinkListener pLsr);
//	public void setEditable(boolean pEdt);
//	public void setContent(String pContent);
//    public Component getScrollableView();
//
//	/**
//	 * For now, this enum only works for Windows. I should be able to modify it
//	 * for other platforms, but I can't test them right now.
//	 */ 
//	public enum BrowserType
//	{
//		MozillaBrowser() { public IBrowserCanvas makeCanvas() { return BrowserFactory.spawnMozilla(); }},
//		IEBrowser() { public IBrowserCanvas makeCanvas() { return BrowserFactory.spawnInternetExplorer(); }};
//		IBrowserCanvas mCanvas=null;
//		BrowserType() { }
//		public abstract IBrowserCanvas makeCanvas();
//	}
//	
//	public class SwingBrowser extends JEditorPane implements BrowserFacade
//	{
//		public void setContent(String pContent)
//		{
//			setText(pContent);
//		}
//
//        public Component getScrollableView() { return this; }
//	}
//	
//	public class WebRenderBrowser extends JPanel implements BrowserFacade
//	{
//		IBrowserCanvas mCanvas = BrowserType.IEBrowser.makeCanvas();
//		WebRenderBrowser(BrowserType pType)
//		{
//			mCanvas = pType.makeCanvas();
//		}
//
//        public Component getScrollableView() {
//            return mCanvas.getCanvas();
//        }
//
//
//		public void addHyperlinkListener(final HyperlinkListener pLsr)
//		{
//			MouseListener ml = new MouseAdapter()
//			{
//				public void onClick(MouseEvent event)
//				{
//					super.onClick(event);
//				}
//			}; 
//			mCanvas.addMouseListener(ml);
//		}
//
//		public void setEditable(boolean pEdt)
//		{
//
//		}
//
//		public void setContent(String pContent)
//		{
//            mCanvas.loadHTML("text/html", pContent);
//		}
//
//		/**
//		 * Returns the preferred size of the viewport for a view component. For
//		 * example, the preferred size of a <code>JList</code> component is the size
//		 * required to accommodate all of the cells in its list. However, the value of
//		 * <code>preferredScrollableViewportSize</code> is the size required for
//		 * <code>JList.getVisibleRowCount</code> rows. A component without any
//		 * properties that would affect the viewport size should just return
//		 * <code>getPreferredSize</code> here.
//		 *
//		 * @return the preferredSize of a <code>JViewport</code> whose view is this
//		 *         <code>Scrollable</code>
//		 * @see JViewport#getPreferredSize
//		 */
//		public Dimension getPreferredScrollableViewportSize()
//		{
//			return null;
//		}
//
//		/**
//		 * Components that display logical rows or columns should compute the scroll
//		 * increment that will completely expose one new row or column, depending on
//		 * the value of orientation.  Ideally, components should handle a partially
//		 * exposed row or column by returning the distance required to completely
//		 * expose the item.
//		 * <p/>
//		 * Scrolling containers, like JScrollPane, will use this method each time the
//		 * user requests a unit scroll.
//		 *
//		 * @param visibleRect The view area visible within the viewport
//		 * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
//		 * @param direction   Less than zero to scroll up/left, greater than zero for
//		 *                    down/right.
//		 *
//		 * @return The "unit" increment for scrolling in the specified direction. This
//		 *         value should always be positive.
//		 * @see JScrollBar#setUnitIncrement
//		 */
//		public int getScrollableUnitIncrement(
//		    Rectangle visibleRect, int orientation, int direction
//		    )
//		{
//			return 0;
//		}
//
//		/**
//		 * Components that display logical rows or columns should compute the scroll
//		 * increment that will completely expose one block of rows or columns,
//		 * depending on the value of orientation.
//		 * <p/>
//		 * Scrolling containers, like JScrollPane, will use this method each time the
//		 * user requests a block scroll.
//		 *
//		 * @param visibleRect The view area visible within the viewport
//		 * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
//		 * @param direction   Less than zero to scroll up/left, greater than zero for
//		 *                    down/right.
//		 *
//		 * @return The "block" increment for scrolling in the specified direction. This
//		 *         value should always be positive.
//		 * @see JScrollBar#setBlockIncrement
//		 */
//		public int getScrollableBlockIncrement(
//		    Rectangle visibleRect, int orientation, int direction
//		    )
//		{
//			return 0;
//		}
//
//		/**
//		 * Return true if a viewport should always force the width of this
//		 * <code>Scrollable</code> to match the width of the viewport. For example a
//		 * normal text view that supported line wrapping would return true here, since
//		 * it would be undesirable for wrapped lines to disappear beyond the right edge
//		 * of the viewport.  Note that returning true for a Scrollable whose ancestor
//		 * is a JScrollPane effectively disables horizontal scrolling.
//		 * <p/>
//		 * Scrolling containers, like JViewport, will use this method each time they
//		 * are validated.
//		 *
//		 * @return True if a viewport should force the Scrollables width to match its
//		 *         own.
//		 */
//		public boolean getScrollableTracksViewportWidth()
//		{
//			return false;
//		}
//
//		/**
//		 * Return true if a viewport should always force the height of this Scrollable
//		 * to match the height of the viewport.  For example a columnar text view that
//		 * flowed text in left to right columns could effectively disable vertical
//		 * scrolling by returning true here.
//		 * <p/>
//		 * Scrolling containers, like JViewport, will use this method each time they
//		 * are validated.
//		 *
//		 * @return True if a viewport should force the Scrollables height to match its
//		 *         own.
//		 */
//		public boolean getScrollableTracksViewportHeight()
//		{
//			return false;
//		}
//	}
}
